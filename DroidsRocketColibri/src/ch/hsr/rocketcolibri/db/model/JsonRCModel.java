/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.db.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Artan Veliju
 */
public class JsonRCModel {
	@JsonIgnore
	private final String DATE_FORMAT = "dd.MM.yyyy HH:mm:ss";
	public String process;
	public String timestamp;
	public RCModel model;
	
	public void timestampToNow(){
		timestamp = new SimpleDateFormat(DATE_FORMAT).format(new Date());
	}
	
	@JsonIgnore
	public Date getTimestampAsDate(){
		try {
			return new SimpleDateFormat(DATE_FORMAT).parse(timestamp);
		} catch (ParseException e) {
			try {return new SimpleDateFormat(DATE_FORMAT).parse("11.01.2000 11:11:11");}catch(Exception e2){}
		}
		return null;
	}
}
