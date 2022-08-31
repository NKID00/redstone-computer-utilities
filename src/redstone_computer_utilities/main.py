from collections import deque
from importlib.metadata import version as _version
from traceback import format_exc
from typing import Any, NoReturn
import asyncio

from .script import Script
from .io import MethodNotFoundError, _JsonRpcIO
from .cli import _cli_init, _wait, _info, _warn, _error

__version__ = _version(__package__)

_registered_scripts: list[Script] = []


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
    _cli_init()
    while True:
        try:
            asyncio.run(_run_async(host, port))
        except KeyboardInterrupt:
            break
        except asyncio.IncompleteReadError:
            pass
    _info('  Stopped')


class EventTriggeredError(Exception):
    pass


async def _run_async(host: str, port: int) -> None:
    async def dispatch_request(method: str, params: dict[str, Any]
                               ) -> Any:
        for script in _registered_scripts:
            try:
                # pylint: disable=protected-access
                return await script._dispatch_request(method, params)
            except MethodNotFoundError:
                continue
        raise MethodNotFoundError()

    tasks: deque[asyncio.Task] = deque()
    task_added_event = asyncio.Event()

    async with _wait(f'Connecting to script server {host}:{port}'):
        while True:
            try:
                io = _JsonRpcIO(*await asyncio.open_connection(host, port),
                                dispatch_request, tasks, task_added_event)
            except OSError:
                await asyncio.sleep(1)
            else:
                break
        _info(f'Connected to script server {host}:{port}')

    async def task_added_event_listener() -> NoReturn:
        nonlocal task_added_event
        await task_added_event.wait()
        raise EventTriggeredError()

    async def watch_dog() -> NoReturn:
        nonlocal tasks
        while True:
            _done, _pending = await asyncio.wait(
                tasks.copy(), return_when=asyncio.FIRST_EXCEPTION)
            tasks_new: deque[asyncio.Task] = deque()
            for task in tasks:
                try:
                    task.result()
                except asyncio.InvalidStateError:
                    tasks_new.append(task)
                    continue
                except EventTriggeredError:
                    task_added_event.clear()
                    tasks_new.append(asyncio.create_task(
                        task_added_event_listener()))
                    continue
                except asyncio.IncompleteReadError:
                    _warn('Disconnected')
                    raise
            tasks.clear()
            tasks.extend(tasks_new)

    tasks.append(asyncio.create_task(task_added_event_listener()))
    watch_dog_task = asyncio.create_task(watch_dog())
    tasks.append(asyncio.create_task(io.run()))

    scripts_len = len(_registered_scripts)
    success_count = 0
    async with _wait() as wait:
        for i, script in enumerate(_registered_scripts):
            wait(f'Registering script(s) ({i}/{scripts_len})')
            script._set_io(io)  # pylint: disable=protected-access
            try:
                await script._register()  # pylint: disable=protected-access
            except Exception:  # pylint: disable=broad-except
                _error(f'Error occurred while registering script '
                       f'{script.name}')
                _error(format_exc())
            else:
                success_count += 1
                _info(f'Script "{script.name}" is registered')
        _info(f'Registered {success_count}/{scripts_len} script(s)')
    if success_count == 0:
        raise KeyboardInterrupt()

    async with _wait('Running') as wait:
        await watch_dog_task
        watch_dog_task.result()
