import pandas as pd
df = pd.read_csv('F:\THesis\Dataset\Crime.csv')
saved_column = df['Start Date/Time'] #you can also use df['column_name']
print(saved_column)
for row in saved_column:
	str(row);
	c = row[0]



