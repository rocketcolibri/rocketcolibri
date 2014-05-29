package ch.hsr.rocketcolibri.activity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RCConstants;

public class EditChannelActivity extends RCActivity{
	private Map<String, Integer> channelViewMap = new HashMap<String, Integer>();
	private int viewIdStart = 999999;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showLoading("Loading...");
		setContentView(R.layout.edit_channel);
		initFooterButtons();
		readWidgetSettings();
	}

	private void readWidgetSettings() {
		LinearLayout contentList = (LinearLayout)findViewById(R.id.content_list);
		Intent i = getIntent();
		Set<String> keySet = i.getExtras().keySet();
		for(String key : keySet){
			if(key.startsWith(RCConstants.PREFIX)){
				i.getStringExtra(key);
				contentList.addView(createLayout(getString(getStringResourceIdOf(key)), key, i.getStringExtra(key)));
			}
		}
	}
	
	private int getStringResourceIdOf(String key){
		return getResources().getIdentifier(key, "string", RCConstants.class.getPackage().getName());
	}
	
	private LinearLayout createLayout(String label, String key, String value){
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		ll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		TextView tv = new TextView(this);
		tv.setText(label);
		tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		ll.addView(tv);
		EditText et = new EditText(this);
		channelViewMap.put(key, Integer.valueOf(viewIdStart));
		et.setId(viewIdStart);
		++viewIdStart;
		if(value!=null)
			et.setText(value);
		et.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		ll.addView(et);
		return ll;
	}
	
	private void fillResultIntent(){
		Set<String> keySet = channelViewMap.keySet();
		Intent resultIntent = new Intent(getIntent().getAction());
		EditText tv = null;
		for(String key : keySet){
			tv = (EditText) findViewById(channelViewMap.get(key).intValue());
			resultIntent.putExtra(key, tv.getText().toString());
		}
		setResult(RESULT_OK, resultIntent);
	}
	
	@Override
	protected void onServiceReady() {
		//just to test the loading sequence
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				SystemClock.sleep(500);
				hideLoading();				
				return null;
			}
		}.execute();
	}
	
	private void initFooterButtons(){
		Button b = (Button) findViewById(R.id.closeBtn);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		b = (Button) findViewById(R.id.saveBtn);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(EditChannelActivity.this, "saved!", Toast.LENGTH_SHORT).show();
				fillResultIntent();
				finish();
			}
		});
	}

	@Override
	protected String getClassName() {
		return EditChannelActivity.class.getSimpleName();
	}
}
