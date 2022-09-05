from __future__ import annotations
import asyncio
import json
from typing import Any, Callable, Coroutine, NoReturn, Optional

from .task import TaskManager


class ResponseError(Exception):
    def __init__(self, code: int, message: str, id_: Optional[str] = None
                 ) -> None:
        super().__init__()
        self._code = code
        self._message = message
        self._id = id_

    def __eq__(self, other: object) -> bool:
        return (isinstance(other, ResponseError)
                and self._code == other._code)

    def __hash__(self) -> int:
        return hash((self._code,))

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

    def with_cause(self, cause: Exception) -> ResponseError:
        result = self.copy()
        result.__cause__ = cause
        return result

    def copy(self) -> ResponseError:
        return ResponseError(self._code, self._message, self._id)

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


class ResponseErrors:
    INVALID_REQUEST = ResponseError(-32600, "Invalid Request")
    METHOD_NOT_FOUND = ResponseError(-32601, "Method not found")
    INVALID_AUTH_KEY = ResponseError(-1, "Invalid authorization key")
    ILLEGAL_NAME = ResponseError(-2, "Illegal name")
    NAME_EXISTS = ResponseError(-3, "Target with the name already exists")
    INVALID_PERMISSION_LEVEL = ResponseError(-4, "Invalid permission level")
    SCRIPT_NOT_FOUND = ResponseError(-5, "Script cannot be found")
    ILLEGAL_ARGUMENT = ResponseError(-6, "Illegal argument")
    SCRIPT_INTERNAL_ERROR = ResponseError(-7, "Script internal error")
    EVENT_NOT_FOUND = ResponseError(-8, "Event cannot be found")
    EVENT_CALLBACK_ALREADY_REGISTERED = ResponseError(
        -9, "Event callback is already registered")
    EVENT_CALLBACK_NOT_REGISTERED = ResponseError(
        -10, "Event callback is not registered")
    ACCESS_DENIED = ResponseError(-11, "Access denied")
    INTERFACE_NOT_FOUND = ResponseError(-12, "Interface cannot be found")
    PLAYER_NOT_FOUND = ResponseError(-13, "Player cannot be found")
    WORLD_NOT_FOUND = ResponseError(-14, "World cannot be found")
    INVALID_SIZE = ResponseError(-15, "Invalid size")
    BLOCK_NOT_TARGET = ResponseError(
        -16, "Non-target block is found in the interface")


class MethodNotFoundError(Exception):
    pass


class JsonRpcIO:
    PARSE_ERROR_RESPONSE = ('{"jsonrpc":"2.0","error":{"code":-32700,'
                            '"message":"Parse error"},"id":null}'
                            ).encode('utf-8')
    INVALID_REQUEST_RESPONSE = ('{"jsonrpc":"2.0","error":{"code":-32600,'
                                '"message":"Invalid Request"},"id": null}'
                                ).encode('utf-8')

    def __init__(self, reader: asyncio.StreamReader,
                 writer: asyncio.StreamWriter,
                 request_handler: Callable[[str, dict[str, Any]],
                                           Coroutine[Any, Any, Any]],
                 task_manager: TaskManager) -> None:
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
            result = await self._request_handler(
                request['method'], request['params'])
        except ResponseError as exc:
            response = exc.to_response(request['id'])
        except MethodNotFoundError:
            response = ResponseErrors.METHOD_NOT_FOUND.with_id(
                request['id']).to_response()
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
            await self._write_bytes(JsonRpcIO.INVALID_REQUEST_RESPONSE)
        elif 'method' in data:
            await self._dispatch_request(data)
        elif 'result' in data or 'error' in data:
            await self._dispatch_response(data)
        else:
            await self._write_bytes(JsonRpcIO.INVALID_REQUEST_RESPONSE)

    async def run(self) -> NoReturn:
        '''Enter main loop'''
        while True:
            length_bytes = await self._reader.readexactly(2)
            length = int.from_bytes(length_bytes, 'big', signed=False)
            data_bytes = await self._reader.readexactly(length)
            try:
                data = json.loads(data_bytes.decode('utf-8'))
            except json.JSONDecodeError:
                await self._write_bytes(JsonRpcIO.PARSE_ERROR_RESPONSE)
            else:
                if isinstance(data, dict):
                    self._task_manager.add_coro(self._dispatch(data))
                elif isinstance(data, list):
                    if len(data) == 0:
                        await self._write_bytes(
                            JsonRpcIO.PARSE_ERROR_RESPONSE)
                    for item in data:
                        self._task_manager.add_coro(self._dispatch(item))
                else:
                    await self._write_bytes(JsonRpcIO.PARSE_ERROR_RESPONSE)

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
