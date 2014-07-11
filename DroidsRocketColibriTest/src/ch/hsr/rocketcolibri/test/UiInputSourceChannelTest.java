/**
 * 
 */
package ch.hsr.rocketcolibri.test;

import ch.hsr.rocketcolibri.ui_data.input.UiInputSourceChannel;
import junit.framework.TestCase;

/**
 * @author lorenz
 *
 */
public class UiInputSourceChannelTest extends TestCase {
	static UiInputSourceChannel out;
	protected static void setUpBeforeClass() throws Exception {
		
	}

	protected static void tearDownAfterClass() throws Exception {
	}

	protected void setUp() throws Exception {
		super.setUp();
		out = new UiInputSourceChannel();
		out.setChannelAssignment(1);
	}
	
	public final void testUiInputSourceChannelDefaults()
	{
		assertEquals(out.getChannelAssignment(), 1);
		assertEquals(out.getChannelMinRange(), UiInputSourceChannel.MIN_CHANNEL_VALUE);
		assertEquals(out.getChannelMaxRange(), UiInputSourceChannel.MAX_CHANNEL_VALUE);
		assertEquals(out.getChannelInverted(), false);
		assertEquals(out.getChannelValue(), 0);
		assertEquals(out.getChannelTrimm(), 0);
		assertEquals(out.getChannelDefaultPosition(), 0);
	}
	
	/** setter */
	
	public final void setChannelMinRange()	{
		out.setWidgetPosition(0);
		assertEquals(1, out.getChannelValue());
		out.setChannelMinRange(10);
		assertEquals(10, out.getChannelValue());
		out.setChannelMinRange(-10);
		assertEquals(1, out.getChannelValue());
	}

	public final void testSetChannelMaxRange()	{

		out.setWidgetPosition(999);
		assertEquals(1000, out.getChannelValue());
		out.setChannelMaxRange(800);
		assertEquals(800, out.getChannelValue());
		out.setChannelMaxRange(500);
		assertEquals(500, out.getChannelValue());
		
		out.setWidgetPosition(499);
		out.setChannelMaxRange(1000);
		assertEquals(500, out.getChannelValue());
		out.setChannelMaxRange(800);
		assertEquals(400, out.getChannelValue());
		out.setChannelMaxRange(500);
		assertEquals(250, out.getChannelValue());
	}
	
	public final void testSetChannelTrimm(){ 
		out.setWidgetPosition(499);
		out.setChannelTrimm(10);
		assertEquals(510, out.getChannelValue());
		out.setChannelTrimm(-10);
		assertEquals(490, out.getChannelValue());
	}
	
	public final void testSetChannelInverted(){ 
		out.setWidgetPosition(799);
		assertEquals(800, out.getChannelValue());
		out.setChannelInverted(true);
		assertEquals(200, out.getChannelValue());
	}
		
	public final void testSetWidgetRange() {
		out.setWidgetRange(-500, 500);
		out.setWidgetPosition(0);
		assertEquals(500, out.getChannelValue());
	}
	
	public void testSetWidgetToDefault() {
		out.setWidgetRange(-500, 500);
		out.setWidgetPosition(0);
		assertEquals(-500, out.setWidgetToDefault());
	}
	
}
