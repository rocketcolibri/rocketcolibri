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
import ch.hsr.rocketcolibri.view.custimizable.CustomizableView;

public class ConnectionStatusWidget extends CustomizableView 
{
	private RectF connectionIconRect;
	private Paint connectionIconPaint;
	private Bitmap connectionIconBitmap;
	
	
	public ConnectionStatusWidget(Context context) 
	{
		super(context);
		init(context, null);
	}

	public ConnectionStatusWidget(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		init(context, attrs);
	}

	public ConnectionStatusWidget(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		init(context, attrs);
	}
	
	private void init(Context context, AttributeSet attrs) 
	{
		connectionIconRect = new RectF(0.0f, 0.0f, 1.0f, 1.0f);
		connectionIconBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.connection_status_connected);
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
}
