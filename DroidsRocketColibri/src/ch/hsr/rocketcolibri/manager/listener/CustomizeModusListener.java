/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.manager.listener;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import ch.hsr.rocketcolibri.manager.IDesktopViewManager;
import ch.hsr.rocketcolibri.view.popup.PopupWindow.OnDismissListener;

/**
 * This Class handles the touch events in the Customize-Mode
 * @author Artan Veliju
 */
public class CustomizeModusListener implements OnTouchListener{

	private int tClickCount = 0;
	private int tViewId = 0;
	private long tStartTime;
	private long tDuration;
	private IDesktopViewManager tDesktopViewManager;
	//the single tab handler
	private SingleTabCountDown tSingleTabCountDown = new SingleTabCountDown(this, MIN_DURATION, MIN_DURATION);
	
	public CustomizeModusListener(IDesktopViewManager desktopViewManager){
		tDesktopViewManager = desktopViewManager;
		tDesktopViewManager.getCustomizeModusPopupMenu().setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				tSingleTabCountDown.safeCancel();
				tViewId = 0;
			}
		});
	}
	
	static final int MAX_DURATION = 500;
	static final int MIN_DURATION = 160;
		
	public boolean onTouch (View v, MotionEvent ev){
		if (!tDesktopViewManager.isInCustomizeModus()) return false;
        switch(ev.getAction() & MotionEvent.ACTION_MASK){
        case MotionEvent.ACTION_DOWN:
        	if(tViewId!=v.hashCode() || System.currentTimeMillis() - tStartTime>MAX_DURATION){
        		tClickCount=0;
        		tStartTime = System.currentTimeMillis();
        	}
            tClickCount++;
            tViewId = v.hashCode();
            break;
        case MotionEvent.ACTION_UP:
        	tDuration = System.currentTimeMillis() - tStartTime;
        	Log.d("CustomModeListener", ""+tDuration +" "+tClickCount);
        	if(tClickCount == 2 && tDuration <= MAX_DURATION){
        		tSingleTabCountDown.safeCancel();
            	tClickCount = 0;
            	tStartTime=0;
            	tDesktopViewManager.getCustomizeModusPopupMenu().dismiss();
            	return doubleTab(v);
        	}else if(tDuration < MIN_DURATION){
        		tSingleTabCountDown.safeCancel();
        		tSingleTabCountDown.setTargetView(v);
        		tSingleTabCountDown.safeStart();
        	}
        case MotionEvent.ACTION_MOVE:
        	if(tClickCount==1){
            	tDuration = System.currentTimeMillis() - tStartTime;
            	if(tDuration>=MAX_DURATION){
            		tStartTime=0;
            		tClickCount = 0;
            		return longHold(v);
            	}
            }
        case MotionEvent.ACTION_CANCEL:
        	return false;  
        }
        return true;  
	}
	
	boolean singleTab(View tabbedView) {
		tDesktopViewManager.getCustomizeModusPopupMenu().show(tabbedView);
    	return true;
	}
	
	private boolean doubleTab(View doubleTabbedView){
		tDesktopViewManager.resizeView(doubleTabbedView);
		return true;
	}
	
	private boolean longHold(View holdingView){
		return tDesktopViewManager.dragView(holdingView);
	}
	
	public void release(){
		tDesktopViewManager.getCustomizeModusPopupMenu().dismiss();
		tSingleTabCountDown.release();
		tSingleTabCountDown = null;
		tDesktopViewManager = null;
	}

}
