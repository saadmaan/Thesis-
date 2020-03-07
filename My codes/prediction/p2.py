import pandas as pd
from sklearn.preprocessing import OneHotEncoder, LabelEncoder
from  sklearn.neural_network import MLPClassifier
from sklearn.ensemble import RandomForestClassifier
from sklearn.datasets import make_classification
import csv
from sklearn.metrics import accuracy_score
from sklearn.metrics import precision_score
from sklearn.metrics import f1_score 
 

trainingFolder = 'F:\THesis\Dataset'
testingFolder = 'F:\THesis\Dataset'

print("Training the data")

#Train Data preprocessing
filename = 'F:\THesis\Dataset\SCrime.csv'

train = pd.read_csv(filename)
train_x = train[['Victims', 'Crime Name3', 'City ']].values
train_y = train[['Zip Code']]

#Test data preprocessing
filename = 'F:\THesis\Dataset\SCrimeTe.csv'

test = pd.read_csv(filename)
test_x = test[['Victims', 'Crime Name3', 'City ']].values
test_y = test[['Zip Code']]

le = LabelEncoder()
le.fit(train_y.append(test_y, ignore_index = True))
train_y = le.transform(train_y)
test_y = le.transform(test_y)

# for i in range(1, 3):
#     le = LabelEncoder()
#     test_x[i] = le.fit_transform(test_x[i])

print(train_y[:5])

#Random Forest
# print('Training RandomForest...')
# clf = RandomForestClassifier(n_estimators = 20)
# clf.fit(train_x, train_y)
# pred = clf.predict(test_x)
# accuracy = accuracy_score(test_y, pred)
# print('RandomForest accuracy:', accuracy)
