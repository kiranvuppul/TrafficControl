package com.example.trafficcontrol;

import java.io.File;
import java.util.ArrayList;

import com.example.trafficcontrol.MainActivity.MyLocationListener;
import com.example.trafficcontrol.adapter.MsgAdapter;
import com.example.trafficcontrol.controller.TrfController;
import com.example.trafficcontrol.model.TrfcViaolator;
import com.example.trafficcontrol.model.User;
import com.example.trafficcontrol.task.GetGeoCodeTask;
import com.example.trafficcontrol.utils.Constants;
import com.example.trafficcontrol.utils.GenericUtility;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class MsgListActivity extends BaseActivity
{
	private TrfcViaolator mTrfcviolator = null;
    private User mUser = new User();
	private SharedPreferences mPrefs;
	
	// constant for requesting picture from camera
	private static final int REQUEST_CODE_SEND_MAIL = 22;
	
	// location manager;
    private LocationManager locManager;
    private LocationListener locListener = new MyLocationListener();
    private Location mLocation;
    
    private boolean gps_enabled = false;
    private boolean network_enabled = false;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) 
    {
		super.onCreate(savedInstanceState);
        
        //....Set Content.
        setContentView(R.layout.msg_list);
        
        //....Set LocationManager.
        locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        
        try 
        {
            network_enabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } 
        catch (Exception ex) 
        {
        	
        }
        
        if (gps_enabled) 
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
        
        if (network_enabled) 
            locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListener);
        
        
		//....Set controller.
		mController = new TrfController();
        mPrefs = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        GenericUtility.list_sel = -1;
        
        //....Set Adapter.
        getList();
        
        //....Set Control events.
        setControlEvent();
        
        //....Set User control.
        loadUser();
    }
	@Override
	public View onCreateView(View parent, String name, Context context, AttributeSet attrs) 
	{
		return super.onCreateView(parent, name, context, attrs);
	}
	
    private void loadUser()
    {
    	String name = mPrefs.getString(Constants.VOLUNTEER_NAME, "");
    	String mailid = mPrefs.getString(Constants.VOLUNTEER_MAIL_ID, "");
    	String phone = mPrefs.getString(Constants.VOLUNTEER_PHONE_NUMBER, "");
    	String Id = mPrefs.getString(Constants.VOLUNTEER_TV, "");
    	String comp = mPrefs.getString(Constants.VOLUNTEER_COMPANY, "");
    	String loc = mPrefs.getString(Constants.VOLUNTEER_LOCATION, "");
    	
    	mUser.setMailId(mailid);
    	mUser.setName(name);
    	mUser.setEmpNumber(Id);
    	mUser.setCompName(comp);
    	mUser.setPhoneNum(phone);
    	mUser.setLocation(loc);
    }
	
	private void getList()
	{
		ListView lstvw = (ListView)findViewById(R.id.lstvwmsglist);
		ArrayList<TrfcViaolator> trfclst = ((TrfController)mController).getTrfcViaolators();
		ArrayAdapter<TrfcViaolator> adapter = new MsgAdapter(this, R.layout.msg_list_row, trfclst);
		
		//....Set adapter.
		lstvw.setAdapter(adapter);
		

	}
	
	private void setControlEvent()
	{
		//....Set event.
		ListView lstvw = (ListView)findViewById(R.id.lstvwmsglist);
		lstvw.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lstvw.setOnItemClickListener(new OnItemClickListener() 
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
			{
				GenericUtility.list_sel = position;
				MsgListActivity.this.mTrfcviolator = (TrfcViaolator)parent.getItemAtPosition(position);
				((ArrayAdapter)((ListView)parent).getAdapter()).notifyDataSetChanged();
				
				//....open preview.
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setDataAndType(Uri.parse("file://" + MsgListActivity.this.mTrfcviolator.getmPicPath()), "image/png");
				startActivity(i);
			}
		});
		
		ImageButton ibSendMail = (ImageButton)findViewById(R.id.ibSendMail);
    	ibSendMail.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				if(mTrfcviolator==null)
				{
					Toast.makeText(MsgListActivity.this, 
							MsgListActivity.this.getResources().getString(R.string.str_pls_sel_img), 
							Toast.LENGTH_SHORT).show();
					return;
				}
				
				new GetGeoCodeTask(MsgListActivity.this).execute(mLocation);
			}
		});
    	
    	ImageButton ibDelete = (ImageButton)findViewById(R.id.ibDelete);
    	ibDelete.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				if(mTrfcviolator==null)
				{
					Toast.makeText(MsgListActivity.this, 
							MsgListActivity.this.getResources().getString(R.string.str_pls_sel_img), 
							Toast.LENGTH_SHORT).show();
					return;
				}
				
				//....delete file.
				File file = new File(mTrfcviolator.getmPicPath());
				file.delete();
				
				//....delete data.
				((TrfController)mController).setMtrfcViaolator(mTrfcviolator);
				((TrfController)mController).deleteTrfcViaolator();
				
				//....Set selected index.
				GenericUtility.list_sel = -1;
				
				//....get list again
				getList();
			}
		});
	}
	
	public void getAddress_CreateMail(StringBuilder address)
    {
    	String strAddress = null;
    	
    	if(address.length() > 0)
    		strAddress = address.toString();
    	
    	SendGmail(strAddress);
    }
	
	private void SendGmail(String Address)
    {    	
    	//....Set Email Body.
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType("text/plain");
		emailIntent.putExtra(Intent.EXTRA_EMAIL, mUser.getTrgtmailId());

		//...Set subject.
		String subject = "Trafiic Violation - " + mUser.getName() + " - " + mUser.getEmpNumber() + " - " + mUser.getCompName();
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		emailIntent.putExtra(Intent.EXTRA_TEXT, CreateTrafficViolationBody(Address));

		if(mTrfcviolator.getmPicPath() != null && mTrfcviolator.getmPicPath() != "")
		{
			//....Set File Attachment.
			File file = new File(mTrfcviolator.getmPicPath());
			if (!file.exists() || !file.canRead()) 
			{
			    return;
			}
			Uri uri = Uri.fromFile(file);
			emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
		}
		
		//....Start activity.
		MsgListActivity.this.startActivityForResult(emailIntent, REQUEST_CODE_SEND_MAIL);
    }

    private String CreateTrafficViolationBody(String Address)
    {
    	String mailBody = null;
    	StringBuilder strReturnedAddress = new StringBuilder();
    	
    	//....Set name,id and company.
    	strReturnedAddress.append("Name: " + mUser.getName() + "\n" );
    	strReturnedAddress.append("Volunteer ID: " + mUser.getEmpNumber() + "\n" );
    	strReturnedAddress.append("Company: " + mUser.getCompName() + "\n" );
    	strReturnedAddress.append("Phone: " + mUser.getPhoneNum() + "\n" );
    	strReturnedAddress.append("Email ID: " + mUser.getMailId() + "\n" );
    	strReturnedAddress.append("\n");
    	
    	if(!GenericUtility.isStringBlank(mTrfcviolator.getmMessage()) && !mTrfcviolator.getmMessage().equals("null"))
    	{
	    	//....Set violation message.
	    	strReturnedAddress.append("Violation description: " + mTrfcviolator.getmMessage() + "\n" );
	    	strReturnedAddress.append("\n");
    	}

    	if(!GenericUtility.isStringBlank(Address))
    	{
    		strReturnedAddress.append("Location:\n");
    		strReturnedAddress.append(Address + "\n");
    	}
    	else if(!GenericUtility.isStringBlank(mUser.getLocation()))
    	{
        	strReturnedAddress.append("Location: " + mUser.getLocation() + "\n" );
    	}
    	
    	strReturnedAddress.append("Time: " + mTrfcviolator.getmPicTakenDate() + "\n" );
    	mailBody = strReturnedAddress.toString();
    	
    	return mailBody;
    }
    
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == REQUEST_CODE_SEND_MAIL)
		{
			//....Set selected index = -1.
			GenericUtility.list_sel = -1;
			
			//....delete data.
			((TrfController)mController).setMtrfcViaolator(mTrfcviolator);
			((TrfController)mController).deleteTrfcViaolator();
			mTrfcviolator = null;
			
			//...get list again.
			getList();
		}
	}
	
	class MyLocationListener implements LocationListener 
	{
        @Override
        public void onLocationChanged(Location location) 
        {
            if (location != null) 
            {
                // This needs to stop getting the location data and save the battery power.
                locManager.removeUpdates(locListener); 
                MsgListActivity.this.mLocation = location;
                ((TrfController)MsgListActivity.this.mController).getMtrfcViaolator().setmLatitude(mLocation.getLatitude());
                ((TrfController)MsgListActivity.this.mController).getMtrfcViaolator().setmLongitude(mLocation.getLongitude());
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }
    }
}
