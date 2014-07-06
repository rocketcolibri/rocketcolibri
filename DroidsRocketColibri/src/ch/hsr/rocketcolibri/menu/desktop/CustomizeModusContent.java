package ch.hsr.rocketcolibri.menu.desktop;

import java.io.FileNotFoundException;
import java.util.List;

import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RocketColibriDefaults;
import ch.hsr.rocketcolibri.util.CacheUtil;
import ch.hsr.rocketcolibri.view.AbsoluteLayout;
import ch.hsr.rocketcolibri.view.custimizable.ICustomizableView;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import ch.hsr.rocketcolibri.view.widget.IRCWidget;
import ch.hsr.rocketcolibri.widgetdirectory.WidgetEntry;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

public class CustomizeModusContent extends ModusContent{
	
	public CustomizeModusContent(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onCreate(List<WidgetEntry> widgetEntries) {
		GridLayout gLayout = (GridLayout) findViewById(R.id.widgetEntryContent);
		CacheUtil cacheUtil = new CacheUtil(tContext);
		int width = RocketColibriDefaults.dpToPixel(tContext.getResources().getDisplayMetrics(), 80);
		int height = RocketColibriDefaults.dpToPixel(tContext.getResources().getDisplayMetrics(), 80);
		for(WidgetEntry wEntry : widgetEntries){
			try {
				ImageView iView = new ImageView(tContext);
				iView.setLayoutParams(new LayoutParams(width, height));
				iView.setScaleType(ScaleType.CENTER);
				Bitmap viewBitmap = null;
				try{
					viewBitmap = cacheUtil.loadBitmap(wEntry.getClassPath());
					Log.d("loadBitmap", ""+wEntry.getClassPath());
				}catch(FileNotFoundException e){ 
					Log.d("createBitmap", ""+wEntry.getClassPath());
					viewBitmap = cacheUtil.createBitmap((View)createIconView(wEntry, width, height), wEntry.getClassPath());
				}
				iView.setImageBitmap(viewBitmap);
				iView.setBackgroundResource(R.drawable.border);
				iView.setOnTouchListener(new WidgetTouchListener(wEntry));
				gLayout.addView(iView);
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
	private IRCWidget createIconView(WidgetEntry wEntry, int width, int height) throws Exception{
		ViewElementConfig vec = wEntry.getDefaultViewElementConfig();
		vec.setLayoutParams(new AbsoluteLayout.LayoutParams(width, height, 0, 0));
		return tDesktopViewManager.createView(vec);
	}
	
	private ICustomizableView setupView(WidgetEntry we, MotionEvent e) throws Exception{
		ViewElementConfig vec = we.getDefaultViewElementConfig();
		AbsoluteLayout.LayoutParams lp = vec.getLayoutParams();
		AbsoluteLayout rootView = tDesktopViewManager.getRootView();
		lp.x = (int) (rootView.getWidth()/2)-lp.width/2;
		lp.y = (int) (rootView.getHeight()/2)-lp.height/2;
		ICustomizableView v1 = (ICustomizableView) tDesktopViewManager.createAndAddView(vec);
		v1.setCustomizeModus(true);
		return v1;
	}
	
}
