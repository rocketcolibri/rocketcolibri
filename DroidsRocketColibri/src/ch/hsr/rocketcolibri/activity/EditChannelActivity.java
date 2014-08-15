package ch.hsr.rocketcolibri.activity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RCConstants;

public class EditChannelActivity extends RCActivity{
	private Map<String, View> channelViewMap = new HashMap<String, View>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showLoading("Loading...");
		setContentView(R.layout.edit_channel);
		initFooterButtons();
		readWidgetSettings();
	}

	private void readWidgetSettings() {
		GridLayout contentList = (GridLayout)findViewById(R.id.content_list);
		Intent i = getIntent();
		Set<String> keySet = i.getExtras().keySet();
		String labelText = null;
		for(String key : keySet){
			if(key.startsWith(RCConstants.PREFIX)){
				i.getStringExtra(key);
				try{labelText = getString(getStringResourceIdOf(key));}catch(Exception e){labelText="undefined";};
				contentList.addView(createLabelView(labelText));
				contentList.addView(createInputView(key, i.getStringExtra(key)));
			}
		}
	}
	
	private int getStringResourceIdOf(String key){
		return getResources().getIdentifier(key, "string", RCConstants.class.getPackage().getName());
	}
	
	private View createLabelView(String value){
		TextView tv = new TextView(this);
		tv.setText(value);
		tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		return tv;
	}
	
	private View createInputView(String key, String value){
		switch(RCConstants.getDataTypeOf(key)){
		case BOOLEAN:
			final Switch switc = new Switch(this){
				public String getText(){
					return Boolean.toString(this.isChecked());
				}
			};
			switc.setChecked(Boolean.parseBoolean(value));
			switc.setGravity(Gravity.CENTER_VERTICAL);
			channelViewMap.put(key, switc);
			return switc;
		case DOUBLE:
		case FLOAT:
			return createEditTextWithInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL, key, value);
		case INT:
			return createEditTextWithInputType(InputType.TYPE_NUMBER_FLAG_SIGNED, key, value);
		default:
			return createEditTextWithInputType(InputType.TYPE_CLASS_TEXT, key, value);
		}
	}
	
	private View createEditTextWithInputType(int inputType, String key, String value){
		EditText tv = new EditText(this);
//		tv.setInputType(inputType);
//		tv.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
		tv.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
//		tv.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
		tv.setText(value);
		tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		
		// set the cursor at the end..
		int textLength = tv.getText().length();
		tv.setSelection(textLength, textLength);
		channelViewMap.put(key, tv);
		return tv;
	}
	
	private void fillResultIntent(){
		Set<String> keySet = channelViewMap.keySet();
		Intent resultIntent = new Intent(getIntent().getAction());
		TextView tv = null;
		for(String key : keySet){
			tv = (TextView) channelViewMap.get(key);
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
