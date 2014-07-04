package ch.hsr.rocketcolibri.view.widget;

import java.util.HashMap;
import java.util.Map;

import ch.hsr.rocketcolibri.RCConstants;
import ch.hsr.rocketcolibri.ui_data.input.Channel;
import ch.hsr.rocketcolibri.ui_data.output.UiOutputDataType;
import ch.hsr.rocketcolibri.util.DrawingTools;
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.custimizable.ICustomizableView;
import ch.hsr.rocketcolibri.view.custimizable.ModusChangeListener;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class AnalogStickWidget extends View implements ICustomizableView, IRCWidget {
	public static final int INVALID_POINTER_ID = -1;
	private boolean tDebug;

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

	private AnalogStickMovedListener moveListener;
	private AnalogStickClickedListener clickListener;

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
	private float touchPressure;
	private boolean clicked;
	private float clickThreshold;

	// Last touch point in view coordinates
	private int pointerId = INVALID_POINTER_ID;
	private float touchX, touchY;

	// Last reported position in view coordinates (allows different reporting
	// sensitivities)
	private float reportX, reportY;

	// Handle center in view coordinates
	private float handleX, handleY;

	// Center of the view in view coordinates
	private int cX, cY;

	// Size of the view in view coordinates
	private int dimX, dimY;

	// Cartesian coordinates of last touch point - joystick center is (0,0)
	private float cartX, cartY;

	// Polar coordinates of the touch point from joystick center
	private double radial;
	private double angle;

	// User coordinates of last touch point
	private float userX, userY;

	// Offset co-ordinates (used when touch events are received from parent's
	// coordinate origin)
	private int offsetX;
	private int offsetY;
	
	private Channel tChannelV = new Channel();
	private Channel tChannelH = new Channel();
	private OnChannelChangeListener tControlModusListener;
	private boolean tCustomizeModusActive = false;
	protected RCWidgetConfig tWidgetConfig;
	protected OnTouchListener tCustomizeModusListener;
	private ControlModeListener tInternalControlListener = new ControlModeListener();
	private ModusChangeListener tModusChangeListener = new ModusChangeListener() {
		public void customizeModeDeactivated() {}
		public void customizeModeActivated() {}};

	public AnalogStickWidget(Context context, ViewElementConfig viewElementConfig) {
		this(context, new RCWidgetConfig(viewElementConfig));
	}
	
	public AnalogStickWidget(Context context, RCWidgetConfig rcWidgetConfig) {
		super(context);
		tWidgetConfig = rcWidgetConfig;
		setLayoutParams(rcWidgetConfig.viewElementConfig.getLayoutParams());
		setAlpha(tWidgetConfig.viewElementConfig.getAlpha());
		initJoystickView();
	}

	public AnalogStickWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		initJoystickView();
	}

	public AnalogStickWidget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initJoystickView();
	}

	private void initJoystickView() {
		setFocusable(true);

		dbgPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
		dbgPaint1.setColor(Color.RED);
		dbgPaint1.setStrokeWidth(1);
		dbgPaint1.setStyle(Paint.Style.STROKE);

		dbgLine = new Paint(Paint.ANTI_ALIAS_FLAG);
		dbgLine.setColor(Color.GREEN);
		dbgLine.setStrokeWidth(1);
		dbgLine.setStyle(Paint.Style.FILL_AND_STROKE);
		dbgLine.setTextSize(40);

		bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		bgPaint.setColor(Color.GRAY);
		bgPaint.setStrokeWidth(1);
		bgPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		
		handleStick = new Paint(Paint.ANTI_ALIAS_FLAG);
		handleStick.setColor(Color.DKGRAY);
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
		setMovementConstraint(CONSTRAIN_CIRCLE);
		initDefaultProtocolConfig();
		initListener();
	}

	public void setUserCoordinateSystem(int userCoordinateSystem) {
		if (userCoordinateSystem < COORDINATE_CARTESIAN
				|| movementConstraint > COORDINATE_DIFFERENTIAL)
			Log.e(AnalogStickWidget.class.getSimpleName(), "invalid value for userCoordinateSystem");
		else
			this.userCoordinateSystem = userCoordinateSystem;
	}

	public int getUserCoordinateSystem() {
		return userCoordinateSystem;
	}

	public void setMovementConstraint(int movementConstraint) {
		if (movementConstraint < CONSTRAIN_BOX
				|| movementConstraint > CONSTRAIN_CIRCLE)
			Log.e(AnalogStickWidget.class.getSimpleName(), "invalid value for movementConstraint");
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
			Log.e(AnalogStickWidget.class.getSimpleName(), "clickThreshold must range from 0...1.0f inclusive");
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

	public void setOnJostickMovedListener(AnalogStickMovedListener listener) {
		this.moveListener = listener;
	}

	public void setOnJostickClickedListener(AnalogStickClickedListener listener) {
		this.clickListener = listener;
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

		int d = Math.min(getLayoutParams().width, getLayoutParams().height);
		dimX = d;
		dimY = d;

		cX = d / 2;
		cY = d / 2;

		bgRadius = dimX / 2-innerPadding;
		stickRadius = (int) (bgRadius-bgRadius*0.2);
		handleRadius = (int) (d * 0.2);
		handleStick.setStrokeWidth((float) (handleRadius * 0.75));
		handleInnerBoundaries = handleRadius;
		movementRadius = Math.min(cX, cY) - handleInnerBoundaries;
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
		canvas.drawCircle(cX, cY, (handleStick.getStrokeWidth()/10), handleStick);
		canvas.drawLine(cX, cY, handleX, handleY, handleStick);
		canvas.drawCircle(handleX, handleY, handleRadius, handlePaint);

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

			canvas.drawText(String.format("%.3f, %.3f", userX, userY),
					0, 40, dbgLine);
			canvas.drawText(String.format("%.0f, %.1f", radial,
									angle * 57.2957795) + (char) 0x00B0,
					0, getHeight()-15, dbgLine);
		}

		// Log.d(TAG, String.format("touch(%f,%f)", touchX, touchY));
		// Log.d(TAG, String.format("onDraw(%.1f,%.1f)\n\n", handleX, handleY));
		canvas.restore();
		if (tCustomizeModusActive) 
			DrawingTools.drawCustomizableForground(this, canvas);

	}

	// Constrain touch within a box
	private void constrainBox() {
		touchX = Math.max(Math.min(touchX, movementRadius), -movementRadius);
		touchY = Math.max(Math.min(touchY, movementRadius), -movementRadius);
	}

	// Constrain touch within a circle
	private void constrainCircle() {
		float diffX = touchX;
		float diffY = touchY;
		double radial = Math.sqrt((diffX * diffX) + (diffY * diffY));
		if (radial > movementRadius) {
			touchX = (float) ((diffX / radial) * movementRadius);
			touchY = (float) ((diffY / radial) * movementRadius);
		}
	}

	public void setPointerId(int id) {
		this.pointerId = id;
	}

	public int getPointerId() {
		return pointerId;
	}

	private class ControlModeListener implements OnTouchListener{
		@Override
		public boolean onTouch(View v, MotionEvent ev) {

		final int action = ev.getAction();
		switch (action & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_MOVE: {
				return processMoveEvent(ev);
			}
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP: {
				if (pointerId != INVALID_POINTER_ID) {
					// Log.d(AnalogStickWidget.class.getSimpleName(), "ACTION_UP");
					returnHandleToCenter();
					setPointerId(INVALID_POINTER_ID);
				}
				break;
			}
			case MotionEvent.ACTION_POINTER_UP: {
				if (pointerId != INVALID_POINTER_ID) {
					final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
					final int pId = ev.getPointerId(pointerIndex);
					if (pId == pointerId) {
						// Log.d(AnalogStickWidget.class.getSimpleName(), "ACTION_POINTER_UP: " + pointerId);
						returnHandleToCenter();
						setPointerId(INVALID_POINTER_ID);
						return true;
					}
				}
				break;
			}
			case MotionEvent.ACTION_DOWN: {
				if (pointerId == INVALID_POINTER_ID) {
					int x = (int) ev.getX();
					if (x >= offsetX && x < offsetX + dimX) {
						setPointerId(ev.getPointerId(0));
						// Log.d(AnalogStickWidget.class.getSimpleName(), "ACTION_DOWN: " + getPointerId());
						return true;
					}
				}
				break;
			}
			case MotionEvent.ACTION_POINTER_DOWN: {
				if (pointerId == INVALID_POINTER_ID) {
					final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
					final int pointerId = ev.getPointerId(pointerIndex);
					int x = (int) ev.getX(pointerId);
					if (x >= offsetX && x < offsetX + dimX) {
						// Log.d(TAG, "ACTION_POINTER_DOWN: " + pointerId);
						setPointerId(pointerId);
						return true;
					}
				}
				break;
			}
		}
		return false;
		}
	}

	private boolean processMoveEvent(MotionEvent ev) {
		if (pointerId != INVALID_POINTER_ID) {
			final int pointerIndex = ev.findPointerIndex(pointerId);

			// Translate touch position to center of view
			float x = ev.getX(pointerIndex);
			touchX = x - cX - offsetX;
			float y = ev.getY(pointerIndex);
			touchY = y - cY - offsetY;

//			 Log.d(AnalogStickWidget.class.getSimpleName(),
//			 String.format("ACTION_MOVE: (%03.0f, %03.0f) => (%03.0f, %03.0f)",
//			 x, y, touchX, touchY));

			reportOnMoved();
			invalidate();

			touchPressure = ev.getPressure(pointerIndex);
			reportOnPressure();

			return true;
		}
		return false;
	}

	private void reportOnMoved() {
		if (movementConstraint == CONSTRAIN_CIRCLE)
			constrainCircle();
		else
			constrainBox();

		calcUserCoordinates();

		if (moveListener != null) {
			boolean rx = Math.abs(touchX - reportX) >= moveResolution;
			boolean ry = Math.abs(touchY - reportY) >= moveResolution;
			if (rx || ry) {
				this.reportX = touchX;
				this.reportY = touchY;

				 Log.d(AnalogStickWidget.class.getSimpleName(), String.format("moveListener.OnMoved(%d,%d)",
				 (int)userX, (int)userY));
				moveListener.OnMoved(userX, userY);
			}
		}
	}

	private void calcUserCoordinates() {
		// First convert to cartesian coordinates
		cartX = (touchX / movementRadius * tChannelH.getMaxRange());
		cartY = (touchY / movementRadius * tChannelV.getMaxRange());

		radial = Math.sqrt((cartX * cartX) + (cartY * cartY));
		angle = Math.atan2(cartY, cartX);

		// Invert Y axis if requested
		if (!tChannelV.getInverted())
			cartY *= -1;

		if (userCoordinateSystem == COORDINATE_CARTESIAN) {
//			userX = cartX;
//			userY = cartY;
			if (cartX >= tChannelH.getMinRange() && cartX <= tChannelH.getMaxRange())
				userX = cartX;
			if (cartY >= tChannelV.getMinRange() && cartY <= tChannelV.getMaxRange())
				userY = cartY;
		} else if (userCoordinateSystem == COORDINATE_DIFFERENTIAL) {
			userX = (cartY + cartX / 4);
			userY = (cartY - cartX / 4);

			if (userX < tChannelH.getMinRange())
				userX = tChannelH.getMinRange();
			if (userX > tChannelH.getMaxRange())
				userX = tChannelH.getMaxRange();

			if (userY < tChannelV.getMinRange())
				userY = tChannelV.getMinRange();
			if (userY > tChannelV.getMaxRange())
				userY = tChannelV.getMaxRange();
		}
		if(tControlModusListener!=null){
			tControlModusListener.onChannelChange(tChannelH.getAssignment(), (int) userX);
			tControlModusListener.onChannelChange(tChannelV.getAssignment(), (int) userY);
		}

	}

	// Simple pressure click
	private void reportOnPressure() {
		// Log.d(TAG, String.format("touchPressure=%.2f", this.touchPressure));
		if (clickListener != null) {
			if (clicked && touchPressure < clickThreshold) {
				clickListener.OnReleased();
				this.clicked = false;
				// Log.d(TAG, "reset click");
				invalidate();
			} else if (!clicked && touchPressure >= clickThreshold) {
				clicked = true;
				clickListener.OnClicked();
				// Log.d(TAG, "click");
				invalidate();
				performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			}
		}
	}

	private void returnHandleToCenter() {
		if (!tChannelH.getSticky()) {
			final int numberOfFrames = 5;
			final double intervalsX = (0 - touchX) / numberOfFrames;
			final double intervalsY = (0 - touchY) / numberOfFrames;
			for (int i = 0; i < numberOfFrames; i++) {
				final int j = i;
				postDelayed(new Runnable() {public void run() {
					touchX += intervalsX;
					touchY += intervalsY;
					reportOnMoved();
					invalidate();
					if (moveListener != null && j == numberOfFrames - 1) {
						moveListener.OnReturnedToCenter();
					}
				}}, i * 40);
			}

			if (moveListener != null) {
				moveListener.OnReleased();
			}
		}
	}

	public void setTouchOffset(int x, int y) {
		offsetX = x;
		offsetY = y;
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
			tWidgetConfig.protocolMap.put(RCConstants.CHANNEL_ASSIGNMENT_H, "0");
			tWidgetConfig.protocolMap.put(RCConstants.MAX_RANGE_H, "1000");
			tWidgetConfig.protocolMap.put(RCConstants.MIN_RANGE_H, "-1000");
			tWidgetConfig.protocolMap.put(RCConstants.TRIMM_H, "");
			
			tWidgetConfig.protocolMap.put(RCConstants.CHANNEL_ASSIGNMENT_V, "1");
			tWidgetConfig.protocolMap.put(RCConstants.INVERTED_V, "false");
			tWidgetConfig.protocolMap.put(RCConstants.MAX_RANGE_V, "1000");
			tWidgetConfig.protocolMap.put(RCConstants.MIN_RANGE_V, "-1000");
			tWidgetConfig.protocolMap.put(RCConstants.TRIMM_V, "");
			tWidgetConfig.protocolMap.put(RCConstants.STICKY, "false");
			tWidgetConfig.protocolMap.put(RCConstants.DEBUG, "false");
		}else{
			updateProtocolMap();
		}
	}
	
	@Override
	public void updateProtocolMap() {
		try{
			tChannelH.setAssignment(getProtocolMapInt(RCConstants.CHANNEL_ASSIGNMENT_H));
			tChannelH.setMaxRange(getProtocolMapInt(RCConstants.MAX_RANGE_H));
			tChannelH.setMinRange(getProtocolMapInt(RCConstants.MIN_RANGE_H));
			tChannelH.setTrimm(getProtocolMapInt(RCConstants.TRIMM_H));
			tChannelH.setSticky(getProtocolMapBoolean(RCConstants.STICKY));
			
			tChannelV.setAssignment(getProtocolMapInt(RCConstants.CHANNEL_ASSIGNMENT_V));
			tChannelV.setInverted(getProtocolMapBoolean(RCConstants.INVERTED_V));
			tChannelV.setMaxRange(getProtocolMapInt(RCConstants.MAX_RANGE_V));
			tChannelV.setMinRange(getProtocolMapInt(RCConstants.MIN_RANGE_V));
			tChannelV.setTrimm(getProtocolMapInt(RCConstants.TRIMM_V));
			tChannelV.setSticky(getProtocolMapBoolean(RCConstants.STICKY));
			tDebug = getProtocolMapBoolean(RCConstants.DEBUG);
			returnHandleToCenter();
		}catch(Exception e){
			e.printStackTrace();
		}
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
	
	private boolean areChannelsValid(){
		return tChannelH.getAssignment()>-1 || tChannelV.getAssignment()>-1;
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
	public void setControlModusListener(OnChannelChangeListener channelListener) {
		tControlModusListener = channelListener;
		if(areChannelsValid())
			setOnTouchListener(tInternalControlListener); 
	}
	
	@Override
	public void setCustomizeModusListener(OnTouchListener customizeModusListener){
		tCustomizeModusListener = customizeModusListener;
		setOnTouchListener(tCustomizeModusListener);
	}

	@Override
	public void setProtocolMap(Map<String, String> protocolMap) {
		tWidgetConfig.protocolMap = protocolMap;
		updateProtocolMap();
	}

	@Override
	public void onNotifyUiOutputSink(Object data) {
		// TODO Auto-generated method stub
	}

	@Override
	public UiOutputDataType getType(){
		return UiOutputDataType.ConnectionState;
	}
	
	public static ViewElementConfig getDefaultViewElementConfig() {
		return DefaultViewElementConfigRepo.getDefaultConfig(AnalogStickWidget.class);
	}
}