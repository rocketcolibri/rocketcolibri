package ch.hsr.rocketcolibri.manager.listener;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.PopupWindow;
import ch.hsr.rocketcolibri.manager.IDesktopViewManager;

/**
 * This Class handles the touch events in the Customize-Mode
 * @author artvel
 */
public class CustomizeModusListener implements OnTouchListener{

	private int clickCount = 0;
	private long startTime;
	private long duration;
	private IDesktopViewManager tDesktopViewManger;
	private PopupWindow tCustomizeModusPopup;
	private View tRootView;//this is needed to dismiss the Popup
	private SingleTabCountDown tSingleTabCountDown = new SingleTabCountDown(this, MIN_DURATION, MIN_DURATION);
	
	public CustomizeModusListener(IDesktopViewManager desktopViewManager, View rootView, PopupWindow customizeModusPopup){
		tDesktopViewManger = desktopViewManager;
		tRootView = rootView;
		tCustomizeModusPopup = customizeModusPopup;
		tRootView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d("tRootView", "tRootViewtRootViewtRootViewtRootView");
				tSingleTabCountDown.cancel();
				dismissPopupIfIsShowing();
			}
		});
	}
	
	static final int MAX_DURATION = 500;
	static final int MIN_DURATION = 160;
		
	public boolean onTouch (View v, MotionEvent ev){
		if (!tDesktopViewManger.isInCustomizeModus()) return false;
        switch(ev.getAction() & MotionEvent.ACTION_MASK)
        {
        case MotionEvent.ACTION_DOWN:
        	if(System.currentTimeMillis() - startTime>MAX_DURATION){
        		clickCount=0;
        		startTime = System.currentTimeMillis();
        	}
            clickCount++;
            break;
        case MotionEvent.ACTION_UP:
        	duration = System.currentTimeMillis() - startTime;
        	Log.d("CustomModeListener", ""+duration +" "+clickCount);
        	if(clickCount == 2 && duration <= MAX_DURATION){
        		tSingleTabCountDown.cancel();
            	clickCount = 0;
            	startTime=0;
            	dismissPopupIfIsShowing();
            	return doubleTab(v);
        	}else if(duration < MIN_DURATION){
        		tSingleTabCountDown.cancel();
        		tSingleTabCountDown.setTargetView(v);
        		tSingleTabCountDown.start();
        	}
        case MotionEvent.ACTION_MOVE:
        	if(clickCount==1){
            	duration = System.currentTimeMillis() - startTime;
            	if(duration>=MAX_DURATION){
            		startTime=0;
            		clickCount = 0;
            		return longHold(v);
            	}
            }
        }
        return true;  
	}
	
	boolean singleTab(View tabbedView){
		dismissPopupIfIsShowing();
    	tCustomizeModusPopup.showAsDropDown(tabbedView);
    	return true;
	}
	
	private boolean doubleTab(View doubleTabbedView){
		tDesktopViewManger.resizeView(doubleTabbedView);
		return true;
	}
	
	private boolean longHold(View holdingView){
		return tDesktopViewManger.dragView(holdingView);
	}
	
	private void dismissPopupIfIsShowing(){
    	if(tCustomizeModusPopup.isShowing())
    		tCustomizeModusPopup.dismiss();
	}
	
	public void release(){
		tSingleTabCountDown.release();
		tSingleTabCountDown = null;
		dismissPopupIfIsShowing();
		tCustomizeModusPopup = null;
		tDesktopViewManger = null;
	}

}
