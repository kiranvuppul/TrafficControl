package com.example.trafficcontrol.controller;

import com.example.trafficcontrol.utils.Constants;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;

public class LoginController extends BaseController {
	private Context mContext = null;
	private SharedPreferences mPrefs;

	public LoginController(Context aContext) {
		this.mContext = aContext;
		mPrefs = aContext.getSharedPreferences(Constants.PREF_NAME,
				Context.MODE_PRIVATE);
	}

	public boolean isNewUser() {
		boolean ret_val = false;
		String pwd = mPrefs.getString(Constants.VOLUNTEER_PASSWORD, null);
		if (pwd == null || pwd.length() == 0)
			ret_val = true;

		return ret_val;
	}

	public boolean isValidPassword(String pwd, String confirmPwd) {
		boolean ret_val = false;
		if (pwd == null || pwd.length() == 0 || confirmPwd == null
				|| confirmPwd.length() == 0)
			Toast.makeText(mContext, "Plese Enter Valid Password.",
					Toast.LENGTH_SHORT).show();
		else if (!pwd.equals(confirmPwd))
			Toast.makeText(mContext, "Confirmation password didn't matched.",
					Toast.LENGTH_SHORT).show();
		else {
			ret_val = true;
			Editor edit = mPrefs.edit();
			edit.putString(Constants.VOLUNTEER_PASSWORD, pwd);
			edit.commit();
		}

		return ret_val;
	}

	public boolean isProfileFilled() {
		boolean ret_val = true;
		String tvId = mPrefs.getString(Constants.VOLUNTEER_TV, null);
		if (tvId == null || tvId.length() == 0)
			ret_val = false;

		return ret_val;
	}

	public boolean validateDetails(String userName, String userMail, String userNumber, String tvNum, String companyName, String location) {
		boolean ret_val = false;
		if (!tvNum.startsWith("TV"))
			Toast.makeText(mContext, "Invalid Traffic Volunteer Number",
					Toast.LENGTH_SHORT).show();
		else if (userName.equals("") || userMail.equals("")
				|| userNumber.equals("") || companyName.equals("")|| location.equals(""))
			Toast.makeText(mContext, "Don't leave Fields Empty",
					Toast.LENGTH_SHORT).show();
		else {
			Editor edit = mPrefs.edit();
			edit.putString(Constants.VOLUNTEER_NAME, userName);
			edit.putString(Constants.VOLUNTEER_MAIL_ID, userMail);
			edit.putString(Constants.VOLUNTEER_PHONE_NUMBER, userNumber);
			edit.putString(Constants.VOLUNTEER_TV, tvNum);
			edit.putString(Constants.VOLUNTEER_COMPANY, companyName);
			edit.putString(Constants.VOLUNTEER_LOCATION, location);
			edit.commit();
			ret_val = true;
			Toast.makeText(mContext, "Profile Setup Completed",
					Toast.LENGTH_SHORT).show();
		}
		return ret_val;
	}

	public boolean isPwdValid(String pwd) {
		String storedPwd = mPrefs.getString(Constants.VOLUNTEER_PASSWORD, null);
		if (storedPwd.equals(pwd)) 
		{
			Toast.makeText(mContext, "Welcome "+mPrefs.getString(Constants.VOLUNTEER_NAME, ""),
					Toast.LENGTH_SHORT).show();
			return true;
		} else
		{
			Toast.makeText(mContext, "Wrong Password Entered.",
					Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	public boolean comapreWithOldPwd(CharSequence s)
	{
		String storedPwd = mPrefs.getString(Constants.VOLUNTEER_PASSWORD, null);
		if (storedPwd.equals(s))
			return true;
		else 
			return false;
	}

	public boolean getLoginStatus()
	{
		return mPrefs.getBoolean(Constants.VOLUNTEER_LOGIN_STAT, false);
	}

	public void setloginStatus(boolean loginStat) 
	{
		Editor edit=mPrefs.edit();
		edit.putBoolean(Constants.VOLUNTEER_LOGIN_STAT, loginStat);
		edit.commit();
	}

}
