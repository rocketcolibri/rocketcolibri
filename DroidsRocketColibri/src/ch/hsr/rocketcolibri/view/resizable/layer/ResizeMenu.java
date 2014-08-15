package ch.hsr.rocketcolibri.view.resizable.layer;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.view.AbsoluteLayout;
import ch.hsr.rocketcolibri.view.custimizable.ICustomizableView;
import ch.hsr.rocketcolibri.view.resizable.ResizeConfig;

public class ResizeMenu extends LinearLayout{

	private MenuListener tMListener;
	private ImageView tMaximizedIv;
	private ImageView tMinimizedIv;
	
	public ResizeMenu(Context context, AbsoluteLayout parent, View target, MenuListener mListener){
		this(context, parent, target, new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT,  AbsoluteLayout.LayoutParams.WRAP_CONTENT, 0, 0), mListener);
	}
	
	public ResizeMenu(Context context, AbsoluteLayout parent, View target, AbsoluteLayout.LayoutParams lp, MenuListener mListener){
		super(context);
		setOrientation(LinearLayout.HORIZONTAL);
		tMListener = mListener;
		float density = getContext().getResources().getDisplayMetrics().density;
		tMaximizedIv = new ImageView(getContext());
		settings(density, tMaximizedIv);
		tMaximizedIv.setImageResource(R.drawable.maximize);
		addView(tMaximizedIv);
		tMaximizedIv.setOnClickListener(new OnClickListener(){public void onClick(View v){
			tMListener.maximize();
			enableMaximizedAndMinimized(true);
		}});
		tMinimizedIv = new ImageView(getContext());
		settings(density, tMinimizedIv);
		tMinimizedIv.setImageResource(R.drawable.minimize);
		addView(tMinimizedIv);
		tMinimizedIv.setOnClickListener(new OnClickListener(){public void onClick(View v){
			tMListener.minimize();
			enableMaximizedAndMinimized(false);
		}});
		AbsoluteLayout.LayoutParams tResizeTargetLP = (ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams) target.getLayoutParams();
		try{
			ResizeConfig tConfig = ((ICustomizableView)target).getWidgetConfig().viewElementConfig.getResizeConfig();
			enableMaximized(!(tResizeTargetLP.height==tConfig.maxHeight && tResizeTargetLP.width==tConfig.maxWidth));
			enableMinimized(!(tResizeTargetLP.height==tConfig.minHeight && tResizeTargetLP.width==tConfig.minWidth));
		}catch(Exception e){}
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
	
	private void enableMaximizedAndMinimized(boolean isMaximized){
		enableMaximized(!isMaximized);
		enableMinimized(isMaximized);
	}
	
	private void enableMaximized(boolean enable){
		tMaximizedIv.setAlpha(enable?1f:0.4f);
	}
	
	private void enableMinimized(boolean enable){
		tMinimizedIv.setAlpha(enable?1f:0.4f);
	}
}
