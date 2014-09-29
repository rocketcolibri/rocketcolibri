package ch.hsr.rocketcolibri.view.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ch.hsr.rocketcolibri.RCConstants;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolFsm.s;
import ch.hsr.rocketcolibri.ui_data.input.IUiInputSource;
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
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;


/**
 * This is a sample file on how to use the gravity sensor
 * https://github.com/a500381/SensorBallDraw/blob/master/SensorBallDraw/src/sensorball/view/BallDraw.java
 *
 * Offical android documentation
 * http://developer.android.com/reference/android/hardware/SensorEvent.html
 * 
 * @author lorenz
 *
 */
public class MotionControlWidget extends View implements ICustomizableView, IUiInputSource, IUiOutputSinkChangeObserver {
	private SensorManager tSensorManager;
	private Sensor tSensor;
	private SensorEventListener tSensorEventListener;
	final String TAG = "MotionControlWidget";
	public static final int INVALID_POINTER_ID = -1;
	private boolean tDebug;
	private boolean tIsControlling = false;

	private Paint dbgPaint1;
	private Paint dbgLine;

	private Paint bgPaint;
	private Paint handleStick;
	private Paint handlePaint;

	private int innerPadding;
	private int bgRadius;
	private int stickRadius;
	private int handleRadius;
	private int movementRadius;
	private int handleInnerBoundaries;

	// # of pixels movement required between reporting to the listener
	private float moveResolution;

	// Max range of movement in user coordinate system
	public final static int CONSTRAIN_BOX = 0;
	public final static int CONSTRAIN_CIRCLE = 1;
	private int movementConstraint;

	public final static int COORDINATE_CARTESIAN = 0; // Regular cartesian
														// coordinates
	public final static int COORDINATE_DIFFERENTIAL = 1; // Uses polar rotation
															// of 45 degrees to
															// calc differential
															// drive paramaters
	private int userCoordinateSystem;

	// Records touch pressure for click handling
	private float clickThreshold;

	// Last touch point in view coordinates
	private float touchX, touchY;

	// Handle center in view coordinates
	private float handleX, handleY;

	// Center of the view in view coordinates
	private int cX, cY;

	// Size of the view in view coordinates
	private int dimX, dimY;

	// User coordinates of last touch point
//	private float userX, userY;

	float sensorX;
	float sensorY;
	float sensorZ;

	private UiInputSourceChannel tChannelH = new UiInputSourceChannel();
	private UiInputSourceChannel tChannelV = new UiInputSourceChannel();

	private boolean tCustomizeModusActive = false;
	protected RCWidgetConfig tWidgetConfig;

	
	private ModusChangeListener tModusChangeListener = new ModusChangeListener() {
		public void customizeModeDeactivated() {}
		public void customizeModeActivated() {}};

	public MotionControlWidget(Context context, ViewElementConfig viewElementConfig) {
		this(context, new RCWidgetConfig(viewElementConfig));
	}
	
	public MotionControlWidget(Context context, RCWidgetConfig rcWidgetConfig) {
		super(context);
		tWidgetConfig = rcWidgetConfig;
		setLayoutParams(rcWidgetConfig.viewElementConfig.getLayoutParams());
		setAlpha(tWidgetConfig.viewElementConfig.getAlpha());
		initJoystickView(context);
	}

