package com.hcg2003.yamba;

import java.security.acl.LastOwnerException;
import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.Twitter.Status;
import android.app.Application;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class YambaApplication extends Application
{
	public static final String TAG = YambaApplication.class.getSimpleName();
	private Twitter twitter;
	private SharedPreferences shPrefs;
	private boolean serviceRunning;
	private StatusData statusData;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		this.shPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.shPrefs.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener()
		{
			@Override
			public synchronized void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
			{
				twitter = null;		
			}
		});
	}
	
	public synchronized Twitter getTwitter()
	{
		if(this.twitter == null)
		{
			String username = this.shPrefs.getString("username", "");
			String password = this.shPrefs.getString("password", "");
			String apiRoot = this.shPrefs.getString("apiRoot", "http://yamba.marakana.com/api");
			if(!TextUtils.isEmpty(username)&&!TextUtils.isEmpty(password)&&!TextUtils.isEmpty(apiRoot))
			{
				this.twitter = new Twitter(username, password);
				this.twitter.setAPIRootUrl(apiRoot);
			}
		}
			
		return this.twitter;
	}
	
	public StatusData getStatusData()
	{
		if(statusData==null)
		{
			statusData = new StatusData(this);
		}
		return statusData;
	}
	
	public boolean isServiceRunning()
	{
		return serviceRunning;
	}
	
	public void setServiceRunning(boolean serviceRunning)
	{
		this.serviceRunning = serviceRunning;
	}
	
	public synchronized int fetchStatusUpdates()
	{
		Log.d(TAG, "Fetching status updates");
		Twitter twitter = this.getTwitter();
		if(twitter == null)
		{
			Log.d(TAG, "Twitter not initialized");
			return 0;
		}
		try{
			List<Status> statusUpdates = twitter.getFriendsTimeline();
			long latestStatusCreatedAtTime = this.getStatusData().getLatestStatusCreatedAtTime();
			int count = 0;
			//Lopp over the timeline
			ContentValues cv = new ContentValues();
			for(Status status: statusUpdates)
			{
				cv.clear();
				cv.put(StatusData.C_ID, status.getId());
				long createdAt = status.getCreatedAt().getTime();
				cv.put(StatusData.C_CREATED_AT, createdAt);
				cv.put(StatusData.C_USER, status.getUser().getName());
				cv.put(StatusData.C_TEXT, status.getText());
				Log.d(TAG, "Got update with id "+status.getId()+". Saving");
				this.getStatusData().insertOrIgnore(cv);
				if(latestStatusCreatedAtTime < createdAt)
				{
					count++;
				}
				
				Log.d(TAG, count>0?"Got "+count+" status updates":"No new status updates");
			}
			return count;
		}catch (RuntimeException e)
		{
			Log.e(TAG, "Failed to fetch status updates", e);
			return 0;
		}
	}
}
