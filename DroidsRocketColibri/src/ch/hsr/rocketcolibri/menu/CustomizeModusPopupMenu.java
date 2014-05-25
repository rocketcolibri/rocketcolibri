/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.menu;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RocketColibriDefaults;
import ch.hsr.rocketcolibri.activity.EditChannelActivity;
import ch.hsr.rocketcolibri.manager.IDesktopViewManager;
import ch.hsr.rocketcolibri.view.AbsoluteLayout;
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.custimizable.CustomizableView;
import ch.hsr.rocketcolibri.view.popup.PopupWindow;

/**
 * @author Artan Veliju
 */
public class CustomizeModusPopupMenu extends PopupWindow{

	private CustomizableView tTargetView;
	private SeekBar alphaChangeSlider;
	private IDesktopViewManager tDesktopViewManager;
	
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
		Button b = (Button) findViewById(R.id.editChannel);
		b.setOnClickListener(new OnClickListener() {
			public void onClick(View targetView) {
				tDesktopViewManager.startEditActivity(targetView);
			}
		});

		b = (Button) findViewById(R.id.resizeElementBtn);
		b.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dismiss();
				tDesktopViewManager.resizeView(tTargetView);
			}
		});

		setUpDeleteButton(R.id.deleteElementBtn);
		
		alphaChangeSlider = (SeekBar)findViewById(R.id.alphaSlider);
		alphaChangeSlider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				tTargetView.setAlpha(progress/100f);
			}
		});
	}
	
	private void setUpDeleteButton(int resourceId){
		Button b = (Button) findViewById(resourceId);
		final Drawable startColor = b.getBackground();
		final Drawable endColor = mContext.getResources().getDrawable(R.drawable.delete_foreground);
		b.setOnTouchListener(new OnTouchListener() {
		    Drawable[] color = {startColor, endColor};
		    TransitionDrawable trans = new TransitionDrawable(color);
		    final int MAX_DURATION = 4000;
		    long duration = 0;
		    long startTime = 0;
		    
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN: 
	        		startTime = System.currentTimeMillis();
	        		v.setBackground(trans);
	        		trans.startTransition(MAX_DURATION);
	        		break;
				case MotionEvent.ACTION_MOVE: 
					duration = System.currentTimeMillis() - startTime;
					if(duration >= MAX_DURATION){
						tDesktopViewManager.deleteView(tTargetView);
						dismiss();
						trans.resetTransition();
					}
					break;
				case MotionEvent.ACTION_UP: 
					if(duration < MAX_DURATION){
						trans.reverseTransition(500);
					}
					break;
		        case MotionEvent.ACTION_CANCEL:
		        	return false;
		        }
				return true;
			}
		});
	}
	
	public void show(CustomizableView cView){
		dismissPopupIfIsShowing();
		tTargetView = cView;
		alphaChangeSlider.setProgress((int)(tTargetView.getAlpha()*100f));
		showAtBestPosition(cView);
	}
	
	private void dismissPopupIfIsShowing(){
    	if(isShowing())
    		dismiss();
	}
	
	private View findViewById(int id){
		return getContentView().findViewById(id);
	}
	
}
