package ch.hsr.rocketcolibri.menu.desktop;

import java.io.File;
import java.util.List;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.activity.DesktopActivity;
import ch.hsr.rocketcolibri.db.RocketColibriDataHandler;
import ch.hsr.rocketcolibri.widgetdirectory.WidgetEntry;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
		findViewById(R.id.shareModelsBtn).setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if(tDesktopMenu.getService()!=null){
					try {
						RocketColibriDataHandler rcDataHandler = new RocketColibriDataHandler(tContext, tDesktopMenu.getService().getRocketColibriDB(), false);
						Intent sendIntent = new Intent();
						sendIntent.setAction(Intent.ACTION_SEND);
						File shareFile = rcDataHandler.exportDataToFile();
						shareFile.setReadable(true, false);
						sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(shareFile));
						sendIntent.setType("text/plain");
						sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
						tContext.startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.export_models)));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

}
