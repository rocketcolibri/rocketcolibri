/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.fsm;

// sources from https://github.com/olibye/guvna

public interface Action<T> 
{
	public void apply(T target, Object event, Object futureState);
}