/**
 * Rocket Colibri © 2014
 * 
 * @author Naxxx
 */
package ch.hsr.rocketcolibri.view;

import android.view.View;

/**
 * @author Artan Veliju
 */
public interface OnHoldListener {
	void onHoldStart(View v, int overallDuration);
	void onHoldEnd(View v);
	void onHoldCanceled();
}
