package com.example.otp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import com.example.otp.HelperClasses.JSONParser;
import com.example.otp.HelperClasses.SessionManager;

public class WebLoginActivity extends Activity {

//	private boolean LoginSuccessful = false;
	
	String url_hotspot_login_info = "http://54.149.8.166/gratifi-back/v1/index.php/get_hotspot_info/";
	
	String location_id = null;
    SharedPreferences pref;
    
	String login_url = "http://192.168.137.1/log.html";
	String userName = "user";
	String password = "12345678";
	SessionManager session;
	// Creating JSON Parser object
	JSONParser jParser;
//	= new JSONParser(getApplicationContext());
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		session = new SessionManager(getApplicationContext());
		jParser = new JSONParser(getApplicationContext());
		setContentView(R.layout.web_login_layout);
		Bundle bundle = getIntent().getExtras();

        if(bundle != null && bundle.getString("location_id")!= null)
        {
        	location_id =(String) bundle.get("location_id");
        	url_hotspot_login_info = url_hotspot_login_info + location_id;
    		new GetLoginCredentials().execute();
        }
		
	}
	

	public class GetLoginCredentials extends AsyncTask<String, Integer, Long> {
	     protected Long doInBackground(String... url) {
	         
	    	 System.out.println("Getting Login Credentials");
	    	 
		    	SessionManager sessionManager = new SessionManager(getApplicationContext());
		    	
		    	List<NameValuePair> paramValuePairs = new ArrayList<NameValuePair>(2);
		    			    				    	
		    	// getting JSON string from URL
//				JSONObject json = jParser.makeHttpRequest(url_hotspot_login_info, "GET", paramValuePairs, true);
				
				// Check your log cat for JSON reponse
//				Log.d("otp request response: ", json.toString());
	
				try {
					// Checking for SUCCESS TAG
//					Boolean error = json.getBoolean("error");
	
					if (true){
//					if (!error) {	
//						login_url = json.getString("login_url");
//						userName = json.getString("login_user");
//						password = json.getString("login_password_hash");
//						new WebLogin().execute();						
					} else {
						
					}
				} catch (Exception e) {
					e.printStackTrace();
				}		
			  
			    return (long) 1;
	     }

	     protected void onPostExecute(Long result) {	    	 
	         //showDialog("Downloaded " + result + " bytes");
	    	 if(pref.getBoolean("IS_WEB", true)){
	    		 Intent intent=new Intent(WebLoginActivity.this,FBLoginActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);					
				startActivity(intent);
	    	 }
	     }
	 }


	
	
	public class WebLogin extends AsyncTask<String, Integer, Long> {
	     protected Long doInBackground(String... url) {
	         
	    	 System.out.println("Trying Login");
				// Create a new HttpClient and Post Header
			    HttpClient httpclient = new DefaultHttpClient();
			    
			    HttpPost httppost = new HttpPost(login_url);
				
			    try {
			        // Add your data
			        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			        nameValuePairs.add(new BasicNameValuePair("_pass", password));
			        nameValuePairs.add(new BasicNameValuePair("_action", "freelogin"));
			        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			        // Execute HTTP Post Request
//			        HttpResponse response = httpclient.execute(httppost);
			    if (true){
//			        if(response != null)
//			        {
			    	
			        ConnectWifiActivity.pref.edit().putBoolean("IS_WEB", true).commit();
//			        	ConnectWifiActivity.WebLoginConnecting = true;
			        }
			    System.out.println("Bypassed WebLogin");
//			        System.out.println(response.toString());
			        
			    
			    } 
//			    catch (ClientProtocolException e) {
//			        // TODO Auto-generated catch block
//			    	System.out.println("1");
////			    	session.createWebSession();
////			    	ConnectWifiActivity.WebLoginConnecting = false;
//			    } 
			    catch (Exception e) {
			        // TODO Auto-generated catch block
			    	System.out.println("2");
//			    	session.createWebSession();
//			    	ConnectWifiActivity.WebLoginConnecting = false;
			    }
	    	 
			    return (long) 1;
	     }

	     protected void onPostExecute(Long result) {	    	 
	         //showDialog("Downloaded " + result + " bytes");
	    	 if(pref.getBoolean("IS_WEB", false)){
	    		 Intent intent=new Intent(WebLoginActivity.this,FBLoginActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);					
				startActivity(intent);
				finish();
	    	 }
	     }
	 }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity5, menu);
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
