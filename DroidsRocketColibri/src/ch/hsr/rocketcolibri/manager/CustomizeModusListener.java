package ch.hsr.rocketcolibri.manager;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.PopupWindow;

public class CustomizeModusListener implements OnTouchListener{

	private int clickCount = 0;
	private long startTime;
	private long duration;
	private IDesktopViewManager tDesktopViewManger;
	private PopupWindow tCustomizeModusPopup;
	
	public CustomizeModusListener(IDesktopViewManager desktopViewManager, PopupWindow customizeModusPopup){
		tDesktopViewManger = desktopViewManager;
		tCustomizeModusPopup = customizeModusPopup;
	}
	
	static final int MAX_DURATION = 500;
	static final int MIN_DURATION = 40;
		
	public boolean onTouch (View v, MotionEvent ev){
		if (!tDesktopViewManger.isInCustomizeModus()) return false;
        switch(ev.getAction() & MotionEvent.ACTION_MASK)
        {
        case MotionEvent.ACTION_DOWN:
        	if(System.currentTimeMillis() - startTime>MAX_DURATION){
        		clickCount=0;
        	}
            if(clickCount == 0){
            	startTime = System.currentTimeMillis();
            }
            clickCount++;
            break;
        case MotionEvent.ACTION_UP:
        	duration = System.currentTimeMillis() - startTime;
        	Log.d("CustomModeListener", ""+duration +" "+clickCount);
        	if(clickCount == 2 && duration<= MAX_DURATION && duration> MIN_DURATION){
            	clickCount = 0;
            	startTime=0;
            	dismissPopupIfIsShowing();
            	return doubleTab(v);
        	}else if(clickCount == 1 && duration < MIN_DURATION){
//            	duration = System.currentTimeMillis() - startTime;
            	clickCount = 0;
//                if(duration<= MAX_DURATION){
                	startTime=0;
                	return singleTab(v);
//                }
//                break;
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
	
	private boolean singleTab(View tabbedView){
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
		dismissPopupIfIsShowing();
		tCustomizeModusPopup = null;
		tDesktopViewManger = null;
	}

}
