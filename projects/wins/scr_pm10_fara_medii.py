import numpy as  np
from sklearn.svm import SVR
import matplotlib.pyplot as plt

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

start = 0
stop = 0
#print(len(PM10))
PM10 = []

days, months, years = [], [], []

for i in range(1,32):
    days.append(str(i))

for i in range(1,13):
    months.append(str(i))

years.append('2018')
years.append('2019')

print("days: " + str(days))
print("months: " + str(months))


for i in range(len(PM10_time)):
    #print(PM10[i])
    if PM10_time[i][0:8] == '26-11-18':
        start = i
    elif PM10_time[i][0:8] == "27-11-18":
        stop = i

x_data, y_data, x_data_days_hours = [],[],[]

#print(start, stop)

for i in range(start+1, stop+1):
    #print(PM10[i])
    x_data.append(i)
    y_data.append(PM10_data[i])
    x_data_days_hours.append(PM10_time[i] + " " + PM10_time[i])

print(len(x_data))
print(len(y_data))
x = []
for i in range(len(x_data)):
    x.append(i)

print(x)
print(y_data)
x_data = np.array([x_data]).T
x = np.array([x]).T
y_data = np.array(y_data)

'''
print("x: " + str(x))
print("x data: " + str(x_data))
print("y data: " + str(y_data))
print(x.shape)
print(x_data.shape)
print(y_data.shape)

'''
svr_rbf = SVR(kernel='rbf', C=1e5, gamma=0.3)
y_rbf = svr_rbf.fit(x, y_data).predict(x)
lw = 2
plt.show()
#plt.scatter(x, y_data, color='black', label='data')
plt.plot(x, y_rbf, color='red', lw=3, label='RBF model')
plt.ylim(bottom=0)
plt.ylim(top=200)
plt.xlabel('data')
plt.ylabel('target')
plt.title('Support Vector Regression')
plt.legend()
plt.show()


