package ch.hsr.rocketcolibri.db;

import java.io.File;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;

import android.content.Context;
import ch.hsr.rocketcolibri.db.model.RCModel;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;

public class RocketColibriDB {
	private ODB tOdb;
	
	public RocketColibriDB(Context context){
		File directory = context.getDir("data", Context.MODE_PRIVATE);
        String fileName = directory.getAbsolutePath() + "/rocketcolibri.db";
        tOdb = ODBFactory.open(fileName);
	}

	public Objects fetchAllRCModels(){
		return tOdb.getObjects(RCModel.class);
	}
	
	/*not needed at the moment*/
	public ViewElementConfig fetchViewElementConfigById(int id){
		try{
			return  (ViewElementConfig)tOdb.getObjects(new CriteriaQuery(ViewElementConfig.class, Where.equal("id", id))).getFirst();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public RCModel fetchRCModelByName(String name){
		try{
			return  (RCModel)tOdb.getObjects(new CriteriaQuery(RCModel.class, Where.equal("name", name))).getFirst();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public void delete(Object obj){
		tOdb.deleteCascade(obj);
		tOdb.commit();
	}
	
	public void store(Object obj){
		tOdb.store(obj);
		tOdb.commit();
	}
	
	public void close(){
		tOdb.close();
	}

}
