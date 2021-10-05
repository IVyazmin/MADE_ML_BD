#!/usr/bin/env python

import sys

count = 0
mean = 0
var = 0

for line in sys.stdin:
    count_part, mean_part, var_part = map(float, line.split())
    var1 = (count * var + count_part * var_part) / (count + count_part)
    var2 = count * count_part * ((mean - mean_part) / (count + count_part))**2
    var = var1 + var2
    mean = (count * mean + count_part * mean_part) / (count + count_part)
    count += count_part
print(mean, var, sep='\t')