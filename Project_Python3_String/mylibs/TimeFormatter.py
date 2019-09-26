# coding: utf-8

import time

def format(sec):
    millis = sec * 1000
    day = int(millis // (1000 * 60 * 60 * 24))
    hour = int((millis // (1000 * 60 * 60)) % 24)
    minute = int((millis // (1000 * 60)) % 60)
    second = int((millis // 1000) % 60)
    millisSec = int(millis % 1000)

    if day > 0:
        return "{0:d} day + {1:02d}:{2:02d}:{3:02d}.{4:03d}".format(day, hour, minute, second, millisSec)
    else:
        return "{0:02d}:{1:02d}:{2:02d}.{3:03d}".format(hour, minute, second, millisSec)
