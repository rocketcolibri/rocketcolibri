package ch.hsr.rocketcolibri.widgetservice;

public class WidgetDirectoryEntry {
	/**
	 * Description
	 */
	String description;
	
	/**
	 * Name of the class
	 */
	String className;
	
	/**
	 * 
	 */
	String range;
	
	public WidgetDirectoryEntry(String className, String description)
	{
		this.className = className;
		this.description = description;
	}

	public WidgetDirectoryEntry(String className, String description, String range)
	{
		this.className = className;
		this.description = description;
		this.range = range;
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
