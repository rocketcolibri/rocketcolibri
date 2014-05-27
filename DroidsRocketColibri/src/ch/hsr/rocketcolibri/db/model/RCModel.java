/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.db.model;

import java.util.List;

import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
/**
 * @author Artan Veliju
 */
public class RCModel {
	private String name;
	private List<ViewElementConfig> viewElementConfigs;
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ViewElementConfig> getViewElementConfigs() {
		return viewElementConfigs;
	}

	public void setViewElementConfigs(List<ViewElementConfig> viewElementConfigs) {
		this.viewElementConfigs = viewElementConfigs;
	}
	
	@Override
	public boolean equals(Object theModel) {
		if (this.getName().equals(((RCModel)theModel).getName())) {
			for (int i = 0; i < viewElementConfigs.size(); i++) {
				if (!viewElementConfigs.get(i).equals(((RCModel)theModel).getViewElementConfigs().get(i))) {
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