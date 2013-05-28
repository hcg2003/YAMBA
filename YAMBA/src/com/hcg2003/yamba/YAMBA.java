package com.hcg2003.yamba;

import winterwell.jtwitter.Twitter;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class YAMBA extends Activity
{
	private static final String TAG = "StatusActivity";
	EditText etTweet;
	Button btnUpadte;
	Twitter twitter;
	
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
	}
	
	private void findViews()
	{
		etTweet = (EditText)findViewById(R.id.evTweet);
		btnUpadte = (Button)findViewById(R.id.btnUpdate);
		twitter = new Twitter("student", "password");
		twitter.setAPIRootUrl("http://yamba.marakana.com/api");
		btnUpadte.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				twitter.setStatus(etTweet.getText().toString());
				Log.d(TAG, "onClicked");
			}
		});
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.yamb, menu);
		return true;
	}

}