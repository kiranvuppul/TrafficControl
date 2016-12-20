package com.example.trafficcontrol;

import com.example.trafficcontrol.controller.LoginController;
import com.example.trafficcontrol.controller.TrfController;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends BaseActivity 
{
	private EditText etPassword;
	private LoginController mLoginController;
	@Override
    protected void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);    	// Removes title bar
        setContentView(R.layout.login);							//....Set Content.
        
		
        etPassword=(EditText)findViewById(R.id.etPassword);
        
        //....Set Control events.
        SetControlEvent();
        
        //....Set controller.
        mLoginController = new LoginController(getApplicationContext());
        
        if (mLoginController.getLoginStatus())
        {
        	Intent intent = new Intent(LoginActivity.this,
					MainActivity.class);
			LoginActivity.this.startActivity(intent);
			LoginActivity.this.finish();
        }
    }
	
	private void SetControlEvent() {
		Button loginBtn = (Button) findViewById(R.id.btnLogin);
		loginBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) 
			{
				if (mLoginController
						.isPwdValid(etPassword.getText().toString())) 
				{
					mLoginController.setloginStatus(true);
					Intent intent = new Intent(LoginActivity.this,
							MainActivity.class);
					LoginActivity.this.startActivity(intent);
					LoginActivity.this.finish();
					
					(new TrfController()).addSignInTimeStamp();
				}
			}
		});
		Button setUpBtn = (Button) findViewById(R.id.btnSetup);
		setUpBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
					Intent intent = new Intent(LoginActivity.this,
							SetProfileActivity.class);
					LoginActivity.this.startActivity(intent);
			}
		});
		Button changePwd = (Button) findViewById(R.id.btnChngPwd);
		changePwd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,
						ChangePasswordActivity.class);
				intent.putExtra("isNew", false);
				LoginActivity.this.startActivity(intent);
			}
		});
	}
	
	

}
