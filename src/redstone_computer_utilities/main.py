import contextlib
from importlib.metadata import version as _version
from typing import Any, Dict, List, Optional
import asyncio
import itertools

from .script import Script
from .io import MethodNotFoundError, _JsonRpcIO, ResponseError

__version__ = _version(__package__)

_registered_scripts: List[Script] = []


def register_script(script: Script) -> None:
    '''Register the script. It is recommended to use ``create_script`` rather
    than register manually.'''
    _registered_scripts.append(script)


def create_script(name: str, description: str = '',
                  permission_level: int = 2) -> Script:
    '''Create a new script and register it.

    :param name: Name of the script. MUST be unique among all registered
    scripts and MUST be a string consists of only letters, numbers
    and underlines.
    :param description: Description of the script. SHOULD be any unicode
    string that can be displayed as plain text in Minecraft.
    :param permission_level: Permission level required to run the script.
    SHOULD be a integer >= 2 and <= 4.'''
    script = Script(name, description, permission_level)
    register_script(script)
    return script


def run(host: str = 'localhost', port: int = 37265) -> None:
    '''Try to connect with script server and enter the main loop.'''
    while True:
        try:
            asyncio.run(_run_async(host, port))
        except KeyboardInterrupt:
            print()
            _info('Stopped')
            break
        except asyncio.IncompleteReadError:
            print()
            _info('Disconnected')


_SPINNER = itertools.cycle('⠸⢰⣠⣄⡆⠇⠋⠙')


class _Wait:
    def __init__(self, message: Optional[str]) -> None:
        self._message = message

    def __call__(self, message: Optional[str]) -> Any:
        self._message = message

    def get_message(self) -> Optional[str]:
        return self._message

    def set_message(self, message: Optional[str]) -> None:
        self._message = message


@contextlib.asynccontextmanager
async def _wait(message: Optional[str] = None):
    wait_object = _Wait(message)

    async def wait():
        while True:
            if message is not None:
                print(f'\r{next(_SPINNER)} {wait_object.get_message()}', end='')
            await asyncio.sleep(0.1)
    task = asyncio.create_task(wait())
    yield wait_object
    task.cancel()
    spaces = ' ' * len(f'{next(_SPINNER)} {wait_object.get_message()}')
    print('\r' + spaces, end='\r')


def _info(message: str):
    print('  ' + message)


_warn = _info
_error = _info


async def _run_async(host: str, port: int) -> None:
    async def dispatch_request(method: str, params: Dict[str, Any]
                               ) -> Any:
        for script in _registered_scripts:
            try:
                # pylint: disable=protected-access
                return await script._dispatch_request(method, params)
            except MethodNotFoundError:
                continue
        raise MethodNotFoundError()

    async with _wait(f'Connecting to script server {host}:{port}'):
        while True:
            try:
                io = _JsonRpcIO(*await asyncio.open_connection(host, port),
                                dispatch_request)
            except OSError:
                await asyncio.sleep(1)
            else:
                break
    _info(f'Connected to script server {host}:{port}')

    task = asyncio.create_task(io.run())

    scripts_len = len(_registered_scripts)
    success_count = 0
    async with _wait() as wait:
        for i, script in enumerate(_registered_scripts):
            wait(f'Registering script(s) ({i}/{scripts_len})')
            script._set_io(io)  # pylint: disable=protected-access
            try:
                await script._register()  # pylint: disable=protected-access
            except ResponseError as exc:
                _error(f'Error occurred while registering script '
                       f'"{script.name}": {exc}')
            else:
                success_count += 1
                _info(f'Script "{script.name}" is registered')
    _info(f'Registered {success_count}/{scripts_len} script(s)')
    if success_count == 0:
        raise KeyboardInterrupt()

    async with _wait('Running') as wait:
        await asyncio.wait([task])
