package com.example.otp;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.otp.HelperClasses.ReenableAllApsWhenNetworkStateChanged;
import com.example.otp.HelperClasses.SessionManager;

public class ConnectWifiActivity extends Activity implements OnClickListener {
	static final int SECURITY_NONE = 0;
	static final int SECURITY_WEP = 1;
	static final int SECURITY_PSK = 2;
	static final int SECURITY_EAP = 3;
	public boolean internet_process = false;
	ProgressDialog progress;
	public static boolean wifi_connect = false;
	public static final String spssid = "spssidhotspot"; 
	public static final String spbssid = null; 
	public static final String MyPREFERENCES = "MyPrefs" ;
	enum PskType {
		UNKNOWN, WPA, WPA2, WPA_WPA2
	}

	private static final int MAX_PRIORITY = 99999;
	ProgressDialog progressDialog;
	WifiConfiguration wc = new WifiConfiguration();

	public static WifiManager wifiManager;
	WifiReceiver receiverWifi;
	private List<ScanResult> mScanResults;
	public static boolean LoginSuccessful = false;
	public static boolean WebLoginConnecting = false;
	Button tryAgainButton, findHotspotsButton;
	ImageView noWifiText, switchOnText, connectingText, logo, sponsor;
	ProgressBar progressBar;
	Button bt;
	TextView un, pw;
	SessionManager session;
	Timer timer;
	MyTimerTask myTimerTask;
	public static SharedPreferences pref;

