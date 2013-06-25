package com.hcg2003.yamba;

import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

public class UpdaterService extends Service
{
	static final int DELAY = 60000; // 1 min
	private boolean runFlag = false;
	private Updater updater;
	private YambaApplication yamba;
	
//	DbHelper dbHelper;
//	SQLiteDatabase db;
	
	private class Updater extends Thread
	{
		List<Twitter.Status> timeline;
		
		public Updater()
		{
			super("UpdaterService-Updater");
		}
		@Override
		public void run()
		{
			UpdaterService updaterService = UpdaterService.this;
			while(updaterService.runFlag)
			{
				try
				{
					try
					{
						timeline = yamba.getTwitter().getFriendsTimeline();
					} catch (TwitterException e)
					{
						e.printStackTrace();
					}
					
//					//Open db
//					db = dbHelper.getWritableDatabase();
//					
//					//Lopp over the timeline
//					ContentValues cv = new ContentValues();
//					for(Twitter.Status status: timeline)
//					{
//						cv.clear();
//						cv.put(DbHelper.C_ID, status.id);
//						cv.put(DbHelper.C_CREATED_AT, status.createdAt.getTime());
//						cv.put(DbHelper.C_USER, status.user.name);
//						cv.put(DbHelper.C_TEXT, status.text);
//
//						db.insertOrThrow(DbHelper.TABLE, null, cv);
//
//						Log.d("UpdaterService", String.format("%s: %s", status.user.name, status.text));			
//					}
//					
//					//Close db
//					db.close();
					
					Log.d("UpdaterService", "Updater ran");
					Thread.sleep(DELAY);
					
				} catch (InterruptedException e)
				{
					updaterService.runFlag = false;
				}
			}
		super.run();
		}
	}
	
	
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		this.updater = new Updater();
		this.yamba = (YambaApplication) getApplication();
		
//		dbHelper = new DbHelper(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		this.runFlag = true;
		this.yamba.setServiceRunning(true);
		this.updater.start();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		this.runFlag = false;
		this.yamba.setServiceRunning(false);
		this.updater.interrupt();
		this.updater = null;
	}
	
}
