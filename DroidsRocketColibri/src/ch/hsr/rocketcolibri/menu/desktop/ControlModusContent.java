package ch.hsr.rocketcolibri.menu.desktop;

import java.util.List;

import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.activity.DesktopActivity;
import ch.hsr.rocketcolibri.widgetdirectory.WidgetEntry;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class ControlModusContent extends ModusContent {

	public ControlModusContent(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onCreate(List<WidgetEntry> widgetEntries) {
		findViewById(R.id.modelListBtn).setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				((DesktopActivity)tContext).openModelListActivity();
			}
		});
	}

}
