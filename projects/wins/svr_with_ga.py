from operator import itemgetter
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.svm import SVR
import matplotlib.pyplot as plt
import csv



def crossover(parent_1, parent_2):
    offspring = [x for x in parent_1]
    for i in range(int(len(offspring)/2), len(offspring)):
        offspring[i] = parent_2[i]
    return offspring

def mutation(offspring_crossover, p):
    for idx in range(len(population)):
        if np.random.rand() > 1-p:
            offspring_crossover[idx] = int(not offspring_crossover[idx])
    return offspring_crossover

def apply_svr(X_train, y_train, X_test, y_test):
    svr_rbf = SVR(kernel='rbf', C=1e2, gamma=0.1).fit(X_train, y_train)
    y_pred = svr_rbf.predict(X_test)
    y_pred = ['%.2f' % elem for elem in y_pred]
    y_pred = [float(e) for e in y_pred]
    y_test = ['%.2f' % elem for elem in y_test]
    y_test = [float(e) for e in y_test]
    corr_coef = np.corrcoef(y_test, y_pred)[0, 1]
    return corr_coef

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

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state = 0)

y_test = [float(e) for e in y_test]
#y_train = [float(e) for e in y_train]

#X_test = [float(e) for e in X_test]
#X_train = [float(e) for e in X_train]

population = []

for i in range(20):
    population.append([np.random.choice([1, 0], p=[0.95, 0.05]) for i in range(len(X_train))])

#print(X_train.shape)

num_generations = 300
chr_hist, acc_hist = [], []
max_acc = 0
best_chr = []
bests_chr = []
'''
print(X_train.shape, len(X_train))
print(X_test.shape, len(X_test))
print(X_train.shape, len(X_train))
print(X_test.shape, len(X_test))
'''

corr_coef = apply_svr(X_train, y_train, X_test, y_test)
print("Accuracy without GA: ", corr_coef*100)



with open("accuracy.csv", "w", newline='') as file:
    writer = csv.writer(file)
    writer.writerow(['Generation', 'Accuracy'])
    writer.writerow(['0', (str(corr_coef*100))])

    for generation in range(1, num_generations+1):
        print("Generation : ", generation)
        chr_and_acc_list = []
        for chr in population:
            new_X_train, new_y_train = [], []
            for i in range(len(chr)):
                if chr[i] == 1:
                    new_X_train.append(X_train[i])
                    new_y_train.append(y_train[i])
            #print(len(new_y_train), len(new_X_train))


            corr_coef = apply_svr(new_X_train, new_y_train, X_test, y_test)
            chr_and_acc_list.append([chr, corr_coef*100])

            #print(corr_coef)
        chr_and_acc_list = sorted(chr_and_acc_list, key=itemgetter(1))
        if chr_and_acc_list[-1][1] > max_acc:
            best_chr = [x for x in chr_and_acc_list[-1][0]]
            max_acc = chr_and_acc_list[-1][1]

        chrOfOnes = [x for x in best_chr if x == 1]
        # print(sum(best_chr))
        chr_hist.append(len(chrOfOnes))
        acc_hist.append(max_acc)
        print("Accuracy: ", max_acc)
        #row = list('Accuracy for generation ' + str(generation) +  " : " + str(max_acc))
        writer.writerow([str(generation), str(max_acc)])
        parent_1 = [x for x in chr_and_acc_list[-1][0]]
        parent_2 = [x for x in chr_and_acc_list[-2][0]]
        # Încrucișarea:
        index = population.index(parent_1)
        offspring = crossover(parent_1, parent_2)
        del population[index]
        population.insert(index, offspring)
        # Mutația:
        population = [mutation(chr, p=0.05) for chr in population]


plt.figure()

plt.subplot(2, 1, 1)
plt.plot([i for i in range(num_generations)], acc_hist, color='black')
plt.xlabel("Generations")
plt.ylabel("Accuracy")
plt.show()

plt.subplot(2, 1, 2)
plt.plot([i for i in range(num_generations)], chr_hist, color='black')
plt.xlabel("Generations")
plt.ylabel("DataSet dimension")
plt.show()

new_X_train, new_y_train = [], []
for i in range(len(X_train)):

    if best_chr[i] == 1:
        new_X_train.append(X_train[i])
        new_y_train.append(y_train[i])


plt.figure()

plt.subplot(2, 1, 1)
plt.plot(X_train, y_train, 'o', color='red', label="Initial training set")

plt.show()

plt.subplot(2, 1, 2)
plt.plot(new_X_train, new_y_train, 'o', color='red', label="Training set after selection")
plt.show()
