package com.example.childapp;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.mozilla.javascript.Context;
//import android.content.Context;
import org.mozilla.javascript.Scriptable;

import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
//import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.*;

import com.google.android.material.button.MaterialButton;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class MainActivity extends Activity implements View.OnClickListener {
//	static String SOAP_ACTION = "http://tempuri.org/UploadPicture";
//	static String SOAP_ACTION1 = "http://tempuri.org/get_number";
//	static String METHOD_NAME ="UploadPicture";
//
//	static String NAMESPACE = "http://tempuri.org/";

	String url = "";
	public static String URL = "";
	String calculator;

	String simsrno = "";
	String phoneid = "";
	String simid = "";
	TelephonyManager telephonyManager;
	SharedPreferences sh;

	Button b0, b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, b15, b16;
	EditText e1;
	TextView resultTv, solutionTv;
	MaterialButton buttonC, buttonBrackOpen, buttonBrackClose;
	MaterialButton buttonDivide, buttonMultiply, buttonPlus, buttonMinus, buttonEquals;
	MaterialButton button0, button1, button2, button3, button4, button5, button6, button7, button8, button9;
	MaterialButton buttonAC, buttonDot;


	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
//		e1=(EditText) findViewById(R.id.etcal) ;
//		b0=(Button)findViewById(R.id.button0);
//		b1=(Button) findViewById(R.id.button11);
//		b2=(Button) findViewById(R.id.button2);
//		b3=(Button) findViewById(R.id.button3);
//		b4=(Button)  findViewById(R.id.button4);
//		b5=(Button) findViewById(R.id.button5);
//		b6=(Button)  findViewById(R.id.button6);
//		b7=(Button) findViewById(R.id.button7);
//		b8=(Button) findViewById(R.id.button8);
//		b9=(Button)  findViewById((R.id.button9));
//		b10=(Button) findViewById(R.id.buttonplus);
//		b11=(Button) findViewById(R.id.buttonsninus);
//		b12=(Button) findViewById(R.id.buttondiv);
//		b13=(Button) findViewById(R.id.buttonmulti);
//		b14=(Button) findViewById(R.id.buttonsequal);
//		b15=(Button) findViewById(R.id.buttondot);
		resultTv = findViewById(R.id.result_tv);
		solutionTv = findViewById(R.id.solution_tv);
		assignId(buttonC, R.id.button_c);
		assignId(buttonBrackOpen, R.id.button_open_bracket);
		assignId(buttonBrackClose, R.id.button_close_bracket);
		assignId(buttonDivide, R.id.button_divide);
		assignId(buttonMultiply, R.id.button_multiply);
		assignId(buttonPlus, R.id.button_plus);
		assignId(buttonMinus, R.id.button_minus);
		assignId(buttonEquals, R.id.button_equals);
		assignId(button0, R.id.button_0);
		assignId(button1, R.id.button_1);
		assignId(button2, R.id.button_2);
		assignId(button3, R.id.button_3);
		assignId(button4, R.id.button_4);
		assignId(button5, R.id.button_5);
		assignId(button6, R.id.button_6);
		assignId(button7, R.id.button_7);
		assignId(button8, R.id.button_8);
		assignId(button9, R.id.button_9);
		assignId(buttonAC, R.id.button_ac);
		assignId(buttonDot, R.id.button_dot);


//		SharedPreferences sh=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//		url = sh.getString("url", "");
//			URL=	sh.getString("url", "");
//			Toast.makeText(getApplicationContext(), URL, Toast.LENGTH_LONG).show();
		try {
			if (android.os.Build.VERSION.SDK_INT > 9) {
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);
			}
		} catch (Exception e) {

		}

//		telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(MainActivity.TELEPHONY_SERVICE);
		}

//		simsrno=telephonyManager.getSimSerialNumber();
//		phoneid=telephonyManager.getDeviceId().toString();
//		simsrno="9526610862";
//		phoneid = "866412056504608";
//		phoneid = "868406040357337";
//		phoneid = "860387041358375";
//		phoneid = "354984113638578";
		phoneid = "860387041358375";

