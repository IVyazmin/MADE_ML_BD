import pandas as pd

df = pd.read_csv('AB_NYC_2019.csv')
df = df.dropna(subset=['price'])
mean = df.price.mean()
var = df.price.var(ddof=0)
with open('results.txt', 'w') as f:
	f.write(str(mean) + ' ' + str(var))

# with open('prices.txt', 'w') as f:
# 	for p in df.price.values:
# 		f.write(str(p) + '\n')