package ch.hsr.rocketcolibri.view.widget;

import java.io.IOException;
import java.util.Map;

import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RocketColibriService;
import ch.hsr.rocketcolibri.ui_data.output.UiOutputDataType;
import ch.hsr.rocketcolibri.ui_data.output.VideoUrl;
import ch.hsr.rocketcolibri.util.DrawingTools;
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
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Shader;
import android.graphics.PorterDuff.Mode;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

/**
 * The widgets dispays the video stream coming from the servo controller.
 * 
 * The video display shows an camera icon if the RocketColibri application is in the customizable mode.
 * 
 * If the video stream is not availabel the camera icon is displayed with a red cross
 * 
 *  During the buffering phase of the video, a dotted progress bar is dispöayed.
 *  
 * @author lorenz
 *
 */
public class VideoStreamWidget extends SurfaceView implements
		ICustomizableView, IRCWidget, SurfaceHolder.Callback, OnPreparedListener, OnErrorListener, OnBufferingUpdateListener{

	static final String TAG = "VideoStreamWidget";
	private Bitmap tVideoBitmap;
	private Paint tVideoBitmapPaint;

	private Paint tLinePaintVideoUnavailable;
	
	// progress dots
	private float tDotRadius;
	private int tDotIndex = 0;
	private int tDotMargin = 4;
	private int tDotCount = 3;
	private Paint tDotFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint tDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Handler tDotProressHandler = new Handler();

	protected RCWidgetConfig tWidgetConfig;
	protected OnTouchListener tCustomizeModusListener;
	protected OnChannelChangeListener tControlModusListener;
	
	
	private String tVideoUrl = new String("");
	private MediaPlayer tMediaPlayer;
	private SurfaceHolder tHolder;

	private boolean tCustomizeModusActive = false;
	private boolean tIsPrepared = false; 	
	

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
		//tRectRectF = new RectF(0.0f, 0.0f, 1.0f, 1.0f);
		tVideoBitmap =
				DrawingTools.getRoundedCornerBitmap(
				BitmapFactory.decodeResource(getContext().getResources(), R.drawable.video_camera),
				DrawingTools.radiusEdge);
		BitmapShader paperShader1 = new BitmapShader(tVideoBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
		Matrix paperMatrix1 = new Matrix();
		paperMatrix1.setScale(1.0f/tVideoBitmap.getWidth(), 1.0f/tVideoBitmap.getWidth());
		paperShader1.setLocalMatrix(paperMatrix1);
		tVideoBitmapPaint = new Paint();
		tVideoBitmapPaint.setStyle(Paint.Style.FILL);
		tVideoBitmapPaint.setFilterBitmap(false);
		tVideoBitmapPaint.setShader(paperShader1);

		
		tLinePaintVideoUnavailable  = new Paint();
		tLinePaintVideoUnavailable.setColor(Color.RED);
		tLinePaintVideoUnavailable.setStrokeWidth(tVideoBitmap.getWidth()/20);
		tLinePaintVideoUnavailable.setAlpha(100);

		// progress dots
		tDotRadius = 10;
		// dot fill color
		tDotFillPaint.setStyle(Style.FILL);
		tDotFillPaint.setColor(Color.LTGRAY);
		// dot background color
		tDotPaint.setStyle(Style.FILL);
		tDotPaint.setColor(0x33000000);
		tDotCount = 3;
		
		
		setModusChangeListener(new ModusChangeListener() {
			@Override
			public void customizeModeDeactivated() {

			}

			@Override
			public void customizeModeActivated() {

			}
		});
		
		tHolder = getHolder();
  	  	tHolder.addCallback(this);

		setWillNotDraw(false);
		postInvalidate();
	}

	private ModusChangeListener tModusChangeListener = new ModusChangeListener() {
		@Override
		public void customizeModeDeactivated() {
		}

		@Override
		public void customizeModeActivated() {
		}
	};

	private void startDotProgress() {
		tIsPrepared = false;
		tDotIndex = -1;
		//tDotProressHandler.removeCallbacks(tRunnable);
		tDotProressHandler.post(tRunnable);
	}

	private void stopDotProgress() {
		tIsPrepared = true;
		tDotProressHandler.removeCallbacks(tRunnable);
	}
	
	private int step = 1;
	private Runnable tRunnable = new Runnable() {

		@Override
		public void run() {
			tDotIndex += step;
			if (tDotIndex < 0) {
				tDotIndex = 1;
				step = 1;
			} else if (tDotIndex > (tDotCount - 1)) {
				if ((tDotCount - 2) >= 0) {
					tDotIndex = tDotCount - 2;
					step = -1;
				} else{
					tDotIndex = 0;
					step = 1;
				}

			}

			postInvalidate();
			tDotProressHandler.postDelayed(tRunnable, 300);
		}

	};

	
	/**
	 * draws the progress dots during buffering the video stream
	 * @param canvas
	 */
	private void onDrawDotProgress(Canvas canvas){
		canvas.drawColor(0, Mode.CLEAR);
		
		if(tIsPrepared)	{
			// display video stream
			canvas.drawColor(0, Mode.CLEAR);			
		}else{
			// diplay dotted progress bar
			float dX = (canvas.getWidth() - tDotCount * tDotRadius * 2 - (tDotCount - 1) * tDotMargin) / 2.0f;
			float dY = canvas.getHeight() / 2;
			for (int i = 0; i < tDotCount; i++) {
				if (i == tDotIndex)
					canvas.drawCircle(dX, dY, tDotRadius, tDotFillPaint);
				else
					canvas.drawCircle(dX, dY, tDotRadius, tDotPaint);
				dX += (2 * tDotRadius + tDotMargin);
			}
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		// draw background rectangle
		if (tCustomizeModusActive){
			canvas.drawBitmap(DrawingTools.resizeBitmap(tVideoBitmap, canvas.getWidth(), canvas.getHeight()),0 ,0,tVideoBitmapPaint);	
			DrawingTools.drawCustomizableForground(this, canvas);
		}else{
			// display camera icon
			if(tVideoUrl.length() > 2){
				onDrawDotProgress(canvas);
			}else{
				int borderX = canvas.getWidth()/5;
				int borderY = canvas.getHeight()/5;
				canvas.drawBitmap(DrawingTools.resizeBitmap(tVideoBitmap, canvas.getWidth(), canvas.getHeight()),0 ,0,tVideoBitmapPaint);
				canvas.drawLine(borderX, borderY, canvas.getWidth()-borderX, canvas.getHeight()-borderY, tLinePaintVideoUnavailable);
	            canvas.drawLine(canvas.getWidth()-borderX, borderY, borderX, canvas.getHeight()-borderY, tLinePaintVideoUnavailable);
			}
		}
	}

	private void moveToBack(View currentView) 
	{
	    ViewGroup vg = ((ViewGroup) currentView.getParent());
	    int index = vg.indexOfChild(currentView);
	    for(int i = 0; i<index; i++)
	    {
	    vg.bringChildToFront(vg.getChildAt(0));
	    }
	}
	
	@Override
	protected void onAttachedToWindow()
	{
		moveToBack(this);
		super.onAttachedToWindow();
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
		if (((VideoUrl) data).getVideoUrl().startsWith("rtsp://")) 
			Log.d(TAG, "start stream " + ((VideoUrl) data).getVideoUrl());
		else
			Log.d(TAG, "stop stream " );
		setVideoUrl(((VideoUrl) data).getVideoUrl());
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

	}

	public static ViewElementConfig getDefaultViewElementConfig() {
		ResizeConfig rc = new ResizeConfig();
		rc.maxHeight = 1080;
		rc.minHeight = 100;
		rc.maxWidth = 1920;
		rc.minWidth = 180;
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
	
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){
		Log.d(TAG, "surfaceChanged");
    	holder.setFixedSize(width, height);
    }
    
    private void playVideo() {
    	if(tVideoUrl.length() > 2) {
            try {
            	if(null == tMediaPlayer)
            		tMediaPlayer = new MediaPlayer();
                tMediaPlayer.setDisplay(tHolder);
                tMediaPlayer.setDataSource(tVideoUrl);
                tMediaPlayer.prepareAsync();
                tMediaPlayer.setOnPreparedListener(this);
                tMediaPlayer.setOnErrorListener(this);
				startDotProgress();
         } catch (IllegalArgumentException e) {
        	 e.printStackTrace();
         } catch (SecurityException e) {
        	 e.printStackTrace();
         } catch (IllegalStateException e) {
        	 e.printStackTrace();
         } catch (IOException e) {
        	 e.printStackTrace();
         }     		
    	}  		
    	postInvalidate();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, "surfaceDestroyed");
    	tHolder = holder;
    }
    
    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
		Log.d(TAG, "surfaceDestroyed");
		releaseMediaPlayer();
    }
    
    private void releaseMediaPlayer()
    {
          if (tMediaPlayer != null)
          {
                tMediaPlayer.release();
                tMediaPlayer = null;             
          }
    }

	@Override
	public void onPrepared(MediaPlayer mp) {
		Log.d(TAG, "onPrepared");
		stopDotProgress();
		tIsPrepared = true;
		tMediaPlayer.start();
		postInvalidate();
	}
	
	public void restart(){
        try {
	        for (int u=1; u<=5; u++) {
		        Thread.sleep(5000);
		        tMediaPlayer.stop();
		        tMediaPlayer.release();
	        };

        }
        catch (Exception e)
        {
        }
        tMediaPlayer=null;
        playVideo();
    }
	
	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		restart();
		Log.d(TAG, "Video Player Error:" + what + "Extra:" +extra);
		return true;
	}
	
	public String getVideoUrl() {
		return tVideoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		tVideoUrl = videoUrl;
		if(tVideoUrl.length() > 2)
			playVideo();
		else
			releaseMediaPlayer();
		postInvalidate();
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		Log.d(TAG, "onBufferingUpdate:" + percent + "%");
	}
}
