from flask import request
from flask import redirect
from flask import render_template
from flask_login import current_user
from main import app
from main import mydb
from statsmodels.tsa.statespace.sarimax import SARIMAX
import performanceIndicators

import MySQLdb
import json


@app.route('/sensorsData')
def sensorsData():
    if(current_user.is_authenticated == False):
        return redirect("/login")

    userID = current_user.id

    query =    ("SELECT s.ID, s.DatasetName "
                  "FROM Subscriptions s "
                 "WHERE s.UserID = " + str(userID) + " "
                   "AND s.Validity = 1 "
                 "ORDER BY s.ID ASC " )

    mycursor = mydb.connection.cursor(MySQLdb.cursors.DictCursor)
    mycursor.execute(query)
    result = mycursor.fetchall()

    mycursor.execute("SELECT ID, Name FROM Scenarios WHERE UserID = %s" % userID)
    scenarios = mycursor.fetchall()

    return render_template("sensorsData.html", datasets=result, scenarios=scenarios)


@app.route('/sensorsData/getOneDay')
def getOneDay():
    if(current_user.is_authenticated == False):
        return redirect("/login")

    subscriptionID = request.args.get("subscriptionID")
    date = request.args.get("date")
    newList = []

    query =    ("SELECT * FROM EnergySensorsData e " +
                 "WHERE e.timestamp < '" + date + " 00:00:00' " +
                   "and e.subscriptionID = '" + subscriptionID + "' " +
                 "order by e.ID desc " +
                 "limit 1")

    mycursor = mydb.connection.cursor(MySQLdb.cursors.DictCursor)
    mycursor.execute(query)
    result = mycursor.fetchall()

    if(len(result) == 0):
        elem = {}
        #elem["SensorID"] = "0"
        #elem["Timestamp"] = datetime.datetime.now()
        elem["Value"] = 0
        elem["delta"] = 0
        elem["label"] = "0"
        newList.append(elem)
    else:
        elem = {}
        elem["Value"] = result[0]["Value"]
        elem["delta"] = 0
        elem["label"] = "0"
        newList.append(elem)

    query = ( "SELECT * FROM EnergySensorsData e " +
               "WHERE e.timestamp like '" + date + " %' " +
                 "and e.subscriptionID = '" + subscriptionID + "' " +
               "order by e.ID asc" )

    mycursor.execute(query)
    result = mycursor.fetchall()

    for elem in result:
        index = result.index(elem)
        if (index < len(result) -1 ):
            if(elem['Timestamp'].hour != result[index+1]['Timestamp'].hour):
                newList.append(elem)
        else:
            newList.append(elem)

    for elem in newList:
        if(newList.index(elem) == 0):
            continue
        ts = elem['Timestamp']
        elem.pop('SensorID')
        elem.pop('Timestamp')


        elem['label'] = '%02d' % ts.hour

        index = newList.index(elem)
        if(index > 0):
            elem['delta'] = elem['Value'] - newList[index - 1]['Value']
        else:
            elem['delta'] = 0

    return json.dumps(newList)


@app.route('/sensorsData/getOneMonth')
def getOneMonth():
    if(current_user.is_authenticated == False):
        return redirect("/login")

    subscriptionID = request.args.get("subscriptionID")
    date = request.args.get("date")
    newList = []

    query =    ("SELECT * FROM EnergySensorsData e " +
                 "WHERE e.timestamp < '" + date + "01 00:00:00' " +
                   "and e.subscriptionID = '" + subscriptionID + "' " +
                 "order by e.ID desc " +
                 "limit 1 ")

    mycursor = mydb.connection.cursor(MySQLdb.cursors.DictCursor)
    mycursor.execute(query)
    result = mycursor.fetchall()

    if(len(result) == 0):
        elem = {}
        #elem["SensorID"] = "0"
        #elem["Timestamp"] = datetime.datetime.now()
        elem["Value"] = 0
        elem["delta"] = 0
        elem["label"] = "0"
        newList.append(elem)
    else:
        elem = {}
        elem["Value"] = result[0]["Value"]
        elem["delta"] = 0
        elem["label"] = "0"
        newList.append(elem)


    query = ( "SELECT * FROM EnergySensorsData e " +
               "WHERE e.timestamp BETWEEN '" + date + "01 00:00:00' AND '"  + date + "31 23:59:59' " +
                 "and e.subscriptionID = '" + subscriptionID + "' " +
               "order by e.ID asc")

    mycursor.execute(query)
    result = mycursor.fetchall()

    for elem in result:
        index = result.index(elem)
        if (index < len(result) -1 ):
            if(elem['Timestamp'].day != result[index+1]['Timestamp'].day):
                newList.append(elem)
        else:
            newList.append(elem)

    for elem in newList:
        if(newList.index(elem) == 0):
            continue
        ts = elem['Timestamp']
        elem.pop('SensorID')
        elem.pop('Timestamp')


        elem['label'] = '%02d' % ts.day

        index = newList.index(elem)
        if(index > 0):
            elem['delta'] = elem['Value'] - newList[index - 1]['Value']
        else:
            elem['delta'] = 0

    return json.dumps(newList)



