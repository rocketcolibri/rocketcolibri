package ch.hsr.rocketcolibri.protocol;

import ch.hsr.rocketcolibri.protocol.fsm.Action;
import ch.hsr.rocketcolibri.protocol.fsm.StateMachine;
import ch.hsr.rocketcolibri.protocol.fsm.StateMachinePlan;

/**
 * Implementation of the state RocketColibri state machine
 * 
 */
public class RocketColibriProtocolFsm extends StateMachine 
{
	/**
	 * States
	 */
	public enum s 
	{
		IDLE, AVAIL, TRY_CONN, CONN_PASSIV, CONN_LCK_OUT, CONN_TRY_ACT, CONN_ACT
	}

	/**
	 * Events
	 */
	public enum e {
		E1_RECV_SSID, E2_USR_WIFI, E3_RECV_TELE_NONE, E4_RECV_TELE_ALIEN, E5_RECV_TELE_OWN, E6_USR_CONNECT, E7_USR_OBSERVE, E8_TIMEOUT 
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
ri(null,          e.E1_RECV_SSID, e.E2_USR_WIFI, e.E3_RECV_TELE_NONE, e.E4_RECV_TELE_ALIEN, e.E5_RECV_TELE_OWN, e.E6_USR_CONNECT, e.E7_USR_OBSERVE, e.E8_TIMEOUT);
at(s.IDLE,        s.AVAIL,        null,         null,                null,                 null,              null,            null,             s.IDLE);
at(s.AVAIL,       null,           s.TRY_CONN,   null,                null,                 null,              null,            null,             s.IDLE);
at(s.TRY_CONN,    null,           null,         s.CONN_PASSIV,       s.CONN_LCK_OUT,        null,              null,            null,            s.IDLE);
at(s.CONN_PASSIV, null,           null,         null,               s.CONN_LCK_OUT,        null,              s.CONN_TRY_ACT,   null,            s.IDLE);
at(s.CONN_LCK_OUT,null,           null,         s.CONN_PASSIV,       null,                 null,              null,            null,             s.IDLE);
at(s.CONN_TRY_ACT,null,           null,         null,               s.CONN_LCK_OUT,        s.CONN_ACT,         null,            s.TRY_CONN,       null);
at(s.CONN_ACT,    null,           null,         null,               null,                 null,               null,            s.CONN_PASSIV,    null);
// @formatter:on

			Action<RocketColibriProtocolFsm> startBeepTimer = new Action<RocketColibriProtocolFsm>() {
				public void apply(RocketColibriProtocolFsm fsm, Object event,
						Object nextState) {
				}
			};

			Action<RocketColibriProtocolFsm> startWaitTimer = new Action<RocketColibriProtocolFsm>() {
				public void apply(RocketColibriProtocolFsm fsm, Object event,
						Object nextState) {
				}
			};

			// TODO
//			entryAction(s.Red, startBeepTimer);
//			entryAction(s.RedAmber, startWaitTimer);
//			entryAction(s.Amber, startWaitTimer);

		}
	};

	public RocketColibriProtocolFsm(Object aStartState) {
		super(sPLAN, aStartState);
	}

}