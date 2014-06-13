package ch.hsr.rocketcolibri.view.widget;

import java.util.HashMap;
import java.util.Map;

import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RCConstants;
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
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class RotaryKnobWidget extends View implements ICustomizableView, IRCWidget {

	private int backgroundResource;
	protected RCWidgetConfig tWidgetConfig;
	protected ViewElementConfig tViewElementConfig;
	ImageView ivBack = null;
	private float angle = 0f;
	private float theta_old = 0f;
	private RectF rimRect;
	private RectF faceRect;
	private Rect bounds;
	private Bitmap faceTexture;
	private Paint facePaint;
	private static final float rimSize = 0.02f;
	protected OnTouchListener tCustomizeModusListener;
	private OnChannelChangeListener tControlModusListener;
	private MyOnTouchListener tInternalControlListener = new MyOnTouchListener();
	private Channel tChannelH = new Channel();
	private Map<String, String> protocolMap = new HashMap<String, String>();
	private boolean isInitialized = false;

	private RotaryKnobListener listener;

	public RotaryKnobWidget(Context context, ViewElementConfig elementConfig) {
		super(context);
		tViewElementConfig = elementConfig;
		tWidgetConfig = new RCWidgetConfig(tViewElementConfig);
		setLayoutParams(tViewElementConfig.getLayoutParams());
		setAlpha(tViewElementConfig.getAlpha());
		backgroundResource = R.drawable.rotoron;
		initialize(context);
	}

	public RotaryKnobWidget(Context context, RCWidgetConfig widgetConfig) {
		super(context);
		tViewElementConfig = widgetConfig.viewElementConfig;
		tWidgetConfig = widgetConfig;
		setLayoutParams(tViewElementConfig.getLayoutParams());
		setAlpha(tViewElementConfig.getAlpha());
		backgroundResource = R.drawable.rotoron;
		initialize(context);
	}

	public interface RotaryKnobListener {
		public void onKnobChanged(int arg);
	}

	public void setKnobListener(RotaryKnobListener l) {
		listener = l;
	}

	private boolean isChannelValid() {
		return tChannelH.getDefaultChannelValue() > -1;
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
				angle += 3 * direction;
				notifyListener(direction);
				break;
			}
			return true;
		}
	}

	private void initListener() {
		setModusChangeListener(new ModusChangeListener() {
			public void customizeModeDeactivated() {
				if (isChannelValid())
					setOnTouchListener(tInternalControlListener);
			}

			public void customizeModeActivated() {
				setOnTouchListener(tCustomizeModusListener);
			}
		});
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
		if (!isInitialized) {
			bounds = new Rect();

			rimRect = new RectF(0.0f, 0.0f, 1.0f, 1.0f);

			faceRect = new RectF();
			faceRect.set(rimRect.left + rimSize, rimRect.top + rimSize,
					rimRect.right - rimSize, rimRect.bottom - rimSize);

			faceTexture = BitmapFactory.decodeResource(getContext().getResources(),
					backgroundResource);
			BitmapShader paperShader = new BitmapShader(faceTexture,
					Shader.TileMode.MIRROR, Shader.TileMode.MIRROR);
			Matrix paperMatrix = new Matrix();
			paperMatrix.setScale(1.0f / faceTexture.getWidth(),
					1.0f / faceTexture.getHeight());

			paperShader.setLocalMatrix(paperMatrix);

			facePaint = new Paint();
			facePaint.setFilterBitmap(true);
			facePaint.setStyle(Paint.Style.FILL);
			facePaint.setShader(paperShader);

			initListener();

			// init protocol mapping
			protocolMap.put(RCConstants.CHANNEL_H, "");
			protocolMap.put(RCConstants.INVERTED_H, "");
			protocolMap.put(RCConstants.MAX_RANGE_H, "");
			protocolMap.put(RCConstants.MIN_RANGE_H, "");
			protocolMap.put(RCConstants.TRIMM_H, "");

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

			isInitialized = true;
		}
	}

	private void notifyListener(int arg) {
		if (null != listener)
			listener.onKnobChanged(arg);
	}

	protected void onDraw(Canvas canvas) {
		canvas.getClipBounds(bounds);

		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.translate(bounds.left, bounds.top);

		float scale = (float) getWidth();
		float midX = bounds.width() / 2.0f;
		float midY = bounds.height() / 2.0f;
		canvas.scale(scale, scale);
		canvas.drawOval(faceRect, facePaint);
		canvas.rotate(angle, midX, midY);
		canvas.restore();
		super.onDraw(canvas);
	}

	@Override
	public void updateProtocolMap() {
		try {
			tChannelH.setDefaultChannelValue(getProtocolMapInt(RCConstants.CHANNEL_H));
			tChannelH.setInverted(getProtocolMapBoolean(RCConstants.INVERTED_H));
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
		setOnTouchListener(tInternalControlListener);
	}

	@Override
	public RCWidgetConfig getWidgetConfig() {
		tWidgetConfig.viewElementConfig = getViewElementConfig();
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
	public void notifyServiceReady(Service tService) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setModusChangeListener(ModusChangeListener mcl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ViewElementConfig getViewElementConfig() {
		// TODO Auto-generated method stub
		return null;
	}

}