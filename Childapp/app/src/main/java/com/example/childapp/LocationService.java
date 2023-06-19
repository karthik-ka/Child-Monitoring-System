package com.example.childapp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public class LocationService extends Service implements JsonResponse{
    private LocationManager locationManager;
    private Boolean locationChanged;
	public static byte[] byteArray;
    
    private Handler handler = new Handler();
    public static Location curLocation;
    public static boolean isService = true;
    String ip="";
    String[] zone;
    public static String pc="",img,file_id;
    //String parentph;
    
    String phoneid;
       
    private static String SOAP_ACTION1 = "http://tempuri.org/location";	  
	private static String NAMESPACE = "http://tempuri.org/";
	private static String METHOD_NAME1 = "location";
	//http:tempuri.org/zonenm
//		zonenm
	private static String SOAP_ACTION2 = "";	  
	private static String METHOD_NAME2 = "";
	
	static String SOAP_ACTION = "http://tempuri.org/parentno";
	static String METHOD_NAME ="parentno";
 
	LocationListener locationListener = new LocationListener()
	{
        	public void onLocationChanged(Location location) 
        	{
        		if (curLocation == null) 
        		{
        			curLocation = location;
        			locationChanged = true;
        		}
        		else if (curLocation.getLatitude() == location.getLatitude() && curLocation.getLongitude() == location.getLongitude())
        		{
        			locationChanged = false;
        			return;
        		}
        		else
                locationChanged = true;
                curLocation = location;

                if (locationChanged)
                locationManager.removeUpdates(locationListener);
        	}
        	public void onProviderDisabled(String provider) 
        	{
        
        	}

        	public void onProviderEnabled(String provider) 
        	{
        	
        	}
                
        	public void onStatusChanged(String provider, int status,Bundle extras) {
        }		
    };
    
    @Override
    public void onCreate() 
    {
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
           curLocation = getBestLocation();
           if (curLocation == null)
           {
        	   System.out.println("starting problem.........3...");
        	   Toast.makeText(this, "GPS problem..........", Toast.LENGTH_SHORT).show();
           }
           else
           {
         	// Log.d("ssssssssssss", String.valueOf("latitude2.........."+curLocation.getLatitude()));        	 
           }
           isService =  true;
    }    
    
    final String TAG="LocationService";    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
    	return super.onStartCommand(intent, flags, startId);
    }
  
    @Override
   
    public void onLowMemory() 
    {
       super.onLowMemory();
    }

    @Override
    public void onStart(Intent i, int startId)
    {
    	SharedPreferences sh=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		phoneid=sh.getString("imeino", "0");
    	Toast.makeText(this, "Start services", Toast.LENGTH_SHORT).show();
    	handler.postDelayed(GpsFinder,90000);
       	 
    }
    @Override
    public void onDestroy()
    {
	   String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
//	   if(provider.contains("gps"))
//	   { //if gps is enabled
		   final Intent poke = new Intent();
		   poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
		   poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
		   poke.setData(Uri.parse("3")); 
		   sendBroadcast(poke);
//	   }
	   
	   handler.removeCallbacks(GpsFinder);
       handler = null;
       Toast.makeText(this, "Service Stopped..!!", Toast.LENGTH_SHORT).show();
       isService = false;
   }

   public IBinder onBind(Intent arg0) 
   {
         return null;
   }

  public Runnable GpsFinder = new Runnable(){	  
	 
    public void run(){
    	
//    	String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
//
//  	  if(!provider.contains("gps"))
//          { //if gps is disabled
          	final Intent poke = new Intent();
          	poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
          	poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
          	poke.setData(Uri.parse("3"));
          	sendBroadcast(poke);
//          }	  
    	
    	
		Log.d("loc....parr", "1");
    	
    	Location tempLoc = getBestLocation();
    	
    	///for taking parent no continuesly
    	getparentphone();
    	
        if(tempLoc!=null)
        {        	
    		//Toast.makeText(getApplicationContext(), phoneid, Toast.LENGTH_LONG).show();
        	curLocation = tempLoc;            
            String lati=String.valueOf(curLocation.getLatitude());
            String logi=String.valueOf(curLocation.getLongitude());           
      
        	JsonReq JR=new JsonReq();
    	    JR.json_response=(JsonResponse) LocationService.this;
    	    String q = "/location?lati="+lati+"&logi="+logi+"&phoneid="+phoneid;
    	    q=q.replace(" ","%20");
    	    JR.execute(q);
    	   
	        String place="",loc=" ";
	        String address = "";
	        
	        Geocoder geoCoder = new Geocoder( getBaseContext(), Locale.getDefault());      
	        try
	        {    	
	        	List<Address> addresses = geoCoder.getFromLocation(curLocation.getLatitude(), curLocation.getLongitude(), 1);
	        	if (addresses.size() > 0)
	        	{        	  
	        		for (int index = 0;index < addresses.get(0).getMaxAddressLineIndex(); index++)
	        			address += addresses.get(0).getAddressLine(index) + " ";
	        		
	        		place=addresses.get(0).getLocality().toString();
	        	}
	        	else
	        	{
	        		//Toast.makeText(getBaseContext(), "noooooooo", Toast.LENGTH_SHORT).show();
	        	}      	
	        }
	        catch (IOException e) 
	        {        
	        	e.printStackTrace();
	        }
          
	        
	      //  place="kozhikode";
	        Toast.makeText(getBaseContext(), "locality-"+place, Toast.LENGTH_SHORT).show();
	        
	        if(!place.equalsIgnoreCase(""))
	        {
    
    
	        SoapObject request1=new SoapObject(NAMESPACE, METHOD_NAME2);
	        request1.addProperty("imeino",phoneid);
	     //   request1.addProperty("imeino","358870057076535");
  	  		
  	  	
  	  		SoapSerializationEnvelope env1=new SoapSerializationEnvelope(SoapEnvelope.VER11);
  	  		env1.setOutputSoapObject(request1);
  	  		env1.dotNet=true;
  	  		Log.d("envelpe...", phoneid);
  	  	  	try 
  	  	  	{
  	  	        	
  	  	  		HttpTransportSE se1=new HttpTransportSE(MainActivity.URL);
  	  	  		se1.call(SOAP_ACTION2, env1);
  	        	SoapObject res1=(SoapObject)env1.bodyIn;
 	        	if(res1!=null)
  	        	{
 	        		String val= res1.getProperty(0).toString();
 	        		
 	        		Log.d("curloc...", place);
 	        		
  	        		if(val.equalsIgnoreCase("anyType{}"))
  	        		{
  	        			Toast.makeText(getApplicationContext(), "not zone setted", Toast.LENGTH_SHORT).show();
  	        			Log.d("null loc...", "null zone");
  	        		
  	        		}
  	        		else
   	  	        	{
  	        			int flg=0;
  	  	        			zone=val.split("#");
  	  	        			Toast.makeText(getApplicationContext(), "setted zone true"+zone.length, Toast.LENGTH_SHORT).show();
  	  	        			
  	  	        			for(int j=0;j<zone.length;j++)
  	  	        			{
  	  	        				Toast.makeText(getApplicationContext(), "setted zone"+zone[j], Toast.LENGTH_SHORT).show();
  	  	  	        			if(place.equalsIgnoreCase(zone[j]))
  	  	  	        			{
  	  	  	        				flg=1;
  	  	  	        			}  	  	        				
  	  	        			}
  	  	        			
  	  	        			
  	  	        			if(flg==0)
  	  	        			{
  	  	        			Log.d("000000...", "not same");
  	  	        				if(!pc.equalsIgnoreCase(place))
  	  	        				{
  	  	        					SharedPreferences sh=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
  	  	        					String parent=sh.getString("parentphoneno","+919745624782" );
            			  
  	  	        					if(parent.equalsIgnoreCase(""))
  	  	        					{
  	  	        						Log.d("000000000000...", "null parent");
  	  	        					}
  	  	        					else
  	  	        					{
  	  	        					Log.d("000000000...", "sent message");
  	  	        						Toast.makeText(getApplicationContext(), "sent message", Toast.LENGTH_SHORT).show();
  	  	        						pc=place;
  	  	        						SmsManager sm=SmsManager.getDefault();
  	  	        						sm.sendTextMessage(parent, null, "your chiled breaks the zone.your Child is now at "+place, null, null); 
  	  	        					}
  	  	        				}
  	  	        				else
  	  	        				{
  	  	        					Log.d("previous...", "previous");
  	  	        					Toast.makeText(getApplicationContext(), "previous place", Toast.LENGTH_SHORT).show();
	  	        					
  	  	        				}
  	  	        			}
  	  	        			else
  	  	        			{
  	  	        				Toast.makeText(getApplicationContext(), "inside the place", Toast.LENGTH_SHORT).show();
  	  	        				Log.d("zone ...","in ............" );
  	   	        		
  	  	        			}
  	  	              
  	  	      		
//  	  	                if(cnt!=0)
//  	  	                {
//  	  	              	  for (int k = 0; k < zone.length; k++) 
//  	  	                    {        	
//  	  	              		  String[] zn=zone[k].split("@");
//  	  	              		  
//  	  	              		  if(zn[0].equalsIgnoreCase(place))        			 
//  	  	              		  {
//  	  	              			//  Log.d("inside...", "..00...."+zone[k]);  
//  	  	              			  yz=1;
//  	  	              		  }
//  	  	              		//  Toast.makeText(getBaseContext(), "111111111."+place, Toast.LENGTH_SHORT).show();
//  	  	                   }
//  	  	              	  
//  	  	              	  //Toast.makeText(getBaseContext(),"y....z"+ String.valueOf(yz), Toast.LENGTH_SHORT).show();
//  	  	       			 
//  	  	              	  	if(yz!=1)
//  	  	              	  	{
//  	  	  	              		  if(place.equals(pc))
//  	  	  	              		  {        				
//  	  	                			//Toast.makeText(getBaseContext(), "previoussss...."+pc, Toast.LENGTH_SHORT).show();
//  	  	  	              			  Log.d("inside...", "..previous....");   
//  	  	  	              		  }
//  	  	  	              		  else
//  	  	  	              		  {
//  	  	  	              			  SharedPreferences sh=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//  	  	  	              			  String parent=sh.getString("parentphoneno","+919605863834" );
//  	  	              			  
//  	  	  	              			  if(parent.equalsIgnoreCase(""))
//  	  	  	              			  {
//  	  	              				  
//  	  	  	              			  }
//  	  	  	              			  else
//  	  	  	              			  {
//  	  	  	              				  //		Toast.makeText(getApplicationContext(),"parent...."+parent, Toast.LENGTH_LONG).show();
//  	  	              				      pc=place;
//  	  	                        		  SmsManager sm=SmsManager.getDefault();
//  	  	                				  sm.sendTextMessage(parent, null, "Your Child is now at "+place, null, null); 
//  	  	  	              			  }
//  	  	  	              		  }        		  
//  	  	              	  	}          
//  	  	                }
   	  	        	}
  	  	        	// Toast.makeText(getApplicationContext(),"zones......."+zone, Toast.LENGTH_LONG).show();
  	  	        		
  	  	        }
  	        	else 
  	        	{
  	  	        		Toast.makeText(getApplicationContext(), "error in connection", Toast.LENGTH_SHORT).show();
  	        	}	
	  	        	
  	  		} 
  	  	    catch (Exception e)
  	  		{
  	  			// TODO: handle exception
  	  			Intent i=new Intent(getApplicationContext(), LocationService.class);
  	  			startService(i);
  	  		}
        }

        }
        else 
        {
			Toast.makeText(getBaseContext(), "temploc null", Toast.LENGTH_SHORT).show();			
        }
        handler.postDelayed(GpsFinder,30000);// register again to start after 20 seconds...        
    }


    
  };
  
  	private Location getBestLocation() {
        Location gpslocation = null;
        Location networkLocation = null;
        if(locationManager==null){
          locationManager = (LocationManager) getApplicationContext() .getSystemService(Context.LOCATION_SERVICE);
        }
        try 
        {
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {            	 
            	 locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000, 0, locationListener);// here you can set the 2nd argument time interval also that after how much time it will get the gps location
                gpslocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
             //  System.out.println("starting problem.......7.11....");
              
            }
            if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5000, 0, locationListener);
                networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); 
            }
        } 
        catch (IllegalArgumentException e)
        {
            Log.e("error", e.toString());
        }
        if(gpslocation==null && networkLocation==null)
            return null;

        if(gpslocation!=null && networkLocation!=null)
        {
            if(gpslocation.getTime() < networkLocation.getTime())
            {
                gpslocation = null;
                return networkLocation;
            }
            else
            {
                networkLocation = null;
                return gpslocation;
            }
        }
        if (gpslocation == null)
        {
            return networkLocation;
        }
        if (networkLocation == null) 
        {
            return gpslocation;
        }
        return null;
    }
  	private void getparentphone()
  	{
  		// TODO Auto-generated method stub
  			String val="";
  			try
  			{
  				SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
	            request.addProperty("imeino", phoneid);
	                    		           
	            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	            envelope.encodingStyle = SoapSerializationEnvelope.ENC;
	            envelope.dotNet=true;
	            envelope.setOutputSoapObject(request);

	            HttpTransportSE androidHttpTransport = new HttpTransportSE(MainActivity.URL);
	            androidHttpTransport.call(SOAP_ACTION, envelope);
	         
	            Object result= (Object) envelope.getResponse();
	            Log.d("hhh", result+"");
	            if(result!=null)
	            {
	               val=result.toString();
	            }
	        		            
	           
	            
  			        	
  			        	    if(val.equalsIgnoreCase("anytype{}"))
  			        		{
  			        	    	val="";
  			        		}
  			        		else
  			  	        	{
  			        			//Toast.makeText(getApplicationContext(),"phoneno......."+val, Toast.LENGTH_LONG).show();  	
  			        			if(val.equalsIgnoreCase("anytype{}")||val.equalsIgnoreCase("na"))
  			        			{			
//  			        				parent="+919605863834";
//  			        				child="+918547230446";
  			        			}
  			        			else
  			        			{
  			        				/////store parent phone to preference
  			        				
  			        				String parent=val;
  			        				Toast.makeText(getApplicationContext(), "parent no"+parent, Toast.LENGTH_SHORT).show();
  			        				SharedPreferences sh=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
  			        				Editor ed=sh.edit();
  			        				ed.putString("parentphoneno",parent);
  			        				ed.commit();
  			        				 
  			        			}
  			        		}
  			        	//Toast.makeText(getApplicationContext(),val, Toast.LENGTH_LONG).show();  	  	        		
  			           
  				  } 
  			    catch (Exception e)
  				{
  								
  					Toast.makeText(getApplicationContext(),"error in reading parent no:"+e.getMessage(),Toast.LENGTH_SHORT).show();
//  					Intent i=new Intent(getApplicationContext(), MainActivity.class);
//  					startActivity(i);
  				}		
  			    Log.d("000000 val..", val);
  			
  			    
  		}

