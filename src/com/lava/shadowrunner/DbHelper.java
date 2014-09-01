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
		
		System.out.println("CREATE_USER_TABLE: " + DataBaseManager.CREATE_USER_TABLE);
		System.out.println("CREATE_USERPATH_TABLE: " + DataBaseManager.CREATE_USERPATH_TABLE);
		System.out.println("CREATE_PATH_TABLE: " + DataBaseManager.CREATE_PATH_TABLE);
		
		db.execSQL(DataBaseManager.CREATE_USER_TABLE);
		db.execSQL(DataBaseManager.CREATE_PATH_TABLE);
		db.execSQL(DataBaseManager.CREATE_USERPATH_TABLE);
		
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
