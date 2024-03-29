from __future__ import annotations
import json
import sys
import shlex


def main():
    source = sys.argv[1]
    target = sys.argv[2]
    data: dict[str, str] = {}
    with open(source, 'r', encoding='utf-8') as f:
        msgid = ''
        for line in f.readlines():
            items = shlex.split(line, comments=True)
            if len(items) < 2:
                continue
            if items[0] == 'msgid':
                msgid = items[1]
            if items[0] == 'msgstr' and msgid != '':
                data[msgid] = items[1]
    with open(target, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=4, sort_keys=True)


if __name__ == '__main__':
    main()
