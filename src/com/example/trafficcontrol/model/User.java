package com.example.trafficcontrol.model;

public class User extends BaseModel 
{
	private String mailId;
	private String[] mTrgtmailId = new String[]{ "cyberabadtraffic@gmail.com" };
	private String mEmpNumber;
	private String mName;
	private String mCompName;
	private String mPhoneNum;
	private String mLocation;
	
	public String getMailId() {
		return mailId;
	}
	
	public void setMailId(String mailId) {
		this.mailId = mailId;
	}
	
	public String[] getTrgtmailId() {
		return mTrgtmailId;
	}
	
	public String getEmpNumber() {
		return mEmpNumber;
	}
	
	public void setEmpNumber(String mEmpNumber) {
		this.mEmpNumber = mEmpNumber;
	}
	
	public String getName() {
		return mName;
	}
	
	public void setName(String mName) {
		this.mName = mName;
	}
	
	public String getCompName() {
		return mCompName;
	}
	
	public void setCompName(String mCompName) {
		this.mCompName = mCompName;
	}

	public String getPhoneNum() {
		return mPhoneNum;
	}

	public void setPhoneNum(String mPhoneNum) {
		this.mPhoneNum = mPhoneNum;
	}

	public String getLocation() {
		return mLocation;
	}

	public void setLocation(String mLocation) {
		this.mLocation = mLocation;
	}

}
