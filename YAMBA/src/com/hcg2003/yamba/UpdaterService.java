package com.hcg2003.yamba;

import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UpdaterService extends Service
{
	public static final String TAG = UpdaterService.class.getSimpleName();
	static final int DELAY = 60000; // 1 min
	private boolean runFlag = false;
	private Updater updater;
	private YambaApplication yamba;
	
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
				Log.d(TAG, "Running background thread");
				try
				{
					YambaApplication yamba = (YambaApplication) updaterService.getApplication();
					int newUpdates = yamba.fetchStatusUpdates();
					if(newUpdates > 0)
					{
						Log.d(TAG, "We have a new status");
					}

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
