package ch.hsr.rocketcolibri.activity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.SystemClock;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RCConstants;

import com.fortysevendeg.swipelistview.SwipeListView;

import java.util.List;

public class PackageAdapter extends BaseAdapter {

    private List<ModelRow> data;
    private ModelListActivity context;
    private SwipeListView swipeListView;

    public PackageAdapter(ModelListActivity context, List<ModelRow> data, SwipeListView swipeListView) {
        this.context = context;
        this.data = data;
        this.swipeListView = swipeListView;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public ModelRow getItem(int position) {
        return data.get(position);
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
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
				context.setSelectedItem(position);
				Intent cameraIntent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				context.startActivityForResult(cameraIntent, RCConstants.CAPTURE_RESULT_CODE);
			} 
		});

        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	enableEditModeButtons(holder, true);
            	swipeListView.closeAnimate(position);
            }
        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	swipeListView.dismiss(position);
            	context.deleteItem(position);
            }
        });
        holder.acceptEditBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	ViewGroup vg = (ViewGroup) v.getParent().getParent();
            	String name = "nothing";
            	try{
            		name = ((EditText)vg.findViewById(R.id.model_row_title)).getText().toString();
            	}catch(Exception e){e.printStackTrace();}
            	context.saveItem(position, name);
            	enableEditModeButtons(holder, false);
            }
		});
        holder.cancelEditBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				context.cancelItem(position);
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