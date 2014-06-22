/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri;

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

	public static void dpToPixel(float density, RCWidgetConfig widgetConfig) {
		widgetConfig.viewElementConfig.getLayoutParams().height = (int) (widgetConfig.viewElementConfig.getLayoutParams().height * density + 0.5f);
		widgetConfig.viewElementConfig.getLayoutParams().width = (int) (widgetConfig.viewElementConfig.getLayoutParams().width * density + 0.5f);
		widgetConfig.viewElementConfig.getResizeConfig().maxHeight = (int) (widgetConfig.viewElementConfig.getResizeConfig().maxHeight * density + 0.5f);
		widgetConfig.viewElementConfig.getResizeConfig().minHeight = (int) (widgetConfig.viewElementConfig.getResizeConfig().minHeight * density + 0.5f);
		widgetConfig.viewElementConfig.getResizeConfig().maxWidth = (int) (widgetConfig.viewElementConfig.getResizeConfig().minHeight * density + 0.5f);
		widgetConfig.viewElementConfig.getResizeConfig().minWidth = (int) (widgetConfig.viewElementConfig.getResizeConfig().minWidth * density + 0.5f);
	}
	
}
