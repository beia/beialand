import os
import json

from flask import request
from flask import redirect
from flask import render_template
from flask_login import current_user
from main import app
from main import mydb
import MySQLdb


@app.route('/comparePage')
def comparePage():
    scen1 = request.args.get("scenarioID_1")
    scen2 = request.args.get("scenarioID_2")
    paramsExist = True
    if(scen1 is None or scen2 is None):
        paramsExist = False

    print("Exist = " + str(paramsExist))

    mycursor = mydb.connection.cursor(MySQLdb.cursors.DictCursor)
    mycursor.execute("select s.ID, s.Name, (select count(OutputID) from BasicOutput b where b.ScenarioID = s.ID) as len from Scenarios s where s.UserID = " + str(current_user.id))
    scenarios = mycursor.fetchall()

    if(paramsExist):
        mycursor.execute("select count(s.ID) as num from Scenarios s where s.ID in (" + str(scen1) + "," + str(scen2) + ") and s.UserID = " + str(current_user.id))
        num = mycursor.fetchone()
        print num['num']
        if(num['num'] != 2 and scen1 != scen2):
            return redirect("/comparePage")

        mycursor.execute("select s.Name from Scenarios s where s.ID = %s" % scen1)
        row = mycursor.fetchone()
        scen1_name = row["Name"]
        mycursor.execute("select * from BasicOutput b where b.ScenarioID = %s" % scen1)
        scen1_data = mycursor.fetchall()

        mycursor.execute("select s.Name from Scenarios s where s.ID = %s" % scen2)
        row = mycursor.fetchone()
        scen2_name = row["Name"]
        mycursor.execute("select * from BasicOutput b where b.ScenarioID = %s" % scen2)
        scen2_data = mycursor.fetchall()


    if(paramsExist):
        print scen1_name
        print scen2_name
        print scen1_data
        print scen2_data
        return render_template("compare.html", scenarios=scenarios,
                                               scen1_name=scen1_name,
                                               scen1_data=scen1_data,
                                               scen2_name=scen2_name,
                                               scen2_data=scen2_data)
    else:
        return render_template("compare.html", scenarios=scenarios)
