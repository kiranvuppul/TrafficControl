package com.example.trafficcontrol;

import com.example.trafficcontrol.controller.BaseController;
import com.example.trafficcontrol.utils.GenericUtility;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

public class BaseActivity extends Activity 
{
	protected BaseController mController;
	public ProgressDialog mProgress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		//....Set application context.
		GenericUtility.setApplicationContext(this);
		GenericUtility.setActivity(this);
	}
	
	public void showHideProgress(final boolean showDialog) 
	{
		if (showDialog) 
		{	
			String Title = "Please wait...";
			mProgress = GenericUtility.showProgressDialog(this, Title);
		} 
		else if (mProgress != null) 
		{
			mProgress.dismiss();
		}
	}
}
