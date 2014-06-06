package ch.hsr.rocketcolibri.manager;

import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.ui_data.output.UiOutputDataType;
import ch.hsr.rocketcolibri.ui_data.output.VideoUrl;
import ch.hsr.rocketcolibri.view.AbsoluteLayout;
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.custimizable.ModusChangeListener;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import ch.hsr.rocketcolibri.view.resizable.ResizeConfig;
import ch.hsr.rocketcolibri.view.widget.RCWidgetConfig;
import ch.hsr.rocketcolibri.view.widget.VideoStreamWidget;
import ch.hsr.rocketcolibri.view.widget.VideoStreamWidgetSurface;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

public class DesktopVewManagerShowCustomizableBackground extends View{
	
    
    
    private Bitmap tVideoBitmap;
    private Paint tRectPaint;
    private Rect tRectRect;
    private RectF tRectRectF;
    RelativeLayout tRel;

    
	public DesktopVewManagerShowCustomizableBackground(Context context) {
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
