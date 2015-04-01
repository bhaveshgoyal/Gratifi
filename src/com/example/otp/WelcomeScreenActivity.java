package com.example.otp;

import com.example.otp.HelperClasses.SessionManager;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class WelcomeScreenActivity extends Activity {
	
	public static final String KEY_NAME = "name";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_screen_layout);
			
		SessionManager sessionManager = new SessionManager(getApplicationContext());
    	String userName = sessionManager.getUserDetails().get(KEY_NAME);
    	//TextView welcome = (TextView) findViewById(R.id.textView1);
    	//welcome.setText("Welcome " + userName);
    	//Log.i("Activity4", welcome.getText().toString());
	    	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity4, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
