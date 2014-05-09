/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.widgetdirectory.uioutputdata;

import ch.hsr.rocketcolibri.widgetdirectory.UiOutputDataType;

public class VideoUrl extends UiOutputData 
{
	private String videoUrl;
	
	public VideoUrl() 
	{
		type = UiOutputDataType.Video;
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
