package ch.hsr.rocketcolibri.view.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.CompoundButton;
import android.widget.Switch;
import ch.hsr.rocketcolibri.RCConstants;
import ch.hsr.rocketcolibri.ui_data.input.UiInputSourceChannel;
import ch.hsr.rocketcolibri.util.DrawingTools;
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.custimizable.ICustomizableView;
import ch.hsr.rocketcolibri.view.custimizable.ModusChangeListener;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;

public class SwitchWidget extends Switch implements ICustomizableView,
		IRCWidget {
	private boolean tDebug;
	private Paint dbgLine;

	private static final int tSwitchMax = 999;
	private static final int tSwitchMin = 0;
	
	protected RCWidgetConfig tWidgetConfig;
	private UiInputSourceChannel tChannel = new UiInputSourceChannel();

	private boolean tCustomizeModusActive = false;
	protected OnTouchListener tCustomizeModusListener;

	public SwitchWidget(Context context, ViewElementConfig elementConfig) {
		super(context);
		tWidgetConfig = new RCWidgetConfig(elementConfig);
		initProtocolMapping();
		init(elementConfig);
	}

	public SwitchWidget(Context context, RCWidgetConfig widgetConfig) {
		super(context);
		tWidgetConfig = widgetConfig;
		updateProtocolMap();
		init(tWidgetConfig.viewElementConfig);
	}

	private ModusChangeListener tModusChangeListener = new ModusChangeListener() {
		@Override
		public void customizeModeDeactivated() {
			setClickable(true);
			setOnCheckedChangeListener(tSwitchOnCheckedChangeListener);
		}

		@Override
		public void customizeModeActivated() {
			setOnTouchListener(tCustomizeModusListener);
		}
	};

	private OnCheckedChangeListener tSwitchOnCheckedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			try{
				if (isChecked) {
						tChannel.setWidgetPosition(tSwitchMax);
					} else {
						tChannel.setWidgetPosition(tSwitchMin);
				}
			}catch(Exception e){}
		}
	};

	public void initProtocolMapping() {
		// init protocol mapping
		tWidgetConfig.protocolMap = new HashMap<String, String>();
		if (UiInputSourceChannel.CHANNEL_UNASSIGNED == tChannel.getChannelAssignment() )
			tWidgetConfig.protocolMap.put(RCConstants.CHANNEL_ASSIGNMENT, "");
		else
			tWidgetConfig.protocolMap.put(RCConstants.CHANNEL_ASSIGNMENT, Integer.valueOf(tChannel.getChannelAssignment()).toString());
		tWidgetConfig.protocolMap.put(RCConstants.INVERTED, Boolean.valueOf(tChannel.getChannelInverted()).toString());
		tWidgetConfig.protocolMap.put(RCConstants.MAX_RANGE, Integer.valueOf(tChannel.getChannelMaxRange()).toString());
		tWidgetConfig.protocolMap.put(RCConstants.MIN_RANGE, Integer.valueOf(tChannel.getChannelMinRange()).toString());
		tWidgetConfig.protocolMap.put(RCConstants.DEFAULT_POSITION, Integer.valueOf(tChannel.getChannelDefaultPosition()).toString());
		tWidgetConfig.protocolMap.put(RCConstants.DEBUG, Boolean.valueOf(false).toString());
	}

	private void init(ViewElementConfig elementConfig) {
		setLayoutParams(elementConfig.getLayoutParams());
		setAlpha(elementConfig.getAlpha());
		tChannel.setWidgetRange(tSwitchMin,tSwitchMax);
		createWidget();

		dbgLine = new Paint(Paint.ANTI_ALIAS_FLAG);
		dbgLine.setStrokeWidth(1);
		dbgLine.setStyle(Paint.Style.FILL_AND_STROKE);
		dbgLine.setTextSize(getPixels(15));
	}
	
	private int getPixels(float size) {
	    DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
	    return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, size, metrics);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (tCustomizeModusActive)
			DrawingTools.drawCustomizableForground(this, canvas);
		
		if (tDebug) {
			String dbgString;
			if (UiInputSourceChannel.CHANNEL_UNASSIGNED == tChannel.getChannelAssignment() ){
				dbgString = String.format(Locale.getDefault(),"unassigned", tChannel.getChannelValue());
				dbgLine.setColor(Color.RED);
			}
			else{
				dbgString = String.format(Locale.getDefault(), "C[%d]:%d", tChannel.getChannelAssignment(), tChannel.getChannelValue());
				dbgLine.setColor(Color.GREEN);
			}
			canvas.drawText(dbgString,0, getHeight()/2, dbgLine);
		}
	}

	@Override
	public void updateProtocolMap() {
		try {
			tChannel.setChannelAssignment(getProtocolMapInt(RCConstants.CHANNEL_ASSIGNMENT));
			tChannel.setChannelMaxRange(getProtocolMapInt(RCConstants.MAX_RANGE));
			tChannel.setChannelMinRange(getProtocolMapInt(RCConstants.MIN_RANGE));
			tChannel.setChannelDefaultPosition(getProtocolMapInt(RCConstants.DEFAULT_POSITION));
			tChannel.setChannelInverted(getProtocolMapBoolean(RCConstants.INVERTED));
			tDebug = getProtocolMapBoolean(RCConstants.DEBUG);
			postInvalidate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getNumberOfChannelListener() {
		return 1;
	}

	private void createWidget() {
//		StateListDrawable states = new StateListDrawable();
//		states.addState(new int[] {-android.R.attr.state_pressed},
//		    getResources().getDrawable(R.drawable.apptheme_switch_thumb_pressed_holo_light));
//		states.addState(new int[] {-android.R.attr.state_focused},
//		    getResources().getDrawable(R.drawable.apptheme_switch_thumb_activated_holo_light));
//		states.addState(new int[] { },
//		    getResources().getDrawable(R.drawable.apptheme_switch_thumb_disabled_holo_light));
//		setThumbDrawable(states);
//		refreshDrawableState();
		setOnCheckedChangeListener(tSwitchOnCheckedChangeListener);
		setClickable(true);
		setChecked(tChannel.getChannelDefaultPosition() > 0);
	}

	public static ViewElementConfig getDefaultViewElementConfig() {
		return DefaultViewElementConfigRepo.getDefaultConfig(SwitchWidget.class);
	}

	@Override
	public void create(RCWidgetConfig rcWidgetConfig) {
		tWidgetConfig = rcWidgetConfig;
		updateProtocolMap();
		init(tWidgetConfig.viewElementConfig);
	}

	@Override
	public void create(ViewElementConfig vElementConfig) {
		tWidgetConfig = new RCWidgetConfig(vElementConfig);
		initProtocolMapping();
		init(vElementConfig);
	}

	@Override
	public void setProtocolMap(Map<String, String> protocolMap) {
		tWidgetConfig.protocolMap = protocolMap;
		updateProtocolMap();
	}

	protected int getProtocolMapInt(String key) {
		try {
			return Integer.parseInt(tWidgetConfig.protocolMap.get(key));
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	protected boolean getProtocolMapBoolean(String key) {
		try {
			return Boolean.parseBoolean(tWidgetConfig.protocolMap.get(key));
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public RCWidgetConfig getWidgetConfig() {
		tWidgetConfig.viewElementConfig = this.getViewElementConfig();
		return tWidgetConfig;
	}

	@Override
	public Map<String, String> getProtocolMap() {
		return tWidgetConfig.protocolMap;
	}

	@Override
	public void setCustomizeModusListener(OnTouchListener customizeModusListener) {
		tCustomizeModusListener = customizeModusListener;
	}

	@Override
	public void setCustomizeModus(boolean enabled) {
		if (tCustomizeModusActive != enabled) {
			if (enabled) {
				tModusChangeListener.customizeModeActivated();
			} else {
				tModusChangeListener.customizeModeDeactivated();
			}
			invalidate();
			tCustomizeModusActive = enabled;
		}
	}

	@Override
	public ViewElementConfig getViewElementConfig() {
		tWidgetConfig.viewElementConfig
				.setLayoutParams((LayoutParams) getLayoutParams());
		tWidgetConfig.viewElementConfig.setAlpha(getAlpha());
		return tWidgetConfig.viewElementConfig;
	}

	@Override
	public List<UiInputSourceChannel> getUiInputSourceList() {
		List<UiInputSourceChannel> list = new ArrayList<UiInputSourceChannel>();
		list.add(tChannel);
	    return list;
	}
}
