/**
 * Rocket Colibri Â© 2014
 */
package ch.hsr.rocketcolibri.view.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RCConstants;
import ch.hsr.rocketcolibri.protocol.RCProtocolUdp;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolFsm.s;
import ch.hsr.rocketcolibri.ui_data.input.UiInputSourceChannel;
import ch.hsr.rocketcolibri.ui_data.output.ConnectionState;
import ch.hsr.rocketcolibri.ui_data.output.IUiOutputSinkChangeObserver;
import ch.hsr.rocketcolibri.ui_data.output.UiOutputDataType;
import ch.hsr.rocketcolibri.util.DrawingTools;
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.custimizable.ICustomizableView;
import ch.hsr.rocketcolibri.view.custimizable.ModusChangeListener;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Toast;
import android.content.res.Resources;
import android.content.res.TypedArray;

/**
 * @author Artan Veliju
 */
public final class Circle extends View implements ICustomizableView, IRCWidget, IUiOutputSinkChangeObserver  {
//	private static final String TAG = Circle.class.getSimpleName();
	private boolean tDebug;
	private Paint dbgLine;
	private RectF rimRect;
	private Paint rimPaint;
	private Paint rimCirclePaint;
	private RectF faceRect;
	private Bitmap faceTexture;
	private Paint facePaint;
	private Paint rimShadowPaint;
	private int backgroundResource;
	private int positionInPercentX;
	private int positionInPercentY;
	private String orientationSide = "left";
	public int diameterInDP;
	public static final int maxChannel = 1000;
	private static final float rimSize = 0.02f;
	private UiInputSourceChannel tChannelV = new UiInputSourceChannel();
	private UiInputSourceChannel tChannelH = new UiInputSourceChannel();
	private MyOnTouchListener tInternalControlListener = new MyOnTouchListener();
	private boolean tCustomizeModusActive = true;
	private boolean tIsControlling = false;
	//inner circle stuff
    private int tRadius = 100;
    private CircleArea tInnerCircleDimension;
    private Paint tCirclePaint;
	//--------
    
	protected RCWidgetConfig tWidgetConfig;
	protected OnTouchListener tCustomizeModusListener;
	
	public Circle(Context context, RCWidgetConfig rcWidgetConfig){
		super(context);
		tWidgetConfig = rcWidgetConfig;
		
//		RocketColibriDefaults.dpToPixel(getContext().getResources().getDisplayMetrics().density, tWidgetConfig);
		setLayoutParams(tWidgetConfig.viewElementConfig.getLayoutParams());
		setAlpha(tWidgetConfig.viewElementConfig.getAlpha());
		backgroundResource = R.drawable.cross;
		positionInPercentX = 20;
		positionInPercentY = 100;
		orientationSide = "left";
		diameterInDP = rcWidgetConfig.viewElementConfig.getLayoutParams().width;
		tChannelV.setWidgetRange(0, diameterInDP);
		tChannelH.setWidgetRange(0, diameterInDP);
		init(context, null);
		updateProtocolMap();
	}
	
	public Circle(Context context, ViewElementConfig elementConfig) {
		super(context);
		tWidgetConfig = new RCWidgetConfig(elementConfig);
		setLayoutParams(elementConfig.getLayoutParams());
		setAlpha(elementConfig.getAlpha());
		backgroundResource = R.drawable.cross;
		positionInPercentX = 20; 
		positionInPercentY = 100;
		orientationSide = "left";
		diameterInDP = elementConfig.getLayoutParams().width;
		tChannelV.setWidgetRange(0, diameterInDP);
		tChannelH.setWidgetRange(0, diameterInDP);
		init(context, null);
		initDefaultProtocolConfig();
	}
	
	@Override protected void finalize() throws Throwable
	{
	  try {
		  faceTexture.recycle();
		  faceTexture = null;
	  } catch (Exception e) {}
	  finally {
	    super.finalize();
	  }
	}
	
