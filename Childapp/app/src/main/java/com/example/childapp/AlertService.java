package com.example.childapp;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class AlertService extends Service implements JsonResponse{
   
	private Handler handler = new Handler();
    //public static boolean isService = true;
    
	static String NAMESPACE = "http://tempuri.org/";
	static String SOAP_ACTION2 = "http://tempuri.org/blockno";
	static String METHOD_NAME2 ="blockno";
	SharedPreferences sh;
	String url=""; 
    @Override
    public void onCreate() 
    {
    	SharedPreferences sh=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		url = sh.getString("url", "");
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
    	
    	
    }    
    final String TAG="LocationService";    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	return super.onStartCommand(intent, flags, startId);
   }
   @Override
   public void onLowMemory() {
       super.onLowMemory();
   }

   @Override
   public void onStart(Intent i, int startId){
	  Toast.makeText(this, "Start services", Toast.LENGTH_SHORT).show();

	  handler.postDelayed(GpsFinder,30000);

   }
   @Override
   public void onDestroy() {
       handler.removeCallbacks(GpsFinder);
       handler = null;
       Toast.makeText(this, "Service Stopped..!!", Toast.LENGTH_SHORT).show();
       //isService = false;
   }

   public IBinder onBind(Intent arg0) 
   {
         return null;
   }

  public Runnable GpsFinder = new Runnable(){
    public void run()
    {
    	getblock();    	
        handler.postDelayed(GpsFinder,30000);// register again to start after 20 seconds...        
    }

  };
protected String getblock() {
	// TODO Auto-generated method stub
	
	SharedPreferences sh=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	String phoneid=sh.getString("imeino", "0");
	
	String ret="";
	
	JsonReq JR=new JsonReq();
    JR.json_response=(JsonResponse) AlertService.this;
    String q = "/blockno?imeino="+phoneid;
    q=q.replace(" ","%20");
    JR.execute(q);
	
	/*
	 
			try 
			{  
				SharedPreferences sh=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				String phoneid=sh.getString("imeino", "0");
				
				String ret="";
			
		            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME2);
		            request.addProperty("imeino", phoneid);
		                    		           
		            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		            envelope.encodingStyle = SoapSerializationEnvelope.ENC;
		            envelope.dotNet=true;
		            envelope.setOutputSoapObject(request);

		            HttpTransportSE androidHttpTransport = new HttpTransportSE(url);
		            androidHttpTransport.call(SOAP_ACTION2, envelope);
		         
		            Object result= (Object) envelope.getResponse();
		            Log.d("hhh", result+"");
		            
		            if(result!=null)
		            {
		               ret=result.toString();
		               Toast.makeText(getApplicationContext(), ret, Toast.LENGTH_LONG).show();
		            }
		        		            
		            Log.d("BLOCK LIST0000000", ret);
		            if(ret.equalsIgnoreCase("#")||ret.equalsIgnoreCase("anyType{}")||ret.equalsIgnoreCase("na"))
		            {
		            	ret="#";
		            }
		            
			            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
			    		SharedPreferences.Editor editor = preferences.edit();
			    		editor.putString("block", ret);
			    		editor.commit();   
		            
		       
	        } 
			catch (Exception e) 
			{
		            Toast.makeText(getApplicationContext(), e.getMessage(),Toast.LENGTH_LONG).show();
		            Log.d("Error", e.toString());
		    }	
		    */
			return null;
			
	}
@Override
public void response(JSONObject jo) {
	// TODO Auto-generated method stub
	try 
	{
			String method=jo.getString("method");
			if (method.equals("blocno"))
			{
			String status=jo.getString("status");
			if(status.equalsIgnoreCase("success"))
			{
			
					String no="";
					JSONArray ja1=(JSONArray)jo.getJSONArray("data");
				
					
				    no=ja1.getJSONObject(0).getString("message");
				
				
				 	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		    		SharedPreferences.Editor editor = preferences.edit();
		    		editor.putString("block", no);
		    		editor.commit(); 
				//Toast.makeText(getApplicationContext(),"successfully added",Toast.LENGTH_LONG).show();
				
			} else 
			{
				//Toast.makeText(getApplicationContext(),"enquiry is not send..!",Toast.LENGTH_LONG).show();
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