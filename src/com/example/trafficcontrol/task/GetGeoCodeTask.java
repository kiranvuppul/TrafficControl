package com.example.trafficcontrol.task;

import java.io.IOException;
import java.util.List;

import com.example.trafficcontrol.MainActivity;
import com.example.trafficcontrol.MsgListActivity;
import com.example.trafficcontrol.utils.GenericUtility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;

public class GetGeoCodeTask extends AsyncTask<Object, Void, Void>
{

	private ProgressDialog statusDialog;
	private Activity geocodeActivity;
	private StringBuilder strReturnedAddress = new StringBuilder();

	public GetGeoCodeTask(Activity activity) 
	{
		geocodeActivity = activity;
	}

	protected void onPreExecute() 
	{
		statusDialog = new ProgressDialog(geocodeActivity);
		statusDialog.setMessage("Getting address...");
		statusDialog.setIndeterminate(false);
		statusDialog.setCancelable(false);
		statusDialog.show();
	}

	@Override
	protected Void doInBackground(Object... params) 
	{
		Location mLocation = (Location)params[0];
		
    	//....Set Geocode.
		Geocoder geocoder = new Geocoder(geocodeActivity);
    	Address returnedAddress = null;

    	if(mLocation != null)
    	{
        	
	        try 
	        {        	
	        	List<Address> addresses = geocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
	        	if(addresses != null && addresses.size() > 0) 
	        	{
	        		returnedAddress = addresses.get(0);
	        	
		        	if(returnedAddress.getMaxAddressLineIndex()!=0)
		        	{
		        	    for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) 
		        	    	strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
		        	}
		        	else
		        	{
		        		if(!GenericUtility.isStringBlank(returnedAddress.getFeatureName()))
		        			strReturnedAddress.append(returnedAddress.getFeatureName());
		        	}
		        	
		        	//statusDialog.setMessage("Address fetched...");
	        	}
	        } 
	        catch (IOException e) 
	        {
	        	//statusDialog.setMessage("address not fetched...");
	        } 
    	}
    	
		return null;
	}

	@Override
	protected void onPostExecute(Void result) 
	{
		//....Get address and create mail.
		if(geocodeActivity.getClass().toString().contains("MainActivity"))
			((MainActivity)geocodeActivity).getAddress_CreateMail(strReturnedAddress);
		else if(geocodeActivity.getClass().toString().contains("MsgListActivity"))
			((MsgListActivity)geocodeActivity).getAddress_CreateMail(strReturnedAddress);
		
		statusDialog.dismiss();
		
		super.onPostExecute(result);

	}
}
