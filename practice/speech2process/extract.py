#!/usr/bin/env python
# -*- coding: utf-8 -*-
# vi: syntax=python
# vim: set fileencoding=utf-8 :
from __future__ import print_function
import re
import codecs

SPLIT = r'(?:\s|[\(\),\.:;\!\?\'\\/"„])+'
SKIP_WORDS = ['', '-']

def file_words(filename):
    with codecs.open(filename, encoding='utf-8-sig') as f:
        for line in f:
            yield from re.split(SPLIT, line)

def all_files_words(input_stream):
    for line in input_stream:
        yield from file_words(line.strip())

def final_process(words):
    for word in words:
        if word in SKIP_WORDS: continue
        yield word.lower()

def main(input_stream):
    return sorted(set(final_process(all_files_words(input_stream))))

if __name__ == "__main__":
    import fileinput
    import json
    print(json.dumps(main(fileinput.input()), indent=4, ensure_ascii=False))
