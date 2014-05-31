/**
 * Rocket Colibri © 2014
 */
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
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import ch.hsr.rocketcolibri.view.resizable.ResizeConfig;
import ch.hsr.rocketcolibri.widgetdirectory.UiOutputDataType;
import ch.hsr.rocketcolibri.widgetdirectory.uioutputdata.ConnectionState;


/**
 * @short widget to display the connection status received from the ServoController
 * 
 *  The status of the connection is indicated with a icon according to the Systemschnittstellen document 
 */
public class ConnectionStatusWidget extends RCWidget {
	private RectF connectionIconRect;
	private Paint connectionIconPaint;
	private Bitmap connectionIconBitmap;
	
	
	public ConnectionStatusWidget(Context context, ViewElementConfig elementConfig) {
		super(context, elementConfig);
		init(context, null);
	}
	
	public ConnectionStatusWidget(Context context, RCWidgetConfig widgetConfig) {
		super(context, widgetConfig);
		init(context, null);
	}
	
	private void init(Context context, AttributeSet attrs) {
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
	protected void onDraw(Canvas canvas) {
		float scale = (float) getWidth();
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.scale(scale, scale);
		canvas.drawRect(connectionIconRect, connectionIconPaint);
		canvas.restore();
		super.onDraw(canvas);
	}
	
	private void setConnectionState(s state)
	{
		switch(state)
		{
		case DISC:
			connectionIconBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.connection_status_disconneted);	
			break;
		case CONN_CONTROL:
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

	@Override
	public void onNotifyUiOutputSink(Object p) 
	{
		ConnectionState data = (ConnectionState)p;
		setConnectionState(data.getState());
	}
	
	@Override
	public UiOutputDataType getType()
	{
		return UiOutputDataType.ConnectionState;
	}
	
	@Override
	public void updateProtocolMap() {
		// TODO Auto-generated method stub
		
	}
	
	public static ViewElementConfig getDefaultViewElementConfig() {
		ResizeConfig rc = new ResizeConfig();
	    rc.maxHeight=150;
	    rc.minHeight=50;
	    rc.maxWidth=150;
	    rc.minWidth=50;
	    LayoutParams lp = new LayoutParams(100, 100 , 0, 0);
	    ViewElementConfig elementConfig = new ViewElementConfig(ConnectionStatusWidget.class.getName(), lp, rc);
	    elementConfig.setAlpha(1);
	    return elementConfig;
	}
}
