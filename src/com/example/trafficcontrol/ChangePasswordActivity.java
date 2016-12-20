package com.example.trafficcontrol;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.trafficcontrol.controller.LoginController;
import com.example.trafficcontrol.utils.Constants;

public class ChangePasswordActivity extends BaseActivity
{
	@SuppressWarnings("unused")
	private SharedPreferences mPrefs;

	private EditText enterPwd,reEnterPwd,oldPassword;
	private Button setPwdBtn;
	private LoginController mLoginController;
	private boolean isNew;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);    	// Removes title bar
		setContentView(R.layout.change_password);
		
        mPrefs = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
		
		enterPwd=(EditText)findViewById(R.id.password);
		reEnterPwd=(EditText)findViewById(R.id.rePassword);
		oldPassword=(EditText)findViewById(R.id.oldPassword);
		setPwdBtn=(Button)findViewById(R.id.setPassword);
		isNew=getIntent().getBooleanExtra("isNew", false);
		oldPassword.setVisibility(isNew?View.GONE:View.VISIBLE);
		if (!isNew)
		{
			enterPwd.setHint(R.string.str_enter_new_password);
			setPwdBtn.setText(R.string.str_reset_password);
		}
		else
		{
			enterPwd.setHint(R.string.str_enter_password);
			setPwdBtn.setText(R.string.str_create_account);
		}
		
		mLoginController=new LoginController(getApplicationContext());
		
		SetControlEvent();
	}
	
	private void SetControlEvent()
	{
		setPwdBtn.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				if (!isNew) {
					if (oldPassword.getText().toString().equals(""))
						Toast.makeText(getApplicationContext(),
								"Please enter your old password",
								Toast.LENGTH_SHORT).show();
					else if (!mLoginController.comapreWithOldPwd(oldPassword
							.getText().toString())) {
						oldPassword.setText("");
						oldPassword.requestFocus();
						Toast.makeText(getApplicationContext(),
								"Please enter your old password correctly.",
								Toast.LENGTH_SHORT).show();
						return;
					}
				}
				if (mLoginController.isValidPassword(enterPwd.getText().toString(),reEnterPwd.getText().toString()))
				{
					Toast.makeText(getApplicationContext(), "Password Setup completed", Toast.LENGTH_SHORT).show();
					if (mLoginController.isProfileFilled())
					{
						Intent intent = new Intent(ChangePasswordActivity.this,
								MainActivity.class);
						startActivity(intent);
						ChangePasswordActivity.this.finish();
					}
					else
					{
						Toast.makeText(getApplicationContext(), "Please Complete initial Setup", Toast.LENGTH_SHORT).show();
						Intent intent=new Intent(getApplicationContext(), SetProfileActivity.class);
						startActivity(intent);
						ChangePasswordActivity.this.finish();
					}
				}
			}
		});
	}
}
