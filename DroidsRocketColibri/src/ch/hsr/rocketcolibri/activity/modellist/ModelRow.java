package ch.hsr.rocketcolibri.activity.modellist;
import org.neodatis.odb.OID;

import android.graphics.drawable.Drawable;

public class ModelRow {
	private OID id;
    private Drawable icon;
    private String name;
    private String description;

    public void setId(OID id){
    	this.id = id;
    }
    public OID getId(){
    	return id;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}