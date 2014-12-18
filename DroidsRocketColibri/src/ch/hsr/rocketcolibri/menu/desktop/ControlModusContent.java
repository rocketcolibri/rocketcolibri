package ch.hsr.rocketcolibri.menu.desktop;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RCConstants;
import ch.hsr.rocketcolibri.activity.DesktopActivity;
import ch.hsr.rocketcolibri.activity.ModelListActivity;
import ch.hsr.rocketcolibri.activity.SetupConnectionActivity;
import ch.hsr.rocketcolibri.db.RocketColibriDataHandler;
import ch.hsr.rocketcolibri.view.custimizable.ICustomizableView;
import ch.hsr.rocketcolibri.widgetdirectory.WidgetEntry;
import android.app.Activity;
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
		findViewById(R.id.setupConnection).setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if(tDesktopMenu.getService()!=null){
					try {
						
						Intent i = new Intent(tContext, SetupConnectionActivity.class);
						i.putExtra(RCConstants.FLAG_ACTIVITY_RC_MODEL, ((DesktopActivity)tContext).getDefaultModelName());
						i.putExtra(RCConstants.AUTOCONNECT, true);
						i.putExtra(RCConstants.IP_SERVOCONTROLLER, "192.168.200.1");
						i.putExtra(RCConstants.PORT_SERVOCONTROLLER, "3000");
						
						// TODO read settings from model
						
						((DesktopActivity) tContext).startActivityForResult(i, RCConstants.RC_MODEL_RESULT_CODE);
						tContext.startActivity(i);
						
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

}
