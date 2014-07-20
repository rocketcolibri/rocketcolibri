package ch.hsr.rocketcolibri.view.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RCConstants;
import ch.hsr.rocketcolibri.ui_data.input.UiInputSourceChannel;
import ch.hsr.rocketcolibri.util.DrawingTools;
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.custimizable.ICustomizableView;
import ch.hsr.rocketcolibri.view.custimizable.ModusChangeListener;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class RotaryKnobWidget extends ImageView implements ICustomizableView,
		IRCWidget {

	private int tRotaryKnobResource;
	private int tRimImageResource;
	protected RCWidgetConfig tWidgetConfig;
	private float tAngle;					// Has the range 215ï¿½ up to 505ï¿½
	private float tAngleMin = 215f;
	private float tAngleMax = 145f + 360;	// +360 because tAngle is not set to zero at 360ï¿½
	private float theta_old = 0f;
	private float tResolution = tAngleMax - tAngleMin;	// Available range in degrees
	private float tKnobRange;
	private float tKnobRangeResolution;
	private float tKnobRangeIncDecValue;
	private float tKnobValue;
	private static final int tKnobValueMin = 0;
	private static final int tKnobValueMax = 999;
	private int tKnobIncDecValue = 2;

	protected OnTouchListener tCustomizeModusListener;
	private MyOnTouchListener tInternalControlListener = new MyOnTouchListener();

	private UiInputSourceChannel tChannel = new UiInputSourceChannel();
	private boolean tCustomizeModusActive = false;

	private RotaryKnobListener tListener;

	public RotaryKnobWidget(Context context, ViewElementConfig elementConfig) {
		super(context);
		tWidgetConfig = new RCWidgetConfig(elementConfig);
		setLayoutParams(tWidgetConfig.viewElementConfig.getLayoutParams());
		setAlpha(tWidgetConfig.viewElementConfig.getAlpha());
		initProtocolMapping();
		initialize();
	}

	public RotaryKnobWidget(Context context, RCWidgetConfig widgetConfig) {
		super(context);
		tWidgetConfig = widgetConfig;
		setLayoutParams(tWidgetConfig.viewElementConfig.getLayoutParams());
		setAlpha(tWidgetConfig.viewElementConfig.getAlpha());
		updateProtocolMap();
		initialize();
	}

	public interface RotaryKnobListener {
		public void onKnobChanged(int arg);
	}

	public void setKnobListener(RotaryKnobListener listener) {
		tListener = listener;
	}

	private boolean isChannelValid() {
		if (tChannel.getChannelAssignment() > -1) {
			tRotaryKnobResource = R.drawable.rotoron;
			this.setImageResource(tRotaryKnobResource);
			return true;
		} else {
			tRotaryKnobResource = R.drawable.rotoroff;
			this.setImageResource(tRotaryKnobResource);
			return false;
		}
	}

	class MyOnTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			float x = event.getX(0);
			float y = event.getY(0);
			float theta = getTheta(x, y);

			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_POINTER_DOWN:
				theta_old = theta;
				break;

			case MotionEvent.ACTION_MOVE:
				invalidate();
				float delta_theta = theta - theta_old;
				theta_old = theta;
				int direction = (delta_theta > 0) ? 1 : -1;
				tAngle += tKnobIncDecValue * direction;

				if ((tAngle <= 360) && (tAngle >= 180)) {
					if (tAngle < tAngleMin) {
						tAngle = tAngleMin;
					} else {
						tKnobValue += tKnobRangeIncDecValue * direction;
						tChannel.setWidgetPosition((int)tKnobValue);
						Log.d("Testing", "tKnobValue is set: " + (int) tKnobValue);
					}
				} else {
					if (tAngle > tAngleMax) {
						tAngle = tAngleMax;
					} else {
						tKnobValue += tKnobRangeIncDecValue * direction;
						tChannel.setWidgetPosition((int)tKnobValue);
						Log.d("Testing", "tKnobValue is set: " + (int) tKnobValue);
					}
				}

				notifyListener(direction);
				break;
			}
			return true;
		}
	}

	private float getTheta(float x, float y) {
		float sx = x - (getWidth() / 2.0f);
		float sy = y - (getHeight() / 2.0f);

		float length = (float) Math.sqrt(sx * sx + sy * sy);
		float nx = sx / length;
		float ny = sy / length;
		float theta = (float) Math.atan2(ny, nx);

		final float rad2deg = (float) (180.0 / Math.PI);
		float thetaDeg = theta * rad2deg;

		return (thetaDeg < 0) ? thetaDeg + 360.0f : thetaDeg;
	}

	public void initProtocolMapping() {
		// init protocol mapping
		tWidgetConfig.protocolMap = new HashMap<String, String>();
		tWidgetConfig.protocolMap.put(RCConstants.CHANNEL_ASSIGNMENT, "");
		tWidgetConfig.protocolMap.put(RCConstants.INVERTED, "");
		tWidgetConfig.protocolMap.put(RCConstants.MAX_RANGE, "");
		tWidgetConfig.protocolMap.put(RCConstants.MIN_RANGE, "");
		tWidgetConfig.protocolMap.put(RCConstants.DEFAULT_POSITION, "");
		tWidgetConfig.protocolMap.put(RCConstants.TRIMM, "");
	}

	public void initialize() {

		tRotaryKnobResource = R.drawable.rotoroff;
		tRimImageResource = R.drawable.stator;

		this.setBackgroundResource(tRimImageResource);
		this.setImageResource(tRotaryKnobResource);
		calculateKnobValues();

		this.setKnobListener(new RotaryKnobWidget.RotaryKnobListener() {
			@Override
			public void onKnobChanged(int arg) {
//				Log.d("onKnobChanged", "arg= " + arg);
//				if (arg > 0)
//					; // rotate right
//				else
//					; // rotate left
			}
		});

		if (isChannelValid()) {
			setOnTouchListener(tInternalControlListener);
		}
	}

	private void calculateKnobValues() {
		tAngle = tAngleMin;
		tChannel.setWidgetRange(tKnobValueMin, tKnobValueMax);	
		tKnobValue = tChannel.setWidgetToDefault();

		tKnobRangeResolution = (int) tResolution / tKnobIncDecValue;
		tKnobRange = tKnobValueMax - tKnobValueMin;
		tKnobRangeIncDecValue = tKnobRange / tKnobRangeResolution;
	}

	private void notifyListener(int arg) {
		if (null != tListener) {
			tListener.onKnobChanged(arg);
		}
	}

	private ModusChangeListener tModusChangeListener = new ModusChangeListener() {
		@Override
		public void customizeModeDeactivated() {
			if (isChannelValid()) {
				setOnTouchListener(tInternalControlListener);
			}
		}

		@Override
		public void customizeModeActivated() {
			setOnTouchListener(tCustomizeModusListener);
		}
	};

	protected void onDraw(Canvas canvas) {
		canvas.rotate(tAngle, getWidth() / 2, getHeight() / 2);
		super.onDraw(canvas);

		if (tCustomizeModusActive)
			DrawingTools.drawCustomizableForground(this, canvas);
	}

	@Override
	public void updateProtocolMap() {
		try {
			tChannel.setChannelAssignment(getProtocolMapInt(RCConstants.CHANNEL_ASSIGNMENT));
			tChannel.setChannelInverted(getProtocolMapBoolean(RCConstants.INVERTED));
			tChannel.setChannelMaxRange(getProtocolMapInt(RCConstants.MAX_RANGE));
			tChannel.setChannelMinRange(getProtocolMapInt(RCConstants.MIN_RANGE));
			tChannel.setChannelDefaultPosition(getProtocolMapInt(RCConstants.DEFAULT_POSITION));
			tChannel.setChannelTrimm(getProtocolMapInt(RCConstants.TRIMM));

			// Protocol values has changed recalculate the values
			calculateKnobValues();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ViewElementConfig getDefaultViewElementConfig() {
		return DefaultViewElementConfigRepo.getDefaultConfig(RotaryKnobWidget.class);
	}

	@Override
	public void create(RCWidgetConfig rcWidgetConfig) {
		tWidgetConfig = rcWidgetConfig;
		setLayoutParams(tWidgetConfig.viewElementConfig.getLayoutParams());
		setAlpha(tWidgetConfig.viewElementConfig.getAlpha());
		updateProtocolMap();
		initialize();
	}

	@Override
	public void create(ViewElementConfig vElementConfig) {
		tWidgetConfig = new RCWidgetConfig(vElementConfig);
		setLayoutParams(tWidgetConfig.viewElementConfig.getLayoutParams());
		setAlpha(tWidgetConfig.viewElementConfig.getAlpha());
		initProtocolMapping();
		initialize();
	}

	@Override
	public void setCustomizeModusListener(OnTouchListener customizeModusListener) {
		tCustomizeModusListener = customizeModusListener;
		setOnTouchListener(tCustomizeModusListener);
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
