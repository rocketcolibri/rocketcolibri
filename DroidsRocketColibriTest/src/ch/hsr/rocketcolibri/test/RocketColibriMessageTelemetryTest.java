package ch.hsr.rocketcolibri.test;

import org.json.JSONException;
import org.json.JSONObject;

import ch.hsr.rocketcolibri.protocol.RocketColibriMessageTelemetry;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolFsm;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolFsm.s;
import junit.framework.TestCase;

public class RocketColibriMessageTelemetryTest extends TestCase {

	protected static void setUpBeforeClass() throws Exception {
	}

	protected static void tearDownAfterClass() throws Exception {
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public final void testRocketColibriMessageTelemetry() {
		try {
			RocketColibriMessageTelemetry out = new RocketColibriMessageTelemetry(new JSONObject(
				"{	\"v\" : 1,	\"cmd\" : \"tdc\","+
				"\"sequence\": 1,"+
				"\"activeip\": {\"user\":\"usrA\", \"ip\":\"192.168.1.51\"}," +
				"\"passiveip\": [ {\"user\": \"usrB\", \"ip\":\"192.168.1.50\"},{\"user\":\"usrC\", \"ip\": \"192.168.1.55\"}]}"));
			
			assertTrue("usrA".equals(out.getActiveUser().getName()));
			assertTrue("192.168.1.51".equals(out.getActiveUser().getIpAddress()));
			
		} catch (JSONException e) {
			fail();
		}
	
		}
	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
