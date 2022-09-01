from __future__ import annotations
import asyncio
import json
from typing import Any, Awaitable, Callable, NoReturn, Optional

import redstone_computer_utilities as rcu


class ResponseError(Exception):
    def __init__(self, code: int, message: str, id_: Optional[str] = None
                 ) -> None:
        super().__init__()
        self._code = code
        self._message = message
        self._id = id_

    @classmethod
    def from_response(cls, response: dict[str, Any]) -> ResponseError:
        '''Convert json-rpc response dict to exception.'''
        error = response['error']
        return ResponseError(error['code'], error['message'],
                             response['id'])

    def to_response(self, id_: Optional[str] = None) -> dict[str, Any]:
        '''Convert exception to json-rpc response dict.'''
        if id_ is None:
            id_ = self._id
        return {'jsonrpc': '2.0', 'error': {
            'code': self._code, 'message': self._message}, 'id': id_}

    def with_id(self, id_: str) -> ResponseError:
        return ResponseError(self._code, self._message, id_)

    def get_code(self) -> int:
        '''Get code.'''
        return self._code

    def get_message(self) -> str:
        '''Get message.'''
        return self._message

    def get_id(self) -> Optional[str]:
        '''Get id.'''
        return self._id

    def __str__(self) -> str:
        return json.dumps(self.to_response())


class MethodNotFoundError(Exception):
    pass


class _JsonRpcIO:
    PARSE_ERROR_RESPONSE = ('{"jsonrpc":"2.0","error":{"code":-32700,'
                            '"message":"Parse error"},"id":null}'
                            ).encode('utf-8')
    INVALID_REQUEST_RESPONSE = ('{"jsonrpc":"2.0","error":{"code":-32600,'
                                '"message":"Invalid Request"},"id": null}'
                                ).encode('utf-8')

    def __init__(self, reader: asyncio.StreamReader,
                 writer: asyncio.StreamWriter,
                 request_handler: Callable[[str, dict[str, Any]],
                                           Awaitable[Optional[Any]]],
                 task_manager: rcu.task._TaskManager) -> None:
        self._reader = reader
        self._writer = writer
        self._responses: dict[str, dict[str, Any]] = {}
        self._request_handler = request_handler
        self._response_events: dict[str, asyncio.Event] = {}
        self._task_manager = task_manager

    async def _write(self, data: dict[str, Any]) -> None:
        data_bytes = json.dumps(data, ensure_ascii=False, indent=None,
                                separators=(',', ':')).encode('utf-8')
        await self._write_bytes(data_bytes)

    async def _write_bytes(self, data_bytes: bytes) -> None:
        # an OverflowError is raised when length > 65535
        frame = len(data_bytes).to_bytes(2, 'big', signed=False) + data_bytes
        self._writer.write(frame)
        await self._writer.drain()

    async def _dispatch_request(self, request: dict[str, Any]
                                ) -> None:
        try:
            result = await self._request_handler(request['method'],
                                                 request['params'])
        except ResponseError as exc:
            if exc.get_id() is None:
                response = exc.to_response(request['id'])
            else:
                raise
        except MethodNotFoundError:
            response = ResponseError(-32601, "Method not found", request['id']
                                     ).to_response()
        else:
            response = {'jsonrpc': '2.0', 'result': result,
                        'id': request['id']}
        await self._write(response)

    async def _dispatch_response(self, response: dict[str, Any]
                                 ) -> None:
        id_ = response.get('id')  # default is None
        if id_ is not None and id_ in self._response_events:
            self._responses[response['id']] = response
            self._response_events[id_].set()

    async def _dispatch(self, data: dict[str, Any]) -> None:
        if 'jsonrpc' not in data or data['jsonrpc'] != '2.0':
            await self._write_bytes(_JsonRpcIO.INVALID_REQUEST_RESPONSE)
        elif 'method' in data:
            await self._dispatch_request(data)
        elif 'result' in data or 'error' in data:
            await self._dispatch_response(data)
        else:
            await self._write_bytes(_JsonRpcIO.INVALID_REQUEST_RESPONSE)

    async def run(self) -> NoReturn:
        '''Enter main loop'''
        while True:
            length_bytes = await self._reader.readexactly(2)
            length = int.from_bytes(length_bytes, 'big', signed=False)
            data_bytes = await self._reader.readexactly(length)
            try:
                data = json.loads(data_bytes.decode('utf-8'))
            except json.JSONDecodeError:
                await self._write_bytes(_JsonRpcIO.PARSE_ERROR_RESPONSE)
            else:
                if isinstance(data, dict):
                    self._task_manager.add_coro(self._dispatch(data))
                elif isinstance(data, list):
                    if len(data) == 0:
                        await self._write_bytes(_JsonRpcIO.PARSE_ERROR_RESPONSE)
                    for item in data:
                        self._task_manager.add_coro(self._dispatch(item))
                else:
                    await self._write_bytes(_JsonRpcIO.PARSE_ERROR_RESPONSE)

    async def send(self, method: str, params: dict[str, Any], id_: str
                   ) -> Any:
        '''Send a request and receive the corresponding response. A
        ResponseError is raised when an error is received. An OverflowError is
        raised when length of json is greater than 65535'''
        self._response_events[id_] = self._task_manager.event()
        await self._write({'jsonrpc': '2.0', 'method': method,
                           'params': params, 'id': id_})
        await self._response_events[id_].wait()
        del self._response_events[id_]
        response = self._responses.pop(id_)
        if 'error' in response:
            raise ResponseError.from_response(response)
        return response['result']
