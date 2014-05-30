package ch.hsr.rocketcolibri;

public class RCConstants {
	public static final String PREFIX = "rc_";
	
	private static final String _CHANNEL = "channel";
	private static final String _INVERTED = "inverted";
	private static final String _MIN_RANGE = "min_range";
	private static final String _MAX_RANGE = "max_range";
	private static final String _TRIMM = "trimm";
	
	public static final String CHANNEL_H = PREFIX+_CHANNEL+"_h";
	public static final String INVERTED_H = PREFIX+_INVERTED+"_h";
	public static final String MIN_RANGE_H = PREFIX+_MIN_RANGE+"_h";
	public static final String MAX_RANGE_H = PREFIX+_MAX_RANGE+"_h";
	public static final String TRIMM_H = PREFIX+_TRIMM+"_h";
	
	public static final String CHANNEL_V = PREFIX+_CHANNEL+"_v";
	public static final String INVERTED_V = PREFIX+_INVERTED+"_v";
	public static final String MIN_RANGE_V = PREFIX+_MIN_RANGE+"_v";
	public static final String MAX_RANGE_V = PREFIX+_MAX_RANGE+"_v";
	public static final String TRIMM_V = PREFIX+_TRIMM+"_v";
	
	public static final String CHANNEL = PREFIX+_CHANNEL;
	public static final String INVERTED = PREFIX+_INVERTED;
	public static final String MIN_RANGE = PREFIX+_MIN_RANGE;
	public static final String MAX_RANGE = PREFIX+_MAX_RANGE;
	public static final String TRIMM = PREFIX+_TRIMM;
	
	/**
	 * @return DataType is just used to
	 * determine the data type of the RCConstants keys 
	 */
	public static DataType getDataTypeOf(String key){
//		if(key.contains(_CHANNEL)) return DataType.INT;
		if(key.contains(_INVERTED)) return DataType.BOOLEAN;
//		if(key.contains(_MIN_RANGE)) return DataType.INT;
//		if(key.contains(_MAX_RANGE)) return DataType.INT;
//		if(key.contains(_TRIMM)) return DataType.INT;
		return DataType.INT;
	}
}
