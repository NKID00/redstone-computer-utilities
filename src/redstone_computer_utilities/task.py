import asyncio
from collections import deque
from typing import Coroutine, Iterable, Deque


class TaskManager:
    def __init__(self, loop: asyncio.AbstractEventLoop) -> None:
        self.loop = loop
        self.tasks: Deque[asyncio.Task] = deque()
        self.task_added_event = self.event()

    def event(self) -> asyncio.Event:
        asyncio.set_event_loop(self.loop)
        return asyncio.Event()

    def add_task(self, task: asyncio.Task) -> asyncio.Task:
        self.tasks.append(task)
        self.task_added_event.set()
        return task

    def add_tasks(self, *tasks: asyncio.Task) -> Iterable[asyncio.Task]:
        self.tasks.extend(tasks)
        self.task_added_event.set()
        return tasks

    def create_task(self, coro: Coroutine) -> asyncio.Task:
        return self.loop.create_task(coro)

    def create_tasks(self, *coros: Coroutine) -> Iterable[asyncio.Task]:
        return [self.loop.create_task(coro) for coro in coros]

    def add_coro(self, coro: Coroutine) -> asyncio.Task:
        return self.add_task(self.create_task(coro))

    def add_coros(self, *coros: Coroutine) -> Iterable[asyncio.Task]:
        return self.add_tasks(*self.create_tasks(*coros))
