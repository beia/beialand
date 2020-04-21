#!flask/bin/python
from flask import Flask
from flask import render_template
from flask_cors import CORS

app = Flask(__name__)
CORS(app)


@app.route('/')
def dashboardPage():
    return render_template("dashboard.html")

@app.route('/login')
def loginPage():
    return render_template("login.html")

@app.route('/register')
def registerPage():
    return render_template("register.html")






if(__name__ == "__main__"):
    app.run(host='0.0.0.0', port=5000)
