package com.example.otp.HelperClasses;

import java.util.HashMap;

import com.example.otp.OTPVerificationActivity;
 
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
 
public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;
     
    // Editor for Shared preferences
    Editor editor;
     
    // Context
    Context _context;
     
    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "GratifiPref";
     
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
     
    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";
    
    public static final String KEY_API = "api";
    
    public static final String IS_WEB = "NO";
    
    public static final String IS_OTP = "NO";
    
    // Email address (make variable public to access from outside)
    public static final String KEY_MOBILE = "mobile";
     
    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        
    }
     
    /**
     * Create login session
     * */
    public void createLoginSession(String mobile, String api){
        // Storing login value as TRUE
    	editor = pref.edit();
    	
    	editor.putBoolean(IS_LOGIN, true);        
         
        // Storing email in pref
        editor.putString(KEY_MOBILE, mobile);
        
        editor.putString(KEY_API, api);
         
        // commit changes
        editor.commit();
    }   
    public void createOTPSession(){
        // Storing login value as TRUE
    	editor = pref.edit();
    	
    	editor.putBoolean(IS_OTP,true);        
         
        // commit changes
        editor.commit();
    }   

    public void createWebSession(){
        // Storing login value as TRUE
    	editor = pref.edit();
    	
    	editor.putBoolean(IS_WEB,true);        
         
        // commit changes
        editor.commit();
    }   
    
    /**
     * Create login session
     * */
    public void updateUserName(String name){        
        // Storing name in pref
    	editor = pref.edit();
        editor.putString(KEY_NAME, name);
         
        // commit changes
        editor.commit();
    }   
     
    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, OTPVerificationActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             
            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             
            // Staring Login Activity
            _context.startActivity(i);
        }
         
    }
 
    public boolean checkWeb(){
        return pref.getBoolean("IS_Web", false);
         
    }

    public boolean checkOTP(){
        return pref.getBoolean("IS_OTP", false);
         
    }
    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
         
        // user email id
        user.put(KEY_MOBILE, pref.getString(KEY_MOBILE, null));
         
        // return user
        return user;
    }
     
    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
//        editor.clear();
//        editor.commit();
         
        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, OTPVerificationActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
         
        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         
        // Staring Login Activity
        _context.startActivity(i);
    }
     
    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
    	return pref.getBoolean(IS_LOGIN, false);
    }
}