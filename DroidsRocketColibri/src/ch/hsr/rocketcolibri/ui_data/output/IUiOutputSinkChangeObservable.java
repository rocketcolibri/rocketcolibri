/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.ui_data.output;

/**
 * This interface must be implemented by a service that serves as a UiSink source
 */
public interface IUiOutputSinkChangeObservable 
{
	public void registerUiOutputSinkChangeObserver(IUiOutputSinkChangeObserver observer);
	public void unregisterUiOutputSinkChangeObserver(IUiOutputSinkChangeObserver observer);
}
