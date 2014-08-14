/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.menu;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.manager.IDesktopViewManager;
import ch.hsr.rocketcolibri.view.AbsoluteLayout;
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.HoldImageView.OnHoldListener;
import ch.hsr.rocketcolibri.view.HoldImageView;
import ch.hsr.rocketcolibri.view.custimizable.ICustomizableView;
import ch.hsr.rocketcolibri.view.popup.PopupWindow;
import ch.hsr.rocketcolibri.view.resizable.ViewElementCustomizer;
import ch.hsr.rocketcolibri.view.widget.IRCWidget;
import ch.hsr.rocketcolibri.view.widget.RCWidgetConfig;

/**
 * @author Artan Veliju
 */
public class CustomizeModusPopupMenu extends PopupWindow{

	private View tTargetView;
	private SeekBar alphaChangeSlider;
	private IDesktopViewManager tDesktopViewManager;
	private ImageView tEditIv;
	private ImageView tMaximizeIv;
	private ImageView tMinimizeIv;
	private ViewElementCustomizer viewCustomizer = new ViewElementCustomizer();
	
	public CustomizeModusPopupMenu(IDesktopViewManager desktopViewManager, View contentView){
		super((AbsoluteLayout) desktopViewManager.getRootView(), contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		setTouchable(true);
		setClippingEnabled(false);
		tDesktopViewManager = desktopViewManager;
		setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setAnimationStyle(R.style.PopupAnimation);
		setSoftInputMode(android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
		onCreate();
	}
	
	private void onCreate(){
		ImageView b = (ImageView) findViewById(R.id.resizeIv);
		b.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dismiss();
				tDesktopViewManager.resizeView((View) tTargetView);
			}
		});
		tMaximizeIv = (ImageView) findViewById(R.id.maximizeIv);
		tMaximizeIv.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try{
					viewCustomizer.setTargetView(tTargetView);
					viewCustomizer.maximize();
					enableMaximizedAndMinimized(true);
				}catch(Exception e){e.printStackTrace();}
			}
		});
		tMinimizeIv = (ImageView) findViewById(R.id.minimizeIv);
		tMinimizeIv.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try{
					viewCustomizer.setTargetView(tTargetView);
					viewCustomizer.minimize();
					enableMaximizedAndMinimized(false);
				}catch(Exception e){e.printStackTrace();}
			}
		});
		tEditIv = (ImageView) findViewById(R.id.editChannel);
		tEditIv.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				tDesktopViewManager.startEditActivity((View) tTargetView);
			}
		});
		
		findViewById(R.id.duplicateBtn).setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				try{ 
					RCWidgetConfig rcwc = ((IRCWidget)tTargetView).getWidgetConfig().copy();
					AbsoluteLayout.LayoutParams lp = rcwc.viewElementConfig.getLayoutParams();
					AbsoluteLayout rootView = tDesktopViewManager.getRootView();
					lp.x = (int) (rootView.getWidth()/2)-lp.width/2;
					lp.y = (int) (rootView.getHeight()/2)-lp.height/2;
					ICustomizableView v1 = (ICustomizableView) tDesktopViewManager.createAndAddView(rcwc);
					v1.setCustomizeModus(true);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		
		HoldImageView deleteElementBtn = (HoldImageView) findViewById(R.id.deleteElementBtn);
		deleteElementBtn.setOnHoldListener(new OnHoldListener() {
			AlphaAnimation deleteAnimation = new AlphaAnimation(0.75f, 0.009f);
			public void onHoldStart(View v, int overallDuration) {
				deleteAnimation.setDuration(overallDuration);
				deleteAnimation.setFillAfter(true);
				tTargetView.startAnimation(deleteAnimation);
			}
			public void onHoldEnd(View v) {
				tDesktopViewManager.deleteView((View) tTargetView);
				dismiss();
			}
			public void onHoldCanceled() {
				deleteAnimation.cancel();
				tTargetView.setAnimation(null);
			}
		});
		
		alphaChangeSlider = (SeekBar)findViewById(R.id.alphaSlider);
		alphaChangeSlider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {
				tDesktopViewManager.viewChanged((View) tTargetView);
			}
			public void onStartTrackingTouch(SeekBar seekBar) {}
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				tTargetView.setAlpha(progress/100f);
			}
		});
	}

	public void show(View cView){
		dismissPopupIfIsShowing();
		tTargetView = cView;
		viewCustomizer.setTargetView(tTargetView);
		viewCustomizer.enableView(tMaximizeIv, !viewCustomizer.isMaximized());
		viewCustomizer.enableView(tMinimizeIv, !viewCustomizer.isMinimized());
		try {
			if (((IRCWidget) cView).getProtocolMap() != null) {
				setVisibilityOfEditChannelBtn(View.VISIBLE);
			} else {setVisibilityOfEditChannelBtn(View.GONE);}
		} catch (Exception e) {setVisibilityOfEditChannelBtn(View.GONE);}
		alphaChangeSlider.setProgress((int)(tTargetView.getAlpha()*100f));
		showAtBestPosition(cView);
	}
	
	private void setVisibilityOfEditChannelBtn(int visibility){
		if(tEditIv.getVisibility()!=visibility){
			tEditIv.setVisibility(visibility);
			switch(tEditIv.getVisibility()){
			case View.VISIBLE:updateLayoutHeight(getHeight()+tEditIv.getHeight());
			case View.GONE:updateLayoutHeight(getHeight()-tEditIv.getHeight());
			}
		}
	}
	
	private void enableMaximizedAndMinimized(boolean isMaximized){
		viewCustomizer.enableView(tMaximizeIv, !isMaximized);
		viewCustomizer.enableView(tMinimizeIv, isMaximized);
	}
	
	private void dismissPopupIfIsShowing(){
    	if(isShowing())
    		dismiss();
	}
	
	private View findViewById(int id){
		return getContentView().findViewById(id);
	}
	
}
