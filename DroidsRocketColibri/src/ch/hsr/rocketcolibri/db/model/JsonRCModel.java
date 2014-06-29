/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.db.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Artan Veliju
 */
public class JsonRCModel {
	public String process;
	public String timestamp;
	public RCModel model;
	
	public Date getTimestampAsDate(){
		try {
			return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse(timestamp);
		} catch (ParseException e) {
			try {return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse("11.01.2000 11:11:11");}catch(Exception e2){}
		}
		return null;
	}
}
