/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.db.model;

import java.util.List;

import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import ch.hsr.rocketcolibri.view.widget.RCWidgetConfig;
/**
 * @author Artan Veliju
 */
public class RCModel {
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
	
	@Override
	public boolean equals(Object theModel) {
		if (this.getName().equals(((RCModel)theModel).getName())) {
			for (int i = 0; i < widgetConfigs.size(); i++) {
				if (!widgetConfigs.get(i).equals(((RCModel)theModel).getWidgetConfigs().get(i))) {
					return false;
				}
			}
		}
		else {
			return false;
		}

		return true;
	}
}