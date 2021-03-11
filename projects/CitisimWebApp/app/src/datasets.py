from flask import request
from flask import redirect
from flask import render_template
from flask_login import current_user
from main import app
from main import mydb
import flask_excel as excel
import MySQLdb
import json

excel.init_excel(app)

@app.route('/datasets')
def datasets():
    if(current_user.is_authenticated == False):
        return redirect("/login")

    userID = current_user.id

    query =    ("SELECT s.ID, s.SensorID, s.DatasetName, s.DatasetDescription "
                  "FROM Subscriptions s "
                 "WHERE s.UserID = " + str(userID) + " "
                   "AND s.Validity = 1 "
                 "ORDER BY s.ID ASC " )

    mycursor = mydb.connection.cursor(MySQLdb.cursors.DictCursor)
    mycursor.execute(query)
    result = mycursor.fetchall()

    return render_template("datasets.html", datasets=result)

@app.route('/datasets/newDataset', methods=["POST"])
def addNewDataset():
    if(current_user.is_authenticated == False):
        return redirect("/login")

    if(request.is_json == False):
        return "Wrong data format!"

    userID = current_user.id
    content = request.get_json()
    query = ("INSERT INTO Subscriptions (ID, UserID, SensorID, DatasetName, DatasetDescription, Validity, StartDate, EndDate) " +
             "VALUES (NULL, %s, '%s', '%s', '%s', 1, CURRENT_TIMESTAMP, NULL)" % ( userID, content["SensorID"], content["DatasetName"],content["DatasetDescription"] ))

    mycursor = mydb.connection.cursor(MySQLdb.cursors.DictCursor)
    mycursor.execute(query)
    mydb.connection.commit()

    return 'rowid:' + str(mycursor.lastrowid)


@app.route('/datasets/getDataset')
def getDataset():
    if(current_user.is_authenticated == False):
        return redirect("/login")

    datasetID = request.args.get("datasetID")

    query = ("SELECT s.SensorID, s.DatasetName, s.DatasetDescription from Subscriptions s where s.ID = %s" % datasetID)

    mycursor = mydb.connection.cursor(MySQLdb.cursors.DictCursor)
    mycursor.execute(query)
    result = mycursor.fetchone()

    return json.dumps(result)


@app.route('/datasets/updatetDataset',  methods=["POST"])
def updateDataset():
    if(current_user.is_authenticated == False):
        return redirect("/login")
    if(request.is_json == False):
        return "Wrong data format!"

    content = request.get_json()
    query = ("UPDATE Subscriptions s SET s.SensorID='%s', s.DatasetName='%s', s.DatasetDescription='%s' WHERE s.ID = %s" % ( content["SensorID"], content["DatasetName"],content["DatasetDescription"],content["DatasetID"]))

    mycursor = mydb.connection.cursor(MySQLdb.cursors.DictCursor)
    mycursor.execute(query)
    mydb.connection.commit()

    return "Succes"

@app.route('/datasets/removeDataset',  methods=["POST"])
def removeDataset():
    if(current_user.is_authenticated == False):
        return redirect("/login")
    if(request.is_json == False):
        return "Wrong data format!"

    content = request.get_json()
    query = ("DELETE FROM Subscriptions WHERE ID = %s" % content["datasetID"])
    mycursor = mydb.connection.cursor(MySQLdb.cursors.DictCursor)
    mycursor.execute(query)
    mydb.connection.commit()

    return "DatasetRemoved"

@app.route('/datasets/export')
def exportDataset():
    datasetID = request.args.get("datasetID")

    query = ("SELECT * FROM EnergySensorsData e WHERE e.SubscriptionID = " + datasetID)
    mycursor = mydb.connection.cursor(MySQLdb.cursors.DictCursor)
    mycursor.execute(query)
    result = mycursor.fetchall()

    csv = []
    csv.append(["Value","Timestamp","SensorID"])

    for row in result:
        rowValues = [row["Value"], row["Timestamp"], row["SensorID"]]
        csv.append(rowValues)

    return excel.make_response_from_array(csv, "csv", file_name="export_data")


@app.route('/datasets/import',  methods=["POST"])
def importDataset():
    datasetID = request.form.get("datasetID")
    data = request.get_array(field_name='file')
    data.pop(0)


    if(len(data) > 0):
        delQuery = ("DELETE FROM EnergySensorsData  WHERE SubscriptionID = " + str(datasetID))
        mycursor = mydb.connection.cursor(MySQLdb.cursors.DictCursor)
        mycursor.execute(delQuery)

        query = ("INSERT INTO EnergySensorsData (SubscriptionID, SensorID, Value, Timestamp) VALUES ")
        for row in data:
            query = query + "(" + str(datasetID) + ","
            query = query + "'" + str(row[2]) + "', "
            query = query + str(row[0]) + ","
            query = query + "'" + str(row[1]) + "') ,"

        query = query[0:-1]
        mycursor.execute(query)
        mydb.connection.commit()

    return redirect("/datasets")
