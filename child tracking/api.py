from flask import *
from database import *
import demjson
import uuid
api=Blueprint('api',__name__)



@api.route('/call',methods=['get','post'])
def call():
	data={}
	imeino = request.args['imeino']
	phno = request.args['phno']
	type = request.args['type']
	duration = request.args['duration']
	q="insert into call_list values(null,(select child_id from child where imei_no='%s'),'%s','%s','%s',curdate(),curtime())" % (imeino,phno,type,duration)
	id=insert(q)
	print(q)
	if(id>0):
		data['status'] = 'success'
		data['method']  ='call'
	else:
		data['status']	= 'failed'
		data['method']  ='call'
	print(data)
	return str(data)

@api.route('/message',methods=['get','post'])
def message():
	data={}
	imeino = request.args['imeino']
	msg = request.args['msg']
	type = request.args['type']
	frm = request.args['frm']
	q="insert into msg values(null,(select child_id from child where imei_no='%s'),'%s','%s',curdate(),curtime(),'%s')" % (imeino,frm,type,msg)
	id=insert(q)
	if(id>0):
		data['status'] = 'success'
		data['method']  ='message'
	else:
		data['status']	= 'failed'
		data['method']  ='message'
	print(data)
	return str(data)

@api.route('/location',methods=['get','post'])
def location():
	data={}
	lati = request.args['lati']
	logi = request.args['logi']
	imeino = request.args['phoneid']
	q="select * from location where child_id=(select child_id from child where imei_no='%s')"%(imeino)
	res=select(q)
	if res:
		q="update location set lattitude='%s',longitude='%s' where child_id=(select child_id from child where imei_no='%s')" % (lati,logi,imeino)
		c=update(q)
		if(c>0):

			data['status'] = 'success'
			data['method']  ='location'
		else:
			data['status']	= 'failed'
			data['method']  ='location'
	else:
		q="insert into location values(null,(select child_id from child where imei_no='%s'),'%s','%s',curdate(),curtime())" % (imeino,logi,lati)
		id=insert(q)
		if(id>0):
			data['status'] = 'success'
			data['method']  ='location'
		else:
			data['status']	= 'failed'
			data['method']  ='location'
		
	return str(data)

@api.route('/applist')
def applist():
	data={}
	imeino = request.args['imeino']
	apps = request.args['apps']

	app_lists = apps.split('$')

	q="SELECT child_id FROM child WHERE imei_no='%s'" %(imeino)
	res1=select(q)
	for app in app_lists:
		try:
			q="SELECT * FROM `app` WHERE child_id='%s' AND `app_dtls`='%s'"%(res1[0]['child_id'],app)
			res=select(q)
			if res:
				pass
			else:
				q="INSERT INTO app (child_id, app_dtls) VALUES ('%s', '%s');" % (res1[0]['child_id'], app)
				print(q)
				id=insert(q)
				data['status'] = 'success'

		except Exception:
			pass
	return str(data)

@api.route('/files')
def files():
	data={}
	imeino=request.args['imeino']
	fname=request.args['fnames']
	app_lists = fname.split('$')

	q="SELECT child_id FROM child WHERE imei_no='%s'" %(imeino)
	res1=select(q)
	for fname in app_lists:
		try:
			q="SELECT * FROM `gallery` WHERE child_id='%s' AND `img`='%s'"%(res1[0]['child_id'],fname)
			print(q)
			res=select(q)
			if res:
				pass
			else:
				q="INSERT INTO `gallery` VALUES(NULL,'%s','%s')"%(res1[0]['child_id'],fname)
				print(q)
				res=insert(q)
				data['status']='success'
		except Exception:
			pass	
	return str(data)

@api.route('/requests')
def requests():
	data={}
	
	q="select * from filetodowload inner join gallery using (img_id) where status='request'"
	res=select(q)
	if res:
		data['status'] = 'success'
		data['data']  =res
	else:
		data['status']	= 'failed'
	data['method']  ='requests'
	return str(data)

@api.route('/dowloadimage',methods=['get','post'])
def dowloadimage():
	data={}
	
	image=request.files['image']
	path='static/uploads/'+str(uuid.uuid4())+ ".jpg"
	image.save(path)
	file_id=request.form['file_id']
	q="UPDATE `filetodowload` SET `status`='downloaded' , `path`='%s' WHERE `file_id`='%s'"%(path,file_id)
	print(q)
	update(q)
	data['status']="success"
	data['method']  ='requests'


	return str(data)
