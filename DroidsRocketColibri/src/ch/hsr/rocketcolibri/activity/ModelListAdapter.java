package ch.hsr.rocketcolibri.activity;
import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RCConstants;

import com.fortysevendeg.swipelistview.SwipeListView;

import java.util.List;

public class ModelListAdapter extends BaseAdapter {

    private List<ModelRow> tData;
    private ModelListActivity tContext;
    private SwipeListView tSwipeListView;

    public ModelListAdapter(ModelListActivity context, List<ModelRow> data, SwipeListView swipeListView) {
        this.tContext = context;
        this.tData = data;
        this.tSwipeListView = swipeListView;
    }

    @Override
    public int getCount() {
        return tData.size();
    }

    @Override
    public ModelRow getItem(int position) {
        return tData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

//    @Override
//    public boolean isEnabled(int position) {
//        if (position == 2) {
//            return false;
//        } else {
//            return true;
//        }
//    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ModelRow item = getItem(position);
        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) tContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(R.layout.model_row, parent, false);
            holder = new ViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.model_row_icon);
            holder.title = (EditText) convertView.findViewById(R.id.model_row_title);
            holder.description = (TextView) convertView.findViewById(R.id.model_row_description);
            holder.editBtn = (ImageView) convertView.findViewById(R.id.model_row_editBtn);
            holder.deleteBtn = (ImageView) convertView.findViewById(R.id.model_row_deleteBtn);
            holder.acceptEditBtn = (ImageView) convertView.findViewById(R.id.model_row_acceptEditBtn);
            holder.cancelEditBtn = (ImageView) convertView.findViewById(R.id.model_row_cancelEditBtn);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ((SwipeListView)parent).recycle(convertView, position);

        holder.icon.setImageDrawable(item.getIcon());
        holder.title.setText(item.getName());
        holder.description.setText(item.getDescription());
        
        holder.icon.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				tContext.setSelectedItem(position);
				Intent cameraIntent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				tContext.startActivityForResult(cameraIntent, RCConstants.CAPTURE_RESULT_CODE);
			} 
		});

        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	enableEditModeButtons(holder, true);
            	tSwipeListView.closeAnimate(position);
            }
        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	tSwipeListView.dismiss(position);
            	tContext.deleteItem(position);
            }
        });
        holder.acceptEditBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	String name = "nothing";
            	try{
            		name = holder.title.getText().toString();
            	}catch(Exception e){e.printStackTrace();}
            	if(tContext.saveItem(position, name)){
            		enableEditModeButtons(holder, false);
            	}else{
            		holder.title.setText(tData.get(position).getName());
            	}
            }
		});
        holder.cancelEditBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				tContext.cancelItem(position);
	        	enableEditModeButtons(holder, false);
			}
		});
        return convertView;
    }
    
    private void enableEditModeButtons(ViewHolder holder, final boolean enable){
    	int visibility = enable?View.VISIBLE:View.INVISIBLE;
    	holder.title.setEnabled(enable);
    	if(enable) 
    		holder.title.setFocusable(enable);
    	holder.title.setLongClickable(enable);
    	holder.title.setClickable(enable);
    	holder.title.setInputType(enable?InputType.TYPE_CLASS_TEXT:InputType.TYPE_NULL);
    	holder.acceptEditBtn.setVisibility(visibility);
    	holder.cancelEditBtn.setVisibility(visibility);
    }

    static class ViewHolder {
        ImageView icon;
        EditText title;
        TextView description;
        ImageView editBtn;
        ImageView deleteBtn;
        ImageView acceptEditBtn;
        ImageView cancelEditBtn;
    }

}