package com.example.trafficcontrol.task;

import java.util.ArrayList;
import java.util.List;

import com.example.trafficcontrol.service.MailService;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

public class SendMailTask extends AsyncTask<Object, Object, Object> 
{
	private ProgressDialog statusDialog;
	private Activity sendMailActivity;

	public SendMailTask(Activity activity) 
	{
		sendMailActivity = activity;

	}

	protected void onPreExecute() 
	{
		statusDialog = new ProgressDialog(sendMailActivity);
		statusDialog.setMessage("Getting ready...");
		statusDialog.setIndeterminate(false);
		statusDialog.setCancelable(false);
		statusDialog.show();
	}

	@Override
	protected Object doInBackground(Object... args) 
	{
		try {
			
			publishProgress("Processing input....");
			List<String> toMails = new ArrayList<String>();
			toMails.add("surojit.salt@gmail.com");
			@SuppressWarnings("unchecked")
			MailService androidEmail = new MailService(args[0].toString(),
													   args[1].toString(), 
													   (List<String>) args[2], 
													   args[3].toString(),
													   args[4].toString());
			
			publishProgress("Preparing mail message....");
			androidEmail.createEmailMessage();
			
			publishProgress("Sending email....");
			androidEmail.sendEmail();
			
			publishProgress("Email Sent.");
			
		} 
		catch (Exception e) 
		{
			return "error occured";
			//publishProgress(e.getMessage());
		}
		return null;
	}

	@Override
	public void onProgressUpdate(Object... values) {
		statusDialog.setMessage(values[0].toString());

	}

	@Override
	public void onPostExecute(Object result) {
		statusDialog.dismiss();
	}
}
