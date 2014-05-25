/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.view.resizable;

public class ResizeConfig {
	public boolean keepRatio;
	public int minWidth;
	public int maxWidth;
	public int minHeight;
	public int maxHeight;
	
	public ResizeConfig copy(){
		ResizeConfig rc = new ResizeConfig();
		rc.keepRatio = keepRatio;
		rc.maxHeight = maxHeight;
		rc.minHeight = minHeight;
		rc.maxWidth = maxWidth;
		return rc;
	}
}
