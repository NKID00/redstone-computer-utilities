import json
import sys


def main():
    source = sys.argv[1]
    target = sys.argv[2]
    with open(source, 'r', encoding='utf-8') as f:
        data = json.load(f)
    with open(target, 'w', encoding='utf-8') as f:
        for k, v in data.items():
            print('msgid', json.dumps(k), file=f)
            print('msgstr', json.dumps(v), file=f)
            print(file=f)


if __name__ == '__main__':
    main()
