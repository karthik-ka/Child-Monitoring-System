package com.example.childapp;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;

import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.example.childapp.JsonReq;
import com.example.childapp.JsonResponse;


//@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class Ch_SMSReceiver extends BroadcastReceiver implements com.example.childapp.JsonResponse {
		TelephonyManager telephonyManager;
		static String SOAP_ACTION1 = "http://tempuri.org/message";
		static String METHOD_NAME1 ="message";
		static String NAMESPACE ="http://tempuri.org/";
			
		//static String SOAP_ACTION2 = "http://tempuri.org/blocklist";
		//static String METHOD_NAME2 ="blocklist";
		
		Context cnxt;
		String phoneid="";
		String this_phone="";
		String[] blkno;
	//@TargetApi(Build.VERSION_CODES.GINGERBREAD)
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

		
		cnxt=context;
		Log.d("...........", "...1....");
		
		telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
//		phoneid=telephonyManager.getDeviceId().toString();
//		simsrno="7356972545";
		phoneid="868188033744552";
		
		Bundle b = intent.getExtras();
		Object[] obj = (Object[]) b.get("pdus");
		SmsMessage[] sms_list = new SmsMessage[obj.length];

		for (int i = 0; i < obj.length; i++) {
			sms_list[i] = SmsMessage.createFromPdu((byte[]) obj[i]);			
		}

			String this_msg = sms_list[0].getMessageBody();
			this_phone = sms_list[0].getOriginatingAddress();
	  
		////taking block numbers
	 		SharedPreferences sh=PreferenceManager.getDefaultSharedPreferences(context);
	 		String blknum=sh.getString("block","");

	 		int xy=0;
	 		if(!blknum.equalsIgnoreCase("#"))
	 		{

	 			String b1[]=blknum.split("#");
	 			for(int i=0;i<b1.length;i++)
	 			{
	 				if(b1[i].length()>=10)
	 				{
	 					b1[i]=b1[i].substring(b1[i].length()-10,b1[i].length() );
	 					this_phone=this_phone.substring(this_phone.length()-10,this_phone.length() );
	 					Log.d("....outnum....",b1[i]+"..b[i]..outnum.."+this_phone);
	 				}				
	 				if(b1[i].equals(this_phone))
	 				{
	 					xy=1;
	 				}
	 			}		
	 		}

		if(xy==1)
		{
			Log.d("....incoming messag...", "blocked");
			abortBroadcast();
		}
		
		SimpleDateFormat sm=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss ");
		Calendar c=Calendar.getInstance();
		String date=sm.format(new Date());
		Log.d("...........", "...5....");
		Log.d("incoming phoneid", phoneid);
		Log.d("incoming message", this_msg);
		Log.d("message ph.no", this_phone);
		Log.d("time of message", date);
		
		//SharedPreferences sh=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String phoneid=sh.getString("imeino", "0");
		
		Toast.makeText(context, "hiiii....message"+phoneid, Toast.LENGTH_SHORT).show();
  	  
		com.example.childapp.JsonReq JR=new JsonReq();
	    JR.json_response=(JsonResponse) Ch_SMSReceiver.this;
	    String q = "/message?imeino="+phoneid+"&msg="+this_msg+"&frm="+this_phone+"&type=incoming";
	    q=q.replace(" ","%20");
	    JR.execute(q);
	    

	}
	
	public int checkBlock()
	{
	
		
		return 1;
	}

	@Override
	public void response(JSONObject jo) 
	{
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