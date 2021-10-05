#!/usr/bin/env python

import sys

total = []

for price in sys.stdin:
	total.append(int(price))

else:
	count = len(total)
	mean = sum(total) / count
	var = [(x - mean)**2 for x in total]
	var = sum(var) / count
	print(count, mean, var)