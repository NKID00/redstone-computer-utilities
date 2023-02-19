# WIP, not an operational example

import redstone_computer_utilities as rcu


def calc_expected_result(test_case):
    return test_case * 16


# auto test
# arguments: name, test input iterable, expected result provider, time to
#            finalize the output, cooldown between two test cases
rcu.create_validator("more_highlevel", range(256), calc_expected_result,
                     rcu.redstonetick(2), 0)


rcu.run()
