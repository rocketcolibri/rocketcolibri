package ch.hsr.rocketcolibri.view.widget;

import java.util.HashMap;
import java.util.Map;

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
import android.graphics.drawable.Drawable;
import android.view.View;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RCConstants;
import ch.hsr.rocketcolibri.RocketColibriService;
import ch.hsr.rocketcolibri.ui_data.input.Channel;
import ch.hsr.rocketcolibri.ui_data.output.UiOutputDataType;
import ch.hsr.rocketcolibri.util.DrawingTools;
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.custimizable.ICustomizableView;
import ch.hsr.rocketcolibri.view.custimizable.ModusChangeListener;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import ch.hsr.rocketcolibri.view.resizable.ResizeConfig;

public class SwitchWidget extends View implements ICustomizableView, IRCWidget {

	protected RCWidgetConfig tWidgetConfig;
	protected OnTouchListener tCustomizeModusListener;
	private RectF switchIconRect;
	private Paint switchIconPaint;
	private Bitmap switchIconBitmap;
	private Channel tChannelH = new Channel();
	private int bitmapResource;
	private int tPosition = 0;
	private OnChannelChangeListener tControlModusListener;
	private boolean switchSetOn = false;
	private boolean tCustomizeModusActive = false;

	public SwitchWidget(Context context, ViewElementConfig elementConfig) {
		super(context);
		tWidgetConfig = new RCWidgetConfig(elementConfig);
		setLayoutParams(elementConfig.getLayoutParams());
		setAlpha(elementConfig.getAlpha());
		bitmapResource = R.drawable.switch_off;
		init(context, null);
	}

	public SwitchWidget(Context context, RCWidgetConfig widgetConfig) {
		super(context);
		tWidgetConfig = widgetConfig;
		setLayoutParams(tWidgetConfig.viewElementConfig.getLayoutParams());
		setAlpha(tWidgetConfig.viewElementConfig.getAlpha());
		bitmapResource = R.drawable.switch_off;
		init(context, null);
	}

	private boolean isChannelValid() {
		return tChannelH.getDefaultChannelValue() > -1;
	}

	OnClickListener onclicklistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (switchSetOn) {
				switchSetOn = false;
				bitmapResource = R.drawable.switch_off;
				tPosition = 0;
			} else {
				switchSetOn = true;
				bitmapResource = R.drawable.switch_on;
				tPosition = 1000;
			}
			setSwitchState();

			if (isChannelValid()) {
				tControlModusListener.onChannelChange(
						tChannelH.getDefaultChannelValue(), tPosition);
			}
		}
	};

	private ModusChangeListener tModusChangeListener = new ModusChangeListener() {
		@Override
		public void customizeModeDeactivated() {
		}

		@Override
		public void customizeModeActivated() {
		}
	};

	private void init(Context context, Object object) {
		switchIconRect = new RectF(0.0f, 0.0f, 1.0f, 1.0f);
		this.createWidget();

		this.setOnClickListener(onclicklistener);

		// init protocol mapping
		tWidgetConfig.protocolMap = new HashMap<String, String>();
		tWidgetConfig.protocolMap.put(RCConstants.CHANNEL_H, "");
		tWidgetConfig.protocolMap.put(RCConstants.INVERTED_H, "");
		tWidgetConfig.protocolMap.put(RCConstants.MAX_RANGE_H, "");
		tWidgetConfig.protocolMap.put(RCConstants.MIN_RANGE_H, "");
		tWidgetConfig.protocolMap.put(RCConstants.TRIMM_H, "");
	}

	@Override
	protected void onDraw(Canvas canvas) {
		float scale = (float) getWidth();
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.scale(scale, scale);
		canvas.drawRect(switchIconRect, switchIconPaint);
		canvas.restore();

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

	private void setSwitchState() {
		this.createWidget();
		postInvalidate();
	}

	private void createWidget() {
		switchIconBitmap = BitmapFactory.decodeResource(getContext()
				.getResources(), bitmapResource);
		BitmapShader paperShader = new BitmapShader(switchIconBitmap,
				Shader.TileMode.MIRROR, Shader.TileMode.MIRROR);
		Matrix paperMatrix = new Matrix();
		paperMatrix.setScale(1.0f / switchIconBitmap.getWidth(),
				1.0f / switchIconBitmap.getHeight());
		paperShader.setLocalMatrix(paperMatrix);
		switchIconPaint = new Paint();
		switchIconPaint.setFilterBitmap(false);
		switchIconPaint.setStyle(Paint.Style.FILL);
		switchIconPaint.setShader(paperShader);
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
		init(getContext(), null);
	}

	@Override
	public void create(ViewElementConfig vElementConfig) {
		tWidgetConfig = new RCWidgetConfig(vElementConfig);
		init(getContext(), null);
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
	public void notifyServiceReady(Service rcService) {
		try {
			((RocketColibriService) rcService).tProtocol
					.registerUiOutputSinkChangeObserver((IRCWidget) this);
		} catch (Exception e) {
		}
	}

	@Override
	public ViewElementConfig getViewElementConfig() {
		tWidgetConfig.viewElementConfig.setLayoutParams((LayoutParams) getLayoutParams());
		tWidgetConfig.viewElementConfig.setAlpha(getAlpha());
		return tWidgetConfig.viewElementConfig;
	}
}