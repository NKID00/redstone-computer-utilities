import redstone_computer_utilities as rcu

script = rcu.create_script("highlevel")


def calc_expected_result(test_case):
    return test_case * 16


@script.main
# auto argument parse & validate
async def _(target: rcu.Interface, result: rcu.Interface):
    for test_case in range(256):
        await target.write(test_case)
        await script.wait(rcu.redstonetick(2))  # highlevel wait api
        expected = calc_expected_result(test_case)
        real = await result.read()
        if expected != real:
            print(f'Test case failed: expect {expected}, got {real}')


@script.main  # multiple main functions
# auto dispatch according to arguments
async def _():
    while True:
        # class Interval: rcu.gametick, rcu.redstonetick, rcu.second
        await script.wait(rcu.gametick(20))  # different time units
        print(f'Current gametime is: {await script.gametime()}')


rcu.run()
