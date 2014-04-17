package ch.hsr.rocketcolibri.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import java.util.Locale;
import ch.hsr.rocketcolibri.RCService;
import ch.hsr.rocketcolibri.RCService.RCServiceBinder;

public abstract class RCActivity extends Activity {
	protected RCService service;
	protected ProgressDialog mDialog;
	
	private ServiceConnection bindableServiceConnection = new ServiceConnection() {
		@Override 
		public void onServiceDisconnected(ComponentName componentName) {
			Log.d(getClassName(), "onServiceDisconnect");
			service = null;
		}
		@Override
		public void onServiceConnected(ComponentName componentName, IBinder binder) {
			Log.d(getClassName(), "onServiceConnected");
			RCServiceBinder customerBinder = (RCServiceBinder) binder;
			service = customerBinder.getService();
			onServiceReady();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(getClassName(), "onCreate");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(getClassName(), "onResume");
		if (!RCService.running) {
			startService(new Intent(this, RCService.class));
		}
		Intent intent = new Intent(this, RCService.class);
		bindService(intent, bindableServiceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(getClassName(), "onPause");
		unbindService(bindableServiceConnection);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(getClassName(), "onDestroy");
	}
	
	/**
	 * this method is called when the service is connected
	 */
	protected abstract void onServiceReady();
	
	protected void setWindowSettings(){
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().getDecorView().getRootView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
	}
	
	protected void showLoading(String message){
		if(mDialog!=null && mDialog.isShowing())return;
		mDialog = new ProgressDialog(this);
        mDialog.setMessage(message);
        mDialog.setCancelable(false);
		runOnUiThread(new Runnable(){
			public void run(){
	            mDialog.show();
			}
		});
	}
	
	protected void hideLoading(){
		if(mDialog==null)return;
		runOnUiThread(new Runnable(){
			public void run(){
				mDialog.dismiss();
			}
		});
	}
	
	protected void toast(String msg) {
		Toast t3 = Toast.makeText(this, msg, 100);
		t3.show();
	}
	
	protected void uitoast(final String msg){
		runOnUiThread(new Runnable() {
			public void run() {
				toast(msg);
			}
		});
	}

	protected void setLanguage(String langCode) {
		Resources res = getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		android.content.res.Configuration conf = res.getConfiguration();
		conf.locale = new Locale(langCode);
		res.updateConfiguration(conf, dm);
	}
	
	protected abstract String getClassName();
}
