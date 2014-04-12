package ch.hsr.rocketcolibri.view.resizable.layer;

import java.util.ArrayList;

import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.view.MyAbsoluteLayout;
import ch.hsr.rocketcolibri.view.resizable.CornerBall;
import ch.hsr.rocketcolibri.view.resizable.IResizeDoneListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;

public class ResizeableTargetLayer extends MyAbsoluteLayout {

	private Point point0, point2;
	private Point point1, point3;

    /**
     * point0 and point 2 are of same group and same as point 1 and point3
     */
    private short groupId = -1;
    private ArrayList<CornerBall> colorballs = new ArrayList<CornerBall>();
    private short ballID = 0;// variable to know what ball is being dragged
    
    private Paint borderPaint;
    private Paint ballPaint = new Paint();
    private Canvas canvas;
    private View resizeTarget;
    private LayoutParams resizeTargetLP;
    private IResizeDoneListener listener;
    private int topLeftCornerX;
    private int topLeftCornerY;
    private int bottomRightCornerX;
    private int bottomRightCornerY;
    
    public ResizeableTargetLayer(final Context context, View resizeTarget, LayoutParams layoutParams, final IResizeDoneListener listener) {
        super(context);
        this.resizeTarget = resizeTarget;
        resizeTargetLP = (LayoutParams) resizeTarget.getLayoutParams();
        setLayoutParams(layoutParams);
        createBalls(context);
        setBackgroundColor(Color.TRANSPARENT);
        addView(resizeTarget);
        resizeTarget.setEnabled(false);
        this.listener = listener;
    }
    
    private void createBalls(Context context){
        borderPaint = new Paint();
        setFocusable(true); // necessary for getting the touch events
        canvas = new Canvas();
        int cornerItemResource = R.drawable.circle;

        point0 = new Point(); //top left
        CornerBall cBall = new CornerBall(context, cornerItemResource, point0, 0);// predefine to get the dimension of the corner item
        
        point0.x = resizeTargetLP.x-cBall.getWidthOfBall();
        point0.y = resizeTargetLP.y-cBall.getHeightOfBall();

        point1 = new Point(); 
        point1.x = (int)resizeTargetLP.x-cBall.getWidthOfBall(); // bottom left
        point1.y = (int)resizeTargetLP.y+resizeTargetLP.height;

        point2 = new Point();// bottom right
        point2.x = (int)resizeTargetLP.x+resizeTargetLP.width;
        point2.y = (int)resizeTargetLP.y+resizeTargetLP.height;

        point3 = new Point();/// top right
        point3.x = (int) (resizeTargetLP.x+resizeTargetLP.width);
        point3.y = (int)resizeTargetLP.y-cBall.getHeightOfBall();

        colorballs.add(cBall);
        colorballs.add(new CornerBall(context, cornerItemResource, point1, 1));
        colorballs.add(new CornerBall(context, cornerItemResource, point2, 2));
        colorballs.add(new CornerBall(context, cornerItemResource, point3, 3));
    }

    
    // the method that draws the balls
    @Override
    protected void onDraw(Canvas canvas) {
        borderPaint.setAntiAlias(true);
        borderPaint.setDither(true);
        borderPaint.setStyle(Paint.Style.FILL);
        borderPaint.setStrokeJoin(Paint.Join.ROUND);
        // mPaint.setStrokeCap(Paint.Cap.ROUND);
        borderPaint.setStrokeWidth(5);
        borderPaint.setColor(Color.parseColor("#55FFFFFF"));

            canvas.drawRect(point1.x + colorballs.get(1).getWidthOfBall() / 2,
                    point3.y + colorballs.get(3).getWidthOfBall() / 2, point3.x
                            + colorballs.get(3).getWidthOfBall() / 2, point1.y
                            + colorballs.get(1).getWidthOfBall() / 2, borderPaint);

        for (CornerBall ball : colorballs) {
            canvas.drawBitmap(ball.getBitmap(), ball.getX(), ball.getY(), ballPaint);
        }
    }

