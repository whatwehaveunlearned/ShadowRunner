/*This is the class used to manage the database, create, read, update, delete*/
package com.lava.shadowrunner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DataBaseManager {
	public static final String TABLE_NAME1 = "user";
	public static final String TABLE_NAME2 = "userpath";
	public static final String TABLE_NAME3 = "path";
	
	//Define Columns names  for "user" TABLE
	public static final String CN_USER_ID="_id";
	public static final String CN_USER_NAME="user";
	//Define Columns names  for "userpath" TABLE
	public static final String CN_USERPATH_ID="_id";
	public static final String CN_USERPATH_NAME="user";
	public static final String CN_PATH="path";
	//Define Columns names for "path" TABLE
	public static final String CN_PATH_ID="_id";
	public static final String CN_ISBEST="isbest";
	public static final String CN_DISTANCES="distances";
	public static final String CN_TIMES="times";
	public static final String CN_ACCELERATIONS="accelerations";
	
	/**************SQL Strings to create data bases********************************/
	public static final String CREATE_USER_TABLE = "create table " + TABLE_NAME1 +" ("
			+ CN_USER_ID + " integer primary key autoincrement,"
			+ CN_USER_NAME + " text not null);";
	
	public static final String CREATE_USERPATH_TABLE = "create table " + TABLE_NAME2 +" ("
			+ CN_USERPATH_ID + " integer primary key autoincrement,"
			+ CN_ISBEST + " text not null,"
			+ CN_USERPATH_NAME + " integer not null," 
			+ CN_PATH + " integer not null,"
			+ "foreign key(" + CN_USERPATH_NAME + ") references user("+ CN_USER_ID +"),foreign key("+ CN_PATH + ") references path(" + CN_PATH_ID + "));";
	
	public static final String CREATE_PATH_TABLE = "create table " + TABLE_NAME3 +" ("
			+ CN_PATH_ID + " integer primary key autoincrement,"
			+ CN_DISTANCES + " real," 
			+ CN_TIMES + " real," 
			+ CN_ACCELERATIONS + " real);";
	/************** END SQL Strings to create data bases********************************/
	
	
	private DbHelper helper;
	private SQLiteDatabase db;
	
	//Constructor
	public DataBaseManager(Context context){
		//Create DB
		helper = new DbHelper(context);
		//This creates the dataBase if it doesn't exist or returns it if it does
		db=helper.getWritableDatabase();
	}
	
	/************************** INSERT ****************************************/
	//Method to Generate the Content Values
	public ContentValues generateContentValues (String column, String text){
		ContentValues values = new ContentValues();
		values.put(column, text);
		return values;
	}
	//Method to Insert in tables
	public void insertDB (String dbName, String columnName, String text){
		db.insert(dbName, null, generateContentValues(columnName,text)); //returns -1 when there is a problem
	}
	/************************** END INSERT *************************************/
	
	/************************** READ ****************************************/
	//Method to read from data Base All arguments can be null except table
	public Cursor LoadCursor(String table, String selection, String[] selectionArgs,  String groupBy, String having, String orderBy, String limit){
		String[] columns;
		//We select the columns we want to read depending on the table
		if (table==TABLE_NAME1){
			columns = new String[]{CN_USER_ID,CN_USER_NAME};
		}else if (table==TABLE_NAME2){
			columns = new String[]{CN_USERPATH_ID,CN_USER_NAME,CN_PATH};
		}else if (table==TABLE_NAME3){
			columns = new String[]{CN_PATH_ID,CN_DISTANCES,CN_TIMES,CN_ACCELERATIONS};
		}else{
			System.out.println("Your Table does not exist;");
			return null;
		}
		return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
	}
	/**************************END READ ****************************************/
	
	/************************** DELETE ****************************************/
	public void deleteRecord (String record){
		db.delete(TABLE_NAME1,CN_USER_NAME+"?",new String[]{record});
	}
	
	/************************** END DELETE ****************************************/
}
