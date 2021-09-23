#!flask/bin/python
import mysql.connector

from flask import Flask
from flask import redirect
from flask import send_file
from flask_login import login_user,logout_user,current_user
from flask_cors import CORS
from flask_login import LoginManager
from flask_mysqldb import MySQL
import MySQLdb
import os

app = Flask(__name__)
CORS(app)
app.config['SECRET_KEY'] = '5791628bb0b13ce0c676dfde280ba246'
login_manager = LoginManager(app)

app.config['HELP_FILE_PATH'] = '/var/www/html/CitiSIM/CitiSIM/static'
app.config['HELP_FILE'] = 'static/HELP.pdf'
# app.config['MYSQL_HOST'] = 'localhost'
# app.config['MYSQL_USER'] = 'libcitisim'
# app.config['MYSQL_PASSWORD'] = 'libcitisimDB'
# app.config['MYSQL_DB'] = 'libcitisim'

app.config['MYSQL_HOST'] = os.getenv('CITISIM_MYSQL_HOST', 'citisim_db')
app.config['MYSQL_USER'] = os.getenv('CITISIM_MYSQL_USER', 'root')
app.config['MYSQL_PASSWORD'] = os.getenv('CITISIM_MYSQL_PASSWORD', 'toor')
app.config['MYSQL_DB'] = os.getenv('CITISIM_MYSQL_DB', 'libcitisim')

mydb = MySQL(app)

def getMScenarios():
    mycursor = mydb.connection.cursor(MySQLdb.cursors.DictCursor)  

    mycursor.execute("select * from Scenarios s where s.UserID = " + str(current_user.id))
    rows = mycursor.fetchall()

    '''
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
    '''
    return rows

def getMRules():

    mycursor = mydb.connection.cursor(MySQLdb.cursors.DictCursor)
    mycursor.execute("select * from Rules r where r.ScenarioID in (select s.id from Scenarios s where s.UserID = "+str(current_user.id)+")")
    rows = mycursor.fetchall()
    
    '''
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
    '''
    return rows

@app.route('/help')
def getHelp():
    return send_file(app.config['HELP_FILE'], as_attachment = True)

@app.route('/')
def indexPage():
    if(current_user.is_authenticated == False):
        print("User not authenticated")
        return redirect("/login")

    print("Authenthicated user")
    scenarios = getMScenarios()
    rules = getMRules()
    return render_template("index.html", scenarios=scenarios, rules=rules)

from scenarios import *
from rules import *
from table import *
from compare import *
from login import *
from register import *
from sensorsData import *
from datasets import *
from performanceIndicators import *






if(__name__ == "__main__"):
    app.run(host='0.0.0.0', port=5000)
