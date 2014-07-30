package ch.hsr.rocketcolibri.menu.desktop;

import java.util.List;

import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RCConstants;
import ch.hsr.rocketcolibri.activity.ModelListActivity;
import ch.hsr.rocketcolibri.widgetdirectory.WidgetEntry;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
				Intent i = new Intent(tContext, ModelListActivity.class);
				((Activity)tContext).startActivityForResult(i, RCConstants.RC_MODEL_RESULT_CODE);
				
			}
		});
	}

}
