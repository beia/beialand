import numpy as  np
from sklearn.svm import SVR
import matplotlib.pyplot as plt
from sklearn.model_selection import train_test_split

time_list = [line.rstrip('\n') for line in open('time_file.txt')] #extract date/time data
data_list = [line.rstrip('\n') for line in open('data_file.txt')] #extract values data
type_list = [line.rstrip('\n') for line in open('type_file.txt')] #extract type data

#creating 2 lists for each parameter with time and data/values
PM10_time = []
PM10_data = []

#sorting the data and put every date in the specific list
for i in range(len(type_list)):
    if type_list[i] == "PM10":
        PM10_time.append(time_list[i])
        PM10_data.append(data_list[i])

'''
for i in range(len(PM10_time)):
    print(PM10_time[i])
'''
#print(len(PM10))
PM10 = []

days_of_the_week = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday']
PM10_time.insert(0,'24-10-18')

unic_days = []
unic_days.append(PM10_time[0][0:8])

for d in PM10_time:
    if d[0:8] in unic_days:
        pass
    else:
        unic_days.append(d[0:8])

for d in unic_days:
    start = 0
    stop = 0
    for i in range(len(PM10_time)):
        if PM10_time[i][0:8] == d:
            start = i
        elif PM10_time[i][0:8] == unic_days[unic_days.index(d)+1]:
            stop = i

    x_data, y_data, x_data_days_hours = [], [], []

    for i in range(start + 1, stop + 1):
        # print(PM10[i])
        x_data.append(i)
        y_data.append(PM10_data[i])
        x_data_days_hours.append(PM10_time[i] + " " + PM10_time[i])

    #print("start is: " + str(start))
    #print("stop is: " + str(stop))
    #print(len(x_data))
    #print(len(y_data))
    x = []
    for i in range(len(x_data)):
        x.append(i)

    print("lungimea lui x: ", len(x))
    print("x: ", x)

    print("lungimea lui y: ", len(y_data))
    print("y: ", y_data)

    x_data = np.array([x_data]).T
    x = np.array([x]).T
    print("x 2: ", x)

    print("y_data: ", y_data)
    y = y_data
    y_data = np.array(y_data).ravel()
    print("y_data_2: ", y_data)

    y_data = np.array(y_data)
    print("y_data_3: ", y_data)

    print("forma lui x ", x.shape)
    print("forma lui y ", y_data.shape)
    X_train, X_test, y_train, y_test = train_test_split(x, y_data, test_size=0.2)
    print("x train shape: ", X_train.shape)
    print("y train shape: ", y_train.shape)

    '''
    print("x: " + str(x))
    print("x data: " + str(x_data))
    print("y data: " + str(y_data))
    print(x.shape)
    print(x_data.shape)
    print(y_data.shape)
    '''

    svr_rbf = SVR(kernel='rbf', C=1e2, gamma=0.025)
    y_rbf = svr_rbf.fit(x, y_data).predict(x)
    plt.show()
    y = [float(i) for i in y_data]
    plt.scatter(x, y, color='black', label='data')
    plt.plot(x, y_rbf, color='red', lw=3, label='RBF model')
    plt.ylim(bottom=0)
    plt.ylim(top=200)
    plt.xlabel('data')
    plt.ylabel('grad de poluare PM10')
    plt.title(days_of_the_week[(unic_days.index(d)+3)%7] + " " + unic_days[unic_days.index(d)+1])
    plt.legend()
    plt.show()

