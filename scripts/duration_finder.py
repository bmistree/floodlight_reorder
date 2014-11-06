#!/usr/bin/env python

import re
import sys

class Entry(object):
    def __init__(self,duration,tcp_port):
        self.duration = duration
        self.tcp_port = tcp_port

    def pretty_print(self):
        print str(self.duration) + '  ' + str(self.tcp_port)

class MaxMinPair(object):
    def __init__(self):
        self.current_max = None
        self.current_min = None

    def check_val(self,new_val):
        if ((self.current_max is None) or
            (self.current_max < new_val)):
            self.current_max = new_val
        if ((self.current_min is None) or
            (self.current_min > new_val)):
            self.current_min = new_val
            
class EntryRangeDict(object):
    def __init__(self):
        # keys are timestamps, values are MaxMinPair instances
        self.range_dict = {}

    def add_entry(self,entry):
        if entry.duration not in self.range_dict:
            self.range_dict[entry.duration] = MaxMinPair()
        self.range_dict[entry.duration].check_val(entry.tcp_port)

    def pretty_print(self):
        for key in sorted(self.range_dict.keys()):
            max_min_range = self.range_dict[key]
            print (
                str(key) + ':  ' + str(max_min_range.current_min) + ' - ' +
                str(max_min_range.current_max))
        
        
def print_sorted(dump_filename):
    file_text = ''
    with open(dump_filename,'r') as fd:
        file_text = fd.read()

    all_entries = []
    one_zero_regex = (
        'duration=(?P<duration>\\d+.\\d+)s.*?tp_src=(?P<tcp_port>\\d+)')
    one_three_regex = (
        'tp_src=(?P<tcp_port>\\d+)' +
        '.*?' +
        'durationSeconds=(?P<duration_seconds>\\d+)'
        '.*?' +
        'durationNanoseconds=(?P<duration_ns>\\d+)')

    for match in re.finditer(one_zero_regex,file_text):
        duration_str = match.group('duration')
        tcp_port_str = match.group('tcp_port')
        duration = float(duration_str)
        all_entries.append(Entry(duration,int(tcp_port_str)))

    for match in re.finditer(one_three_regex,file_text):
        duration_seconds_str = match.group('duration_seconds')
        duration_ns_str = match.group('duration_ns')
        duration_seconds = float (duration_seconds_str)
        duration_ns = float(duration_ns_str)
        duration = duration_seconds + (duration_ns / (1e9))

        tcp_port_str = match.group('tcp_port')
        all_entries.append(Entry(duration,int(tcp_port_str)))


    # all_entries.sort(
    #     key= lambda val: val.duration)

    # for entry in all_entries:
    #     entry.pretty_print()

    entry_range_dict = EntryRangeDict()
    for entry in all_entries:
        entry_range_dict.add_entry(entry)

    print '\n\n'
    entry_range_dict.pretty_print()
    print '\n\n'

if __name__ == '__main__':
    print_sorted(sys.argv[1])
