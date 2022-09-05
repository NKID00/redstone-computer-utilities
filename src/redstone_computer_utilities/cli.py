from __future__ import annotations
import contextlib
import io
from typing import Any, Optional, TextIO, cast
import asyncio
import itertools
import sys

import colorama

from .task import TaskManager


def cli_init() -> None:
    colorama.init()


def endswith_line_break(s: str) -> bool:
    return s.endswith(('\r', '\n'))


class WaitIOWrapper:
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
        if len(lines) > 1 or endswith_line_break(lines[0]):
            lines[0] = self._buffer.getvalue() + lines[0]
            self._buffer = io.StringIO()
            self.clear_message()
            if endswith_line_break(lines[-1]):
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


class Wait:
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


SPINNER_ITER = itertools.cycle('⠸⢰⣠⣄⡆⠇⠋⠙')


@contextlib.asynccontextmanager
async def spinner(message: Optional[str] = None, *, task_manager: TaskManager):
    wait_object = Wait(message)
    stdout = sys.stdout
    wrapper = WaitIOWrapper(sys.stdout)
    sys.stdout = cast(TextIO, wrapper)

    async def wait():
        while True:
            if wait_object.message is not None:
                wrapper.message = f'\r{next(SPINNER_ITER)} {wait_object.message}'
            await asyncio.sleep(0.1)

    task = task_manager.create_task(wait())
    try:
        yield wait_object
    finally:
        task.cancel()
        wrapper.clear_message()
        sys.stdout = stdout


def print_colored(color: str, *values: object, sep: Optional[str] = None,
                  end: Optional[str] = None) -> None:
    if len(values) == 0:
        return
    elif len(values) == 1:
        print(color + str(values[0]) + colorama.Fore.RESET, end=end)
    else:
        print(color + str(values[0]), *values[1:-1],
              values[-1] + colorama.Fore.RESET, sep=sep, end=end)


def info(*values: object, sep: Optional[str] = None,
         end: Optional[str] = None) -> None:
    print(*values, sep=sep, end=end)


def warn(*values: object, sep: Optional[str] = None,
         end: Optional[str] = None):
    print_colored(colorama.Fore.YELLOW, *values, sep=sep, end=end)


def error(*values: object, sep: Optional[str] = None,
          end: Optional[str] = None):
    print_colored(colorama.Fore.RED, *values, sep=sep, end=end)
