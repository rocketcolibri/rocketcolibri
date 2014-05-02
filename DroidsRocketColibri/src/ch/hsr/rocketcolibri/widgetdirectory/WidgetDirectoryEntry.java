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
public class WidgetDirectoryEntry 
{
	/**
	 * Description
	 */
	String description;
	
	/**
	 * Name of the class
	 */
	String className;
	
	public WidgetDirectoryEntry(String className, String description)
	{
		this.className = className;
		this.description = description;
	}

	/**
	 * Get a new widget instance of this WidgetDirectoryEntry
	 * @param context
	 * @param elementConfig
	 * @return widget view
	 * @throws Exception
	 */
	public CustomizableView createWidget(Context context, ViewElementConfig elementConfig) throws Exception
	{
	    Class<?> c = Class.forName(this.className);
	    Constructor<?> cons = c.getConstructor(Context.class, ViewElementConfig.class);
	    CustomizableView view = (CustomizableView)cons.newInstance(context, elementConfig);
	    return view;
	}
	
	public String getDescription()
	{
		return this.description;
	}
	
	public void setDescription(String description) 
	{
		this.description = description;
	}
	
}
