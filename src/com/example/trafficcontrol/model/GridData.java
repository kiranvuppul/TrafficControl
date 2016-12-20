package com.example.trafficcontrol.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.example.trafficcontrol.utils.GenericUtility;

import android.annotation.SuppressLint;
import android.content.ContentValues;

public class GridData {
	public int currentPosition;
	public String[] fieldName;
	// public ArrayList<String> fields;
	public List<String[]> fieldValues = new ArrayList<String[]>();
	private List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
	public boolean moreData;
	public String session;
	public ArrayList<ContentValues> contentValues;


	// public boolean hasValue(String field) {
	// return fields.contains(field);
	// }

	public void addRowToGridAtLocation(String[] record, int location) 
	{
		
		if (fieldValues.size() - 1 >= 0)
			fieldValues.add(location, record);
		else
			fieldValues.add(0, record);
	}
	
	public void deleteRowInGridAtLocation(int location) 
	{
		
		if (fieldValues.size() - 1 >= 0)
			fieldValues.remove(location);
		
	}
	
	
	public int addRowToGrid(String[] record) {
		fieldValues.add(record);
		if (fieldValues.size() - 1 >= 0)
			return fieldValues.size() - 1;
		else
			return -1;
	}

	@SuppressLint("SimpleDateFormat")
	public Date getFieldAsDate(String field, int row) {
		int index = getIndex(field);
		if (index == -1)
			return null;
		String[] recordData = fieldValues.get(row);
		String dt_val = recordData[index];
		Date date = null;
		try 
		{
//			if(!GenericUtility.isStringBlank(dt_val) && dt_val.contains("."))
//			{
//				date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
//				   .parse(dt_val);
//			}
//			else if(!GenericUtility.isStringBlank(dt_val))
//			{
//				date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//			   .parse(dt_val);
//			}
			return GenericUtility.parseDate(dt_val);
		} 
		catch (Exception e) 
		{
			
			date = null;
		}
		return date;
	}

	public Number getFieldAsNumber(String field, int row) {
		int index = getIndex(field);
		if (index == -1)
			return null;
		String[] recordData = fieldValues.get(row);
		Double value = null;
		try {
			value = Double.valueOf(recordData[index]);
		} catch (Exception e) {

		}

		return value;
	}

	public String getFieldAsString(String field, int row) {
		int index = getIndex(field);
		if (index == -1)
			return null;
		String[] recordData = fieldValues.get(row);
		return recordData[index];
	}

	public int getIndex(String field) {
		for (int i = 0; i < fieldName.length; i++) {
			if (fieldName[i].equalsIgnoreCase(field))
				return i;
		}
		return -1;
	}

	
	  public void setFieldAsNumber(String field, int row, Double value) 
	  { 
	 	int index = getIndex(field); 
	 if (index != -1) 
	 { String[] recordData =
	 fieldValues.get(row); recordData[index] = Double.toString(value);
	 fieldValues.set(row, recordData); }
	 }
	 

	/*
	 * public void setFieldAsDate(String field, int row, Date value) { int index
	 * = getIndex(field); if (index != -1) { String[] recordData =
	 * fieldValues.get(row); recordData[index] = value; fieldValues.set(row,
	 * recordData); } }
	 */

	public GridData reBuildGridForChildListView(String[] fieldName) 
	{
		GridData newGrid = new GridData();
		newGrid.fieldName = fieldName;
		
		if ((fieldValues != null) && (fieldValues.size() > 0)) 
		{	
			// ....Set fieldvalues in New Grid Data.
			for (int i = 0; i < fieldValues.size(); i++) 
			{
				String[] fieldValues = new String[fieldName.length];

				for (int j = 0; j < fieldValues.length; j++) 
				{
					fieldValues[j] = getFieldAsString(newGrid.fieldName[j], i);
				}
				newGrid.fieldValues.add(fieldValues);
			}
			
		}
		return newGrid;
	}

	public void setFieldAsString(String field, int row, String value) {
		int index = getIndex(field);
		if (index != -1) {
			String[] recordData = fieldValues.get(row);
			recordData[index] = value;
			fieldValues.set(row, recordData);
		}

	}
	
	public void setFieldAsBlob(String field, int row, byte[] value) {
		HashMap<String, Object> hashData;
		if (data.size() > row && data.get(row) != null)
		{
			hashData = data.get(row);
			hashData.put(field, value);
		}
		else {
			hashData = new HashMap<String, Object>();
			hashData.put(field, value);
			data.add(row, hashData);
		}

	}

	public byte[] getFieldAsBlob(String field, int row) {
		HashMap<String, Object> hashData = data.get(row);
		if (hashData != null)
			return (byte[])hashData.get(field);
		return null;

	}
	
	public void updateRowInGrid(String[] record, int row) {
		fieldValues.set(row, record);
	}

}
