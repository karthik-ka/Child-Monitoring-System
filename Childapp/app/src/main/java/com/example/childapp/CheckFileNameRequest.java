package com.example.childapp;

import java.io.File;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
public class CheckFileNameRequest extends Service implements JsonResponse{
	Handler handler=new Handler();
	
	String NAMESPACE = "http://tempuri.org/";
	
	String SOAP_ACTION = "http://tempuri.org/requestForNames";
	String METHOD_NAME ="requestForNames";
	
	String SOAP_ACTION1 = "http://tempuri.org/filelist";
	String METHOD_NAME1 ="filelist";
	
	String filePath;
	String phoneid;
	SharedPreferences sh;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		//super.onStart(intent, startId);
		
		
		sh=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		phoneid=sh.getString("imeino", "0");
		Toast.makeText(getApplicationContext(), "filr service started", Toast.LENGTH_SHORT).show();
		handler.postDelayed(checkRequests,2000);
	}


	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
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


	public Runnable checkRequests=new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
//			String c=check();
			
//			if(c.equalsIgnoreCase("1"))
//			{
				////take file names from memory
				//String filenames="";
				File file[] = Environment.getExternalStorageDirectory().listFiles();
				filePath="";
				String files= recursiveFileFind(file);
				Log.d("filenames", files);
				send(files);
//			}
			
			handler.postDelayed(checkRequests, 200000);
		}
	};
	
	///////////calling service to know the namees are requested
	public String check()
	{
		String re="";
		try
		{
		      SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
	         // request.addProperty("imeino",phoneid);
	          	         
	          SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	          envelope.encodingStyle = SoapSerializationEnvelope.ENC;
	          
	          envelope.setOutputSoapObject(request);
	          envelope.dotNet=true;
	          HttpTransportSE androidHttpTransport = new HttpTransportSE(MainActivity.URL);
	          androidHttpTransport.call(SOAP_ACTION, envelope);
	       
	          Object    result= (Object) envelope.getResponse();
	                  
	          if(result!=null)
	          {
	              re=result.toString();
	          }

		}
		catch(Exception e)
		{
			re="no";
		}
		return re;
	}
	
	///////////calling service to send file names
	public void send(String fnames)
	{
		try
		{
////		      SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);
////	          //request.addProperty("imeino",phoneid);
////	          request.addProperty("a",fnames);
//
//	          SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//	          envelope.encodingStyle = SoapSerializationEnvelope.ENC;
//
//	          envelope.setOutputSoapObject(request);
//	          envelope.dotNet=true;
//	          HttpTransportSE androidHttpTransport = new HttpTransportSE(MainActivity.URL);
//	          androidHttpTransport.call(SOAP_ACTION1, envelope);
			JsonReq JR=new JsonReq();
			JR.json_response=(JsonResponse) CheckFileNameRequest.this;
			String q = "/files?imeino="+phoneid+"&fnames="+fnames;
			q=q.replace(" ","%20");
			JR.execute(q);
		}
		catch(Exception e)
		{
			Toast.makeText(getApplicationContext(), "Error in sendinf Filenames:"+e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	
	/////////mehode for taking file names
	
	public String recursiveFileFind(File[] file1)
	{
		String gname;
		int i = 0;
		if(file1!=null)
		{
		    while(i!=file1.length)
		    {
		    	if(file1[i].isDirectory())
		    	{
		    		File file[] = file1[i].listFiles();
		            recursiveFileFind(file);
		        }
		        else
		        { 
		        	gname=file1[i].getAbsolutePath();
		        	if(gname.contains(".jpg"))
		        	{
		        		filePath+= file1[i].getAbsolutePath()+"$";

					}
		            //filePath+= file1[i].getAbsolutePath()+"#";                    
		        }

		        i++;
		        Log.d(i+"..", filePath);
		    }
//			Toast.makeText(getApplicationContext(), "filepath : " + filePath, Toast.LENGTH_LONG).show();

		}
		  return filePath;
	}

	@Override
	public void response(JSONObject jo) {

	}
}
