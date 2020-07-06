import pickle
from math import sqrt
import numpy as np
import matplotlib.pyplot as plt
from math import sqrt
from sklearn.neural_network import MLPClassifier
from sklearn.model_selection import train_test_split
from sklearn.metrics import mean_squared_error, confusion_matrix
import torch


#Set mic de date:

# clf = torch.load("model_small_dataset")
#
# with open("X_values", 'rb') as f:
#     X = pickle.load(f)
#
# with open("y_values", 'rb') as f:
#     y = pickle.load(f)


#Set mare de date:

# clf = torch.load("model_large_dataset")

with open("X_values_serie_mare", 'rb') as f:
    X = pickle.load(f)

with open("y_values_serie_mare", 'rb') as f:
    y = pickle.load(f)


print(len(X))
print(len(y))

print()

print("X: ", X)
print("y: ", y)

print()

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=0)
# clf = MLPClassifier(solver='adam', alpha=0.00001, hidden_layer_sizes=(100,), random_state=False)
# clf = MLPClassifier(hidden_layer_sizes=(100,), alpha=0.0001)
clf = MLPClassifier()

# clf = MLPClassifier(solver='sofmax', alpha=1000, hidden_layer_sizes=(15,), random_state=1)
clf.fit(X_train, y_train)
y_pred = []

torch.save(clf, "model_large_dataset3")

print("X_test: ", X_test)
print("y_test: ",y_test)
print()

for i in range(len(X_test)):
    pred = clf.predict([X_test[i]])
    print(pred[0], y_test[i])
    y_pred.append(pred[0])

print()

corr_coef = np.corrcoef(y_test, y_pred)[0,1]
print("corelatia: ",corr_coef)

mse = mean_squared_error(y_test, y_pred)
print("Mean Squared Error:", mse)

print()

max_val = max(max(y_test), max(y_pred))

x_graph = [x for x in range(int(round(max_val)))]
y_graph = x_graph

plt.scatter(y_pred, y_test)
plt.plot(x_graph, y_graph, color='black')
plt.show()

print(len([x for x in y if x > 400]))

lista = [x for x in y if x > 400]

for x in lista:
    print(y.index(x))

plt.plot(y)
plt.show()