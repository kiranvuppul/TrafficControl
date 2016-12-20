package com.example.trafficcontrol.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import android.database.Cursor;
import com.example.trafficcontrol.dal.DB;
import com.example.trafficcontrol.model.TrfcViaolator;

public class TrfController extends BaseController 
{

	private TrfcViaolator mtrfcViaolator;
	
	public void setMtrfcViaolator(TrfcViaolator trfcviolator) 
	{
		this.mtrfcViaolator = trfcviolator;
	}
	
	public TrfcViaolator getMtrfcViaolator() 
	{
		return mtrfcViaolator;
	}
	
	public TrfController()
	{
		mtrfcViaolator = new TrfcViaolator();
	}
	
	public void AddTrfcViolator()
	{
		DB db = new DB();
		
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy HH:mm:ss a");
		String strDate = sdf.format(c.getTime());
		
		mtrfcViaolator.setmPicTakenDate(strDate);
		
		String sqlQuery = "INSERT INTO TRFCVIOLATOR (DESCRIPTION, IMAGEPATH, LATITUDE, LONGITUDE, TIMESTAMP) VALUES('" 
						 + mtrfcViaolator.getmMessage() + "','" 
						 + mtrfcViaolator.getmPicPath() + "',"
						 + mtrfcViaolator.getmLatitude() + "," 
						 + mtrfcViaolator.getmLatitude() + ",'" 
						 + strDate + "')";
		db.executequery(sqlQuery);
	}

	public void addSignInTimeStamp()
	{
		DB db = new DB();
		
		String sqlQuery = "DELETE FROM USERLOGIN;";
		db.executequery(sqlQuery);
		
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy HH:mm:ss a");
		String strDate = sdf.format(c.getTime());
		
		sqlQuery = "INSERT INTO USERLOGIN (USERSIGNIN, USERSIGNOUT) VALUES('" 
				   + strDate + "', 'NULL');";
		db.executequery(sqlQuery);
	}
	
	public void deleteTrfcViaolator()
	{
		DB db = new DB();
		String sqlQuery = "DELETE FROM TRFCVIOLATOR WHERE IMAGEPATH = '" + mtrfcViaolator.getmPicPath() + "';";
		db.executequery(sqlQuery);
	}
	
	public void updateSignOutTimeStamp()
	{
		DB db = new DB();
		
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy HH:mm:ss a");
		String strDate = sdf.format(c.getTime());
		
		String sqlQuery = "UPDATE USERLOGIN SET USERSIGNOUT = '" + strDate + "' ";
		db.executequery(sqlQuery);
	}
	
	public String[] getTimeStamps()
	{
		String[] retVal = new String[2];
		DB db = new DB();

		String sqlQuery = "SELECT USERSIGNIN, USERSIGNOUT FROM USERLOGIN;";
		Cursor data = db.select(sqlQuery);
		
		String strSignIn = "";
		String strSignOut = "";
		
		if (data.moveToFirst()) 
		{
			strSignIn = data.getString(0);
			strSignOut = data.getString(1);
		}

		db.close();
		retVal[0] = strSignIn;
		retVal[1] = strSignOut;
		
		return retVal;
	}
	
	public ArrayList<TrfcViaolator> getTrfcViaolators()
	{
		ArrayList<TrfcViaolator> trfcviolators = new ArrayList<TrfcViaolator>();
		
		DB db = new DB();
		String sqlQuery = "SELECT DESCRIPTION, IMAGEPATH, LATITUDE, LONGITUDE, TIMESTAMP FROM TRFCVIOLATOR;";
		Cursor data = db.select(sqlQuery);

		TrfcViaolator trfc = new TrfcViaolator();
		if(data.moveToFirst()) 
		{
			String strDesc = data.getString(0);
			String strimgPath = data.getString(1);
			Double longitude = data.getDouble(2);
			Double latitude = data.getDouble(3);
			String strtimestamp = data.getString(4);
			
			trfc.setmMessage(strDesc);
			trfc.setmPicPath(strimgPath);
			trfc.setmLatitude(latitude);
			trfc.setmLongitude(longitude);
			trfc.setmPicTakenDate(strtimestamp);
			
			trfcviolators.add(trfc);
		}
		
		data.moveToNext();
		while(!data.isAfterLast())
		{
			TrfcViaolator trfc_new = new TrfcViaolator();
			String strDesc = data.getString(0);
			String strimgPath = data.getString(1);
			Double longitude = data.getDouble(2);
			Double latitude = data.getDouble(3);
			String strtimestamp = data.getString(4);
			
			trfc_new.setmMessage(strDesc);
			trfc_new.setmPicPath(strimgPath);
			trfc_new.setmLatitude(latitude);
			trfc_new.setmLongitude(longitude);
			trfc_new.setmPicTakenDate(strtimestamp);
			
			trfcviolators.add(trfc_new);
			data.moveToNext();
		}
		
		return trfcviolators;
	}

}
