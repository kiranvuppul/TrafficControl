package com.example.trafficcontrol;

import com.example.trafficcontrol.controller.LoginController;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

public class SplashActivity extends BaseActivity {

	private static String TAG = SplashActivity.class.getName();    
	private static long SLEEP_TIME = 2;    // Sleep for some time
	private LoginController mLoginController;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);    	// Removes title bar
        setContentView(R.layout.splash);

        mLoginController = new LoginController(getApplicationContext());
        
		IntentLauncher launcher = new IntentLauncher();       
		launcher.start();   
    }

    
    private class IntentLauncher extends Thread 
	{       
		/*
		 Sleep for some time and than start new activity.        
		 */    
		@Override 
		public void run()
		{         
			try 
			{           
				// set Sleep time.             
				Thread.sleep(SLEEP_TIME * 1000);          
			} 
			catch (Exception e)
			{            
				Log.e(TAG, e.getMessage());          
			}
			
			// Start main activity  
			Intent intent = null;
			if (mLoginController.isNewUser())
			{
				intent = new Intent(SplashActivity.this, ChangePasswordActivity.class);
				intent.putExtra("isNew", true);
			}
			else if (!mLoginController.isProfileFilled())
				intent = new Intent(SplashActivity.this, SetProfileActivity.class);
			else 
				intent = new Intent(SplashActivity.this, LoginActivity.class);
			          
			SplashActivity.this.startActivity(intent);   
			SplashActivity.this.finish();	
		}
	       
	}

}
