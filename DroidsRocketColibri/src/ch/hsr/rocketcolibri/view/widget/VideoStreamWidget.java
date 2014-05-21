package ch.hsr.rocketcolibri.view.widget;

import java.io.IOException;

import ch.hsr.rocketcolibri.activity.DesktopActivity;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import ch.hsr.rocketcolibri.view.custimizable.ViewSequence;
import ch.hsr.rocketcolibri.widgetdirectory.UiOutputDataType;
import ch.hsr.rocketcolibri.widgetdirectory.uioutputdata.VideoUrl;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;


public class VideoStreamWidget extends RCWidget  implements SurfaceHolder.Callback, OnPreparedListener {

	private MediaPlayer tMediaPlayer;
    private SurfaceView tVideoSurfaceView;
    private SurfaceHolder tHolder;

    RelativeLayout tRel;
    
	VideoUrl tVideoUrl;

	
	public VideoStreamWidget(Context context, ViewElementConfig elementConfig) 	{
		super(context, elementConfig);
		
  	  	tVideoSurfaceView = (SurfaceView)new SurfaceView(context);
  	  	tVideoSurfaceView.setLayoutParams(elementConfig.getLayoutParams());
  	  	tHolder = tVideoSurfaceView.getHolder();
  	  	tHolder.addCallback(this); 
  	    DesktopActivity activity = (DesktopActivity)context;
  	    // TODO: if next line is active the SurfaceView won't be created!
  	    //activity.setContentView(tVideoSurfaceView, elementConfig.getLayoutParams());
	}

	
	@Override
	protected void onDraw(Canvas canvas) 
	{
		super.onDraw(canvas);
	}
	
	/**
	 * RocketColibriService sends UiSink change notification with this methods
	 * The Object class depends on the return value of getType
	 * @param data
	 */
	public void onNotifyUiOutputSink(Object data) {
		tVideoUrl = (VideoUrl)data;
		//Play_Video();
	}
	
	/**
	 * Override this function with the Ui Output data type the widget wants to receive with the onNotifyUiOutputSink method. 
	 * @return type
	 */
	@Override
	public UiOutputDataType getType() {
		return UiOutputDataType.Video;
	}

	/**
	 * The video view must be place in the background
	 */
	@Override
	public ViewSequence getViewSequence () {
		return ViewSequence.Background; 
	}

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
    	holder.setFixedSize(width, height);
    }
    
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            tMediaPlayer = new MediaPlayer();
            tMediaPlayer.setDisplay(holder);
            // TODO set videoUrl here
            tMediaPlayer.setDataSource("rtsp://v6.cache1.c.youtube.com/CjYLENy73wIaLQkDsLHya4-Z9hMYDSANFEIJbXYtZ29vZ2xlSARSBXdhdGNoYKX4k4uBjbOiUQw=/0/0/0/video.3gp");
            tMediaPlayer.prepare();
            tMediaPlayer.setOnPreparedListener(this);
            tMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
     } catch (IllegalArgumentException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
     } catch (SecurityException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
     } catch (IllegalStateException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
     } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
     }
    	
    }
    
    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
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
		tMediaPlayer.start();
	}
}
