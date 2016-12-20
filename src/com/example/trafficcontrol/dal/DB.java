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

import com.example.trafficcontrol.utils.GenericUtility;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DB 
{
	private SQLiteDatabase database = null;
	private SQLiteHelper dbHelper;
	
	public DB() throws SQLException
	{
		dbHelper = new SQLiteHelper(GenericUtility.getApplicationContext());
	}

    public void open() throws SQLException 
    {
        try 
        {
            database = dbHelper.openDataBase();
        } 
        catch (SQLException sqle) 
        {
            throw sqle;
        }
    }

	public void close() throws SQLException
	{
		dbHelper.close();
	}
	
	public long insert(ContentValues cv, String TableName) throws SQLException
	{
		open();
		long insertId = database.insert(TableName,null, cv);
		close();
		return insertId;
	}
	
	public void executequery(String query) throws SQLException
	{
		open();
		database.execSQL(query);
		close();
	}
	
	public void delete(String TableName) throws SQLException
	{
		open();
		database.delete(TableName, null, null);
		close();
	}

	public Cursor select(String query) throws SQLException
	{
		Cursor ret_Cursor;
		open();
		ret_Cursor = database.rawQuery(query, null);
		
		return ret_Cursor;
	}
}
