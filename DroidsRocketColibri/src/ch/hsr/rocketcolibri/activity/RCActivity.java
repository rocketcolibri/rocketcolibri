/**
 * Rocket Colibri © 2014
 */
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Locale;








import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RocketColibriDefaults;
import ch.hsr.rocketcolibri.RocketColibriService;

/**
 * @author Artan Veliju
 */
public abstract class RCActivity extends Activity {
	protected RocketColibriService rcService;
	protected ProgressDialog mDialog;
	protected boolean tFullscreen = true;
	private ServiceConnection bindableServiceConnection = new ServiceConnection() {
		@Override 
		public void onServiceDisconnected(ComponentName componentName) {
			Log.d(getClassName(), "onServiceDisconnect");
			rcService = null;
		}
		@Override
		public void onServiceConnected(ComponentName componentName, IBinder binder) {
			Log.d(getClassName(), "onServiceConnected");
			RocketColibriService.RocketColibriServiceBinder customerBinder = (RocketColibriService.RocketColibriServiceBinder) binder;
			rcService = customerBinder.getService();
			onServiceReady();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(getClassName(), "onCreate");
		setWindowSettings();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(getClassName(), "onResume");
		if (!RocketColibriService.tRunning) {
			startService(new Intent(this, RocketColibriService.class));
		}
		Intent intent = new Intent(this, RocketColibriService.class);
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
		if(tFullscreen){
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			RocketColibriDefaults.setDefaultViewSettings(getWindow().getDecorView().getRootView());
			getWindow().getDecorView()
	        .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
	            @Override
	            public void onSystemUiVisibilityChange(int visibility) {
	                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
	                	RocketColibriDefaults.setDefaultViewSettings(getWindow().getDecorView());
	                }
	            }
	        });
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(tFullscreen){
		    View mDecorView = getWindow().getDecorView().findViewById(android.R.id.content);
		    if (hasFocus) {
		    	RocketColibriDefaults.setDefaultViewSettings(mDecorView);
		    }
        }
	}
	
	protected void showLoading(boolean showOnUiThread){
		showLoading(getString(R.string.loading), showOnUiThread);
	}
	
	protected void showLoading(){
		showLoading(getString(R.string.loading), true);
	}
	
	protected void showLoading(String message, boolean showOnUiThread){
		if(mDialog!=null && mDialog.isShowing())return;
		mDialog = new ProgressDialog(this);
        mDialog.setMessage(message);
        mDialog.setCancelable(false);
        if(showOnUiThread){
			runOnUiThread(new Runnable(){public void run(){
				mDialog.show();
			}});
        }else{
        	mDialog.show();
        }
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
		Toast t3 = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
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
