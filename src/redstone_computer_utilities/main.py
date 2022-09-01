from collections import deque
from importlib.metadata import version as _version
from traceback import format_exc
from typing import Any, NoReturn
import asyncio

from .script import Script
from .io import MethodNotFoundError, _JsonRpcIO
from .cli import _cli_init, _wait, _info, _warn, _error
from .task import _TaskManager

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
    _info('  Use Ctrl-C to exit')
    while True:
        try:
            asyncio.run(_run_async(host, port))
        except KeyboardInterrupt:
            break
        except asyncio.IncompleteReadError:
            pass
    _info('  Stopped')


class _EventTriggeredError(Exception):
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

    task_manager = _TaskManager(asyncio.get_running_loop())

    async with _wait(f'Connecting to script server {host}:{port}',
                     task_manager=task_manager):
        while True:
            try:
                io = _JsonRpcIO(*await asyncio.open_connection(host, port),
                                dispatch_request, task_manager)
            except OSError:
                await asyncio.sleep(1)
            else:
                break
        _info(f'Connected to script server {host}:{port}')

    async def task_added_event_listener() -> NoReturn:
        nonlocal task_manager
        await task_manager.task_added_event.wait()
        raise _EventTriggeredError()

    async def watch_dog() -> NoReturn:
        nonlocal task_manager
        while True:
            _done, _pending = await asyncio.wait(
                task_manager.tasks.copy(),
                return_when=asyncio.FIRST_EXCEPTION)
            tasks_new: deque[asyncio.Task] = deque()
            for task in task_manager.tasks:
                try:
                    task.result()
                except asyncio.InvalidStateError:
                    tasks_new.append(task)
                    continue
                except _EventTriggeredError:
                    task_manager.task_added_event.clear()
                    tasks_new.append(task_manager.create_task(
                        task_added_event_listener()))
                    continue
                except asyncio.IncompleteReadError:
                    _warn('Disconnected')
                    raise
            task_manager.tasks.clear()
            task_manager.tasks.extend(tasks_new)

    task_manager.add_coros(task_added_event_listener(), io.run())
    watch_dog_task = task_manager.create_task(watch_dog())

    scripts_len = len(_registered_scripts)
    success_count = 0
    async with _wait(task_manager=task_manager) as wait:
        for i, script in enumerate(_registered_scripts):
            wait(f'Registering script(s) ({i}/{scripts_len})')
            # pylint: disable=protected-access
            script._set_internal(io, task_manager)
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

    async with _wait('Running', task_manager=task_manager) as wait:
        await watch_dog_task
        watch_dog_task.result()
