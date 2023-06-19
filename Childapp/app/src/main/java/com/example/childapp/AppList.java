package com.example.childapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class AppList extends Service implements JsonResponse {
	
	private Handler handler = new Handler();
	SharedPreferences sh;
    
    List<PackageInfo> packs;
    ArrayList<String> packg, permissions;
    ArrayList<String> clas;
    Context context;
    int  clickcount = 0;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
    public void onCreate() {
        super.onCreate();
        
        context = getApplicationContext();

        sh = PreferenceManager.getDefaultSharedPreferences(context);
        
        String installedApps = ""; 
        installedApps = getInstalledApps();
		Toast.makeText(getApplicationContext(), "Apps : " + installedApps, Toast.LENGTH_LONG).show();

        
        
        if (!installedApps.equals("")) {
        	String phoneid=sh.getString("imeino", "0");
        	
        	JsonReq JR=new JsonReq();
    	    JR.json_response=(JsonResponse) AppList.this;
    	    String q = "/applist?imeino="+phoneid+"&apps=" + installedApps;
    	    q=q.replace(" ","%20");
    	    JR.execute(q);
        }
    }
	
	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		handler.postDelayed(GpsFinder, 45000);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	public Runnable GpsFinder = new Runnable() {	  
		 
	    public void run(){
	    	
	        handler.postDelayed(GpsFinder,30000);// register again to start after 20 seconds...        
	    }
	};
	
	private String getInstalledApps() {
		String apps = "";
        try {
            packs = getPackageManager().getInstalledPackages(0);

            for (int i = 0; i < packs.size(); i++) {
                PackageInfo p = packs.get(i);

                String appName = p.applicationInfo.loadLabel(getPackageManager()).toString();
                String pkg = p.packageName;
                apps = apps + appName + "$";
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Exception : " + e, Toast.LENGTH_LONG).show();
        }
        Toast.makeText(getApplicationContext(), "Apps : " + apps, Toast.LENGTH_LONG).show();
        return apps;
    }

	@Override
	public void response(JSONObject jo) {
		
	}
	
	

}
