package ch.hsr.rocketcolibri.view.resizable.layer;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.view.AbsoluteLayout;

public class ResizeMenu extends LinearLayout{

	private MenuListener tMListener;
	
	public ResizeMenu(Context context, MenuListener mListener){
		this(context, new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,  AbsoluteLayout.LayoutParams.WRAP_CONTENT, 0, 0), mListener);
	}
	
	public ResizeMenu(Context context, AbsoluteLayout.LayoutParams lp, MenuListener mListener){
		super(context);
		setOrientation(LinearLayout.HORIZONTAL);
		tMListener = mListener;
		onCreate();
	}
	
	protected void onCreate(){
		float density = getContext().getResources().getDisplayMetrics().density;
		ImageView maximize = new ImageView(getContext());
		settings(density, maximize);
		maximize.setImageResource(R.drawable.maximize);
		addView(maximize);
		maximize.setOnClickListener(new OnClickListener(){public void onClick(View v){tMListener.maximize();}});
		ImageView minimize = new ImageView(getContext());
		settings(density, minimize);
		minimize.setImageResource(R.drawable.minimize);
		addView(minimize);
		minimize.setOnClickListener(new OnClickListener(){public void onClick(View v){tMListener.minimize();}});
	}
	
	private void settings(float density, ImageView iv){
		iv.setScaleType(ScaleType.FIT_CENTER);
		int paddingPixel = dpToPx(8);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(96, 96);
		layoutParams.setMargins(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
		iv.setLayoutParams(layoutParams);
		iv.setAdjustViewBounds(true);
	}
	
	private int dpToPx(int px){
		return dpToPx(getContext().getResources().getDisplayMetrics(), px);
	}
	
	public int pxToDp(DisplayMetrics dMetrics, int px) {
	    int dp = Math.round(px / (dMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
	    return dp;
	}
	
	public int dpToPx(DisplayMetrics dMetrics, int dp) {
	    int px = Math.round(dp * (dMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));       
	    return px;
	}
}
