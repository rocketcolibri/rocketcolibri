package ch.hsr.rocketcolibri.util;

import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.view.AbsoluteLayout;
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Helper function for the rocket colibri application.
 * 
 * @author lorenz
 *
 */
public class DrawingTools {
	
	public static final int radiusEdge=10;
	static Rect rect = new Rect();
	
	public static Bitmap resizeBitmap(Bitmap bitmap, int width, int height) { //width - height in pixel not in DP
	    bitmap.setDensity(Bitmap.DENSITY_NONE); 
	    Bitmap newbmp = Bitmap.createScaledBitmap(bitmap, width, height, true);
	    return newbmp;
	}

	/**
	 * cut rounded edges of a bitmap
	 * @param bitmap original bitmap
	 * @param pixels (radius of the edge)
	 * @return cutted bitmap
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
	
	/**
	 * draw a background with rounded edges
	 * @param r
	 * @param canvas
	 * @param paint
	 */
	public static void drawRoundWidgetBacktground(RectF r, Canvas canvas, Paint paint) {
		rect.set(0, 0, canvas.getWidth(),canvas.getHeight());
		r.set(rect);
		canvas.drawRoundRect( r, 10f,10f, paint);
	}
	
	/**
	 * this function should be used by all widgets in customizable mode to draw the green
	 * shadow in the widget forground.
	 * 
	 * @param view
	 * @param canvas
	 */
	public static void drawCustomizableForground(View view, Canvas canvas) {
		final Drawable foreground = view.getResources().getDrawable(
				R.drawable.dragforeground);
		if (foreground != null) {
			foreground.setBounds(0, 0, view.getRight() - view.getLeft(), view.getBottom()
					- view.getTop());

			final int scrollX = view.getScrollX();
			final int scrollY = view.getScrollY();

			if ((scrollX | scrollY) == 0) {
				foreground.draw(canvas);
			} else {
				canvas.translate(scrollX, scrollY);
				foreground.draw(canvas);
				canvas.translate(-scrollX, -scrollY);
			}
		}
	}

	public static LayoutParams checkMaxSize(LayoutParams lParam, AbsoluteLayout parent) {
		LayoutParams maxSizeLP = lParam;

		if(maxSizeLP.height + maxSizeLP.getY() > parent.getHeight()) {
			maxSizeLP.setY(parent.getHeight() - maxSizeLP.height);
		}

		if(lParam.width + lParam.getX() > parent.getWidth()) {
			maxSizeLP.setX(parent.getWidth() - maxSizeLP.width);
		}
		
		if (maxSizeLP.getY() < 0) {
			maxSizeLP.setY(0);
		}

		if (maxSizeLP.getX() < 0) {
			maxSizeLP.setX(0);
		}

		if (maxSizeLP.height > parent.getHeight()) {
			maxSizeLP.height = parent.getHeight();
		}

		if (maxSizeLP.width > parent.getWidth()) {
			maxSizeLP.width = parent.getWidth();
		}
		return maxSizeLP;
	}
}
