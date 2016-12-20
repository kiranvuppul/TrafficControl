package com.example.trafficcontrol;

import com.example.trafficcontrol.controller.LoginController;
import com.example.trafficcontrol.utils.Constants;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetProfileActivity extends BaseActivity implements
		OnClickListener, TextWatcher {
	private EditText name, mailId, phoneNumber, tvNumber,comapnyName,locationET;
	private Button setProfile, resetBtn;
	private LoginController mLoginController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);    	// Removes title bar
		setContentView(R.layout.setup_profile);
		
		name = (EditText) findViewById(R.id.userName);
		mailId = (EditText) findViewById(R.id.userMailId);
		phoneNumber = (EditText) findViewById(R.id.userNumber);
		tvNumber = (EditText) findViewById(R.id.userTvID);
		comapnyName = (EditText) findViewById(R.id.userCompany);
		locationET = (EditText) findViewById(R.id.userLocation);
		
		setProfile = (Button) findViewById(R.id.setProfile);
		resetBtn = (Button) findViewById(R.id.resetBtn);

		mLoginController = new LoginController(getApplicationContext());

		name.addTextChangedListener(this);
		mailId.addTextChangedListener(this);
		phoneNumber.addTextChangedListener(this);
		comapnyName.addTextChangedListener(this);
		tvNumber.addTextChangedListener(this);
		locationET.addTextChangedListener(this);
		
		setProfile.setOnClickListener(this);
		resetBtn.setOnClickListener(this);
		
		loadProfileVals();
	}
	
	private void loadProfileVals()
    {
    	SharedPreferences mPrefs = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    	
		name = (EditText) findViewById(R.id.userName);
		mailId = (EditText) findViewById(R.id.userMailId);
		phoneNumber = (EditText) findViewById(R.id.userNumber);
		tvNumber = (EditText) findViewById(R.id.userTvID);
		comapnyName = (EditText) findViewById(R.id.userCompany);
		
    	String strname = mPrefs.getString(Constants.VOLUNTEER_NAME, "");
    	String strmailid = mPrefs.getString(Constants.VOLUNTEER_MAIL_ID, "");
    	String strphone = mPrefs.getString(Constants.VOLUNTEER_PHONE_NUMBER, "");
    	String strId = mPrefs.getString(Constants.VOLUNTEER_TV, "");
    	String strcomp = mPrefs.getString(Constants.VOLUNTEER_COMPANY, "");
    	String strloc = mPrefs.getString(Constants.VOLUNTEER_LOCATION, "");
    	
    	name.setText(strname);
    	mailId.setText(strmailid);
    	phoneNumber.setText(strphone);
    	tvNumber.setText(strId);
    	comapnyName.setText(strcomp);
    	locationET.setText(strloc);
    }

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.setProfile:
			String userName = name.getText().toString();
			String userMail = mailId.getText().toString();
			String userNumber = phoneNumber.getText().toString();
			String tvNum = tvNumber.getText().toString();
			String companyName=comapnyName.getText().toString();
			String location=comapnyName.getText().toString();
			if (mLoginController.validateDetails(userName, userMail, userNumber, tvNum,companyName,location))
			{
				Intent intent = new Intent(SetProfileActivity.this, LoginActivity.class);
				SetProfileActivity.this.startActivity(intent);   
				SetProfileActivity.this.finish();
			}
			break;
		case R.id.resetBtn:
			name.setText("");
			mailId.setText("");
			phoneNumber.setText("");
			tvNumber.setText("");
			comapnyName.setText("");
			locationET.setText("");
			name.setHint(R.string.str_enter_name);
			mailId.setHint(R.string.str_enter_mail_id);
			phoneNumber.setHint(R.string.str_enter_phone_number);
			tvNumber.setHint(R.string.str_enter_TV);
			comapnyName.setHint(R.string.str_enter_company_name);
			locationET.setHint(R.string.str_enter_location);
			break;
		default:
			break;
		}
	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count)
	{
		if (s.equals(""))
			Toast.makeText(getApplicationContext(), "This feild cann't be empty", Toast.LENGTH_SHORT).show();
	}
}
