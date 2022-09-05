import asyncio


class Timer:
    def __init__(self, event: asyncio.Event, task: asyncio.Task) -> None:
        self.event = event
        self.task = task
