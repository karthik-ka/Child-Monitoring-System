from flask import Flask
from database import *
from parent import parent
from public import public
from api import api

app=Flask(__name__)

app.secret_key="secretkey"
app.register_blueprint(public)
app.register_blueprint(parent,url_prefix='/parent')
app.register_blueprint(api,url_prefix='/api')

app.run(debug=True,host="0.0.0.0",port=5044)
 
 