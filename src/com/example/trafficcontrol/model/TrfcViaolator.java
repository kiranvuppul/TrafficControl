package com.example.trafficcontrol.model;

public class TrfcViaolator extends BaseModel
{
	private String mMessage;
	private String mPicPath;
	private String mPicTakenDate;
	private double mLatitude;
	private double mLongitude;
	
	public String getmMessage() 
	{
		return mMessage;
	}
	
	public void setmMessage(String mMessage) 
	{
		this.mMessage = mMessage;
	}
	
	public String getmPicPath() 
	{
		return mPicPath;
	}
	
	public void setmPicPath(String mPicPath) 
	{
		this.mPicPath = mPicPath;
	}

	public double getmLatitude() 
	{
		return mLatitude;
	}

	public void setmLatitude(double mLatitude) 
	{
		this.mLatitude = mLatitude;
	}

	public double getmLongitude() 
	{
		return mLongitude;
	}

	public void setmLongitude(double mLongitude) 
	{
		this.mLongitude = mLongitude;
	}

	public String getmPicTakenDate() 
	{
		return mPicTakenDate;
	}

	public void setmPicTakenDate(String mPicTakenDate) 
	{
		this.mPicTakenDate = mPicTakenDate;
	}
	
}
