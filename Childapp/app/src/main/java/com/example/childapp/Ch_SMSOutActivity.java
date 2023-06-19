package com.example.childapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;

public class Ch_SMSOutActivity extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
       // setContentView(R.layout.activity_track_main);
        
        //Intent serverIntent = new Intent(this, DeviceListActivity.class); 
        
        long currentTime = System.currentTimeMillis();
        Editor editor = getSharedPreferences("some_file_name", MODE_PRIVATE).edit();
        editor.putLong("time_last_checked", currentTime);
        editor.commit();
        
        PendingIntent outgoingSmsLogger = PendingIntent.getBroadcast(this, 0, new Intent("CHECK_OUTGOING_SMS"), 0);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, currentTime + 3000L, 3000L, outgoingSmsLogger);
    }
    
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}   
}