package com.example.otp;

import java.util.List;

import com.example.otp.HelperClasses.SessionManager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.widget.Toast;

public class ScanService extends Service {   

	

	public static WifiManager wifiManager;
	WifiReceiver receiverWifi;
	private static final int NOTIFICATION_ID=1;
	private List<ScanResult> mScanResults;
	SessionManager session;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
    	wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//    	Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();
    }

    
	private void StartWiFiScanning(){
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
    public void onDestroy() {
        
//    	Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart(Intent intent, int startid) {
    	
    	
		StartWiFiScanning();
//    	Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show();
    }
    
    
	class WifiReceiver extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
				mScanResults = wifiManager.getScanResults();
                
				// Wait for connection
				final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				if (wifiInfo != null) {
					final SupplicantState state = wifiInfo.getSupplicantState();
					final NetworkInfo.DetailedState detailedState = WifiInfo
							.getDetailedStateOf(state);
					
//					if((detailedState == NetworkInfo.DetailedState.SCANNING || detailedState == NetworkInfo.DetailedState.OBTAINING_IPADDR)   
//							&& wifiInfo.getSSID().equals("\"MyHotspot\"")){
//						wifiLoginSuccessful = true;
//						System.out.println("wifiLoginSuccessful");
//						Toast.makeText(getApplicationContext(), "SUCCESSFUL", Toast.LENGTH_SHORT).show();
//						
//					}

						for(ScanResult result : mScanResults) {
							System.out.println(result.SSID.toString());
							if (result.SSID.toString().equals("MyHotspot")) {
								 
								final NotificationManager mgr=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
								Notification notification=new Notification(R.drawable.ic_launcher,"Gratifi Network Spotted!", System.currentTimeMillis());
								Intent intentback = new Intent(getApplicationContext(), ConnectWifiActivity.class);
								PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(), 0, intentback, 0);
								notification.setLatestEventInfo(getApplicationContext(), "Gratifi Network Available", "Click to enjoy free Wifi Service ", pendingIntent);
								mgr.notify(NOTIFICATION_ID,notification);
							
							}
						}
					
					}

				
			}
		}

	}

}