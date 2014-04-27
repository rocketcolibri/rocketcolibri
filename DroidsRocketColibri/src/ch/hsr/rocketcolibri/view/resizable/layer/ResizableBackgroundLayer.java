package ch.hsr.rocketcolibri.view.resizable.layer;

import android.content.Context;
import android.graphics.Color;
import ch.hsr.rocketcolibri.view.AbsoluteLayout;

public class ResizableBackgroundLayer extends AbsoluteLayout{

	public ResizableBackgroundLayer(final Context context, LayoutParams layoutParams) {
		super(context);
		setLayoutParams(layoutParams);
    	setBackgroundColor(Color.BLACK);
    	setAlpha(0.8f);
	}
}
