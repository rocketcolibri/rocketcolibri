package ch.hsr.rocketcolibri.test;

import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolFsm;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolFsm.e;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolFsm.s;
import junit.framework.TestCase;

public class RocketColibriProtocolFsmTest extends TestCase {

	protected static void setUpBeforeClass() throws Exception {
	}

	protected static void tearDownAfterClass() throws Exception {
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public final void testRocketColibriProtocolFsm() {
		RocketColibriProtocolFsm out = new RocketColibriProtocolFsm(s.IDLE);
		assertTrue(out.getState() == s.IDLE);
		
		out.queue(e.E1_RECV_SSID);
		out.processNextEvent();
		assertTrue(out.getState() == s.AVAIL);
		
		out.queue(e.E2_USR_WIFI);
		out.processNextEvent();
		assertTrue(out.getState() == s.TRY_CONN);
		
		out.queue(e.E3_RECV_TELE_NONE);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_PASSIV);
		
		out.queue(e.E6_USR_CONNECT);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_TRY_ACT);
		
		out.queue(e.E5_RECV_TELE_OWN);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_ACT);
	}

}
