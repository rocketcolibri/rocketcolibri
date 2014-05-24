/**
 * Rocket Colibri © 2014
 * 
 * @author Naxxx
 */
package ch.hsr.rocketcolibri.menu.desktop;

import java.util.List;

import ch.hsr.rocketcolibri.activity.DesktopActivity;
import ch.hsr.rocketcolibri.manager.IDesktopViewManager;
import ch.hsr.rocketcolibri.widgetdirectory.WidgetEntry;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

public abstract class ModusContent extends LinearLayout {
	protected Context tContext;
	protected IDesktopViewManager tDesktopViewManager;

	public ModusContent(Context context) {
		super(context);
		tContext = context;
	}

	public ModusContent(Context context, AttributeSet attrs) {
		super(context, attrs);
		tContext = context;
	}

	public ModusContent(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		tContext = context;
	}
	
	public void create(List<WidgetEntry> widgetEntries){
		tDesktopViewManager = ((DesktopActivity)tContext).getDesktopViewManager();
		Log.d("ModusContent", "dvm: "+tDesktopViewManager);
		onCreate(widgetEntries);
	}
	
	protected abstract void onCreate(List<WidgetEntry> widgetEntries);

}