	private void initDefaultProtocolConfig(){
		tWidgetConfig.protocolMap = new HashMap<String, String>();
		if (UiInputSourceChannel.CHANNEL_UNASSIGNED == tChannelH.getChannelAssignment() )
			tWidgetConfig.protocolMap.put(RCConstants.CHANNEL_ASSIGNMENT_H, "");
		else
			tWidgetConfig.protocolMap.put(RCConstants.CHANNEL_ASSIGNMENT_H, Integer.valueOf(tChannelH.getChannelAssignment()).toString());
		tWidgetConfig.protocolMap.put(RCConstants.INVERTED_H, Boolean.valueOf(tChannelH.getChannelInverted()).toString());
		tWidgetConfig.protocolMap.put(RCConstants.MAX_RANGE_H, Integer.valueOf(tChannelH.getChannelMaxRange()).toString());
		tWidgetConfig.protocolMap.put(RCConstants.MIN_RANGE_H, Integer.valueOf(tChannelH.getChannelMinRange()).toString());
		tWidgetConfig.protocolMap.put(RCConstants.DEFAULT_POSITION_H, Integer.valueOf(tChannelH.getChannelDefaultPosition()).toString());
		tWidgetConfig.protocolMap.put(RCConstants.TRIMM_H, Integer.valueOf(tChannelH.getChannelTrimm()).toString());
		tWidgetConfig.protocolMap.put(RCConstants.STICKY_H, Boolean.valueOf(tChannelH.getWidgetSticky()).toString());
		
		if (UiInputSourceChannel.CHANNEL_UNASSIGNED == tChannelV.getChannelAssignment() )
			tWidgetConfig.protocolMap.put(RCConstants.CHANNEL_ASSIGNMENT_V, "");
		else
			tWidgetConfig.protocolMap.put(RCConstants.CHANNEL_ASSIGNMENT_V, Integer.valueOf(tChannelV.getChannelAssignment()).toString());
		tWidgetConfig.protocolMap.put(RCConstants.INVERTED_V, Boolean.valueOf(tChannelV.getChannelInverted()).toString());
		tWidgetConfig.protocolMap.put(RCConstants.MAX_RANGE_V, Integer.valueOf(tChannelV.getChannelMaxRange()).toString());
		tWidgetConfig.protocolMap.put(RCConstants.MIN_RANGE_V, Integer.valueOf(tChannelV.getChannelMinRange()).toString());
		tWidgetConfig.protocolMap.put(RCConstants.DEFAULT_POSITION_V, Integer.valueOf(tChannelV.getChannelDefaultPosition()).toString());
		tWidgetConfig.protocolMap.put(RCConstants.TRIMM_V, Integer.valueOf(tChannelV.getChannelTrimm()).toString());
		tWidgetConfig.protocolMap.put(RCConstants.STICKY_V, Boolean.valueOf(tChannelV.getWidgetSticky()).toString());
		
		tWidgetConfig.protocolMap.put(RCConstants.DEBUG, Boolean.valueOf(false).toString());		
	}
	
