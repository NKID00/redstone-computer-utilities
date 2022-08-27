# WIP, not a operational example

import redstone_computer_utilities as rcu


def calc_expected_result(test_case: int):
    return test_case * 16


# auto test
rcu.create_validator("more_highlevel", range(256), calc_expected_result, 2)


rcu.run()
