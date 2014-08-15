package ch.hsr.rocketcolibri.ui_data.input;

import java.util.List;

public interface IUiInputSource {
	/**
	 * Override this function and return all Channel objects that are assigned to this Widget
	 * @return List<Channels>
	 */
	public List<UiInputSourceChannel> getUiInputSourceList();
}
