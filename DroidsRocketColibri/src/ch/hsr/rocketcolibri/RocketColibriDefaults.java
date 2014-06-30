/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri;

import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;

/**
 * @author Artan Veliju
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class RocketColibriDefaults {
	
	public static void setDefaultViewSettings(View view){
		view.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		
//        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//      | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//      | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//      | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
//      | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
//      | View.SYSTEM_UI_FLAG_IMMERSIVE);
	}

	public static void dpToPixel(float density, ViewElementConfig viewElementConfig) {
		viewElementConfig.getLayoutParams().height = dpToPixel(density, viewElementConfig.getLayoutParams().height);
		viewElementConfig.getLayoutParams().width = dpToPixel(density, viewElementConfig.getLayoutParams().width);
		viewElementConfig.getLayoutParams().x = dpToPixel(density, viewElementConfig.getLayoutParams().x);
		viewElementConfig.getLayoutParams().y = dpToPixel(density, viewElementConfig.getLayoutParams().y);
		viewElementConfig.getResizeConfig().maxHeight = dpToPixel(density, viewElementConfig.getResizeConfig().maxHeight);
		viewElementConfig.getResizeConfig().minHeight = dpToPixel(density, viewElementConfig.getResizeConfig().minHeight);
		viewElementConfig.getResizeConfig().maxWidth = dpToPixel(density, viewElementConfig.getResizeConfig().maxWidth);
		viewElementConfig.getResizeConfig().minWidth = dpToPixel(density, viewElementConfig.getResizeConfig().minWidth);
	}
	
	public static int dpToPixel(float density, int pixel){
		return (int) (pixel * density + 0.5f);
	}
	
}
