/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.view.custimizable;

import ch.hsr.rocketcolibri.view.AbsoluteLayout;
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.resizable.ResizeConfig;

/**
 * @author Artan Veliju
 */
public class ViewElementConfig {
	private int id;
	private String classPath;
	private LayoutParams layoutParams;
	private ResizeConfig resizeConfig;
	private float alpha;
	
	public ViewElementConfig(){}
	
	public ViewElementConfig(String classPath, LayoutParams layoutParams, ResizeConfig resizeConfig){
		this(0, classPath, layoutParams, resizeConfig);
	}
	
	public ViewElementConfig(int id, String classPath, LayoutParams layoutParams, ResizeConfig resizeConfig){
		this.id = id;
		this.classPath = classPath;
		this.layoutParams = layoutParams;
		this.resizeConfig = resizeConfig;
		this.alpha = 1;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getClassPath() {
		return classPath;
	}

	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}

	public LayoutParams getLayoutParams(){
		return layoutParams;
	}
	
	public ResizeConfig getResizeConfig(){
		return resizeConfig;
	}

	public void setLayoutParams(LayoutParams layoutParams) {
		this.layoutParams = layoutParams;
	}

	public void settResizeConfig(ResizeConfig resizeConfig) {
		this.resizeConfig = resizeConfig;
	}
	
	public void setAlpha(float alpha){
		this.alpha = alpha;
	}
	
	public float getAlpha(){
		return alpha;
	}
	
	public ViewElementConfig copy(){
		ViewElementConfig vec = new ViewElementConfig();
		vec.setAlpha(alpha);
		vec.setId(id);
		vec.setClassPath(classPath);
		vec.setLayoutParams(new AbsoluteLayout.LayoutParams(layoutParams.width, layoutParams.height, layoutParams.x, layoutParams.y));
		vec.settResizeConfig(resizeConfig.copy());
		return vec;
	}
	
	
}
