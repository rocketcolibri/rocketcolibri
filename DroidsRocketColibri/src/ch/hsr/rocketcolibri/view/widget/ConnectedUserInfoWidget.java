/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.view.widget;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.View;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.protocol.RcOperator;
import ch.hsr.rocketcolibri.ui_data.output.IUiOutputSinkChangeObserver;
import ch.hsr.rocketcolibri.ui_data.output.UiOutputDataType;
import ch.hsr.rocketcolibri.ui_data.output.UserData;
import ch.hsr.rocketcolibri.util.DrawingTools;
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.custimizable.ICustomizableView;
import ch.hsr.rocketcolibri.view.custimizable.ModusChangeListener;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;

/**
 * @short widget to display the user data received from the ServoController 
 */
public class ConnectedUserInfoWidget extends View implements ICustomizableView, IRCWidget, IUiOutputSinkChangeObserver {
	
	protected RCWidgetConfig tWidgetConfig;
	private Paint tUserBitmapPaint;
	private Bitmap tObserverBitmap;
	private Bitmap tControlBitmap;
	private Bitmap tUsersBitmap;
	private Paint tTextPaint;
	
	static final int tFontSize = 24;
	static final int tLineSpace = 4;
	static final int tBorderSize = 10;
    private Paint tRectPaint;
    private RectF tRectRectF;
    private Context tContext;
	private boolean tCustomizeModusActive = false;

	private UserData tUserData;
	
	public ConnectedUserInfoWidget(Context context, ViewElementConfig elementConfig) {
		super(context);
		tWidgetConfig = new RCWidgetConfig(elementConfig);
		setLayoutParams(tWidgetConfig.viewElementConfig.getLayoutParams());
		setAlpha(tWidgetConfig.viewElementConfig.getAlpha());
		tContext = context;
		init(context, null);
	}
	
	public ConnectedUserInfoWidget(Context context, RCWidgetConfig widgetConfig) {
		super(context);
		tWidgetConfig = widgetConfig;
		setLayoutParams(tWidgetConfig.viewElementConfig.getLayoutParams());
		setAlpha(tWidgetConfig.viewElementConfig.getAlpha());
		tContext = context;
		init(context, null);
	}
	
	@Override protected void finalize() throws Throwable
	{
	  try {
		  tObserverBitmap.recycle();
		  tObserverBitmap = null;
		  tControlBitmap.recycle();
		  tControlBitmap = null;
		  tUsersBitmap.recycle();
		  tUsersBitmap = null;
	  } catch (Exception e) {}
	  finally {
	    super.finalize();
	  }
	}
	
	private void init(Context context, AttributeSet attrs) {
		tRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		tRectPaint.setColor(Color.DKGRAY);
		tRectPaint.setAlpha(200);
		tRectRectF  = new RectF();
		
		tTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		tTextPaint.setColor(Color.WHITE);
		tTextPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
		tTextPaint.setTextSize(tFontSize);
		tObserverBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(tContext.getResources(), R.drawable.connection_status_connected_observe), tFontSize, tFontSize, true);	
		tControlBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(tContext.getResources(), R.drawable.connection_status_control), tFontSize, tFontSize, true);
		tUsersBitmap =  BitmapFactory.decodeResource(tContext.getResources(), R.drawable.connected_users);
		
		tUserBitmapPaint = new Paint();
		tUserBitmapPaint.setFilterBitmap(false);
	}
	
	private ModusChangeListener tModusChangeListener = new ModusChangeListener() {
		@Override
		public void customizeModeDeactivated() {
		}

		@Override
		public void customizeModeActivated() {
		}
	};

	protected void onMeasure(int wMeasureSpec, int hMeasureSpec)	{
		int measuredHeight = measureHeight(hMeasureSpec);
		int measuredWidth  = measureWitdth(wMeasureSpec);
		
		tRectPaint.setShader(new LinearGradient(0,0,measuredWidth, measuredHeight,Color.WHITE, Color.LTGRAY, TileMode.CLAMP));
		
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
	
	@Override
	protected void onDraw(Canvas canvas) {
		// draw background rectangle
		DrawingTools.drawRoundWidgetBacktground(tRectRectF, canvas, tRectPaint);
		
		// draw bitmap
		int size = Math.min(canvas.getHeight(),canvas.getWidth());
		canvas.drawBitmap(DrawingTools.resizeBitmap(this.tUsersBitmap,size,size), canvas.getWidth()-size, 0, null);
		
		// draw text
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

		if (tCustomizeModusActive) 
			DrawingTools.drawCustomizableForground(this, canvas);

	}
	
	public static ViewElementConfig getDefaultViewElementConfig(){
	    return DefaultViewElementConfigRepo.getDefaultConfig(ConnectedUserInfoWidget.class);
	}
	
	/**
	 * Implementation of the interfaces
	 */

	@Override
	public void create(RCWidgetConfig rcWidgetConfig) {
		tWidgetConfig = rcWidgetConfig;
		init(tContext, null);
	}

	@Override
	public void create(ViewElementConfig vElementConfig) {
		tWidgetConfig = new RCWidgetConfig(vElementConfig);
		init(tContext, null);
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
	public void updateProtocolMap() {}

	@Override
	public void onNotifyUiOutputSink(Object p)	{
		tUserData = (UserData)p;
		postInvalidate();
	}

	@Override
	public UiOutputDataType getType() {
		return UiOutputDataType.ConnectedUsers;
	}


	@Override
	public void setCustomizeModusListener(OnTouchListener customizeModusListener) {
		setOnTouchListener(customizeModusListener);
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
	public List<ch.hsr.rocketcolibri.ui_data.input.UiInputSourceChannel> getUiInputSourceList() {
		return null;
	}

}
