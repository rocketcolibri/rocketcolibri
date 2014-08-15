/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.db;

import java.util.List;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import ch.hsr.rocketcolibri.RocketColibriDefaults;
import ch.hsr.rocketcolibri.db.model.JsonRCModel;
import ch.hsr.rocketcolibri.db.model.LastUpdateFromFile;
import ch.hsr.rocketcolibri.db.model.RCModel;
import ch.hsr.rocketcolibri.util.AndroidUtil;
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import ch.hsr.rocketcolibri.view.resizable.ResizeConfig;
import ch.hsr.rocketcolibri.view.widget.DefaultViewElementConfigRepo;
import ch.hsr.rocketcolibri.view.widget.RCWidgetConfig;

/**
 * @author Artan Veliju
 */
public class RCDBProcessor {
	private Context tContext;
	private RocketColibriDB tRocketColibriDB;
	public static final String INSERT = "insert";
	public static final String UPDATE = "update";
	public static final String DELETE = "delete";
	private static final String PLACEHOLDER = "{% INDEX %}";
	private LastUpdateFromFile tLastUpdateFromFile;
	private boolean tCheckTimestamp;
	private Point tRealScreenSize;
	
	public RCDBProcessor(Context context, RocketColibriDB rocketColibriDB, boolean checkTimestamp){
		tContext = context;
		tRocketColibriDB = rocketColibriDB;
		tCheckTimestamp = checkTimestamp;
		tRealScreenSize = new AndroidUtil(tContext).getRealSize();
	}
	
	public void process(List<JsonRCModel> models) throws Exception{
		tLastUpdateFromFile = fetchLastUpdateFromFile();
		boolean updated = false;
		for(JsonRCModel m : models){
			if(shouldInsert(m)){
				insert(m);
				updated = true;
			}else if(shouldUpdate(m)){
				update(m);
				updated = true;
			}else if(shouldDelete(m)){
				delete(m);
				updated = true;
			}
		}
		if(updated){
			tLastUpdateFromFile.timestamp = System.currentTimeMillis();
			tRocketColibriDB.store(tLastUpdateFromFile);
		}
	}
	
	private boolean shouldInsert(JsonRCModel m){
		return m.process.equals(INSERT) && tCheckTimestamp?needToUpdate(m.getTimestampAsDate().getTime()):true;
	}
	
	private boolean shouldUpdate(JsonRCModel m){
		return m.process.equals(UPDATE) && tCheckTimestamp?needToUpdate(m.getTimestampAsDate().getTime()):true;
	}
	
	private boolean shouldDelete(JsonRCModel m){
		return m.process.equals(DELETE) && tCheckTimestamp?needToUpdate(m.getTimestampAsDate().getTime()):true;
	}
	
	private void insert(JsonRCModel m){
		dpToPixel(m);
		makeSureNameIsUnique(m);
		tRocketColibriDB.store(m.model);
	}
	
	private void update(JsonRCModel m){
		RCModel dbModel = tRocketColibriDB.fetchRCModelByName(m.model.getName());
		dpToPixel(m);
		if (dbModel == null) {
			tRocketColibriDB.store(m.model);	// couldn't find in database, insert it
		} else {
			dbModel.setName(m.model.getName());
			dbModel.setWidgetConfigs(m.model.getWidgetConfigs());
			tRocketColibriDB.store(dbModel);	// found in database, update it
		}
	}
	
	private void delete(JsonRCModel m){
		tRocketColibriDB.delete(tRocketColibriDB.fetchRCModelByName(m.model.getName()));
	}
	
	private boolean needToUpdate(long fileTimeStamp){
		return tLastUpdateFromFile.timestamp<fileTimeStamp;
	}
	
	private LastUpdateFromFile fetchLastUpdateFromFile(){
		LastUpdateFromFile luff = null;
		try{
			luff = (LastUpdateFromFile) tRocketColibriDB.fetch(LastUpdateFromFile.class).getFirst();
		}catch(Exception e){
			luff = new LastUpdateFromFile();
		}
		return luff;
	}
	
	/**
	 * This is needed on import to be sure we're not storing the new Models with Names that already exists in the Database
	 * @param m
	 */
	private void makeSureNameIsUnique(JsonRCModel m){
		String uniqueModelName = m.model.getName();
		boolean exists = tRocketColibriDB.rcModelExists(m.model.getName());
		if(exists){
			uniqueModelName += " ("+PLACEHOLDER+")";
			int index = 1;
			while(true){
				exists = tRocketColibriDB.rcModelExists(uniqueModelName.replace(PLACEHOLDER, String.valueOf(index)));
				if(exists){
					++index;
				}else{break;}
			}
			m.model.setName(uniqueModelName.replace(PLACEHOLDER, String.valueOf(index)));
		}
	}
	
	private void dpToPixel(JsonRCModel future){
		DisplayMetrics dm = tContext.getResources().getDisplayMetrics();
		for(RCWidgetConfig wc : future.model.getWidgetConfigs()){
			RocketColibriDefaults.dpToPixel(dm, wc.viewElementConfig);
			checkCompatibility(wc.viewElementConfig);
		}
	}
	
	private void checkCompatibility(ViewElementConfig vec){
		try{
			ViewElementConfig defaultVec = DefaultViewElementConfigRepo.getDefaultConfig(Class.forName(vec.getClassPath()));
			overrideResizeConfig(vec.getResizeConfig(), defaultVec.getResizeConfig());
			checkPositionAndSize(vec);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void overrideResizeConfig(ResizeConfig targetConfig, ResizeConfig defaultConfig){
		targetConfig.keepRatio = defaultConfig.keepRatio;
		targetConfig.maxHeight = defaultConfig.maxHeight;
		targetConfig.maxWidth = defaultConfig.maxWidth;
		targetConfig.minHeight = defaultConfig.minHeight;
		targetConfig.minWidth = defaultConfig.minWidth;
	}
	
	private void checkPositionAndSize(ViewElementConfig vec){
		LayoutParams lp = vec.getLayoutParams();
		if (vec.getResizeConfig().maxHeight > tRealScreenSize.y) {
			lp.height = tRealScreenSize.y;
		}else{
			lp.height = vec.getResizeConfig().maxHeight;
		}
		if (vec.getResizeConfig().maxWidth > tRealScreenSize.x) {
			lp.width = tRealScreenSize.x;
		}else{
			lp.width = vec.getResizeConfig().maxWidth;
		}
		if(lp.height+lp.y>tRealScreenSize.y)
			lp.y=tRealScreenSize.y-lp.height;
		if(lp.width+lp.x>tRealScreenSize.x)
			lp.x=tRealScreenSize.x-lp.width;
		if (lp.y < 0) {lp.y = 0;}
		if (lp.x < 0) {lp.x = 0;}
	}
}
