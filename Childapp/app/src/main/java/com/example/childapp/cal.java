
package com.example.childapp;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

//import com.android.internal.telephony.ITelephony;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.IBinder;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class cal extends Service implements JsonResponse
{
	
	String dt="",tm="";
	long diffinmin,diffinhr;
	TelephonyManager telephonyManager;
	TelephonyManager telman;

//	String SOAP_ACTION1 = "http://tempuri.org/call";
//	String NAMESPACE = "http://tempuri.org/";
//	String METHOD_NAME1 ="call";
//	String url="";
	SharedPreferences sh;
	
	public static int flg=0,blkdur=0;	
	String phnop="";
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	public void onCreate() 
	 {		
		// TODO Auto-generated method stub
			super.onCreate();
		
//			SharedPreferences sh=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//			url = sh.getString("url", "");
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
		

			SimpleDateFormat tet=new SimpleDateFormat("hh:mm:ss");
			tm=tet.format(new Date());
			
			telman=(TelephonyManager)getApplicationContext().getSystemService(TELEPHONY_SERVICE);
			telman.listen(phlist,PhoneStateListener.LISTEN_CALL_STATE);
			Log.d("....old...", ".....00");
	}
   public PhoneStateListener phlist=new PhoneStateListener()
   {
	   public void onCallStateChanged(int state, String inNum) 
	   {
		
		  switch (state) 
		  {
		     case TelephonyManager.CALL_STATE_IDLE:
		    	 Toast.makeText(getApplicationContext(), "in idle", Toast.LENGTH_SHORT).show();
		    	 		SimpleDateFormat dd=new SimpleDateFormat("dd/MM/yyyy");
						SimpleDateFormat tt=new SimpleDateFormat("hh:mm:ss");
						
						String d=dd.format(new Date());
						String t=tt.format(new Date());
						String duration="";
						long tmdiff=0;
						
						Log.d("....old...", ".....3");
						try 
						{
								Date dt1=tt.parse(t);
								Date dt2=tt.parse(tm);
							
								tmdiff=dt1.getTime()-dt2.getTime();
							
								tmdiff=TimeUnit.MILLISECONDS.toSeconds(tmdiff);
								diffinmin=tmdiff/(60);
								diffinhr=diffinmin/(60);
								tmdiff-=(diffinmin*60);
			
								duration=diffinhr+":"+ diffinmin + ":"+ tmdiff;
						
						} 
						catch (Exception e) 
						{
							// TODO Auto-generated catch block
							Toast.makeText(getApplicationContext(), "error1 in call:"+e.getMessage(), Toast.LENGTH_SHORT).show();
							Log.d("error1",e.getMessage());
						}
						
						SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
						String name = preferences.getString("callstatus", "hi");
						
						///desiding block number or not
						if(blkdur==1)
						{
							duration="blocked";
							blkdur=0;
						}
						if(flg==1)
						{
						if(name.equalsIgnoreCase("incoming"))
						{
							Log.d("....1....", "..incall..");
							try 
							{
								SharedPreferences sh=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
								String phoneid=sh.getString("imeino", "0");
								call(phoneid,phnop,"incoming",duration);
							} 
							catch (Exception e)
							{
								// TODO Auto-generated catch block
								Toast.makeText(getApplicationContext(), "error2 in call:"+e.getMessage(), Toast.LENGTH_SHORT).show();
								Log.d("error2",e.getMessage());
							} 
								
							 flg=0;
						 }
						 else 
						 {
							 Log.d("....1....", "..outcall..");
							 try 
							 {
								SharedPreferences sh=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
								String opn=sh.getString("outnum", "");
								String phoneid=sh.getString("imeino", "0");
								call(phoneid,opn,"outgoing",duration);
							 } 
							 catch (Exception e) 
							 {
								// TODO Auto-generated catch block
								 Toast.makeText(getApplicationContext(), "error3 in call:"+e.getMessage(), Toast.LENGTH_SHORT).show();
								 Log.d("error3",e.getMessage());
							 }
							 
							 flg=0;
						 }
		  }
						
				         SharedPreferences.Editor editor = preferences.edit();
				         editor.putString("callstatus","idle");
				         editor.commit();
				         
				         break;
			
			
		     case TelephonyManager.CALL_STATE_OFFHOOK:
		    	 Toast.makeText(getApplicationContext(), "in off hoook", Toast.LENGTH_SHORT).show();
		    	 		SimpleDateFormat sm=new SimpleDateFormat("dd/MM/yyyy");
		    	 		SimpleDateFormat sn=new SimpleDateFormat("hh:mm:ss");
			
		    	 		flg=1;
			
		    	 		dt=sm.format(new Date());
		    	 		tm=sn.format(new Date());
		    	 		Toast.makeText(getApplicationContext(), dt + "  " + tm, Toast.LENGTH_LONG).show();
						
		    	 		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		    	 		String opn=pref.getString("outnum", "");
			
		    	 		if(opn.equalsIgnoreCase(""))
		    	 		{
		    	 				opn=phnop;
		    	 		}
			
		    	 		////taking block numbers
		    	 		SharedPreferences sh=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		    	 		String blknum=sh.getString("block","#");
			
		    	 		int xy=0;
		    	 		Log.d("...outblockno..",blknum+"..outn.."+opn);
		    	 		if(!blknum.equalsIgnoreCase("#"))
		    	 		{
			
		    	 			String b[]=blknum.split("#");
		    	 			for(int i=0;i<b.length;i++)
		    	 			{
		    	 				if(b[i].length()>=10 && opn.length()>=10)
		    	 				{
		    	 					b[i]=b[i].substring(b[i].length()-10,b[i].length() );
		    	 					opn=opn.substring(opn.length()-10,opn.length() );
		    	 					Log.d("....outnum....",b[i]+"..b[i]..outnum.."+opn);
		    	 				}				
		    	 				if(b[i].equals(opn))
		    	 				{
		    	 					xy=1;
		    	 				}
		    	 			}		
		    	 		}
			
		    	 		if(xy==1)
		    	 		{
		    	 			////call reject					
		    	 			try 
		    	 			{			      		   
		    	 				Log.d("...outn..","cutng........"); 
		    	 				telephonyManager = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
		    	 				Class c = Class.forName(telephonyManager.getClass().getName());
		    	 				Method m = c.getDeclaredMethod("getITelephony");
		    	 				m.setAccessible(true);
		    	 				ITelephony telephonyService = (ITelephony)m.invoke(telephonyManager);
		    	 				Log.d("Out block##########", "step1");
		    	 				//telephonyService.silenceRinger();
          						telephonyService.endCall();
          						Log.d("Out block##########", "step2");
          						/////variable for desiding block
          						blkdur=1;
					  		 				 
		    	 			} 
		    	 			catch (Exception e)
		    	 			{
		    	 				// TODO: handle exception
		    	 				Toast.makeText(getApplicationContext(), "error4 in call:"+e.getMessage(), Toast.LENGTH_SHORT).show();
		    	 				Log.d("error4",e.getMessage());
		    	 			}
		    	 		}
		    	 		break;
	
		     case TelephonyManager.CALL_STATE_RINGING:
		    	 Toast.makeText(getApplicationContext(), "in ringing", Toast.LENGTH_SHORT).show();
		     			phnop=inNum;
		     			
		     			Toast.makeText(getApplicationContext(), "in number.......:"+phnop, Toast.LENGTH_SHORT).show();
						
						sh=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
						blknum=sh.getString("block","#");
					
						SharedPreferences.Editor ed= sh.edit();
						ed.putString("callstatus","incoming");
						ed.putString("outnum","");
						ed.commit();
	///ends
						int xyz=0;	
						Log.d("...rnggg block no..",blknum+"..innum.."+inNum);
						
						if(!blknum.equalsIgnoreCase("#"))
						{        
							String bb[]=blknum.split("#");
							Log.d("block no length in incoming",bb.length+"");
							
							for(int i=0;i<bb.length;i++)
							{
								if(bb[i].length()>=10 && inNum.length()>=10)
								{
									bb[i]=bb[i].substring(bb[i].length()-10,bb[i].length() );
									inNum=inNum.substring(inNum.length()-10,inNum.length() );
									Log.d("...rnggg..substring..",bb[i]+"..innum.."+inNum);
								}
				
								if(bb[i].equals(inNum))
								{		
										xyz=1;
								}
							}
						}		////call reject 
						if(xyz==1)
						{		
							try
							{
								Log.d("...rnggg..","cutng........");  
			      		
								telephonyManager = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
								Class c = Class.forName(telephonyManager.getClass().getName());
								Method m = c.getDeclaredMethod("getITelephony");
								m.setAccessible(true);
								ITelephony telephonyService = (ITelephony)m.invoke(telephonyManager);
								
								telephonyService.endCall();
								/////variable for desiding block
          						blkdur=1;		 
							}
							catch (Exception e) 
							{
								// TODO: handle exception
								Toast.makeText(getApplicationContext(), "error5 in call:"+e.getMessage(), Toast.LENGTH_SHORT).show();
								Log.d("error5",e.getMessage());
							}
						}				
			
						break;
		  }
		
	   }

   };

	private void call(String imei, String inN, String Type, String duration) throws IOException, XmlPullParserException 
	{
		// TODO Auto-generated method stub
		 Toast.makeText(getApplicationContext(), "hiiiii.......call......", Toast.LENGTH_SHORT).show();
         
		JsonReq JR=new JsonReq();
	    JR.json_response=(JsonResponse) cal.this;
	    String q = "/call?imeino="+imei+"&phno="+inN+"&type="+Type+"&duration="+duration;
	    q=q.replace(" ","%20");
	    JR.execute(q);
	    
	    Log.d("..4242424242424....",imei+"..inN."+inN+"..duration."+duration+"..Type."+Type );
	     
	    
		/*
		  SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);
          request.addProperty("imeino",imei );
          request.addProperty("phno", inN);
          request.addProperty("type", Type);
          request.addProperty("duration", duration);
          
          Log.d("..4242424242424....",imei+"..inN."+inN+"..duration."+duration+"..Type."+Type );
          
 //         Toast.makeText(getApplicationContext(), METHOD_NAME1, Toast.LENGTH_SHORT).show();
         
          SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
          envelope.encodingStyle = SoapSerializationEnvelope.ENC;
          
          envelope.setOutputSoapObject(request);
          envelope.dotNet=true;
//          HttpTransportSE androidHttpTransport = new HttpTransportSE(MainActivity.URL);
          HttpTransportSE androidHttpTransport = new HttpTransportSE(url);
          androidHttpTransport.call(SOAP_ACTION1, envelope);
       
          Object    result= (Object) envelope.getResponse();
          String re="";          
          if(result!=null)
          {
              re=result.toString();
          }
          */
         // return re;
	}	


	@Override
	public IBinder onBind(Intent arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void response(JSONObject jo) 
	{
		// TODO Auto-generated method stub
		try {
			String method=jo.getString("method");
			if (method.equals("call"))
			{
				String status=jo.getString("status");
				if(status.equalsIgnoreCase("success")){
				Toast.makeText(getApplicationContext(),"success call",Toast.LENGTH_LONG).show();
				
				} else 
				{
					Toast.makeText(getApplicationContext(),"failed call..!",Toast.LENGTH_LONG).show();
				}
				
			}
			
			else {
				//Toast.makeText(getApplicationContext(), "no data", Toast.LENGTH_LONG).show();
	
		} 
			
		}catch (Exception e) {
			// TODO: handle exception
			
			  Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_LONG).show();
		}
	
	}
	
}
