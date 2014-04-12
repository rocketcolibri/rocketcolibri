package ch.hsr.rocketcolibri.dbService;

import java.io.File;

import org.neodatis.odb.ODB;
import org.neodatis.odb.OID;
import org.neodatis.odb.ODBFactory;

import android.content.Context;
import android.widget.Toast;

/**
 * For the connection to NeoDatis database. The connection must
 * be a singleton, because only one instance can exist. Thus will be
 * granted from the service. Service will be create an instance and
 * take care after being a singleton. The connection to the database
 * will be created at start of the activity and closed at finish..
 */
public class DBService {
	/**
	 * Handle to the NeoDatis database, all
	 * access will be done over this handle.
	 */
	private Context mActiveContext = null;

	/**
	 * Handle to the NeoDatis database, all
	 * access will be done over this handle.
	 */
	private ODB activeDB = null;

	/**
	 * Constructor for a new instance.
	 */
	public DBService(Context theContext) {
		this.mActiveContext = theContext;
	}

	/**
	 * For opening the connection to the NeoDatis database.
	 * 
	 * The database file will be stored in android data directory:
	 * "/data/data/ch.hsr.rocketcolibri/app_data/rocketcolibri.neodatis" 
	 *
	 * @return Boolean		true, for successful connection. 
	 */
	public Boolean ConnectToDatabase () {
		try {
	        if (this.activeDB == null) {
				// Ask Android where we can store our file
				File directory = mActiveContext.getDir("data", Context.MODE_PRIVATE);
	            String fileName = directory.getAbsolutePath() + "/rocketcolibri.neodatis";
	 
	            // Opens the NeoDatis database
	            this.activeDB = ODBFactory.open(fileName);

	            this.ShowInfo("NeoDatis DB opened!");
			}
			else {
	            this.ShowInfo("NeoDatis DB already opened!");
			}
        } catch (Throwable e) {
            if (this.activeDB != null) {
            	this.activeDB.rollback();
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
        if (this.activeDB != null) {
        	this.activeDB.close();

            this.ShowInfo("NeoDatis DB is closed!");
        }
	}

	/**
	 * To find out if successfully connected to NeoDatis database.
	 *
	 * @return	Boolean		true, if connection was successful
	 */
	public Boolean IsConnectedToDB () {
        if (this.activeDB != null) {
        	return true;
        }
        else {
        	return false;
        }
	}

	/**
	 * An object will be stored in NeoDatis database.
	 *
	 * @param theObject		an object to store in database
	 * @return OID			the object ID
	 */
	public OID StoreToDatabase (Object theObject) {
        if (this.activeDB != null) {
	        // Stores the object in DB
	        OID oid = this.activeDB.store(theObject);

            this.ShowInfo("NeoDatis DB object stored!");
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
        if (this.activeDB != null) {
	        // Retrieve an object with the specific id 
	        Object theObject = this.activeDB.getObjectFromId(oid);
	
            this.ShowInfo("NeoDatis DB object read!");
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

        Toast toast = Toast.makeText(mActiveContext, strInfo, duration);
        toast.show();
	}
}