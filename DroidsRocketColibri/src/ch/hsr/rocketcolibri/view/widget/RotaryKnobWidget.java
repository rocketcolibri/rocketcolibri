package ch.hsr.rocketcolibri.view.widget;

import java.util.HashMap;
import java.util.Map;

import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RCConstants;
import ch.hsr.rocketcolibri.RocketColibriService;
import ch.hsr.rocketcolibri.ui_data.input.Channel;
import ch.hsr.rocketcolibri.ui_data.output.UiOutputDataType;
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.custimizable.ICustomizableView;
import ch.hsr.rocketcolibri.view.custimizable.ModusChangeListener;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import ch.hsr.rocketcolibri.view.resizable.ResizeConfig;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class RotaryKnobWidget extends ImageView implements ICustomizableView,
		IRCWidget {

	private int tRotaryKnobResource;
	private int tBackgroundResource;
	protected RCWidgetConfig tWidgetConfig;
	ImageView tIvBack = null;
	private float tAngle = 0f;
	private float theta_old = 0f;
	private static final float tRimSize = 0.02f;

	private RectF tRotaryKnobRimRect;
	private Paint tRimPaint;
	private RectF tFaceRect;
	private Bitmap tFaceTexture;

	protected OnTouchListener tCustomizeModusListener;
	private OnChannelChangeListener tControlModusListener;
	private MyOnTouchListener tInternalControlListener = new MyOnTouchListener();

	private Channel tChannelH = new Channel();
	private boolean tCustomizeModusActive = false;

	private RotaryKnobListener tListener;

	public RotaryKnobWidget(Context context, ViewElementConfig elementConfig) {
		super(context);
		tWidgetConfig = new RCWidgetConfig(elementConfig);
		setLayoutParams(tWidgetConfig.viewElementConfig.getLayoutParams());
		setAlpha(tWidgetConfig.viewElementConfig.getAlpha());
		tRotaryKnobResource = R.drawable.rotoroff;
		initialize(context);
	}

	public RotaryKnobWidget(Context context, RCWidgetConfig widgetConfig) {
		super(context);
		tWidgetConfig = widgetConfig;
		setLayoutParams(tWidgetConfig.viewElementConfig.getLayoutParams());
		setAlpha(tWidgetConfig.viewElementConfig.getAlpha());
		tRotaryKnobResource = R.drawable.rotoroff;
		initialize(context);
	}

	public interface RotaryKnobListener {
		public void onKnobChanged(int arg);
	}

	public void setKnobListener(RotaryKnobListener listener) {
		tListener = listener;
	}

	private boolean isChannelValid() {
		if (tChannelH.getDefaultChannelValue() > -1) {
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
				tAngle += 3 * direction;
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

	public void initialize(Context context) {
		this.setImageResource(tRotaryKnobResource);

		// init protocol mapping
		tWidgetConfig.protocolMap = new HashMap<String, String>();
		tWidgetConfig.protocolMap.put(RCConstants.CHANNEL_H, "");
		tWidgetConfig.protocolMap.put(RCConstants.INVERTED_H, "");
		tWidgetConfig.protocolMap.put(RCConstants.MAX_RANGE_H, "");
		tWidgetConfig.protocolMap.put(RCConstants.MIN_RANGE_H, "");
		tWidgetConfig.protocolMap.put(RCConstants.TRIMM_H, "");

		this.setKnobListener(new RotaryKnobWidget.RotaryKnobListener() {
			@Override
			public void onKnobChanged(int arg) {

				Log.d("onKnobChanged", "arg= " + arg);
				if (arg > 0)
					; // rotate right
				else
					; // rotate left
			}
		});
	}

	private void notifyListener(int arg) {
		if (null != tListener)
			tListener.onKnobChanged(arg);
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

	public static ViewElementConfig getDefaultViewElementConfig() {
		ResizeConfig rc = new ResizeConfig();
		rc.keepRatio = true;
		rc.maxHeight = 300;
		rc.minHeight = 34;
		rc.maxWidth = 800;
		rc.minWidth = 137;

		LayoutParams lp = new LayoutParams(250, 250, 0, 0);

		ViewElementConfig elementConfig = new ViewElementConfig(
				RotaryKnobWidget.class.getName(), lp, rc);
		elementConfig.setAlpha(1);
		return elementConfig;
	}

	@Override
	public void create(RCWidgetConfig rcWidgetConfig) {
		tWidgetConfig = rcWidgetConfig;
		initialize(getContext());
	}

	@Override
	public void create(ViewElementConfig vElementConfig) {
		tWidgetConfig = new RCWidgetConfig(vElementConfig);
		initialize(getContext());
	}

	@Override
	public void setCustomizeModusListener(OnTouchListener customizeModusListener) {
		tCustomizeModusListener = customizeModusListener;
		setOnTouchListener(tCustomizeModusListener);
	}

	@Override
	public void setControlModusListener(OnChannelChangeListener channelListener) {
		tControlModusListener = channelListener;
		if (isChannelValid()) {
			setOnTouchListener(tInternalControlListener);
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

	@Override
	public void notifyServiceReady(Service rcService) {
		try {
			((RocketColibriService) rcService).tProtocol
					.registerUiOutputSinkChangeObserver((IRCWidget) this);
		} catch (Exception e) {
		}
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
}