	//	public static SharedPreferences sharedpref;
	private static boolean wifiLoginSuccessful = false;
	boolean is_internet = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connect_wifi_layout);
		//		ConnectWifiActivity.pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		ConnectWifiActivity.pref = this.getSharedPreferences("MyPrefs",MODE_WORLD_READABLE);
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		session = new SessionManager(getApplicationContext());
		//Initialize the UI elements
		noWifiText = (ImageView)findViewById(R.id.nowifitext);
		switchOnText = (ImageView)findViewById(R.id.switchontext);
		connectingText = (ImageView)findViewById(R.id.connectingtext);
		logo = (ImageView)findViewById(R.id.logo);
		sponsor = (ImageView)findViewById(R.id.sponsor);
		//		tryAgainButton = (Button)findViewById(R.id.tryagainbutton);
		findHotspotsButton = (Button)findViewById(R.id.findhotspotsbutton);
		progressBar = (ProgressBar)findViewById(R.id.progressBar);	

		findHotspotsButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Scanning for Networks", Toast.LENGTH_LONG).show();
				wifiManager.setWifiEnabled(true);
				StartWiFiScanning();
			}
		});
		//		timer = new Timer();
		//	    myTimerTask = new MyTimerTask();
		//	    timer.schedule(myTimerTask, 5000);
		//	

		//		findHotspotsButton.setOnClickListener(new View.OnClickListener() {			
		//			@Override
		//			public void onClick(View v) {				
		//			}
		//        });
		//		if (!session.isLoggedIn())
		progress = progressDialog.show(ConnectWifiActivity.this, "Checking Connectivity", "Please Wait ...", true);
		progress.setCancelable(false);
		new CheckInternet().execute();
		CheckForWiFi();
	}



	private void CheckForWiFi(){		
		if(!wifiManager.isWifiEnabled()){			
			switchOnText.setVisibility(View.VISIBLE);			
			findHotspotsButton.setVisibility(View.VISIBLE);				
		}
		else{
			StartWiFiScanning();
		}
	}

	private void StartWiFiScanning(){
		progressDialog = ProgressDialog.show(ConnectWifiActivity.this, "Please wait",  "Searching For Networks ...", true);
		progressDialog.setCancelable(false);

		receiverWifi = new WifiReceiver();

		// Register broadcast receiver
		// Broacast receiver will automatically call when number of wifi connections changed
		registerReceiver(receiverWifi, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		wifiManager.startScan();

		/*
		 * wifiManager.setWifiEnabled(true); wifiManager.startScan();
		 * wc.SSID="\"myssid\""; wc.preSharedKey = "\"mypwd\"";
		 * wc.status = WifiConfiguration.Status.ENABLED;
		 */
	}

	@Override
	protected void onPause() {
		super.onPause();		
	}

	@Override
	protected void onResume() {

		super.onResume();
	}

	class WifiReceiver extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
				mScanResults = wifiManager.getScanResults();
				System.out.println("onReceive");

				// Wait for connection
				final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				if (wifiInfo != null) {
					System.out.println("SSIDthis" + wifiInfo.getSSID().toString());
					final SupplicantState state = wifiInfo.getSupplicantState();
					final NetworkInfo.DetailedState detailedState = WifiInfo
							.getDetailedStateOf(state);
					System.out.println("state - " + detailedState.toString());

					//					if((detailedState == NetworkInfo.DetailedState.SCANNING || detailedState == NetworkInfo.DetailedState.OBTAINING_IPADDR)   
					//							&& wifiInfo.getSSID().equals("\"MyHotspot\"")){
					//						wifiLoginSuccessful = true;
					//						System.out.println("wifiLoginSuccessful");
					//						Toast.makeText(getApplicationContext(), "SUCCESSFUL", Toast.LENGTH_SHORT).show();
					//						
					//					}

					if (!wifi_connect) {
						System.out.println("Enter");
						for(ScanResult result : mScanResults) {
							System.out.println(result.SSID.toString());
							if (result.SSID.toString().equals("MyHotspot")) {
								Toast.makeText(getApplicationContext(), "Gratifi Network Found", Toast.LENGTH_LONG).show();
								wifiLoginSuccessful = ConnectWiFi(result);
								wifi_connect = true;
								progressDialog.dismiss();
							}
						}
						if (!wifi_connect){
							findHotspotsButton.setVisibility(View.VISIBLE);
							unregisterReceiver(receiverWifi);
							progressDialog.dismiss();
							Toast.makeText(getApplicationContext(), "Look For Nearby Hotspots", Toast.LENGTH_SHORT).show();
						}

					}

					if((detailedState == NetworkInfo.DetailedState.CONNECTED || detailedState == NetworkInfo.DetailedState.OBTAINING_IPADDR || detailedState == NetworkInfo.DetailedState.CONNECTING)   
							&& wifiInfo.getSSID().equals("\"MyHotspot\"")  && !pref.getBoolean("IS_WEB", false) && !pref.getBoolean("IS_OTP", false)){
						Intent intent1 = new Intent(ConnectWifiActivity.this,OTPVerificationActivity.class);
						intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
						intent1.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);				
						startActivity(intent1);				
						unregisterReceiver(receiverWifi);
					}
					wifiManager.startScan();
				}
				else{
					System.out.println("No Wifi connected");
				}

			}
		}

	}
	class MyTimerTask extends TimerTask {

		@Override
		public void run() {
			timer.cancel();
			timer = null;		     


			if (ConnectWifiActivity.pref.getBoolean("IS_WIFI", false)){
				Toast.makeText(getApplicationContext(), "Look For Nearby Hotpots", Toast.LENGTH_LONG).show();
			}
		}		  
	}
	@Override
	protected void onDestroy(){

		super.onDestroy();
	}

	
	private class CheckInternet extends AsyncTask<Void, Void, Boolean>{
		
		@Override
		protected Boolean doInBackground(Void... params) {
			ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

		      
	        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

			
			if (netInfo != null && netInfo.isConnected())
			{
			    try
			    {
			        HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
			        urlc.setRequestProperty("User-Agent", "Test");
			        urlc.setRequestProperty("Connection", "close");
			        urlc.setConnectTimeout(3000); //choose your own timeframe
			        urlc.setReadTimeout(4000); //choose your own timeframe
			        urlc.connect();
			       if (urlc.getResponseCode() == 200){
			    	   is_internet = true;
			       }
			    } catch (IOException e)
			    {
			      is_internet = false;
			      return false;
			    }
			} 
			else
			{
				 is_internet = false;
				 return false;
			}
			return true;
		}
		
		protected void onPostExecute(Boolean result){
			if (result == true){
			internet_process = true;
			progress.dismiss();
			
				
			boolean otpcheck = ConnectWifiActivity.pref.getBoolean("IS_OTP", false);
			boolean webcheck = ConnectWifiActivity.pref.getBoolean("IS_WEB", false);
			System.out.println(otpcheck);
			System.out.println(wifiManager.getConnectionInfo().getSSID());
			System.out.println(WebLoginConnecting);
			
			
		    if(wifiManager.getConnectionInfo().getSSID().equals("\"MyHotspot\"") && webcheck && otpcheck){
		    	   System.out.println("Already logged in");
		    	   Intent intent=new Intent(ConnectWifiActivity.this,WelcomeScreenActivity.class);					
				//	        	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
			//		   else{
			else if ((wifiManager.getConnectionInfo().getSSID().equals("\"MyHotspot\"") && !otpcheck) || (is_internet && !otpcheck)){
				Intent intent1 = new Intent(getApplicationContext(),OTPVerificationActivity.class);
				//					intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				//					intent1.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(intent1);
				//					unregisterReceiver(receiverWifi);
			}
			else if (wifiManager.getConnectionInfo().getSSID().equals("\"MyHotspot\"") && otpcheck && !webcheck){
				Intent intent1 = new Intent(getApplicationContext(),WebLoginActivity.class);
				intent1.putExtra("location_id",wifiManager.getConnectionInfo().getBSSID());

				//					intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);					
				startActivity(intent1);
				//					unregisterReceiver(receiverWifi);
			}

			}
			else{
				ConnectWifiActivity.this.runOnUiThread(new Runnable() {
				    public void run() {
				    	internet_process = true;
				    	progress.dismiss();
				    	Toast.makeText(ConnectWifiActivity.this, "Error Checking Internet Connectivity", Toast.LENGTH_SHORT).show();
				    }
				});
			}
		
		}
		
	}
	private class ConnectWebLogin extends AsyncTask<String, Integer, Long> {
		protected Long doInBackground(String... url) {

			System.out.println("Trying Login");
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();

			HttpPost httppost = new HttpPost("http://192.168.137.1/log.html");

			try {
				// Add your data
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("_pass", "12345678"));
				nameValuePairs.add(new BasicNameValuePair("_action", "freelogin"));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);
				LoginSuccessful = true;
				System.out.println(response.toString());

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				System.out.println("1");
				LoginSuccessful = false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("2");
				LoginSuccessful = false;
			}

			//	    	 int count = urls.length;
			//	         long totalSize = 0;
			//	         for (int i = 0; i < count; i++) {
			//	             totalSize += Downloader.downloadFile(urls[i]);
			//	             //publishProgress((int) ((i / (float) count) * 100));
			//	             // Escape early if cancel() is called
			//	             if (isCancelled()) break;
			//	         }
			return (long) 1;
		}

		//	     protected void onProgressUpdate(Integer... progress) {
		//	         setProgressPercent(progress[0]);
		//	     }

		protected void onPostExecute(Long result) {	    	 
			//showDialog("Downloaded " + result + " bytes");
		}
	}


	public static String convertToQuotedString(String string) {
		if (TextUtils.isEmpty(string)) {
			return "";
		}

		final int lastPos = string.length() - 1;
		if (lastPos > 0
				&& (string.charAt(0) == '"' && string.charAt(lastPos) == '"')) {
			return string;
		}

		return "\"" + string + "\"";
	}

	public void setupSecurity(WifiConfiguration config, String security,
			String password) {
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();

		final int sec = security == null ? SECURITY_NONE : Integer
				.valueOf(security);
		final int passwordLen = password == null ? 0 : password.length();
		switch (sec) {
		case SECURITY_NONE:
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			break;

		case SECURITY_WEP:
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
			if (passwordLen != 0) {
				// WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
				if ((passwordLen == 10 || passwordLen == 26 || passwordLen == 58)
						&& password.matches("[0-9A-Fa-f]*")) {
					config.wepKeys[0] = password;
				} else {
					config.wepKeys[0] = '"' + password + '"';
				}
			}
			break;

		case SECURITY_PSK:
			config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
			if (passwordLen != 0) {
				if (password.matches("[0-9A-Fa-f]{64}")) {
					config.preSharedKey = password;
				} else {
					config.preSharedKey = '"' + password + '"';
				}
			}
			break;

		case SECURITY_EAP:
			config.allowedKeyManagement.set(KeyMgmt.WPA_EAP);
			config.allowedKeyManagement.set(KeyMgmt.IEEE8021X);
			// config.eap.setValue((String)
			// mEapMethodSpinner.getSelectedItem());
			//
			// config.phase2.setValue((mPhase2Spinner.getSelectedItemPosition()
			// == 0) ? "" :
			// "auth=" + mPhase2Spinner.getSelectedItem());
			// config.ca_cert.setValue((mEapCaCertSpinner.getSelectedItemPosition()
			// == 0) ? "" :
			// KEYSTORE_SPACE + Credentials.CA_CERTIFICATE +
			// (String) mEapCaCertSpinner.getSelectedItem());
			// config.client_cert.setValue((mEapUserCertSpinner.getSelectedItemPosition()
			// == 0) ?
			// "" : KEYSTORE_SPACE + Credentials.USER_CERTIFICATE +
			// (String) mEapUserCertSpinner.getSelectedItem());
			// config.private_key.setValue((mEapUserCertSpinner.getSelectedItemPosition()
			// == 0) ?
			// "" : KEYSTORE_SPACE + Credentials.USER_PRIVATE_KEY +
			// (String) mEapUserCertSpinner.getSelectedItem());
			// config.identity.setValue((mEapIdentityView.length() == 0) ? "" :
			// mEapIdentityView.getText().toString());
			// config.anonymous_identity.setValue((mEapAnonymousView.length() ==
			// 0) ? "" :
			// mEapAnonymousView.getText().toString());
			// if (mPasswordView.length() != 0) {
			// config.password.setValue(mPasswordView.getText().toString());
			// }
			break;

		default:
			Log.e("WIFI", "Invalid security type: " + sec);
		}
	}

	private static int getSecurity(WifiConfiguration config) {
		if (config.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
			return SECURITY_PSK;
		}
		if (config.allowedKeyManagement.get(KeyMgmt.WPA_EAP)
				|| config.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {
			return SECURITY_EAP;
		}
		return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
	}

	private static int getSecurity(ScanResult result) {
		if (result.capabilities.contains("WEP")) {
			return SECURITY_WEP;
		} else if (result.capabilities.contains("PSK")) {
			return SECURITY_PSK;
		} else if (result.capabilities.contains("EAP")) {
			return SECURITY_EAP;
		}
		return SECURITY_NONE;
	}

	private static int getMaxPriority(final WifiManager wifiManager) {
		final List<WifiConfiguration> configurations = wifiManager
				.getConfiguredNetworks();
		int pri = 0;
		for (final WifiConfiguration config : configurations) {
			if (config.priority > pri) {
				pri = config.priority;
			}
		}
		return pri;
	}

	public static WifiConfiguration getWifiConfiguration(
			final WifiManager wifiMgr, final WifiConfiguration configToFind,
			String security) {
		final String ssid = configToFind.SSID;
		if (ssid.length() == 0) {
			return null;
		}

		final String bssid = configToFind.BSSID;

		if (security == null) {
			security = String.valueOf(getSecurity(configToFind));
		}

		final List<WifiConfiguration> configurations = wifiMgr
				.getConfiguredNetworks();

		for (final WifiConfiguration config : configurations) {
			if (config.SSID == null || !ssid.equals(config.SSID)) {
				continue;
			}
			if (config.BSSID == null || bssid == null
					|| bssid.equals(config.BSSID)) {
				final String configSecurity = String
						.valueOf(getSecurity(config));
				if (security.equals(configSecurity)) {
					return config;
				}
			}
		}
		return null;
	}

	private static void sortByPriority(
			final List<WifiConfiguration> configurations) {
		java.util.Collections.sort(configurations,
				new Comparator<WifiConfiguration>() {

			@Override
			public int compare(WifiConfiguration object1,
					WifiConfiguration object2) {
				return object1.priority - object2.priority;
			}
		});
	}

	private static int shiftPriorityAndSave(final WifiManager wifiMgr) {
		final List<WifiConfiguration> configurations = wifiMgr
				.getConfiguredNetworks();
		sortByPriority(configurations);
		final int size = configurations.size();
		for (int i = 0; i < size; i++) {
			final WifiConfiguration config = configurations.get(i);
			config.priority = i;
			wifiMgr.updateNetwork(config);
		}
		wifiMgr.saveConfiguration();
		return size;
	}

	public boolean ConnectWiFi(ScanResult result) {
		// if(result. mIsOpenNetwork) {
		// connResult = Wifi.connectToNewNetwork(mFloating, mWifiManager,
		// mScanResult, null, mNumOpenNetworksKept);
		// } else {
		// String ClassPassword = new String("09876512345543210123456103");

		String security = String.valueOf(getSecurity(result));
		String password = "12345678";

		wc.SSID = convertToQuotedString("MyHotspot");
		wc.BSSID = result.BSSID;
		setupSecurity(wc, security, password);
		// wc.preSharedKey = "12345678";
		// wc.status = WifiConfiguration.Status.ENABLED;

		int id = -1;
		try {
			id = wifiManager.addNetwork(wc);
		} catch (NullPointerException e) {
			Log.e("WIFI", "Weird!! Really!! What's wrong??", e);
			// Weird!! Really!!
			// This exception is reported by user to Android Developer
			// Console(https://market.android.com/publish/Home)
		}

		if (!wifiManager.saveConfiguration()) {
			return false;
		}

		wc = getWifiConfiguration(wifiManager, wc, security);
		if (wc == null) {
			return false;
		}

		int oldPri = wc.priority;
		// Make it the highest priority.
		int newPri = getMaxPriority(wifiManager) + 1;
		if (newPri > MAX_PRIORITY) {
			newPri = shiftPriorityAndSave(wifiManager);
			wc = getWifiConfiguration(wifiManager, wc, security);
			if (wc == null) {
				return false;
			}
		}

		wc.priority = newPri;

		int networkId = wifiManager.updateNetwork(wc);
		if (networkId == -1) {
			return false;
		}

		// Do not disable others
		if (!wifiManager.enableNetwork(networkId, false)) {
			wc.priority = oldPri;
			return false;
		}

		if (!wifiManager.saveConfiguration()) {
			wc.priority = oldPri;
			return false;
		}

		// We have to retrieve the WifiConfiguration after save.
		wc = getWifiConfiguration(wifiManager, wc, security);
		if (wc == null) {
			return false;
		}

		ReenableAllApsWhenNetworkStateChanged.schedule(this);

		if (!wifiManager.enableNetwork(wc.networkId, true)) {
			return false;
		}

		return true;
		// }
	}

	public void connect() {
		System.out.println("Web Login");
		un = new TextView(this);
		pw = new TextView(this);
		un.setText("nancy");
		pw.setText("123");

		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("_pass", "12345678"));
		postParameters.add(new BasicNameValuePair("_action", "freelogin"));

		//		postParameters.add(new BasicNameValuePair("username", un.toString()));
		//		postParameters.add(new BasicNameValuePair("password", pw.toString()));

		String response = null;
		try {
			response = CustomHttpClient.executeHttpPost("http://192.168.137.1/log.html",
					postParameters);
			String res = response.toString();
			res = res.replaceAll("\\s+", "");
			if (res.equals("1"))
				Toast.makeText(getApplicationContext(), "right username", 10)
				.show();

			else
				Toast.makeText(getApplicationContext(), "incorrect username",
						10).show();
		} catch (Exception e) {
			un.setText(e.toString());
		}

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

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

	public static class CustomHttpClient {
		/** The time it takes for our client to timeout */
		public final static int HTTP_TIMEOUT = 30 * 1000; // milliseconds

		/** Single instance of our HttpClient */
		private static HttpClient mHttpClient;

		/**
		 * Get our single instance of our HttpClient object.
		 * 
		 * @return an HttpClient object with connection parameters set
		 */
		private static HttpClient getHttpClient() {
			if (mHttpClient == null) {
				mHttpClient = new DefaultHttpClient();
				//				final HttpParams params = mHttpClient.getParams();
				//				HttpConnectionParams.setConnectionTimeout(params, HTTP_TIMEOUT);
				//				HttpConnectionParams.setSoTimeout(params, HTTP_TIMEOUT);
				//				ConnManagerParams.setTimeout(params, HTTP_TIMEOUT);
			}
			return mHttpClient;
		}

		/**
		 * Performs an HTTP Post request to the specified url with the specified
		 * parameters.
		 * 
		 * @param url
		 *            The web address to post the request to
		 * @param postParameters
		 *            The parameters to send via the request
		 * @return The result of the request
		 * @throws Exception
		 */
		public static String executeHttpPost(String url,
				ArrayList<NameValuePair> postParameters) throws Exception {
			BufferedReader in = null;
			try {
				HttpClient client = getHttpClient();
				HttpPost request = new HttpPost(url);
				UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
						postParameters);
				request.setEntity(formEntity);
				HttpResponse response = client.execute(request);
				in = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));

				StringBuffer sb = new StringBuffer("");
				String line = "";
				String NL = System.getProperty("line.separator");
				while ((line = in.readLine()) != null) {
					sb.append(line + NL);
				}
				in.close();

				String result = sb.toString();
				return result;
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		/**
		 * Performs an HTTP GET request to the specified url.
		 * 
		 * @param url
		 *            The web address to post the request to
		 * @return The result of the request
		 * @throws Exception
		 */
		public String executeHttpGet(String url) throws Exception {
			BufferedReader in = null;
			try {
				HttpClient client = getHttpClient();
				HttpGet request = new HttpGet();
				request.setURI(new URI(url));
				HttpResponse response = client.execute(request);
				in = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));

				StringBuffer sb = new StringBuffer("");
				String line = "";
				String NL = System.getProperty("line.separator");
				while ((line = in.readLine()) != null) {
					sb.append(line + NL);
				}
				in.close();

				String result = sb.toString();
				return result;
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

}

