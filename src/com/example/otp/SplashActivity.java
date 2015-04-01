package com.example.otp;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.example.otp.HelperClasses.ReenableAllApsWhenNetworkStateChanged;
import com.example.otp.HelperClasses.SessionManager;


import android.R.bool;
import android.app.Activity;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends Activity{
	
	Timer timer;
	MyTimerTask myTimerTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);		
		
		setContentView(R.layout.splash_layout);
		
//		 getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//	     getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		
		//2 second timer for Splash screen of Gratifi
		timer = new Timer();
	    myTimerTask = new MyTimerTask();
	    timer.schedule(myTimerTask, 2000);
	}
	
	class MyTimerTask extends TimerTask {

		  @Override
		  public void run() {
			  timer.cancel();
		      timer = null;		     
	    	  
		      	Intent intent1 = new Intent(SplashActivity.this,ConnectWifiActivity.class);		    	
		      	intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);					
		      	startActivity(intent1);
		      	SplashActivity.this.finish();
   		  }		  
	 }

	protected void onPause() {
		super.onPause();		
	}

	protected void onResume() {
		super.onResume();
	}
	
}

