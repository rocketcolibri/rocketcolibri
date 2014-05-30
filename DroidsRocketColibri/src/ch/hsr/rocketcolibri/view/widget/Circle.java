/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.view.widget;

import java.util.HashMap;
import java.util.Map;

import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RCConstants;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocol;
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.custimizable.CustomizableView;
import ch.hsr.rocketcolibri.view.custimizable.ModusChangeListener;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import ch.hsr.rocketcolibri.view.resizable.ResizeConfig;
import ch.hsr.rocketcolibri.widgetdirectory.UiOutputDataType;
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
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Toast;
import android.content.res.TypedArray;

/**
 * @author Artan Veliju
 */
public final class Circle extends RCWidget {
//	private static final String TAG = Circle.class.getSimpleName();
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
	private Map<String, String> protocolMap = new HashMap<String, String>();
	private int tChannelV = -1;
	private int tChannelH = -1;
	private OnChannelChangeListener tControlModusListener;
	private MyOnTouchListener tInternalControlListener = new MyOnTouchListener();
	
	public Circle(Context context, ViewElementConfig elementConfig) {
		super(context, elementConfig);
		backgroundResource = R.drawable.cross;
		positionInPercentX = 20;
		positionInPercentY = 100;
		orientationSide = "left";
		diameterInDP = elementConfig.getLayoutParams().width;
		init(context, null);
	}

	public Circle(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public Circle(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}
	
	/**
	 * the MyOnTouchListener holds a horizontal an vertical channel listener
	 */
	class MyOnTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			try{
				Log.d("onTouchListener", 
						String.valueOf(event.getAxisValue(MotionEvent.AXIS_Y)) + "," + 
					    String.valueOf(event.getAxisValue(MotionEvent.AXIS_X)));
				tControlModusListener.onChannelChange(tChannelH, eventPoistionToChannelValue(event.getAxisValue(MotionEvent.AXIS_X)));
				tControlModusListener.onChannelChange(tChannelV, eventPoistionToChannelValue(event.getAxisValue(MotionEvent.AXIS_Y)));
			}catch(Exception e){
				Toast.makeText(Circle.this.getContext(), "check your channel configuration!", Toast.LENGTH_SHORT).show();
			}
			return true;
		}
	}
	
	@Override
	public void setControlModusListener(OnChannelChangeListener channelListener) {
		tControlModusListener = channelListener;
		if(areChannelsValid())
			setOnTouchListener(tInternalControlListener); 
	}
	
	private boolean areChannelsValid(){
		return tChannelH>-1 || tChannelV>-1;
	}

	/**
	 * converts the position read from the event to the channel position
	 * @param eventPos (event position)
	 * @return channel position
	 */
	public int eventPoistionToChannelValue(float eventPos) {
		int channelPos = (int)(eventPos * RocketColibriProtocol.MAX_CHANNEL_VALUE / diameterInDP);
		if (channelPos > RocketColibriProtocol.MAX_CHANNEL_VALUE) return RocketColibriProtocol.MAX_CHANNEL_VALUE;
		else if (channelPos < RocketColibriProtocol.MIN_CHANNEL_VALUE) return RocketColibriProtocol.MIN_CHANNEL_VALUE;
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
		
		//init protocol mapping
		protocolMap.put(RCConstants.CHANNEL_H, "");
		protocolMap.put(RCConstants.INVERTED_H, "");
		protocolMap.put(RCConstants.MAX_RANGE_H, "");
		protocolMap.put(RCConstants.MIN_RANGE_H, "");
		protocolMap.put(RCConstants.TRIMM_H, "");
		
		protocolMap.put(RCConstants.CHANNEL_V, "");
		protocolMap.put(RCConstants.INVERTED_V, "");
		protocolMap.put(RCConstants.MAX_RANGE_V, "");
		protocolMap.put(RCConstants.MIN_RANGE_V, "");
		protocolMap.put(RCConstants.TRIMM_V, "");
	}
	
	private void initListener(){
		setModusChangeListener(new ModusChangeListener() {
			public void customizeModeDeactivated() {
				if(areChannelsValid())
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

	}
	
	private void initPosition(){
		if (this.getViewTreeObserver().isAlive()) {
			this.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						setMeasuredDimension(diameterInDP, diameterInDP);
					    float density  = getResources().getDisplayMetrics().density;
					    // FIXME @artan I fixed that but I'm not sure if we have am mess with dp an pixels 
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
	
	private int chooseDimension(int mode, int size) {
		if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
			return size;
		} else { // (mode == MeasureSpec.UNSPECIFIED)
			return getPreferredSize();
		} 
	}
	
	// in case there is no size specified
	private int getPreferredSize() {
		return 150;
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
		super.onDraw(canvas);
	}

	@Override
	public Map<String, String> getProtocolMap() {
		return protocolMap;
	}

	@Override
	public void updateProtocolMap() {
		tChannelH = getInt(RCConstants.CHANNEL_H);
		tChannelV = getInt(RCConstants.CHANNEL_V);
	}
	
	private int getInt(String key){
		try{
			return Integer.parseInt(protocolMap.get(key));
		}catch(NumberFormatException e){
			return -1;
		}
	}

	@Override
	public int getNumberOfChannelListener() {
		return 2; 
	}
	
	public static ViewElementConfig getDefaultViewElementConfig() {
		ResizeConfig rc = new ResizeConfig();
	    rc.keepRatio=true;
	    rc.maxHeight=500;
	    rc.minHeight=50;
	    rc.maxWidth=500;
	    rc.minWidth=50;
	    LayoutParams lp = new LayoutParams(380, 380 , 100, 300);
	    ViewElementConfig elementConfig = new ViewElementConfig(Circle.class.getName(), lp, rc);
	    return elementConfig;
	}
	
}
