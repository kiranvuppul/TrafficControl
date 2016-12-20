package com.example.trafficcontrol;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.example.trafficcontrol.controller.LoginController;
import com.example.trafficcontrol.controller.TrfController;
import com.example.trafficcontrol.model.User;
import com.example.trafficcontrol.task.GetGeoCodeTask;
import com.example.trafficcontrol.utils.Constants;
import com.example.trafficcontrol.utils.GenericUtility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class MainActivity extends BaseActivity 
{
	private static Boolean getFile = false;
	private String mCameraImagePath = null;
	private String mViolatormsg;
	private Boolean mSignout = false;
	
	private SharedPreferences mPrefs;
	
	// constant for requesting picture from camera
	private static final int REQUEST_CODE_SEND_MAIL = 22;
	private static final int REQUEST_CODE_CHOOSE_PICTURE_FROM_CAMERA = 23;
	
	// location manager;
    private LocationManager locManager;
    private LocationListener locListener = new MyLocationListener();
    private Location mLocation;
    
    private boolean gps_enabled = false;
    private boolean network_enabled = false;
    private User mUser = new User();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        //....Set Content.
        setContentView(R.layout.main);
        
        mPrefs = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        
        //....Set LocationManager.
        locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        
        //....Set controller.
        mController = new TrfController();
        
        //....Set Control Event
        SetControlEvent();
        
        //....Set Action Bar.
        GenericUtility.setActionBarProp(this, false);
        
        //....Set GPS and Network Boolean.
        try 
        {
            gps_enabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } 
        catch (Exception ex) 
        {
        	
        }
        
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
        
        loadUser();
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
    
    public boolean onCreateOptionsMenu(Menu menu) 
	{
		super.onCreateOptionsMenu(menu);
		
		//....set main menu options.
	    getMenuInflater().inflate(R.menu.main_options, menu);
	    
	    return true;
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	switch (item.getItemId()) 
    	{
		
			case R.id.menu_item_profile:
				//======================
				
				Intent intent = new Intent(MainActivity.this, SetProfileActivity.class);
				MainActivity.this.startActivity(intent);   
				
				return true;
				
			case R.id.menu_item_pending_message:
				//=============================
				Intent intent1 = new Intent(MainActivity.this, MsgListActivity.class);
				MainActivity.this.startActivity(intent1);  
				
				return true;
				
			case R.id.menu_item_log_out:
				//======================
				
				LoginController mLoginController=new LoginController(getApplicationContext());
				mLoginController.setloginStatus(false);
				
				//....Set Intent.
				Intent intent2 = new Intent(MainActivity.this, LoginActivity.class);
				MainActivity.this.startActivity(intent2);   
				MainActivity.this.finish();
				
				(new TrfController()).updateSignOutTimeStamp();
				mSignout = true;
				SendGmail(null);
				
				return true;
				
			default:
				return super.onOptionsItemSelected(item);
		}
    	
    }
    
    private void SetControlEvent()
	{
    	ImageButton ibSendMail = (ImageButton)findViewById(R.id.ibSendMail);
    	ibSendMail.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				if(mCameraImagePath == null)
				{
					Toast.makeText(MainActivity.this, 
							MainActivity.this.getResources().getString(R.string.str_pls_sel_img), 
							Toast.LENGTH_SHORT).show();
					return;
				}
				
				new GetGeoCodeTask(MainActivity.this).execute(mLocation);
			}
		});
    	
    	ImageButton ibOpenCamera = (ImageButton)findViewById(R.id.ibOpenCamera);
    	ibOpenCamera.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				getFileFromCamera();
			}
		});
    	
    	ImageButton ibSave = (ImageButton)findViewById(R.id.ibSave);
    	ibSave.setEnabled(true);
    	ibSave.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				if(mCameraImagePath == null)
				{
					Toast.makeText(MainActivity.this, "Please select a image to send mail.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				((TrfController)MainActivity.this.mController).AddTrfcViolator();
				
				
				//....reset values.
				resetVals();
			}
		});
    	
    	final EditText etViolatorMsg = (EditText)findViewById(R.id.etViolatorMsg);
    	etViolatorMsg.addTextChangedListener(new TextWatcher() 
    	{
			public void afterTextChanged(Editable s) 
			{
				if (etViolatorMsg.getText() != null)
					mViolatormsg = etViolatorMsg.getText().toString() ;
				else
					mViolatormsg = "";
				
				((TrfController)mController).getMtrfcViaolator().setmMessage(mViolatormsg);

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
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
		
		if(!mSignout)
		{
			//...Set subject.
			String subject = "Trafiic Violation - " + mUser.getName() + " - " + mUser.getEmpNumber() + " - " + mUser.getCompName();
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		}
		else
		{
			//...Set subject.
			String subject = "Time Sheet - " + mUser.getName() + " - " + mUser.getEmpNumber() + " - " + mUser.getCompName();
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		}
		
		if(!mSignout)
			emailIntent.putExtra(Intent.EXTRA_TEXT, CreateTrafficViolationBody(Address));
		else
			emailIntent.putExtra(Intent.EXTRA_TEXT, CreateTimeSheetBody(Address));
		
		if(((TrfController)mController).getMtrfcViaolator().getmPicPath() != null 
				&& ((TrfController)mController).getMtrfcViaolator().getmPicPath() != "")
		{
			//....Set File Attachment.
			File file = new File(((TrfController)mController).getMtrfcViaolator().getmPicPath());
			if (!file.exists() || !file.canRead()) {
			    return;
			}
			
			Uri uri = Uri.fromFile(file);
			emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
		}
		
		//....Start activity.
		MainActivity.this.startActivityForResult(emailIntent, REQUEST_CODE_SEND_MAIL);
    }
    
    private String CreateTimeSheetBody(String Address)
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
    	
    	String[] vals = ((TrfController)mController).getTimeStamps();
    	strReturnedAddress.append("Start Time: " + vals[0] + "\n" );
    	strReturnedAddress.append("End Time: " + vals[1] + "\n" );

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMMM-yyyy HH:mm:ss a");
        Date Date1 = null, Date2 = null;
		try 
		{
			Date1 = formatter.parse(vals[0]);
	        Date2 = formatter.parse(vals[1]);
		} 
		catch (ParseException e) 
		{
			e.printStackTrace();
		}

    	
    	long mills = Date2.getTime() - Date1.getTime();
    	long differenceInSeconds = mills / DateUtils.SECOND_IN_MILLIS;
    	String formatted = DateUtils.formatElapsedTime(differenceInSeconds);

    	if(formatted.contains(":")) 
    	{
    		String[] timevals = formatted.split(":");
    		
    		if(timevals.length > 2)
    		{
    			formatted = timevals[0] + " hour " + timevals[1] + " minute " + timevals[2] + " second ";
    		}
    		else if(timevals.length > 1)
    		{
    			formatted = timevals[0] + " minute " + timevals[1] + " second ";
    		}
    	}
    	
    	strReturnedAddress.append("Total Time: " + formatted + "\n");
    	strReturnedAddress.append("\n");
    	
    	if(!GenericUtility.isStringBlank(mUser.getLocation()))
    	{
        	strReturnedAddress.append("Location: " + mUser.getLocation() + "\n" );
    	}
    	
    	mailBody = strReturnedAddress.toString();
    	
    	return mailBody;
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
    	
    	if(!GenericUtility.isStringBlank(mViolatormsg))
    	{
	    	//....Set violation message.
	    	strReturnedAddress.append("Violation description: " + mViolatormsg + "\n" );
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
    	
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy HH:mm:ss a");
		String strDate = sdf.format(c.getTime());
    	strReturnedAddress.append("Time: " + strDate + "\n" );
    	
    	mailBody = strReturnedAddress.toString();
    	
    	return mailBody;
    }
    
    @Override
    protected void onResume() 
    {
    	//....Get file from Camera.
    	if(!getFile)
    	{
    		getFileFromCamera();
    	}
    	
    	super.onResume();
    	
    }
    
    @Override
    public View onCreateView(View parent, String name, Context context,
    		AttributeSet attrs) 
    {
    	//....Get file from Camera.
    	//if(!getFile)
    	//{
    	//	getFileFromCamera();
    	//}
    	
    	return super.onCreateView(parent, name, context, attrs);
    }
    
    public void getFileFromCamera() 
	{
    	getFile = true;
		if(Camera.getNumberOfCameras()>0)
		{
			final Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			
			//Provide a file to store the image captured
			mCameraImagePath = null;
			File f = createCameraImageFile();	
			if (f!= null)
			{
				camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
				mCameraImagePath = f.getAbsolutePath();
			}			
			this.startActivityForResult(camera,
					REQUEST_CODE_CHOOSE_PICTURE_FROM_CAMERA);
		}
		else
		{
			
		}

	}
    
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == REQUEST_CODE_CHOOSE_PICTURE_FROM_CAMERA)
		{
			if(mCameraImagePath != null && resultCode!=0)
			{
				String filePath = getRealPathFromString(mCameraImagePath);
	    		((TrfController)mController).getMtrfcViaolator().setmPicPath(filePath);
	    		
				WebView wvDoc = (WebView) this.findViewById(R.id.webView1);
				wvDoc.setInitialScale(40);
				wvDoc.getSettings().setUseWideViewPort(true);
				wvDoc.getSettings().setBuiltInZoomControls(true);
				
				wvDoc.setTag("file://" + mCameraImagePath);
				wvDoc.loadUrl("file://" + mCameraImagePath);
				
				//@SuppressWarnings("unused")
				//Bitmap bm = getBitmapFromLocalPath(filePath,1);
			}
			else
			{
				resetVals();
			}
		}
		else
		{
			//....Set showhide progressbar = false.
			MainActivity.this.showHideProgress(false);
			
			//....delete file.
			//File file = new File(getRealPathFromString(mCameraImagePath));
			//file.delete();
			
			//....reset values.
			resetVals();

			//....Open camera.
			getFileFromCamera();

		}
	}
	
	private void resetVals()
	{
		//....Set values = blank
		mCameraImagePath = null;
		mViolatormsg = null;
		
		WebView wvDoc = (WebView) this.findViewById(R.id.webView1);
		wvDoc.setInitialScale(40);
		wvDoc.getSettings().setUseWideViewPort(true);
		wvDoc.getSettings().setBuiltInZoomControls(true);
		wvDoc.setTag("file:///android_res/drawable/file_img.png");
		wvDoc.loadUrl("file:///android_res/drawable/file_img.png");
		
		//....Reset text.
		EditText etViolatorMsg = (EditText)findViewById(R.id.etViolatorMsg);
		etViolatorMsg.setText("");
	}
	
	private File createCameraImageFile() 
	{
		
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "EAMImages");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists())
	    {
	        if (! mediaStorageDir.mkdirs())
	        {
	            //Logger.Log("HMSImages", "failed to create directory");
	        	
	            return null;
	        }
	    }
		
		File file = new File(mediaStorageDir.getPath() + File.separator + genarateUniqueNumber() + ".png");
	    file.setReadable(true, false);
	    return file;
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String genarateUniqueNumber() 
	{
		final String DATE_FORMAT_NOW = "yyyyMMddHHmmssSSS";
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		
		return "USER" + sdf.format(cal.getTime());
	}
	
	private String getRealPathFromString(String imagpath)
	{
		String filePath = null;
		File file = new File(imagpath);
		FileInputStream is = null;
		try 
		{
			is = new FileInputStream(file);
		
			if (is != null)
			{
				File file1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), genarateUniqueNumber() + ".png");
				FileOutputStream output = null;
				output = new FileOutputStream(file1, false);

				byte[] buffer = new byte[8192];
				int bytesRead;
				while ((bytesRead = is.read(buffer)) != -1) 
					output.write(buffer, 0, bytesRead);
												
				output.close();
				is.close();
				file1.setReadable(true, false);
				filePath = file1.getAbsolutePath();
			}
		}
		catch (FileNotFoundException ex){
					
		}
		catch (IOException ex){
			
		}
		
		return filePath;
	}
	
	public static Bitmap getBitmapFromLocalPath(String path, int sampleSize)
	{
	   try
	   {
	     BitmapFactory.Options options = new BitmapFactory.Options();
	     options.inSampleSize = sampleSize;
	     return BitmapFactory.decodeFile(path, options);
	   }
	   catch(Exception e)
	   {
	   }
	   
	   return null;
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
                MainActivity.this.mLocation = location;
                ((TrfController)MainActivity.this.mController).getMtrfcViaolator().setmLatitude(mLocation.getLatitude());
                ((TrfController)MainActivity.this.mController).getMtrfcViaolator().setmLongitude(mLocation.getLongitude());
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
