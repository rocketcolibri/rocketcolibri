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
		rc.setKeepRatio(this.keepRatio);
		rc.setMaxHeight(this.maxHeight);
		rc.setMinHeight(this.minHeight);
		rc.setMaxWidth(this.maxWidth);
		rc.setMinWidth(this.minWidth);

		return rc;
	}

	public boolean isKeepRatio() {
		return this.keepRatio;
	}

	public void setKeepRatio(boolean keepRatio) {
		this.keepRatio = keepRatio;
	}

	public int getMinWidth() {
		return this.minWidth;
	}

	public void setMinWidth(int minWidth) {
		this.minWidth = minWidth;
	}

	public int getMaxWidth() {
		return this.maxWidth;
	}

	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	public int getMinHeight() {
		return this.minHeight;
	}

	public void setMinHeight(int minHeight) {
		this.minHeight = minHeight;
	}

	public int getMaxHeight() {
		return this.maxHeight;
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}

	/**
	 * Comparing two ResizeConfig objects
	 *
	 * @param theConfig object to compare with this
	 * @return returns true if all members are equal
	 */
	public boolean equals(ResizeConfig theConfig) {
		if (this.isKeepRatio() != theConfig.isKeepRatio()) {
			return false;
		}

		if (this.getMinWidth() != theConfig.getMinWidth()) {
			return false;
		}

		if (this.getMaxWidth() != theConfig.getMaxWidth()) {
			return false;
		}

		if (this.getMinHeight() != theConfig.getMinHeight()) {
			return false;
		}

		if (this.getMaxHeight() != theConfig.getMaxHeight()) {
			return false;
		}

		return true;
	}
}