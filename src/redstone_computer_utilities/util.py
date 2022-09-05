from base64 import b64decode, b64encode
from math import ceil


def base64_to_bytes(v: str) -> bytes:
    return b64decode(v)


def bytes_to_base64(v: bytes) -> str:
    return b64encode(v).decode('ascii')


def base64_to_int(v: str) -> int:
    return int.from_bytes(base64_to_bytes(v), 'little')


def int_to_base64(v: int) -> str:
    if v < 0:
        raise ValueError('integer must be positive, manual convert required')
    return bytes_to_base64(v.to_bytes(
        ceil(v.bit_length() / 8), 'little'))
