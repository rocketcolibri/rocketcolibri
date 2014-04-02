package ch.hsr.rocketcolibri.dbService;

import java.io.File;

import org.neodatis.odb.ODB;
import org.neodatis.odb.OID;
import org.neodatis.odb.ODBFactory;

import android.content.Context;
import android.widget.Toast;

/**
 * For the connection to NeoDatis database. The connection must
 * be a singleton, because only one instance can exist. The connection
 * to the database will be created at start of the activity and closed
 * at finish..
 */
public class DBService {
	/**
	 * For the one and only existing instance
	 */
	private static DBService instance = null;

	/**
	 * Handle to the NeoDatis database, all
	 * access will be done over this handle.
	 */
	private ODB activeDB = null;

	/**
	 * To find out if the connection to database was successful
	 */
	private Boolean isConnected = false;

	/**
	 * This class is static and sometimes non static methods like getting
	 * android "data" directory must be executed. The active context can't be 
	 * accessed, therefore the active context should be given as a parameter
	 * and stored in this variable 
	 */
	private Context activeContext = null;

	/**
	 * Exists only to defeat instantiation.
	 */
	protected DBService() {
	      // Exists only to defeat instantiation.
	}

	/**
	 * For receiving the single instance of the DBService
	 *
	 * @return	The instance of DBService
	 */
	public static DBService getInstance() {
		if (instance == null) {
	    	instance = new DBService();
	    }

		return instance;
	}

	/**
	 * For opening the connection to the NeoDatis database.
	 * 
	 * The database file will be stored in android data directory:
	 * "/data/data/ch.hsr.rocketcolibri/app_data/rocketcolibri.neodatis" 
	 *
	 * @param theContext	active context for getting android data directory
	 * @return Boolean		true, for successful connection. 
	 */
	public Boolean ConnectToDatabase(Context theContext) {
		try {
            // Set active context
			activeContext = theContext;

			// Ask Android where we can store our file
			File directory = activeContext.getDir("data", Context.MODE_PRIVATE);
            String fileName = directory.getAbsolutePath() + "/rocketcolibri.neodatis";
 
            // Opens the NeoDatis database
            activeDB = ODBFactory.open(fileName);

            isConnected = true;

            ShowInfo("NeoDatis DB opened!");
        } catch (Throwable e) {
            if (activeDB != null) {
            	activeDB.rollback();
            }

            return false;
        }

		return true;		
	}

	/**
	 * For closing the connection from NeoDatis database
	 */
	public void CloseDatabase () {
		// Close the database
        if (activeDB != null) {
        	activeDB.close();

            ShowInfo("NeoDatis DB closed!");
        }
	}

	/**
	 * To find out if successfully connected to NeoDatis database.
	 *
	 * @return	Boolean		true, if connection was successful
	 */
	public Boolean IsConnectedToDB () {
		return isConnected;
	}

	/**
	 * An object will be stored in NeoDatis database.
	 *
	 * @param theObject		an object to store in database
	 * @return OID			the object ID
	 */
	public OID StoreToDatabase (Object theObject) {
        if (activeDB != null) {
	        // Stores the object in DB
	        OID oid = activeDB.store(theObject);

            ShowInfo("NeoDatis DB object stored!");
            return oid;
        }
        
        return null;
	}

	/**
	 * Reading the values of an object from NeoDatis database.
	 *
	 * @param oid		to specify which object to read
	 * @return Object	the complete object from database
	 */
	public Object ReadFromDatabase (OID oid) {
        if (activeDB != null) {
	        // Retrieve an object with the specific id 
	        Object theObject = activeDB.getObjectFromId(oid);
	
            ShowInfo("NeoDatis DB object read!");
			return theObject;
        }
        
        return null;
	}

	/**
	 * Shows a specific string on the activity
	 *
	 * @param strInfo		specific info string to show 
	 */
	public void ShowInfo (String strInfo) {
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(activeContext, strInfo, duration);
        toast.show();
	}
}