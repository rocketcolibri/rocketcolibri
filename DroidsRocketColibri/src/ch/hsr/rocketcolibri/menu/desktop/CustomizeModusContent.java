package ch.hsr.rocketcolibri.menu.desktop;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RocketColibriDefaults;
import ch.hsr.rocketcolibri.util.CacheUtil;
import ch.hsr.rocketcolibri.view.AbsoluteLayout;
import ch.hsr.rocketcolibri.view.custimizable.ICustomizableView;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import ch.hsr.rocketcolibri.widgetdirectory.WidgetEntry;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

public class CustomizeModusContent extends ModusContent{
	
	public CustomizeModusContent(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onCreate(List<WidgetEntry> widgetEntries) {
		GridView gLayout = (GridView) findViewById(R.id.widgetEntryContent);
		CacheUtil cacheUtil = new CacheUtil(tContext);
		int size = (int) tContext.getResources().getDimension(
				R.dimen.desktop_menu_customize_mode_widget_size);
		List<ImageView> widgetList = new ArrayList<ImageView>(
				widgetEntries.size());
		for (WidgetEntry wEntry : widgetEntries) {
			try {
				ImageView iView = new ImageView(tContext);
				iView.setLayoutParams(new GridView.LayoutParams(size, size));
				iView.setScaleType(ScaleType.CENTER);
				Bitmap viewBitmap = null;
				try {
					viewBitmap = cacheUtil.loadBitmap(wEntry.getClassPath());
					Log.d("loadBitmap", "" + wEntry.getClassPath());
				} catch (FileNotFoundException e) {
					Log.d("createBitmap", "" + wEntry.getClassPath());
					viewBitmap = cacheUtil.createBitmap(
							(View) createIconView(wEntry, size, size),
							wEntry.getClassPath());
				}
				iView.setImageBitmap(viewBitmap);
//				iView.setBackgroundResource(R.drawable.border);
				iView.setOnTouchListener(new WidgetTouchListener(wEntry));
				widgetList.add(iView);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		gLayout.setAdapter(new ImageAdapter(widgetList));

	}

	class WidgetTouchListener implements View.OnTouchListener {
		private WidgetEntry tWidgetEntry;

		public WidgetTouchListener(WidgetEntry wEntry) {
			tWidgetEntry = wEntry;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				tDesktopMenu.animateToggle();
				try {
					setupView(tWidgetEntry, event);
					Toast.makeText(tContext, tWidgetEntry.getLabelText(),
							Toast.LENGTH_SHORT).show();
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
			return true;
		}

	}

	/**
	 * @return View
	 * This View is meant for showing how the Widget will look like
	 * not for interaction with the View itself.
	 * @throws Exception 
	 */
	private ICustomizableView createIconView(WidgetEntry wEntry, int width, int height) throws Exception{
		ViewElementConfig vec = wEntry.getDefaultViewElementConfig();
		vec.setLayoutParams(new AbsoluteLayout.LayoutParams(width, height, 0, 0));
		return tDesktopMenu.getDesktopViewManager().createView(vec);
	}

	private ICustomizableView setupView(WidgetEntry we, MotionEvent e)
			throws Exception {
		ViewElementConfig vec = we.getDefaultViewElementConfig();
		AbsoluteLayout.LayoutParams lp = vec.getLayoutParams();
		AbsoluteLayout rootView = tDesktopMenu.getDesktopViewManager()
				.getRootView();
		lp.setX((int) (rootView.getWidth() / 2) - lp.width / 2);
		lp.setY((int) (rootView.getHeight() / 2) - lp.height / 2);
		ICustomizableView v1 = (ICustomizableView) tDesktopMenu
				.getDesktopViewManager().createAndAddView(vec);
		v1.setCustomizeModus(true);
		return v1;
	}

	class ImageAdapter extends BaseAdapter {
		private List<ImageView> widgetIcons;

		public ImageAdapter(List<ImageView> ws) {
			widgetIcons = ws;
		}

		public int getCount() {
			return widgetIcons.size();
		}

		public Object getItem(int position) {
			return widgetIcons.get(position);
		}

		public long getItemId(int position) {
			return 0;
		}

		// create a new ImageView for each item referenced by the Adapter
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = null;
			if (convertView == null) {
				imageView = widgetIcons.get(position);
			} else {
				imageView = (ImageView) convertView;
			}
			return imageView;
		}
	}
}
