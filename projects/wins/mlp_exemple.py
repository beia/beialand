from sklearn.neural_network import MLPClassifier

X = [[0., 0., 1.], [1., 1., 0.]]
# input = 3 neuron : ora_crt, cantit_pm_ora_Crt, ora_urmat
#output: 1 sg neuron: cantit_pm_ora_urmat
y = [0., 1.]
clf = MLPClassifier(solver='lbfgs', alpha=1e-5, hidden_layer_sizes=(5, 2), random_state=1)
clf.fit(X, y)

print(clf.predict([[2., 2., 1.], [-1., -2., 0.]]))
# answer = 2.5376456
# answer = round(answer, 2)
# print(type(answer))
