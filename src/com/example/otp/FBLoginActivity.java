package com.example.otp;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.example.otp.HelperClasses.MainFragment;
import com.example.otp.HelperClasses.SessionManager;
import com.facebook.Session;

public class FBLoginActivity extends FragmentActivity {
	
  Button bt;
  public MainFragment mainFragment;
  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fb_login_layout);
	
		
		Session session = Session.getActiveSession();

		if (session != null && session.isOpened())
		  {			  
			  System.out.println("FB Screen Visible 1");
			  
			  goToNextActivity();
			  finish();
		  }	
		else{
					
			if (savedInstanceState == null) {
		        // Add the fragment on initial activity setup
		        mainFragment = new MainFragment();
		        getSupportFragmentManager().beginTransaction().add(android.R.id.content, mainFragment).commit();
		    } else {
		        // Or set the fragment from restored state info
		        mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);
		    }
			
		}
	}
	
	private void goToNextActivity(){
		Intent intent=new Intent(FBLoginActivity.this,CampaignActivity.class);	
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		SessionManager sessionManager = new SessionManager(getApplicationContext());
		if(FBLoginActivity.this.mainFragment != null){
			sessionManager.updateUserName(FBLoginActivity.this.mainFragment.user.getFirstName().toString());
		}
		//intent.putExtra("USER_NAME", Activity2.this.mainFragment.user.getFirstName());
		startActivity(intent);
	}
		
	@Override
    public void onResume() {
        super.onResume();
        
        Session session = Session.getActiveSession();
		  if (session != null && session.isOpened() && FBLoginActivity.this.mainFragment != null 
				  && FBLoginActivity.this.mainFragment.user != null)
		  {	  
			  Log.i("MainFragment", "Logged in... Main Activity" + FBLoginActivity.this.mainFragment.user.getFirstName() );
			  goToNextActivity();
		  }		  
    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	  
	  Session session = Session.getActiveSession();
	  if (session != null && session.isOpened() && FBLoginActivity.this.mainFragment.user != null)
	  {	  
		  Log.i("MainFragment", "Logged in... Main Activity");
		  goToNextActivity();
	  }	  
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity2, menu);
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
