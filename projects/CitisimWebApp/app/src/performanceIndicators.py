import os
import json
import numpy
import math

from flask import request
from flask import redirect
from flask import render_template
from flask_login import current_user
from main import app
from main import mydb
import MySQLdb


def computeKPIs(kpis, scenarioID):
    mycursor = mydb.connection.cursor(MySQLdb.cursors.DictCursor)

    for bill in kpis:
        bill["savingsMU"] = bill["base"] -  bill["curr"]
        if(bill["base"] == 0):
            bill["savingsPercent"] = 0
        else:
            bill["savingsPercent"] = round((float(bill["savingsMU"])/ bill["base"]) *100, 2)

        mycursor.execute("select s.Value from Scenarios s where s.ID = %s " % scenarioID)
        bill["initialValue"] = mycursor.fetchone()["Value"]

        values = []
        values.append(bill["initialValue"] * -1);

        bill["amntRet"] = 0
        index = kpis.index(bill)
        for i in range(0,index):
            bill["amntRet"] = bill["amntRet"] + kpis[i]["savingsMU"]
            values.append(kpis[i]["savingsMU"])

        bill["amntRet"] = bill["amntRet"] + bill["savingsMU"]
        values.append(bill["savingsMU"])

        bill["rest"] = bill["initialValue"] - bill["amntRet"]
        if(bill["rest"] < 0):
            bill["rest"] = 0

        bill["roi"] = round((bill["amntRet"]/ bill["initialValue"]) * 100, 2)
        bill["irr"] = round(numpy.irr(values)*100,2)
        bill["npv"] = round(numpy.npv(0.03, values),2)

        bill["esco"] = 0
        bill["client"] = 0
        mycursor.execute("select * from Rules where ScenarioID=%s" % scenarioID)
        rows = mycursor.fetchall()
        for row in rows:
            if (bill["savingsPercent"] >= row["RuleMin"] and bill["savingsPercent"] < row["RuleMax"]):
                bill["esco"] = (bill["savingsMU"] * row["RuleEsco"])/100
                bill["client"] = (bill["savingsMU"] * row["RuleClient"])/100
                break

        for elem in bill:
            if((type(bill[elem]) != str) and (type(bill[elem]) != unicode)):
                if(math.isnan(bill[elem])):
                    bill[elem] = "NULL"

    return kpis
