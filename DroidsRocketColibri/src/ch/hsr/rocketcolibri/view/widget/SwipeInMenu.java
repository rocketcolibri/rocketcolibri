/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.view.widget;

import ch.hsr.rocketcolibri.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;

/**
 * SwipeInMenu is a copy of SlidingTray (http://aniqroid.sileria.com/doc/api/com/sileria/android/view/SlidingTray.html)
 * which is a modification of {@link android.widget.SlidingDrawer} with two major changes:
 * <ul>
 *     <li>It lets you create the drawer programmatically instead of just via xml</li>
 *     <li>Secondly you can {@link #setOrientation(Orientation)} to any 4 corners of the parent</li>
 * </ul>
 *
 * A SlidingDrawer hides content out of the screen and allows the user to drag a handle
 * to bring the content on screen. SlidingDrawer can be used vertically or horizontally.
 * <p/>
 * A special widget composed of two children views: the handle, that the users drags,
 * and the content, attached to the handle and dragged with it.
 * <p/>
 * SlidingDrawer should be used as an overlay inside layouts. This means SlidingDrawer
 * should only be used inside of a FrameLayout or a RelativeLayout for instance. The
 * size of the SlidingDrawer defines how much space the content will occupy once slid
 * out so SlidingDrawer should usually use match_parent for both its dimensions.
 * <p/>
 * <strong>Coding Example:</strong>
 * <blockquote><pre>
 * // handle
 * Button handle = new Button( this );
 * handle.setText( "Push Me" );
 *
 * // content
 * TextView content = T.newText( "Sample Text." );
 *
 * // drawer
 * SwipeInMenu drawer = new SwipeInMenu( this, handle, content, SwipeInMenu.Orientation.TOP );
 * </pre></blockquote>
 *
 *
 * <strong>XML Example:</strong>
 *
 * <blockquote><pre class="prettyprint">
 * &lt;com.your.app.SwipeInMenu
 *     xmlns:custom="http://schemas.android.com/apk/res-auto/com.your.app"
 *     android:id="@+id/drawer"
 *     android:layout_width="match_parent"
 *     android:layout_height="match_parent"
 *     custom:orientation="top"
 *     custom:handle="@+id/handle_id"
 *     custom:content="@+id/content"&gt;
 *
 *     &lt;ImageView
 *         android:id="@+id/handle_id"
 *         android:layout_width="88dip"
 *         android:layout_height="44dip" /&gt;
 *
 *     &lt;GridView
 *         android:id="@+id/content"
 *         android:layout_width="match_parent"
 *         android:layout_height="match_parent" /&gt;
 *
 * &lt;/com.your.app.SwipeInMenu&gt;
 * </pre></blockquote>
 *
 * @author Ahmed Shakil
 * @author PatrickF
 * @author Artan Veliju
 */
public class SwipeInMenu extends ViewGroup {

	private static final int   TAP_THRESHOLD          = 6;
	private static final float MAXIMUM_TAP_VELOCITY   = 100.0f;
	private static final float MAXIMUM_MINOR_VELOCITY = 150.0f;
	private static final float MAXIMUM_MAJOR_VELOCITY = 200.0f;
	private static final float MAXIMUM_ACCELERATION   = 2000.0f;
	private static final int VELOCITY_UNITS           = 1000;
	private static final int MSG_ANIMATE              = 1000;
	private static final int ANIMATION_FRAME_DURATION = 1000 / 60;
	
	private static final int EXPANDED_FULL_OPEN       = -10001;
	private static final int COLLAPSED_FULL_CLOSED    = -10002;

	private final float tDensityScale;
	private final int tHeadId;
	private final int tContentId;

	private View tHead;
	private ViewGroup tContent;
	private float tHeadDefaultAlpha;

	private final Rect tHeadDragArea = new Rect();
	private final Rect tHeadRegion = new Rect();
	private final Rect tContentDragArea = new Rect();
	private Rect closedRect = new Rect();
	private boolean mTracking;
	private boolean mLocked;

	private VelocityTracker mVelocityTracker;

	private OrientationSpecific tOSpecific;
	private Orientation tOrientation;
	private Side mHandlePos;
	private boolean tVertical;
	private boolean mInvert;

	private boolean tIsOpen;
	private int mBottomOffset;
	private int mTopOffset;
	private int mHandlePad;
	
	private int tScreenHeight;
	private int tScreenWidth;

	private OnDrawerOpenListener mOnDrawerOpenListener;
	private OnDrawerCloseListener mOnDrawerCloseListener;
	private OnDrawerScrollListener mOnDrawerScrollListener;

	private final Handler mHandler = new SlidingHandler();
	private float mAnimatedAcceleration;
	private float mAnimatedVelocity;
	private float mAnimationPosition;
	private long mAnimationLastTime;
	private long mCurrentAnimationTime;
	private int mTouchDelta;
	private boolean mAnimating;
	private boolean mAllowSingleTap = true;
	private boolean mAnimateOnClick = true;

	private int mTapThreshold;
	private int mMaximumTapVelocity;
	private int mMaximumMinorVelocity;
	private int mMaximumMajorVelocity;
	private int mMaximumAcceleration;
	private int mVelocityUnits;
	private Object animLock = new Object();

	/**
	 * Construct a <code>SwipeInMenu</code> object programmatically with the specified
	 * <code>handle</code>, <code>content</code> and <code>orientation</code>.
	 *
	 * @param context Activity context
	 * @param handle  Cannot be <code>null</code>
	 * @param content Cannot be <code>null</code>
	 * @param orientation TOP, LEFT, BOTTOM or RIGHT.
	 */
	public SwipeInMenu(Context context, View handle, View content, Orientation orientation) {
		super( context );
		tDensityScale = context.getResources().getDisplayMetrics().xdpi;
		// handle
		if (handle == null)
			throw new NullPointerException("Handle cannot be null.");
		addView( tHead = handle );
		tHead.setOnClickListener(new DrawerToggler());

		// content
		if (content == null)
			throw new IllegalArgumentException("Content cannot be null.");
		addView( tContent = (ViewGroup) content );
		tContent.setVisibility(View.GONE);

		tHeadId = tContentId = 0;

		setOrientation(orientation);

		setVelocityAndThreshold();
	}

