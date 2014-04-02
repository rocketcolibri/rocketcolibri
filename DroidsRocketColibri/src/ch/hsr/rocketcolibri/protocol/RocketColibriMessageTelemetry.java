package ch.hsr.rocketcolibri.protocol;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RocketColibriMessageTelemetry extends RocketColibriMessage
{
	private RcOperator activeuser = new RcOperator();
	private List<RcOperator> passivuser = new ArrayList<RcOperator>() ;
	private int sequence;
	
	final String TAG = this.getClass().getName();
	public RocketColibriMessageTelemetry(JSONObject jObject) throws JSONException
	{
		// read sequence number
		this.sequence = jObject.getInt("sequence");
		
		// read the active user from message
		this.activeuser.setName(jObject.getJSONObject("activeip").getString("user"));
		this.activeuser.setIpAddress(jObject.getJSONObject("activeip").getString("ip"));
		
		// read the list with the passive users from the message
		JSONArray ja = jObject.getJSONArray("passiveip");
		for (int i = 0; i < ja.length(); i++) 
		{
			RcOperator rco = new RcOperator();
			rco.setName(ja.getJSONObject(i).getString("user"));
			rco.setIpAddress(ja.getJSONObject(i).getString("ip"));
			this.passivuser.add(rco);
		}
	} 
	
	public RcOperator getActiveUser()
	{
		return activeuser;
	}
	
	public List<RcOperator>getPassivUsers()
	{
		return passivuser;
	}
	
	public int getSequence()
	{
		return sequence;
	}
}
