package com.wimy.android.quicksettinglauncher;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

public class FlashActivity extends Activity
{

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);

	    View v = new View(this);
	    v.setBackgroundColor(Color.WHITE);
	    
	    setContentView(v);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
	}

}
