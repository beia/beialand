from pandas import read_csv
from pandas import datetime
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.svm import SVR
import matplotlib.pyplot as plt
from sklearn.metrics import mean_squared_error
import math
import  pickle

def parser(x):
    return datetime.strptime(x, "%Y-%m-%d %H")

#SCP_rfc3339_medii_orare.csv
dates, values = [], []
data = read_csv('SCP_rfc3339_medii_orare.csv')

'''
for row in data.iterrows():
    if row[1][0]=='mqtt.meshliumfa30.SCP3.PM10.value':
        #print(row[1][1], row[1][2])
        dates.append(parser(row[1][1][0:10] + " " + row[1][1][11:13]))
        values.append(row[1][2])

#print(type(dates[0]))
#print(values)
X = dates
X = [x for x in range(len(X))]
'''
X, y = [], []

start = int(137782)
stop = int(144553)

for row in data.iterrows():
    if int(row[0]) >= start and int(row[0]) < stop:
        if(row[1][2] != 'mean'):
            X.append(int(row[0]) - start)
            #print(row[1][2])
            y.append(float(row[1][2]))

print(X)
print(y)
#y = values

with open("X_values_serie_mare", 'wb') as f:
    pickle.dump(X, f)

with open("y_values_serie_mare", 'wb') as f:
    pickle.dump(y, f)

X = np.array([X]).T
y = np.array(y).ravel()
y = np.array(y)

print("Min value: ", min(y), ", Max value: ", max(y))
valori_mari = [x for x in y if x>500]
print("Values greater then 500: ", len(valori_mari))

print("x length: ", len(X))
print("y length: ", len(y))

print("x type: ", type(X))
print("y type: ", type(y))

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state = 0)

print("x train length: ", len(X_train))
print("y train length: ", len(y_train))

print("x test length: ", len(X_test))
print("y test length: ", len(y_test))

y_test = [float(e) for e in y_test]
y_train = [float(e) for e in y_train]

svr_rbf = SVR(kernel= 'rbf', C = 1e2, gamma = 0.1).fit(X_train, y_train)
y_pred = svr_rbf.predict(X_test)

y_pred = [ '%.2f' % elem for elem in y_pred]
y_pred = [float(e) for e in y_pred]

y_test = [ '%.2f' % elem for elem in y_test]
y_test = [float(e) for e in y_test]

'''
for i in range(len(y_test)):
    print("Predicted data: ", y_pred[i], "Tested data: ", y_test[i])
print()
'''

corr_coef = np.corrcoef(y_test, y_pred)[0,1]
print("corelatia: ",corr_coef)

mse = mean_squared_error(y_test, y_pred)
print("Mean Squared Error:", mse)

rmse = math.sqrt(mse)
print("Root Mean Squared Error:", rmse)

y_test_max = max(y_test)
y_pred_max = max(y_pred)

max_val = max(y_test_max, y_pred_max)

x_graph = [x for x in range(round(max_val))]
y_graph = x_graph

plt.scatter(y_pred, y_test)
plt.plot(x_graph, y_graph, color='black')
plt.show()

for i in range(100):
    print("x: ", X_test[i], " pred: ", y_pred[i], " real: ", y_test[i])

'''
plt.scatter(X_test, y_test, color='black', label='real data')
plt.plot(X_test, y_pred, color='red', lw=2, label="RBF model")
#plt.ylim(bottom=0)
#plt.ylim(top=250)
plt.xlabel('data index')
plt.ylabel('pollution degree PM10')
plt.legend()
plt.show()
'''