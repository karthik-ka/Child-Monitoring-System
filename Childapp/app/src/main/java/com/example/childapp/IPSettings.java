package com.example.childapp;

import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class IPSettings extends Activity {

	Button b1;
	EditText e1;
	public static String ip = "";
	SharedPreferences sh;
	String imei = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ipsettings);
		
		try {
			if(Build.VERSION.SDK_INT > 9) {
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);
			}
		} catch(Exception e) { }
		
		sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		 b1=(Button)findViewById(R.id.button1);
		 e1=(EditText)findViewById(R.id.editText1);
		 e1.setText(sh.getString("ip", "192.168.1.1"));
		 
		 b1.setOnClickListener(new View.OnClickListener() {
			 
			 @Override
			 public void onClick(View arg0) {
				 ip = e1.getText().toString();
				 if (ip.equals("")) {
					 e1.setError("IP address please");
				 } else {
					 Editor ed = sh.edit();
					 ed.putString("ip", ip);
					 ed.commit();
					 startActivity(new Intent(getApplicationContext(), MainActivity.class));
					}
				 }
			 });
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.ipsettings, menu);
//		return true;
//	}

}
