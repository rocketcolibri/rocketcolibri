package db.model;

import java.util.List;

import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;

public class RCModel {
	private String name;
	private List<ViewElementConfig> viewElementConfigs;
	
	public String getName() {
		return name;
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
}