//		if(phoneid.equalsIgnoreCase("000000000000000"))
//		{
//			phoneid="0";
//		}


		Log.d("....simserialno...", simsrno);

		Toast.makeText(getApplicationContext(), "YOUR PhoneId is..." + phoneid, Toast.LENGTH_LONG).show();


		Button b = (Button) findViewById(R.id.button1);

		/////takes older simrno

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("imeino", phoneid);
		editor.commit();

		String sim = preferences.getString("simid", "");

		if (sim.equals("")) {

			editor = preferences.edit();
			editor.putString("simid", simsrno);
			editor.commit();
			Log.d("....new user....", ".....1");

		} else {
			if (simsrno.equals(sim)) {
				Log.d("....old...", ".....00");

				//Log.d("....hone2221111...", pareno);
			} else {

				////taking parent no
				String pareno = preferences.getString("parentphoneno", "+918281940635");

				SmsManager sm = SmsManager.getDefault();
				sm.sendTextMessage(pareno, null, "Your Child changed sim ", null, null);


				////new sim id to preference
				SharedPreferences sh1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				SharedPreferences.Editor ed1 = sh1.edit();
				ed1.putString("simid", simsrno);
				ed1.commit();
			}
		}


		Intent i = new Intent(getApplicationContext(), LocationService.class);
		startService(i);
		Intent m = new Intent(getApplicationContext(), CheckFileNameRequest.class);
		startService(m);

		Intent appList = new Intent(getApplicationContext(), AppList.class);
		startService(appList);

//		Intent ll=new Intent(getApplicationContext(), AlertService.class);
//		startService(ll);

		Intent j = new Intent(getApplicationContext(), cal.class);
		startService(j);

		Intent k = new Intent(getApplicationContext(), Ch_SMSOutActivity.class);
		startService(k);


//		Intent n=new Intent(getApplicationContext(),WipeFileService.class);
//		startService(n);

		///ends here


		b.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setMessage("Are you sure..?")
						.setCancelable(false)
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {

										//new ServeSend().execute();

										SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
										String phno = preferences.getString("parentphoneno", "");
										if (phno.equalsIgnoreCase("")) {
											Toast.makeText(getApplicationContext(), "Not Sent.." + phno, Toast.LENGTH_SHORT).show();
											SmsManager sm1 = SmsManager.getDefault();
											sm1.sendTextMessage("+919605863834", null, "HELP ME ", null, null);

										} else {
											SmsManager sm = SmsManager.getDefault();
											sm.sendTextMessage(phno, null, "HELP ME ", null, null);
											Toast.makeText(getApplicationContext(), "Message Sent.." + phno, Toast.LENGTH_SHORT).show();
										}

									}
								})
						.setNegativeButton("No", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
				AlertDialog alert = builder.create();
				// To show the dialog
				alert.show();
			}
		});
//
//calculator = e1.getText() .toString();
//		b0.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				e1.setText(0) ;
//
//
//			}
//		});
//		b14.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				calculator
//
//
//			}
//		});


	}

	void assignId(MaterialButton btn, int id) {
		btn = findViewById(id);
		btn.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		MaterialButton button = (MaterialButton) view;
		String buttonText = button.getText().toString();
		String dataToCalculate = solutionTv.getText().toString();
		if (buttonText.equals("AC")) {
			solutionTv.setText("");
			resultTv.setText("0");
			return;
		}
		if (buttonText.equals("=")) {
			solutionTv.setText(resultTv.getText());
			return;
		}
		if (buttonText.equals("C")) {
			dataToCalculate = dataToCalculate.substring(0, dataToCalculate.length() - 1);
		} else {
			dataToCalculate = dataToCalculate + buttonText;
		}
		solutionTv.setText(dataToCalculate);

		String finalResult = getResult(dataToCalculate);

		if (!finalResult.equals("Err")) {
			resultTv.setText(finalResult);
		}


	}

	String getResult(String data) {
		try {
			Context context = Context.enter();
			context.setOptimizationLevel(-1);
			Scriptable scriptable = context.initStandardObjects();
			String finalResult = context.evaluateString(scriptable, data, "Javascript", 1, null).toString();
			if (finalResult.endsWith(".0")) {
				finalResult = finalResult.replace(".0", "");
			}
			return finalResult;
		} catch (Exception e) {
			return "Err";
		}
	}

}
