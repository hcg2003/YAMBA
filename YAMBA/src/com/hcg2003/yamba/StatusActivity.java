package com.hcg2003.yamba;


//import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.os.StrictMode;
//import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StatusActivity extends Activity
{
//	private static final String TAG = "StatusActivity";
	TextView tvCount;
	EditText etTweet;
	Button btnUpadte;
//	Twitter twitter;
	ProgressDialog pd;
//	SharedPreferences shPrefs;
	
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
//		getTwitter();
	}
	
	private void findViews()
	{
		//Setup text counter
		tvCount = (TextView)findViewById(R.id.tvCount);
		tvCount.setText(Integer.toString(140));
		tvCount.setTextColor(Color.GREEN);
		
		etTweet = (EditText)findViewById(R.id.evTweet);
		btnUpadte = (Button)findViewById(R.id.btnUpdate);
		//Setup Preferences
//		shPrefs = PreferenceManager.getDefaultSharedPreferences(this);
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
			}
		});
		
		//Text Counter Listener
		etTweet.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count){}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			
			@Override
			public void afterTextChanged(Editable s)
			{
				int count = 140 - s.length();
				tvCount.setText(Integer.toString(count));
				tvCount.setTextColor(Color.GREEN);
				if(count < 10)
				{
					tvCount.setTextColor(Color.YELLOW);
				}
				if(count < 0)
				{
					tvCount.setTextColor(Color.RED);
				}				
			}
		});
		
		//SharedPreferences Listener
//		shPrefs.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener()
//		{
//			@Override
//			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
//			{
//				twitter = null;			
//			}
//		});
	}

//	private Twitter getTwitter()
//	{
//		if(twitter == null)
//		{
//			String username= shPrefs.getString("username","");
//			String password= shPrefs.getString("password", "");
//			String apiRoot= shPrefs.getString("apiRoot", "http://yamba.marakana.com/api");
//			twitter = new Twitter(username, password);
//			twitter.setAPIRootUrl(apiRoot);			
//		}
//		return twitter;
//	}
	
	class PostToTwitter extends AsyncTask<String, Integer, String>{

		@Override
		protected String doInBackground(String... params)
		{
			try
			{
				return ((YambaApplication)getApplication()).getTwitter().setStatus(params[0]).text;
			}catch(TwitterException te)
			{
				return "Failed to post";
			}
		}	
		
		@Override
		protected void onPreExecute()
		{
			pd = ProgressDialog.show(StatusActivity.this, getResources().getString(R.string.titleProgressDialog), getResources().getString(R.string.summaryProgressDialog));
			super.onPreExecute();
		}
		
		@Override
		protected void onPostExecute(String result)
		{
			Toast.makeText(StatusActivity.this, result, Toast.LENGTH_SHORT).show();
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
				
			case R.id.itemServiceStart:
			startService(new Intent(this, UpdaterService.class));
			break;
			
			case R.id.itemServiceStop:
			stopService(new Intent(this, UpdaterService.class));
			break;			
	
			case R.id.itemPrefs:
			startActivity(new Intent(this, PrefsActivity.class));
			break;
			
		}
		return true;
	}
}
