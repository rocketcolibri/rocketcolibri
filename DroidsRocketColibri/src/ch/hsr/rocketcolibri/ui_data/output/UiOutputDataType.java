/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.ui_data.output;

/**
 * user interface output data type
 * 
 * Enumerates all data types that are provided by the RocketColibriService (which acts as a 
 * user interface data source).
 */
public enum UiOutputDataType {
	None, 				/**< no UI output data type define */ 
	ConnectionState,  	/**< Connection state data type */
	ConnectedUsers, 	/**< Connected users data type */
	Video; 				/**< video URL data type */
}
