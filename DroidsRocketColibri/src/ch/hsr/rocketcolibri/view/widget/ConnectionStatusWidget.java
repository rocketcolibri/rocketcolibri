package ch.hsr.rocketcolibri.view.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolFsm.s;
import ch.hsr.rocketcolibri.view.custimizable.CustomizableView;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;

/**
 * @short widget to display the connection status received from the ServoController
 * 
 *  The status of the connection is indicated with a icon according to the Systemschnittstellen document 
 */
public class ConnectionStatusWidget extends CustomizableView 
{
	private RectF connectionIconRect;
	private Paint connectionIconPaint;
	private Bitmap connectionIconBitmap;
	
	
	public ConnectionStatusWidget(Context context, ViewElementConfig elementConfig) 
	{
		super(context, elementConfig);
		init(context, null);
	}
	
	private void init(Context context, AttributeSet attrs) 
	{
		connectionIconRect = new RectF(0.0f, 0.0f, 1.0f, 1.0f);
		connectionIconBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.connection_status_disconneted);
		BitmapShader paperShader = new BitmapShader(connectionIconBitmap, Shader.TileMode.MIRROR, Shader.TileMode.MIRROR);
		Matrix paperMatrix = new Matrix();
		paperMatrix.setScale(1.0f / connectionIconBitmap.getWidth(), 1.0f / connectionIconBitmap.getHeight());
		paperShader.setLocalMatrix(paperMatrix);
		connectionIconPaint = new Paint();
		connectionIconPaint.setFilterBitmap(false);
		connectionIconPaint.setStyle(Paint.Style.FILL);
		connectionIconPaint.setShader(paperShader);
	}
	
	
	
	@Override
	protected void onDraw(Canvas canvas) 
	{
		float scale = (float) getWidth();
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.scale(scale, scale);
		canvas.drawOval(connectionIconRect, connectionIconPaint);
		canvas.restore();
		super.onDraw(canvas);
	}
	
	/**
	 * 
	 */
	public void setConnectionState(s state)
	{
		switch(state)
		{
		case DISC:
			connectionIconBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.connection_status_disconneted);	
			break;
		case CONN_ACT:
			connectionIconBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.connection_status_control);
			break;
		default:
			connectionIconBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.connection_status_connected);
			break;
		}
		BitmapShader paperShader = new BitmapShader(connectionIconBitmap, Shader.TileMode.MIRROR, Shader.TileMode.MIRROR);
		Matrix paperMatrix = new Matrix();
		paperMatrix.setScale(1.0f / connectionIconBitmap.getWidth(), 1.0f / connectionIconBitmap.getHeight());
		paperShader.setLocalMatrix(paperMatrix);
		connectionIconPaint = new Paint();
		connectionIconPaint.setFilterBitmap(false);
		connectionIconPaint.setStyle(Paint.Style.FILL);
		connectionIconPaint.setShader(paperShader);
		postInvalidate();
		
	}
}
