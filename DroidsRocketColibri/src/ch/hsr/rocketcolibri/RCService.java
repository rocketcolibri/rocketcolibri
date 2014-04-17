package ch.hsr.rocketcolibri;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class RCService extends Service {
    public static volatile boolean running;
    
    //db instanc variable and servocontroller connection stuff declaration here
    
	public class RCServiceBinder extends Binder {
		public RCService getService(){
			return RCService.this;
		}
	}

	private final IBinder binder = new RCServiceBinder();
	
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public void writeSomethingToLogFile(String data){
		Log.d("CustomIntentService", data);
	}
	
    @Override
    public void onCreate() {
        running = true;
        //
        //
        // AppStartup stuff goes here.. initialization ....
        //
        //
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
    
    @Override
	public void onDestroy() {
    	running = false;
	}
	
}
