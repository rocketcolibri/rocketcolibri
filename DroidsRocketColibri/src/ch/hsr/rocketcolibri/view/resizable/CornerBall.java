package ch.hsr.rocketcolibri.view.resizable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Point;

public class CornerBall {

        Bitmap bitmap;
        Context mContext;
        Point point;
        int id;

        public CornerBall(Context context, int resourceId, Point point, int id) {
            this.id = id;
            bitmap = BitmapFactory.decodeResource(context.getResources(),
                    resourceId);
//            bitmap = Bitmap.createScaledBitmap(bitmap, size, size, false);
            mContext = context;
            this.point = point;
        }

        public int getWidthOfBall() {
            return bitmap.getWidth();
        }

        public int getHeightOfBall() {
            return bitmap.getHeight();
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public int getX() {
            return point.x;
        }

        public int getY() {
            return point.y;
        }

        public int getID() {
            return id;
        }

        public void setX(int x) {
            point.x = x;
        }

        public void setY(int y) {
            point.y = y;
        }
    }