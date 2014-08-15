package ch.hsr.rocketcolibri.view.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class RotaryKnobWidget extends ImageView implements ICustomizableView,
		IRCWidget {
	private boolean tDebug;
	private Paint dbgLine;
	
	private int tRotaryKnobResource;
	private int tRimImageResource;
	protected RCWidgetConfig tWidgetConfig;
	private float tAngle;					// Has the range 215° up to 505°
	private float theta_old = 0f;
	
	private final static float ANGLE_MIN = 215f;
	private final static float ANGLE_MAX = 145f + 360;	// +360 because tAngle is not set to zero at 360
	private final static  int KNOB_INC_DEC_VALUE = 2;

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
				tAngle += KNOB_INC_DEC_VALUE * direction;

				if ((tAngle <= 360) && (tAngle >= 180)) {
					if (tAngle < ANGLE_MIN) {
						tAngle = ANGLE_MIN;
					} else {
						tChannel.setWidgetPosition((int)tAngle);
					}
				} else {
					if (tAngle > ANGLE_MAX) {
						tAngle = ANGLE_MAX;
					} else {
						tChannel.setWidgetPosition((int)tAngle);
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
		if (UiInputSourceChannel.CHANNEL_UNASSIGNED == tChannel.getChannelAssignment() )
			tWidgetConfig.protocolMap.put(RCConstants.CHANNEL_ASSIGNMENT, "");
		else
			tWidgetConfig.protocolMap.put(RCConstants.CHANNEL_ASSIGNMENT, Integer.valueOf(tChannel.getChannelAssignment()).toString());
		tWidgetConfig.protocolMap.put(RCConstants.INVERTED, Boolean.valueOf(tChannel.getChannelInverted()).toString());
		tWidgetConfig.protocolMap.put(RCConstants.MAX_RANGE, Integer.valueOf(tChannel.getChannelMaxRange()).toString());
		tWidgetConfig.protocolMap.put(RCConstants.MIN_RANGE, Integer.valueOf(tChannel.getChannelMinRange()).toString());
		tWidgetConfig.protocolMap.put(RCConstants.DEFAULT_POSITION, Integer.valueOf(tChannel.getChannelDefaultPosition()).toString());
		tWidgetConfig.protocolMap.put(RCConstants.TRIMM, Integer.valueOf(tChannel.getChannelTrimm()).toString());
		tWidgetConfig.protocolMap.put(RCConstants.DEBUG, Boolean.valueOf(false).toString());
	}

	public void initialize() {
		tChannel.setWidgetRange((int)ANGLE_MIN, (int)ANGLE_MAX);
		tRimImageResource = R.drawable.stator;
		dbgLine = new Paint(Paint.ANTI_ALIAS_FLAG);
		dbgLine.setStrokeWidth(1);
		dbgLine.setStyle(Paint.Style.FILL_AND_STROKE);
		dbgLine.setTextSize(getPixels(15));
		this.setBackgroundResource(tRimImageResource);
		setRotaryKnobResource();
		calculateKnobValues();

		this.setKnobListener(new RotaryKnobWidget.RotaryKnobListener() {
			@Override
			public void onKnobChanged(int arg) {	}
		});

		if (UiInputSourceChannel.CHANNEL_UNASSIGNED != tChannel.getChannelAssignment())
			setOnTouchListener(tInternalControlListener);
	}

	private void setRotaryKnobResource() {
		if (UiInputSourceChannel.CHANNEL_UNASSIGNED == tChannel.getChannelAssignment() )
			tRotaryKnobResource = R.drawable.rotoroff;
		else
			tRotaryKnobResource = R.drawable.rotoron;
		this.setImageResource(tRotaryKnobResource);
	}
	
	private int getPixels(float size) {
	    DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
	    return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, size, metrics);
	}
	private void calculateKnobValues() {
		tAngle = tChannel.setWidgetToDefault();
	}

	private void notifyListener(int arg) {
		if (null != tListener) {
			tListener.onKnobChanged(arg);
		}
	}

	private ModusChangeListener tModusChangeListener = new ModusChangeListener() {
		@Override
		public void customizeModeDeactivated() {
			setRotaryKnobResource();

			if (UiInputSourceChannel.CHANNEL_UNASSIGNED != tChannel.getChannelAssignment())
				setOnTouchListener(tInternalControlListener);
		}

		@Override
		public void customizeModeActivated() {
			setOnTouchListener(tCustomizeModusListener);
		}
	};

	protected void onDraw(Canvas canvas) {

		canvas.rotate(tAngle, getWidth() / 2, getHeight() / 2);
		super.onDraw(canvas);
		canvas.rotate(-tAngle, getWidth() / 2, getHeight() / 2);
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
			tChannel.setChannelInverted(getProtocolMapBoolean(RCConstants.INVERTED));
			tChannel.setChannelMaxRange(getProtocolMapInt(RCConstants.MAX_RANGE));
			tChannel.setChannelMinRange(getProtocolMapInt(RCConstants.MIN_RANGE));
			tChannel.setChannelDefaultPosition(getProtocolMapInt(RCConstants.DEFAULT_POSITION));
			tChannel.setChannelTrimm(getProtocolMapInt(RCConstants.TRIMM));
			tDebug = getProtocolMapBoolean(RCConstants.DEBUG);
			// Protocol values has changed recalculate the values
			calculateKnobValues();
			postInvalidate();
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
