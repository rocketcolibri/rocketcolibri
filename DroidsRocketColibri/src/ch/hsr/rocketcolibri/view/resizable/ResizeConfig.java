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
		rc.keepRatio = this.keepRatio;
		rc.maxHeight = this.maxHeight;
		rc.minHeight = this.minHeight;
		rc.maxWidth = this.maxWidth;
		rc.minWidth = this.minWidth;

		return rc;
	}

	/**
	 * Comparing two ResizeConfig objects
	 *
	 * @param theConfig object to compare with this
	 * @return returns true if all members are equal
	 */
	public boolean equals(ResizeConfig theConfig) {
		if (this.keepRatio != theConfig.keepRatio) {
			return false;
		}

		if (this.minWidth != theConfig.minWidth) {
			return false;
		}

		if (this.maxWidth != theConfig.maxWidth) {
			return false;
		}

		if (this.minHeight != theConfig.minHeight) {
			return false;
		}

		if (this.maxHeight != theConfig.maxHeight) {
			return false;
		}

		return true;
	}
}