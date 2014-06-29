/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.view.custimizable;

import android.app.Service;


/**
 * @author Artan Veliju
 */
public interface ICustomizableView {
	/**
	 * this is the interface method for the view elements to change their face,
	 * so that the user knows he is in a customizable modus
	 * @param enabled
	 */
	void setCustomizeModus(boolean enabled);
	void setModusChangeListener(ModusChangeListener mcl);
	ViewElementConfig getViewElementConfig();
}