    // events when touching the screen
    public boolean onTouchEvent(MotionEvent event) {
    	if (!isEnabled()) {
			return true;
		}
        int eventaction = event.getAction();

        int X = (int) event.getX();
        int Y = (int) event.getY();
        switch (eventaction) {

        case MotionEvent.ACTION_DOWN: // touch down so check if the finger is on
                                        // a ball
            ballID = -1;
            groupId = -1;
            for (CornerBall ball : colorballs) {
                // check if inside the bounds of the ball (circle)
                // get the center for the ball
                int centerX = ball.getX() + ball.getWidthOfBall();
                int centerY = ball.getY() + ball.getHeightOfBall();
                borderPaint.setColor(Color.CYAN);
                // calculate the radius from the touch to the center of the ball
                double radCircle = Math
                        .sqrt((double) (((centerX - X) * (centerX - X)) + (centerY - Y)
                                * (centerY - Y)));

                if (radCircle < ball.getWidthOfBall()) {
                    ballID = (short) ball.getID();
                    if (ballID == 1 || ballID == 3) {
                        groupId = 2;
                        canvas.drawRect(point0.x, point2.y, point2.x, point0.y,
                                borderPaint);
                    } else {
                        groupId = 1;
                        canvas.drawRect(point1.x, point3.y, point3.x, point1.y,
                                borderPaint);
                    }
                    invalidate();
                    break;
                }
                invalidate();
            }
            break;

        case MotionEvent.ACTION_MOVE: // touch drag with the ball
            // move the balls the same as the finger
            if (ballID > -1 && ballID<4) {
                colorballs.get(ballID).setX(X);
                colorballs.get(ballID).setY(Y);

                borderPaint.setColor(Color.CYAN);

                if (groupId == 1) {
                    /*bottom left*/ colorballs.get(1).setX(colorballs.get(0).getX());//top left
                    /*bottom left*/ colorballs.get(1).setY(colorballs.get(2).getY());//bottom right
                    /*top right  */ colorballs.get(3).setX(colorballs.get(2).getX());//bottom right
                    /*top right  */ colorballs.get(3).setY(colorballs.get(0).getY());//top left
                    canvas.drawRect(point0.x, point2.y, point2.x, point0.y, borderPaint);
                } else {
                   /*top left    */ colorballs.get(0).setX(colorballs.get(1).getX());//bottom left
                   /*top left    */ colorballs.get(0).setY(colorballs.get(3).getY());//top right
                   /*bottom right*/ colorballs.get(2).setX(colorballs.get(3).getX());//top right
                   /*bottom right*/ colorballs.get(2).setY(colorballs.get(1).getY());//bottom left
                    canvas.drawRect(point1.x, point3.y, point3.x, point1.y, borderPaint);
                }
               	topLeftCornerX = colorballs.get(0).getX()+colorballs.get(0).getWidthOfBall();
            	topLeftCornerY = colorballs.get(0).getY()+colorballs.get(0).getHeightOfBall();
               	bottomRightCornerX = colorballs.get(2).getX();
            	bottomRightCornerY = colorballs.get(2).getY();
            	
             	resizeTargetLP.width = bottomRightCornerX-topLeftCornerX;
            	resizeTargetLP.height = bottomRightCornerY-topLeftCornerY;
            	resizeTargetLP.x = topLeftCornerX;
            	resizeTargetLP.y = topLeftCornerY;
                invalidate();
            }

            break;

        case MotionEvent.ACTION_UP:
          if(groupId==-1){
        	  finish();
        	  listener.done(resizeTarget);
          }
          return true;
        }
        invalidate();
        resizeTarget.setLayoutParams(resizeTargetLP);
        this.updateViewLayout(resizeTarget, resizeTargetLP);
        return true;

    }
    
    private void finish(){
    	setEnabled(false);
    	resizeTarget.setEnabled(true);
    	removeAllViews();
    }

    public void shade_region_between_points() {
        canvas.drawRect(point0.x, point2.y, point2.x, point0.y, borderPaint);
    }
}