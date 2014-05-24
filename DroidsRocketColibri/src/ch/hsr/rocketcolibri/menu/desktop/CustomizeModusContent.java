package ch.hsr.rocketcolibri.menu.desktop;

import java.util.List;

import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.activity.DesktopActivity;
import ch.hsr.rocketcolibri.manager.IDesktopViewManager;
import ch.hsr.rocketcolibri.view.AbsoluteLayout;
import ch.hsr.rocketcolibri.view.custimizable.CustomizableView;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import ch.hsr.rocketcolibri.view.resizable.ResizeConfig;
import ch.hsr.rocketcolibri.widgetdirectory.WidgetEntry;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

public class CustomizeModusContent extends ModusContent{
	
	public CustomizeModusContent(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onCreate(List<WidgetEntry> widgetEntries) {
		ToggleButton tb = (ToggleButton)findViewById(R.id.testElement);
		LinearLayout b = new LinearLayout(tContext);
		b.setLayoutParams(new LayoutParams(300, 300));
		b.setOrientation(LinearLayout.VERTICAL);
		b.setBackgroundColor(Color.GRAY);
		for(WidgetEntry wEntry : widgetEntries){
			try {
				Log.d("wEntry", ""+wEntry.getClassPath());
				CustomizableView view = createIconView(wEntry.getClassPath(), 300, 300);
				b.addView(view);
			
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		this.addView(b);
		tb.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction() & MotionEvent.ACTION_MASK){
				case MotionEvent.ACTION_UP:
					tDesktopViewManager.getDesktopMenu().animateToggle();
					try {
						setupView(event);
						return true;
					} catch (Exception e) {
						return false;
					}
				}
				return false;
			}				
		});
	}
	
	private CustomizableView setupView(MotionEvent e) throws Exception{
		CustomizableView v1 = (CustomizableView) tDesktopViewManager.createAndAddView(getDemoConfig());
		AbsoluteLayout rootView = tDesktopViewManager.getRootView();
		
		v1.setCustomizeModus(true);
		Toast.makeText(tContext, "X:"+e.getX()+" Y:"+e.getY(), Toast.LENGTH_SHORT).show();
		AbsoluteLayout.LayoutParams lp = (ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams) v1.getLayoutParams();
		lp.x = (int) (rootView.getWidth()/2)-lp.width/2;
		lp.y = (int) (rootView.getHeight()/2)-lp.height/2;
		v1.invalidate();
		return v1;
	}
	
	private ViewElementConfig getDemoConfig(){
		ResizeConfig rc = new ResizeConfig();
	    rc.keepRatio=true;
	    rc.maxHeight=500;
	    rc.minHeight=50;
	    rc.maxWidth=500;
	    rc.minWidth=50;
	    AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(380, 380 , 0, 0);
	    ViewElementConfig elementConfig = new ViewElementConfig("ch.hsr.rocketcolibri.view.widget.Circle", lp, rc);
		return elementConfig;
	}
	
	/**
	 * @return View
	 * This View is meant for showing how the Widget will look like
	 * not for interaction with the View itself.
	 * @throws Exception 
	 */
	private CustomizableView createIconView(String widgetClassPath, int width, int height) throws Exception{
		AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(width, height, 0, 0);
		ViewElementConfig vElementConfig = new ViewElementConfig(widgetClassPath, lp, getDefaultResizeConfig());
		return tDesktopViewManager.createView(vElementConfig);
	}
	
	/**
	 * This Method returns a never used ResizeConfig, its needs to be set
	 * to the ViewElementConfig to avoid unexpected Exceptions.
	 * Its not because we create the View just to show how the Widget will look like
	 * not for interaction with the View itself.
	 * @return
	 */
	private ResizeConfig getDefaultResizeConfig(){
		ResizeConfig rc = new ResizeConfig();
	    rc.keepRatio=true;
	    rc.maxHeight=500;
	    rc.minHeight=50;
	    rc.maxWidth=500;
	    rc.minWidth=50;
	    return rc;
	}
	
}
