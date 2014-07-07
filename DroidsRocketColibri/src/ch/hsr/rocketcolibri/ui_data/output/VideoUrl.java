/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.ui_data.output;


/**
 * Contains informations about the video stream available from
 * the ServoController 
 */
public class VideoUrl extends UiOutputData 
{
	private String videoUrl;
	
	public VideoUrl() 
	{
		type = UiOutputDataType.Video;
		videoUrl = new String("");
	}
	
	public  synchronized boolean setVideoUrl(String videoUrl)
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
	
	public synchronized  String getVideoUrl()
	{
		return this.videoUrl;
	}
}
