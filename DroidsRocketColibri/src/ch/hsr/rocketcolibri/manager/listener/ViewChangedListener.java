/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.manager.listener;

import ch.hsr.rocketcolibri.view.widget.RCWidgetConfig;

/**
 * @author Artan Veliju
 */
public interface ViewChangedListener {
	void onViewAdd(RCWidgetConfig widgetConfig);
	void onViewChange(RCWidgetConfig widgetConfig);
	void onViewDelete(RCWidgetConfig widgetConfig);
}
