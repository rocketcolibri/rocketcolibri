package ch.hsr.rocketcolibri.manager.listener;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.PopupWindow;
import ch.hsr.rocketcolibri.manager.IDesktopViewManager;
import ch.hsr.rocketcolibri.menu.CustomizeModusPopupMenu;
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.custimizable.CustomizableView;
import ch.hsr.rocketcolibri.view.popup.PopupWindow.OnDismissListener;

/**
 * This Class handles the touch events in the Customize-Mode
 * @author artvel
 */
public class CustomizeModusListener implements OnTouchListener{

	private int tClickCount = 0;
	private int tViewId = 0;
	private long tStartTime;
	private long tDuration;
	private IDesktopViewManager tDesktopViewManager;
	private CustomizeModusPopupMenu tCustomizeModusPopup;
	//the single tab handler
	private SingleTabCountDown tSingleTabCountDown = new SingleTabCountDown(this, MIN_DURATION, MIN_DURATION);
	
	public CustomizeModusListener(IDesktopViewManager desktopViewManager, CustomizeModusPopupMenu customizeModusPopup){
		tDesktopViewManager = desktopViewManager;
		tCustomizeModusPopup = customizeModusPopup;
		tCustomizeModusPopup.setOnDismissListener(new OnDismissListener() {
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
        		tSingleTabCountDown.safeCancel();
            	tClickCount = 0;
            	tStartTime=0;
            	dismissPopupIfIsShowing();
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
		dismissPopupIfIsShowing();
		tCustomizeModusPopup.setTouchedView((CustomizableView) tabbedView);
		//TODO Issue #23 
		//replace showAsDropDown with showAtLocation(parent, gravity, x, y)
		//to place the Popup close to the tabbedView in a visible area.
		//To get the max width and max height call tDesktopViewManager.getRootView()
		//To get the Position Params just remove the comment from the following line.
		LayoutParams lp = (LayoutParams) tabbedView.getLayoutParams();
		LayoutParams calclparam = this.calculateCoordinatesForPopup(lp);

		tCustomizeModusPopup.showAtLocation(tabbedView, 3, calclparam.x, calclparam.y);
    	return true;
	}
	
	private LayoutParams calculateCoordinatesForPopup(LayoutParams lparam) {
		int iPosX = 0;
		int iPosY = 0;
		int iMaxHeight = tDesktopViewManager.getRootView().getMeasuredHeight();
		int iMaxWidth = tDesktopViewManager.getRootView().getMeasuredWidth();
		int iPopupHeight = 200; //tCustomizeModusPopup.getHeight();
		int iPopupWidth = 240; //tCustomizeModusPopup.getWidth();
		LayoutParams newlparam = new LayoutParams();

		if ((lparam.y <= 0) && (lparam.x <= 0)){
			iPosY = 0;
			iPosX = lparam.x + lparam.width;
		}
		else {
			if (lparam.y + iPopupHeight > iMaxHeight) {
				iPosY = lparam.y - iPopupHeight;
				
				if (lparam.x + iPopupWidth > iMaxWidth) {
					iPosX = lparam.x - iPopupWidth;
				}
				else {
					if (lparam.x <= 0) {
						iPosX = 0;
					}
					else {
						iPosX = lparam.x;
					}
				}
			}
			else {
				if (lparam.y <= 0) {
					iPosY = 0;
				}
				else {
					iPosY = lparam.y;
				}

				if (lparam.x + lparam.width + iPopupWidth > iMaxWidth) {
					iPosX = lparam.x - iPopupWidth;
				}
				else {
					iPosX = lparam.x + lparam.width;
				}
			}
		}
		
		newlparam.x = iPosX;
		newlparam.y = iPosY;

		return newlparam;
	}

	private boolean doubleTab(View doubleTabbedView){
		tDesktopViewManager.resizeView(doubleTabbedView);
		return true;
	}
	
	private boolean longHold(View holdingView){
		return tDesktopViewManager.dragView(holdingView);
	}
	
	private void dismissPopupIfIsShowing(){
    	if(tCustomizeModusPopup.isShowing())
    		tCustomizeModusPopup.dismiss();
	}
	
	public void release(){
		dismissPopupIfIsShowing();
		tSingleTabCountDown.release();
		tSingleTabCountDown = null;
		tCustomizeModusPopup = null;
		tDesktopViewManager = null;
	}

}
