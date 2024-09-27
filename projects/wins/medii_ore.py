import numpy as  np

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
            #print(element)
            #print(HUM_time[i][:9] + " " + h + " " + str(medie) + " " + str(nr_masuratori))
            l.clear()
        except:
            print("exeptie")

print(len(PM10_data))
for i in range(len(PM10)):
        print(PM10[i])