	/**
	 * the MyOnTouchListener holds a horizontal an vertical channel listener
	 */
	private boolean tChannelError = false;
	class MyOnTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
	        switch (event.getActionMasked()) {
	          case MotionEvent.ACTION_DOWN:
	        	  updateInnerCirclePosition((int) event.getX(0), (int) event.getY(0));
	              return true;
	          case MotionEvent.ACTION_POINTER_DOWN:
	        	  updateInnerCirclePosition((int) event.getX(0), (int) event.getY(0));
	              return true;
	          case MotionEvent.ACTION_MOVE:
	        	  updateInnerCirclePosition((int) event.getX(0), (int) event.getY(0));
	              return true;
	          case MotionEvent.ACTION_UP:
	        	  updateInnerCircleAndCheckSticky((int) event.getX(0), (int) event.getY(0));
	              if(tChannelError){
	            	  Toast.makeText(Circle.this.getContext(), "check your channel configuration!", Toast.LENGTH_SHORT).show();
	            	  tChannelError = false;
	              }
	              return true;
	          case MotionEvent.ACTION_POINTER_UP:
	              invalidate();
	              return true;
	          case MotionEvent.ACTION_CANCEL:
	              return true;
	        }
			return false;
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		tInnerCircleDimension.radius = tRadius = tWidgetConfig.viewElementConfig.getLayoutParams().width/6;
		updateWithDefaultPosition();
	}
	
	private ModusChangeListener tModusChangeListener = new ModusChangeListener() {
		@Override
		public void customizeModeDeactivated() {
		}

		@Override
		public void customizeModeActivated() {
		}
	};

	@Override
	public void setCustomizeModusListener(OnTouchListener customizeModusListener){
		tCustomizeModusListener = customizeModusListener;
		setOnTouchListener(tCustomizeModusListener);
	}
	
	/**
	 * converts the position read from the event to the channel position
	 * @param channel Position
	 * @return whdget position
	 */
	public int channelValueToWidgetPosition(int channelWidgetPos) {
		int channelPos = diameterInDP * channelWidgetPos / RCProtocolUdp.MAX_CHANNEL_VALUE;
		if (channelPos > RCProtocolUdp.MAX_CHANNEL_VALUE) return RCProtocolUdp.MAX_CHANNEL_VALUE;
		else if (channelPos < RCProtocolUdp.MIN_CHANNEL_VALUE) return RCProtocolUdp.MIN_CHANNEL_VALUE;
		else return channelPos;
	}
	
	/**
	 * converts the position read from the event to the channel position
	 * @param eventPos (event position)
	 * @return channel position
	 */
	public int widgetPoistionToChannelValue(float eventPos) {
		int channelPos = (int)(eventPos * RCProtocolUdp.MAX_CHANNEL_VALUE / diameterInDP);
		if (channelPos > RCProtocolUdp.MAX_CHANNEL_VALUE) return RCProtocolUdp.MAX_CHANNEL_VALUE;
		else if (channelPos < RCProtocolUdp.MIN_CHANNEL_VALUE) return RCProtocolUdp.MIN_CHANNEL_VALUE;
		else return channelPos;
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}
	
	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		Bundle bundle = (Bundle) state;
		Parcelable superState = bundle.getParcelable("superState");
		super.onRestoreInstanceState(superState);
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();

		Bundle state = new Bundle();
		state.putParcelable("superState", superState);
		return state;
	}

	private float percentInDP(float displayDP, int percent){
		return displayDP/100*percent;
	}

	@Override
	protected void onFinishInflate() {
		setMeasuredDimension(1, 1);
		super.onFinishInflate();
	}
	
	private void init(Context context, AttributeSet attrs) {
		if (context != null && attrs != null){
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Circle);
			backgroundResource = a.getResourceId(R.styleable.Circle_backgroundResource, backgroundResource);
			positionInPercentX = a.getInteger(R.styleable.Circle_positionInPercentX, positionInPercentX);
			positionInPercentY = a.getInteger(R.styleable.Circle_positionInPercentY, positionInPercentY);
			orientationSide = a.getString(R.styleable.Circle_orientationSide);
			diameterInDP = a.getInt(R.styleable.Circle_diameterInDP, diameterInDP);
			initPosition();
		}
		initDrawingTools();
		initListener();
	}
	
	private void initListener(){
		setModusChangeListener(new ModusChangeListener() {
			public void customizeModeDeactivated() {
				setOnTouchListener(tInternalControlListener); 
			}
			public void customizeModeActivated() {
				setOnTouchListener(tCustomizeModusListener);
			}
		});
	}

	private void initDrawingTools() {
		rimRect = new RectF(0.0f, 0.0f, 1.0f, 1.0f);

		faceRect = new RectF();
		faceRect.set(rimRect.left  + rimSize, rimRect.top    + rimSize, 
			         rimRect.right - rimSize, rimRect.bottom - rimSize);		
		
		faceTexture = BitmapFactory.decodeResource(getContext().getResources(), backgroundResource);
		BitmapShader paperShader = new BitmapShader(faceTexture, 
												    Shader.TileMode.MIRROR, 
												    Shader.TileMode.MIRROR);
		Matrix paperMatrix = new Matrix();
		paperMatrix.setScale(1.0f / faceTexture.getWidth(), 
				             1.0f / faceTexture.getHeight());

		paperShader.setLocalMatrix(paperMatrix);

		rimShadowPaint = new Paint();
		rimShadowPaint.setShader(new RadialGradient(0.5f, 0.5f, rimRect.width() / 2.1f, 
				                 new int[] { 0x00000000, 0x00000500, 0x50000500 },
				                 new float[] { 0.98f, 0.98f, 1.99f },
				                 Shader.TileMode.MIRROR));
		rimShadowPaint.setStyle(Paint.Style.FILL);

		// the linear gradient is a bit skewed for realism
		rimPaint = new Paint();
		rimPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		rimPaint.setShader(new LinearGradient(0.40f, 0.0f, 0.60f, 1.0f, 
										   Color.rgb(0xf0, 0xf5, 0xf0),
										   Color.rgb(0x30, 0x31, 0x30),
										   Shader.TileMode.CLAMP));		

		rimCirclePaint = new Paint();
		rimCirclePaint.setAntiAlias(true);
		rimCirclePaint.setStyle(Paint.Style.STROKE);
		rimCirclePaint.setColor(Color.argb(0x4f, 0x33, 0x36, 0x33));
		rimCirclePaint.setStrokeWidth(0.005f);
		
		facePaint = new Paint();
		facePaint.setFilterBitmap(true);
		facePaint.setStyle(Paint.Style.FILL);
		facePaint.setShader(paperShader);
		
		initInnerStickyCircle();
		
		dbgLine = new Paint(Paint.ANTI_ALIAS_FLAG);
		dbgLine.setStrokeWidth(1);
		dbgLine.setStyle(Paint.Style.FILL_AND_STROKE);
		dbgLine.setTextSize(getPixels(15));
	}

	int getPixels(float size) {
	    DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
	    return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, size, metrics);
	}
	
	private void initInnerStickyCircle(){
		tRadius = tWidgetConfig.viewElementConfig.getLayoutParams().width/6;
	    tInnerCircleDimension = new CircleArea(tWidgetConfig.viewElementConfig.getLayoutParams().width/2, tWidgetConfig.viewElementConfig.getLayoutParams().width/2, tRadius);
	    tCirclePaint = new Paint();
	    tCirclePaint.setStrokeWidth(40);
	    tCirclePaint.setStyle(Paint.Style.FILL);
	}
    
	private void initPosition(){
		if (this.getViewTreeObserver().isAlive()) {
			this.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						setMeasuredDimension(diameterInDP, diameterInDP);
					    float dpHeight = getResources().getDisplayMetrics().heightPixels; /* removed / density */
					    float dpWidth  = getResources().getDisplayMetrics().widthPixels; /* removed / density */
						Circle.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
						int y = (int) (percentInDP(dpHeight, positionInPercentY)-getMeasuredHeight()/2);
						int x = 0;
						if(orientationSide.toLowerCase().equals("left")){
							x = (int) (percentInDP(dpWidth, positionInPercentX)-getMeasuredWidth()/2);
						}else if(orientationSide.toLowerCase().equals("right")){
							x = (int) (dpWidth-percentInDP(dpWidth, positionInPercentX)-getMeasuredWidth()/2);
						}
						LayoutParams lp = new LayoutParams(getMeasuredWidth(), getMeasuredHeight(), x, y);
						Circle.this.setLayoutParams(lp);
					}
				}
			);
		}
	}
	
	private void drawRim(Canvas canvas) {
		// first, draw the metallic body
		canvas.drawOval(rimRect, rimPaint);
		// now the outer rim circle
		canvas.drawOval(rimRect, rimShadowPaint);
	}
	
	private void drawFace(Canvas canvas) {		
		canvas.drawOval(faceRect, facePaint);
		// draw the inner rim circle
		canvas.drawOval(faceRect, rimCirclePaint);
		// draw the rim shadow inside the face
		canvas.drawOval(faceRect, rimShadowPaint);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		float scale = (float) getWidth();
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.scale(scale, scale);
		drawRim(canvas);
		drawFace(canvas);
		canvas.restore();
		
		if(tIsControlling)
		    tCirclePaint.setColor(Color.BLUE);
		else
			tCirclePaint.setColor(Color.LTGRAY);
		
		canvas.drawCircle(tInnerCircleDimension.centerX, tInnerCircleDimension.centerY, tInnerCircleDimension.radius, tCirclePaint);
		
		if (tCustomizeModusActive) 
			DrawingTools.drawCustomizableForground(this, canvas);
		
		if (tDebug) {
			String dbgString;
			if (UiInputSourceChannel.CHANNEL_UNASSIGNED == tChannelH.getChannelAssignment() ){
				dbgString = String.format(Locale.getDefault(),"H:unassigned");
				dbgLine.setColor(Color.RED);
			}
			else {
				dbgString = String.format(Locale.getDefault(), "H[%d]:%d", tChannelH.getChannelAssignment(), tChannelH.getChannelValue());
				dbgLine.setColor(Color.GREEN);
			}
			canvas.drawText(dbgString,0, getHeight()/2, dbgLine);
			if (UiInputSourceChannel.CHANNEL_UNASSIGNED == tChannelV.getChannelAssignment() ){
				dbgString = String.format(Locale.getDefault(), "V:unassigned");
				dbgLine.setColor(Color.RED);
			}
			else{
				dbgString = String.format(Locale.getDefault(), "V[%d]:%d", tChannelV.getChannelAssignment(), tChannelV.getChannelValue());
				dbgLine.setColor(Color.GREEN);
			}
			canvas.drawText(dbgString,getWidth()/2, dbgLine.getTextSize(), dbgLine);
		}
	}

	@Override
	public void onNotifyUiOutputSink(Object p) {
		ConnectionState data = (ConnectionState)p;
		tIsControlling = ((s.TRY_CONN == data.getState()) || (s.CONN_CONTROL == data.getState()));
		postInvalidate();
	}

	@Override
	public UiOutputDataType getType(){
		return UiOutputDataType.ConnectionState;
	}
	
	@Override
	public List<UiInputSourceChannel> getUiInputSourceList() {
		List<UiInputSourceChannel> list = new ArrayList<UiInputSourceChannel>();
		list.add(tChannelH);
		list.add(tChannelV);
	    return list;
	}
	
	@Override
	public void updateProtocolMap() {
		try{
			tChannelH.setChannelAssignment(getProtocolMapInt(RCConstants.CHANNEL_ASSIGNMENT_H));
			tChannelH.setChannelInverted(getProtocolMapBoolean(RCConstants.INVERTED_H));
			tChannelH.setChannelMaxRange(getProtocolMapInt(RCConstants.MAX_RANGE_H));
			tChannelH.setChannelMinRange(getProtocolMapInt(RCConstants.MIN_RANGE_H));
			tChannelH.setChannelDefaultPosition(getProtocolMapInt(RCConstants.DEFAULT_POSITION_H));
			tChannelH.setChannelTrimm(getProtocolMapInt(RCConstants.TRIMM_H));
			tChannelH.setWidgetSticky(getProtocolMapBoolean(RCConstants.STICKY_H));
			
			
			tChannelV.setChannelAssignment(getProtocolMapInt(RCConstants.CHANNEL_ASSIGNMENT_V));
			tChannelV.setChannelInverted(getProtocolMapBoolean(RCConstants.INVERTED_V));
			tChannelV.setChannelMaxRange(getProtocolMapInt(RCConstants.MAX_RANGE_V));
			tChannelV.setChannelMinRange(getProtocolMapInt(RCConstants.MIN_RANGE_V));
			tChannelV.setChannelDefaultPosition(getProtocolMapInt(RCConstants.DEFAULT_POSITION_V));
			tChannelV.setChannelTrimm(getProtocolMapInt(RCConstants.TRIMM_V));
			tChannelV.setWidgetSticky(getProtocolMapBoolean(RCConstants.STICKY_V));
			tDebug = getProtocolMapBoolean(RCConstants.DEBUG);	
			updateInnerCircleAndCheckSticky(-1,-1);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void updateInnerCircleAndCheckSticky(int x, int y){
		if(tChannelH.getWidgetSticky())
  	  		updateInnerCirclePosition(x, y);
		else
			updateWithDefaultPosition();
	}
	
	private void updateInnerCirclePosition(int x, int y){
		if(x < tRadius)
			tInnerCircleDimension.centerX = tRadius;
		else if (x > this.getWidth() - tRadius)
			tInnerCircleDimension.centerX = this.getWidth() - tRadius;
		else
			tInnerCircleDimension.centerX = x;
		
		if(y < tRadius)
			tInnerCircleDimension.centerY = tRadius;
		else if (y > this.getWidth() - tRadius)
			tInnerCircleDimension.centerY = this.getHeight() - tRadius;
		else
			tInnerCircleDimension.centerY = y;
		updateChannel( x, y);
		invalidate();
	}

	private void updateChannel(int x, int y) {
		// update channel
  		try{
  			tChannelH.setWidgetPosition(x);
  			tChannelV.setWidgetPosition(y);
		}catch(Exception e){
			tChannelError = true;
		}
	}

	private void updateWithDefaultPosition() {
		int x = tChannelH.setWidgetToDefault(); 
		int y = tChannelV.setWidgetToDefault();
		Log.d("Circle", "updateWithDefaultPosition " + x + "/" +y);
		updateInnerCirclePosition(x,y);
	}
	
	public int getNumberOfChannelListener() {
		return 2; 
	}
	
	public static ViewElementConfig getDefaultViewElementConfig() {
		return DefaultViewElementConfigRepo.getDefaultConfig(Circle.class);
	}

	@Override
	public void create(RCWidgetConfig rcWidgetConfig) {
		tWidgetConfig = rcWidgetConfig;
		init(getContext(), null);
	}

	@Override
	public void create(ViewElementConfig vElementConfig) {
		tWidgetConfig = new RCWidgetConfig(vElementConfig);
		init(getContext(), null);
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

	protected int getProtocolMapInt(String key){
		try{
			return Integer.parseInt(tWidgetConfig.protocolMap.get(key));
		}catch(NumberFormatException e){
			return -1;
		}
	}
	
	protected boolean getProtocolMapBoolean(String key){
		try{
			return Boolean.parseBoolean(tWidgetConfig.protocolMap.get(key));
		}catch(Exception e){
			return false;
		}
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

	@Override
	public void setModusChangeListener(ModusChangeListener mcl) {
		tModusChangeListener = mcl;
	}

	@Override
	public ViewElementConfig getViewElementConfig() {
		tWidgetConfig.viewElementConfig.setLayoutParams((LayoutParams) getLayoutParams());
		tWidgetConfig.viewElementConfig.setAlpha(getAlpha());
		return tWidgetConfig.viewElementConfig;
	}
	
    /** Stores dimension data about single circle */
    private static class CircleArea {
        int radius;
        int centerX;
        int centerY;

        CircleArea(int centerX, int centerY, int radius) {
            this.radius = radius;
            this.centerX = centerX;
            this.centerY = centerY;
        }

        @Override
        public String toString() {
            return "Circle[" + centerX + ", " + centerY + ", " + radius + "]";
        }
    }
}
