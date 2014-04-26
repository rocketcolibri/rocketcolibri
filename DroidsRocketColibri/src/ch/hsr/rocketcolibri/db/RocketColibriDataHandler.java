package ch.hsr.rocketcolibri.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import ch.futuretek.json.JsonTransformer;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.db.model.JsonRCModel;
import ch.hsr.rocketcolibri.db.model.RCModel;

public class RocketColibriDataHandler {

	private Context tContext;
	private RocketColibriDB tRocketColibriDB;

	public RocketColibriDataHandler(Context context, RocketColibriDB db) throws Exception {
		tContext = context;
		tRocketColibriDB = db;
		process();
	}
	
	private void process() throws Exception{
		List<JsonRCModel> models = readJsonData();
		for(JsonRCModel m : models){
			if(m.process.equals("insert")){
				if(tRocketColibriDB.fetchRCModelByName(m.model.getName())==null)
					tRocketColibriDB.store(m.model);
			}else if(m.process.equals("update")){
				RCModel dbModel = tRocketColibriDB.fetchRCModelByName(m.model.getName());
				if (dbModel == null) {
					tRocketColibriDB.store(m.model);	// couldn't find in database, insert it
				}
				else {
					dbModel.setName(m.model.getName());
					dbModel.setViewElementConfigs(m.model.getViewElementConfigs());
					tRocketColibriDB.store(dbModel);	// found in database, update it
				}
			}else if(m.process.equals("delete")){
				tRocketColibriDB.delete(tRocketColibriDB.fetchRCModelByName(m.model.getName()));
			}
		}
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
}