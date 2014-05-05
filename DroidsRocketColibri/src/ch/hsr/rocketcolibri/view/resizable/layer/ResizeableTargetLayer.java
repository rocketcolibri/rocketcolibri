/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.view.resizable.layer;

import java.util.ArrayList;

import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.view.AbsoluteLayout;
import ch.hsr.rocketcolibri.view.resizable.CornerBall;
import ch.hsr.rocketcolibri.view.resizable.IResizeDoneListener;
import ch.hsr.rocketcolibri.view.resizable.ResizeConfig;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author Artan Veliju
 */
public class ResizeableTargetLayer extends AbsoluteLayout {

	/**
	 *  point0
	 *  O--------------O point2
	 *  -              -
	 *  - View Element -
	 *  -              -
	 *  O--------------O point3
	 *  point1
	 */
	private Point tPoint0, tPoint2;
	private Point tPoint1, tPoint3;

    /**
     * point0 and point 2 are of same group and same as point 1 and point3
     */
    private short tGroupId = -1;
    private ArrayList<CornerBall> tColorballs = new ArrayList<CornerBall>();
    private short tBallID = 0;// variable to know what ball is being dragged
    
    private Paint tOuterBorderPaint = new Paint();
    private Paint tInnerBorderPaint = new Paint();
    private Paint tBallPaint = new Paint();
    private Canvas tCanvas;
    private View tResizeTarget;
    private LayoutParams tResizeTargetLP;
    private IResizeDoneListener tListener;
    private ResizeConfig tConfig;
    private int tTopLeftOrRightX;
    private int tTopLeftOrRightY;
    private int tBottomLeftOrRightX;
    private int tBottomLeftOrRightY;
    private int tHeight;
    private int tWidth;
    private int tCenterX;
    private int tCenterY;
    private double tRadCircle;
    private int tCurrentX;
    private int tCurrentY;
    private double tRatio;

    /**
	 * Class name for logging 
	 */
	final String TAG = this.getClass().getName();

    public ResizeableTargetLayer(final Context context, View resizeTarget, LayoutParams layoutParams, final IResizeDoneListener listener) {
    	this(context, resizeTarget, layoutParams, listener, new ResizeConfig());
    }
    
    public ResizeableTargetLayer(final Context context, View resizeTarget, LayoutParams layoutParams, final IResizeDoneListener listener, ResizeConfig config) {
        super(context);
        this.tResizeTarget = resizeTarget;
        tResizeTargetLP = (LayoutParams) resizeTarget.getLayoutParams();
        setLayoutParams(layoutParams);
        createBalls(context);
        setBorderPaintSettings();
        setBackgroundColor(Color.TRANSPARENT);
        addView(resizeTarget);
        resizeTarget.setEnabled(false);
        this.tListener = listener;
        tConfig = config;
        if(tConfig.keepRatio){
        	tRatio = tResizeTargetLP.width / (double)tResizeTargetLP.height;
        }
    }
    
    private void createBalls(Context context){
        setFocusable(true); // necessary for getting the touch events
        tCanvas = new Canvas();
        int cornerItemResource = R.drawable.square;

        tPoint0 = new Point(); //top left
        CornerBall cBall = new CornerBall(context, cornerItemResource, tPoint0, (short)0);// predefine to get the dimension of the corner item
        
        tPoint0.x = tResizeTargetLP.x-cBall.getWidthOfBall();
        tPoint0.y = tResizeTargetLP.y-cBall.getHeightOfBall();

        tPoint1 = new Point(); 
        tPoint1.x = (int)tResizeTargetLP.x-cBall.getWidthOfBall(); // bottom left
        tPoint1.y = (int)tResizeTargetLP.y+tResizeTargetLP.height;

        tPoint2 = new Point();// bottom right
        tPoint2.x = (int)tResizeTargetLP.x+tResizeTargetLP.width;
        tPoint2.y = (int)tResizeTargetLP.y+tResizeTargetLP.height;

        tPoint3 = new Point();/// top right
        tPoint3.x = (int) (tResizeTargetLP.x+tResizeTargetLP.width);
        tPoint3.y = (int)tResizeTargetLP.y-cBall.getHeightOfBall();

        tColorballs.add(cBall);
        tColorballs.add(new CornerBall(context, cornerItemResource, tPoint1, (short)1));
        tColorballs.add(new CornerBall(context, cornerItemResource, tPoint2, (short)2));
        tColorballs.add(new CornerBall(context, cornerItemResource, tPoint3, (short)3));
    }
    
