/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.neodatis.odb.Objects;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import ch.futuretek.json.JsonTransformer;
import ch.futuretek.json.exception.TransformException;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RocketColibriDefaults;
import ch.hsr.rocketcolibri.db.model.JsonRCModel;
import ch.hsr.rocketcolibri.db.model.RCModel;
import ch.hsr.rocketcolibri.util.AndroidUtil;
import ch.hsr.rocketcolibri.view.widget.RCWidgetConfig;

/**
 * @author Artan Veliju
 */
public class RocketColibriDataHandler {

	private Context tContext;
	private RocketColibriDB tRocketColibriDB;
	private Point tRealSize;


	public RocketColibriDataHandler(Context context, RocketColibriDB db) throws Exception {
		this(context, db, true);
	}
	
	public RocketColibriDataHandler(Context context, RocketColibriDB db, boolean checkIfItsFirstTime) throws Exception{
		tContext = context;
		tRocketColibriDB = db;
		tRealSize = new AndroidUtil(tContext).getRealSize();
		if(checkIfItsFirstTime){
			new RCDBProcessor(tContext, tRocketColibriDB, true).process(inputStreamToJsonRCModel(tContext.getResources().openRawResource(R.raw.rc)));
		}
	}
	
	public boolean importData(InputStream is){
		try {
			new RCDBProcessor(tContext, tRocketColibriDB, false).process(inputStreamToJsonRCModel(is));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * @return stored cache file with the json models
	 */
	public File exportDataToFile(){
		//create wrapper list
		List<JsonRCModel> jsons = new ArrayList<JsonRCModel>();
		Objects<RCModel> rcModels = tRocketColibriDB.fetchAllRCModels();
		if(rcModels!=null && rcModels.size()>0){
			for(RCModel m : rcModels){
				JsonRCModel j = new JsonRCModel();
				j.pixelHeight = tRealSize.y;
				j.pixelWidth = tRealSize.x;
				//exportCopy is needed otherwise we override the RCWidgetConfigs by calculating from pixel to dp
				RCModel exportCopy = m.copy();
				j.model = exportCopy;
				pixelToDP(exportCopy.getWidgetConfigs());
				j.process = RCDBProcessor.INSERT;
				j.timestampToNow();
				jsons.add(j);
			}
		}
		//create file and store data to it
		File file = null;
		try {
			file = storeToFile(new JsonTransformer().unsafeTransform(jsons), tContext.getString(R.string.export_file_name));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	private File storeToFile(String data, String fileName) throws IOException {
		File cacheFile = new File(tContext.getCacheDir(),fileName);
		cacheFile.createNewFile();
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(cacheFile), "UTF-8");
		osw.write(data);
		osw.flush();
		osw.close();
		return cacheFile;
	}
	
	private void pixelToDP(List<RCWidgetConfig> wcs){
		DisplayMetrics dm = tContext.getResources().getDisplayMetrics();
		for(RCWidgetConfig wc : wcs){
			RocketColibriDefaults.pixelToDp(dm, wc.viewElementConfig);
		}
	}

	private List<JsonRCModel> inputStreamToJsonRCModel(InputStream is) throws TransformException, IOException{
		List<JsonRCModel> result = new JsonTransformer().transformList(JsonRCModel.class, streamToString(is));
		is.close();
		return result;
	}

	private String streamToString(InputStream is) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		String line = bufferedReader.readLine();
		while (line != null) {
			sb.append(line);
			sb.append('\n');
			line = bufferedReader.readLine();
		}
		return sb.toString();
	}
}
