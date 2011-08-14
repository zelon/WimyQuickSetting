package com.wimy.android.quicksettinglauncher;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends Activity implements OnClickListener
{
	static final int NOTIFICATIONBAR_UID = 1;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		addNotibar();
		
		setButtonListener(R.id.btnWifi);
		setButtonListener(R.id.btnGPS);
		setButtonListener(R.id.btnFlash);
		setButtonListener(R.id.btnAutoRotate);
		
		this.registerReceiver(new BroadcastReceiver()
		{
			
			@Override
			public void onReceive(Context context, Intent intent)
			{
				SettingActivity.this.updateWifi();
			}
		}, new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED"));
	}
	
	private void addNotibar()
	{
		NotificationManager mNotiMgr = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		
		Notification noti = new Notification();

		removeNotificationIcon(noti);
		
		Intent intent = new Intent(this, SettingActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
		
		noti.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
		//noti.setLatestEventInfo(this, "QuickSetting", "Tap here to launch Quick Setting", pendingIntent);
		
		RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notibar);
		noti.contentView = contentView;
		noti.contentIntent = pendingIntent;
		
		mNotiMgr.notify(NOTIFICATIONBAR_UID, noti);
	}

	private void removeNotificationIcon(Notification noti)
	{
		noti.icon = R.drawable.alpha;
		noti.when = -Long.MAX_VALUE;
	}

	private boolean isGPSEnabled()
	{
		LocationManager mgr = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
		//GpsStatus status = mgr.getGpsStatus(null);
		return mgr.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	
	private void setGPS(boolean bEnable)
	{
	//	LocationManager mgr = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
		Intent myIntent = new Intent( Settings.ACTION_SECURITY_SETTINGS );
	    startActivity(myIntent);
	}
	
    private void updateGPS()
	{
		TextView tv = (TextView)findViewById(R.id.statusGPS);
		Button btn = (Button)findViewById(R.id.btnGPS);
		
		if ( isGPSEnabled() )
		{
			tv.setText("GPS enabled");
			btn.setText("Show setting to off");
		}
		else
		{
			tv.setText("GPS disalbed");
			btn.setText("Show setting to on");
		}
	}
	private boolean isWifiEnabled()
	{
		WifiManager wifiMgr = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		return wifiMgr.isWifiEnabled();
	}
	
	private void setWifi(boolean bEnable)
	{
		WifiManager wifiMgr = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		
		wifiMgr.setWifiEnabled(bEnable);
	}
	
	private void updateWifi()
	{
		TextView tv = (TextView)findViewById(R.id.statusWifi);
		Button btn = (Button)findViewById(R.id.btnWifi);
		
		if ( isWifiEnabled() )
		{
			tv.setText("Wifi enabled");
			btn.setText("Disable wifi");
		}
		else
		{
			tv.setText("Wifi disalbed");
			btn.setText("Enable wifi");
		}
		
		updateWifiDetail();
	}
	
	private void updateWifiDetail()
	{
		TextView tv = (TextView)findViewById(R.id.statusWifi);
		WifiManager wifiMgr = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		
		StringBuilder sb = new StringBuilder();
		
		switch ( wifiMgr.getWifiState() )
		{
		case WifiManager.WIFI_STATE_DISABLED:
			sb.append("Wifi disabled");
			break;
		case WifiManager.WIFI_STATE_DISABLING:
			sb.append("Wifi disabling");
			break;
		case WifiManager.WIFI_STATE_ENABLED:
			sb.append("Wifi enabled");
			if ( null != wifiMgr.getConnectionInfo().getSSID() ) sb.append(" : " + wifiMgr.getConnectionInfo().getSSID() );
			break;
		case WifiManager.WIFI_STATE_ENABLING:
			sb.append("Wifi enabling");
			break;
		case WifiManager.WIFI_STATE_UNKNOWN:
			sb.append("Wifi unknown state");
			break;
		default:
			sb.append("...?");
			break;
		}
		tv.setText(sb.toString());
		
		
	}

	private void updateStatus()
	{
		updateWifi();
		updateGPS();
		updateAutoRotate();
	}
	
	private void updateAutoRotate()
	{
		TextView tv = (TextView)findViewById(R.id.statusAutoRotate);
		Button btn = (Button)findViewById(R.id.btnAutoRotate);
		
		if ( isAutoRotateEnabled() )
		{
			tv.setText("AutoRotate enabled");
			btn.setText("Off");
		}
		else
		{
			tv.setText("AutoRotate disalbed");
			btn.setText("On");
		}
	}

	private void setButtonListener(int id)
	{
		Button btn = (Button)findViewById(id);
		btn.setOnClickListener(this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		updateStatus();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.btnWifi:
			
			if ( isWifiEnabled() )
			{
				setWifi(false);
				Msg("Set Wifi off");
			}
			else
			{
				setWifi(true);
				Msg("Set Wifi on");
			}
			updateWifi();
			break;
			
		case R.id.btnGPS:
			
			if ( isGPSEnabled() )
			{
				setGPS(false);
				Msg("Set GPS off");
			}
			else
			{
				setGPS(true);
				Msg("Set GPS on");
			}
			break;

		case R.id.btnAutoRotate:
		{
			if ( isAutoRotateEnabled() )
			{
				setAutoRotate(false);
				Msg("Set autorotate off");
			}
			else
			{
				setAutoRotate(true);
				Msg("Set autorotate on");
			}
			updateAutoRotate();
			break;
		}
			
		case R.id.btnFlash:
		{
			Intent i = new Intent(this, FlashActivity.class);
			startActivity(i);
			break;
		}

		default:
			break;
		}
	}
	
	private void setAutoRotate(boolean bEnable)
	{
		Settings.System.putInt(this.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, bEnable ? 1 : 0 );
	}

	private boolean isAutoRotateEnabled()
	{
		try
		{
			if ( 1 == Settings.System.getInt(this.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION) )
			{
				return true;
			}
		}
		catch (SettingNotFoundException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	private void Msg(String msg)
	{
		Log.i("zelon",  msg);
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
}
