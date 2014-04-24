package ch.hsr.rocketcolibri.menu;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RocketColibriDefaults;
import ch.hsr.rocketcolibri.manager.IDesktopViewManager;
import ch.hsr.rocketcolibri.view.AbsoluteLayout;
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.custimizable.CustomizableView;
import ch.hsr.rocketcolibri.view.popup.PopupWindow;

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
		RocketColibriDefaults.setDefaultViewSettings(getContentView());
		onCreate();
		
	}
	
	
	
	private void onCreate(){
		Button b = (Button) findViewById(R.id.editChannel);
		b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d("editChannel", "click");
			}
		});

		b = (Button) findViewById(R.id.resizeMode);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
				tDesktopViewManager.resizeView(tTargetView);
			}
		});
		
		alphaChangeSlider = (SeekBar)findViewById(R.id.seekBar1);
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
	
	public void setTouchedView(CustomizableView cView){
		tTargetView = cView;
		alphaChangeSlider.setProgress((int)(tTargetView.getAlpha()*100f));
	}
	
	private View findViewById(int id){
		return getContentView().findViewById(id);
	}
	
}
