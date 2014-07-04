/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import ch.futuretek.json.JsonTransformer;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RCConstants;
import ch.hsr.rocketcolibri.RocketColibriDefaults;
import ch.hsr.rocketcolibri.db.model.JsonRCModel;
import ch.hsr.rocketcolibri.db.model.LastUpdateFromFile;
import ch.hsr.rocketcolibri.db.model.RCModel;
import ch.hsr.rocketcolibri.view.widget.Circle;
import ch.hsr.rocketcolibri.view.widget.ConnectedUserInfoWidget;
import ch.hsr.rocketcolibri.view.widget.ConnectionStatusWidget;
import ch.hsr.rocketcolibri.view.widget.RCWidgetConfig;

/**
 * @author Artan Veliju
 */
public class RocketColibriDataHandler {

	private Context tContext;
	private RocketColibriDB tRocketColibriDB;

	public RocketColibriDataHandler(Context context, RocketColibriDB db) throws Exception {
		tContext = context;
		tRocketColibriDB = db;
//		makeJsonTestPrintOut();
		process();
	}

	private void process() throws Exception{
		List<JsonRCModel> models = readJsonData();
		LastUpdateFromFile luff = fetchLastUpdateFromFile();
		boolean updated = false;
		for(JsonRCModel m : models){
			if(m.process.equals("insert") && needToUpdate(luff, m.getTimestampAsDate().getTime())){
				dpToPixel(m.model.getWidgetConfigs());
				tRocketColibriDB.store(m.model);
				updated = true;
			}else if(m.process.equals("update") && needToUpdate(luff, m.getTimestampAsDate().getTime())){
				RCModel dbModel = tRocketColibriDB.fetchRCModelByName(m.model.getName());
				dpToPixel(m.model.getWidgetConfigs());
				if (dbModel == null) {
					tRocketColibriDB.store(m.model);	// couldn't find in database, insert it
				} else {
					dbModel.setName(m.model.getName());
					dbModel.setWidgetConfigs(m.model.getWidgetConfigs());
					tRocketColibriDB.store(dbModel);	// found in database, update it
				}
				updated = true;
			}else if(m.process.equals("delete") && needToUpdate(luff, m.getTimestampAsDate().getTime())){
				tRocketColibriDB.delete(tRocketColibriDB.fetchRCModelByName(m.model.getName()));
				updated = true;
			}
		}
		if(updated){
			luff.timestamp = System.currentTimeMillis();
			tRocketColibriDB.store(luff);
		}
	}
	
	private void dpToPixel(List<RCWidgetConfig> wcs){
		float density = tContext.getResources().getDisplayMetrics().density;
		for(RCWidgetConfig wc : wcs){
			RocketColibriDefaults.dpToPixel(density, wc.viewElementConfig);
		}
	}
	
	private boolean needToUpdate(LastUpdateFromFile luff, long fileTimeStamp){
		return luff.timestamp<fileTimeStamp;
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

	private List<JsonRCModel> readJsonData() throws Exception {
		try {
			Resources res = tContext.getResources();
			InputStream is = res.openRawResource(R.raw.rc);
			return new JsonTransformer().transformList(JsonRCModel.class, streamToString(is));
		} catch (Exception e) {
			throw e;
		}
	}

	private String streamToString(InputStream is) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(is, "UTF-8"));
		String line = bufferedReader.readLine();
		while (line != null) {
			sb.append(line);
			sb.append('\n');
			line = bufferedReader.readLine();
		}
		return sb.toString();
	}

	/**
	 * This method is just for create a Json output from the Class Model
	 * IT IS NOT USED ON RUNTIME OR PRODUCTION !
	 */
	private void makeJsonTestPrintOut(){
		RCModel model = new RCModel();
		model.setName("Test Model");
		List<RCWidgetConfig> widgetConfigs = new ArrayList<RCWidgetConfig>();
		Map<String, String> tProtocolMap = new HashMap<String, String>();
		tProtocolMap.put(RCConstants.CHANNEL_ASSIGNMENT_H, "2");
		tProtocolMap.put(RCConstants.INVERTED_H, "1");
		tProtocolMap.put(RCConstants.MAX_RANGE_H, "");
		tProtocolMap.put(RCConstants.MIN_RANGE_H, "");
		tProtocolMap.put(RCConstants.TRIMM_H, "");
		tProtocolMap.put(RCConstants.CHANNEL_ASSIGNMENT_V, "");
		tProtocolMap.put(RCConstants.INVERTED_V, "");
		tProtocolMap.put(RCConstants.MAX_RANGE_V, "2");
		tProtocolMap.put(RCConstants.MIN_RANGE_V, "1");
		tProtocolMap.put(RCConstants.TRIMM_V, "");
		widgetConfigs.add(new RCWidgetConfig(tProtocolMap, Circle.getDefaultViewElementConfig()));
		widgetConfigs.add(new RCWidgetConfig(ConnectionStatusWidget.getDefaultViewElementConfig()));
		widgetConfigs.add(new RCWidgetConfig(ConnectedUserInfoWidget.getDefaultViewElementConfig()));
		model.setWidgetConfigs(widgetConfigs);
		
		List<JsonRCModel> jsons = new ArrayList<JsonRCModel>();
		JsonRCModel j = new JsonRCModel();
		j.model = model;
		j.process = "insert";
		jsons.add(j);
		
		model = new RCModel();
		model.setName("Test Model 2");
		widgetConfigs = new ArrayList<RCWidgetConfig>();
		tProtocolMap = new HashMap<String, String>();
		tProtocolMap.put(RCConstants.CHANNEL_ASSIGNMENT, "2");
		tProtocolMap.put(RCConstants.INVERTED, "1");
		tProtocolMap.put(RCConstants.MAX_RANGE, "");
		tProtocolMap.put(RCConstants.MIN_RANGE, "");
		tProtocolMap.put(RCConstants.TRIMM, "");
		widgetConfigs.add(new RCWidgetConfig(tProtocolMap, Circle.getDefaultViewElementConfig()));
		widgetConfigs.add(new RCWidgetConfig(ConnectionStatusWidget.getDefaultViewElementConfig()));
		model.setWidgetConfigs(widgetConfigs);
		
		j = new JsonRCModel();
		j.model = model;
		j.process = "insert";
		jsons.add(j);
		System.out.println(new JsonTransformer().unsafeTransform(jsons));
	}
	
}
