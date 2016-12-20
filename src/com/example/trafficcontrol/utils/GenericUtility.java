package com.example.trafficcontrol.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class GenericUtility {

	public static int list_sel =-1;
	private static Context applicationContext;
	private static Activity currentActivity;
	private static final Map<String, String> DATE_FORMAT_REGEXPS = new HashMap<String, String>() {
		/**
		* 
		*/
		private static final long serialVersionUID = 1L;

		{
			put("^\\d{8}$", "yyyyMMdd");
			put("^\\d{1,2}-\\d{1,2}-\\d{4}$", "dd-MM-yyyy");
			put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
			put("^\\d{1,2}/\\d{1,2}/\\d{4}$", "MM/dd/yyyy");
			put("^\\d{4}/\\d{1,2}/\\d{1,2}$", "yyyy/MM/dd");
			put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$", "dd MMM yyyy");
			put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$", "dd MMMM yyyy");
			put("^\\d{12}$", "yyyyMMddHHmm");
			put("^\\d{8}\\s\\d{4}$", "yyyyMMdd HHmm");
			put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}$",
					"dd-MM-yyyy HH:mm");
			put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}$",
					"yyyy-MM-dd HH:mm");
			put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}$",
					"MM/dd/yyyy HH:mm");
			put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}$",
					"yyyy/MM/dd HH:mm");
			put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}$",
					"dd MMM yyyy HH:mm");
			put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}$",
					"dd MMMM yyyy HH:mm");
			put("^\\d{14}$", "yyyyMMddHHmmss");
			put("^\\d{8}\\s\\d{6}$", "yyyyMMdd HHmmss");
			put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$",
					"dd-MM-yyyy HH:mm:ss");
			put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$",
					"yyyy-MM-dd HH:mm:ss");
			put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}.\\d{3}$",
										"yyyy-MM-dd HH:mm:ss.SSS");
			put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}.\\d{2}$",
					"yyyy-MM-dd HH:mm:ss.SS");
			put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$",
					"MM/dd/yyyy HH:mm:ss");
			put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$",
					"yyyy/MM/dd HH:mm:ss");
			put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$",
					"dd MMM yyyy HH:mm:ss");
			put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$",
					"dd MMMM yyyy HH:mm:ss");
		}
	};
	
	public static void setApplicationContext(Context context) {
		applicationContext = context;
	}
	
	public static Context getApplicationContext() {
		return applicationContext;
	}
	
	public static void setActivity(Activity activity) {
		currentActivity = activity;
	}
	
	public static Context getActivity() {
		return currentActivity;
	}
	
	public static boolean isStringBlank(final String pString) {
		if (pString == null) {
			return true;
		}

		if (pString.equals("")) {
			return true;
		}

		if (pString.length() == 0) {
			return true;
		}

		return false;
	}
	
	public static Date parseDate(final String dateStr) {
		Date date = null;

		try {
			if (dateStr != null) {
				// remove hundreds
				String tDateStr = dateStr;

				if (dateStr != null && dateStr.endsWith(".0")) {
					final int ndx = dateStr.lastIndexOf(".");

					if (ndx > -1) {
						tDateStr = dateStr.substring(0, ndx);
					}
				}

				// determine format
				final String format = determineDateFormat(tDateStr);
				final SimpleDateFormat sdf = new SimpleDateFormat(format);
				date = sdf.parse(tDateStr);
			}
		} catch (final Exception e) {
			// ignore
		}

		return date;
	}
	
	@SuppressLint("DefaultLocale")
	public static String determineDateFormat(final String dateString) {
		for (final String regexp : DATE_FORMAT_REGEXPS.keySet()) {
			if (dateString.toLowerCase().matches(regexp)) {
				return DATE_FORMAT_REGEXPS.get(regexp);
			}
		}
		return null; 
	}
	
	public static ProgressDialog showProgressDialog(final Activity activity, final String msg) {
		return ProgressDialog.show(activity, "", msg, true);
	}

	public static void showAlertDialog(final Builder adb, final String message,
			final String label, final String Title) {

		final AlertDialog ad = adb.create();
		ad.setMessage(message);
		ad.setTitle(Title);
		ad.setIcon(android.R.drawable.ic_dialog_alert);
		ad.setButton(DialogInterface.BUTTON_POSITIVE, label,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog,
							final int id) {
						dialog.cancel();

					}
				});

		ad.show();
	}

	public static void showAlertDialog(final Context c, final String message,
			final String label, final String Title) {
		final Builder adb = new AlertDialog.Builder(c);
		showAlertDialog(adb, message, label, Title);
	}

	public static void showAlertDialog(final View v, final String message,
			final String label, final String Title) {
		final Builder adb = new AlertDialog.Builder(v.getContext());
		showAlertDialog(adb, message, label, Title);
	}
	
	public static void setActionBarProp(Activity act, Boolean IsHomeBtnEnable) 
	{
		// get ActionBar.
		ActionBar actionBar = act.getActionBar();

		// enable Home Button.
		if (IsHomeBtnEnable) 
		{
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		// get color Drawable.
		ColorDrawable colorDrawable = new ColorDrawable(
				Color.parseColor("#FFFFFFFF"));
		actionBar.setBackgroundDrawable(colorDrawable);

		// get Title Id.
		int titleId = Resources.getSystem().getIdentifier("action_bar_title",
				"id", "android");
		TextView yourTextView = (TextView) act.findViewById(titleId);

		// Set text.
		yourTextView.setTextColor(Color.parseColor("#FF000000"));
	}
	
	public static byte[] download(String url) {
		byte[] dataBlob = null;
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		HttpResponse response = null;

		try {
			response = client.execute(request);
		} catch (ClientProtocolException e) {
			return dataBlob;
		} catch (IOException e) {
			return dataBlob;
		}

		HttpEntity entity = response.getEntity();
		InputStream is = null;

		try {
			is = entity.getContent();
		} catch (IllegalStateException e) {
			return dataBlob;
		} catch (IOException e) {
			return dataBlob;
		}

		byte[] buffer = new byte[8192];
		int bytesRead;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			while ((bytesRead = is.read(buffer)) != -1) {
				output.write(buffer, 0, bytesRead);
			}
		} catch (IOException e) {

			return dataBlob;
		}

		dataBlob = output.toByteArray();
		return dataBlob;

	}
}
