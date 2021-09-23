#!flask/bin/python
from flask import request
from flask import flash
from flask import redirect
from flask import render_template
from flask_login import login_user,logout_user,current_user
from main import app
from main import mydb
from users import User
import MySQLdb

@app.route('/login', methods=['GET','POST'])
def loginPage():
    if(current_user.is_authenticated):
        return redirect("/")

    email = request.form.get('email')
    password = request.form.get('password')
    if(email is not None):
        user = User()
        if(user.userAuthentication(email, password)):
            login_user(user.getUserByEmail(email))
            return redirect("/")
        else:
            flash("Wrong email or password!")
    return render_template("login.html")

@app.route('/logout', methods=['GET','POST'])
def logOut():
    if(current_user.is_authenticated):
        logout_user()
    return redirect("/login")
