from __future__ import annotations
from typing import Iterable


class Vec3i:
    '''Vector of three integers. Can represent position of a block.'''

    def __init__(self, x: int, y: int, z: int) -> None:
        self._x = x
        self._y = y
        self._z = z

    @classmethod
    def from_iterable(cls, iterable: Iterable[int]) -> Vec3i:
        '''Create from triple-element list, tuple or other iterable'''
        it = iter(iterable)
        x = next(it)
        y = next(it)
        z = next(it)
        return Vec3i(x, y, z)

    @property
    def x(self):
        return self._x

    @property
    def y(self):
        return self._y

    @property
    def z(self):
        return self._z
