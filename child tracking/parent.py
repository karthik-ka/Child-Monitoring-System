from flask import *
from database import *
import urllib.request

parent=Blueprint('parent',__name__)
@parent.route('/parenthome',methods=['post','get'])
def parenthome():
	return render_template('parenthome.html')
@parent.route('/childaccount',methods=['get','post'])
def childaccount():
	data={}
	log=session['log_id']
	if 'action' in request.args:
		action=request.args['action']
		id=request.args['id']
		print(id)
	else:
		action=None
	if action=="delete":
		q="delete from child where child_id='%s'"%(id)
		delete(q)
	if action=="update":
		q="select * from child where child_id='%s'"%(id)
		res=select(q)
		data['updatepart']=res
	if 'update' in request.form:
		Fname=request.form['fname']
		last=request.form['lname']
		phn=request.form['phn']
		imei=request.form['imei_no']
		email=request.form['email']
		q="update child set first_name='%s',last_name='%s',phn='%s',imei_no='%s',email='%s' where child_id='%s'"%(Fname,last,phn,imei,email,id)
		update(q)
		return redirect(url_for('parent.childaccount'))				
	if 'submit' in request.form:
		Fname=request.form['fname']
		last=request.form['lname']
		phn=request.form['phn']
		imei=request.form['imei_no']
		email=request.form['email']
		
		q="insert into child values(null,(SELECT parent_id FROM parent WHERE log_id='%s'),'%s','%s','%s','%s','%s')" %(log,Fname,last,phn,imei,email)
		insert(q)
	q="select child.child_id,child.first_name as fname ,child.last_name as lname,child.phn,child.imei_no,child.email,concat(parent.first_name,parent.last_name)as NAME from child inner join parent using(parent_id) where log_id='%s'"%(log)
	res=select(q)
	data['view']=res

		
	return render_template("childregistration.html",data=data)

@parent.route('/viewcalllist',methods=['post','get'])
def viewcalllist():
	data={}
	log=session['pid']
	if 'submit' in request.form:
		id=request.form['childs']
		print(id)
		q="select *,concat(first_name,' ',last_name)as name from call_list inner join child using(child_id) where call_list.child_id='%s'" %(id)
		print(q)
		res=select(q)
		data['call']=res
		print(res)
	q="select *,concat(first_name,' ',last_name)as name from child where parent_id='%s'" %(log)
	res=select(q) 
	data['childdetails']=res

	return render_template('viewcalllist.html',data=data)

@parent.route('/viewmsg',methods=['post','get'])
def viewmsg():
	data={}
	log=session['pid']
	if 'submit' in request.form:
		id=request.form['childs']
		q="select *,concat(first_name,' ',last_name)as name from msg inner join child using(child_id) where msg.child_id='%s'" %(id)
		res=select(q)
		data['msg']=res
	q="select *,concat(first_name,' ',last_name)as name from child where parent_id='%s'" %(log)
	print(q)
	res=select(q) 
	data['childdetails']=res

	return render_template('viewmsg.html',data=data)

@parent.route('/viewgallery',methods=['post','get'])
def viewgallery():
	data={}
	log=session['pid']
	if 'submit' in request.form:

		id=request.form['childs']
		q="select *,concat(first_name,' ',last_name)as name from gallery inner join child using(child_id) where gallery.child_id='%s'" %(id)
		res=select(q)
		data['img']=res
	if 'id' in request.args:
		# action=request.args['action']
		id = request.args['id']
		ids=request.args['ids']
		q="select * from filetodowload where  child_id='%s' and img_id='%s'"%(id,ids)
		res=select(q)
		print(res)
		if res:
			if res[0]['status']=='request':
				print("Haiiiiiissss")
				flash("Not yet Dowloaded")
				return redirect(url_for('parent.viewgallery'))
			else:
				print("Haiiii")
				# url = res[0]['path']
				# filename = 'file.txt'
				# urllib.request.urlretrieve(url, filename)
				return send_file("D:/riss/python/anzar/child/child tracking josy/"+res[0]['path'], as_attachment=True)

		else:
			q="INSERT INTO `filetodowload` VALUES(NULL,'%s','%s','request','pending')"%(id,ids)
			insert(q)
			flash("Requested")

	# if action=='delete':
	# 	q="delete from gallery where child_id='%s' "%(id)
	# 	delete(q)
		return redirect(url_for('parent.viewgallery'))

	q="select *,concat(first_name,' ',last_name)as name from child where parent_id='%s'" %(log)
	res=select(q) 
	data['childdetails']=res

	return render_template('viewgallery.html',data=data)
	
@parent.route('/viewapp',methods=['post','get'])
def viewapp():
	data={}
	log=session['pid']
	if 'submit' in request.form:
		id=request.form['childs']
		q="select *,concat(first_name,' ',last_name)as name from app inner join child using(child_id) where app.child_id='%s'" %(id)
		res=select(q)
		data['app']=res
	q="select *,concat(first_name,' ',last_name)as name from child where parent_id='%s'" %(log)
	res=select(q) 
	data['childdetails']=res

	return render_template('viewapp.html',data=data)


	
@parent.route('/viewlocation',methods=['post','get'])
def viewlocation():
	data={}
	log=session['pid']
	if 'submit' in request.form:
		id=request.form['childs']
		q="select *,concat(first_name,' ',last_name)as name from location inner join child using(child_id) where location.child_id='%s'" %(id)
		res=select(q)
		data['loc']=res
	q="select *,concat(first_name,' ',last_name)as name from child where parent_id='%s'" %(log)
	res=select(q) 
	data['childdetails']=res

	return render_template('viewlocation.html',data=data)




