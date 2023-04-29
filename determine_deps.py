import subprocess
import sys


def main():
    lines = iter(subprocess.run(
        './gradlew -q dependencies', shell=True, capture_output=True, text=True
    ).stdout.splitlines())
    for line in lines:
        if 'minecraftLibraries' in line:
            break
    else:
        sys.exit(1)
    for line in lines:
        match line.split(':'):
            case [_, 'gson', version]:
                print(f'    gson_version={version}')
            case [_, 'guava', version]:
                print(f'    guava_version={version}')
            case [_, 'netty-codec', version]:
                print(f'    netty_version={version}')
            case [_, _, _]:
                pass
            case _:
                break
    else:
        sys.exit(1)


if __name__ == '__main__':
    main()
