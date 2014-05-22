package ch.hsr.rocketcolibri.view.widget;

import java.io.IOException;

import ch.hsr.rocketcolibri.R;
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
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class VideoStreamWidgetSurface extends SurfaceView implements SurfaceHolder.Callback, OnPreparedListener, OnErrorListener {
	static final String TAG = "VideoStreamWidgetSurface";
	private String tVideoUrl = new String("");
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
		super.onDraw(canvas);
		
		if(tVideoUrl.length() > 2)
			canvas.drawColor(0, Mode.CLEAR);
		else
		{
			float scale = (float) getWidth();
			canvas.save(Canvas.MATRIX_SAVE_FLAG);
			canvas.scale(scale, scale);
			canvas.drawRect(videoNotAvailIconRect, videoNotAvailIconPaint);
			canvas.restore();
		}
	}
	
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){
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
                tMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
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
}
