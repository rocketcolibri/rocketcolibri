package ch.hsr.rocketcolibri.view.resizable.layer;

import java.util.ArrayList;

import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.view.MyAbsoluteLayout;
import ch.hsr.rocketcolibri.view.resizable.CornerBall;
import ch.hsr.rocketcolibri.view.resizable.IResizeDoneListener;
import ch.hsr.rocketcolibri.view.resizable.ResizeConfig;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;

public class ResizeableTargetLayer extends MyAbsoluteLayout {

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
    private int tTopLeftCornerX;
    private int tTopLeftCornerY;
    private int tBottomRightCornerX;
    private int tBottomRightCornerY;
    private int tCenterX;
    private int tCenterY;
    private double tRadCircle;
    private int tCurrentX;
    private int tCurrentY;
    private double tRation;


    
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
        	tRation = tResizeTargetLP.width/tResizeTargetLP.height;
        }
    }
    
    private void createBalls(Context context){
        setFocusable(true); // necessary for getting the touch events
        tCanvas = new Canvas();
        int cornerItemResource = R.drawable.square;

        tPoint0 = new Point(); //top left
        CornerBall cBall = new CornerBall(context, cornerItemResource, tPoint0, 0);// predefine to get the dimension of the corner item
        
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
        tColorballs.add(new CornerBall(context, cornerItemResource, tPoint1, 1));
        tColorballs.add(new CornerBall(context, cornerItemResource, tPoint2, 2));
        tColorballs.add(new CornerBall(context, cornerItemResource, tPoint3, 3));
    }
    
    private void setBorderPaintSettings(){
        tOuterBorderPaint.setAntiAlias(true);
        tOuterBorderPaint.setDither(true);
        tOuterBorderPaint.setStyle(Paint.Style.STROKE);
        tOuterBorderPaint.setStrokeJoin(Paint.Join.ROUND);
//        tOuterBorderPaint.setStrokeCap(Paint.Cap.ROUND);
        tOuterBorderPaint.setStrokeWidth(3);
        tOuterBorderPaint.setColor(Color.parseColor("#FF9500"));
        
        tInnerBorderPaint.setAntiAlias(true);
        tInnerBorderPaint.setDither(true);
        tInnerBorderPaint.setStyle(Paint.Style.STROKE);
        tInnerBorderPaint.setStrokeJoin(Paint.Join.ROUND);
        // tInnerBorderPaint.setStrokeCap(Paint.Cap.ROUND);
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
                    tBallID = (short) ball.getID();
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

        case MotionEvent.ACTION_MOVE: // touch drag with the ball
            // move the balls the same as the finger
            if (tBallID > -1 && tBallID<4) {
                tColorballs.get(tBallID).setX(tCurrentX);
                tColorballs.get(tBallID).setY(tCurrentY);
                
                if (tGroupId == 1) {
                    /*bottom left*/ tColorballs.get(1).setX(tColorballs.get(0).getX());//top left
                    /*bottom left*/ tColorballs.get(1).setY(tColorballs.get(2).getY());//bottom right
                    /*top right  */ tColorballs.get(3).setX(tColorballs.get(2).getX());//bottom right
                    /*top right  */ tColorballs.get(3).setY(tColorballs.get(0).getY());//top left
                    tCanvas.drawRect(tPoint0.x, tPoint2.y, tPoint2.x, tPoint0.y, tOuterBorderPaint);
                } else {
                   /*top left    */ tColorballs.get(0).setX(tColorballs.get(1).getX());//bottom left
                   /*top left    */ tColorballs.get(0).setY(tColorballs.get(3).getY());//top right
                   /*bottom right*/ tColorballs.get(2).setX(tColorballs.get(3).getX());//top right
                   /*bottom right*/ tColorballs.get(2).setY(tColorballs.get(1).getY());//bottom left
                    tCanvas.drawRect(tPoint1.x, tPoint3.y, tPoint3.x, tPoint1.y, tOuterBorderPaint);
                }
                
                if(tConfig.keepRatio){
	                //calculate the new dimension of the resize target
	               	tTopLeftCornerX = tColorballs.get(0).getX()+tColorballs.get(0).getWidthOfBall();
	            	tTopLeftCornerY = tColorballs.get(0).getY()+tColorballs.get(0).getHeightOfBall();
	               	tBottomRightCornerX = tColorballs.get(2).getX();
	            	tBottomRightCornerY = tColorballs.get(2).getY();
	            	
	             	tResizeTargetLP.width = tBottomRightCornerX-tTopLeftCornerX;
	            	tResizeTargetLP.height = tBottomRightCornerY-tTopLeftCornerY;
	            	tResizeTargetLP.x = tTopLeftCornerX;
	            	tResizeTargetLP.y = tTopLeftCornerY;
                }else{
	                //calculate the new dimension of the resize target
	               	tTopLeftCornerX = tColorballs.get(0).getX()+tColorballs.get(0).getWidthOfBall();
	            	tTopLeftCornerY = tColorballs.get(0).getY()+tColorballs.get(0).getHeightOfBall();
	               	tBottomRightCornerX = tColorballs.get(2).getX();
	            	tBottomRightCornerY = tColorballs.get(2).getY();
	            	
	             	tResizeTargetLP.width = tBottomRightCornerX-tTopLeftCornerX;
	            	tResizeTargetLP.height = tBottomRightCornerY-tTopLeftCornerY;
	            	tResizeTargetLP.x = tTopLeftCornerX;
	            	tResizeTargetLP.y = tTopLeftCornerY;
                }
                invalidate();
            }

            break;

        case MotionEvent.ACTION_UP:
          if(tGroupId==-1){
        	  finish();
        	  tListener.done(tResizeTarget);
          }
          return true;
        }
        invalidate();
        tResizeTarget.setLayoutParams(tResizeTargetLP);
        this.updateViewLayout(tResizeTarget, tResizeTargetLP);
        return true;

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