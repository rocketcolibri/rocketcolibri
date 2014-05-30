/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri;

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
	
}
