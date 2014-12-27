package ch.hsr.rocketcolibri.ui_data.input;

public class UiInputProtocol extends UiInputData {

	static final int tPortDefault = 30001;
	static final String tIpDefault = "192.168.200.1";
	static final String tWpaPasswordDefault = "1234567890";
	static final String tWpaSsidDefault = "RocketColibri";
	
	boolean tAutoMode = true;
	String tIpAddressServoController = tIpDefault;
	String tWpaPassword = tWpaPasswordDefault;
	String tWpaSsid = tWpaSsidDefault;
	
	int tPortServoController = tPortDefault; 
	
	
	public void setIpAddress(String address) 
	{
		tIpAddressServoController = address;
	}
	
	public void setWpaPassword(String newWpaPassword)
	{
		tWpaPassword = newWpaPassword;
	}
	
	public void setWpaSsid(String newWpaSssid)
	{
		tWpaSsid = newWpaSssid;
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
		return tAutoMode ? tIpDefault : tIpAddressServoController;
	}
	
	public int getPort()
	{	 
		return tAutoMode ? tPortDefault : tPortServoController ;
	}
	
	public boolean getAutoMode()
	{
		return tAutoMode;
	}
	
	public String getWpaPassword()
	{
		return tWpaPassword;
	}
	
	public String getWpaSsid()
	{
		return tWpaSsid;
	}
}
