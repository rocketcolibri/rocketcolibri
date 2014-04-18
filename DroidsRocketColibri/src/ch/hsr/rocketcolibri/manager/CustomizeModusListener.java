package ch.hsr.rocketcolibri.manager;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class CustomizeModusListener implements OnTouchListener{

	private int clickCount = 0;
	private long startTime;
	private long duration;
	private IDesktopViewManager tDesktopViewManger;
	
	public CustomizeModusListener(IDesktopViewManager desktopViewManager){
		tDesktopViewManger = desktopViewManager;
	}
	
	static final int MAX_DURATION = 500;
		
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
            if(clickCount == 2){
            	duration = System.currentTimeMillis() - startTime;
                if(duration<= MAX_DURATION){
                	startTime=0;
                	tDesktopViewManger.resizeView(v);
                }
                clickCount = 0;
                break;             
            }
        case MotionEvent.ACTION_MOVE:
        	if(clickCount==1){
            	duration = System.currentTimeMillis() - startTime;
            	if(duration>=MAX_DURATION){
            		startTime=0;
            		clickCount = 0;
            		return tDesktopViewManger.dragView(v);
            	}
            }
        }
        return true;  
	}

}
