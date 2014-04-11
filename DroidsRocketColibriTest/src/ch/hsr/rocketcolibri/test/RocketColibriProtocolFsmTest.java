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

	public final void testRocketColibriProtocolFsmTransitionsFromStateDisconnected() {
		RocketColibriProtocolFsm out = new RocketColibriProtocolFsm(s.DISC);
		assertTrue(out.getState() == s.DISC);
		out.queue(e.E2_DISC_SSID);
		out.processNextEvent();
		assertTrue(out.getState() == s.DISC);
		out.queue(e.E3_RECV_TELE_NONE);
		out.processNextEvent();
		assertTrue(out.getState() == s.DISC);
		out.queue(e.E4_RECV_TELE_ALIEN);
		out.processNextEvent();
		assertTrue(out.getState() == s.DISC);
		out.queue(e.E5_RECV_TELE_OWN);
		out.processNextEvent();
		assertTrue(out.getState() == s.DISC);
		out.queue(e.E6_USR_CONNECT);
		out.processNextEvent();
		assertTrue(out.getState() == s.DISC);
		out.queue(e.E7_USR_OBSERVE);
		out.processNextEvent();
		assertTrue(out.getState() == s.DISC);
		out.queue(e.E8_TIMEOUT);
		out.processNextEvent();
		assertTrue(out.getState() == s.DISC);		
		
		// transitions to other states
		out.queue(e.E1_CONN_SSID);
		out.processNextEvent();
		assertTrue(out.getState() == s.TRY_CONN);
	}
	
	public final void testRocketColibriProtocolFsmTransitionsFromStateTryConnect() 
	{
		RocketColibriProtocolFsm out = new RocketColibriProtocolFsm(s.TRY_CONN);
		out.queue(e.E1_CONN_SSID);
		out.processNextEvent();
		assertTrue(out.getState() == s.TRY_CONN);

		out = new RocketColibriProtocolFsm(s.TRY_CONN);
		out.queue(e.E2_DISC_SSID);
		out.processNextEvent();
		assertTrue(out.getState() == s.DISC);

		out = new RocketColibriProtocolFsm(s.TRY_CONN);
		out.queue(e.E3_RECV_TELE_NONE);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_PASSIV);
		
		out = new RocketColibriProtocolFsm(s.TRY_CONN);
		out.queue(e.E4_RECV_TELE_ALIEN);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_LCK_OUT);
		
		out = new RocketColibriProtocolFsm(s.TRY_CONN);
		out.queue(e.E5_RECV_TELE_OWN);
		out.processNextEvent();
		assertTrue(out.getState() == s.TRY_CONN);
		
		out = new RocketColibriProtocolFsm(s.TRY_CONN);
		out.queue(e.E6_USR_CONNECT);
		out.processNextEvent();
		assertTrue(out.getState() == s.TRY_CONN);
		
		out = new RocketColibriProtocolFsm(s.TRY_CONN);
		out.queue(e.E7_USR_OBSERVE);
		out.processNextEvent();
		assertTrue(out.getState() == s.TRY_CONN);
		
		out = new RocketColibriProtocolFsm(s.TRY_CONN);
		out.queue(e.E8_TIMEOUT);
		out.processNextEvent();
		assertTrue(out.getState() == s.TRY_CONN);
	}
	
	public final void testRocketColibriProtocolFsmTransitionsFromStateConnectionLockedOut() {
		RocketColibriProtocolFsm out = new RocketColibriProtocolFsm(s.CONN_LCK_OUT);
		out.queue(e.E1_CONN_SSID);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_LCK_OUT);


		out = new RocketColibriProtocolFsm(s.CONN_LCK_OUT);
		out.queue(e.E2_DISC_SSID);
		out.processNextEvent();
		assertTrue(out.getState() == s.DISC);
	
		out =  new RocketColibriProtocolFsm(s.CONN_LCK_OUT);
		out.queue(e.E3_RECV_TELE_NONE);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_PASSIV);
		
		
		out = new RocketColibriProtocolFsm(s.CONN_LCK_OUT);
		out.queue(e.E4_RECV_TELE_ALIEN);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_LCK_OUT);
		
		out = new RocketColibriProtocolFsm(s.CONN_LCK_OUT);
		out.queue(e.E5_RECV_TELE_OWN);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_LCK_OUT);
		
		out = new RocketColibriProtocolFsm(s.CONN_LCK_OUT);
		out.queue(e.E6_USR_CONNECT);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_LCK_OUT);
		
		out = new RocketColibriProtocolFsm(s.CONN_LCK_OUT);
		out.queue(e.E7_USR_OBSERVE);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_LCK_OUT);
	
	
		out =  new RocketColibriProtocolFsm(s.CONN_LCK_OUT);
		out.queue(e.E8_TIMEOUT);
		out.processNextEvent();
		assertTrue(out.getState() == s.TRY_CONN);
	}
	
	public final void testRocketColibriProtocolFsmTransitionsFromStateConnectionPassive() {

		RocketColibriProtocolFsm out = new RocketColibriProtocolFsm(s.CONN_PASSIV);
		out.queue(e.E1_CONN_SSID);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_PASSIV);

		out = new RocketColibriProtocolFsm(s.CONN_PASSIV);
		out.queue(e.E2_DISC_SSID);
		out.processNextEvent();
		assertTrue(out.getState() == s.DISC);
		
		out = new RocketColibriProtocolFsm(s.CONN_PASSIV);
		out.queue(e.E3_RECV_TELE_NONE);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_PASSIV);

		out =  new RocketColibriProtocolFsm(s.CONN_PASSIV);
		out.queue(e.E4_RECV_TELE_ALIEN);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_LCK_OUT);

		
		out = new RocketColibriProtocolFsm(s.CONN_PASSIV);
		out.queue(e.E5_RECV_TELE_OWN);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_PASSIV);

		out =  new RocketColibriProtocolFsm(s.CONN_PASSIV);
		out.queue(e.E6_USR_CONNECT);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_TRY_ACT);

		out = new RocketColibriProtocolFsm(s.CONN_PASSIV);
		out.queue(e.E7_USR_OBSERVE);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_PASSIV);
		
		out =  new RocketColibriProtocolFsm(s.CONN_PASSIV);
		out.queue(e.E8_TIMEOUT);
		out.processNextEvent();
		assertTrue(out.getState() == s.TRY_CONN);
	}
	
	public final void testRocketColibriProtocolFsmTransitionsFromStateConnectionTryActive() {
		RocketColibriProtocolFsm out = new RocketColibriProtocolFsm(s.CONN_TRY_ACT);
		assertTrue(out.getState() == s.CONN_TRY_ACT);
		out.queue(e.E1_CONN_SSID);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_TRY_ACT);
		
		out = new RocketColibriProtocolFsm(s.CONN_TRY_ACT);
		out.queue(e.E2_DISC_SSID);
		out.processNextEvent();
		assertTrue(out.getState() == s.DISC);
		
		out = new RocketColibriProtocolFsm(s.CONN_TRY_ACT);
		out.queue(e.E3_RECV_TELE_NONE);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_TRY_ACT);

		out = new RocketColibriProtocolFsm(s.CONN_TRY_ACT);
		out.queue(e.E4_RECV_TELE_ALIEN);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_LCK_OUT);

		out =  new RocketColibriProtocolFsm(s.CONN_TRY_ACT);
		out.queue(e.E5_RECV_TELE_OWN);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_ACT);
		
		out = new RocketColibriProtocolFsm(s.CONN_TRY_ACT);
		out.queue(e.E6_USR_CONNECT);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_TRY_ACT);
		
		out =  new RocketColibriProtocolFsm(s.CONN_TRY_ACT);
		out.queue(e.E7_USR_OBSERVE);
		out.processNextEvent();
		assertTrue(out.getState() == s.TRY_CONN);
		
		out =  new RocketColibriProtocolFsm(s.CONN_TRY_ACT);
		out.queue(e.E8_TIMEOUT);
		out.processNextEvent();
		assertTrue(out.getState() == s.TRY_CONN);
	}
	
	public final void testRocketColibriProtocolFsmTransitionsFromStateConnectionActive() {
		RocketColibriProtocolFsm out = new RocketColibriProtocolFsm(s.CONN_ACT);
		assertTrue(out.getState() == s.CONN_ACT);
		out.queue(e.E1_CONN_SSID);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_ACT);

		out = new RocketColibriProtocolFsm(s.CONN_ACT);
		out.queue(e.E2_DISC_SSID);
		out.processNextEvent();
		assertTrue(out.getState() == s.DISC);
		
		out = new RocketColibriProtocolFsm(s.CONN_ACT);
		out.queue(e.E3_RECV_TELE_NONE);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_ACT);
		
		out = new RocketColibriProtocolFsm(s.CONN_ACT);
		out.queue(e.E4_RECV_TELE_ALIEN);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_ACT);
		
		out = new RocketColibriProtocolFsm(s.CONN_ACT);
		out.queue(e.E5_RECV_TELE_OWN);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_ACT);
		
		out = new RocketColibriProtocolFsm(s.CONN_ACT);
		out.queue(e.E6_USR_CONNECT);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_ACT);
		
		out =  new RocketColibriProtocolFsm(s.CONN_ACT);
		out.queue(e.E7_USR_OBSERVE);
		out.processNextEvent();
		assertTrue(out.getState() == s.TRY_CONN);
		
		out = new RocketColibriProtocolFsm(s.CONN_ACT);
		out.queue(e.E8_TIMEOUT);
		out.processNextEvent();
		assertTrue(out.getState() == s.CONN_ACT);
	}
}
