package ch.hsr.rocketcolibri.test;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import ch.hsr.rocketcolibri.protocol.RcOperator;
import ch.hsr.rocketcolibri.protocol.RocketColibriMessageTelemetry;
import junit.framework.TestCase;

public class RocketColibriMessageTelemetryTest extends TestCase {

	protected static void setUpBeforeClass() throws Exception {
	}

	protected static void tearDownAfterClass() throws Exception {
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public final void testRocketColibriMessageTelemetryActiveUser() 
	{
		try 
		{
			RocketColibriMessageTelemetry out = new RocketColibriMessageTelemetry(new JSONObject(
				"{	\"v\" : 1,	\"cmd\" : \"tdc\","+
				"\"sequence\": 1,"+
				"\"activeip\": {\"user\":\"usrA\", \"ip\":\"192.168.1.51\"}," +
				"\"passiveip\": [ {\"user\": \"usrB\", \"ip\":\"192.168.1.50\"},{\"user\":\"usrC\", \"ip\": \"192.168.1.55\"}]}"));
			
			assertTrue("usrA".equals(out.getActiveUser().getName()));
			assertTrue("192.168.1.51".equals(out.getActiveUser().getIpAddress()));
			
		}
		catch (JSONException e) 
		{
			fail();
		}
	}
	
	
	public final void testRocketColibriMessageTelemetryPassivUser()
	{
		try 
		{
			RocketColibriMessageTelemetry out = new RocketColibriMessageTelemetry(new JSONObject(
				"{	\"v\" : 1,	\"cmd\" : \"tdc\","+
				"\"sequence\": 1,"+
				"\"activeip\": {\"user\":\"usrA\", \"ip\":\"192.168.1.51\"}," +
				"\"passiveip\": [ {\"user\": \"usrB\", \"ip\":\"192.168.1.50\"},{\"user\":\"usrC\", \"ip\": \"192.168.1.55\"}]}"));
	
			List<RcOperator> pu = out.getPassivUsers();
			assertTrue(pu != null);
			assertTrue(2 == pu.size() );
			assertTrue("usrB".equals(pu.get(0).getName()));
			assertTrue("192.168.1.50".equals(pu.get(0).getIpAddress()));
			assertTrue("usrC".equals(pu.get(1).getName()));
			assertTrue("192.168.1.55".equals(pu.get(1).getIpAddress()));
		} 
		catch (JSONException e) 
		{
			fail();
		}
	}

	public final void testRocketColibriMessageTelemetryPassivSequence()
	{
		try
		{
			RocketColibriMessageTelemetry out = new RocketColibriMessageTelemetry(new JSONObject(
				"{	\"v\" : 1,	\"cmd\" : \"tdc\","+
				"\"sequence\": 1,"+
				"\"activeip\": {\"user\":\"usrA\", \"ip\":\"192.168.1.51\"}," +
				"\"passiveip\": [ {\"user\": \"usrB\", \"ip\":\"192.168.1.50\"},{\"user\":\"usrC\", \"ip\": \"192.168.1.55\"}]}"));
	
			assertTrue(1 == out.getSequence());
		} 
		catch (JSONException e) 
		{
			fail();
		}
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
