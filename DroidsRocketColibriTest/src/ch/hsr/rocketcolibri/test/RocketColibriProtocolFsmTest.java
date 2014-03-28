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

	public final void testRocketColibriProtocolFsmTransitionsFromStateIdle() {
		RocketColibriProtocolFsm out = new RocketColibriProtocolFsm(s.IDLE);
		assertTrue(out.getState() == s.IDLE);
		out.queue(e.E2_USR_WIFI);
		out.processNextEvent();
		assertTrue(out.getState() == s.IDLE);
		out.queue(e.E3_RECV_TELE_NONE);
		out.processNextEvent();
		assertTrue(out.getState() == s.IDLE);
		out.queue(e.E4_RECV_TELE_ALIEN);
		out.processNextEvent();
		assertTrue(out.getState() == s.IDLE);
		out.queue(e.E5_RECV_TELE_OWN);
		out.processNextEvent();
		assertTrue(out.getState() == s.IDLE);
		out.queue(e.E6_USR_CONNECT);
		out.processNextEvent();
		assertTrue(out.getState() == s.IDLE);
		out.queue(e.E7_USR_OBSERVE);
		out.processNextEvent();
		assertTrue(out.getState() == s.IDLE);
		out.queue(e.E8_TIMEOUT);
		out.processNextEvent();
		assertTrue(out.getState() == s.IDLE);		
		
		// transitions to other states
		out.queue(e.E1_RECV_SSID);
		out.processNextEvent();
		assertTrue(out.getState() == s.AVAIL);
	}

	public final void testRocketColibriProtocolFsmTransitionsFromStateAvail() 
	{
		RocketColibriProtocolFsm out = new RocketColibriProtocolFsm(s.AVAIL);
		assertTrue(out.getState() == s.AVAIL);
		out.queue(e.E1_RECV_SSID);
		out.processNextEvent();
		assertTrue(out.getState() == s.AVAIL);
		out.queue(e.E3_RECV_TELE_NONE);
		out.processNextEvent();
		assertTrue(out.getState() == s.AVAIL);
		out.queue(e.E4_RECV_TELE_ALIEN);
		out.processNextEvent();
		assertTrue(out.getState() == s.AVAIL);
		out.queue(e.E5_RECV_TELE_OWN);
		out.processNextEvent();
		assertTrue(out.getState() == s.AVAIL);
		out.queue(e.E6_USR_CONNECT);
		out.processNextEvent();
		assertTrue(out.getState() == s.AVAIL);
		out.queue(e.E7_USR_OBSERVE);
		out.processNextEvent();
		assertTrue(out.getState() == s.AVAIL);
		
		// transitions to other states
		out.queue(e.E2_USR_WIFI);
		out.processNextEvent();
		assertTrue(out.getState() == s.TRY_CONN);

		out = new RocketColibriProtocolFsm(s.AVAIL);
		assertTrue(out.getState() == s.AVAIL);
		out.queue(e.E8_TIMEOUT);
		out.processNextEvent();
		assertTrue(out.getState() == s.IDLE);		

	}

	
	public final void testRocketColibriProtocolFsmTransitionsFromStateTryConnect() 
	{
		RocketColibriProtocolFsm out = new RocketColibriProtocolFsm(s.TRY_CONN);
		assertTrue(out.getState() == s.TRY_CONN);
		out.queue(e.E1_RECV_SSID);
		out.processNextEvent();
		assertTrue(out.getState() == s.TRY_CONN);
		out.queue(e.E2_USR_WIFI);
		out.processNextEvent();
		assertTrue(out.getState() == s.TRY_CONN);
		out.queue(e.E5_RECV_TELE_OWN);
		out.processNextEvent();
		assertTrue(out.getState() == s.TRY_CONN);
		out.queue(e.E6_USR_CONNECT);
		out.processNextEvent();
		assertTrue(out.getState() == s.TRY_CONN);
		out.queue(e.E7_USR_OBSERVE);
		out.processNextEvent();
		assertTrue(out.getState() == s.TRY_CONN);
		
		// transitions to other states
		out.queue(e.E4_RECV_TELE_ALIEN);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_LCK_OUT);

		
		out = new RocketColibriProtocolFsm(s.TRY_CONN);
		assertTrue(out.getState() == s.TRY_CONN);
		out.queue(e.E3_RECV_TELE_NONE);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_PASSIV);
		
		out = new RocketColibriProtocolFsm(s.TRY_CONN);
		assertTrue(out.getState() == s.TRY_CONN);
		out.queue(e.E8_TIMEOUT);
		out.processNextEvent();
		assertTrue(out.getState() == s.IDLE);
	}
	
	public final void testRocketColibriProtocolFsmTransitionsFromStateConnectionLockedOut() {
		RocketColibriProtocolFsm out = new RocketColibriProtocolFsm(s.CONN_LCK_OUT);
		assertTrue(out.getState() == s.CONN_LCK_OUT);
		out.queue(e.E1_RECV_SSID);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_LCK_OUT);
		out.queue(e.E2_USR_WIFI);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_LCK_OUT);
		out.queue(e.E4_RECV_TELE_ALIEN);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_LCK_OUT);
		out.queue(e.E5_RECV_TELE_OWN);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_LCK_OUT);
		out.queue(e.E6_USR_CONNECT);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_LCK_OUT);
		out.queue(e.E7_USR_OBSERVE);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_LCK_OUT);
	
		// transitions to other states
		out =  new RocketColibriProtocolFsm(s.CONN_LCK_OUT);
		out.queue(e.E3_RECV_TELE_NONE);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_PASSIV);
		
		out =  new RocketColibriProtocolFsm(s.CONN_LCK_OUT);
		out.queue(e.E8_TIMEOUT);
		out.processNextEvent();
		assertTrue(out.getState() == s.IDLE);
	}
	
	public final void testRocketColibriProtocolFsmTransitionsFromStateConnectionPassive() {
		RocketColibriProtocolFsm out = new RocketColibriProtocolFsm(s.CONN_PASSIV);
		assertTrue(out.getState() == s.CONN_PASSIV);
		out.queue(e.E1_RECV_SSID);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_PASSIV);
		out.queue(e.E2_USR_WIFI);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_PASSIV);
		out.queue(e.E3_RECV_TELE_NONE);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_PASSIV);
		out.queue(e.E5_RECV_TELE_OWN);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_PASSIV);
		out.queue(e.E7_USR_OBSERVE);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_PASSIV);
		
		
		// transitions to other states
		out =  new RocketColibriProtocolFsm(s.CONN_PASSIV);
		out.queue(e.E4_RECV_TELE_ALIEN);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_LCK_OUT);

		out =  new RocketColibriProtocolFsm(s.CONN_PASSIV);
		out.queue(e.E6_USR_CONNECT);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_TRY_ACT);

		out =  new RocketColibriProtocolFsm(s.CONN_PASSIV);
		out.queue(e.E8_TIMEOUT);
		out.processNextEvent();
		assertTrue(out.getState() == s.IDLE);
	}
	
	public final void testRocketColibriProtocolFsmTransitionsFromStateConnectionTryActive() {
		RocketColibriProtocolFsm out = new RocketColibriProtocolFsm(s.CONN_TRY_ACT);
		assertTrue(out.getState() == s.CONN_TRY_ACT);
		out.queue(e.E1_RECV_SSID);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_TRY_ACT);
		out.queue(e.E2_USR_WIFI);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_TRY_ACT);
		out.queue(e.E3_RECV_TELE_NONE);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_TRY_ACT);
		out.queue(e.E6_USR_CONNECT);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_TRY_ACT);

		
		// transitions to other states
		out =  new RocketColibriProtocolFsm(s.CONN_TRY_ACT);
		out.queue(e.E4_RECV_TELE_ALIEN);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_LCK_OUT);

		out =  new RocketColibriProtocolFsm(s.CONN_TRY_ACT);
		out.queue(e.E5_RECV_TELE_OWN);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_ACT);

		out =  new RocketColibriProtocolFsm(s.CONN_TRY_ACT);
		out.queue(e.E7_USR_OBSERVE);
		out.processNextEvent();
		assertTrue(out.getState() == s.TRY_CONN);
		
		out =  new RocketColibriProtocolFsm(s.CONN_TRY_ACT);
		out.queue(e.E8_TIMEOUT);
		out.processNextEvent();
		assertTrue(out.getState() == s.IDLE);
	}
	
	public final void testRocketColibriProtocolFsmTransitionsFromStateConnectionActive() {
		RocketColibriProtocolFsm out = new RocketColibriProtocolFsm(s.CONN_ACT);
		assertTrue(out.getState() == s.CONN_ACT);
		out.queue(e.E1_RECV_SSID);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_ACT);
		out.queue(e.E2_USR_WIFI);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_ACT);
		out.queue(e.E3_RECV_TELE_NONE);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_ACT);
		out.queue(e.E4_RECV_TELE_ALIEN);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_ACT);
		out.queue(e.E5_RECV_TELE_OWN);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_ACT);
		out.queue(e.E6_USR_CONNECT);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_ACT);
		out.queue(e.E8_TIMEOUT);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_ACT);
		
		// transitions to other states
		out =  new RocketColibriProtocolFsm(s.CONN_ACT);
		out.queue(e.E7_USR_OBSERVE);
		out.processNextEvent();
		assertTrue(out.getState() == s.TRY_CONN);
	}
}
