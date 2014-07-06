/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri;

import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import android.annotation.TargetApi;
import android.os.Build;
import android.util.DisplayMetrics;
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

	public static void dpToPixel(DisplayMetrics density, ViewElementConfig viewElementConfig) {
		viewElementConfig.getLayoutParams().height = dpToPixel(density, viewElementConfig.getLayoutParams().height);
		viewElementConfig.getLayoutParams().width = dpToPixel(density, viewElementConfig.getLayoutParams().width);
		viewElementConfig.getLayoutParams().x = dpToPixel(density, viewElementConfig.getLayoutParams().x);
		viewElementConfig.getLayoutParams().y = dpToPixel(density, viewElementConfig.getLayoutParams().y);
		viewElementConfig.getResizeConfig().maxHeight = dpToPixel(density, viewElementConfig.getResizeConfig().maxHeight);
		viewElementConfig.getResizeConfig().minHeight = dpToPixel(density, viewElementConfig.getResizeConfig().minHeight);
		viewElementConfig.getResizeConfig().maxWidth = dpToPixel(density, viewElementConfig.getResizeConfig().maxWidth);
		viewElementConfig.getResizeConfig().minWidth = dpToPixel(density, viewElementConfig.getResizeConfig().minWidth);
	}
	
	public static void pixelToDp(DisplayMetrics density, ViewElementConfig viewElementConfig) {
		viewElementConfig.getLayoutParams().height = pixelToDp(density, viewElementConfig.getLayoutParams().height);
		viewElementConfig.getLayoutParams().width = pixelToDp(density, viewElementConfig.getLayoutParams().width);
		viewElementConfig.getLayoutParams().x = pixelToDp(density, viewElementConfig.getLayoutParams().x);
		viewElementConfig.getLayoutParams().y = pixelToDp(density, viewElementConfig.getLayoutParams().y);
		viewElementConfig.getResizeConfig().maxHeight = pixelToDp(density, viewElementConfig.getResizeConfig().maxHeight);
		viewElementConfig.getResizeConfig().minHeight = pixelToDp(density, viewElementConfig.getResizeConfig().minHeight);
		viewElementConfig.getResizeConfig().maxWidth = pixelToDp(density, viewElementConfig.getResizeConfig().maxWidth);
		viewElementConfig.getResizeConfig().minWidth = pixelToDp(density, viewElementConfig.getResizeConfig().minWidth);
	}

	
	public static int pixelToDp(DisplayMetrics dMetrics, int px) {
	    int dp = Math.round(px / (dMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
	    return dp;
	}
	
	public static int dpToPixel(DisplayMetrics dMetrics, int dp) {
	    int px = Math.round(dp * (dMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));       
	    return px;
	}
	
}
