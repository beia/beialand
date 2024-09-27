from pandas import read_csv
from pandas import datetime
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.svm import SVR
import matplotlib.pyplot as plt
from sklearn.metrics import mean_squared_error
import math



def apply_svr(X_train, y_train, X_test, y_test):
    svr_rbf = SVR(kernel='rbf', C=1e2, gamma=0.1).fit(X_train, y_train)
    y_pred = svr_rbf.predict(X_test)
    y_pred = ['%.2f' % elem for elem in y_pred]
    y_pred = [float(e) for e in y_pred]
    y_test = ['%.2f' % elem for elem in y_test]
    y_test = [float(e) for e in y_test]
    corr_coef = np.corrcoef(y_test, y_pred)[0, 1]
    return corr_coef