from flask import *
from database import *
public=Blueprint('public',__name__)

@public.route('/',methods=['get','post'])
def home():
	return render_template("home.html")
@public.route('/login',methods=['post','get'])
def login():
	if 'submit' in request.form:
		username=request.form['username']
		passw=request.form['password']

		q="select * from login where username='%s' and password='%s'"%(username,passw)
		res=select(q)
		if res:
			session ['log_id']=res[0]['log_id']
			if res[0]['type']=="employee":
				q="select * from parent where log_id='%s'"%(session['log_id'])
				val=select(q)
				if val:
					session['pid']=val[0]['parent_id']
				return redirect(url_for("parent.parenthome"))

	return render_template('login.html')
@public.route('/reg',methods=['get','post'])
def reg():
	if 'submit' in request.form:
		Fname=request.form['fname']
		last=request.form['lname']
		phn=request.form['phn']
		email=request.form['email']
		children_no=request.form['children_no']
		hname=request.form['hname']
		place=request.form['place']
		uname=request.form['username']
		pas=request.form['password']

		q="insert into login values(null,'%s','%s','employee')"%(uname,pas)
		id=insert(q)

		q="insert into parent values(null,'%s','%s','%s','%s','%s','%s','%s','%s')"%(id,Fname,last,phn,email,children_no,hname,place)
		insert(q)
	return render_template("parentregistration.html")