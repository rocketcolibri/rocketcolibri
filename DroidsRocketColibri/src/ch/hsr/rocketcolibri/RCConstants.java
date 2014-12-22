package ch.hsr.rocketcolibri;

public class RCConstants {
	public static final int CAPTURE_RESULT_CODE = 1999;
	public static final int RC_MODEL_RESULT_CODE = 1777;
	public static final String FLAG_ACTIVITY_RC_MODEL = "fa_rc_model";
	public static final String PREFIX = "rc_";
	
	private static final String _CHANNEL_ASSIGNMENT = "channel_assignment";
	private static final String _INVERTED = "inverted";
	private static final String _MIN_RANGE = "min_range";
	private static final String _MAX_RANGE = "max_range";
	private static final String _DEFAULT_POSITION = "default_position";
	private static final String _TRIMM = "trimm";
	private static final String _STICKY = "sticky";
	private static final String _FAILSAFE = "failsafe";
	private static final String _EXPO = "expo";
	private static final String _DEBUG = "debug";
	
	public static final String CHANNEL_ASSIGNMENT_H = PREFIX+_CHANNEL_ASSIGNMENT+"_h";
	public static final String INVERTED_H = PREFIX+_INVERTED+"_h";
	public static final String MIN_RANGE_H = PREFIX+_MIN_RANGE+"_h";
	public static final String MAX_RANGE_H = PREFIX+_MAX_RANGE+"_h";
	public static final String DEFAULT_POSITION_H = PREFIX+_DEFAULT_POSITION+"_h";
	public static final String TRIMM_H = PREFIX+_TRIMM+"_h";
	public static final String STICKY_H = PREFIX+_STICKY+"_h";
	public static final String FAILSAFE_H = PREFIX+_FAILSAFE+"_h";
	public static final String EXPO_H = PREFIX+_EXPO+"_h";
	
	public static final String CHANNEL_ASSIGNMENT_V = PREFIX+_CHANNEL_ASSIGNMENT+"_v";
	public static final String INVERTED_V = PREFIX+_INVERTED+"_v";
	public static final String MIN_RANGE_V = PREFIX+_MIN_RANGE+"_v";
	public static final String MAX_RANGE_V = PREFIX+_MAX_RANGE+"_v";
	public static final String DEFAULT_POSITION_V = PREFIX+_DEFAULT_POSITION+"_v";
	public static final String TRIMM_V = PREFIX+_TRIMM+"_v";
	public static final String STICKY_V = PREFIX+_STICKY+"_v";
	public static final String FAILSAFE_V = PREFIX+_FAILSAFE+"_v";
	public static final String EXPO_V = PREFIX+_EXPO+"_v";
	
	public static final String CHANNEL_ASSIGNMENT = PREFIX+_CHANNEL_ASSIGNMENT;
	public static final String INVERTED = PREFIX+_INVERTED;
	public static final String MIN_RANGE = PREFIX+_MIN_RANGE;
	public static final String MAX_RANGE = PREFIX+_MAX_RANGE;
	public static final String DEFAULT_POSITION = PREFIX+_DEFAULT_POSITION;
	public static final String TRIMM = PREFIX+_TRIMM;
	public static final String STICKY = PREFIX+_STICKY;
	public static final String FAILSAFE = PREFIX+_FAILSAFE;
	public static final String EXPO = PREFIX+_EXPO;
	public static final String DEBUG = PREFIX+_DEBUG;
	
	
	// connection settings
	private static final String _AUTOCONNECT = "autoconnect";
	private static final String _IP_SERVOCONTROLLER = "ip_servocontroller";
	private static final String _PORT_SERVOCONTROLLER = "port_servocontroller";
	
	public static final String AUTOCONNECT = PREFIX+_AUTOCONNECT;
	public static final String IP_SERVOCONTROLLER = PREFIX+_IP_SERVOCONTROLLER;
	public static final String PORT_SERVOCONTROLLER = PREFIX+_PORT_SERVOCONTROLLER;
	
	
	/**
	 * @return DataType is just used to
	 * determine the data type of the RCConstants keys 
	 */
	public static DataType getDataTypeOf(String key){
//		if(key.contains(_CHANNEL_ASSIGNMENT)) return DataType.INT;
		if(key.contains(_INVERTED)) return DataType.BOOLEAN;
		if(key.contains(_STICKY)) return DataType.BOOLEAN;
		if(key.contains(_EXPO)) return DataType.BOOLEAN;
		if(key.contains(_DEBUG)) return DataType.BOOLEAN;
		if(key.contains(_AUTOCONNECT)) return DataType.BOOLEAN;
		if(key.contains(_IP_SERVOCONTROLLER)) return DataType.STRING;
//		if(key.contains(_MIN_RANGE)) return DataType.INT;
//		if(key.contains(_MAX_RANGE)) return DataType.INT;
//		if(key.contains(_MAX_DEFAULT_POSITION)) return DataType.INT;
//		if(key.contains(_FAILSAFE)) return DataType.INT;
//		if(key.contains(_TRIMM)) return DataType.INT;
		return DataType.INT;
	}
}
