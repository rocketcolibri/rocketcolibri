/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.manager.listener;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;

/**
 * @author Artan Veliju
 */
public class SingleTabCountDown extends CountDownTimer{
	private Lock syncedStartCancel = new ReentrantLock(true);
	private volatile boolean running;
	private CustomizeModusListener tCustomizeModusListener;
	private View tTargetView;
	public SingleTabCountDown(CustomizeModusListener customizeModusListener, long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
		tCustomizeModusListener = customizeModusListener;
	}
	
	@Override
	public void onFinish() {
		tCustomizeModusListener.singleTab(tTargetView);
	}

	@Override
	public void onTick(long millisUntilFinished) {
		Log.d(SingleTabCountDown.class.getName(), "onTick: "+millisUntilFinished);
	}
	
	public void setTargetView(View view){
		tTargetView = view;
	}
	
	public void safeStart(){
		syncedStartCancel.lock();
		if(!running){
			running = true;
			start();
		}
		syncedStartCancel.unlock();
	}
	
	public void safeCancel(){
		syncedStartCancel.lock();
		if(running){
			running = false;
			cancel();
		}
		syncedStartCancel.unlock();
	}
	
	public void release(){
		safeCancel();
		syncedStartCancel = null;
		tCustomizeModusListener = null;
		tTargetView = null;
	}
}
