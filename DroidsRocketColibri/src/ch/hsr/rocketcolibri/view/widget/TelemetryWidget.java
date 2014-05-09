/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import ch.hsr.rocketcolibri.widgetdirectory.UiOutputDataType;
import ch.hsr.rocketcolibri.widgetdirectory.uioutputdata.UserData;

/**
 * @short widget to display the telemetry data received from the ServoController 
 */
public class TelemetryWidget extends RCWidget 
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
	
	private void setTelemetryData(String telemetryData)
	{
		mDisplayText = telemetryData;
		Log.d("TelemetryWidget", mDisplayText);
		postInvalidate();
	}
	
	@Override
	public void onNotifyUiOutputSink(Object p) 
	{
		UserData data = (UserData)p;
		if(null != data.getActiveUser())
			setTelemetryData(data.getActiveUser().getName() +"("+ data.getActiveUser().getIpAddress() +")");
		else
			setTelemetryData("");
	}
	
	@Override
	public UiOutputDataType getType()
	{
		return UiOutputDataType.ConnectedUsers;
	}
}
