package ch.hsr.rocketcolibri.view.widget;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.CompoundButton;
import android.widget.Switch;
import ch.hsr.rocketcolibri.RCConstants;
import ch.hsr.rocketcolibri.ui_data.input.Channel;
import ch.hsr.rocketcolibri.ui_data.output.UiOutputDataType;
import ch.hsr.rocketcolibri.util.DrawingTools;
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.custimizable.ICustomizableView;
import ch.hsr.rocketcolibri.view.custimizable.ModusChangeListener;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;

public class SwitchWidget extends Switch implements ICustomizableView,
		IRCWidget {

	protected RCWidgetConfig tWidgetConfig;
	private Channel tChannelH = new Channel();

	private OnChannelChangeListener tControlModusListener;
	private boolean tCustomizeModusActive = false;
	protected OnTouchListener tCustomizeModusListener;
	private SwitchOnCheckedChangeListener tSwitchOnCheckedChangeListener = new SwitchOnCheckedChangeListener();

	public SwitchWidget(Context context, ViewElementConfig elementConfig) {
		super(context);
		tWidgetConfig = new RCWidgetConfig(elementConfig);
		setLayoutParams(elementConfig.getLayoutParams());
		setAlpha(elementConfig.getAlpha());
		initProtocolMapping();
		init();
	}

	public SwitchWidget(Context context, RCWidgetConfig widgetConfig) {
		super(context);
		tWidgetConfig = widgetConfig;
		setLayoutParams(tWidgetConfig.viewElementConfig.getLayoutParams());
		setAlpha(tWidgetConfig.viewElementConfig.getAlpha());
		updateProtocolMap();
		init();
	}

	private boolean isChannelValid() {
		return tChannelH.getDefaultChannelValue() > -1;
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

	class SwitchOnCheckedChangeListener implements OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {

			if (isChecked) {
				tControlModusListener.onChannelChange(
						tChannelH.getDefaultChannelValue(),
						tChannelH.getMaxRange());
			} else {
				tControlModusListener.onChannelChange(
						tChannelH.getDefaultChannelValue(),
						tChannelH.getMinRange());
			}
		}
	}

	public void initProtocolMapping() {
		// init protocol mapping
		tWidgetConfig.protocolMap = new HashMap<String, String>();
		tWidgetConfig.protocolMap.put(RCConstants.CHANNEL_H, "");
		tWidgetConfig.protocolMap.put(RCConstants.INVERTED_H, "");
		tWidgetConfig.protocolMap.put(RCConstants.MAX_RANGE_H, "");
		tWidgetConfig.protocolMap.put(RCConstants.MIN_RANGE_H, "");
		tWidgetConfig.protocolMap.put(RCConstants.TRIMM_H, "");
	}

	private void init() {
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
			tChannelH
					.setDefaultChannelValue(getProtocolMapInt(RCConstants.CHANNEL_H));
			tChannelH
					.setInverted(getProtocolMapBoolean(RCConstants.INVERTED_H));
			tChannelH.setMaxRange(getProtocolMapInt(RCConstants.MAX_RANGE_H));
			tChannelH.setMinRange(getProtocolMapInt(RCConstants.MIN_RANGE_H));
			tChannelH.setTrimm(getProtocolMapInt(RCConstants.TRIMM_H));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getNumberOfChannelListener() {
		return 1;
	}

	private void createWidget() {
		setLayoutParams(tWidgetConfig.viewElementConfig.getLayoutParams());
		setAlpha(tWidgetConfig.viewElementConfig.getAlpha());

		if (isChannelValid()) {
			setOnCheckedChangeListener(tSwitchOnCheckedChangeListener);
			setClickable(true);

			if (tChannelH.getDefaultChannelValue() > 0) {
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
	public void setControlModusListener(OnChannelChangeListener channelListener) {
		tControlModusListener = channelListener;
	}

	@Override
	public void create(RCWidgetConfig rcWidgetConfig) {
		tWidgetConfig = rcWidgetConfig;
		setLayoutParams(tWidgetConfig.viewElementConfig.getLayoutParams());
		setAlpha(tWidgetConfig.viewElementConfig.getAlpha());
		init();
		updateProtocolMap();
	}

	@Override
	public void create(ViewElementConfig vElementConfig) {
		tWidgetConfig = new RCWidgetConfig(vElementConfig);
		setLayoutParams(vElementConfig.getLayoutParams());
		setAlpha(vElementConfig.getAlpha());
		init();
		initProtocolMapping();
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
}