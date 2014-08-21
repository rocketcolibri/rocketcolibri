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
		viewElementConfig.getLayoutParams().height = dpToPixelY(density, viewElementConfig.getLayoutParams().height);
		viewElementConfig.getLayoutParams().width = dpToPixelX(density, viewElementConfig.getLayoutParams().width);
		viewElementConfig.getLayoutParams().setX(dpToPixelX(density, viewElementConfig.getLayoutParams().getX()));
		viewElementConfig.getLayoutParams().setY(dpToPixelY(density, viewElementConfig.getLayoutParams().getY()));
		viewElementConfig.getResizeConfig().maxHeight = dpToPixelY(density, viewElementConfig.getResizeConfig().maxHeight);
		viewElementConfig.getResizeConfig().minHeight = dpToPixelY(density, viewElementConfig.getResizeConfig().minHeight);
		viewElementConfig.getResizeConfig().maxWidth = dpToPixelX(density, viewElementConfig.getResizeConfig().maxWidth);
		viewElementConfig.getResizeConfig().minWidth = dpToPixelX(density, viewElementConfig.getResizeConfig().minWidth);
	}
	
	public static void pixelToDp(DisplayMetrics density, ViewElementConfig viewElementConfig) {
		viewElementConfig.getLayoutParams().height = pixelToDpY(density, viewElementConfig.getLayoutParams().height);
		viewElementConfig.getLayoutParams().width = pixelToDpX(density, viewElementConfig.getLayoutParams().width);
		viewElementConfig.getLayoutParams().setX(pixelToDpX(density, viewElementConfig.getLayoutParams().getX()));
		viewElementConfig.getLayoutParams().setY(pixelToDpY(density, viewElementConfig.getLayoutParams().getY()));
		viewElementConfig.getResizeConfig().maxHeight = pixelToDpY(density, viewElementConfig.getResizeConfig().maxHeight);
		viewElementConfig.getResizeConfig().minHeight = pixelToDpY(density, viewElementConfig.getResizeConfig().minHeight);
		viewElementConfig.getResizeConfig().maxWidth = pixelToDpX(density, viewElementConfig.getResizeConfig().maxWidth);
		viewElementConfig.getResizeConfig().minWidth = pixelToDpX(density, viewElementConfig.getResizeConfig().minWidth);
	}
	
	private static int calcCompatiblePosition(int target, int origin, int current){
		double or = origin;double tar = target;double cur = current;
		System.out.println("target "+target+" /"+ " orgigin: "+origin+" current: "+current);
		return (int) (tar/or*cur);
	}
	
	public static int pixelToDpX(DisplayMetrics dMetrics, int px) {
	    int dp = Math.round(px / (dMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
	    return dp;
	}
	
	public static int pixelToDpY(DisplayMetrics dMetrics, int px) {
	    int dp = Math.round(px / (dMetrics.ydpi / DisplayMetrics.DENSITY_DEFAULT));
	    return dp;
	}
	
	public static int dpToPixelX(DisplayMetrics dMetrics, int dp) {
	    int px = Math.round(dp * (dMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));       
	    return px;
	}
	
	public static int dpToPixelY(DisplayMetrics dMetrics, int dp) {
	    int px = Math.round(dp * (dMetrics.ydpi / DisplayMetrics.DENSITY_DEFAULT));       
	    return px;
	}
	
}
