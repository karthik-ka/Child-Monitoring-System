package com.example.cts;

import java.io.File;

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

import com.example.childapp.MainActivity;

public class WipeFileService extends Service {

	Handler handler=new Handler();
	
	String NAMESPACE = "http://tempuri.org/";
	
	String SOAP_ACTION = "http://tempuri.org/filenameForWipe";
	String METHOD_NAME ="filenameForWipe";
	
	String phoneid;
	SharedPreferences sh;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onStart(Intent intent, int startId) {
		
		
		// TODO Auto-generated method stub
		//super.onStart(intent, startId);
		sh=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		phoneid=sh.getString("imeino", "0");
		Toast.makeText(getApplicationContext(), "filr service started", Toast.LENGTH_SHORT).show();
		handler.postDelayed(checkForDelete,2000);
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

	public Runnable checkForDelete=new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			String re="";
			try
			{
			      SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		          //request.addProperty("imeino",phoneid);
		          	         
		          SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		          envelope.encodingStyle = SoapSerializationEnvelope.ENC;
		          
		          envelope.setOutputSoapObject(request);
		          envelope.dotNet=true;
		          HttpTransportSE androidHttpTransport = new HttpTransportSE(MainActivity.URL);
		          androidHttpTransport.call(SOAP_ACTION, envelope);
		       
		          Object    result= (Object) envelope.getResponse();
		          re=result.toString();
		         Log.d("Result","**********"+re+"************");
		          if(!re.equalsIgnoreCase("anyType{}"))
		          {
		        	  Log.d("inside","**********"+re+"************");
		        	  File temp=new File(re);
		        	  if(temp.delete())
		        	  {
		        		  Log.d("tryfordelete","********** success  ************");
		        	  }
		        	  else
		        	  {
		        		  Log.d("tryfordelete","**********failed************");
		        	  }
//		        	  if(temp.getCanonicalFile().delete())
//		        	  {
//		        		  Log.d("tryfordelete","********** success  ************");
//		        	  }
//		        	  else
//		        	  {
//		        		  Log.d("tryfordelete","**********failed************");
//		        	  }
		        	  
		        	  Log.d("afterdelete","**********"+re+"************");
		          }
			}
			catch(Exception e)
			{
				Log.d("********************error********","**********"+e.getMessage()+"************");
				Toast.makeText(getApplicationContext(), "eroor in delete file"+e.getMessage(), Toast.LENGTH_SHORT).show();
			}
			
			handler.postDelayed(checkForDelete, 2000);
		}
	};
}
