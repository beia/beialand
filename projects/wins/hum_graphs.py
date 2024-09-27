import numpy as  np
from sklearn.svm import SVR
import matplotlib.pyplot as plt

time_list = [line.rstrip('\n') for line in open('time_file.txt')] #extract date/time data
data_list = [line.rstrip('\n') for line in open('data_file.txt')] #extract values data
type_list = [line.rstrip('\n') for line in open('type_file.txt')] #extract type data

#creating 2 lists for each parameter with time and data/values
HUM_time = []
HUM_data = []

#sorting the data and put every date in the specific list
for i in range(len(type_list)):
    if type_list[i] == "HUM":
        HUM_time.append(time_list[i])
        HUM_data.append(data_list[i])

'''
for i in range(len(PM10_time)):
    print(PM10_time[i])
'''
#print(len(PM10))
HUM = []

days_of_the_week = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday']
HUM_time.insert(0,'24-10-18')

unic_days = []
unic_days.append(HUM_time[0][0:8])

for d in HUM_time:
    if d[0:8] in unic_days:
        pass
    else:
        unic_days.append(d[0:8])

for d in unic_days:
    start = 0
    stop = 0
    for i in range(len(HUM_time)):
        if HUM_time[i][0:8] == d:
            start = i
        elif HUM_time[i][0:8] == unic_days[unic_days.index(d)+1]:
            stop = i

    x_data, y_data, x_data_days_hours = [], [], []

    for i in range(start + 1, stop + 1):
        # print(PM10[i])
        x_data.append(i)
        y_data.append(HUM_data[i])
        x_data_days_hours.append(HUM_time[i] + " " + HUM_time[i])

    print("start is: " + str(start))
    print("stop is: " + str(stop))
    print(len(x_data))
    print(len(y_data))
    x = []

    for i in range(len(x_data)):
        x.append(i)

    y_data=np.array(y_data).ravel()
    print(x)
    print(y_data)
    x_data = np.array([x_data]).T
    x = np.array([x]).T
    y = y_data
    y_data = np.array(y_data)

    '''
    print("x: " + str(x))
    print("x data: " + str(x_data))
    print("y data: " + str(y_data))
    print(x.shape)
    print(x_data.shape)
    print(y_data.shape)
    '''
    svr_rbf = SVR(kernel='rbf', C=1e1, gamma=0.025)
    y_rbf = svr_rbf.fit(x, y_data).predict(x)
    plt.show()
    #plt.scatter(x, y, color='black', label='data')
    plt.plot(x, y_rbf, color='red', lw=3, label='RBF model')
    #plt.ylim(bottom=0)
    #plt.ylim(top=200)
    plt.xlabel('data')
    plt.ylabel('grad de umiditate')
    plt.title(days_of_the_week[(unic_days.index(d)+3)%7] + " " + unic_days[unic_days.index(d)+1])
    plt.legend()
    plt.show()

