/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.ui_data.output;

import ch.hsr.rocketcolibri.view.widget.RCWidget;

/**
 * This interface must be implemented by a service that serves as a UiSink source
 */
public interface IUiOutputSinkChangeObservable 
{
	public void registerUiOutputSinkChangeObserver(RCWidget observer);
	public void unregisterUiOutputSinkChangeObserver(RCWidget observer);
}
