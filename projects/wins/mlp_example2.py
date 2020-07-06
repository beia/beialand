import numpy as np
from sklearn.datasets import load_iris
from sklearn.linear_model import Perceptron
import pickle
import math
from pandas import read_csv

iris = load_iris()

# print(iris.data[:3])
# print(iris.data[15:18])
# print(iris.data[37:40])

# X = iris.data[:, (2, 3)]
# print(X)
# print(type(X))
#
# # print(iris.target)
# y = (iris.target==0).astype(np.int8)
# print(y)
# print(type(y))
#
# p = Perceptron(random_state=42, max_iter=10, tol=0.001)
# p.fit(X, y)
#
# values = [[1.5, 0.1], [1.8, 0.4], [1.3,0.2]]

# for value in X:
#     pred = p.predict([value])
#     print([pred])


# from sklearn.neural_network import MLPClassifier
# X = [[0., 0.], [0., 1.], [1., 0.], [1., 1.]]
# y = [0, 0, 0, 1]
# clf = MLPClassifier(solver='lbfgs', alpha=1e-5,
#                     hidden_layer_sizes=(5, 2), random_state=1)
#
# print(clf.fit(X, y))

dates, values = [], []
data = read_csv('SCP_rfc3339_medii_orare.csv')



with open("X_values_serie_mare", 'rb') as f:
    X = pickle.load(f)

with open("y_values_serie_mare", 'rb') as f:
    y = pickle.load(f)

X, y = [], []

start = int(137782)
stop = int(144553)
hours = [0., 1., 2., 3., 4., 5., 6., 7., 8., 9., 10., 11., 12., 13., 14., 15., 16., 17., 18., 19., 20., 21., 22., 23.]

for row in data.iterrows():
    if int(row[0]) >= start and int(row[0]) < stop:
        if(row[1][2] != 'mean'):
            X.append(float(int(row[0]) - start))
            #print(row[1][2])
            y.append(float(round(float(row[1][2]))))

print(X)
print(y)
print(type(y[0]))

X_data = X
y_data = y

X, y = [], []

for i in range(1, len(X_data)):
    if math.isnan(y_data[i]) or math.isnan(X_data[i]) or math.isnan(X_data[i - 1]) or math.isnan(y_data[i - 1]):
        print("is nan", y_data[i], i)
    else:
        print([hours[(i-1)%24], y_data[i - 1], hours[i%24]], y_data[i])
        X.append([hours[(i-1)%24], y_data[i - 1], hours[i%24]])
        y.append(y_data[i])


print(X)
print(y)

print(len(X))
print(len(y))

#
# X_input = []
# y_input = []
#
# for i in range(1,len(X)):
#     if math.isnan(X[i-1]) or math.isnan(X[i]) or math.isnan(round(y[i-1])) or math.isnan(y[i]):
#         print("is nan")
#     else:
#         X_input.append([float(X[i-1]), float(round(y[i-1])), float(X[i])])
#         y_input.append(float(round(y[i])))

#     if math.isnan(PM10[i][3]) or math.isnan(PM10[i][1]) or math.isnan(PM10[i - 1][1]) or math.isnan(PM10[i - 1][3]):
#         print("is nan", PM10[i][3], i)
#     else:
#         X.append([PM10[i - 1][1], PM10[i - 1][3], PM10[i][1]])
#         y.append(PM10[i][3])

# print(X_input)
# print(y_input)
#
with open("X_values_serie_mare", 'wb') as f:
    pickle.dump(X, f)

with open("y_values_serie_mare", 'wb') as f:
    pickle.dump(y, f)