/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.view.resizable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

/**
 * @author Artan Veliju
 */
public class CornerBall {
    Bitmap tBitmap;
    Context tContext;
    Point tPoint;
    short tId;

    public CornerBall(Context context, int resourceId, Point point, short id) {
        this.tId = id;
        tBitmap = BitmapFactory.decodeResource(context.getResources(),
                resourceId);
//            bitmap = Bitmap.createScaledBitmap(bitmap, size, size, false);
        tContext = context;
        this.tPoint = point;
    }

    public int getWidthOfBall() {
        return tBitmap.getWidth();
    }

    public int getHeightOfBall() {
        return tBitmap.getHeight();
    }

    public Bitmap getBitmap() {
        return tBitmap;
    }

    public int getX() {
        return tPoint.x;
    }

    public int getY() {
        return tPoint.y;
    }

    public short getID() {
        return tId;
    }

    public void setX(int x) {
        tPoint.x = x;
    }

    public void setY(int y) {
        tPoint.y = y;
    }
}