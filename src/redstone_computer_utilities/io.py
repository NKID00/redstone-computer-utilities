from __future__ import annotations
import asyncio
import json
from typing import Any, Awaitable, Callable, Dict, NoReturn, Optional


class ResponseError(Exception):
    def __init__(self, code: int, message: str, id_: str) -> None:
        super().__init__()
        self._code = code
        self._message = message
        self._id = id_

    @classmethod
    def from_response(cls, response: Dict[str, Any]) -> ResponseError:
        '''Convert json-rpc response dict to exception.'''
        error = response['error']
        return ResponseError(error['code'], error['message'],
                             response['id'])

    def to_response(self, id_: Optional[str] = None) -> Dict[str, Any]:
        '''Convert exception to json-rpc response dict.'''
        if id_ is None:
            id_ = self._id
        return {'jsonrpc': '2.0', 'error': {
            'code': self._code, 'message': self._message}, 'id': id_}

    def get_code(self) -> int:
        '''Get code.'''
        return self._code

    def get_message(self) -> str:
        '''Get message.'''
        return self._message

    def get_id(self) -> str:
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
                 request_handler: Callable[[str, Dict[str, Any]],
                                           Awaitable[Optional[Any]]]) -> None:
        self._reader = reader
        self._writer = writer
        self._responses: Dict[str, Dict[str, Any]] = {}
        self._request_handler = request_handler
        self._response_events: Dict[str, asyncio.Event] = {}

    async def _write(self, data: Dict[str, Any]) -> None:
        data_bytes = json.dumps(data, ensure_ascii=False, indent=None,
                                separators=(',', ':')).encode('utf-8')
        await self._write_bytes(data_bytes)

    async def _write_bytes(self, data_bytes: bytes) -> None:
        # an OverflowError is raised when length > 65535
        frame = len(data_bytes).to_bytes(2, 'big', signed=False) + data_bytes
        self._writer.write(frame)
        await self._writer.drain()

    async def _dispatch_request(self, request: Dict[str, Any]
                                ) -> None:
        try:
            result = await self._request_handler(request['method'],
                                                 request['params'])
        except ResponseError as exc:
            response = exc.to_response()
        except MethodNotFoundError:
            response = ResponseError(-32601, "Method not found", request['id']
                                     ).to_response()
        else:
            response = {'jsonrpc': '2.0', 'result': result,
                        'id': request['id']}
        await self._write(response)

    async def _dispatch_response(self, response: Dict[str, Any]
                                 ) -> None:
        id_ = response.get('id')  # default is None
        if id_ is not None and id_ in self._response_events:
            self._responses[response['id']] = response
            self._response_events[id_].set()

    async def _dispatch(self, data: Dict[str, Any]) -> None:
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
                    await self._dispatch(data)
                elif isinstance(data, list):
                    if len(data) == 0:
                        await self._write_bytes(_JsonRpcIO.PARSE_ERROR_RESPONSE)
                    for item in data:
                        await self._dispatch(item)
                else:
                    await self._write_bytes(_JsonRpcIO.PARSE_ERROR_RESPONSE)

    async def send(self, method: str, params: Dict[str, Any], id_: str
                   ) -> Any:
        '''Send a request and receive the corresponding response. A
        ResponseError is raised when an error is received. An OverflowError is
        raised when length of json is greater than 65535'''
        self._response_events[id_] = asyncio.Event()
        await self._write({'jsonrpc': '2.0', 'method': method,
                           'params': params, 'id': id_})
        await self._response_events[id_].wait()
        del self._response_events[id_]
        response = self._responses.pop(id_)
        if 'error' in response:
            raise ResponseError.from_response(response)
        return response['result']