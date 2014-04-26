package ch.hsr.rocketcolibri.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import ch.hsr.rocketcolibri.view.custimizable.CustomizableView;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;

/**
 * @short widget to display the telemetry data received from the ServoController 
 */
public class TelemetryWidget extends CustomizableView 
{
	private String mDisplayText = "";
	private Paint mTextPaint;
	static final int fontSize = 20;
	static final int borderSize = 10;

	public TelemetryWidget(Context context, ViewElementConfig elementConfig)
	{
		super(context, elementConfig);
		init(context, null);
	}
	
	private void init(Context context, AttributeSet attrs) 
	{
		// prepare drawing tools
		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setColor(Color.BLACK);
		mTextPaint.setTextSize(fontSize);
	}
	
	@Override
	protected void onMeasure(int wMeasureSpec, int hMeasureSpec)
	{
		int measuredHeight = measureHeight(hMeasureSpec);
		int measuredWidth  = measureWitdth(wMeasureSpec);
		setMeasuredDimension(measuredWidth, measuredHeight);
	}
	
	private int measureHeight(int measureSpec)
	{
		//int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		return specSize;
	}
	
	private int measureWitdth(int measureSpec)
	{
		//int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		return specSize;
	}
	
	@Override
	protected void onDraw(Canvas canvas) 
	{
		// float textWidth = mTextPaint.measureText(mDisplayText);	
		canvas.drawText(mDisplayText, borderSize , borderSize + fontSize, mTextPaint);
		super.onDraw(canvas);
	}
	
	public void setTelemetryData(String telemetryData)
	{
		mDisplayText = telemetryData;
		Log.d("TelemetryWidget", mDisplayText);
		postInvalidate();
	}
}
