package ch.hsr.rocketcolibri.db.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LastUpdateFromFile {
	public long timestamp;
	
	public LastUpdateFromFile(){}
	
	public LastUpdateFromFile(long date){
		timestamp = date;
	}
	
	public Date getTimestampAsDate(){
		try {
			return new Date(timestamp);
		} catch (Exception e) {
			try {return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse("11.01.2000 11:11:11");}catch(Exception e2){}
		}
		return null;
	}
}
