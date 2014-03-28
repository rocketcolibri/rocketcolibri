package ch.hsr.rocketcolibri.protocol;

import org.json.JSONException;
import org.json.JSONObject;

public class RocketColibriMessageTelemetry extends RocketColibriMessage
{
	private RcOperator activeuser = new RcOperator();
	
	final String TAG = this.getClass().getName();
	public RocketColibriMessageTelemetry(JSONObject jObject) throws JSONException
	{
		this.activeuser.setName(jObject.getJSONObject("activeip").getString("user"));
		this.activeuser.setIpAddress(jObject.getJSONObject("activeip").getString("ip"));
	} 
	
	public RcOperator getActiveUser()
	{
		return activeuser;
	}
}
