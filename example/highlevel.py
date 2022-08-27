# WIP, not a operational example

import redstone_computer_utilities as rcu

script = rcu.create_script("highlevel")


def calc_expected_result(test_case: int):
    return test_case * 16


@script.main
# auto argument parse & validate
async def _(target: rcu.Interface, result: rcu.Interface):
    for test_case in range(256):
        await target.write(test_case)
        await script.wait_redstonetick(2)  # highlevel wait api
        expected = calc_expected_result(test_case)
        real = await result.read()
        if expected != real:
            print(f'Test case failed: expect {expected}, got {real}')


@script.main  # multiple main functions
# auto dispatch according to arguments
async def _():
    while True:
        await script.wait_gametick(20)
        print(f'Current gametime is: {await script.gametime()}')


rcu.run()
