from __future__ import annotations
import contextlib
import io
from typing import Any, Optional, TextIO, cast
import asyncio
import itertools
import sys

import colorama

import redstone_computer_utilities as rcu


def _cli_init() -> None:
    colorama.init()


def _endswith_line_break(s: str) -> bool:
    return s.endswith(('\r', '\n'))


class _Wrapper:
    def __init__(self, inner: TextIO, message: str = '') -> None:
        self._inner = inner
        self._buffer = io.StringIO()
        self._last_message = message
        self.message = message

    def clear_message(self) -> None:
        # add more spaces to clear ^C
        print('\r', ' '*len(self._last_message), end='\r', file=self._inner)

    def print_message(self) -> None:
        if len(self._last_message) - len(self._message):
            self.clear_message()
        print('\r' + self._message, end='', file=self._inner)
        self._last_message = self._message

    @property
    def message(self) -> str:
        return self._message

    @message.setter
    def message(self, message: str) -> None:
        self._message = message
        self.print_message()

    def __call__(self, message: str) -> None:
        self.message = message

    def write(self, s: str) -> int:
        if s == '':
            return 0
        lines = s.splitlines(True)
        if len(lines) > 1 or _endswith_line_break(lines[0]):
            lines[0] = self._buffer.getvalue() + lines[0]
            self._buffer = io.StringIO()
            self.clear_message()
            if _endswith_line_break(lines[-1]):
                self._inner.write(''.join(map(lambda s: '  ' + s, lines)))
            else:
                self._inner.write(''.join(map(lambda s: '  ' + s, lines[:-1])))
                self._buffer.write(lines[-1])
            self.print_message()
        else:
            self._buffer.write(s)
        return len(s)

    def __getattr__(self, name: str) -> Any:
        return getattr(self._inner, name)


class _Wait:
    def __init__(self, message: Optional[str]) -> None:
        self._message = message

    def __call__(self, message: Optional[str]) -> Any:
        self._message = message

    @property
    def message(self) -> Optional[str]:
        return self._message

    @message.setter
    def message(self, message: Optional[str]) -> None:
        self._message = message


_SPINNER = itertools.cycle('⠸⢰⣠⣄⡆⠇⠋⠙')


@contextlib.asynccontextmanager
async def _wait(message: Optional[str] = None, *, task_manager: rcu.task._TaskManager):
    wait_object = _Wait(message)
    stdout = sys.stdout
    wrapper = _Wrapper(sys.stdout)
    sys.stdout = cast(TextIO, wrapper)

    async def wait():
        while True:
            if wait_object.message is not None:
                wrapper.message = f'\r{next(_SPINNER)} {wait_object.message}'
            await asyncio.sleep(0.1)

    task = task_manager.create_task(wait())
    try:
        yield wait_object
    finally:
        task.cancel()
        wrapper.clear_message()
        sys.stdout = stdout


def _info(message: str):
    print(message)


def _warn(message: str):
    print(colorama.Fore.YELLOW + message + colorama.Fore.RESET)


def _error(message: str):
    print(colorama.Fore.RED + message + colorama.Fore.RESET)
