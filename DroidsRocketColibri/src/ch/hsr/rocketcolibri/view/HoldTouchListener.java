/**
 * Rocket Colibri © 2014
 * 
 * @author Naxxx
 */
package ch.hsr.rocketcolibri.view;

import android.graphics.drawable.TransitionDrawable;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * @author Artan Veliju
 */
public class HoldTouchListener implements OnTouchListener{
    private TransitionDrawable tTransition;
    private OnHoldListener tHoldListener;
    private final int MAX_DURATION = 3000;
    private CountDownTimer tCountDown;
    private boolean tDone = false;
    
    public HoldTouchListener(TransitionDrawable transition, OnHoldListener holdListener){
    	tTransition = transition;
    	tHoldListener = holdListener;
    }
    
	public boolean onTouch(final View v, MotionEvent event) {
		switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				tDone = false;
        		tHoldListener.onHoldStart(v, MAX_DURATION);
        		tTransition.startTransition(MAX_DURATION);
        		cancelCountDown();
        		tCountDown = new CountDownTimer(MAX_DURATION, MAX_DURATION) {
					public void onTick(long millisUntilFinished) {}
					public void onFinish() {
						tDone = true;
						tHoldListener.onHoldEnd(v);
						tTransition.resetTransition();
					}
				};
				tCountDown.start();
				break;
			case MotionEvent.ACTION_MOVE:
				return false;
			case MotionEvent.ACTION_UP: 
				if(!tDone)tHoldListener.onHoldCanceled();
				tTransition.resetTransition();
				cancelCountDown();
				break;
	        case MotionEvent.ACTION_CANCEL:
	        	return false;
        }
		return true;
	}
	
	private void cancelCountDown(){
		if(tCountDown!=null){
			tCountDown.cancel();
			tCountDown = null;
		}
	}
}
