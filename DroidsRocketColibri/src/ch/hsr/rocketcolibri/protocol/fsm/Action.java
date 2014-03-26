package ch.hsr.rocketcolibri.protocol.fsm;

// sources from https://github.com/olibye/guvna

public interface Action<T> 
{
	public void apply(T target, Object event, Object futureState);
}