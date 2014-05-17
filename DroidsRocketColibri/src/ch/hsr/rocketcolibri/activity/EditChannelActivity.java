package ch.hsr.rocketcolibri.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import ch.hsr.rocketcolibri.R;

public class EditChannelActivity extends RCActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showLoading("Loading...");
		setContentView(R.layout.edit_channel);
		initCloseButton();
		readWidgetSettings();
	}

	private void readWidgetSettings() {
		// TODO read widget settings
		
	}

	@Override
	protected void onServiceReady() {
//		rcService.
		
		//just to test the loading sequence
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				SystemClock.sleep(2000);
				hideLoading();				
				return null;
			}
		}.execute();
	}
	
	private void initCloseButton(){
		Button b = (Button) findViewById(R.id.btn_close);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected String getClassName() {
		return EditChannelActivity.class.getSimpleName();
	}
}
