package com.example.otp;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.otp.HelperClasses.JSONParser;
import com.example.otp.HelperClasses.SessionManager;
import com.example.otp.OTPVerificationActivity.SendOTPRequest;
import com.example.otp.WebLoginActivity.WebLogin;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

public class CampaignActivity extends Activity {
	Button bt;
 
	//Creating JSON Parser object
	JSONParser jParser;
//	= new JSONParser(getApplicationContext());
	
	// url to send OTP request
	private static String url_campaign_request = "http://54.149.8.166/gratifi-back/v1/index.php/get_campaign_link";
		 
	private String campaign_link = "";

	private String TAG_ERROR = "error";
	
	SessionManager sessionManager;

 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		jParser = new JSONParser(getApplicationContext());
		setContentView(R.layout.campaign_layout);
		bt=(Button)findViewById(R.id.skipbutton);
		bt.setOnClickListener(new View.OnClickListener() {				
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(CampaignActivity.this,WelcomeScreenActivity.class);
				startActivity(intent);
			}
        });
		
		sessionManager = new SessionManager(getApplicationContext());
		new GetCampaign().execute();
	}
	
	
	public class GetCampaign extends AsyncTask<String, Integer, Long> {
	     protected Long doInBackground(String... url) {
	         
	    	 System.out.println("Getting Campaign");
//		    	SessionManager sessionManager = new SessionManager(getApplicationContext());
//		    	List<NameValuePair> pa = new ArrayList<NameValuePair>();
		    	List<NameValuePair> paramValuePairs = new ArrayList<NameValuePair>(2);
		    			    				    	
		    	// getting JSON string from URL
				JSONObject json = jParser.makeHttpRequest(url_campaign_request, "GET", paramValuePairs, true);
				
				// Check your log cat for JSON reponse
//				Log.d("otp request response: ", json.toString());
				if (json != null){
				try {
					// Checking for SUCCESS TAG
					Boolean error = json.getBoolean("error");
	
					if (!error) {	
						campaign_link = json.getString("campaign_link");						
						WebView myWebView = (WebView) findViewById(R.id.webview);
						myWebView.setWebViewClient(new MyWebViewClient());
						myWebView.loadUrl(campaign_link);	
					} else {
						Intent intent=new Intent(CampaignActivity.this,WelcomeScreenActivity.class);
						startActivity(intent);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Intent intent=new Intent(CampaignActivity.this,WelcomeScreenActivity.class);
					startActivity(intent);
				}		
				} 
			    return (long) 1;
	     }

	     protected void onPostExecute(Long result) {	    	 
	         
	     }
	 }

	
	private class MyWebViewClient extends WebViewClient {
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        //if (Uri.parse(url).getHost().equals("www.google.com")) {
	            // This is my web site, so do not override; let my WebView load the page
	        	view.loadUrl(url);
	        //    return false;
	        //}
	        // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
	        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
	        //startActivity(intent);
	        return true;
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity3, menu);
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
