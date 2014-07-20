package ch.hsr.rocketcolibri.ui_data.output;

/**
 * This interface has to be implemented if the object must be a UiOutputSinkChange observer
 */
public interface IUiOutputSinkChangeObserver {
	
	/**
	 * RocketColibriService sends UiSink change notification with this methods
	 * The Object class depends on the return value of getType
	 * @param data
	 */
	void onNotifyUiOutputSink(Object data);
	
	/**
	 * Override this function with the Ui Output data type the widget wants to receive with the onNotifyUiOutputSink method. 
	 * @return type
	 */
	UiOutputDataType getType();
}
