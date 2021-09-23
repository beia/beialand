#!flask/bin/python
from flask import request
from flask import flash
from flask import redirect
from flask import render_template
from flask_login import login_user,logout_user,current_user
from users import User
from main import app
from main import mydb
import re
import MySQLdb

@app.route('/register', methods=['GET','POST'])
def registerPage():
    if(current_user.is_authenticated):
        return redirect("/")

    name = request.form.get('name')
    email = request.form.get('email')
    password = request.form.get('password')


    if(email is not None):
        if(not re.match(r'[^@]+@[^@]+\.[^@]+',email)):
            flash("Email is not in the right format!")
            return render_template("register.html")
        user = User()
        if(user.checkIfEmailExists(email)):
            flash("Email already used! Try another one!")
            return render_template("register.html")
        else:
            user.addUser(name, email, password )
            login_user(user.getUserByEmail(email))
            return redirect("/")

    return render_template("register.html")