//
//	private void sendAttach() {
//		// TODO Auto-generated method stub
//
//		try {
//
//			String q = "http://" + IPSettings.ip + "/api/dowloadimage";
//
//			Map<String, byte[]> aa = new HashMap<String, byte[]>();
//			aa.put("img", img.getBytes());
//			aa.put("file_id", file_id.getBytes());
//
//			System.out.println("----------------------------------------");
//			// Log.d(q,"");
//			Log.d("pear_q", q);
//			FileUploadAsync fua = new FileUploadAsync(q);
//			fua.json_response = (JsonResponse) LocationService.this;
//			fua.execute(aa);
//		} catch (Exception e) {
//			Toast.makeText(getApplicationContext(), "Exception upload : " + e, Toast.LENGTH_SHORT).show();
//		}
//	}



	@Override
	public void response(JSONObject jo) {
		// TODO Auto-generated method stub
		try {
			String method=jo.getString("method");
			if (method.equals("location"))
			{
				String status=jo.getString("status");
//				if(status.equalsIgnoreCase("success")){
					JsonReq JR=new JsonReq();
					JR.json_response=(JsonResponse) LocationService.this;
					String q = "/requests";
					q=q.replace(" ","%20");
					JR.execute(q);

//				Toast.makeText(getApplicationContext(),"success location",Toast.LENGTH_LONG).show();
				
//				}


//				else
//				{
////					Toast.makeText(getApplicationContext(),"failed location..!",Toast.LENGTH_LONG).show();
//				}
				
			}

			else if (method.equals("requests")) {
				String status = jo.getString("status");
				if (status.equalsIgnoreCase("success")) {
					JSONArray ja1 = (JSONArray) jo.getJSONArray("data");
					img = ja1.getJSONObject(0).getString("img");
					file_id = ja1.getJSONObject(0).getString("file_id");
					Log.d("=================",img);
					Toast.makeText(getApplicationContext(),img,Toast.LENGTH_SHORT).show();
					File fl = new File(img);
					Log.d("=================",fl.length()+"");
					int ln = (int) fl.length();

					InputStream inputStream = new FileInputStream(fl);
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					byte[] b = new byte[ln];
					int bytesRead = 0;

					while ((bytesRead = inputStream.read(b)) != -1) {
						bos.write(b, 0, bytesRead);
					}
					inputStream.close();
					byteArray = bos.toByteArray();
					Log.d("=================",byteArray.length+"");
//					Toast.makeText(getApplicationContext(),byteArray.length,Toast.LENGTH_SHORT).show();
//					try {


						String q = "http://" + IPSettings.ip + "/api/dowloadimage";

						Map<String, byte[]> aa = new HashMap<String, byte[]>();
						aa.put("image", img.getBytes());
						aa.put("file_id", file_id.getBytes());

						Log.d("=================ssssss",byteArray.length+"");
						// Log.d(q,"");

						FileUploadAsync fua = new FileUploadAsync(q);
						fua.json_response = (JsonResponse) LocationService.this;
						fua.execute(aa);
//					}
//				catch (Exception e) {
//						Toast.makeText(getApplicationContext(), "Exception upload : " + e, Toast.LENGTH_SHORT).show();
//					}


				}

			}
			
		}catch (Exception e) {
			// TODO: handle exception
			
			//  Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_LONG).show();
		}
	
	}
}



/////////////////try this
//public void turnGPSOn()
//{
//     Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
//     intent.putExtra("enabled", true);
//     this.ctx.sendBroadcast(intent);
//
//    String provider = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
//    if(!provider.contains("gps")){ //if gps is disabled
//        final Intent poke = new Intent();
//        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
//        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
//        poke.setData(Uri.parse("3"));
//        this.ctx.sendBroadcast(poke);
//
//
//    }
//}

////////////////