	public MotionControlWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		initJoystickView(context);
	}

	public MotionControlWidget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initJoystickView(context);
	}

	private void initJoystickView(Context context) {
		setFocusable(true);
		dbgPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
		dbgPaint1.setColor(Color.RED);
		dbgPaint1.setStrokeWidth(1);
		dbgPaint1.setStyle(Paint.Style.STROKE);

		dbgLine = new Paint(Paint.ANTI_ALIAS_FLAG);
		dbgLine.setColor(Color.GREEN);
		dbgLine.setStrokeWidth(1);
		dbgLine.setStyle(Paint.Style.FILL_AND_STROKE);
		dbgLine.setTextSize(getPixels(15));

		bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		bgPaint.setColor(Color.GRAY);
		bgPaint.setStrokeWidth(1);
		bgPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		
		handleStick = new Paint(Paint.ANTI_ALIAS_FLAG);
		handleStick.setStrokeWidth(20);
		handleStick.setStyle(Paint.Style.FILL_AND_STROKE);

		handlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		handlePaint.setColor(Color.parseColor("#4F4F4F"));
		handlePaint.setStrokeWidth(1);
		handlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
		
		innerPadding = 0;

		setMoveResolution(1.0f);
		setClickThreshold(0.4f);
		setUserCoordinateSystem(COORDINATE_CARTESIAN);
		setMovementConstraint(CONSTRAIN_BOX);
		initDefaultProtocolConfig();
		tSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		tSensor = tSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
		tSensorEventListener = new SensorEventListener() {
			
			@Override
			public void onSensorChanged(SensorEvent event) {

				if(event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
					return;
				
				if(Sensor.TYPE_GRAVITY == event.sensor.getType() )
				{
					sensorX = event.values[1];
					sensorY = event.values[0];
					sensorZ = event.values[2];
					touchX = 150 * sensorX;
					touchY = 150 * sensorY;
					tChannelH.setWidgetPosition((int)touchX);
					tChannelV.setWidgetPosition((int)touchY);
					postInvalidate();
				}
				
			}
			
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// nothing to do here
			}
		};
		tSensorManager.registerListener(tSensorEventListener, tSensor, SensorManager.SENSOR_DELAY_FASTEST);
	}
	
	int getPixels(float size) {
	    DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
	    return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, size, metrics);
	}

	public void setUserCoordinateSystem(int userCoordinateSystem) {
		if (userCoordinateSystem < COORDINATE_CARTESIAN
				|| movementConstraint > COORDINATE_DIFFERENTIAL)
			Log.e(MotionControlWidget.class.getSimpleName(), "invalid value for userCoordinateSystem");
		else
			this.userCoordinateSystem = userCoordinateSystem;
	}

	public int getUserCoordinateSystem() {
		return userCoordinateSystem;
	}

	public void setMovementConstraint(int movementConstraint) {
		if (movementConstraint < CONSTRAIN_BOX
				|| movementConstraint > CONSTRAIN_CIRCLE)
			Log.e(MotionControlWidget.class.getSimpleName(), "invalid value for movementConstraint");
		else
			this.movementConstraint = movementConstraint;
	}

	public int getMovementConstraint() {
		return movementConstraint;
	}

	/**
	 * Set the pressure sensitivity for registering a click
	 * 
	 * @param clickThreshold
	 *            threshold 0...1.0f inclusive. 0 will cause clicks to never be
	 *            reported, 1.0 is a very hard click
	 */
	public void setClickThreshold(float clickThreshold) {
		if (clickThreshold < 0 || clickThreshold > 1.0f)
			Log.e(MotionControlWidget.class.getSimpleName(), "clickThreshold must range from 0...1.0f inclusive");
		else
			this.clickThreshold = clickThreshold;
	}

	public float getClickThreshold() {
		return clickThreshold;
	}

	public void setMoveResolution(float moveResolution) {
		this.moveResolution = moveResolution;
	}

	public float getMoveResolution() {
		return moveResolution;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// Here we make sure that we have a perfect circle
		int measuredWidth = measure(widthMeasureSpec);
		int measuredHeight = measure(heightMeasureSpec);
		setMeasuredDimension(measuredWidth, measuredHeight);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		calculateDimensions();
	}

	private void calculateDimensions() {
		int d = Math.min(getLayoutParams().width, getLayoutParams().height);
		dimX = d;
		dimY = d;

		cX = d / 2;
		cY = d / 2;

		bgRadius = dimX / 2-innerPadding;
		stickRadius = (int) (bgRadius-bgRadius*0.1);
		handleRadius = (int) (d * 0.15);
		handleStick.setStrokeWidth((float) (handleRadius * 0.75));
		handleInnerBoundaries = handleRadius;
		movementRadius = Math.min(cX, cY) - handleInnerBoundaries;
		tChannelH.setWidgetRange(-movementRadius, movementRadius);
		tChannelV.setWidgetRange(-movementRadius, movementRadius);
		touchX = tChannelV.setWidgetToDefault();
		touchY = tChannelH.setWidgetToDefault();
	}

	private int measure(int measureSpec) {
		int result = 0;
		// Decode the measurement specifications.
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		if (specMode == MeasureSpec.UNSPECIFIED) {
			// Return a default size of 200 if no bounds are specified.
			result = 200;
		} else {
			// As you want to fill the available space
			// always return the full available bounds.
			result = specSize;
		}
		return result;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.save();
		// Draw the background
		canvas.drawCircle(cX, cY, stickRadius, bgPaint);
		
		// Draw the handle
		handleX = touchX + cX;
		handleY = touchY + cY;
		
		handlePaint.setColor(tIsControlling ? Color.BLUE : Color.DKGRAY);
		canvas.drawCircle(handleX, handleY, handleRadius, handlePaint);

		String dbgString;
//		dbgString = String.format(Locale.getDefault(), "Sensor(%.2f:%.2f:%.2f]", sensorX,sensorY,sensorZ);
//		dbgLine.setColor(Color.GREEN);
//		canvas.drawText(dbgString,0,40, dbgLine);
		if (tDebug) {
			canvas.drawCircle(cX, cY, bgRadius, dbgPaint1);

			canvas.drawCircle(handleX, handleY, 3, dbgPaint1);

			if (movementConstraint == CONSTRAIN_CIRCLE) {
				canvas.drawCircle(cX, cY, this.movementRadius, dbgPaint1);
			} else {
				canvas.drawRect(cX - movementRadius, cY - movementRadius, cX
						+ movementRadius, cY + movementRadius, dbgPaint1);
			}

			// Origin to touch point
			canvas.drawLine(cX, cY, handleX, handleY, dbgLine);
			
			
			if (UiInputSourceChannel.CHANNEL_UNASSIGNED == tChannelV.getChannelAssignment() ){
				dbgString = String.format(Locale.getDefault(),"H:unassigned");
				dbgLine.setColor(Color.RED);
			}
			else {
				dbgString = String.format(Locale.getDefault(), "H[%d]:%d", tChannelV.getChannelAssignment(), tChannelV.getChannelValue());
				dbgLine.setColor(Color.GREEN);
			}
			canvas.drawText(dbgString,0, getHeight()/2, dbgLine);
			if (UiInputSourceChannel.CHANNEL_UNASSIGNED == tChannelH.getChannelAssignment() ){
				dbgString = String.format(Locale.getDefault(), "V:unassigned");
				dbgLine.setColor(Color.RED);
			}
			else{
				dbgString = String.format(Locale.getDefault(), "V[%d]:%d", tChannelH.getChannelAssignment(), tChannelH.getChannelValue());
				dbgLine.setColor(Color.GREEN);
			}
			canvas.drawText(dbgString,getWidth()/2, dbgLine.getTextSize(), dbgLine);
			dbgLine.setColor(Color.GREEN);
		}
		
		canvas.restore();
		if (tCustomizeModusActive) 
			DrawingTools.drawCustomizableForground(this, canvas);

	}


	public interface AnalogStickMovedListener {
		public void OnMoved(float pan, float tilt);

		public void OnReleased();

		public void OnReturnedToCenter();
	}

	public interface AnalogStickClickedListener {
		public void OnClicked();

		public void OnReleased();
	}
	
	private void initDefaultProtocolConfig(){
		if(tWidgetConfig.protocolMap==null){
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

			if (UiInputSourceChannel.CHANNEL_UNASSIGNED == tChannelV.getChannelAssignment() )
				tWidgetConfig.protocolMap.put(RCConstants.CHANNEL_ASSIGNMENT_V, "");
			else
				tWidgetConfig.protocolMap.put(RCConstants.CHANNEL_ASSIGNMENT_V, Integer.valueOf(tChannelV.getChannelAssignment()).toString());
			tWidgetConfig.protocolMap.put(RCConstants.INVERTED_V, Boolean.valueOf(tChannelV.getChannelInverted()).toString());
			tWidgetConfig.protocolMap.put(RCConstants.MAX_RANGE_V, Integer.valueOf(tChannelV.getChannelMaxRange()).toString());
			tWidgetConfig.protocolMap.put(RCConstants.MIN_RANGE_V, Integer.valueOf(tChannelV.getChannelMinRange()).toString());
			tWidgetConfig.protocolMap.put(RCConstants.DEFAULT_POSITION_V, Integer.valueOf(tChannelV.getChannelDefaultPosition()).toString());
			tWidgetConfig.protocolMap.put(RCConstants.TRIMM_V, Integer.valueOf(tChannelV.getChannelTrimm()).toString());

			tWidgetConfig.protocolMap.put(RCConstants.DEBUG, Boolean.valueOf(false).toString());
		}else{
			updateProtocolMap();
		}
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
			
			tChannelV.setChannelAssignment(getProtocolMapInt(RCConstants.CHANNEL_ASSIGNMENT_V));
			tChannelV.setChannelInverted(getProtocolMapBoolean(RCConstants.INVERTED_V));
			tChannelV.setChannelMaxRange(getProtocolMapInt(RCConstants.MAX_RANGE_V));
			tChannelV.setChannelMinRange(getProtocolMapInt(RCConstants.MIN_RANGE_V));
			tChannelV.setChannelDefaultPosition(getProtocolMapInt(RCConstants.DEFAULT_POSITION_V));
			tChannelV.setChannelTrimm(getProtocolMapInt(RCConstants.TRIMM_V));
			tDebug = getProtocolMapBoolean(RCConstants.DEBUG);
						
			tDebug = getProtocolMapBoolean(RCConstants.DEBUG);
			calculateDimensions();
			postInvalidate();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	@Override
	public void create(RCWidgetConfig rcWidgetConfig) {
		tWidgetConfig = rcWidgetConfig;
//		init(getContext(), null);
	}

	@Override
	public void create(ViewElementConfig vElementConfig) {
		tWidgetConfig = new RCWidgetConfig(vElementConfig);
//		init(getContext(), null);
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
	public ViewElementConfig getViewElementConfig() {
		tWidgetConfig.viewElementConfig.setLayoutParams((LayoutParams) getLayoutParams());
		tWidgetConfig.viewElementConfig.setAlpha(getAlpha());
		return tWidgetConfig.viewElementConfig;
	}

	@Override
	public void setCustomizeModusListener(OnTouchListener customizeModusListener){
		setOnTouchListener(customizeModusListener);
	}

	@Override
	public void setProtocolMap(Map<String, String> protocolMap) {
		tWidgetConfig.protocolMap = protocolMap;
		updateProtocolMap();
	}

	@Override
	public List<UiInputSourceChannel> getUiInputSourceList() {
		List<UiInputSourceChannel> list = new ArrayList<UiInputSourceChannel>();
		list.add(tChannelV);
		list.add(tChannelH);
	    return list;
	}
	
	public static ViewElementConfig getDefaultViewElementConfig() {
		return DefaultViewElementConfigRepo.getDefaultConfig(MotionControlWidget.class);
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
}
	
