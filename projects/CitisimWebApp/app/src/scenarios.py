import os
import json

from flask import request
from flask_login import current_user
from main import app
from main import mydb
import MySQLdb

import performanceIndicators



@app.route('/newScenario', methods=['POST'])
def addNewScenario():
    print(request.is_json)
    content = request.get_json()

    mycursor = mydb.connection.cursor(MySQLdb.cursors.DictCursor)

    mycursor.execute("insert into Scenarios (UserID, Name, Value, ValueType, Duration, DurationType, Description, StartDate) " +
                                 " values (%s,'%s',%s,'%s',%s,'%s','%s','%s')" %
                                 (
                                    current_user.id,
                                    content['name'],
                                    content['value'],
                                    content['valueType'],
                                    content['duration'],
                                    content['durationType'],
                                    content['description'],
                                    content['startDate']
                                 ))
    rowid = mycursor.lastrowid
    mydb.connection.commit()

    scenarioID = rowid
    startDate = content['startDate']
    duration = int(content['duration'])
    if(content['durationType'] == "Years"):
        duration = duration * 12

    split = startDate.split("-")
    year = int(split[0])
    month = int(split[1])


    kpis = []

    for i in range(0,int(duration)):
        bill = {}
        bill["name"] = str(year) + "-" + '%02d' % month
        bill["base"] = 0
        bill["curr"] = 0

        kpis.append(bill)

        month = month + 1
        if(month == 13):
            month = 1
            year = year + 1

    performanceIndicators.computeKPIs(kpis, scenarioID)
    for bill in kpis:
        mycursor.execute(("insert into BasicOutput ( BillName," +
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
                                                 "ScenarioID) values ('%s',%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)") %
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
        mydb.connection.commit()

    return 'rowid:' + str(rowid)




'''
@app.route('/getScenarios', methods=['GET'])
def getScenarios():
    mycursor = mydb.connection.cursor(MySQLdb.cursors.DictCursor)
    mycursor.execute("select * from Scenarios")
    rows = mycursor.fetchall()

    resultSet = {}
    for row in rows:
        resultSet[row['ID']] = {}
        resultSet[row['ID']]['ID'] = row['ID']
        resultSet[row['ID']]['Name'] = row['Name']
        resultSet[row['ID']]['Value'] = row['Value']
        resultSet[row['ID']]['ValueType'] = row['ValueType']
        resultSet[row['ID']]['Duration'] = row['Duration']
        resultSet[row['ID']]['DurationType'] = row['DurationType']
        resultSet[row['ID']]['Description'] = row['Description']

    return json.dumps(resultSet, indent=2, sort_keys=True)
'''

@app.route('/getOneScenario', methods=['POST'])
def getOneScenario():
    content = request.get_json()
    mycursor = mydb.connection.cursor(MySQLdb.cursors.DictCursor)
    mycursor.execute("select * from Scenarios where ID = " + str(content["id"]))
    rows = mycursor.fetchall()

    resultSet = {}
    for row in rows:
        resultSet[row['ID']] = {}
        resultSet[row['ID']]['ID'] = row['ID']
        resultSet[row['ID']]['Name'] = row['Name']
        resultSet[row['ID']]['Value'] = row['Value']
        resultSet[row['ID']]['ValueType'] = row['ValueType']
        resultSet[row['ID']]['Duration'] = row['Duration']
        resultSet[row['ID']]['DurationType'] = row['DurationType']
        resultSet[row['ID']]['StartDate'] = row['StartDate']
        resultSet[row['ID']]['Description'] = row['Description']

    return json.dumps(resultSet, indent=2, sort_keys=True)




@app.route('/removeScenario', methods=['POST'])
def removeScenario():
    content = request.get_json()
    mycursor = mydb.connection.cursor(MySQLdb.cursors.DictCursor)
    mycursor.execute("delete from Scenarios where ID = " + str(content["id"]))
    mycursor.execute("delete from Rules where ScenarioID = " + str(content["id"]))
    mycursor.execute("delete from BasicOutput where ScenarioID = " + str(content["id"]))
    mydb.connection.commit()

    return "Scenario removed"








@app.route('/updateScenario', methods=['POST'])
def updateScenario():
    print(request.is_json)
    content = request.get_json()

    mycursor = mydb.connection.cursor(MySQLdb.cursors.DictCursor)
    mycursor.execute(("update Scenarios set Name = '%s', "+
                                         "Value = %s, "+
                                         "ValueType = '%s', "+
                                         "Duration = %s, "+
                                         "DurationType = '%s', "+
                                         "Description = '%s', " +
                                         "StartDate = '%s' " +
                    " where ID = %s") %
                                 (
                                    content['name'],
                                    content['value'],
                                    content['valueType'],
                                    content['duration'],
                                    content['durationType'],
                                    content['description'],
                                    content['startDate'],
                                    content['id']
                                 ))
    mydb.connection.commit()

    scenarioID = content['id']
    startDate = content['startDate']
    duration = int(content['duration'])
    if(content['durationType'] == "Years"):
        duration = duration * 12

    split = startDate.split("-")
    year = int(split[0])
    month = int(split[1])

    kpis = []
    for i in range(0,int(duration)):
        bill = {}
        bill["name"] = str(year) + "-" + '%02d' % month
        bill["base"] = 0
        bill["curr"] = 0

        mycursor.execute("select BaselineBill, CurrentBill from BasicOutput where ScenarioID = %s and BillName = '%s'" % (scenarioID, bill["name"]))
        rows = mycursor.fetchall()

        if(len(rows) == 0):
            bill["base"] = 0
            bill["curr"] = 0
        else:
            bill["base"] = rows[0]["BaselineBill"]
            bill["curr"] = rows[0]["CurrentBill"]

        kpis.append(bill)

        month = month + 1
        if(month == 13):
            month = 1
            year = year + 1

    performanceIndicators.computeKPIs(kpis, scenarioID)

    mycursor.execute("DELETE FROM BasicOutput WHERE ScenarioID = %s" % scenarioID)
    mydb.connection.commit()

    for bill in kpis:
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
        mydb.connection.commit()

    return "Succes"