    private void setBorderPaintSettings(){
        tOuterBorderPaint.setAntiAlias(true);
        tOuterBorderPaint.setDither(true);
        tOuterBorderPaint.setStyle(Paint.Style.STROKE);
        tOuterBorderPaint.setStrokeJoin(Paint.Join.ROUND);
        tOuterBorderPaint.setStrokeWidth(3);
        tOuterBorderPaint.setColor(Color.parseColor("#FF9500"));
        
        tInnerBorderPaint.setAntiAlias(true);
        tInnerBorderPaint.setDither(true);
        tInnerBorderPaint.setStyle(Paint.Style.STROKE);
        tInnerBorderPaint.setStrokeJoin(Paint.Join.ROUND);
        tInnerBorderPaint.setStrokeWidth(2);
        tInnerBorderPaint.setColor(Color.GREEN);
    }

    
    // the method that draws the balls
    @Override
    protected void onDraw(Canvas canvas) {
    	//draw outer border
        canvas.drawRect(tPoint1.x + tColorballs.get(1).getWidthOfBall() / 2,
                tPoint3.y + tColorballs.get(3).getWidthOfBall() / 2, tPoint3.x
                        + tColorballs.get(3).getWidthOfBall() / 2, tPoint1.y
                        + tColorballs.get(1).getWidthOfBall() / 2, tOuterBorderPaint);
        //draw inner border
        canvas.drawRect(tPoint1.x + tColorballs.get(1).getWidthOfBall(),
                tPoint3.y + tColorballs.get(3).getWidthOfBall(), tPoint3.x, tPoint1.y, tInnerBorderPaint);
        for (CornerBall ball : tColorballs) {
            canvas.drawBitmap(ball.getBitmap(), ball.getX(), ball.getY(), tBallPaint);
        }
    }