	/**
	 * Creates a new SlidingDrawer from a specified set of attributes defined in XML.
	 *
	 * @param context The application's environment.
	 * @param attrs The attributes defined in XML.
	 */
	public SwipeInMenu(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * Creates a new SlidingDrawer from a specified set of attributes defined in XML.
	 *
	 * @param context The application's environment.
	 * @param attrs The attributes defined in XML.
	 * @param defStyle The style to apply to this widget.
	 */
	public SwipeInMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		tDensityScale = context.getResources().getDisplayMetrics().xdpi;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwipeInMenu, defStyle, 0);

		int orientation = a.getInteger(R.styleable.SwipeInMenu_orientation,Orientation.TOP.ordinal());
		setOrientation(Orientation.getByValue(orientation));
		mHandlePos = Side.getByValue(orientation);
		mBottomOffset = (int) a.getDimension(R.styleable.SwipeInMenu_bottomOffset, 0.0f);
		mTopOffset = (int) a.getDimension(R.styleable.SwipeInMenu_topOffset, 0.0f);
		mAllowSingleTap = a.getBoolean(R.styleable.SwipeInMenu_allowSingleTap, true);
		mAnimateOnClick = a.getBoolean(R.styleable.SwipeInMenu_animateOnClick, true);


		int handleId = a.getResourceId(R.styleable.SwipeInMenu_handle, 0);
		if (handleId == 0) {
			throw new IllegalArgumentException("The handle attribute is required and must refer "
					+ "to a valid child.");
		}

        int contentId = a.getResourceId(R.styleable.SwipeInMenu_content, 0);
		if (contentId == 0) {
			throw new IllegalArgumentException("The content attribute is required and must refer "
					+ "to a valid child.");
		}

		if (handleId == contentId) {
			throw new IllegalArgumentException("The content and handle attributes must refer "
					+ "to different children.");
		}

		tHeadId = handleId;
		tContentId = contentId;

		setVelocityAndThreshold();

//        a.recycle();

