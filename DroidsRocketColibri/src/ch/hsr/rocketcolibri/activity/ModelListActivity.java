package ch.hsr.rocketcolibri.activity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RCConstants;
import ch.hsr.rocketcolibri.db.RocketColibriDB;
import ch.hsr.rocketcolibri.db.model.RCModel;
import ch.hsr.rocketcolibri.util.CacheUtil;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

public class ModelListActivity extends RCActivity {

    private PackageAdapter adapter;
    private List<ModelRow> data;

    private SwipeListView swipeListView;

    private ModelRow selected;
    private boolean loadOnce = true;
    private RocketColibriDB db;
    private CacheUtil tCacheUtil;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.model_list);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
//        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        data = new ArrayList<ModelRow>();
        tCacheUtil = new CacheUtil(ModelListActivity.this);
        swipeListView = (SwipeListView) findViewById(R.id.listView);
        adapter = new PackageAdapter(this, data, swipeListView);

        findViewById(R.id.newBtn).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				createItem();
			}
		});
//        if (Build.VERSION.SDK_INT >= 11) {
//            swipeListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
//        }

        swipeListView.setSwipeListViewListener(new BaseSwipeListViewListener() {
            public void onOpened(int position, boolean toRight) {
            }
            public void onClosed(int position, boolean fromRight) {
            }
            public void onListChanged() {
            }
            public void onMove(int position, float x) {
            }
            public void onStartOpen(int position, int action, boolean right) {
            }
            public void onStartClose(int position, boolean right) {
            }
            public void onClickFrontView(int position) {
				Intent i = new Intent(getIntent().getAction());
				i.putExtra(RCConstants.FLAG_ACTIVITY_RC_MODEL, adapter.getItem(position).getName());
            	setResult(RCConstants.RC_MODEL_RESULT_CODE, i);
            	finish();
            }
            public void onClickBackView(int position) {
            }

        });

        swipeListView.setAdapter(adapter);

//        reload();

        showLoading(getString(R.string.loading));

    }

	@Override
	protected void onServiceReady() {
		db = rcService.getRocketColibriDB();
		if(loadOnce){
			loadOnce = false;
			new ListAppTask().execute();
		}
		
	}

    public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RCConstants.CAPTURE_RESULT_CODE) {
			Bitmap photo = null;
			try{
				photo = (Bitmap) data.getExtras().get("data");
			}catch(Exception e){
				//canceled
				return;
			}
        	try {
				tCacheUtil.storeBitmap(photo, getIconNameFromModelOID(selected.getId()), 50);
				selected.setIcon(new BitmapDrawable(getResources(), photo));
				adapter.notifyDataSetChanged();
			} catch (IOException e) {
				//something goes wrong on storing
				e.printStackTrace();
			}
		}
    }
    
    public class ListAppTask extends AsyncTask<Void, Void, List<ModelRow>> {

        protected List<ModelRow> doInBackground(Void... args) {
        	Objects<RCModel> obs = db.fetchAllRCModels();
        	Iterator<RCModel> it = obs.iterator();
        	List<ModelRow> models = new ArrayList<ModelRow>(obs.size());
        	RCModel model = null;
        	while(it.hasNext()){
        		model = it.next();
                models.add(createModelRow(model, null));
        	}
            return models;
        }

        protected void onPostExecute(List<ModelRow> result) {
            data.clear();
            data.addAll(result);
            adapter.notifyDataSetChanged();
            hideLoading();
        }
    }
    
    private ModelRow createModelRow(RCModel model, OID oid){
		ModelRow item = new ModelRow();
		if(oid==null){
			oid = db.getOdb().getObjectId(model);
		}
		item.setId(oid);
        item.setName(model.getName());
        try {
        	item.setIcon(new BitmapDrawable(getResources(), tCacheUtil.loadBitmap(getIconNameFromModelOID(item.getId()))));
		} catch (FileNotFoundException e) {
			item.setIcon(getNoPicDrawable());
		}
        item.setDescription(new StringBuffer("has ").append(model.getWidgetConfigs()==null?0:model.getWidgetConfigs().size()).append(" View Elements").toString());
        return item;
    }
    
    private Drawable getNoPicDrawable(){
    	return getResources().getDrawable(R.drawable.no_pic);
    }
    
    private String getIconNameFromModelOID(OID id){
    	return id.oidToString()+"-icon.png";
    }


	@Override
	protected String getClassName() {
		return null;
	}

    
    public void setSelectedItem(int position){
    	selected = adapter.getItem(position);
    }
    
	public void saveItem(int position, String newName) {
		ModelRow pi = adapter.getItem(position);
		if(newName.equals(pi.getName()))return;
		RCModel m = (RCModel) db.getOdb().getObjectFromId(pi.getId());
		if(m!=null){
			RCModel nameExistsModel = db.fetchRCModelByName(newName);
			if(nameExistsModel!=null){
				if(!pi.getId().equals(db.getOdb().getObjectId(nameExistsModel))){
					uitoast("name already exists");
					adapter.notifyDataSetChanged();
					return;
				}
			}
			m.setName(newName);
			db.store(m);
			pi.setName(newName);
		}
		adapter.notifyDataSetChanged();
	}
	
	public void createItem(){
		RCModel m = new RCModel();
		m.setName("");
		OID oid = db.store(m);
		m.setName("New Model ("+oid.getObjectId()+")");
		oid = db.store(m);
		data.add(createModelRow(m, oid));
		adapter.notifyDataSetChanged();
		swipeListView.setSelection(adapter.getCount()-1);
	}

	public void cancelItem(int position) {
		adapter.notifyDataSetChanged();
	}
	
	public void deleteItem(int position){
		ModelRow pi = adapter.getItem(position);
		data.remove(pi);
		db.getOdb().deleteObjectWithId(pi.getId());
		adapter.notifyDataSetChanged();
	}

}