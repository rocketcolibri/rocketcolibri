/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.widgetdirectory;

import java.lang.reflect.Constructor;

import android.content.Context;
import ch.hsr.rocketcolibri.view.custimizable.CustomizableView;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;

/**
 * Directory entry of a widget class
 * Describes the capabilities of a Widget class
 * Lists all capabilities that are used to add a new widget.
 */
public class WidgetEntry {
	private String tClassPath;
	private String tLabelText;
	private ViewElementConfig tViewElementConfig;
	
	public WidgetEntry(String labelText, String classPath, ViewElementConfig defaultConfig){
		tLabelText = labelText;
		tClassPath = classPath;
		tViewElementConfig = defaultConfig;
	}

	public CustomizableView createWidget(Context context) throws Exception{
		return createWidget(context, getDefaultViewElementConfig());
	}
	
	/**
	 * Get a new widget instance of this Widget
	 * @param context
	 * @param elementConfig
	 * @return widget view
	 * @throws Exception
	 */
	public CustomizableView createWidget(Context context, ViewElementConfig elementConfig) throws Exception{
	    Class<?> c = Class.forName(this.tLabelText);
	    Constructor<?> cons = c.getConstructor(Context.class, ViewElementConfig.class);
	    CustomizableView view = (CustomizableView)cons.newInstance(context, elementConfig);
	    return view;
	}
	
	public String getClassPath(){
		return tClassPath;
	}
	
	public String getLabelText(){
		return tLabelText;
	}
	
	public ViewElementConfig getDefaultViewElementConfig(){
		return tViewElementConfig;
	}
}
