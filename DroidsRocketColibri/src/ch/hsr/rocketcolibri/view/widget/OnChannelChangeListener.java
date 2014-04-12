package ch.hsr.rocketcolibri.view.widget;


public interface OnChannelChangeListener
{
	/**
	 * is called whenever the channel position changes
	 * @param position channel position with a range between 0 and 1000
	 */
	public void onChannelChange(int position);
}
