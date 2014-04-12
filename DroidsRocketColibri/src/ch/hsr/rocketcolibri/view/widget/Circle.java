package ch.hsr.rocketcolibri.view.widget;

import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocol;
import ch.hsr.rocketcolibri.view.MyAbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.custimizable.CustomizableView;
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
import android.content.res.TypedArray;

public final class Circle extends CustomizableView {

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
	private static final float rimSize       = 0.02f;

	/**
	 * converts the position read from the event to the channel position
	 * @param eventPos (event position)
	 * @return channel position
	 */
	public int eventPoistionToChannelValue(float eventPos)
	{
		int channelPos = (int)(eventPos * RocketColibriProtocol.MAX_CHANNEL_VALUE / diameterInDP);
		if (channelPos > RocketColibriProtocol.MAX_CHANNEL_VALUE) return RocketColibriProtocol.MAX_CHANNEL_VALUE;
		else if (channelPos < RocketColibriProtocol.MIN_CHANNEL_VALUE) return RocketColibriProtocol.MIN_CHANNEL_VALUE;
		else 
			return channelPos;
	}
	
	/**
	 * the MyOnTouchListener holds a horizontal an vertical channel listener
	 */
	class MyOnTouchListener implements OnTouchListener
	{
		private OnChannelChangeListener hChannel;
		private OnChannelChangeListener vChannel;
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			Log.d("onTouchListener", 
					String.valueOf(event.getAxisValue(MotionEvent.AXIS_Y)) + "," + 
				    String.valueOf(event.getAxisValue(MotionEvent.AXIS_X)));
	
			if (hChannel != null)
				hChannel.onChannelChange(eventPoistionToChannelValue(event.getAxisValue(MotionEvent.AXIS_X)));
			
			if (vChannel != null)
				vChannel.onChannelChange(eventPoistionToChannelValue(event.getAxisValue(MotionEvent.AXIS_Y)));
			
			return false;
		}
		
		public void setHOnChangeListener(OnChannelChangeListener cl)
		{
			hChannel = cl;
		}
		
		public void setVOnChangeListener(OnChannelChangeListener cl)
		{
			vChannel = cl;
		}
	}
	
	private MyOnTouchListener mListener = new MyOnTouchListener();
	
	/**
	 * set the onChannelChangeListener for the horizontal channel control
	 * @param onChannelChangeListener
	 */
	public void setOnHChannelChangeListener(OnChannelChangeListener onChannelChangeListener) 
	{
		mListener.setHOnChangeListener(onChannelChangeListener);
	}

	/**
	 * set the onChannelChangeListener for the vertical channel control
	 * @param onChannelChangeListener
	 */
	public void setOnVChannelChangeListener(OnChannelChangeListener onChannelChangeListener) 
	{
		mListener.setVOnChangeListener(onChannelChangeListener);
	}
	
	public Circle(Context context) {
		super(context);
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
		// TODO Auto-generated method stub
		Log.d("onFinishInflate", "onInitializeAccessibilityNodeInfo");
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
		
		this.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Log.d("OnClickListener", "");
			}
		});
		
		
		this.setOnGenericMotionListener(new OnGenericMotionListener(){

			@Override
			public boolean onGenericMotion(View v, MotionEvent event) {
				Log.d("OnGenericMotionListener", String.valueOf(event.getY()));
				return false;
			}
			
		});
		this.setOnDragListener(new OnDragListener(){

			@Override
			public boolean onDrag(View v, DragEvent event) {
				Log.d("OnDragListener", String.valueOf(event.getY()));
				return false;
			}
		});
		
		this.setOnTouchListener(mListener); 
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

//	@Override
//	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//		Log.d(TAG, "Size changed to " + w + "x" + h);
//	}
	
}
