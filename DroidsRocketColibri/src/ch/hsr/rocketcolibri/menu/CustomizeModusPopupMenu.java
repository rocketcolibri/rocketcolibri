/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.menu;

import org.neodatis.odb.core.transaction.IWriteAction;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.manager.IDesktopViewManager;
import ch.hsr.rocketcolibri.view.AbsoluteLayout;
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.HoldImageView.OnHoldListener;
import ch.hsr.rocketcolibri.view.HoldImageView;
import ch.hsr.rocketcolibri.view.popup.PopupWindow;
import ch.hsr.rocketcolibri.view.resizable.ResizeConfig;
import ch.hsr.rocketcolibri.view.widget.IRCWidget;

/**
 * @author Artan Veliju
 */
public class CustomizeModusPopupMenu extends PopupWindow{

	private View tTargetView;
	private SeekBar alphaChangeSlider;
	private IDesktopViewManager tDesktopViewManager;
	private ImageView tEditChannelBtn;
	
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
		b = (ImageView) findViewById(R.id.maximizeIv);
		b.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try{
					LayoutParams resizeTargetLP = (LayoutParams) tTargetView.getLayoutParams();
					ResizeConfig config = ((IRCWidget)tTargetView).getWidgetConfig().viewElementConfig.getResizeConfig();
					AbsoluteLayout parent = (AbsoluteLayout) tTargetView.getParent();
					resizeTargetLP.height = config.maxHeight;
					resizeTargetLP.width = config.maxWidth;
					if(resizeTargetLP.height+resizeTargetLP.y>parent.getHeight())
						resizeTargetLP.y=parent.getHeight()-resizeTargetLP.height;
					if(resizeTargetLP.width+resizeTargetLP.x>parent.getWidth())
						resizeTargetLP.x=parent.getWidth()-resizeTargetLP.width;
					parent.updateViewLayout(tTargetView, resizeTargetLP);
				}catch(Exception e){e.printStackTrace();}
			}
		});
		b = (ImageView) findViewById(R.id.minimizeIv);
		b.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try{
					LayoutParams resizeTargetLP = (LayoutParams) tTargetView.getLayoutParams();
					ResizeConfig config = ((IRCWidget)tTargetView).getWidgetConfig().viewElementConfig.getResizeConfig();
					AbsoluteLayout parent = (AbsoluteLayout) tTargetView.getParent();
					resizeTargetLP.height = config.minHeight;
					resizeTargetLP.width = config.minWidth;
					parent.updateViewLayout(tTargetView, resizeTargetLP);
				}catch(Exception e){e.printStackTrace();}
			}
		});
		tEditChannelBtn = (ImageView) findViewById(R.id.editChannel);
		tEditChannelBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				tDesktopViewManager.startEditActivity((View) tTargetView);
			}
		});
		;

		HoldImageView holdButton = (HoldImageView) findViewById(R.id.deleteElementBtn);
		holdButton.setOnHoldListener(new OnHoldListener() {
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
		try {
			if (((IRCWidget) cView).getProtocolMap() != null) {
				setVisibilityOfEditChannelBtn(View.VISIBLE);
			} else {setVisibilityOfEditChannelBtn(View.GONE);}
		} catch (Exception e) {setVisibilityOfEditChannelBtn(View.GONE);}
		alphaChangeSlider.setProgress((int)(tTargetView.getAlpha()*100f));
		showAtBestPosition(cView);
	}
	
	private void setVisibilityOfEditChannelBtn(int visibility){
		if(tEditChannelBtn.getVisibility()!=visibility){
			tEditChannelBtn.setVisibility(visibility);
			switch(tEditChannelBtn.getVisibility()){
			case View.VISIBLE:updateLayoutHeight(getHeight()+tEditChannelBtn.getHeight());
			case View.GONE:updateLayoutHeight(getHeight()-tEditChannelBtn.getHeight());
			}
		}
	}
	
	private void dismissPopupIfIsShowing(){
    	if(isShowing())
    		dismiss();
	}
	
	private View findViewById(int id){
		return getContentView().findViewById(id);
	}
	
}
