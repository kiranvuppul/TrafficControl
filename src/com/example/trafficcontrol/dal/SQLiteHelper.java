/*******************************************************************
*                                                                  *
*                           NOTICE                                 *
*                                                                  *
*   THIS SOFTWARE IS THE PROPERTY OF AND CONTAINS                  *
*   CONFIDENTIAL INFORMATION OF INFORS AND/OR ITS AFFILIATES       *
*   OR SUBSIDIARIES AND SHALL NOT BE DISCLOSED WITHOUT PRIOR       *
*   WRITTEN PERMISSION. LICENSED CUSTOMERS MAY COPY AND            *
*   ADAPT THIS SOFTWARE FOR THEIR OWN USE IN ACCORDANCE WITH       *
*   THE TERMS OF THEIR SOFTWARE LICENSE AGREEMENT.                 *
*   ALL OTHER RIGHTS RESERVED.                                     *
*                                                                  *
*   (c) COPYRIGHT 2012 INFOR.  ALL RIGHTS RESERVED.                *
*   THE WORD AND DESIGN MARKS SET FORTH HEREIN ARE                 *
*   TRADEMARKS AND/OR REGISTERED TRADEMARKS OF INFORS              *
*   AND/OR ITS AFFILIATES AND SUBSIDIARIES. ALL RIGHTS             *
*   RESERVED.  ALL OTHER TRADEMARKS LISTED HEREIN ARE              *
*   THE PROPERTY OF THEIR RESPECTIVE OWNERS.                       *
*                                                                  *
********************************************************************/

package com.example.trafficcontrol.dal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.sqlcipher.SQLException;
import com.example.trafficcontrol.utils.GenericUtility;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper 
{
	private static final String DATABASE_NAME = "TRFCMobile.db3";
	private static final int DATABASE_VERSION = 1;
	private SQLiteDatabase myDataBase;
	
	public SQLiteHelper(Context context) 
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) 
	{

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{

	}
	
	public SQLiteDatabase openDataBase() throws SQLException 
	{
		String defaultPath = getDBPath();
		return openDataBase(defaultPath);
	}
	
    /**
     * 
     * open Database with encrypted password
     * 
     * @param pDatabasePath Database Absolute path
     * @param pDatabasePassword encrypted Database password
     * @return SQLiteDatabase
     * 
     * @throws SQLException
     */
    public SQLiteDatabase openDataBase(String pDatabasePath) throws SQLException 
    {
        try
        {
        	copyDatabaseIfNeeded(pDatabasePath);
            myDataBase = SQLiteDatabase.openDatabase(pDatabasePath, null, SQLiteDatabase.OPEN_READWRITE);    
        }
        catch(SQLException e)
        {
            
        }
        return myDataBase;
    }
	

	public static String getDatabasePath()
	{
		//first, get Application Context
		Context vContext = GenericUtility.getApplicationContext();
		//second, get FilesDir Path
		String vFilesPath = vContext.getFilesDir().getPath();
		//third, ignore the last folder "files" and append the "databases" folder
		String vDatabasePath = vFilesPath.substring(0, vFilesPath.lastIndexOf("/")) + "/databases/";
		//return Database Path
		return vDatabasePath;
	}
	
	public static String getDBPath() 
	{
		
		return getDatabasePath() + DATABASE_NAME;
	}
	

    public static void copyDatabaseIfNeeded(String pDBPath) 
    {
        if(GenericUtility.isStringBlank(pDBPath))
        {
            pDBPath = getDBPath();
        }
        
		File file = new File(pDBPath);
		if (!file.exists()) 
		{
			copyDatabase(pDBPath);
		}
    }
    
	private static void copyDatabase(String pDBPath) 
	{
		File file = new File(pDBPath);
		boolean success = file.exists();
		if (success) 
			file.delete();
		
		SQLiteHelper helper = new SQLiteHelper(GenericUtility.getApplicationContext());
		try 
		{
			helper.copyDataBase(DATABASE_NAME, pDBPath);
		} catch (IOException e) 
		{
		}
	}
	
	public void copyDataBase(String dbName, String destination) throws IOException 
	{
		// Open your local db as the input stream
		InputStream myInput =  GenericUtility.getApplicationContext().getAssets().open(dbName);

		File file = new File(destination);
		if (!file.exists()) 
		{
			myDataBase = this.getWritableDatabase();
		}
		
		OutputStream myOutput = new FileOutputStream(destination);
		
		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) 
		{
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
		myDataBase.close();
	}

}
