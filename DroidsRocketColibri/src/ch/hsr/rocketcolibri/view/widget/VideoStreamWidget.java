package ch.hsr.rocketcolibri.view.widget;


import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import ch.hsr.rocketcolibri.view.resizable.ResizeConfig;
import ch.hsr.rocketcolibri.widgetdirectory.UiOutputDataType;
import ch.hsr.rocketcolibri.widgetdirectory.uioutputdata.VideoUrl;
import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.widget.RelativeLayout;

public class VideoStreamWidget extends RCWidget {

	
    private VideoStreamWidgetSurface tVideoSurfaceView;
    
    RelativeLayout tRel;
	
	
	public VideoStreamWidget(Context context, ViewElementConfig elementConfig) 	{
		super(context, elementConfig);
  	  	tVideoSurfaceView = new VideoStreamWidgetSurface(context);
  	  	tVideoSurfaceView.setLayoutParams(elementConfig.getLayoutParams());
	}
	
	@Override
	protected void onLayout (boolean changed, int left, int top, int right, int bottom)
	{	
		tVideoSurfaceView.setLayoutParams(this.getLayoutParams());
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
	
	/**
	 * RocketColibriService sends UiSink change notification with this methods
	 * The Object class depends on the return value of getType
	 * @param data
	 */
	public void onNotifyUiOutputSink(Object data) {
		tVideoSurfaceView.setVideoUrl(((VideoUrl)data).getVideoUrl());
	}
	
	/**
	 * Override this function with the Ui Output data type the widget wants to receive with the onNotifyUiOutputSink method. 
	 * @return type
	 */
	@Override
	public UiOutputDataType getType() {
		return UiOutputDataType.Video;
	}

	@Override
	public View getOperateOverlayView() {
		return tVideoSurfaceView;
	}
	
	public static ViewElementConfig getDefaultViewElementConfig() {
		ResizeConfig rc = new ResizeConfig();
	    rc.maxHeight=600;
	    rc.minHeight=100;
	    rc.maxWidth=800;
	    rc.minWidth=200;
	    LayoutParams lp = new LayoutParams(400, 300 , 100, 100);
	    ViewElementConfig elementConfig = new ViewElementConfig(VideoStreamWidget.class.getName(), lp, rc);
	    return elementConfig;
	}
}