    // events when touching the screen
    public boolean onTouchEvent(MotionEvent event) {
    	if (!isEnabled()) {
			return true;
		}
        tCurrentX = (int) event.getX();
        tCurrentY = (int) event.getY();
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN: // touch down so check if the finger is on a ball
            tBallID = -1;
            tGroupId = -1;
            for (CornerBall ball : tColorballs) {
                // check if inside the bounds of the ball (circle)
                // get the center for the ball
                tCenterX = ball.getX() + ball.getWidthOfBall();
                tCenterY = ball.getY() + ball.getHeightOfBall();
                // calculate the radius from the touch to the center of the ball
                tRadCircle = Math.sqrt((double) (((tCenterX - tCurrentX) * (tCenterX - tCurrentX)) + (tCenterY - tCurrentY) * (tCenterY - tCurrentY)));
                if (tRadCircle < ball.getWidthOfBall()) {
                    tBallID = ball.getID();
                    if (tBallID == 1 || tBallID == 3) {
                        tGroupId = 2;
                        tCanvas.drawRect(tPoint0.x, tPoint2.y, tPoint2.x, tPoint0.y, tOuterBorderPaint);
                    } else {
                        tGroupId = 1;
                        tCanvas.drawRect(tPoint1.x, tPoint3.y, tPoint3.x, tPoint1.y, tOuterBorderPaint);
                    }
                    invalidate();
                    break;
                }
                invalidate();
            }
            break;

        case MotionEvent.ACTION_MOVE:
            // move the balls the same as the finger
            if (tBallID > -1 && tBallID<4) {
                if (tConfig.keepRatio) {
                	calculationWithRatio();
                }else{
                	calculationWithoutRatio();
                }
                invalidate();
            }
            break;
        case MotionEvent.ACTION_UP:
          if(tGroupId==-1){
        	  stop();
          }
          return true;
        }
        invalidate();
        tResizeTarget.setLayoutParams(tResizeTargetLP);
        this.updateViewLayout(tResizeTarget, tResizeTargetLP);
        return true;
    }
    
	public void stop() {
		finish();
		tListener.done(tResizeTarget);
	}
    
    private void calculationWithRatio(){
        if (tGroupId == 1) {
        	if(tBallID==0){
            	tTopLeftOrRightX = tCurrentX+tColorballs.get(0).getWidthOfBall();
            	tBottomLeftOrRightX = tColorballs.get(2).getX();
        	}else{
        		tBottomLeftOrRightX = tCurrentX;
               	tTopLeftOrRightX = tColorballs.get(0).getX()+tColorballs.get(0).getWidthOfBall();
        	}
        	tWidth = tBottomLeftOrRightX-tTopLeftOrRightX;
        	tHeight = calculateHeightWithRatio();

        	// break if min or max size for width or height is reached
        	if (!isWidthInPermittedRange() || !isHeightInPermittedRange()) return;

        	setXAndYToTheTargetPoint();
        	setWidthToTargetViewAndCalculateRatio();
        	tResizeTargetLP.x = tTopLeftOrRightX;
        	if(tBallID==0){
        		tColorballs.get(0).setY(tColorballs.get(2).getY()-tResizeTargetLP.height-tColorballs.get(2).getHeightOfBall());
        	}else{
        		tColorballs.get(2).setY(tResizeTargetLP.height+tColorballs.get(0).getY()+tColorballs.get(0).getHeightOfBall());
        	}
        	tResizeTargetLP.y = tColorballs.get(0).getY()+tColorballs.get(0).getHeightOfBall();
        	changePointsOnBottomLeftAndTopRightAndDraw();
        } else {
        	if(tBallID==1){
            	tTopLeftOrRightX = tColorballs.get(3).getX();
            	tBottomLeftOrRightX = tCurrentX+tColorballs.get(1).getHeightOfBall();
        	}else{
            	tTopLeftOrRightX = tCurrentX;
            	tBottomLeftOrRightX = tColorballs.get(1).getX()+tColorballs.get(1).getHeightOfBall();
        	}
        	tWidth = tTopLeftOrRightX-tBottomLeftOrRightX;
        	tHeight = calculateHeightWithRatio();

        	// break if min or max size for width or height is reached
        	if (!isWidthInPermittedRange() || !isHeightInPermittedRange()) return;

        	setXAndYToTheTargetPoint();
        	setWidthToTargetViewAndCalculateRatio();
        	if(tBallID==1){
        		tColorballs.get(1).setY(tColorballs.get(3).getHeightOfBall()+tResizeTargetLP.height+tColorballs.get(3).getY());
        	}else{
        		tColorballs.get(3).setY(tColorballs.get(1).getY()-tResizeTargetLP.height-tColorballs.get(3).getHeightOfBall());
        	}
        	tResizeTargetLP.x = tTopLeftOrRightX-tResizeTargetLP.width;
        	tResizeTargetLP.y = tColorballs.get(3).getY()+tColorballs.get(3).getHeightOfBall();
        	changePointsOnTopLeftAndBottomRightAndDraw();
        }
    }
    
    private void setWidthToTargetViewAndCalculateRatio(){
    	tResizeTargetLP.width = tWidth;
    	tResizeTargetLP.height = calculateHeightWithRatio(); //(int) (tResizeTargetLP.width/tRatio);
    }
    
    private int calculateHeightWithRatio(){
    	return (int) (tWidth/tRatio);
    }

    private void calculationWithoutRatio(){
        if (tGroupId == 1) {
        	if(tBallID==0){
               	tTopLeftOrRightX = tCurrentX+tColorballs.get(0).getWidthOfBall();
            	tTopLeftOrRightY = tCurrentY+tColorballs.get(0).getHeightOfBall();
               	tBottomLeftOrRightX = tColorballs.get(2).getX();
            	tBottomLeftOrRightY = tColorballs.get(2).getY();
        	}else{
               	tTopLeftOrRightX = tColorballs.get(0).getX()+tColorballs.get(0).getWidthOfBall();
            	tTopLeftOrRightY = tColorballs.get(0).getY()+tColorballs.get(0).getHeightOfBall();
               	tBottomLeftOrRightX = tCurrentX;
            	tBottomLeftOrRightY = tCurrentY;
        	}
         	tWidth = tBottomLeftOrRightX-tTopLeftOrRightX;
         	if(isWidthInPermittedRange()){
         		tColorballs.get(tBallID).setX(tCurrentX);
            	changePointsOnBottomLeftAndTopRight_X_AndDraw();
            	tResizeTargetLP.width = tWidth;
            	tResizeTargetLP.x = tTopLeftOrRightX;
         	}
//         	if(ratio){
//         		tHeight = (int) (tWidth*tRatio);
//         	}else{
         		tHeight = tBottomLeftOrRightY-tTopLeftOrRightY;
//         	}
         	if(isHeightInPermittedRange()){
         		tColorballs.get(tBallID).setY(tCurrentY);
            	changePointsOnBottomLeftAndTopRight_Y_AndDraw();
            	tResizeTargetLP.height = tHeight;
            	tResizeTargetLP.y = tTopLeftOrRightY;
         	}
         	tCanvas.drawRect(tPoint1.x, tPoint3.y, tPoint3.x, tPoint1.y, tOuterBorderPaint);
        } else {
        	if(tBallID==1){
               	tTopLeftOrRightX = tCurrentX+tColorballs.get(1).getWidthOfBall();
            	tTopLeftOrRightY = tCurrentY;
               	tBottomLeftOrRightX = tColorballs.get(3).getX();
            	tBottomLeftOrRightY = tColorballs.get(3).getY()+tColorballs.get(3).getHeightOfBall();
        	}else{
               	tTopLeftOrRightX = tColorballs.get(1).getX()+tColorballs.get(1).getWidthOfBall();
            	tTopLeftOrRightY = tColorballs.get(1).getY();
               	tBottomLeftOrRightX = tCurrentX;
            	tBottomLeftOrRightY = tCurrentY+tColorballs.get(3).getHeightOfBall();
        	}
         	tWidth = tBottomLeftOrRightX-tTopLeftOrRightX;
         	if(isWidthInPermittedRange()){
         		tColorballs.get(tBallID).setX(tCurrentX);
         		changePointsOnTopLeftAndBottomRight_X_AndDraw();
         		tResizeTargetLP.width = tWidth;
         		tResizeTargetLP.x = tTopLeftOrRightX;
         	}
         	
//         	if(ratio){
//         		tHeight = (int) (tWidth*tRatio);
//         	}else{
            	tHeight = tTopLeftOrRightY-tBottomLeftOrRightY;
//         	}
          	if(isHeightInPermittedRange()){
         		tColorballs.get(tBallID).setY(tCurrentY);
         		changePointsOnTopLeftAndBottomRight_Y_AndDraw();
            	tResizeTargetLP.height = tHeight;
            	tResizeTargetLP.y = tBottomLeftOrRightY;
         	}
         	tCanvas.drawRect(tPoint0.x, tPoint2.y, tPoint2.x, tPoint0.y, tOuterBorderPaint);
        }
    }
    
    private boolean isWidthInPermittedRange(){
    	return tWidth >= tConfig.minWidth && tWidth <= tConfig.maxWidth;
    }
    
    private boolean isHeightInPermittedRange(){
    	return tHeight >= tConfig.minHeight && tHeight <= tConfig.maxHeight;
    }
    
    /**
     * x = could be the target
     * c = will be changed
     *  [x]--------------[c]
     *   |                |
     *   |Resizing Target |
     *   |                |
     *  [c]--------------[x]
     */
    private void changePointsOnBottomLeftAndTopRightAndDraw(){
    	changePointsOnBottomLeftAndTopRight_X_AndDraw();
    	changePointsOnBottomLeftAndTopRight_Y_AndDraw();
    	tCanvas.drawRect(tPoint1.x, tPoint3.y, tPoint3.x, tPoint1.y, tOuterBorderPaint);
    }
    
    private void changePointsOnBottomLeftAndTopRight_X_AndDraw(){
        /*bottom left*/ tColorballs.get(1).setX(tColorballs.get(0).getX());//top left
        /*top right  */ tColorballs.get(3).setX(tColorballs.get(2).getX());//bottom right
    }
    
    private void changePointsOnBottomLeftAndTopRight_Y_AndDraw(){
        /*bottom left*/ tColorballs.get(1).setY(tColorballs.get(2).getY());//bottom right
        /*top right  */ tColorballs.get(3).setY(tColorballs.get(0).getY());//top left
    }
    
    
    /**
     * x = could be the target
     * c = will be changed
     *  [c]--------------[x]
     *   |                |
     *   |Resizing Target |
     *   |                |
     *  [x]--------------[c]
     */
    private void changePointsOnTopLeftAndBottomRightAndDraw(){
    	changePointsOnTopLeftAndBottomRight_X_AndDraw();
    	changePointsOnTopLeftAndBottomRight_Y_AndDraw();
        tCanvas.drawRect(tPoint0.x, tPoint2.y, tPoint2.x, tPoint0.y, tOuterBorderPaint);
    }
    
    private void changePointsOnTopLeftAndBottomRight_X_AndDraw(){
        /*top left    */ tColorballs.get(0).setX(tColorballs.get(1).getX());//bottom left
        /*bottom right*/ tColorballs.get(2).setX(tColorballs.get(3).getX());//top right
   }
    
    private void changePointsOnTopLeftAndBottomRight_Y_AndDraw(){
        /*top left    */ tColorballs.get(0).setY(tColorballs.get(3).getY());//top right
        /*bottom right*/ tColorballs.get(2).setY(tColorballs.get(1).getY());//bottom left
    }
    
    private void setXAndYToTheTargetPoint(){
        tColorballs.get(tBallID).setX(tCurrentX);
        tColorballs.get(tBallID).setY(tCurrentY);
    }

    private void finish(){
    	setEnabled(false);
    	tResizeTarget.setEnabled(true);
    	removeAllViews();
    }

    public void shade_region_between_points() {
        tCanvas.drawRect(tPoint0.x, tPoint2.y, tPoint2.x, tPoint0.y, tOuterBorderPaint);
    }
}