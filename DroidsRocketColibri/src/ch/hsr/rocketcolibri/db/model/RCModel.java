/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.db.model;

import java.util.ArrayList;
import java.util.List;

import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import ch.hsr.rocketcolibri.view.widget.RCWidgetConfig;
/**
 * @author Artan Veliju
 */
public class RCModel {
//	private String iconPath;
	private String name;
	private List<RCWidgetConfig> widgetConfigs;
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<RCWidgetConfig> getWidgetConfigs() {
		return widgetConfigs;
	}

	public void setWidgetConfigs(List<RCWidgetConfig> viewElementConfigs) {
		this.widgetConfigs = viewElementConfigs;
	}
	
	public RCModel copy(){
		RCModel copy = new RCModel();
		copy.setName(name);
		if(widgetConfigs!=null && widgetConfigs.size()>0){
			List<RCWidgetConfig> configCopyList = new ArrayList<RCWidgetConfig>(widgetConfigs.size());
			for(RCWidgetConfig config : widgetConfigs){
				configCopyList.add(config.copy());
			}
			copy.setWidgetConfigs(configCopyList);
		}else{
			copy.setWidgetConfigs(new ArrayList<RCWidgetConfig>(0));
		}
		return copy;
	}
	
	@Override
	public boolean equals(Object theModel) {
		try{
			if (this.getName().equals(((RCModel)theModel).getName())) {
				if(widgetConfigs==null){
					if(((RCModel)theModel).getWidgetConfigs()!=null)
						return false;
					return true;
				}
				for (int i = 0; i < widgetConfigs.size(); i++) {
					if (!widgetConfigs.get(i).equals(((RCModel)theModel).getWidgetConfigs().get(i))) {
						return false;
					}
				}
			}
			else {
				return false;
			}
		}catch(Exception e){
			return false;
		}
		return true;
	}
}