		setAlwaysDrawnWithCacheEnabled(false);
		setOnClickListener(new DrawerToggler());
		setClickable(false);
	}
	
	private void setVelocityAndThreshold(){
		final float density = getResources().getDisplayMetrics().density;
		mTapThreshold = (int) (TAP_THRESHOLD * density + 0.5f);
		mMaximumTapVelocity = (int) (MAXIMUM_TAP_VELOCITY * density + 0.5f);
		mMaximumMinorVelocity = (int) (MAXIMUM_MINOR_VELOCITY * density + 0.5f);
		mMaximumMajorVelocity = (int) (MAXIMUM_MAJOR_VELOCITY * density + 0.5f);
		mMaximumAcceleration = (int) (MAXIMUM_ACCELERATION * density + 0.5f);
		mVelocityUnits = (int) (VELOCITY_UNITS * density + 0.5f);
	}

	/**
	 * Get the current orientation of this sliding tray.
	 */
	public Orientation getOrientation () {
		return tOrientation;
	}

	/**
	 * Lets you change the orientation of the sliding tray at runtime.
	 * <p/>
	 * Orientation must be from one of TOP, LEFT, BOTTOM, RIGHT.
	 *
	 * @param orientation orientation of the sliding tray.
	 */
	public void setOrientation (Orientation orientation) {
		tOrientation = orientation;
		tVertical = tOrientation == Orientation.BOTTOM || tOrientation == Orientation.TOP;
		mInvert = tOrientation == Orientation.LEFT || tOrientation == Orientation.TOP;
		switch(tOrientation){
		case BOTTOM: tOSpecific = new BottomOrientation();
			break;
		case LEFT: tOSpecific = new LeftOrientation();
			break;
		case RIGHT: tOSpecific = new RightOrientation();
			break;
		case TOP: tOSpecific = new TopOrientation();
			break;
		}
		requestLayout();
		invalidate();
	}

	/**
	 * Get the current positioning of this sliding tray handle.
	 */
	public Side getHandlePosition () {
		return mHandlePos;
	}

	/**
	 * Change the handle positioning of the sliding tray at runtime.
	 * <p/>
	 * HandlePos must be {@link Side#TOP}, {@link Side#CENTER} or {@link Side#BOTTOM} for horizontal orientation
	 * or must be {@link Side#LEFT}, {@link Side#CENTER} or {@link Side#RIGHT} for vertical orientation.
	 * <p/>
	 * Default is {@linkplain Side#CENTER}.
	 * @param side Handle Pos of the drawer handle.
	 */
	public void setHandlePosition (Side side) {
		mHandlePos = side;
		requestLayout();
		invalidate();
	}

	/**
	 * Add padding to drawer handle when handle is not centered.
	 * <p/>
	 * Note this padding is only effective when handle is not centered.
	 * @param padding padding in pixels.
	 */
	public void setHandlePadding (int padding) {
		mHandlePad = padding;
		requestLayout();
		invalidate();
	}


	@Override
	protected void onFinishInflate() {
		if (tHeadId > 0) {
			tHead = findViewById(tHeadId);
			if (tHead == null) {
				throw new IllegalArgumentException("The handle attribute must refer to an existing child.");
			}
			tHead.setOnClickListener(new DrawerToggler());
			tHeadDefaultAlpha = tHead.getAlpha();
			createHeadAnimations();
		}

		if (tContentId > 0) {
			tContent = (ViewGroup) findViewById(tContentId);
			if (tContent == null) {
				throw new IllegalArgumentException("The content attribute must refer to an existing child.");
			}
			tContent.setVisibility(View.GONE);
		}
		tContent.setClickable(true);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSpecSize =  MeasureSpec.getSize(widthMeasureSpec);

		int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSpecSize =  MeasureSpec.getSize(heightMeasureSpec);

		if (widthSpecMode == MeasureSpec.UNSPECIFIED || heightSpecMode == MeasureSpec.UNSPECIFIED) {
			throw new RuntimeException("SlidingDrawer cannot have UNSPECIFIED dimensions");
		}

		measureChild(tHead, widthMeasureSpec, heightMeasureSpec);

		if (tVertical) {
			int height = heightSpecSize - tHead.getMeasuredHeight() - mTopOffset;
			tContent.measure(MeasureSpec.makeMeasureSpec(widthSpecSize, MeasureSpec.EXACTLY),
					MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
		} else {
			int width = widthSpecSize - tHead.getMeasuredWidth() - mTopOffset;
			tContent.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
					MeasureSpec.makeMeasureSpec(heightSpecSize, MeasureSpec.EXACTLY));
		}

		setMeasuredDimension(widthSpecSize, heightSpecSize);
		tScreenHeight = ((View)getParent()).getHeight();
		tScreenWidth = ((View)getParent()).getWidth();
		tOSpecific.setClosedRect();

	}

	@Override
	protected void dispatchDraw (Canvas canvas) {
		final long drawingTime = getDrawingTime();
		drawChild(canvas, tHead, drawingTime);

		if (mTracking || mAnimating) {
			final Bitmap cache = tContent.getDrawingCache();
			if (cache != null) {
				// called when opening
				switch (tOrientation) {
					case TOP:    canvas.drawBitmap(cache, 0, tHead.getTop()-cache.getHeight(), null); break;
					case LEFT:   canvas.drawBitmap(cache, tHead.getLeft()-cache.getWidth(), 0, null); break;
					case BOTTOM: canvas.drawBitmap(cache, 0, tHead.getBottom(), null); break;
					case RIGHT:  canvas.drawBitmap(cache, tHead.getRight(), 0, null);  break;
				}
			}
			else {
				// called when closing
				canvas.save();
				switch (tOrientation) {
					case TOP:    canvas.translate(0, tHead.getTop() - tContent.getHeight() );  break;
					case LEFT:   canvas.translate( tHead.getLeft() - tContent.getWidth(), 0);   break;
					case BOTTOM: canvas.translate(0, tHead.getTop() - mTopOffset );    break;
					case RIGHT:  canvas.translate(tHead.getLeft() - mTopOffset, 0);    break;
				}
				drawChild(canvas, tContent, drawingTime);
				canvas.restore();
			}
		}else if (tIsOpen) {
			drawChild(canvas, tContent, drawingTime);
		}
	}

	@Override
	protected void onLayout (boolean changed, int l, int t, int r, int b) {
		if (mTracking) {
			return;
		}
		tOSpecific.layoutContent(r - l, b - t);
		tHead.layout(tOSpecific.headLeft, tOSpecific.headTop,
				tOSpecific.headLeft + tHead.getMeasuredWidth(),
				tOSpecific.headTop + tHead.getMeasuredHeight());
	}
	
	int iteX;
	int iteY;
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (mLocked) {
			return false;
		}
		iteX = (int) event.getX();
		iteY = (int) event.getY();
		if(!tIsOpen){
			tHeadDragArea.set(closedRect);
			if (!mTracking && (!tHeadDragArea.contains(iteX, iteY))){
				return false;
			}
		}else{
			tHead.getHitRect(tHeadDragArea);
			if (!mTracking && (!tHeadDragArea.contains(iteX, iteY))){
//				if(tIsOpen){
//					tContent.getHitRect(tHeadDragArea);
//					tContent.getDrawingRect(tContentDragArea);
//					if (!mTracking && (tContentDragArea.contains(iteX, iteY)
//							||!tHeadDragArea.contains(iteX, iteY))){
//						return false;
//					}
//				}else{
					return false;
//				}
			}
		}

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			mTracking = true;

			tHead.setPressed(true);
			// Must be called before prepareTracking()
			prepareContent();

			// Must be called after prepareContent()
			if (mOnDrawerScrollListener != null) {
				mOnDrawerScrollListener.onScrollStarted();
			}

			final int pt = getSide();
			mTouchDelta = (int)(event.getY() - pt);
			prepareTracking(pt);
			mVelocityTracker.addMovement(event);
		}

		return true;
	}
	
	private int getSide () {
		return tVertical ? tHead.getTop() : tHead.getLeft();
	}

	private int getOppositeSide () {
		int pt=0;
		switch (tOrientation) {
			case TOP:    pt = tHead.getBottom(); break;
			case LEFT:   pt = tHead.getRight(); break;
			case BOTTOM: pt = tHead.getTop(); break;
			case RIGHT:  pt = tHead.getLeft(); break;
		}
		return pt;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mLocked) {
			return true;
		}

		if (mTracking) {
			mVelocityTracker.addMovement(event);
			final int action = event.getAction();
			switch (action) {
				case MotionEvent.ACTION_MOVE:
					tOSpecific.moveHead((int)  (tVertical ? event.getY() : event.getX()) - mTouchDelta);
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL: {
					final VelocityTracker velocityTracker = mVelocityTracker;
					velocityTracker.computeCurrentVelocity(mVelocityUnits);

					float yVelocity = velocityTracker.getYVelocity();
					float xVelocity = velocityTracker.getXVelocity();
					boolean negative;

					if (tVertical) {
						negative = yVelocity < 0;
						if (xVelocity < 0) {
							xVelocity = -xVelocity;
						}
						if (xVelocity > mMaximumMinorVelocity) {
							xVelocity = mMaximumMinorVelocity;
						}
					} else {
						negative = xVelocity < 0;
						if (yVelocity < 0) {
							yVelocity = -yVelocity;
						}
						if (yVelocity > mMaximumMinorVelocity) {
							yVelocity = mMaximumMinorVelocity;
						}
					}

					float velocity = (float) Math.hypot(xVelocity, yVelocity);
					if (negative) {
						velocity = -velocity;
					}

					final int top = tHead.getTop();
					final int left = tHead.getLeft();

					if (Math.abs(velocity) < mMaximumTapVelocity) {

						if (tOSpecific.inThreshold( top, left )) {
							if (mAllowSingleTap) {
								playSoundEffect(SoundEffectConstants.CLICK);

//								if (tIsOpen) {
									//animateClose(vertical ? top : left);
									animateClose( getSide() );
//								} else {
//									animateOpen( getSide() );
									//animateOpen(vertical ? top : left);
//								}
							} else {
								performFling(tVertical ? top : left, velocity, false);
							}

						} else {
							performFling(tVertical ? top : left, velocity, false);
						}
					} else {
						performFling(tVertical ? top : left, velocity, false);
					}
				}
				break;
			}
		}
		return mTracking || mAnimating || super.onTouchEvent(event);
	}

	private void animateClose(int position) {
		prepareTracking(position);
		performFling(position, mMaximumAcceleration * (mInvert ? -1 : 1), true);
	}

	private void animateOpen(int position) {
		prepareTracking(position);
		performFling(position, mMaximumAcceleration * (mInvert ? 1 : -1), true);
	}

	private void performFling(int position, float velocity, boolean always) {
		mAnimationPosition = position;
		mAnimatedVelocity = velocity;
		if (tIsOpen) {
			if (mInvert) {
				if (always || (velocity < -mMaximumMajorVelocity ||
						(position < (tVertical ? getHeight() : getWidth()) / 2 &&
								velocity > -mMaximumMajorVelocity))) {
					// We are expanded and are now going to animate away.
					mAnimatedAcceleration = -mMaximumAcceleration;
					if (velocity > 0) {
						mAnimatedVelocity = 0;
					}
				}
				else {
					// We are expanded, but they didn't move sufficiently to cause
					// us to retract.  Animate back to the expanded position.
					mAnimatedAcceleration = mMaximumAcceleration;
					if (velocity < 0) {
						mAnimatedVelocity = 0;
					}
				}
			}
			else if (always || (velocity > mMaximumMajorVelocity ||
					(position > mTopOffset + (tVertical ? tHead.getHeight() : tHead.getWidth()) &&
							velocity > -mMaximumMajorVelocity))) {
				// We are expanded, but they didn't move sufficiently to cause
				// us to retract.  Animate back to the collapsed position.
				mAnimatedAcceleration = mMaximumAcceleration;
				if (velocity < 0) {
					mAnimatedVelocity = 0;
				}
			} else {
				// We are expanded and are now going to animate away.
				mAnimatedAcceleration = -mMaximumAcceleration;
				if (velocity > 0) {
					mAnimatedVelocity = 0;
				}
			}
		} else {
			//else if (!always && (velocity > mMaximumMajorVelocity ||
			//		(position > (mVertical ? getHeight() : getWidth()) / 2 &&
			//				velocity > -mMaximumMajorVelocity))) {
			if ((velocity > mMaximumMajorVelocity ||
					(position > (tVertical ? getHeight() : getWidth()) / 2 &&
							velocity > -mMaximumMajorVelocity))) {
				// We are collapsed, and they moved enough to allow us to expand.
				mAnimatedAcceleration = mMaximumAcceleration;
				if (velocity < 0) {
					mAnimatedVelocity = 0;
				}
			} else {
				// We are collapsed, but they didn't move sufficiently to cause
				// us to retract.  Animate back to the collapsed position.
				mAnimatedAcceleration = -mMaximumAcceleration;
				if (velocity > 0) {
					mAnimatedVelocity = 0;
				}
			}
		}

//		if (mInvert)
//			mAnimatedAcceleration *= -1;
		mAnimationLastTime = SystemClock.uptimeMillis();
		mCurrentAnimationTime = mAnimationLastTime + ANIMATION_FRAME_DURATION;
		mAnimating = true;
		mHandler.removeMessages(MSG_ANIMATE);
		mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG_ANIMATE), mCurrentAnimationTime);
		stopTracking();
	}

	private void prepareTracking(int position) {
		mTracking = true;
		mVelocityTracker = VelocityTracker.obtain();

		boolean opening = !tIsOpen;
		if (opening) {
			changeHeadOnOpen();
			mAnimatedAcceleration = mMaximumAcceleration;
			mAnimatedVelocity = mMaximumMajorVelocity;
			tOSpecific.prepareTracking();
			tOSpecific.moveHead((int) mAnimationPosition);
			mAnimating = true;
			mHandler.removeMessages(MSG_ANIMATE);
			mAnimationLastTime = SystemClock.uptimeMillis();
			mCurrentAnimationTime = mAnimationLastTime + ANIMATION_FRAME_DURATION;
		} else {
			if (mAnimating) {
				mAnimating = false;
				mHandler.removeMessages(MSG_ANIMATE);
			}
			tOSpecific.moveHead(position);
		}
	}

	private void regionUnionXAndInvalidate(int deltaX){
		tHead.offsetLeftAndRight(deltaX);

		tHead.getHitRect(tHeadDragArea);
		tHeadRegion.set(tHeadDragArea);

		tHeadRegion.union(tHeadDragArea.left - deltaX, tHeadDragArea.top, tHeadDragArea.right - deltaX, tHeadDragArea.bottom);
		tHeadRegion.union(tHeadDragArea.right - deltaX, 0, tHeadDragArea.right - deltaX + tContent.getWidth(), getHeight());

		invalidate(tHeadRegion);
	}
	
	private void regionUnionYAndInvalidate(int deltaY){
		tHead.offsetTopAndBottom(deltaY);
		tHead.getHitRect(tHeadDragArea);
		tHeadRegion.set(tHeadDragArea);

		tHeadRegion.union(tHeadDragArea.left, tHeadDragArea.top - deltaY, tHeadDragArea.right, tHeadDragArea.bottom - deltaY);
		tHeadRegion.union(0, tHeadDragArea.bottom - deltaY, getWidth(), tHeadDragArea.bottom - deltaY + tContent.getHeight());

		invalidate(tHeadRegion);
	}
	private boolean headInAnimEnded = true;
	private boolean headOutAnimEnded = true;
	private void changeHeadOnOpen() {
		synchronized(animLock){
			if(headInAnimEnded){
				headInAnimEnded = false;
				tHead.startAnimation(in);
			}
		}
	}

	private void changeHeadOnClose(){
		synchronized(animLock){
			if(headOutAnimEnded){
				headOutAnimEnded = false;
				tHead.startAnimation(out);
			}
		}
	}
	
	private void finishHeadOpenAnimation(){
//		tHead.clearAnimation();
	}
	
	private Animation in;
	private Animation out;
	private void createHeadAnimations(){
		in = new ScaleAnimation(-1f, -1f, 0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1.0f);
		in.setFillAfter(true);
		in.setDuration(100);
		out = new ScaleAnimation(-1f, -1f, 1.0f, 0.05f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1.0f);
		out.setDuration(100);
		out.setFillAfter(true);
		out.setAnimationListener(new AnimationListener() {
		    public void onAnimationEnd(Animation animation) {
				synchronized(animLock){
					headOutAnimEnded=true;
//					tHead.setAlpha(0f);
				}
		    }
			public void onAnimationRepeat(Animation animation) {}
			public void onAnimationStart(Animation animation) {
				synchronized(animLock){
					headInAnimEnded=true;
				}
			}
		});
		in.setAnimationListener(new AnimationListener() {
		    public void onAnimationEnd(Animation animation) {
				synchronized(animLock){
					headInAnimEnded=true;
				}
		    }
			public void onAnimationRepeat(Animation animation) {}
			public void onAnimationStart(Animation animation) {
				synchronized(animLock){
					headOutAnimEnded=true;
//					tHead.setAlpha(tHeadDefaultAlpha);
				}
			}
		});
		changeHeadOnClose();
	}

	private void prepareContent() {

		if (mAnimating) {
			return;
		}

		// Something changed in the content, we need to honor the ltHeadequest
		// before creating the cached bitmap
		if (tContent.isLayoutRequested()) {
			tOSpecific.measureContent();
		}

		// Try only once... we should really loop but it's not a big deal
		// if the draw was cancelled, it will only be temporary anyway
		tContent.getViewTreeObserver().dispatchOnPreDraw();
		tContent.buildDrawingCache();

		tContent.setVisibility(View.GONE);
	}

	private void stopTracking() {
		tHead.setPressed(false);
		mTracking = false;
		if (mOnDrawerScrollListener != null) {
			mOnDrawerScrollListener.onScrollEnded();
		}
		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}

	private void doAnimation() {
		if (mAnimating) {

			incrementAnimation();

			if (mInvert) {
				if (mAnimationPosition >= (tVertical ? getHeight() : getWidth()) - mTopOffset) {
					mAnimating = false;
					openDrawer();
				} else if (mAnimationPosition < -mBottomOffset) {
					mAnimating = false;
					closeDrawer();
				} else {
					tOSpecific.moveHead((int) mAnimationPosition);
					mCurrentAnimationTime += ANIMATION_FRAME_DURATION;
					mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG_ANIMATE), mCurrentAnimationTime);
				}
			}
			else {
				if (mAnimationPosition >= mBottomOffset + (tVertical ? getHeight() : getWidth()) - 1) {
					mAnimating = false;
					closeDrawer();
				} else if (mAnimationPosition < mTopOffset) {
					mAnimating = false;
					openDrawer();
				} else {
					tOSpecific.moveHead((int) mAnimationPosition);
					mCurrentAnimationTime += ANIMATION_FRAME_DURATION;
					mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG_ANIMATE), mCurrentAnimationTime);
				}
			}
		}
	}

	private void incrementAnimation() {
		long now = SystemClock.uptimeMillis();
		float t = (now - mAnimationLastTime) / 1000.0f;                   // ms -> s
		final float position = mAnimationPosition;
		final float v = mAnimatedVelocity;                                // px/s
		final float a = mAnimatedAcceleration;                            // px/s/s
		mAnimationPosition = position + (v * t) + (0.5f * a * t * t);     // px
		mAnimatedVelocity = v + (a * t);                                  // px/s
		mAnimationLastTime = now;                                         // ms
	}

	/**
	 * Toggles the drawer open and close. Takes effect immediately.
	 *
	 * @see #open()
	 * @see #close()
	 * @see #animateClose()
	 * @see #animateOpen()
	 * @see #animateToggle()
	 */
	public void toggle() {
		if (tIsOpen) {
			closeDrawer();
		} else {
			openDrawer();
		}
		invalidate();
		requestLayout();
	}

	/**
	 * Toggles the drawer open and close with an animation.
	 *
	 * @see #open()
	 * @see #close()
	 * @see #animateClose()
	 * @see #animateOpen()
	 * @see #toggle()
	 */
	public void animateToggle() {
		if (tIsOpen) {
			animateClose();
		} else {
			animateOpen();
		}
	}

	/**
	 * Opens the drawer immediately.
	 *
	 * @see #toggle()
	 * @see #close()
	 * @see #animateOpen()
	 */
	public void open() {
		openDrawer();
		invalidate();
		requestLayout();

		sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
	}

	/**
	 * Closes the drawer immediately.
	 *
	 * @see #toggle()
	 * @see #open()
	 * @see #animateClose()
	 */
	public void close() {
		closeDrawer();
		invalidate();
		requestLayout();
	}

	/**
	 * Closes the drawer with an animation.
	 *
	 * @see #close()
	 * @see #open()
	 * @see #animateOpen()
	 * @see #animateToggle()
	 * @see #toggle()
	 */
	public void animateClose() {
		prepareContent();
		
		if (mOnDrawerScrollListener != null) {
			mOnDrawerScrollListener.onScrollStarted();
		}
		animateClose( getSide() );

		if (mOnDrawerScrollListener != null) {
			mOnDrawerScrollListener.onScrollEnded();
		}
	}

	/**
	 * Opens the drawer with an animation.
	 *
	 * @see #close()
	 * @see #open()
	 * @see #animateClose()
	 * @see #animateToggle()
	 * @see #toggle()
	 */
	public void animateOpen() {
		prepareContent();

		if (mOnDrawerScrollListener != null) {
			mOnDrawerScrollListener.onScrollStarted();
		}
		animateOpen( getSide() );

		sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);

		if (mOnDrawerScrollListener != null) {
			mOnDrawerScrollListener.onScrollEnded();
		}
	}

	private void closeDrawer() {
		tOSpecific.moveHead( COLLAPSED_FULL_CLOSED );
		tContent.setVisibility( View.GONE );
		tContent.destroyDrawingCache();
		
		
		if (!tIsOpen) {
			return;
		}
		tHeadDragArea.set(closedRect);
		
		setClickable(false);
		tIsOpen = false;
		if (mOnDrawerCloseListener != null) {
			mOnDrawerCloseListener.onDrawerClosed();
		}
	}

	private void openDrawer() {
		tOSpecific.moveHead( EXPANDED_FULL_OPEN );
		tContent.setVisibility(View.VISIBLE);

		if (tIsOpen) {
			return;
		}
		
		setClickable(true);

		tIsOpen = true;

		if (mOnDrawerOpenListener != null) {
			mOnDrawerOpenListener.onDrawerOpened();
		}
	}

	/**
	 * Sets the listener that receives a notification when the drawer becomes open.
	 *
	 * @param onDrawerOpenListener The listener to be notified when the drawer is opened.
	 */
	public void setOnDrawerOpenListener(OnDrawerOpenListener onDrawerOpenListener) {
		mOnDrawerOpenListener = onDrawerOpenListener;
	}

	/**
	 * Sets the listener that receives a notification when the drawer becomes close.
	 *
	 * @param onDrawerCloseListener The listener to be notified when the drawer is closed.
	 */
	public void setOnDrawerCloseListener(OnDrawerCloseListener onDrawerCloseListener) {
		mOnDrawerCloseListener = onDrawerCloseListener;
	}

	/**
	 * Sets the listener that receives a notification when the drawer starts or ends
	 * a scroll. A fling is considered as a scroll. A fling will also trigger a
	 * drawer opened or drawer closed event.
	 *
	 * @param onDrawerScrollListener The listener to be notified when scrolling
	 *        starts or stops.
	 */
	public void setOnDrawerScrollListener (OnDrawerScrollListener onDrawerScrollListener) {
		mOnDrawerScrollListener = onDrawerScrollListener;
	}

	/**
	 * Returns thetHeade of the drawer.
	 *
	 * @return The View reprenseting the handle of the drawer, identified by
	 *         the "handle" id in XML.
	 */
	public View getHead() {
		return tHead;
	}

	/**
	 * Convenience method to get the size of the handle.
	 * May not return a valid size until the view is layed out.
	 *
	 * @return Return the height if the component is vertical
	 * otherwise returns the width for Horizontal orientation.
	 */
	public int getHandleSize () {
		return tVertical ? tHead.getHeight() : tHead.getWidth();
	}

	/**
	 * Returns the content of the drawer.
	 *
	 * @return The View reprenseting the content of the drawer, identified by
	 *         the "content" id in XML.
	 */
	public View getContent() {
		return tContent;
	}

	/**
	 * Unlocks the SlidingDrawer so that touch events are processed.
	 *
	 * @see #lock()
	 */
	public void unlock() {
		mLocked = false;
	}

	/**
	 * Locks the SlidingDrawer so that touch events are ignores.
	 *
	 * @see #unlock()
	 */
	public void lock() {
		mLocked = true;
	}

	/**
	 * Indicates whether the drawer is currently fully opened.
	 *
	 * @return True if the drawer is opened, false otherwise.
	 */
	public boolean isOpened() {
		return tIsOpen;
	}

	/**
	 * Indicates whether the drawer is scrolling or flinging.
	 *
	 * @return True if the drawer is scroller or flinging, false otherwise.
	 */
	public boolean isMoving() {
		return mTracking || mAnimating;
	}

	public int getBottomOffset () {
		return mBottomOffset;
	}

	public void setBottomOffset (int offset) {
		this.mBottomOffset = offset;
		invalidate();
	}

	public int getTopOffset () {
		return mTopOffset;
	}

	public void setTopOffset (int offset) {
		this.mTopOffset = offset;
		invalidate();
	}

	public boolean isAllowSingleTap () {
		return mAllowSingleTap;
	}

	public void setAllowSingleTap (boolean mAllowSingleTap) {
		this.mAllowSingleTap = mAllowSingleTap;
	}

	public boolean isAnimateOnClick () {
		return mAnimateOnClick;
	}

	public void setAnimateOnClick (boolean mAnimateOnClick) {
		this.mAnimateOnClick = mAnimateOnClick;
	}

	/**
	 * Drawer click listener.
	 */
	private class DrawerToggler implements OnClickListener {
		public void onClick(View v) {
			if (mLocked) {
				return;
			}
			// mAllowSingleTap isn't relevant here; you're *always*
			// allowed to open/close the drawer by clicking with the
			// trackball.

			if (mAnimateOnClick) {
				animateToggle();
			} else {
				toggle();
			}
		}
	}

	private class SlidingHandler extends Handler {
		public void handleMessage(Message m) {
			switch (m.what) {
				case MSG_ANIMATE:
					doAnimation();
					break;
			}
		}
	}
	
	private abstract class OrientationSpecific{
		protected int headLeft;
		protected int headTop;
		protected abstract void moveHead(int position);
		protected abstract boolean inThreshold (int top, int left);
		protected abstract void prepareTracking();
		protected abstract void measureContent();
		protected abstract void layoutContent(int width, int height);
		protected void setClosedRect(){}
	}
	private final int CLOSED_SIZE = 10;
	private abstract class TopBottomOS extends OrientationSpecific{
		protected int calculateHeightForMeasure(){
			int height = getBottom() - getTop() - tHead.getHeight() - mTopOffset;
			tContent.measure(MeasureSpec.makeMeasureSpec(getRight() - getLeft(), MeasureSpec.EXACTLY),
					MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
			return height;
		}
		protected void setClosedRect(){
			closedRect.set(0, tScreenHeight-CLOSED_SIZE, tScreenWidth, tScreenHeight);
		}
	}
	
	private abstract class LeftRightOS extends OrientationSpecific{
		protected int calculateWidthForMeasure(){
			int width = getRight() - getLeft() - tHead.getWidth() - mTopOffset;
			tContent.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
					MeasureSpec.makeMeasureSpec(getBottom() - getTop(), MeasureSpec.EXACTLY));
			return width;
		}
		protected void setClosedRect(){
			closedRect.set(0, 0, CLOSED_SIZE, tScreenHeight);
		}
	}
	
	private class TopOrientation extends TopBottomOS{
		protected void moveHead(int position){
			if (position == EXPANDED_FULL_OPEN) {
				tHead.offsetTopAndBottom( getBottom() - getTop() - mTopOffset - tHead.getHeight() - tHead.getTop() );
				invalidate();
			} else if (position == COLLAPSED_FULL_CLOSED) {
				tHead.offsetTopAndBottom( -mBottomOffset - tHead.getTop() );
				invalidate();
			} else {
				int deltaY = position - tHead.getTop();
				if (position < -mBottomOffset) {
					deltaY = -mBottomOffset - tHead.getTop();
				}
				else if (position > getBottom() - getTop() - mTopOffset - tHead.getHeight()) {
					deltaY = getBottom() - getTop() - mTopOffset - tHead.getHeight() - tHead.getTop();
				}
				regionUnionYAndInvalidate(deltaY);
			}
		}
		protected boolean inThreshold (int top, int left) {
			return  (!tIsOpen && top < mTapThreshold - mBottomOffset) ||
					( tIsOpen && top > getBottom() - getTop() - tHead.getHeight() - mTopOffset - mTapThreshold);
		}
		protected void measureContent(){
			int height = calculateHeightForMeasure();
			tContent.layout(0, height - tContent.getMeasuredHeight(), tContent.getMeasuredWidth(), height );
		}
		protected void prepareTracking() {
			mAnimationPosition = mBottomOffset;
		}
		protected void layoutContent(int width, int height){
			switch (mHandlePos) {
			case LEFT:	headLeft = mHandlePad; break;
			case RIGHT:	headLeft = width - tHead.getMeasuredWidth() - mHandlePad; break;
			default:	headLeft = (width - tHead.getMeasuredWidth()) / 2; break;}
			headTop = tIsOpen ? height - tHead.getMeasuredHeight() - mTopOffset: -mBottomOffset;
			tContent.layout(0, height - tHead.getMeasuredHeight() - mTopOffset - tContent.getMeasuredHeight(),
				tContent.getMeasuredWidth(), height - tHead.getMeasuredHeight() - mTopOffset );
		}
	}

	private class BottomOrientation extends TopBottomOS{
		protected void moveHead(int position){
			if (position == EXPANDED_FULL_OPEN) {
				tHead.offsetTopAndBottom( mTopOffset - tHead.getTop());
				invalidate();
			} else if (position == COLLAPSED_FULL_CLOSED) {
				tHead.offsetTopAndBottom( mBottomOffset + getBottom() - getTop() - tHead.getHeight() - tHead.getTop());
				invalidate();
				changeHeadOnClose();
				requestLayout();
			} else {
				
				int deltaY = position - tHead.getTop();
				if(deltaY>tHead.getHeight()*2.5){
					finishHeadOpenAnimation();
				}
				if (position < mTopOffset) {
					deltaY = mTopOffset - tHead.getTop();
				} else if (deltaY > mBottomOffset + getBottom() - getTop() - tHead.getHeight() - tHead.getTop()) {
					deltaY = mBottomOffset + getBottom() - getTop() - tHead.getHeight() - tHead.getTop();
				}
				regionUnionYAndInvalidate(deltaY);
			}
		}
		
		protected boolean inThreshold (int top, int left) {
			return  ( tIsOpen && top < mTapThreshold + mTopOffset) ||
					(!tIsOpen && top > mBottomOffset + getBottom() - getTop() -  tHead.getHeight() - mTapThreshold);
		}

		protected void prepareTracking() {
			mAnimationPosition = mBottomOffset + getHeight() - tHead.getHeight();
		}
		
		protected void measureContent(){
			calculateHeightForMeasure();
			tContent.layout(0, mTopOffset + tHead.getHeight(), tContent.getMeasuredWidth(),
					mTopOffset + tHead.getHeight() + tContent.getMeasuredHeight());
		}
		
		protected void layoutContent(int width, int height) {
			switch (mHandlePos) {
			case LEFT:headLeft = mHandlePad;break;
			case RIGHT:headLeft = width - tHead.getMeasuredWidth() - mHandlePad;break;
			default:headLeft = (width - tHead.getMeasuredWidth()) / 2;break;}
			headTop = tIsOpen ? mTopOffset : height - tHead.getMeasuredHeight() + mBottomOffset;
			tContent.layout(0,mTopOffset + tHead.getMeasuredHeight(),tContent.getMeasuredWidth(),
					mTopOffset + tHead.getMeasuredHeight()+ tContent.getMeasuredHeight());
		}
	}
	
	private class LeftOrientation extends LeftRightOS{
		protected void moveHead(int position){
			if (position == EXPANDED_FULL_OPEN) {
				tHead.offsetLeftAndRight( getRight() - getLeft() - mTopOffset - tHead.getWidth() - tHead.getLeft() );
				invalidate();
			} else if (position == COLLAPSED_FULL_CLOSED) {
				tHead.offsetLeftAndRight(-mBottomOffset - tHead.getLeft() );
				invalidate();
			} else {
				int deltaX = position - tHead.getLeft();
				if (position < -mBottomOffset) {
					deltaX = -mBottomOffset - tHead.getLeft();
				}
				else if (position > getRight() - getLeft() - mTopOffset - tHead.getWidth()) {
					deltaX = getRight() - getLeft() - mTopOffset - tHead.getWidth() - tHead.getLeft();
				}
				regionUnionXAndInvalidate(deltaX);
			}
		}
		protected boolean inThreshold (int top, int left) {
			return  (!tIsOpen && left < mTapThreshold - mBottomOffset) ||
					( tIsOpen && left > getRight() - getLeft() - tHead.getWidth() - mTopOffset - mTapThreshold);
		}
		protected void prepareTracking() {
			mAnimationPosition = mBottomOffset;
		}
		protected void measureContent() {
			calculateWidthForMeasure();
			tContent.layout(tHead.getWidth() + mTopOffset, 0,
					mTopOffset + tHead.getWidth() + tContent.getMeasuredWidth(), tContent.getMeasuredHeight());
		}
		protected void layoutContent(int width, int height){
			headLeft = tIsOpen ? width - tHead.getMeasuredWidth() - mTopOffset: -mBottomOffset;
			switch (mHandlePos) {
			case TOP:	 headTop = mHandlePad;break;
			case BOTTOM: headTop = height - tHead.getMeasuredHeight() - mHandlePad;break;
			default:	 headTop = (height - tHead.getMeasuredHeight()) / 2;break;}
			tContent.layout( width - tHead.getMeasuredWidth() - mTopOffset - tContent.getMeasuredWidth(), 0,
					width - tHead.getMeasuredWidth() - mTopOffset, tContent.getMeasuredHeight());
		}
	}
	
	private class RightOrientation extends LeftRightOS{
		protected void moveHead(int position){
			if (position == EXPANDED_FULL_OPEN) {
				tHead.offsetLeftAndRight( mTopOffset - tHead.getLeft());
				invalidate();
			} else if (position == COLLAPSED_FULL_CLOSED) {
				tHead.offsetLeftAndRight( -mBottomOffset );
				invalidate();
			} else {
				int deltaX = position - tHead.getLeft();
				if (position < mTopOffset) {
					deltaX = mTopOffset - tHead.getLeft();
				} else if (deltaX > mBottomOffset + getRight() - getLeft() - tHead.getWidth() - tHead.getLeft()) {
					deltaX = mBottomOffset + getRight() - getLeft() - tHead.getWidth() - tHead.getLeft();
				}
				regionUnionXAndInvalidate(deltaX);
			}
		}
		protected boolean inThreshold (int top, int left) {
			return  ( tIsOpen && left < mTapThreshold + mTopOffset) ||
					(!tIsOpen && left > mBottomOffset + getRight() - getLeft() - tHead.getWidth() - mTapThreshold);
		}

		protected void prepareTracking() {
			mAnimationPosition = mBottomOffset + getWidth() - tHead.getWidth();
		}
		protected void measureContent() {
			int width = calculateWidthForMeasure();
			tContent.layout( width - tContent.getMeasuredWidth(), 0, width, tContent.getMeasuredHeight());
		}
		
		protected void layoutContent(int width, int height){
			headLeft = tIsOpen ? mTopOffset : width - tHead.getMeasuredWidth() + mBottomOffset;
			switch (mHandlePos) {
				case TOP:	 headTop = mHandlePad; break;
				case BOTTOM: headTop = height - tHead.getMeasuredHeight() - mHandlePad; break;
				default:	 headTop = (height - tHead.getMeasuredHeight()) / 2; break;
			}
			tContent.layout( mTopOffset + tHead.getMeasuredWidth(), 0,
					mTopOffset + tHead.getMeasuredWidth() + tContent.getMeasuredWidth(),
					tContent.getMeasuredHeight());
		}
	}

	/**
	 * Callback invoked when the drawer is opened.
	 */
	public static interface OnDrawerOpenListener {
		/**
		 * Invoked when the drawer becomes fully open.
		 */
		public void onDrawerOpened();
	}

	/**
	 * Callback invoked when the drawer is closed.
	 */
	public static interface OnDrawerCloseListener {
		/**
		 * Invoked when the drawer becomes fully closed.
		 */
		public void onDrawerClosed();
	}

	/**
	 * Callback invoked when the drawer is scrolled.
	 */
	public static interface OnDrawerScrollListener {
		/**
		 * Invoked when the user starts dragging/flinging the drawer's handle.
		 */
		public void onScrollStarted();

		/**
		 * Invoked when the user stops dragging/flinging the drawer's handle.
		 */
		public void onScrollEnded();
	}


	public enum Side {
		TOP, LEFT, BOTTOM, RIGHT, FRONT, BACK, CENTER;

		public static Side getByValue(int value) {
			for(Side s: Side.values()) {
				if(s.ordinal() == value) {
					return s;
				}
			}
			throw new IllegalArgumentException("There is no 'Side' enum with this value");
		}
	}

	public enum Orientation {
		TOP, LEFT, BOTTOM, RIGHT;

		public static Orientation getByValue(int value) {
			for(Orientation s: Orientation.values()) {
				if(s.ordinal() == value) {
					return s;
				}
			}
			throw new IllegalArgumentException("There is no 'Orientation' enum with this value");
		}
	}
	
	private int pixelToDp(int dp){
		return Math.round(dp * (tDensityScale / DisplayMetrics.DENSITY_DEFAULT));
	}
}
