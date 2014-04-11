package ch.hsr.rocketcolibri.protocol;

import ch.hsr.rocketcolibri.protocol.fsm.Action;
import ch.hsr.rocketcolibri.protocol.fsm.StateMachine;
import ch.hsr.rocketcolibri.protocol.fsm.StateMachinePlan;

/**
 * Implementation of the state RocketColibri state machine
 * 
 */
public class RocketColibriProtocolFsm extends StateMachine {
	/**
	 * States
	 */
	public enum s {
		DISC, TRY_CONN, CONN_PASSIV, CONN_LCK_OUT, CONN_TRY_ACT, CONN_ACT
	}

	/**
	 * Events
	 */
	public enum e {
		E1_CONN_SSID, E2_DISC_SSID, E3_RECV_TELE_NONE, E4_RECV_TELE_ALIEN, E5_RECV_TELE_OWN, E6_USR_CONNECT, E7_USR_OBSERVE, E8_TIMEOUT
	};

	/**
	 * Static so that it's only constructed once on class load
	 * You obviously shouldn't store individual state in the plan.
	 * Your FSM will be passed into the transition Actions
	 */
	private static final StateMachinePlan sPLAN = new StateMachinePlan() {
		{

			/** Rocket Colibri Transistion table */
// @formatter:off
ri(null,  	e.E1_CONN_SSID,	e.E2_DISC_SSID,	e.E3_RECV_TELE_NONE,	e.E4_RECV_TELE_ALIEN,	e.E5_RECV_TELE_OWN,	e.E6_USR_CONNECT,	e.E7_USR_OBSERVE,	e.E8_TIMEOUT);
at(s.DISC, 	s.TRY_CONN, 	null,	 		null, 					null, 					null, 				null, 				null, 				null);
at(s.TRY_CONN,null, 		s.DISC, 		s.CONN_PASSIV, 			s.CONN_LCK_OUT, 		null,				null,				null,				s.TRY_CONN);
at(s.CONN_PASSIV, null, 	s.DISC, 		null, 					s.CONN_LCK_OUT, 		null,				s.CONN_TRY_ACT,		null,				s.TRY_CONN);
at(s.CONN_LCK_OUT, null, 	s.DISC,			s.CONN_PASSIV,			null,					null,				null,				null,				s.TRY_CONN);
at(s.CONN_TRY_ACT, null, 	s.DISC, 		null, 					s.CONN_LCK_OUT,			s.CONN_ACT,			null,				s.TRY_CONN,			s.TRY_CONN);
at(s.CONN_ACT, null, 		s.DISC, 		null, 					null, 					null, 				null, 				s.TRY_CONN,			null);
// @formatter:on


		}
	};

	public RocketColibriProtocolFsm(Object aStartState) {
		super(sPLAN, aStartState);
	}


}