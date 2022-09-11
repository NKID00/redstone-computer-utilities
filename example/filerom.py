from typing import BinaryIO, Optional
import math
import io

import redstone_computer_utilities as rcu


def main() -> None:
    script = rcu.create_script('filerom')
    data_interface: rcu.Interface
    data_size: int = 0
    addr_interface: rcu.Interface
    addr_size: int = 0
    file: Optional[BinaryIO] = None

    # called when addr interface is updated
    async def callback() -> None:
        addr = await addr_interface.read()
        if file is not None:
            file.seek(addr, io.SEEK_SET)
            if file.readable():
                await data_interface.write(file.read(math.ceil(data_size / 8)))

    # /rcu run filerom <filename> <data interface> <addr interface>
    @script.main
    async def _(filename: str, data: rcu.Interface,
                addr: rcu.Interface) -> None:
        nonlocal data_interface, data_size, addr_interface, addr_size, file
        data_interface = data
        addr_interface = addr
        for name, interface_info in (await script.list_interface()).items():
            if name == data_interface.name:
                data_size = interface_info[4]
            elif name == addr_interface.name:
                addr_size = interface_info[4]
        if data_size == 0 or addr_size == 0:
            await script.error('interface not found')
            raise rcu.ResponseErrors.ILLEGAL_ARGUMENT
        file = open(filename, 'rb')
        await script.on_interface_update_immediate(addr_interface)(callback)

    rcu.run()
    # after script is stopped
    if file is not None:
        file.close()


if __name__ == '__main__':
    main()
