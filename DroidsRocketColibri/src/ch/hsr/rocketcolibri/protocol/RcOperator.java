/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.protocol;

/**
 * simple class to store the user name and the IP address of a RC operator
 */
public class RcOperator {

	private String name;
	private String ipAddress;
	// get-/setter
	public void setName(String name) { this.name = name; }
	public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
	public String getName() { return this.name; }
	public String getIpAddress() { return this.ipAddress; }

	@Override
	public boolean equals(Object obj)
	{
		if (null != obj)
		{
			RcOperator other = (RcOperator)obj;
			return this.getName().equals(other.getName());
		}
		else
			return false;
	}
}
