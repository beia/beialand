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
import performanceIndicators


@app.route('/biPage')
def biPage():
    scenarioID = request.args.get("scenarioID")
    scenarioName = ""

    mycursor = mydb.connection.cursor(MySQLdb.cursors.DictCursor)

    mycursor.execute("select s.Name, s.UserID from Scenarios s where s.ID = %s" % scenarioID)
    row = mycursor.fetchone()

    if(row['UserID'] != current_user.id):
        return redirect("/")
    scenarioName = row['Name']

    mycursor.execute("select * from BasicOutput b where b.ScenarioID = %s" % scenarioID)
    rows = mycursor.fetchall()


    return render_template("biPage.html",
                           scenarioID=scenarioID,
                           scenarioName=scenarioName,
                           rows = rows)




@app.route('/newBill', methods=['POST'])
def addNewBill():
    mycursor = mydb.connection.cursor(MySQLdb.cursors.DictCursor)


    content = request.get_json()
    scenarioID = content["scenarioID"]
    name = content["BillName"]
    base = float(content["BaselineBill"])
    curr = float(content["CurrentBill"])

    bill = computeValues(mycursor, scenarioID, name, base, curr)

    mycursor.execute("insert into BasicOutput ( BillName," +
                                             "BaselineBill,"+
                                             "CurrentBill,"+
                                             "SavingsMU,"+
                                             "SavingsPercent,"+
                                             "AmountReturned,"+
                                             "AmountYetToBeReturned,"+
                                             "ROI,"+
                                             "IRR,"+
                                             "NPV,"+
                                             "ESCO,"+
                                             "Client,"+
                                             "ScenarioID) values ('%s',%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)" %
                            (bill["name"],
                             bill["base"],
                             bill["curr"],
                             bill["savingsMU"],
                             bill["savingsPercent"],
                             bill["amntRet"],
                             bill["rest"],
                             bill["roi"],
                             bill["irr"],
                             bill["npv"],
                             bill["esco"],
                             bill["client"],
                             scenarioID))
    rowid = mycursor.lastrowid
    mydb.connection.commit()

    bill["id"] = rowid
    return json.dumps(bill, indent=2, sort_keys=True)


def computeValues(mycursor, scenarioID, name, base, curr, billID = None):
    bill = {}
    bill["name"] = name
    bill["base"] = base
    bill["curr"] = curr

    bill["savingsMU"] = bill["base"] -  bill["curr"]
    if(bill["base"] == 0):
        bill["savingsPercent"] = 0
    else:
        bill["savingsPercent"] = round((float(bill["savingsMU"])/ bill["base"]) *100, 2)

    mycursor.execute("select s.Value from Scenarios s where s.ID = %s " % scenarioID)
    bill["initialValue"] = mycursor.fetchone()["Value"]

    values = []
    values.append(bill["initialValue"] * -1);
    if(billID == None):
        mycursor.execute("select b.SavingsMU from BasicOutput b where b.scenarioID = %s" % scenarioID)
    else:
        mycursor.execute("select b.SavingsMU from BasicOutput b where b.scenarioID = %s and b.OutputID < %s" % (scenarioID, billID))
    rows = mycursor.fetchall()

    bill["amntRet"] = 0
    for row in rows:
        bill["amntRet"] = bill["amntRet"] + row["SavingsMU"]
        values.append(row["SavingsMU"])

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

    return bill

@app.route('/removeBill', methods=['POST'])
def removeBill():
    mycursor = mydb.connection.cursor(MySQLdb.cursors.DictCursor)
    mycursor.execute("delete from BasicOutput where OutputID = " + str(content["BillID"]))
    mydb.connection.commit()

    return "Bill removed"


@app.route('/changeBill', methods=['POST'])
def changeBill():
    mycursor = mydb.connection.cursor(MySQLdb.cursors.DictCursor)

    content = request.get_json()
    billID = content["billID"]
    scenarioID = content["scenarioID"]
    name = content["name"]
    base = float(content["base"])
    curr = float(content["curr"])

    mycursor.execute("SELECT OutputID, BillName, BaselineBill, CurrentBill FROM BasicOutput WHERE ScenarioID = %s ORDER BY BillName ASC" % scenarioID)
    rows = mycursor.fetchall()


    kpis = []
    for row in rows:
        bill = {}

        bill["name"] = row["BillName"]
        bill["id"] = row["OutputID"]
        if(row["BillName"] == name):
            bill["base"] = base
            bill["curr"] = curr
        else:
            bill["base"] = row["BaselineBill"]
            bill["curr"] = row["CurrentBill"]
        kpis.append(bill)


    performanceIndicators.computeKPIs(kpis, scenarioID)


    for bill in kpis:
        querystr = (("update BasicOutput set BillName = '%s',"+
                                              "BaselineBill = %s,"+
                                              "CurrentBill = %s,"+
                                              "SavingsMU = %s,"+
                                              "SavingsPercent = %s,"+
                                              "AmountReturned = %s,"+
                                              "AmountYetToBeReturned = %s,"+
                                              "ROI = %s,"+
                                              "IRR = %s,"+
                                              "NPV = %s,"+
                                              "ESCO = %s,"+
                                              "Client = %s "+
                                "where OutputID = %s") %
                                (bill["name"],
                                 bill["base"],
                                 bill["curr"],
                                 bill["savingsMU"],
                                 bill["savingsPercent"],
                                 bill["amntRet"],
                                 bill["rest"],
                                 bill["roi"],
                                 bill["irr"],
                                 bill["npv"],
                                 bill["esco"],
                                 bill["client"],
                                 bill["id"]))
        mycursor.execute(querystr)
        mydb.connection.commit()
        # if(math.isnan(bill["irr"])):
        #     bill["irr"] = "None"

    return json.dumps(kpis)


@app.route('/getColumns', methods=['POST'])
def getColumns():
    content = request.get_json()
    cols = content["cols"]
    scenarioID = content["scenarioID"]

    mycursor = mydb.connection.cursor(MySQLdb.cursors.DictCursor)

    mycursor.execute("select OutputID,"+cols+" from BasicOutput where ScenarioID = " + str(scenarioID))
    rows = mycursor.fetchall()


    arr = cols.split(",")
    resultSet = {}
    for row in rows:
        resultSet[row["OutputID"]] = {}
        for item in arr:
            resultSet[row["OutputID"]][str(item)] = row[str(item)]


    return json.dumps(resultSet, indent=2, sort_keys=True)
