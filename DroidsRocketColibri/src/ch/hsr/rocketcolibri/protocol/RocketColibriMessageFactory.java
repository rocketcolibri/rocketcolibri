package ch.hsr.rocketcolibri.protocol;

import java.net.DatagramPacket;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Reads the RocketColibri protocol header and returns a RocketColibriMessage object.
 */
public class RocketColibriMessageFactory 
{
	final String TAG = this.getClass().getName();
	RocketColibriMessage Create(DatagramPacket datagram, RocketColibriMessage lastMessage)
	{
		String jsonStr = new String( datagram.getData(), 0, datagram.getLength() );
    	Log.d(TAG,"received: " + jsonStr);
    	try
    	{
    		// read message header
    		JSONObject jObject = new JSONObject(jsonStr);
    		if ( 1 == jObject.getInt("v"))
    		{
    			String cmd = jObject.getString("cmd");
    			if(cmd.equals("tdc"))
    			{
    				Log.d(TAG,"received telemetry");
    				
    				RocketColibriMessageTelemetry newMessage = new RocketColibriMessageTelemetry(jObject);
    				if(newMessage.equals(lastMessage))
    					return lastMessage;
    				else
    					return newMessage;
    			}
    		}
		} 
    	catch (JSONException e)
    	{
			Log.d(TAG, e.toString());
		}
    	return null;	 
	}
}
