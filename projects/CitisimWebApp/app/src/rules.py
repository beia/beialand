import os
import json

from flask import request
from main import app
from main import mydb
import MySQLdb

@app.route('/newRule', methods=['POST'])
def addNewRule():
    print(request.is_json)
    content = request.get_json()
    print(content)

    mycursor = mydb.connection.cursor(MySQLdb.cursors.DictCursor)
    mycursor.execute("insert into Rules (ScenarioID, RuleName, RuleMin, RuleMax, RuleEsco, RuleClient)" +
                                 " values (%s,'%s',%s,%s,%s,%s)" %
                                 (
                                    content['scenarioID'],
                                    content['name'],
                                    content['min'],
                                    content['max'],
                                    content['esco'],
                                    content['client']
                                 ))
    rowid = mycursor.lastrowid

    mycursor.execute("select * from BasicOutput b where b.ScenarioID = %s" % content['scenarioID'])
    rows = mycursor.fetchall()

    for row in rows:
        print row["SavingsPercent"]
        print float(content["min"])
        print float(content["max"])

        if(row["SavingsPercent"] >= float(content["min"]) and row["SavingsPercent"] < float(content["max"])):
            esco = row["SavingsMU"] * (float(content["esco"])/100)
            client = row["SavingsMU"] * (float(content["client"])/100)
            print "esco " + str(esco)
            print "client " + str(client)
            mycursor.execute("update BasicOutput set ESCO = %s, Client = %s where OutputID = %s" % (esco, client, row["OutputID"]))


    mydb.connection.commit()

    print(content)
    return 'rowid:' + str(rowid)


'''
@app.route('/getRules', methods=['GET'])
def getRules():
    mycursor = mydb.connection.cursor(MySQLdb.cursors.DictCursor)
    mycursor.execute("select * from Rules")
    rows = mycursor.fetchall()

    resultSet = {}
    for row in rows:
        resultSet[row['RuleID']] = {}
        resultSet[row['RuleID']]['RuleID'] = row['RuleID']
        resultSet[row['RuleID']]['ScenarioID'] = row['ScenarioID']
        resultSet[row['RuleID']]['RuleName'] = row['RuleName']
        resultSet[row['RuleID']]['RuleMin'] = row['RuleMin']
        resultSet[row['RuleID']]['RuleMax'] = row['RuleMax']
        resultSet[row['RuleID']]['RuleEsco'] = row['RuleEsco']
        resultSet[row['RuleID']]['RuleClient'] = row['RuleClient']

    return json.dumps(resultSet, indent=2, sort_keys=True)
'''


@app.route('/removeRule', methods=['POST'])
def removeRule():
    content = request.get_json()
    mycursor = mydb.connection.cursor(MySQLdb.cursors.DictCursor)

    print 'here1'
    mycursor.execute("select * from Rules where RuleID = %s " % content["RuleID"])
    rule = mycursor.fetchone()

    print 'ere2'
    mycursor.execute("delete from Rules where RuleID = " + str(content["RuleID"]))

    print 'here3'
    mycursor.execute("select * from BasicOutput where ScenarioID = %s" % rule['ScenarioID'])
    rows = mycursor.fetchall()

    print 'here4'
    print rule["RuleMin"]
    print rule["RuleMax"]
    for row in rows:
        print row["SavingsPercent"]
        if(row["SavingsPercent"] >= rule["RuleMin"] and row["SavingsPercent"] < rule["RuleMax"]):
            print 'here5'
            mycursor.execute("update BasicOutput set ESCO = 0, Client = 0 where OutputID = %s" % row["OutputID"])

    print 'here6'

    mydb.connection.commit()

    return "Rule removed"
