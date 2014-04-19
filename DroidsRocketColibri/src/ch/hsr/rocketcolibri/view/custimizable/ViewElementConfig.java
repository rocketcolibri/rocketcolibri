package ch.hsr.rocketcolibri.view.custimizable;

import ch.hsr.rocketcolibri.view.MyAbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.resizable.ResizeConfig;

public class ViewElementConfig {
	private String tClassPath;
	private LayoutParams tLayoutParams;
	private ResizeConfig tResizeConfig;
	private float tAlpha;
	
	public ViewElementConfig(){}
	
	public ViewElementConfig(String classPath, LayoutParams layoutParams, ResizeConfig resizeConfig){
		tClassPath = classPath;
		tLayoutParams = layoutParams;
		tResizeConfig = resizeConfig;
		tAlpha = 1;
	}

	public String getClassPath() {
		return tClassPath;
	}

	public void setClassPath(String classPath) {
		this.tClassPath = classPath;
	}

	public LayoutParams getLayoutParams(){
		return tLayoutParams;
	}
	
	public ResizeConfig getResizeConfig(){
		return tResizeConfig;
	}

	public void settLayoutParams(LayoutParams layoutParams) {
		tLayoutParams = layoutParams;
	}

	public void settResizeConfig(ResizeConfig resizeConfig) {
		tResizeConfig = resizeConfig;
	}
	
	public void setAlpha(float alpha){
		tAlpha = alpha;
	}
	
	public float getAlpha(){
		return tAlpha;
	}
	
	
}
