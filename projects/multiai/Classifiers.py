import pandas as pd

import matplotlib.pyplot as plt

# from pandas.tools.plotting import scatter_matrix

from pandas.plotting import scatter_matrix

from matplotlib import cm

from sklearn.model_selection import train_test_split

from sklearn.linear_model import LogisticRegression

from sklearn.preprocessing import MinMaxScaler

from sklearn.tree import DecisionTreeClassifier

from sklearn.neighbors import KNeighborsClassifier

from sklearn.linear_model import Lasso

from sklearn.discriminant_analysis import LinearDiscriminantAnalysis

from sklearn.naive_bayes import GaussianNB

from sklearn.svm import SVC






# Read the table from .txt file



fruits = pd.read_table('fruit_data_with_colors.txt')

fruits.head()



# Prepare data for classification



feature_names = ['mass', 'width', 'height', 'color_score']

X = fruits[feature_names]

y = fruits['fruit_label']



X_train, X_test, y_train, y_test = train_test_split(X, y, random_state=0)



scaler = MinMaxScaler()

X_train = scaler.fit_transform(X_train)

X_test = scaler.transform(X_test)



# Classifier: logistic regression



logreg = LogisticRegression()

logreg.fit(X_train, y_train)

print('Accuracy of Logistic regression classifier on training set: {:.2f}'

     .format(logreg.score(X_train, y_train)))

print('Accuracy of Logistic regression classifier on test set: {:.2f}'

     .format(logreg.score(X_test, y_test)))

print(logreg.predict_proba([[0.14285714, 0.08823529, 0.69230769, 0.43243243]]))

print(logreg.predict([[0.14285714, 0.08823529, 0.69230769, 0.43243243]]))







# Classifier: Decission tree





clf = DecisionTreeClassifier().fit(X_train, y_train)

print('Accuracy of Decision Tree classifier on training set: {:.2f}'

     .format(clf.score(X_train, y_train)))

print('Accuracy of Decision Tree classifier on test set: {:.2f}'

     .format(clf.score(X_test, y_test)))



print(clf.predict_proba([[0.14285714, 0.08823529, 0.69230769, 0.43243243]]))

print(clf.predict([[0.14285714, 0.08823529, 0.69230769, 0.43243243]]))





# Classifier: K-Nearest Neighbors





knn = KNeighborsClassifier()

knn.fit(X_train, y_train)

print('Accuracy of K-NN classifier on training set: {:.2f}'

     .format(knn.score(X_train, y_train)))

print('Accuracy of K-NN classifier on test set: {:.2f}'

     .format(knn.score(X_test, y_test)))



print(logreg.predict_proba(X_train))

print(logreg.predict(X_train))



print(knn.predict_proba([[0.14285714, 0.08823529, 0.69230769, 0.43243243]]))

print(knn.predict([[0.14285714, 0.08823529, 0.69230769, 0.43243243]]))



# Classifier Linear Discriminant Analysis



lda = LinearDiscriminantAnalysis()

lda.fit(X_train, y_train)

print(X_train)



print('Accuracy of LDA classifier on training set: {:.2f}'

     .format(lda.score(X_train, y_train)))

print('Accuracy of LDA classifier on test set: {:.2f}'

     .format(lda.score(X_test, y_test)))





print(lda.predict_proba([[0.14285714, 0.08823529, 0.69230769, 0.43243243]]))

print(lda.predict([[0.14285714, 0.08823529, 0.69230769, 0.43243243]]))





# Classifier: Gaussian Naive Bayes



gnb = GaussianNB()

gnb.fit(X_train, y_train)

print('Accuracy of GNB classifier on training set: {:.2f}'

     .format(gnb.score(X_train, y_train)))

print('Accuracy of GNB classifier on test set: {:.2f}'

     .format(gnb.score(X_test, y_test)))



print(gnb.predict_proba([[0.14285714, 0.08823529, 0.69230769, 0.43243243]]))

print(gnb.predict([[0.14285714, 0.08823529, 0.69230769, 0.43243243]]))



# Classifier: Support Vector Machine



svm = SVC(probability=True)

svm.fit(X_train, y_train)

print('Accuracy of SVM classifier on training set: {:.2f}'

     .format(svm.score(X_train, y_train)))

print('Accuracy of SVM classifier on test set: {:.2f}'

     .format(svm.score(X_test, y_test)))



print(svm.predict_proba([[0.14285714, 0.08823529, 0.69230769, 0.43243243]]))

print(svm.predict([[0.14285714, 0.08823529, 0.69230769, 0.43243243]]))



#
# Load the Boston Data Set

# Create training and test split
#
# X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3, random_state=42)
#
# Create an instance of Lasso Regression implementation
#
lasso = Lasso(alpha=1.0)
#
# Fit the Lasso model
#
lasso.fit(X_train, y_train)
#
# Create the model score
#
# lasso.score(X_test, y_test), lasso.score(X_train, y_train)


print('Accuracy of Lasso classifier on training set: {:.2f}'

     .format(lasso.score(X_train, y_train)))

print('Accuracy of Lasso classifier on test set: {:.2f}'

     .format(lasso.score(X_test, y_test)))

# print('Lasso')
print(lasso.predict([[0.14285714, 0.08823529, 0.69230769, 0.43243243]]))
