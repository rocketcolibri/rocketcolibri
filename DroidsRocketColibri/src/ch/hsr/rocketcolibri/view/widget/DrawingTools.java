package ch.hsr.rocketcolibri.view.widget;

import ch.hsr.rocketcolibri.R;
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

public class DrawingTools {
	
	static final int radiusEdge=10;
	static Rect rect = new Rect();
	
	public static Bitmap resizeBitmap(Bitmap bitmap, int width, int height) { //width - height in pixel not in DP
	    bitmap.setDensity(Bitmap.DENSITY_NONE); 
	    Bitmap newbmp = Bitmap.createScaledBitmap(bitmap, width, height, true);
	    return newbmp;
	}

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
	public static void drawRoundWidgetBacktground(RectF r, Canvas canvas, Paint paint) {
		rect.set(0, 0, canvas.getWidth(),canvas.getHeight());
		r.set(rect);
		canvas.drawRoundRect( r, 10f,10f, paint);
	}
	
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
}
