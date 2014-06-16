package ch.hsr.rocketcolibri.view.widget;

import java.util.HashMap;
import java.util.Map;

import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RocketColibriService;
import ch.hsr.rocketcolibri.ui_data.output.UiOutputDataType;
import ch.hsr.rocketcolibri.ui_data.output.VideoUrl;
import ch.hsr.rocketcolibri.view.AbsoluteLayout;
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.custimizable.ICustomizableView;
import ch.hsr.rocketcolibri.view.custimizable.ModusChangeListener;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import ch.hsr.rocketcolibri.view.resizable.ResizeConfig;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;

public class VideoStreamWidget extends SurfaceView implements
		ICustomizableView, IRCWidget {

	static final String TAG = "VideoStreamWidget";
	private VideoStreamWidgetSurface tVideoSurfaceView;
	private Object tVideoSurfaceViewTag = new Object();
	private Bitmap tVideoBitmap;
	private Paint tRectPaint;
	private Rect tRectRect;
	private RectF tRectRectF;
	RelativeLayout tRel;
	private AbsoluteLayout tParent;

	protected RCWidgetConfig tWidgetConfig;
	protected OnTouchListener tCustomizeModusListener;
	protected OnChannelChangeListener tControlModusListener;
	private boolean tCustomizeModusActive = false;

	public VideoStreamWidget(Context context, ViewElementConfig elementConfig) {
		super(context);
		tWidgetConfig = new RCWidgetConfig(elementConfig);
		setLayoutParams(elementConfig.getLayoutParams());
		setAlpha(elementConfig.getAlpha());
		tWidgetConfig = new RCWidgetConfig(elementConfig);
		init(context, null);
	}

	public VideoStreamWidget(Context context, RCWidgetConfig widgetConfig) {
		super(context);
		tWidgetConfig = widgetConfig;
		setLayoutParams(tWidgetConfig.viewElementConfig.getLayoutParams());
		setAlpha(tWidgetConfig.viewElementConfig.getAlpha());
		init(context, null);
	}

	private void init(Context context, AttributeSet attrs) {
		tRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		tRectPaint.setColor(Color.WHITE);
		tRectPaint.setAlpha(100);
		tRectRect = new Rect();
		tRectRectF = new RectF();
		tVideoBitmap = BitmapFactory.decodeResource(
				getContext().getResources(), R.drawable.video_camera);

		tVideoSurfaceView = new VideoStreamWidgetSurface(context);
		if (null != tWidgetConfig.viewElementConfig)
			tVideoSurfaceView
					.setLayoutParams(new android.view.ViewGroup.LayoutParams(
							tWidgetConfig.viewElementConfig.getLayoutParams()));
		tVideoSurfaceView.setTag(tVideoSurfaceViewTag);

		setModusChangeListener(new ModusChangeListener() {
			@Override
			public void customizeModeDeactivated() {
				if (null != tParent) {
					if (null == tParent.findViewWithTag(tVideoSurfaceViewTag))
						tParent.addView(tVideoSurfaceView, 0,
								tWidgetConfig.viewElementConfig
										.getLayoutParams());
				}
			}

			@Override
			public void customizeModeActivated() {
				if (null != tParent) {
					View v = tParent.findViewWithTag(tVideoSurfaceViewTag);
					if (null != v)
						tParent.removeView(v);
				}
			}
		});
	}

	private ModusChangeListener tModusChangeListener = new ModusChangeListener() {
		@Override
		public void customizeModeDeactivated() {
		}

		@Override
		public void customizeModeActivated() {
		}
	};

	@Override
	protected void onAttachedToWindow() {
		tParent = (AbsoluteLayout) this.getParent();
		if (null == tParent.findViewWithTag(tVideoSurfaceViewTag))
			tParent.addView(tVideoSurfaceView, 0,
					tWidgetConfig.viewElementConfig.getLayoutParams());
		super.onAttachedToWindow();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if ((null == tParent)
				|| (null == tParent.findViewWithTag(tVideoSurfaceViewTag))) {
			// draw background rectangle
			tRectRect.set(0, 0, canvas.getWidth(), canvas.getHeight());
			tRectRectF.set(tRectRect);
			canvas.drawRoundRect(tRectRectF, 10f, 10f, tRectPaint);
			// draw bitmap
			canvas.drawBitmap(tVideoBitmap, canvas.getWidth() / 2
					- tVideoBitmap.getWidth() / 2, canvas.getHeight() / 2
					- tVideoBitmap.getHeight() / 2, null);
		}
		if (!tCustomizeModusActive)
			return;
		final Drawable foreground = getResources().getDrawable(
				R.drawable.dragforeground);
		if (foreground != null) {
			foreground.setBounds(0, 0, getRight() - getLeft(), getBottom()
					- getTop());

			final int scrollX = getScrollX();
			final int scrollY = getScrollY();

			if ((scrollX | scrollY) == 0) {
				foreground.draw(canvas);
			} else {
				canvas.translate(scrollX, scrollY);
				foreground.draw(canvas);
				canvas.translate(-scrollX, -scrollY);
			}
		}
	}

	/**
	 * RocketColibriService sends UiSink change notification with this methods
	 * The Object class depends on the return value of getType The media player
	 * is capable to play RTSP video streams.
	 * 
	 * @param data
	 *            (VideoUrl)
	 */
	public void onNotifyUiOutputSink(Object data) {
		if (((VideoUrl) data).getVideoUrl().startsWith("rtsp://")) {
			Log.d(TAG, "start stream " + ((VideoUrl) data).getVideoUrl());
			tVideoSurfaceView.setVideoUrl(((VideoUrl) data).getVideoUrl());
		}
	}

	/**
	 * Override this function with the Ui Output data type the widget wants to
	 * receive with the onNotifyUiOutputSink method.
	 * 
	 * @return type
	 */
	@Override
	public UiOutputDataType getType() {
		return UiOutputDataType.Video;
	}

	@Override
	public void updateProtocolMap() {
		// TODO Auto-generated method stub
	}

	public static ViewElementConfig getDefaultViewElementConfig() {
		ResizeConfig rc = new ResizeConfig();
		rc.maxHeight = 600;
		rc.minHeight = 100;
		rc.maxWidth = 800;
		rc.minWidth = 200;
		LayoutParams lp = new LayoutParams(400, 300, 100, 100);
		ViewElementConfig elementConfig = new ViewElementConfig(
				VideoStreamWidget.class.getName(), lp, rc);
		return elementConfig;
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
	public void setControlModusListener(OnChannelChangeListener channelListener) {
		tControlModusListener = channelListener;
	}

	@Override
	public void setCustomizeModusListener(OnTouchListener customizeModusListener) {
		setOnTouchListener(customizeModusListener);
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
	public void notifyServiceReady(Service rcService) {
		try {
			((RocketColibriService) rcService).tProtocol
					.registerUiOutputSinkChangeObserver((IRCWidget) this);
		} catch (Exception e) {
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
		tWidgetConfig.viewElementConfig.setLayoutParams((LayoutParams) getLayoutParams());
		tWidgetConfig.viewElementConfig.setAlpha(getAlpha());
		return tWidgetConfig.viewElementConfig;
	}
}