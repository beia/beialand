import numpy as np
from sklearn.svm import SVR
from sklearn.model_selection import train_test_split
import matplotlib.pyplot as plt
from sklearn.metrics import mean_squared_error
import math

dates, values = [], []

time_list = [line.rstrip('\n') for line in open('time_file.txt')] #extract date/time data
data_list = [line.rstrip('\n') for line in open('data_file.txt')] #extract values data
type_list = [line.rstrip('\n') for line in open('type_file.txt')] #extract type data

PM10_time, PM10_data = [], []

for i in range(len(type_list)):
    if type_list[i] == "PM10":
        PM10_time.append(time_list[i])
        PM10_data.append(data_list[i])

l = []
i = 0
PM10 = []

while i < len(PM10_time):
    #print(HUM_time[i][9:14])  #selecting the hour
    hours = ['00', '01', '02', '03', '04', '05', '06', '07', '08','09', '10', '11',
         '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23']
    for h in hours:
        try:
            while PM10_time[i][9:11] == h:
                # print(type(HUM_data[i]))
                l.append(float(PM10_data[i]))
                i += 1
            medie = np.mean(l)
            nr_masuratori = len(l)
            element = []
            element.append(PM10_time[i][:9])
            element.append(h)
            element.append(nr_masuratori)
            element.append(medie)
            PM10.append(element)
            l.clear()
        except:
            pass

X, y = [], []

for i in range(len(PM10)):
    X.append(i)
    y.append(PM10[i][3])

y_data = y
y = [ '%.2f' % elem for elem in y_data ]

X = np.array([X]).T
y = np.array(y).ravel()
y = np.array(y)

print("x length: ", len(X), "Shape: ", X.shape)
print("y length: ", len(y), "shape: ", y.shape)

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=0)

print("x train length: ", len(X_train), "shape: ", X_train.shape)
print("y train length: ", len(y_train), "shape: ", y_train.shape)

print("x test length: ", len(X_test),"shape: ", X_test.shape)

#print("y  test length: ", len(y_test), "shape: ", y_test.shape)

y_test = [float(e) for e in y_test]
#print("y  test length: ", len(y_test), "shape: ", y_test.shape)
'''

gamma_list = ['auto', 0.5, 0.3, 0.1, 0.05, 0.04, 0.03,0.02,0.01, 0.005, 0.004, 0.003,0.002,0.001]
c_list = [1, 10, 1e2, 1e3, 1e4, 1e5]


for c in c_list:
    for g in gamma_list:
        svr_rbf = SVR(kernel='rbf', C=c, gamma=g).fit(X_train, y_train)
        y_pred = svr_rbf.predict(X_test)
        y_pred = ['%.2f' % elem for elem in y_pred]
        y_pred = [float(e) for e in y_pred]
        corr_coef = np.corrcoef(y_test, y_pred)[0, 1]
        mse = mean_squared_error(y_test, y_pred)
        print("for C = ", c, " and gamma = ", g, " correlation coefficient is ", corr_coef,
              " and mean squared error = ", mse)

'''
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

svr_for_graph = SVR(kernel= 'rbf', C= 1e2, gamma= 0.1).fit(X_train, y_train)
y_pred_for_graph = svr_for_graph.predict(X)

y_test_max = max(y_test)
y_pred_max = max(y_pred)

max_val = max(y_test_max, y_pred_max)

x_graph = [x for x in range(round(max_val))]
y_graph = x_graph

plt.scatter(y_pred, y_test)
plt.plot(x_graph, y_graph, color='black')
plt.show()



y = [float(i) for i in y]
#plt.plot(X,y, color="black", label="real data")
plt.scatter(X, y, color='black', label='real data')
plt.plot(X, y_pred_for_graph, color='red', lw=2, label="RBF model")
plt.ylim(bottom=0)
plt.ylim(top=150)
plt.xlabel('data index')
plt.ylabel('pollution degree PM10')
plt.legend()
plt.show()
