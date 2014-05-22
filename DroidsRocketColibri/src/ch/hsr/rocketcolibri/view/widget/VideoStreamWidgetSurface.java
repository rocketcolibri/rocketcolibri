package ch.hsr.rocketcolibri.view.widget;

import java.io.IOException;

import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.widgetdirectory.uioutputdata.VideoUrl;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class VideoStreamWidgetSurface extends SurfaceView implements SurfaceHolder.Callback, OnPreparedListener {
	private VideoUrl tVideoUrl;
	private MediaPlayer tMediaPlayer;
	private SurfaceHolder tHolder;

	private RectF videoNotAvailIconRect;
	private Paint videoNotAvailIconPaint;
	private Bitmap videoNotAvailIconBitmap;
	
	public VideoStreamWidgetSurface(Context context) {
		super(context);
  	  	tHolder = getHolder();
  	  	tHolder.addCallback(this);
  	  	
		videoNotAvailIconRect = new RectF(0.0f, 0.0f, 1.0f, 1.0f);
		videoNotAvailIconBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.video_stream_not_available);
		BitmapShader paperShader = new BitmapShader(videoNotAvailIconBitmap, Shader.TileMode.MIRROR, Shader.TileMode.MIRROR);
		Matrix paperMatrix = new Matrix();
		paperMatrix.setScale(1.0f/videoNotAvailIconBitmap.getWidth(), 1.0f/videoNotAvailIconBitmap.getWidth());
		paperShader.setLocalMatrix(paperMatrix);
		videoNotAvailIconPaint = new Paint();
		videoNotAvailIconPaint.setFilterBitmap(false);
		videoNotAvailIconPaint.setStyle(Paint.Style.FILL);
		videoNotAvailIconPaint.setShader(paperShader);
		setWillNotDraw(false);
		postInvalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) 
	{
		if(null == this.tVideoUrl) {
			float scale = (float) getWidth();
			canvas.save(Canvas.MATRIX_SAVE_FLAG);
			canvas.scale(scale, scale);
			canvas.drawRect(videoNotAvailIconRect, videoNotAvailIconPaint);
			canvas.restore();
			super.onDraw(canvas);
		}
		else
			canvas.drawColor(0, Mode.CLEAR);
	}
	
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
    	holder.setFixedSize(width, height);
    }
    
    private void playVideo() {
    	if(null != this.tVideoUrl) {
            try {
            	
                tMediaPlayer = new MediaPlayer();
                tMediaPlayer.setDisplay(tHolder);
                // TODO set videoUrl here
                tMediaPlayer.setDataSource(tVideoUrl.getVideoUrl());
                //tMediaPlayer.setDataSource("rtsp://v6.cache1.c.youtube.com/CjYLENy73wIaLQkDsLHya4-Z9hMYDSANFEIJbXYtZ29vZ2xlSARSBXdhdGNoYKX4k4uBjbOiUQw=/0/0/0/video.3gp");
                tMediaPlayer.prepareAsync();
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
    	postInvalidate();
    }
    
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    	tHolder = holder;
    	playVideo();
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
	
	public VideoUrl getVideoUrl() {
		return tVideoUrl;
	}

	public void setVideoUrl(VideoUrl tVideoUrl) {
		this.tVideoUrl = tVideoUrl;
		playVideo();
	}
}
