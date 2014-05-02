package ch.hsr.rocketcolibri.widgetdirectory.uisinkdata;

import java.util.concurrent.BlockingQueue;

import ch.hsr.rocketcolibri.widgetdirectory.RCUiSinkType;

public class VideoUrl extends UiSinkData 
{
	private String videoUrl;
	
	public VideoUrl(BlockingQueue<UiSinkData> queue) {
		super(queue);
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
