package com.hcg2003.yamba;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class StatusData
{
	private final String TAG = StatusData.class.getSimpleName();
	static final String DB_NAME = "timeline.db";
	static final int DB_VERSION = 1;
	static final String TABLE = "timeline";
	static final String C_ID = "_id";
	static final String C_CREATED_AT = "created_at";
	static final String C_SOURCE = "source";
	static final String C_USER = "user";
	static final String C_TEXT = "txt";

	private static final String ORDER_BY = C_CREATED_AT + " DESC";
	private static final String[] MAX_CREATED_AT_COLS = { "max(" + StatusData.C_CREATED_AT + ")" };
	private static final String[] DB_TEXT_COLS = { C_TEXT };

	class DbHelper extends SQLiteOpenHelper
	{
		// Context context;

		public DbHelper(Context context)
		{
			super(context, DB_NAME, null, DB_VERSION);
			// this.context = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			String sql = "create table " + TABLE + "(" + C_ID + " int, " + C_CREATED_AT
					+ " datetime default current_timestamp, " + C_USER + " text, " + C_TEXT + " text)";
			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			db.execSQL("drop talbe if exists " + TABLE);
			this.onCreate(db);
		}

	}

	private final DbHelper dbHelper;

	public StatusData(Context context)
	{
		this.dbHelper = new DbHelper(context);
		Log.i(TAG, "Initialized data");
	}

	public void close()
	{
		this.dbHelper.close();
	}

	public void insertOrIgnore(ContentValues cv)
	{
		Log.d(TAG, "insertOrIgnore on " + cv);
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		try
		{
			db.insertWithOnConflict(TABLE, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
		} finally
		{
			db.close();
		}
	}

	public Cursor getStatusUpdates()
	{
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		return db.query(TABLE, null, null, null, null, null, ORDER_BY);
	}
	
	public long getLatestStatusCreatedAtTime()
	{
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		try
		{
			Cursor cursor = db.query(TABLE, MAX_CREATED_AT_COLS, null, null, null, null, null);
			try
			{
				return cursor.moveToNext()? cursor.getLong(0):Long.MIN_VALUE;
			}finally
			{
				cursor.close();
			}
		}finally
	{
		db.close();
	}
	}
}