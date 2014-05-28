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
import android.widget.Toast;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RCConstants;

public class EditChannelActivity extends RCActivity{
	
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
		ll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		TextView tv = new TextView(this);
		tv.setText(label);
		tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		ll.addView(tv);
		EditText et = new EditText(this);
		if(value!=null)
			et.setText(value);
		et.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		ll.addView(et);
		return ll;
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
				finish();
			}
		});
		b = (Button) findViewById(R.id.saveBtn);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(EditChannelActivity.this, "save coming soon!", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	protected String getClassName() {
		return EditChannelActivity.class.getSimpleName();
	}
}
