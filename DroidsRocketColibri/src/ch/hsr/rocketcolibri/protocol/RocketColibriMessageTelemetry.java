/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.protocol;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.hsr.rocketcolibri.RocketColibriService;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolFsm.e;

public class RocketColibriMessageTelemetry extends RocketColibriMessage {
	final String TAG = this.getClass().getName();		
	private RcOperator tActiveuser = new RcOperator();
	private List<RcOperator> tPassivuser = new ArrayList<RcOperator>() ;
	private int tSequence;
	
	public RocketColibriMessageTelemetry(JSONObject jObject) throws JSONException	{
		// read sequence number
		this.tSequence = jObject.getInt("sequence");	
		// read the active user from message
		if(jObject.has("activeip")) {
			this.tActiveuser.setName(jObject.getJSONObject("activeip").getString("user"));
			this.tActiveuser.setIpAddress(jObject.getJSONObject("activeip").getString("ip"));
		} else{
			this.tActiveuser = null;
		}
		// read the list with the passive users from the message
		JSONArray ja = jObject.getJSONArray("passiveip");
		if(null != ja)	{
			for (int i = 0; i < ja.length(); i++) {
				RcOperator rco = new RcOperator();
				rco.setName(ja.getJSONObject(i).getString("user"));
				rco.setIpAddress(ja.getJSONObject(i).getString("ip"));
				this.tPassivuser.add(rco);
			}
		} else {
			if(null != this.tPassivuser)
				this.tPassivuser.removeAll(tPassivuser);
		}
		
	} 
	
	public RcOperator getActiveUser(){
		return tActiveuser;
	}
	
	public List<RcOperator>getPassivUsers()	{
		return tPassivuser;
	}
	
	public int getSequence() {
		return tSequence;
	}

	@Override
	public boolean equals(Object obj)	{
		RocketColibriMessageTelemetry other = (RocketColibriMessageTelemetry)obj;
		if(null != other) {
			if(this.tActiveuser.equals(other.tActiveuser)) {
				if (this.tPassivuser.equals(other.tPassivuser)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void sendUpdateUiSinkAndSendEvents(RocketColibriService service) {
		service.tUsers.setConnectedUsers(this.tActiveuser, this.tPassivuser);
		if(null == this.tActiveuser)
			service.tProtocolFsm.queue(e.E3_RECV_TELE_NONE);
		else{
			// TODO
			service.tProtocolFsm.queue(e.E3_RECV_TELE_NONE);
		}
		service.tProtocolFsm.processNextEvent();
	}
}
