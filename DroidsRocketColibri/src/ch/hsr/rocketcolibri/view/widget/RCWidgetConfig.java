package ch.hsr.rocketcolibri.view.widget;

import java.util.HashMap;
import java.util.Map;

import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;

public class RCWidgetConfig {
	public Map<String, String> protocolMap;
	public ViewElementConfig viewElementConfig;
	
	/** for the json handler */
	public RCWidgetConfig(){}
	
	public RCWidgetConfig(ViewElementConfig vec){
		viewElementConfig = vec;
	}

	public RCWidgetConfig(Map<String, String> pm, ViewElementConfig vec){
		protocolMap = pm;
		viewElementConfig = vec;
	}
	
	public RCWidgetConfig copy(){
		return new RCWidgetConfig(new HashMap<String, String>(protocolMap), viewElementConfig.copy());
	}
}
