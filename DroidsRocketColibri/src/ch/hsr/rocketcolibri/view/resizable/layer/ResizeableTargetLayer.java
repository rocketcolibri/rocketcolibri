package ch.hsr.rocketcolibri.view.resizable.layer;

import java.util.ArrayList;

import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.view.MyAbsoluteLayout;
import ch.hsr.rocketcolibri.view.resizable.CornerBall;
import ch.hsr.rocketcolibri.view.resizable.IResizeListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

public class ResizeableTargetLayer extends MyAbsoluteLayout {

    Point point1, point3;
    Point point2, point4;

    /**
     * point1 and point 3 are of same group and same as point 2 and point4
     */
    int groupId = -1;
    private ArrayList<CornerBall> colorballs = new ArrayList<CornerBall>();
    // array that holds the balls
    private int balID = 0;
    // variable to know what ball is being dragged
    private Paint borderPaint;
    private Paint ballPaint = new Paint();
    Canvas canvas;
    private View resizeTarget;
    int ballSpacing = 100;
    LayoutParams resizeTargetLP;
    private IResizeListener listener;
    
    public ResizeableTargetLayer(final Context context, View resizeTarget, LayoutParams layoutParams, final IResizeListener listener) {
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
        // setting the start point for the balls
        point1 = new Point(); //top left
        point1.x = resizeTargetLP.x-ballSpacing;
        point1.y = resizeTargetLP.y-ballSpacing;

        point2 = new Point(); 
        point2.x = (int)resizeTargetLP.x-ballSpacing; // bottom left
        point2.y = (int)resizeTargetLP.y+resizeTargetLP.height;

        point3 = new Point();// bottom right
        point3.x = (int)resizeTargetLP.x+resizeTargetLP.width;
        point3.y = (int)resizeTargetLP.y+resizeTargetLP.height;

        point4 = new Point();/// top right
        point4.x = (int) (resizeTargetLP.x+resizeTargetLP.width);
        point4.y = (int)resizeTargetLP.y-ballSpacing;

        // declare each ball with the ColorBall class
        colorballs.add(new CornerBall(context, R.drawable.square, point1, ballSpacing, 0));
        colorballs.add(new CornerBall(context, R.drawable.square, point2, ballSpacing, 1));
        colorballs.add(new CornerBall(context, R.drawable.square, point3, ballSpacing, 2));
        colorballs.add(new CornerBall(context, R.drawable.square, point4, ballSpacing, 3));
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

            canvas.drawRect(point2.x + colorballs.get(1).getWidthOfBall() / 2,
                    point4.y + colorballs.get(3).getWidthOfBall() / 2, point4.x
                            + colorballs.get(3).getWidthOfBall() / 2, point2.y
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
            balID = -1;
            groupId = -1;
            for (CornerBall ball : colorballs) {
                // check if inside the bounds of the ball (circle)
                // get the center for the ball
                Log.d("","Id : " + ball.getID());
                Log.d("","getX : " + ball.getX() + " getY() : " + ball.getY());
                int centerX = ball.getX() + ball.getWidthOfBall();
                int centerY = ball.getY() + ball.getHeightOfBall();
                borderPaint.setColor(Color.CYAN);
                // calculate the radius from the touch to the center of the ball
                double radCircle = Math
                        .sqrt((double) (((centerX - X) * (centerX - X)) + (centerY - Y)
                                * (centerY - Y)));

                Log.d("","X : " + X + " Y : " + Y + " centerX : " + centerX
                        + " CenterY : " + centerY + " radCircle : " + radCircle);

                if (radCircle < ball.getWidthOfBall()) {
                    balID = ball.getID();
                    Log.d("","Selected ball : " + balID);
                    if (balID == 1 || balID == 3) {
                        groupId = 2;
                        canvas.drawRect(point1.x, point3.y, point3.x, point1.y,
                                borderPaint);
                    } else {
                        groupId = 1;
                        canvas.drawRect(point2.x, point4.y, point4.x, point2.y,
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
            if (balID > -1 && balID<4) {
            	Log.d("","Moving Ball : " + balID);
                colorballs.get(balID).setX(X);
                colorballs.get(balID).setY(Y);

                borderPaint.setColor(Color.CYAN);

                if (groupId == 1) {
                    /*bottom left*/ colorballs.get(1).setX(colorballs.get(0).getX());//top left
                    /*bottom left*/ colorballs.get(1).setY(colorballs.get(2).getY());//bottom right
                    /*top right  */ colorballs.get(3).setX(colorballs.get(2).getX());//bottom right
                    /*top right  */ colorballs.get(3).setY(colorballs.get(0).getY());//top left
                    canvas.drawRect(point1.x, point3.y, point3.x, point1.y, borderPaint);
                   	int topLeftCornerX = colorballs.get(0).getX()+ballSpacing;
                	int topLeftCornerY = colorballs.get(0).getY()+ballSpacing;
                   	int bottomRightCornerX = colorballs.get(2).getX();
                	int bottomRightCornerY = colorballs.get(2).getY();
                	
                 	resizeTargetLP.width = bottomRightCornerX-topLeftCornerX;
                	resizeTargetLP.height = bottomRightCornerY-topLeftCornerY;
                	resizeTargetLP.x = topLeftCornerX;
                	resizeTargetLP.y = topLeftCornerY;

                    Log.d("","moving balls : bottom left + top right");
                } else {
                   /*top left    */ colorballs.get(0).setX(colorballs.get(1).getX());//bottom left
                   /*top left    */ colorballs.get(0).setY(colorballs.get(3).getY());//top right
                   /*bottom right*/ colorballs.get(2).setX(colorballs.get(3).getX());//top right
                   /*bottom right*/ colorballs.get(2).setY(colorballs.get(1).getY());//bottom left
                    canvas.drawRect(point2.x, point4.y, point4.x, point2.y, borderPaint);
                    Log.d("","moving balls : top left  + bottom right");
                   	int topLeftCornerX = colorballs.get(0).getX()+ballSpacing;
                	int topLeftCornerY = colorballs.get(0).getY()+ballSpacing;
                   	int bottomRightCornerX = colorballs.get(2).getX();
                	int bottomRightCornerY = colorballs.get(2).getY();
                	
                 	resizeTargetLP.width = bottomRightCornerX-topLeftCornerX;
                	resizeTargetLP.height = bottomRightCornerY-topLeftCornerY;
                	resizeTargetLP.x = topLeftCornerX;
                	resizeTargetLP.y = topLeftCornerY;
                }
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
        canvas.drawRect(point1.x, point3.y, point3.x, point1.y, borderPaint);
    }
}