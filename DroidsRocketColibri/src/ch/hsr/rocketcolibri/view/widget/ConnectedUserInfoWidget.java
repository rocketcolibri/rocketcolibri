/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.view.widget;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.protocol.RcOperator;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import ch.hsr.rocketcolibri.widgetdirectory.UiOutputDataType;
import ch.hsr.rocketcolibri.widgetdirectory.uioutputdata.UserData;

/**
 * @short widget to display the user data received from the ServoController 
 */
public class ConnectedUserInfoWidget extends RCWidget 
{
	private Paint tUserBitmapPaint;
	private Bitmap tObserverBitmap;
	private Bitmap tControlBitmap;
	private Bitmap tUsersBitmap;
	private Paint tTextPaint;
	static final int tFontSize = 20;
	static final int tLineSpace = 4;
	static final int tBorderSize = 10;
	private UserData tUserData;
	
	public ConnectedUserInfoWidget(Context context, ViewElementConfig elementConfig) {
		super(context, elementConfig);
		init(context, null);
	}
	
	private void init(Context context, AttributeSet attrs) {
		tTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		tTextPaint.setColor(Color.BLACK);
		tTextPaint.setTextSize(tFontSize);
		tObserverBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.connection_status_connected), tFontSize, tFontSize, true);	
		tControlBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.connection_status_control), tFontSize, tFontSize, true);
		tUsersBitmap =  BitmapFactory.decodeResource(getContext().getResources(), R.drawable.connected_users);
		
		tUserBitmapPaint = new Paint();
		tUserBitmapPaint.setFilterBitmap(false);
	}
	
	@Override
	protected void onMeasure(int wMeasureSpec, int hMeasureSpec)	{
		int measuredHeight = measureHeight(hMeasureSpec);
		int measuredWidth  = measureWitdth(wMeasureSpec);
		setMeasuredDimension(measuredWidth, measuredHeight);
	}
	
	private int measureHeight(int measureSpec) {
		//int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		return specSize;
	}
	
	private int measureWitdth(int measureSpec) {
		//int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		return specSize;
	}
	
	private String getUserText(RcOperator user){
		return user.getName()+"("+user.getIpAddress()+")";
	}
	
	private void drawUserLine(Canvas canvas, int line, Bitmap bitmap, RcOperator user) {
		canvas.drawBitmap(bitmap, tBorderSize, tBorderSize+(tLineSpace+tFontSize)*line, null);
		canvas.drawText(getUserText(user), tFontSize+tBorderSize+tLineSpace  , tBorderSize+tFontSize+(tLineSpace+tFontSize)*line, tTextPaint);
	}
	
	public static Bitmap resizeBitmap(Bitmap bitmap, int width, int height) { //width - height in pixel not in DP
	    bitmap.setDensity(Bitmap.DENSITY_NONE); 
	    Bitmap newbmp = Bitmap.createScaledBitmap(bitmap, width, height, true);
	    return newbmp;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if(null != tUserData) {
			int line = 0;
			if(null != tUserData.getActiveUser()) {
				drawUserLine(canvas, line++, tControlBitmap, tUserData.getActiveUser());
			}		
			List<RcOperator> users = tUserData.getPassivUsers();
			for(RcOperator user : users) {
				drawUserLine(canvas, line++, tObserverBitmap, user);
			}
		}
		
		int size = Math.min(canvas.getHeight(),canvas.getWidth());
		canvas.drawBitmap(resizeBitmap(this.tUsersBitmap,size,size), canvas.getWidth()-size, 0, null);
		
		super.onDraw(canvas);
	}

	@Override
	public void onNotifyUiOutputSink(Object p)	{
		tUserData = (UserData)p;
		postInvalidate();
	}
	
	@Override
	public UiOutputDataType getType() {
		return UiOutputDataType.ConnectedUsers;
	}
}