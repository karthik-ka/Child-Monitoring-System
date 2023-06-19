package com.example.childapp;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;


public class caloutreceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		
		Bundle bun=arg1.getExtras();
				if(bun==null)
				{
					return;
				}
				else
				{
					String num=arg1.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
					Toast.makeText(arg0, num+".............", Toast.LENGTH_LONG).show();
					SharedPreferences sh=PreferenceManager.getDefaultSharedPreferences(arg0);
					Editor ed=sh.edit();
					ed.putString("outnum", num);
					ed.putString("type", "outcall");
					ed.commit();
				}
	 }
}