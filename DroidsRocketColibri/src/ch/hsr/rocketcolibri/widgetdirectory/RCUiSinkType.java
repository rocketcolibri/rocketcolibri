package ch.hsr.rocketcolibri.widgetdirectory;

public enum RCUiSinkType {
	None, 				/**< not a UiSink */ 
	ConnectionState,  	/**< Connection state sink */
	Telemetry, 			/**< Telemetry sink */
	Video; 				/**< video URL sink */
}
