package com.example.otp;



import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.otp.HelperClasses.JSONParser;
import com.example.otp.HelperClasses.ReadIncomingSMS;
import com.example.otp.HelperClasses.SessionManager;


public class OTPVerificationActivity extends Activity {
	
	// Progress Dialog
	private ProgressDialog pDialog;
	ImageView otpFailedText; 
	
	// Creating JSON Parser object
	JSONParser jParser;
//	= new JSONParser(getApplicationContext());
	
	// url to send OTP request
	private static String url_otp_request = "http://54.149.8.166/gratifi-back/v1/index.php/otp_generate";
	
	// url to send Register request
	private static String url_register = "http://54.149.8.166/gratifi-back/v1/index.php/register_login_app_user";
	
	private String otp = ""; 

	private String TAG_ERROR = "error";
	WifiManager wifi;
	SessionManager sessionManager;
	
	Button bt;
	TextView view;
	
	private BroadcastReceiver receiver = new ReadIncomingSMS();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        wifi = (WifiManager)getSystemService(WIFI_SERVICE);
        jParser = new JSONParser(getApplicationContext());
        setContentView(R.layout.otp_verification_layout);
        bt=(Button)findViewById(R.id.skipbutton);
        otpFailedText = (ImageView)findViewById(R.id.otpfailedtext);        
        
        sessionManager = new SessionManager(getApplicationContext());
        
//        if(sessionManager.isLoggedIn()){
//        	System.out.println("Already logged in");
//			Intent intent=new Intent(OTPVerificationActivity.this,WebLoginActivity.class);
//			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);					
//			startActivity(intent);
//			finish();
//        }
                
        //Register the SMS Listener
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        ((ReadIncomingSMS)receiver).setMainActivityHandler(OTPVerificationActivity.this);
        registerReceiver(receiver, filter);
        
        bt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
									
				//Here send request to send SMS to the mentioned Mobile number and on receiving the SMS
				//start the next intent.
				System.out.println("Clicked");

                pDialog = new ProgressDialog(OTPVerificationActivity.this);
                otpFailedText.setVisibility(4);
				
				new SendOTPRequest().execute();
				
//				
//				Intent intent=new Intent(MainActivity.this,Activity2.class);
//				startActivity(intent);
//				
				//Unregister the SMS listener
				//Add condition to check if receiver is registered				
//				unregisterReceiver(receiver);
    }
        });
    }
    
    @Override
    protected void onPause() {
		//unregisterReceiver(receiver);				
		super.onPause();		
	} 
    
    public void OnSMSReceived(String otp_received)
    {
    	otp = otp_received;
    	System.out.println("OTP - " + otp);
    	new SendRegisterRequest().execute();
    }
    /**
	 * Background Async Task to Send OTP Request
	 * */
	class SendOTPRequest extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();			
			pDialog.setMessage("Requesting OTP. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			
//			EditText view =(EditText)findViewById(R.id.editText1) ;
			String mobile = "9160668818"; 
//+ view.getText().toString();
			
			params.add(new BasicNameValuePair("mobile", mobile));
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_otp_request, "POST", params, false);
			
			// Check your log cat for JSON response
			//System.out.println("otp request response: " + json.toString());

			try {
				// Checking for SUCCESS TAG
				if(json != null && !json.getBoolean(TAG_ERROR)){
					//Toast.makeText(getApplicationContext(), "OTP Request Sent", Toast.LENGTH_SHORT).show();
				} else {
					runOnUiThread(new Runnable() {
					     @Override
					     public void run() {
					    	 //stuff that updates ui
					    	 otpFailedText.setVisibility(0);
					    }
					});
					
					pDialog.dismiss();
					
					//Toast.makeText(getApplicationContext(), "OTP Request Failed", Toast.LENGTH_SHORT).show();
//					Intent i = new Intent(getApplicationContext(),
//							NewProductActivity.class);
//					// Closing all previous activities
//					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//					startActivity(i);
				}
			} catch (JSONException e) {
				//e.printStackTrace();
				runOnUiThread(new Runnable() {
				     @Override
				     public void run() {
				    	 //stuff that updates ui
				    	 otpFailedText.setVisibility(0);
				    }
				});				
				pDialog.dismiss();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			//pDialog.dismiss();
		}

	}
    

    /**
	 * Background Async Task to Send Register Request
	 * */
	class SendRegisterRequest extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
//			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("Verifying OTP. Please wait...");
//			pDialog.setIndeterminate(false);
//			pDialog.setCancelable(false);
//			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			
//			EditText view =(EditText)findViewById(R.id.editText1) ;
			String mobile = "9160668818";
//			+ view.getText().toString();
			
			params.add(new BasicNameValuePair("mobile", mobile));
			params.add(new BasicNameValuePair("otp", otp));
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_register, "POST", params, false);
			
			// Check your log cat for JSON reponse
			Log.d("otp request response: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				Boolean error = json.getBoolean(TAG_ERROR);

				if (!error) {			
					//Toast.makeText(getApplicationContext(), "OTP Verified", Toast.LENGTH_SHORT).show();
					pDialog.dismiss();
					System.out.println("Successfull");
					unregisterReceiver(receiver);
					
//					SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					ConnectWifiActivity.pref.edit().putBoolean("IS_OTP", true).commit();
				    startService(new Intent(getApplicationContext(), ScanService.class));
					Intent intent=new Intent(OTPVerificationActivity.this,WebLoginActivity.class);
					intent.putExtra("location_id",wifi.getConnectionInfo().getBSSID());
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);					
					startActivity(intent);
										
					finish();
					
				} else {
					pDialog.dismiss();
					otpFailedText.setVisibility(0);
					//Toast.makeText(getApplicationContext(), "OTP Verification Failed", Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			//pDialog.dismiss();
			// updating UI from Background Thread
			/*runOnUiThread(new Runnable() {
				public void run() {
					*//**
					 * Updating parsed JSON data into ListView
					 * *//*
					ListAdapter adapter = new SimpleAdapter(
							AllProductsActivity.this, productsList,
							R.layout.list_item, new String[] { TAG_PID,
									TAG_NAME},
							new int[] { R.id.pid, R.id.name });
					// updating listview
					setListAdapter(adapter);
				}
			});*/

		}

	}
    

	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
