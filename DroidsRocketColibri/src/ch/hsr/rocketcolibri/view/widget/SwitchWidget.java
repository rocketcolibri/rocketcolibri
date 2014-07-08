package ch.hsr.rocketcolibri.view.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.StateListDrawable;
import android.widget.CompoundButton;
import android.widget.Switch;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RCConstants;
import ch.hsr.rocketcolibri.ui_data.input.UiInputSourceChannel;
import ch.hsr.rocketcolibri.ui_data.output.UiOutputDataType;
import ch.hsr.rocketcolibri.util.DrawingTools;
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.custimizable.ICustomizableView;
import ch.hsr.rocketcolibri.view.custimizable.ModusChangeListener;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;

public class SwitchWidget extends Switch implements ICustomizableView,
		IRCWidget {

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

	private boolean isChannelValid() {
		return tChannel.getAssignment() > -1;
	}

	private ModusChangeListener tModusChangeListener = new ModusChangeListener() {
		@Override
		public void customizeModeDeactivated() {
			if (isChannelValid()) {
				setClickable(true);
				setOnCheckedChangeListener(tSwitchOnCheckedChangeListener);
			} else {
				setClickable(false);
			}
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
						tChannel.calculateChannelValue(tChannel.getMaxRange());
					} else {
						tChannel.calculateChannelValue(tChannel.getMinRange());
				}
			}catch(Exception e){}
		}
	};

	public void initProtocolMapping() {
		// init protocol mapping
		tWidgetConfig.protocolMap = new HashMap<String, String>();
		tWidgetConfig.protocolMap.put(RCConstants.CHANNEL_ASSIGNMENT, "");
		tWidgetConfig.protocolMap.put(RCConstants.MAX_RANGE, "");
		tWidgetConfig.protocolMap.put(RCConstants.MIN_RANGE, "");
		tWidgetConfig.protocolMap.put(RCConstants.DEFAULT_POSITION, "");
	}

	private void init(ViewElementConfig elementConfig) {
		setLayoutParams(elementConfig.getLayoutParams());
		setAlpha(elementConfig.getAlpha());
		createWidget();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (tCustomizeModusActive)
			DrawingTools.drawCustomizableForground(this, canvas);
	}

	@Override
	public void updateProtocolMap() {
		try {
			tChannel
					.setAssignment(getProtocolMapInt(RCConstants.CHANNEL_ASSIGNMENT));
			tChannel.setMaxRange(getProtocolMapInt(RCConstants.MAX_RANGE));
			tChannel.setMinRange(getProtocolMapInt(RCConstants.MIN_RANGE));
			tChannel.setDefaultPosition(getProtocolMapInt(RCConstants.DEFAULT_POSITION));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getNumberOfChannelListener() {
		return 1;
	}

	private void createWidget() {
		StateListDrawable states = new StateListDrawable();
		states.addState(new int[] {-android.R.attr.state_pressed},
		    getResources().getDrawable(R.drawable.apptheme_switch_thumb_pressed_holo_light));
		states.addState(new int[] {-android.R.attr.state_focused},
		    getResources().getDrawable(R.drawable.apptheme_switch_thumb_activated_holo_light));
		states.addState(new int[] { },
		    getResources().getDrawable(R.drawable.apptheme_switch_thumb_disabled_holo_light));
		setThumbDrawable(states);
		refreshDrawableState();

		if (isChannelValid()) {
			setOnCheckedChangeListener(tSwitchOnCheckedChangeListener);
			setClickable(true);

			if (tChannel.getDefaultPosition() > 0) {
				setChecked(true);
			} else {
				setChecked(false);
			}
		} else {
			setChecked(false);
			setClickable(false);
		}
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

	@Override
	public void onNotifyUiOutputSink(Object data) {
	}

	@Override
	public UiOutputDataType getType() {
		return UiOutputDataType.None;
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
		setOnTouchListener(customizeModusListener);
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
	public void setModusChangeListener(ModusChangeListener mcl) {
		tModusChangeListener = mcl;
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
