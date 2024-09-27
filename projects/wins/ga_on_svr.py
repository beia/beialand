from pandas import read_csv
import numpy as np
from operator import itemgetter
from sklearn.model_selection import train_test_split
import matplotlib.pyplot as plt
from svr_functions import apply_svr

#SCP_rfc3339_medii_orare.csv

data = read_csv('SCP_rfc3339_medii_orare.csv')

X, y = [], []

start = int(137782)
stop = int(144553)

population = []

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


for row in data.iterrows():
    if int(row[0]) >= start and int(row[0]) < stop:
        if(row[1][2] != 'mean'):
            X.append(int(row[0]) - start)
            #print(row[1][2])
            y.append(float(row[1][2]))

X = np.array([X]).T
y = np.array(y).ravel()
y = np.array(y)


X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state = 0)

y_test = [float(e) for e in y_test]
y_train = [float(e) for e in y_train]

for i in range(20):
    population.append([np.random.choice([1, 0], p=[0.8, 0.2]) for i in range(len(X_train))])

num_generations = 100
chr_hist, acc_hist = [], []
max_acc = 0
best_chr = []
bests_chr = []

for generation in range(num_generations):
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
plt.plot(X_train, y_train, 'o', color='red', label="Set de antrenare initial")

plt.show()

plt.subplot(2, 1, 2)
plt.plot(new_X_train, new_y_train, 'o', color='red', label="Set de antrenare dupa selectie")
plt.show()

