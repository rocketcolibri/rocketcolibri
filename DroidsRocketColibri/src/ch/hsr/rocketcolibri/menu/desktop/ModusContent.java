/**
 * Rocket Colibri © 2014
 * 
 * @author Naxxx
 */
package ch.hsr.rocketcolibri.menu.desktop;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public abstract class ModusContent extends LinearLayout {

	public ModusContent(Context context) {
		super(context);
		onCreate();
	}

	public ModusContent(Context context, AttributeSet attrs) {
		super(context, attrs);
		onCreate();
	}

	public ModusContent(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		onCreate();
	}

	protected abstract void onCreate();

}
