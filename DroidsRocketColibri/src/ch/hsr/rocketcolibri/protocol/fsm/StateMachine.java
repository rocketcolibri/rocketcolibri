package ch.hsr.rocketcolibri.protocol.fsm;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.util.Log;

//sources from https://github.com/olibye/guvna

/**
 * State machine implementation for RocketColibri protocol
 * 
 * @note Not threadsafe.
 */
public class StateMachine
{
	final String TAG = this.getClass().getName();

	private Object _currentState;
	private StateMachinePlan _plan;
	private Queue<Object> _eventQueue = new LinkedList<Object>();

	public StateMachine(StateMachinePlan plan, Object aStartState) 
	{
		_plan = plan;
		_currentState = aStartState;
	}

	public Object getState() 
	{
		return _currentState;
	}

	/**
	 * @param event will cause a runtime exception
	 */
	public void queue(Object event) 
	{
		if (event == null) {
			throw new IllegalArgumentException("Null events are invalid");
		}
		_eventQueue.add(event);
	}

	private void performTransition(Object nextState, Object event) 
	{
		Action leaveAction = _plan._leaveActions.get(_currentState);
		if (leaveAction != null) 
		{
			leaveAction.apply(this, event, nextState);
		}

		Action entryAction = _plan._entryActions.get(nextState);
		if (entryAction != null) 
		{
			entryAction.apply(this, event, nextState);
		}
		// entry actions performed before state change
		_currentState = nextState;
	}

	/**
	 * 
	 * @return true if more exist
	 */
	public boolean processNextEvent() {
		Object event = _eventQueue.poll();

		// Ignore null events as we can't tell if they're real
		// or indicate the queue is empty
		if(event == null) {
			return hasMoreEvents();
		}
		
		int indexOfEvent = _plan._inputs.indexOf(event);

		if (indexOfEvent >= 0) 
		{
			// don't ignore loopback event in column zero
			//Log.d(TAG, "{} received event type:[{}]" + event);

			List<?> list = _plan._transitions.get(_currentState);
			if (list != null) {
				if (list.size() > indexOfEvent) {
					Object nextState = list.get(indexOfEvent);
					if (nextState != null) {
						performTransition(nextState, event);
						Log.d(TAG, "{} processed event type:[{}]" + event);
					}
					else 
					{
						Log.d(TAG, "{} has no transition for registered event:[{}]" + event);
					}
				} else {
					Log.d(TAG, "{} has no transition for registered event:[{}]" + event);
				}
			} else {
				Log.e(TAG, "Unknown state:[{}]" + _currentState);
			}
		} else {
			Log.e(TAG, "{} received Unknown event type:[{}]" + event);
		}
		return hasMoreEvents();
	}

	public boolean hasMoreEvents() {
		return _eventQueue.peek() != null;
	}

	public void processOutstandingEvents() {
		while (processNextEvent()) {
		}
	}

	@Override
	public String toString() {
		return "StateMachine [_currentState=" + _currentState
				+ ", _eventQueue=" + _eventQueue + "]";
	}
}