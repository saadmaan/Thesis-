import pandas as pd
from sklearn.preprocessing import OneHotEncoder, LabelEncoder
lnc = LabelEncoder()
enc = OneHotEncoder()
#from  sklearn.neural_network import MLPClassifier
from sklearn.ensemble import RandomForestClassifier
from sklearn.datasets import make_classification
import csv
from sklearn.metrics import accuracy_score
from sklearn.metrics import precision_score
from sklearn.metrics import f1_score

#clf = MLPClassifier(hidden_layer_sizes=(2, ), activation='tanh', solver='sgd', alpha=0.0001, batch_size='auto', learning_rate='constant', learning_rate_init=0.001, power_t=0.5, max_iter=200, shuffle=True, random_state=1, tol=0.0001, verbose=False, warm_start=False, momentum=0.9, nesterovs_momentum=True, early_stopping=False, validation_fraction=0.1, beta_1=0.9, beta_2=0.999, epsilon=1e-08)
X, y = make_classification(n_samples=1000, n_features=4,
                           n_informative=2, n_redundant=0,
                            random_state=0, shuffle=False)
clf = RandomForestClassifier(max_depth=2, random_state=0)
clf.fit(X, y) 
 
training_data = []
target = []
count = 0
testing_data = []
actual = []

trainingFolder = 'F:\THesis\Dataset'
testingFolder = 'F:\THesis\Dataset'

print("Training the data")

filename = 'F:\THesis\Nebir Vai ppt\CHTPD\SCrime.csv'
file = open(filename, "r")
reader = csv.reader(file)
#print(filename)
count = 0
ddd =0
for line in reader:
    if count == 0:
        count = 1
        continue
    ddd = ddd+1;
    X = []
    # b = str(line[5])
    # X.append(b)
    c = str(line[8])
    X.append(c)
    d = str(line[9])
    X.append(d)
    # e = str(line[10])
    # X.append(e)
    g = str(line[11])
    X.append(g)
    h = str(line[15])
    X.append(h)
    # j = str(line[20])
    # X.append(j)
    # k = str(line[21])
    # X.append(k)
    # l = str(line[22])
    # X.append(l)
    # l = str(line[23])
    # X.append(l)
    # m = str(line[25])
    # X.append(m)
    lnc.fit(X)
    X1 = lnc.transform(X)
    X1 = X1.reshape(-1,1)
    enc = OneHotEncoder(sparse=False) #Easier to read
    enc.fit_transform(X1)
    #enc.fit_transform(X1)
    n = str(line[13])
    #enc.fit_transform(n)
    target.append(n)
    #print(X)
    training_data.append(X1)

#enc.transform(training_data).toarray()
#print(target)
print("Total data:")
print(len(target))

nsamples, nx, ny = training_data.shape
training_data1 = training_data.reshape((nsamples,nx*ny))
clf.fit(training_data1, target)

print("Testing the data")

filename = 'F:\THesis\Nebir Vai ppt\CHTPD\SCrimeTe.csv'
file = open(filename, "r")
reader = csv.reader(file)
#print(filename)
count = 0

for line in reader:
    if count == 0:
        count = 1
        continue
    X = []
    # b = str(line[5])
    # X.append(b)
    c = str(line[8])
    X.append(c)
    d = str(line[9])
    X.append(d)
    # e = str(line[10])
    # X.append(e)
    g = str(line[11])
    X.append(g)
    h = str(line[15])
    X.append(h)
    # j = str(line[20])
    # X.append(j)
    # k = str(line[21])
    # X.append(k)
    # l = str(line[22])
    # X.append(l)
    # l = str(line[23])
    # X.append(l)
    # m = str(line[25])
    # X.append(m)
    #X = lnc.fit(X)
    lnc.fit(X)
    X1 = lnc.transform(X)
    X1 = X1.reshape(-1,1)
    enc = OneHotEncoder(sparse=False) #Easier to read
    enc.fit_transform(X1)
    #enc.fit_transform(X)
    
    n = str(line[13])
    #enc.fit_transform(n)
    #if (n != 0 and n!= 1):
        #continue
    actual.append(n)
    #print(X)
    testing_data.append(X)


#enc.transform(testing_data).toarray()
#print(actual)
total = len(actual)
print(total)

output = clf.predict(testing_data)
correct = 0

print(output)
accuracy = accuracy_score(actual, output) 
for i in range(0, total):
    if (actual[i] == output[i]):
        correct += 1

error = total - correct
print(error)



#print("Complete for Feature :" + str(feature_number));
#print("Train Score : " + str(clf.score(training_data, target)));
#print("Precision : " + str(precision_score(actual, output, average='weighted') * 100.0))
#print("F1 Score : " + str(f1_score(actual, output, average='weighted') * 100.0))
#print("Error Rate : " + str(error / total * 100.0));
#print("---------------------------------------\n");

print("Total test set size : " + str(total));
print("Correct prediction : " + str(correct));
print("Incorrect Prediction : " + str(error));
print("Accuracy : " + str(accuracy * 100.0))

print('DONE')
