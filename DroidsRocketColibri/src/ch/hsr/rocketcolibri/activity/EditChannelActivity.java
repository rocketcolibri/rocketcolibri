package ch.hsr.rocketcolibri.activity;

import java.util.Set;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RCConstants;

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
		LinearLayout contentList = (LinearLayout)findViewById(R.id.content_list);
		Intent i = getIntent();
		Set<String> keySet = i.getExtras().keySet();
		for(String key : keySet){
			if(key.startsWith(RCConstants.PREFIX)){
				i.getStringExtra(key);
				contentList.addView(createLayout(getString(getStringResourceIdOf(key)), i.getStringExtra(key)));
			}
		}
	}
	
	private int getStringResourceIdOf(String key){
		return getResources().getIdentifier(key, "string", RCConstants.class.getPackage().getName());
	}
	
	private LinearLayout createLayout(String label, String value){
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		ll.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
		ll.getLayoutParams().width = LayoutParams.MATCH_PARENT;
		TextView tv = new TextView(this);
		tv.setText(label);
		ll.addView(tv);
		EditText et = new EditText(this);
		if(value!=null)
			et.setText(value);
		ll.addView(et);
		return ll;
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
