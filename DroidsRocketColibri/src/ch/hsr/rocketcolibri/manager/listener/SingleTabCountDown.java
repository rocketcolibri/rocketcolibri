package ch.hsr.rocketcolibri.manager.listener;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;

public class SingleTabCountDown extends CountDownTimer{

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
	
	public void release(){
		this.cancel();
		tCustomizeModusListener = null;
		tTargetView = null;
	}

}
