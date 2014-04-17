package ch.hsr.rocketcolibri.view.resizable.layer;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import ch.hsr.rocketcolibri.view.MyAbsoluteLayout;
import ch.hsr.rocketcolibri.view.resizable.IResizeDoneListener;

public class ResizableBackgroundLayer extends MyAbsoluteLayout{

	public ResizableBackgroundLayer(final Context context, LayoutParams layoutParams) {
		super(context);
		setLayoutParams(layoutParams);
    	setBackgroundColor(Color.BLACK);
    	setAlpha(0.8f);
	}
}
