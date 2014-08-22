/* This class is used to create and update the database*/
package com.lava.shadowrunner;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
	
	//Data base name
	private static final String DB_NAME= "shadowdb.sqlite";
	//Schema version
	private static final int DB_SCHEME_VERSION=1;
	
	//Constructor
	public DbHelper(Context context) {
		super(context, DB_NAME, null, DB_SCHEME_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL(DataBaseManager.CREATE_TABLE1);
		db.execSQL(DataBaseManager.CREATE_TABLE2);
		
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
