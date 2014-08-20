package ch.hsr.rocketcolibri.activity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RCConstants;
import ch.hsr.rocketcolibri.RocketColibriDefaults;
import ch.hsr.rocketcolibri.activity.modellist.ModelListAdapter;
import ch.hsr.rocketcolibri.activity.modellist.ModelRow;
import ch.hsr.rocketcolibri.activity.modellist.ModelRowActionListener;
import ch.hsr.rocketcolibri.db.RocketColibriDB;
import ch.hsr.rocketcolibri.db.model.Defaults;
import ch.hsr.rocketcolibri.db.model.RCModel;
import ch.hsr.rocketcolibri.util.CacheUtil;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

public class ModelListActivity extends RCActivity {

    private ModelListAdapter tAdapter;
    private List<ModelRow> tData;
    private SwipeListView tSwipeListView;
    private ModelRow tSelectedRow;
    private boolean loadOnce = true;
    private RocketColibriDB db;
    private CacheUtil tCacheUtil;
    private boolean tFirstTime = true;
    private OID tSelectedOnDesktop;
    private int tDeviceIconWith;
    private long tLoastLeaveToast;
    private long tOutsideDown;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.model_list);
        tDeviceIconWith = RocketColibriDefaults.dpToPixelX(getResources().getDisplayMetrics(), (int)getResources().getDimension(R.dimen.size_list_image));
        tData = new ArrayList<ModelRow>();
        tCacheUtil = new CacheUtil(ModelListActivity.this);
        tSwipeListView = (SwipeListView) findViewById(R.id.listView);
        tAdapter = new ModelListAdapter(this, tData, createRowActionListener());

        findViewById(R.id.newBtn).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				createItem();
			}
		});

        tSwipeListView.setSwipeListViewListener(new BaseSwipeListViewListener() {
            public void onOpened(int position, boolean toRight) {}
            public void onClosed(int position, boolean fromRight) {}
            public void onListChanged() {}
            public void onMove(int position, float x) {}
            public void onStartOpen(int position, int action, boolean right) {}
            public void onStartClose(int position, boolean right) {}
            public void onClickFrontView(int position) {
				Intent i = new Intent(getIntent().getAction());
				i.putExtra(RCConstants.FLAG_ACTIVITY_RC_MODEL, tAdapter.getItem(position).getName());
            	setResult(RCConstants.RC_MODEL_RESULT_CODE, i);
            	finish();
            }
            public void onClickBackView(int position) {}

        });
        tSwipeListView.setAdapter(tAdapter);
        showLoading();
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	return true;
    }
    
	public boolean dispatchTouchEvent(MotionEvent ev) {
		boolean result = super.dispatchTouchEvent(ev);
		Rect dialogRect = new Rect();
		getWindow().getDecorView().getHitRect(dialogRect);
		if (!dialogRect.contains((int) ev.getX(), (int) ev.getY())) {
			if (ev.getAction() == MotionEvent.ACTION_DOWN) {
				tOutsideDown = System.currentTimeMillis();
			}else if (ev.getAction() == MotionEvent.ACTION_UP) {
				if (System.currentTimeMillis() - tOutsideDown < 2000) {
					if (tFirstTime) {
						if (System.currentTimeMillis() - tLoastLeaveToast > 8000) {
							tLoastLeaveToast = System.currentTimeMillis();
							uitoast(getString(R.string.model_list_leave_toast));
						}
					} else {
						finish();
					}
					return true;
				}
			}
		}
		return result;
	}

	@Override
	protected void onServiceReady() {
		db = rcService.getRocketColibriDB();
        try{
        	if(getIntent()!=null && getIntent().hasExtra(RCConstants.FLAG_ACTIVITY_RC_MODEL)){
        		String modelName = getIntent().getStringExtra(RCConstants.FLAG_ACTIVITY_RC_MODEL);
        		if(modelName!=null){
        			RCModel m = db.fetchRCModelByName(modelName);
        			if(m!=null){
        				tSelectedOnDesktop = db.getOdb().getObjectId(m);
        				tFirstTime = false;
        			}else{
        				tFirstTime = true;
        			}
        		}else{
        			tFirstTime = true;
        		}
        	}else{
        		tFirstTime = true;
        	}
        }catch(Exception e){
        	tFirstTime = true;
        }
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
				photo = tCacheUtil.centerCropBitmap(photo);
				photo = Bitmap.createScaledBitmap(photo, tDeviceIconWith, tDeviceIconWith, true);
			}catch(Exception e){
				//canceled
				return;
			}
        	try {
				tCacheUtil.storeBitmap(photo, getIconNameFromModelOID(tSelectedRow.getId()), 50);
				tSelectedRow.setIcon(new BitmapDrawable(getResources(), photo));
				tAdapter.notifyDataSetChanged();
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
            tData.clear();
            tData.addAll(result);
            tAdapter.notifyDataSetChanged();
            hideLoading();
        }
    }
    
    private ModelRow createModelRow(RCModel model, OID oid){
		ModelRow item = new ModelRow();
		if(oid==null){oid = db.getOdb().getObjectId(model);}
		item.setId(oid);
        item.setName(model.getName());
        try {
        	item.setIcon(new BitmapDrawable(getResources(), tCacheUtil.loadBitmap(getIconNameFromModelOID(item.getId()))));
		} catch (FileNotFoundException e) {
			item.setIcon(getNoPicDrawable());
		}
        item.setDescription(MessageFormat.format(getString(R.string.model_list_row_description), model.getWidgetConfigs()==null?0:model.getWidgetConfigs().size()));
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
    
    private ModelRowActionListener createRowActionListener(){
    	return new ModelRowActionListener() {
			public void icon(int position){
				tSwipeListView.closeAnimate(position);
				tSelectedRow = tAdapter.getItem(position);
				Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(cameraIntent, RCConstants.CAPTURE_RESULT_CODE);
			}

			public boolean saveItem(int position, String newName) {
				ModelRow pi = tAdapter.getItem(position);
				if(newName.equals(pi.getName()))return true;
				RCModel m = (RCModel) db.getOdb().getObjectFromId(pi.getId());
				if(m!=null){
					RCModel nameExistsModel = db.fetchRCModelByName(newName);
					if(nameExistsModel!=null){
						if(!pi.getId().equals(db.getOdb().getObjectId(nameExistsModel))){
							uitoast(getString(R.string.model_list_name_already_exists));
							return false;
						}
					}
					m.setName(newName);
					db.store(m);
					pi.setName(newName);
				}
				tAdapter.notifyDataSetChanged();
				return true;
			}
			
			public void edit(int position) {
				tSwipeListView.closeAnimate(position);
			}
			
			public boolean deleteItem(final int position) {
				runOnUiThread(new Runnable() {public void run() {
					tSwipeListView.dismiss(position);
					ModelRow pi = tAdapter.getItem(position);
					if(pi.getId().equals(tSelectedOnDesktop)){
						try{
							Defaults def = (Defaults) db.fetch(Defaults.class).getFirst();
							db.delete(def);
							tFirstTime = true;
						}catch(Exception e){}
					}
					tData.remove(pi);
					db.getOdb().deleteObjectWithId(pi.getId());
					db.getOdb().commit();
					tAdapter.notifyDataSetChanged();
				}});
				return true;
			}
			
			public void cancelItem(int position) {
				tAdapter.notifyDataSetChanged();
			}
		};
    }
	
	public void createItem(){
		RCModel m = new RCModel();
		m.setName("");
		OID oid = db.store(m);
		m.setName(MessageFormat.format(getString(R.string.model_list_new_model_name), oid.getObjectId()));
		oid = db.store(m);
		tData.add(createModelRow(m, oid));
		tAdapter.notifyDataSetChanged();
		tSwipeListView.setSelection(tAdapter.getCount()-1);
	}

}