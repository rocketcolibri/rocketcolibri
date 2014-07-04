package ch.hsr.rocketcolibri;

public class RCConstants {
	public static final String PREFIX = "rc_";
	
	private static final String _CHANNEL_ASSIGNMENT = "channel_assignment";
	private static final String _INVERTED = "inverted";
	private static final String _MIN_RANGE = "min_range";
	private static final String _MAX_RANGE = "max_range";
	private static final String _DEFAULT_POSITION = "default_position";
	private static final String _TRIMM = "trimm";
	private static final String _STICKY = "sticky";
	
	public static final String CHANNEL_ASSIGNMENT_H = PREFIX+_CHANNEL_ASSIGNMENT+"_h";
	public static final String INVERTED_H = PREFIX+_INVERTED+"_h";
	public static final String MIN_RANGE_H = PREFIX+_MIN_RANGE+"_h";
	public static final String MAX_RANGE_H = PREFIX+_MAX_RANGE+"_h";
	public static final String DEFAULT_POSITION_H = PREFIX+_DEFAULT_POSITION+"_h";
	public static final String TRIMM_H = PREFIX+_TRIMM+"_h";
	public static final String STICKY_H = PREFIX+_STICKY+"_h";
	
	public static final String CHANNEL_ASSIGNMENT_V = PREFIX+_CHANNEL_ASSIGNMENT+"_v";
	public static final String INVERTED_V = PREFIX+_INVERTED+"_v";
	public static final String MIN_RANGE_V = PREFIX+_MIN_RANGE+"_v";
	public static final String MAX_RANGE_V = PREFIX+_MAX_RANGE+"_v";
	public static final String DEFAULT_POSITION_V = PREFIX+_DEFAULT_POSITION+"_v";
	public static final String TRIMM_V = PREFIX+_TRIMM+"_v";
	public static final String STICKY_V = PREFIX+_STICKY+"_v";
	
	public static final String CHANNEL_ASSIGNMENT = PREFIX+_CHANNEL_ASSIGNMENT;
	public static final String INVERTED = PREFIX+_INVERTED;
	public static final String MIN_RANGE = PREFIX+_MIN_RANGE;
	public static final String MAX_RANGE = PREFIX+_MAX_RANGE;
	public static final String DEFAULT_POSITION = PREFIX+_DEFAULT_POSITION;
	public static final String TRIMM = PREFIX+_TRIMM;
	public static final String STICKY = PREFIX+_STICKY;
	
	/**
	 * @return DataType is just used to
	 * determine the data type of the RCConstants keys 
	 */
	public static DataType getDataTypeOf(String key){
//		if(key.contains(_CHANNEL_ASSIGNMENT)) return DataType.INT;
		if(key.contains(_INVERTED)) return DataType.BOOLEAN;
		if(key.contains(_STICKY)) return DataType.BOOLEAN;
//		if(key.contains(_MIN_RANGE)) return DataType.INT;
//		if(key.contains(_MAX_RANGE)) return DataType.INT;
//		if(key.contains(_MAX_DEFAULT_POSITION)) return DataType.INT;
//		if(key.contains(_TRIMM)) return DataType.INT;
		return DataType.INT;
	}
}
