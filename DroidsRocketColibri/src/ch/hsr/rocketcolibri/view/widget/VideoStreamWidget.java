package ch.hsr.rocketcolibri.view.widget;

import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.ui_data.output.UiOutputDataType;
import ch.hsr.rocketcolibri.ui_data.output.VideoUrl;
import ch.hsr.rocketcolibri.view.AbsoluteLayout;
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.custimizable.ModusChangeListener;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import ch.hsr.rocketcolibri.view.resizable.ResizeConfig;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

public class VideoStreamWidget extends RCWidget {
	
	static final String TAG = "VideoStreamWidget";
    private VideoStreamWidgetSurface tVideoSurfaceView;
    private Object tVideoSurfaceViewTag = new Object();
    private Bitmap tVideoBitmap;
    private Paint tRectPaint;
    private Rect tRectRect;
    private RectF tRectRectF;
    RelativeLayout tRel;
    private AbsoluteLayout tParent;
	
	public VideoStreamWidget(Context context, ViewElementConfig elementConfig) 	{
		super(context, elementConfig);
		init(context, null);
	}
	
	public VideoStreamWidget(Context context, RCWidgetConfig widgetConfig) 	{
		super(context, widgetConfig.viewElementConfig);
  	  	init(context, null);
	}
	
	private void init(Context context, AttributeSet attrs) {
		tRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		tRectPaint.setColor(Color.WHITE);
		tRectPaint.setAlpha(100);
		tRectRect = new Rect();
		tRectRectF  = new RectF();
		tVideoBitmap =  BitmapFactory.decodeResource(getContext().getResources(), R.drawable.video_camera);

	  	tVideoSurfaceView = new VideoStreamWidgetSurface(context);
	  	tVideoSurfaceView.setLayoutParams(new android.view.ViewGroup.LayoutParams(tWidgetConfig.viewElementConfig.getLayoutParams()));
	  	tVideoSurfaceView.setTag(tVideoSurfaceViewTag);
	  	
		setModusChangeListener(new ModusChangeListener() {
			@Override
			public void customizeModeDeactivated() {
				if(null != tParent)	{				
					if(null == tParent.findViewWithTag(tVideoSurfaceViewTag))
						tParent.addView(tVideoSurfaceView, 0, tWidgetConfig.viewElementConfig.getLayoutParams());
				}
			}
			
			@Override
			public void customizeModeActivated() {
				if(null != tParent) {
					View v = tParent.findViewWithTag(tVideoSurfaceViewTag);
					if(null != v)
						tParent.removeView(v);
				}
			}
		});
	}
	
	@Override
	protected void onAttachedToWindow() {
		tParent = (AbsoluteLayout) this.getParent();
		if(null == tParent.findViewWithTag(tVideoSurfaceViewTag))
			tParent.addView(tVideoSurfaceView, 0, tWidgetConfig.viewElementConfig.getLayoutParams());
		super.onAttachedToWindow();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if((null == tParent) || (null == tParent.findViewWithTag(tVideoSurfaceViewTag)))	{
			// draw background rectangle
			tRectRect.set(0, 0, canvas.getWidth(),canvas.getHeight());
			tRectRectF.set(tRectRect);
			canvas.drawRoundRect( tRectRectF, 10f,10f, tRectPaint);
			// draw bitmap
			canvas.drawBitmap(tVideoBitmap, canvas.getWidth()/2 - tVideoBitmap.getWidth()/2 , canvas.getHeight()/2 - tVideoBitmap.getHeight()/2 , null);
		}
		super.onDraw(canvas);
	}
	
	/**
	 * RocketColibriService sends UiSink change notification with this methods
	 * The Object class depends on the return value of getType
	 * The media player is capable to play RTSP video streams.
	 * @param data (VideoUrl)
	 */
	public void onNotifyUiOutputSink(Object data) {
		if(((VideoUrl)data).getVideoUrl().startsWith("rtsp://")) {
			Log.d(TAG, "start stream " + ((VideoUrl)data).getVideoUrl());			
			tVideoSurfaceView.setVideoUrl(((VideoUrl)data).getVideoUrl());
		}
	}
	
	/**
	 * Override this function with the Ui Output data type the widget wants to receive with the onNotifyUiOutputSink method. 
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
	    rc.maxHeight=600;
	    rc.minHeight=100;
	    rc.maxWidth=800;
	    rc.minWidth=200;
	    LayoutParams lp = new LayoutParams(400, 300 , 100, 100);
	    ViewElementConfig elementConfig = new ViewElementConfig(VideoStreamWidget.class.getName(), lp, rc);
	    return elementConfig;
	}
}
