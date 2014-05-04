/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.widgetdirectory.uisinkdata;

import ch.hsr.rocketcolibri.widgetdirectory.RCUiSinkType;

public class VideoUrl extends UiSinkData 
{
	private String videoUrl;
	
	public VideoUrl() 
	{
		type = RCUiSinkType.Video;
	}
	
	public boolean setVideoUrl(String videoUrl)
	{
		if (!videoUrl.equals(this.videoUrl))
		{
			this.videoUrl = videoUrl;
			this.notifyThis();
			return true;
		}
		else
			return false;
	}
	
	public String getVideoUrl()
	{
		return this.videoUrl;
	}
}
