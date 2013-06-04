package com.hcg2003.yamba;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class YAMBA extends Activity
{
	private static final String TAG = "StatusActivity";
	TextView tvCount;
	EditText etTweet;
	Button btnUpadte;
	Twitter twitter;
	ProgressDialog pd;
	SharedPreferences shPrefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
//	   StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//		   .detectDiskReads()
//		   .detectDiskWrites()
//		   .detectNetwork() 
//		   .penaltyLog()
//		   .build());
//
//	    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//		   .detectLeakedSqlLiteObjects() 
//		   .penaltyLog() 
//		   .penaltyDeath()
//		   .build()); 

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_yamba);
		findViews();
		listeners();
		getTwitter();
	}
	
	private void findViews()
	{
		tvCount = (TextView)findViewById(R.id.tvCount);
		etTweet = (EditText)findViewById(R.id.evTweet);
		btnUpadte = (Button)findViewById(R.id.btnUpdate);
		//Setup Preferences
		shPrefs = PreferenceManager.getDefaultSharedPreferences(this);
	}
	
	private Twitter getTwitter()
	{
		if(twitter == null)
		{
			String username= shPrefs.getString("username","");
			String password= shPrefs.getString("password", "");
			String apiRoot= shPrefs.getString("apiRoot", "http://yamba.marakana.com/api");
			twitter = new Twitter(username, password);
			twitter.setAPIRootUrl(apiRoot);			
		}
		return twitter;
	}

	private void listeners()
	{
		btnUpadte.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
//				twitter.setStatus(etTweet.getText().toString());
				new PostToTwitter().execute(etTweet.getText().toString());
				Log.d(TAG, "onClicked");
			}
		});
		
		tvCount.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count){}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			
			@Override
			public void afterTextChanged(Editable s)
			{
				int count = 140 - s.length();		
			}
		});
		
		//SharedPreferences Listener
		shPrefs.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener()
		{
			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
			{
				twitter = null;			
			}
		});
	}

	class PostToTwitter extends AsyncTask<String, Integer, String>{

		@Override
		protected String doInBackground(String... params)
		{
			try
			{
				return twitter.setStatus(params[0]).text;
			}catch(TwitterException te)
			{
				return "Failed to post";
			}
		}	
		
		@Override
		protected void onPreExecute()
		{
			pd = ProgressDialog.show(YAMBA.this, "處理中...", "請稍候，處理完畢會自動結束...");
			super.onPreExecute();
		}
		
		@Override
		protected void onPostExecute(String result)
		{
			Toast.makeText(YAMBA.this, result, Toast.LENGTH_SHORT).show();
			pd.dismiss();
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.itemPrefs:
			startActivity(new Intent(this, PrefsActivity.class));
			break;
		}
		return true;
	}
}
