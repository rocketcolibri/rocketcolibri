/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri;

import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import ch.hsr.rocketcolibri.view.widget.RCWidgetConfig;
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
		viewElementConfig.getLayoutParams().height = (int) (viewElementConfig.getLayoutParams().height * density + 0.5f);
		viewElementConfig.getLayoutParams().width = (int) (viewElementConfig.getLayoutParams().width * density + 0.5f);
		viewElementConfig.getLayoutParams().x = (int) (viewElementConfig.getLayoutParams().x * density + 0.5f);
		viewElementConfig.getLayoutParams().y = (int) (viewElementConfig.getLayoutParams().y * density + 0.5f);
		viewElementConfig.getResizeConfig().maxHeight = (int) (viewElementConfig.getResizeConfig().maxHeight * density + 0.5f);
		viewElementConfig.getResizeConfig().minHeight = (int) (viewElementConfig.getResizeConfig().minHeight * density + 0.5f);
		viewElementConfig.getResizeConfig().maxWidth = (int) (viewElementConfig.getResizeConfig().maxWidth * density + 0.5f);
		viewElementConfig.getResizeConfig().minWidth = (int) (viewElementConfig.getResizeConfig().minWidth * density + 0.5f);
	}
	
	public static int dpToPixel(float density, int pixel){
		return (int) (pixel * density + 0.5f);
	}
	
}
