package ch.hsr.rocketcolibri.view.widget;

import java.util.List;
import java.util.Map;

import android.view.View.OnTouchListener;
import ch.hsr.rocketcolibri.ui_data.input.UiInputSourceChannel;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;

public interface IRCWidget {
    /**This Method will be called on loading the whole Model at startup*/
    void create(RCWidgetConfig rcWidgetConfig);
    
    /**This Method will be called on runtime in customize Mode by creating a new Widget*/
    void create(ViewElementConfig vElementConfig);
    
    /**
     * This Method sets the listener for the Customize-Modus, this listener is not active
     * in the Control-Modus, the reference of it should be kept in the Class that implements
     * this interface.
     * */
	void setCustomizeModusListener(OnTouchListener customizeModusListener);
	
   	/**
	 * This Method is called from the DesktopViewManager on any change of the WidgetConfig
	 * to keep the config synchronized with the Database.
	 * @return
	 */
	RCWidgetConfig getWidgetConfig();
	
	/**
	 * This Method is called on clicking on Edit Channel in the Customize-Modus Popup-Menu
	 * to create the Edit-View with the Fields needed for this Widget
	 */
	Map<String, String> getProtocolMap();
	
	/**
	 * This Method Overrides ProtocolMap and calls updateProtocolMap after.
	 */
	void setProtocolMap(Map<String, String> protocolMap);
	
	/** 
	 * This Method is for implementing the update process after the ProtocolMap is changed.
	 */
	void updateProtocolMap();


	/**
	 * Override this function and return all Channel objects that are assigned to this Widget
	 * @return List<Channels>
	 */
	public List<UiInputSourceChannel> getUiInputSourceList();
	
	void setCustomizeModus(boolean tCustomizeModus);

}
