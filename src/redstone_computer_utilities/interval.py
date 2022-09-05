from fractions import Fraction
from numbers import Rational, Real
from typing import Union


class Interval:
    '''Non-ambiguous interval.'''

    def __init__(self, interval_gametick: int, tps: Union[int, Rational] = 20):
        self._gametick = interval_gametick
        self._tps = tps

    def __eq__(self, other: object) -> bool:
        return (isinstance(other, Interval)
                and self._gametick == other._gametick)

    def __hash__(self) -> int:
        return hash((self._gametick,))

    @property
    def gametick(self) -> int:
        return self._gametick

    @property
    def redstonetick(self) -> Union[int, Rational]:
        # int is not rational to mypy :(
        interval = Fraction(self._gametick, 2)
        if interval.denominator == 1:
            return interval.numerator
        return interval

    @property
    def second(self) -> Union[int, Rational]:
        interval = Fraction(self._gametick, self._tps)
        if interval.denominator == 1:
            return interval.numerator
        return interval

    @property
    def tps(self) -> Union[int, Rational]:
        return self._tps


def gametick(interval: int, tps: Union[int, Rational] = 20) -> Interval:
    '''Non-ambiguous interval.'''
    return Interval(interval, tps)


def redstonetick(interval: Union[int, Real],
                 tps: Union[int, Rational] = 20) -> Interval:
    '''Non-ambiguous interval. Non-integral value will be floored after
    converted to gametick.'''
    return Interval(int(interval * 2), tps)


def second(interval: Union[int, Real],
           tps: Union[int, Rational] = 20) -> Interval:
    '''Non-ambiguous interval. Non-integral value will be floored after
    converted to gametick.'''
    return Interval(int(interval * tps), tps)