@app.route('/sensorsData/getMultipleMonths')
def getMultipleMonths():
    if(current_user.is_authenticated == False):
        return redirect("/login")

    subscriptionID = request.args.get("subscriptionID")
    startDate = request.args.get("startDate")
    endDate = request.args.get("endDate")
    newList = []

    query =    ("SELECT * FROM EnergySensorsData e " +
                 "WHERE e.timestamp < '" + startDate + "01 00:00:00' " +
                   "and e.subscriptionID = '" + subscriptionID + "' " +
                 "order by e.ID desc " +
                 "limit 1 " )

    mycursor = mydb.connection.cursor(MySQLdb.cursors.DictCursor)
    mycursor.execute(query)
    result = mycursor.fetchall()

    if(len(result) == 0):
        elem = {}
        #elem["SensorID"] = "0"
        #elem["Timestamp"] = datetime.datetime.now()
        elem["Value"] = 0
        elem["delta"] = 0
        elem["label"] = "0"
        newList.append(elem)
    else:
        elem = {}
        elem["Value"] = result[0]["Value"]
        elem["delta"] = 0
        elem["label"] = "0"
        newList.append(elem)

    query =    ("SELECT * FROM EnergySensorsData e " +
                 "WHERE e.timestamp BETWEEN '" + startDate + "01 00:00:00' AND '"  + endDate + "31 23:59:59' " +
                   "and e.subscriptionID = '" + subscriptionID + "' " +
                 "order by e.ID asc")

    mycursor.execute(query)
    result = mycursor.fetchall()

    for elem in result:
        index = result.index(elem)
        if (index < len(result) -1 ):
            if((elem['Timestamp'].month != result[index+1]['Timestamp'].month) or (elem['Timestamp'].year != result[index+1]['Timestamp'].year)):
                newList.append(elem)
        else:
            newList.append(elem)

    for elem in newList:
        if(newList.index(elem) == 0):
            continue
        ts = elem['Timestamp']
        elem.pop('SensorID')
        elem.pop('Timestamp')


        elem['label'] = str(ts.year) + "-" + '%02d' % ts.month

        index = newList.index(elem)
        if(index > 0):
            elem['delta'] = elem['Value'] - newList[index - 1]['Value']
        else:
            elem['delta'] = 0

    return json.dumps(newList)


@app.route('/sensorsData/forecast', methods=['POST'])
def forecastFutureConsumptionValues():
    if(not request.is_json):
        return "Wrong Format Data"

    content = request.get_json()

    predNum = content["predNum"]
    dataset = content["dataset"]
    del dataset[0]

    if(len(dataset) < 24):
        print "Not enough data"

    lastValue = dataset[-1]["Value"]
    lastMonth = dataset[-1]["label"]
    lastMonthSplit = lastMonth.split("-")
    year = int(lastMonthSplit[0])
    month = int(lastMonthSplit[1])

    data = []
    for element in dataset:
        data.append(element["Value"])


    # fit model
    model = SARIMAX(data, order=(1, 1, 1), seasonal_order=(0, 1, 0, 12))
    model_fit = model.fit(disp=False)
    # make prediction
    predictedValues = model_fit.forecast(predNum)

    predictions = []
    for value in predictedValues:
        elem = {}
        elem["Value"] = value;

        month = month + 1
        if(month == 13):
            month = 1
            year = year + 1
        elem["label"] = str(year) +"-" + '%02d' % month

        predictions.append(elem)


    for elem in predictions:
        index = predictions.index(elem)
        if(index == 0):
            elem["delta"] = elem["Value"] - lastValue
        else:
            elem['delta'] = elem['Value'] - predictions[index - 1]['Value']

    return json.dumps(predictions)


@app.route('/sensorsData/getScenarioDetails')
def getScenarioDetails():
    if(current_user.is_authenticated == False):
        return redirect("/login")

    scenarioID = request.args.get("scenarioID")

    mycursor = mydb.connection.cursor(MySQLdb.cursors.DictCursor)

    mycursor.execute("SELECT  Name, StartDate, Duration, DurationType FROM Scenarios WHERE ID = %s" % scenarioID)
    result = mycursor.fetchone()

    duration = result["Duration"]
    if(result["DurationType"] == "Years"):
        duration = duration * 12

    startDate = result["StartDate"]
    split = startDate.split("-")
    year = int(split[0])
    month = int(split[1])

    month = month + duration - 1
    year = year + (month/12)
    month = month % 12

    endDate = str(year) +"-" + '%02d' % month

    scenario = {}
    scenario["Name"] = result["Name"]
    scenario["StartDate"] = result["StartDate"]
    scenario["EndDate"] = endDate

    return json.dumps(scenario)

@app.route('/sensorsData/exportBaselineBills', methods=['POST'])
def exportBaselineBills():
    if(current_user.is_authenticated == False):
        return redirect("/login")

    if(request.is_json == False):
        return "Wrong data format!"

    content = request.get_json()
    scenarioID = content["scenarioID"]
    bills = content["bils"]

    mycursor = mydb.connection.cursor(MySQLdb.cursors.DictCursor)

    mycursor.execute("SELECT  BillName, BaselineBill, CurrentBill FROM BasicOutput WHERE scenarioID = %s order by BillName asc" % scenarioID)
    result = mycursor.fetchall()

    kpis = []
    for row in result:
        bill = {}
        bill["name"] = row["BillName"]
        bill["base"] = row["BaselineBill"]
        bill["curr"] = row["CurrentBill"]

        for elem in bills:
            if(elem["month"] == bill["name"] ):
                bill["base"] = elem["bill"]
                break

        kpis.append(bill)


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
                                                 "ScenarioID) values ('%s',%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)",
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


    return "succes"
