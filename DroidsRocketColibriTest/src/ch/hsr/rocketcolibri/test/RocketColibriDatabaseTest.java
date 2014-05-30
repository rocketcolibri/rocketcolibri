/**
 * 
 */
package ch.hsr.rocketcolibri.test;

import java.util.ArrayList;
import java.util.List;

import android.test.AndroidTestCase;
import ch.hsr.rocketcolibri.db.RocketColibriDB;
import ch.hsr.rocketcolibri.db.model.RCModel;
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import ch.hsr.rocketcolibri.view.resizable.ResizeConfig;

/**
 * Unit test class for testing the database connection
 *
 * @author Haluk
 *
 */
public class RocketColibriDatabaseTest extends AndroidTestCase {

	RocketColibriDB tDB = null;
	RCModel	tModel1 = new RCModel();
	RCModel	tModel2 = new RCModel();
	String tModelName1 = "Model1";
	String tModelName2 = "Model2";

	public void setUp() throws Exception {
    	super.setUp();
        mContext = getContext();

		// Preparing objects to store
        tModel1 = this.prepareFirstItem(tModelName1);
        tModel2 = this.prepareSecondItem(tModelName2);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Preparing an RCModel for storing in the database
     * 
     * @param pModelName, RCModel name
     * @return new RCModel object
     */
    protected RCModel prepareFirstItem(String pModelName) {
    	RCModel tModel = new RCModel();
    	ResizeConfig tRConfig = new ResizeConfig();
    	List<ViewElementConfig> tElementConfigList = new ArrayList<ViewElementConfig>();
    	LayoutParams tLParam = new LayoutParams(100, 100, 50,200);

	    tRConfig.maxHeight = 745;
	    tRConfig.minHeight = 50;
	    tRConfig.maxWidth =400;
	    tRConfig.minWidth = 30;

	    ViewElementConfig elementConfig = new ViewElementConfig("ch.hsr.rocketcolibri.view.custimizable.CustomizableView", tLParam, tRConfig); 
		tElementConfigList.add(elementConfig);

		tModel.setName(pModelName);
		tModel.setViewElementConfigs(tElementConfigList);

		return tModel;
    }

    /**
     * Preparing an RCModel for storing in the database
     * 
     * @param pModelName, RCModel name
     * @return new RCModel object
     */
    protected RCModel prepareSecondItem(String pModelName) {
    	RCModel tModel = new RCModel();
    	ResizeConfig tRConfig = new ResizeConfig();
    	List<ViewElementConfig> tElementConfigList = new ArrayList<ViewElementConfig>();
    	LayoutParams tLParam = new LayoutParams(50, 20, 50,200);

    	tRConfig.maxHeight = 500;
	    tRConfig.minHeight = 50;
	    tRConfig.maxWidth =300;
	    tRConfig.minWidth = 30;
	    
	    ViewElementConfig elementConfig = new ViewElementConfig("ch.hsr.rocketcolibri.view.custimizable.CustomizableView", tLParam, tRConfig); 
		tElementConfigList.add(elementConfig);

		tModel.setName(pModelName);
		tModel.setViewElementConfigs(tElementConfigList);

		return tModel;
    }

    /**
     * Test if connected to database 
     */
    public void testDBIsConnected() {
		assertNull(tDB);
    	tDB = new RocketColibriDB(mContext);

		assertNotNull(tDB);
	}

    /**
     * Testing the store and read functionality. First an item
     * will be stored and then read again. The both objects must
     * be equal
     */
    public void testStoringAndReadingObjects() {
		assertNull(tDB);
		tDB = new RocketColibriDB(mContext);
		assertNotNull(tDB);

		// Store the RCModel objects in database
		tDB.store(tModel1);
		tDB.store(tModel2);

		// Fetch the object from database
		RCModel tModelFromDB = new RCModel();
		tModelFromDB = tDB.fetchRCModelByName(tModelName1);

		// Compare both objects, both must be the same
		assertEquals(tModel1, tModelFromDB);

		// Delete the RCModel objects from database
		tDB.delete(tModel1);
		tDB.delete(tModel2);

		// Close the database
		tDB.close();
    }

    /**
     * Testing the update functionality. An object will be stored
     * in database then the object will be read and updated. And then
     * stored in the database. The object must stay the same.
     */
    public void testUpdatingObjects() {
		assertNull(tDB);
		tDB = new RocketColibriDB(mContext);
		assertNotNull(tDB);

		// Store the RCModel objects in database
		tDB.store(tModel2);

		// Fetch the object from database
		RCModel tModelFromDBActual = new RCModel();
		tModelFromDBActual = tDB.fetchRCModelByName(tModelName2);

		// Update the object read from the database
    	LayoutParams tLParam = new LayoutParams(115, 125, 50, 250);
    	List<ViewElementConfig> tElementConfigList = new ArrayList<ViewElementConfig>();
		tElementConfigList = tModelFromDBActual.getViewElementConfigs();
		tElementConfigList.get(0).setLayoutParams(tLParam);

		// Store the updated object in database
		tModelFromDBActual.setViewElementConfigs(tElementConfigList);
		tDB.store(tModelFromDBActual);

		// Fetch the updated object from database
		RCModel tModelFromDBUpdated = new RCModel();
		tModelFromDBUpdated = tDB.fetchRCModelByName(tModelName2);

		// Compare both objects, both must be the same
		assertEquals(tModelFromDBActual, tModelFromDBUpdated);

		// Compare objects before and after update
		assertNotSame(tModel2, tModelFromDBUpdated);

		// Delete RCModel and close database
		tDB.delete(tModel2);
		tDB.close();
    }
}