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

for i in range(len(PM10_time)):
    print(PM10_time[i])

l = []
i = 0
PM10 = []
while i < len(PM10_data):
    #print(PM10_time[i][9:14])  #selecting the hour
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

start = 0
stop = 0
#print(len(PM10))

for i in range(1, len(PM10)):
    #print(PM10[i])
    if PM10[i][0] == PM10[i-1][0] and PM10[i-1][1] is '23':
        PM10.insert(i+23, PM10[i-1])
        PM10.remove(PM10[i-1])

for i in range(len(PM10)):
    #print(PM10[i])
    if PM10[i][0] == '28-10-18 ':
        start = i
    elif PM10[i][0] == "02-11-18 ":
        stop = i
#print("start is: " + str(start) + " and stop is: " + str(stop))

#print(PM10[start])
#print(PM10[start + 1])

x_data, y_data, x_data_days_hours = [],[],[]
for i in range(start+1, start+25):
    #print(PM10[i])
    x_data.append(i)
    y_data.append(PM10[i][3])
    x_data_days_hours.append(PM10[i][0] + " " + PM10[i][1])

#for i in range(stop - start):
#    print(str(x_data_days_hours[i]) + " " + str(x_data[i]) + " " + str(y_data[i]))


#print(len(x_data))
#svr_poly = SVR(kernel='poly', C=1e3, degree=2)


svr_rbf = SVR(kernel='rbf', C=1e3, gamma=0.1)

#x_data=np.reshape(x_data, len(x_data), 1)

for i in range(start+1, start+25):
    #print(PM10[i])
    x_data.append(i)
    y_data.append(PM10[i][3])
    x_data_days_hours.append(PM10[i][0] + " " + PM10[i][1])

x_data = np.array([x_data]).T
y_data = np.array(y_data)
print("x data: " + str(x_data))
print("y data: " + str(y_data))
print(x_data.shape)
print(y_data.shape)

y_rbf = svr_rbf.fit(x_data, y_data).predict(x_data)
plt.scatter(x_data, y_data, color='black', label='data')
plt.plot(x_data, y_rbf, color='red', lw=3, label='RBF model')
plt.xlabel('')
plt.ylabel('target')
plt.title('Support Vector Regression')
plt.legend()
plt.show()


'''
y_rbf = svr_rbf.fit(x_data, y_data)
y_poly = svr_poly.fit(x_data, y_data).predict(x_data)
plt.scatter(x_data, y_data, color='black', label='data')

plt.plot(x_data_days_hours, y_rbf, color='red', lw=3, label='RBF model')
plt.plot(x_data_days_hours, y_poly, color='navy', lw=2, linestyle='--', label='Polynomial model')
plt.xlabel('')
plt.ylabel('target')
plt.title('Support Vector Regression')
plt.legend()
plt.show()
'''