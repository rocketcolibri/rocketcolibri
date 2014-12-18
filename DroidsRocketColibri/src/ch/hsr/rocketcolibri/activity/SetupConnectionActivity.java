/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.activity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RCConstants;
import ch.hsr.rocketcolibri.db.RocketColibriDataHandler;
import ch.hsr.rocketcolibri.db.model.Defaults;
import ch.hsr.rocketcolibri.view.custimizable.ICustomizableView;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

/**
 * @author Lorenz Schelling
 */
public class SetupConnectionActivity extends RCActivity{
	private Map<String, View> channelViewMap = new HashMap<String, View>();

	private Semaphore waitForServiceSem = new Semaphore(0);
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		tFullscreen = false;
		super.onCreate(savedInstanceState);
		showLoading();
		setContentView(new View(this));
		
		setContentView(R.layout.edit_channel);
		initFooterButtons();
		readWidgetSettings();
	}
	

	
	private void readWidgetSettings() {
		GridLayout contentList = (GridLayout)findViewById(R.id.content_list);
		Intent i = getIntent();
		Set<String> keySet = i.getExtras().keySet();
		String labelText = null;
		
		// TODO first check if AUTOCONNECT is false
		// -> set IP and PORT_SERVOCONTROLLER enabled
		
		
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
		hideLoading();
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
				Toast.makeText(SetupConnectionActivity.this, getString(R.string.edit_acivity_saved), Toast.LENGTH_SHORT).show();
				fillResultIntent();
				finish();
			}
		});
	}

	@Override
	protected String getClassName() {
		return SetupConnectionActivity.class.getSimpleName();
	}
}
