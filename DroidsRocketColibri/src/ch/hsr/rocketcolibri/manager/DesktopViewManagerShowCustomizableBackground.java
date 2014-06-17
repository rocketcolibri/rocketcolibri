package ch.hsr.rocketcolibri.manager;

import ch.hsr.rocketcolibri.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import android.widget.RelativeLayout;

public class DesktopViewManagerShowCustomizableBackground extends View{

    private Bitmap tVideoBitmap;
    private Paint tRectPaint;
    private Rect tRectRect;
    private RectF tRectRectF;
    RelativeLayout tRel;

	public DesktopViewManagerShowCustomizableBackground(Context context) {
		super(context);
		init(context);
		// TODO Auto-generated constructor stub
	}

	private void init(Context context) {
		tRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		tRectPaint.setColor(Color.WHITE);
		tRectPaint.setAlpha(100);
		tRectRect = new Rect();
		tRectRectF  = new RectF();
		tVideoBitmap =  BitmapFactory.decodeResource(getContext().getResources(), R.drawable.configuration_background);
		setAlpha(0.2f);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		// draw background rectangle
		tRectRect.set(0, 0, canvas.getWidth(),canvas.getHeight());
		tRectRectF.set(tRectRect);
		canvas.drawRoundRect( tRectRectF, 10f,10f, tRectPaint);
		// draw bitmap
		canvas.drawBitmap(tVideoBitmap, canvas.getWidth()/2 - tVideoBitmap.getWidth()/2 , canvas.getHeight()/2 - tVideoBitmap.getHeight()/2 , null);
		super.onDraw(canvas);
	}
}