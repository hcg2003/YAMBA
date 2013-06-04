package com.hcg2003.yamba;

import winterwell.jtwitter.Twitter;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class YambaApplication extends Application
{
	private Twitter twitter;
	private SharedPreferences shPrefs;
	
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
		if(twitter == null)
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
}
