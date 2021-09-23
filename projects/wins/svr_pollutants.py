import numpy as  np
from sklearn.svm import SVR
import matplotlib.pyplot as plt

time_list = [line.rstrip('\n') for line in open('time_file.txt')] #extract date/time data
data_list = [line.rstrip('\n') for line in open('data_file.txt')] #extract values data
type_list = [line.rstrip('\n') for line in open('type_file.txt')] #extract type data

#print(len(time_list))
#print(len(data_list))
#print(len(type_list))

#creating 2 lists for each parameter with time and data/values
HUM_time = []
HUM_data = []
PM1_time = []
PM1_data = []
PM10_time = []
PM10_data = []
PM2_5_time = []
PM2_5_data = []
PRES_time = []
PRES_data = []
TC_time = []
TC_data = []

#sorting the data and put every date in the specific list
for i in range(len(type_list)):
    if type_list[i] == "HUM":
        HUM_time.append(time_list[i])
        HUM_data.append(data_list[i])
    elif type_list[i] == "PM1":
        PM1_time.append(time_list[i])
        PM1_data.append(data_list[i])
    elif type_list[i] == "PM10":
        PM10_time.append(time_list[i])
        PM10_data.append(data_list[i])
    elif type_list[i] == "PM2_5":
        PM2_5_time.append(time_list[i])
        PM2_5_data.append(data_list[i])
    elif type_list[i] == "PRES":
        PRES_time.append(time_list[i])
        PRES_data.append(data_list[i])
    elif type_list[i] == "TC":
        TC_time.append(time_list[i])
        TC_data.append(data_list[i])

#print(len(TC_data))
#print(len(TC_time))
l = []
i = 0
HUM = []
while i < len(HUM_time):
    #print(HUM_time[i][9:14])  #selecting the hour
    hours = ['00', '01', '02', '03', '04', '05', '06', '07', '08','09', '10', '11',
         '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23']

    for h in hours:
        try:
            while HUM_time[i][9:11] == h:
                # print(type(HUM_data[i]))
                l.append(float(HUM_data[i]))
                i += 1
            medie = np.mean(l)
            nr_masuratori = len(l)
            element = []
            element.append(HUM_time[i][:9])
            element.append(h)
            element.append(nr_masuratori)
            element.append(medie)
            HUM.append(element)
            #print(element)
            #print(HUM_time[i][:9] + " " + h + " " + str(medie) + " " + str(nr_masuratori))
            l.clear()
        except:
            pass

start = 0
stop = 0
#print(len(HUM))
for i in range(len(HUM)):
    #print(HUM[i])
    if HUM[i][0] == '28-10-18 ':
        start = i
    elif HUM[i][0] == "02-11-18 ":
        stop = i
print("start is: " + str(start) + " and stop is: " + str(stop))

#print(HUM[start])
#print(HUM[start + 1])

x_data, y_data, x_data_days_hours = [],[],[]
for i in range(start+1, stop+1):
    x_data.append(i)
    y_data.append(HUM[i][3])
    x_data_days_hours.append(HUM[i][0] + " " + HUM[i][1])

x_data = np.array([x_data]).T
print(x_data)

'''for i in range(stop - start):
    print(str(x_data_days_hours[i]) + " " + str(x_data[i]) + " " + str(y_data[i]))
'''

'''
print(len(x_data))
svr_poly = SVR(kernel='poly', C=1e3, degree=2)
svr_rbf = SVR(kernel='rbf', C=1e3, gamma=0.1)
x_data=np.reshape(x_data, len(x_data), 1)
print(x_data.shape)
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
