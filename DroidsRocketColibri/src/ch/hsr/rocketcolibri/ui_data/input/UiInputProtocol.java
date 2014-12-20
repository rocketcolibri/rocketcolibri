package ch.hsr.rocketcolibri.ui_data.input;

public class UiInputProtocol extends UiInputData {

	boolean tAutoMode;
	String tIpAddressServoController;
	int tPortServoController;
	
	
	public void setIpAddress(String address) 
	{
		tIpAddressServoController = address;
	}
	
	public void setPort(int port) 
	{
		tPortServoController = port;
	}
	
	public void setAuto(boolean mode)
	{
		tAutoMode = mode;
	}
	
	public String getIpAddress()
	{
		return tIpAddressServoController;
	}
	
	public int getPort()
	{
		return tPortServoController;
	}
	
	public boolean getAutoMode()
	{
		return tAutoMode;
	}
}
