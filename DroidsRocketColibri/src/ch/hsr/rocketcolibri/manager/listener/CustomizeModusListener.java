package ch.hsr.rocketcolibri.manager.listener;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.PopupWindow;
import ch.hsr.rocketcolibri.manager.IDesktopViewManager;
import ch.hsr.rocketcolibri.view.MyAbsoluteLayout.LayoutParams;

/**
 * This Class handles the touch events in the Customize-Mode
 * @author artvel
 */
public class CustomizeModusListener implements OnTouchListener{

	private int tClickCount = 0;
	private int tViewId = 0;
	private long tStartTime;
	private long tDuration;
	private IDesktopViewManager tDesktopViewManger;
	private PopupWindow tCustomizeModusPopup;
	//this is needed to dismiss the Popup
	private View tRootView;
	//the single tab handler
	private SingleTabCountDown tSingleTabCountDown = new SingleTabCountDown(this, MIN_DURATION, MIN_DURATION);
	
	public CustomizeModusListener(IDesktopViewManager desktopViewManager, View rootView, PopupWindow customizeModusPopup){
		tDesktopViewManger = desktopViewManager;
		tRootView = rootView;
		tCustomizeModusPopup = customizeModusPopup;
		tRootView.setOnClickListener(new OnClickListener() {
			/**
			 * dismiss Popup if touch outside
			 */
			@Override
			public void onClick(View v) {
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
        		tSingleTabCountDown.cancel();
            	tClickCount = 0;
            	tStartTime=0;
            	dismissPopupIfIsShowing();
            	return doubleTab(v);
        	}else if(tDuration < 80){
        		tSingleTabCountDown.cancel();
        		tSingleTabCountDown.setTargetView(v);
        		tSingleTabCountDown.start();
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
        }
        return true;  
	}
	
	boolean singleTab(View tabbedView){
		dismissPopupIfIsShowing();
		//TODO Issue #23 
		//replace showAsDropDown with showAtLocation(parent, gravity, x, y)
		//to place the Popup close to the tabbedView in a visible area.
		//To get the Position Params just remove the comment from the following line.
		//LayoutParams lp = (LayoutParams) tabbedView.getLayoutParams();
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
