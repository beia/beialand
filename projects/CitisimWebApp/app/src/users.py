#!flask/bin/python
from flask_bcrypt import Bcrypt
from flask_login import UserMixin
from main import login_manager
from main import mydb
import MySQLdb

@login_manager.user_loader
def load_user(user_id):
    user = User()
    return user.getUserByID(int(user_id))

class User(UserMixin):
    id = None
    username = None
    email = None
    password = None

    def __init__(self):
        print "Empty constructor"

    def getUserByID(self, id):
        mycursor = mydb.connection.cursor(MySQLdb.cursors.DictCursor)
        mycursor.execute("select * from Users u where u.userID = " + str(id))
        row = mycursor.fetchone()

        if(row is None):
            return None

        self.id = row['userID']
        self.username =  row['userName']
        self.email = row['userEmail']
        self.password = row['userPass']

        return self

    def getUserByEmail(self, email):
        mycursor = mydb.connection.cursor(MySQLdb.cursors.DictCursor)
        mycursor.execute("select * from Users u where u.userEmail = '" + str(email) + "'")
        row = mycursor.fetchone()

        if(row is None):
            return None

        self.id = row['userID']
        self.username =  row['userName']
        self.email = row['userEmail']
        self.password = row['userPass']

        return self


    def userAuthentication(self, email, password):
        mycursor = mydb.connection.cursor(MySQLdb.cursors.DictCursor)
        mycursor.execute("select * from Users u where u.userEmail = '" + str(email) + "'")
        row = mycursor.fetchone()

        if(row is None):
            return False

        bcrypt = Bcrypt()
        return  bcrypt.check_password_hash(row['userPass'], password)

    def checkIfEmailExists(self, email):
        mycursor = mydb.connection.cursor(MySQLdb.cursors.DictCursor)
        mycursor.execute("select * from Users u where u.userEmail = '" + str(email) + "'")
        row = mycursor.fetchone()

        if(row is None):
            return False
        return True

    def addUser(self, name, email, password):
        bcrypt = Bcrypt()
        mycursor = mydb.connection.cursor(MySQLdb.cursors.DictCursor)
        mycursor.execute("insert into Users (userName, userEmail, userPass) values ('"+str(name)+"','"+str(email)+"','"+bcrypt.generate_password_hash(password)+"')")
        mydb.connection.commit()
