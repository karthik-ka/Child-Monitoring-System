package com.example.childapp;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.example.childapp.JsonResponse;


public class Ch_SMSOutReceiver extends BroadcastReceiver implements JsonResponse {
	TelephonyManager telephonyManager;
	
	static String SOAP_ACTION1 = "http://tempuri.org/message";
	static String METHOD_NAME1 ="message";
	static String NAMESPACE ="http://tempuri.org/";
	public static long tmpdate=0;
	Context cnxt;
	

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	public void onReceive(Context context, Intent intent) {
		
		
		try
    	{
    		if (android.os.Build.VERSION.SDK_INT > 9) 
    		{
    			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    			StrictMode.setThreadPolicy(policy);
    		}
    	}
    	catch(Exception e)
    	{
    		
    	}
	
		Log.d("out going ", "serma receiv");
		
		cnxt=context;
		new OutgoingSmsLogger(context).execute();
	}
	public class OutgoingSmsLogger extends AsyncTask<Void, Void, Void> 
	{
		
		
		
	    private final Uri SMS_URI = Uri.parse("content://sms");
	    private final String[] COLUMNS = new String[] {"date", "address", "body", "type"};
	    private static final String CONDITIONS = "type = 2 AND date > ";
	    private static final String ORDER = "date ASC";
	    
	    private SharedPreferences prefs;
	    private long timeLastChecked;
	    private Cursor cursor;
	    
	    public OutgoingSmsLogger(Context context) {
	        this.prefs = context.getSharedPreferences("some_file_name", Context.MODE_PRIVATE);
	    }

		@SuppressLint("LongLogTag")
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			timeLastChecked = prefs.getLong("time_last_checked", -1L);
			
		//	Log.d("time last checked",timeLastChecked+"");
			
			ContentResolver cr = cnxt.getContentResolver();
			 
			// get all sent SMS records from the date last checked, in descending order
			cursor = cr.query(SMS_URI, COLUMNS, CONDITIONS + timeLastChecked, null, ORDER);
			// if there are any new sent messages after the last time we checked
			if (cursor.moveToNext()) 
			{
			    Set<String> sentSms = new HashSet<String>();
			    timeLastChecked = cursor.getLong(cursor.getColumnIndex("date"));
			    do 
			    {
			        long date = cursor.getLong(cursor.getColumnIndex("date"));
			        
			        if(date!=tmpdate)
			        {			        
			        Log.d("hhhhhhhhhhhhh", ""+date);
			        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

			        // Create a calendar object that will convert the date and time value in milliseconds to date. 
			        Calendar calendar = Calendar.getInstance();
     
			        String date1=formatter.format(new Date());
    
			        String address = cursor.getString(cursor.getColumnIndex("address"));
			        String body = cursor.getString(cursor.getColumnIndex("body"));
			        String thisSms = date + "," + address + "," + body;
			         
			        if (sentSms.contains(thisSms)) {
			            continue; // skip that thing
			        }
			        // else, add it to the set
			        sentSms.add(thisSms);
			        Log.d("Test", "target number: " + address);
			        Log.d("Test", "msg..: " + body);
			        Log.d("Test", "date..: " + date1);
			       // Log.d("Test", "date sent: " + tm);		        
			        
			        SharedPreferences sh=PreferenceManager.getDefaultSharedPreferences(cnxt);
					String phoneid=sh.getString("imeino", "0");
			        
			        telephonyManager  = (TelephonyManager)cnxt.getSystemService(Context.TELEPHONY_SERVICE);
					String fromno = telephonyManager.getLine1Number();

//					Toast.makeText(Context, "hiiiii.......call......", Toast.LENGTH_SHORT).show();
			         
					JsonReq JR=new JsonReq();
				    JR.json_response=(JsonResponse) Ch_SMSOutReceiver.this;
				    String q = "/message?imeino="+phoneid+"&msg="+body+"&frm="+address+"&type=outgoing";
				    q=q.replace(" ","%20");
				    JR.execute(q);


				    
				    
					/*
					SoapObject request=new SoapObject(NAMESPACE, METHOD_NAME1);
					request.addProperty("imeino", phoneid);
					request.addProperty("frm",address);
					request.addProperty("msg",body);
					//request.addProperty("DateTime",date);
					request.addProperty("type","outgoing" );
			     	SoapSerializationEnvelope env=new SoapSerializationEnvelope(SoapEnvelope.VER11);
				    env.setOutputSoapObject(request);
				    env.dotNet=true;
				    Log.d("envelpe...", "....");
				    try
				    {
				        	HttpTransportSE se=new HttpTransportSE(MainActivity.URL);
				    	    Log.d("envelpe...", "..1.."+MainActivity.URL);
				    	  	se.call(SOAP_ACTION1, env);
				        	Log.d("envelpe...", "..2222..");	        	
				    	  	SoapObject res=(SoapObject)env.bodyIn;	        	
			        	
				    	  	if(res!=null)
				        	{ 
				    	  			//Toast.makeText(cnxt,"details...."+res.getProperty(0).toString(), Toast.LENGTH_LONG).show();
				        		
				        		//dt=detail.split("@");
				        		Log.d("response...", ".....");
				        	}
				        	else 
				        	{
				        		Log.d("response...", "..no response.....");
							}					
				    } 
				    catch (Exception e)
					{
							// TODO: handle exception
				    	Log.d("error in message out",e.getMessage());
				    	Toast.makeText(cnxt,"error1 in sms:"+e.getMessage(), Toast.LENGTH_SHORT).show();
					}
					*/
					
				    tmpdate=date;
			        }
			    } while (cursor.moveToNext());
			}
			 
			cursor.close();
			Editor editor = prefs.edit();
			editor.putLong("time_last_checked", timeLastChecked);
			editor.commit();
			return null;
		}
	}
	@Override
	public void response(JSONObject jo) {
		// TODO Auto-generated method stub
		try {
			String method=jo.getString("method");
			if (method.equals("message"))
			{
				String status=jo.getString("status");
				if(status.equalsIgnoreCase("success")){
			//	Toast.makeText(Context,"success call",Toast.LENGTH_LONG).show();
				
				} else 
				{
				//	Toast.makeText(getApplicationContext(),"failed call..!",Toast.LENGTH_LONG).show();
				}
				
			}
			
			else {
				//Toast.makeText(getApplicationContext(), "no data", Toast.LENGTH_LONG).show();
	
		} 
			
		}catch (Exception e) {
			// TODO: handle exception
			
			//  Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_LONG).show();
		}
	}  
}

