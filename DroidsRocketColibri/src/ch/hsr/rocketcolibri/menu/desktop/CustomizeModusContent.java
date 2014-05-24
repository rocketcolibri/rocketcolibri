package ch.hsr.rocketcolibri.menu.desktop;

import java.util.List;

import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.view.AbsoluteLayout;
import ch.hsr.rocketcolibri.view.custimizable.CustomizableView;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import ch.hsr.rocketcolibri.view.resizable.ResizeConfig;
import ch.hsr.rocketcolibri.widgetdirectory.WidgetEntry;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.Toast;

public class CustomizeModusContent extends ModusContent{
	
	public CustomizeModusContent(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onCreate(List<WidgetEntry> widgetEntries) {
		GridLayout gLayout = (GridLayout) findViewById(R.id.widgetEntryContent);
		for(WidgetEntry wEntry : widgetEntries){ 
			try {
				Log.d("wEntry", ""+wEntry.getClassPath());
				CustomizableView view = createIconView(wEntry.getClassPath(), 300, 300);
				view.setBackgroundResource(R.drawable.border);
				view.setOnTouchListener(new WidgetTouchListener(wEntry));
				gLayout.addView(view);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

	}
	
	class WidgetTouchListener implements View.OnTouchListener{
		private WidgetEntry tWidgetEntry;
		
		public WidgetTouchListener(WidgetEntry wEntry){
			tWidgetEntry = wEntry;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch(event.getAction()){
			case MotionEvent.ACTION_UP:
				tDesktopViewManager.getDesktopMenu().animateToggle();
				try {
					setupView(tWidgetEntry, event);
					Toast.makeText(tContext, tWidgetEntry.getLabelText(), Toast.LENGTH_SHORT).show();
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
			return true;
		}
		
	}
	
	/**
	 * @return View
	 * This View is meant for showing how the Widget will look like
	 * not for interaction with the View itself.
	 * @throws Exception 
	 */
	private CustomizableView createIconView(String widgetClassPath, int width, int height) throws Exception{
		return tDesktopViewManager.createView(createDemoViewElementConfig(widgetClassPath, width, height));
	}
	
	private ViewElementConfig createDemoViewElementConfig(String widgetClassPath, int width, int height){
		AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(width, height, 0, 0);
		ViewElementConfig vElementConfig = new ViewElementConfig(widgetClassPath, lp, getDefaultResizeConfig());
		return vElementConfig;
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
	
	private CustomizableView setupView(WidgetEntry we, MotionEvent e) throws Exception{
		CustomizableView v1 = (CustomizableView) tDesktopViewManager.createAndAddView(getDemoConfig(we));
		AbsoluteLayout rootView = tDesktopViewManager.getRootView();
		AbsoluteLayout.LayoutParams lp = (AbsoluteLayout.LayoutParams) v1.getLayoutParams();
		lp.x = (int) (rootView.getWidth()/2)-lp.width/2;
		lp.y = (int) (rootView.getHeight()/2)-lp.height/2;
		v1.setCustomizeModus(true);
		return v1;
	}
	
	private ViewElementConfig getDemoConfig(WidgetEntry we){
		ResizeConfig rc = new ResizeConfig();
	    rc.keepRatio=true;
	    rc.maxHeight=500;
	    rc.minHeight=50;
	    rc.maxWidth=500;
	    rc.minWidth=50;
	    AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(380, 380 , 0, 0);
	    ViewElementConfig elementConfig = new ViewElementConfig(we.getClassPath(), lp, rc);
		return elementConfig;
	}
	
}
