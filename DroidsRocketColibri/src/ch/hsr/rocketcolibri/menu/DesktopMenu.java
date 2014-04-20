package ch.hsr.rocketcolibri.menu;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.manager.IDesktopViewManager;

/**
 * This Class is for the Instantiations of the Children's of the Menu
 * and the Listener of them
 * @author artvel
 */
public class DesktopMenu {
	private View tContentView;
	private Context tContext;
	private IDesktopViewManager tDesktopViewManager;
	
	public DesktopMenu(Context context, View contentView, IDesktopViewManager desktopViewManager){
		tContext = context;
		tContentView = contentView;
		tDesktopViewManager = desktopViewManager;
		onCreate();
	}
	
	private void onCreate(){
		Button b = (Button)findViewById(R.id.switchModus);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tDesktopViewManager.switchCustomieModus();
			}
		});
		SeekBar sBar = (SeekBar)findViewById(R.id.seekBar1);
		sBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				Toast.makeText(tContext, ""+progress, Toast.LENGTH_SHORT).show();
			}
		});
		RatingBar rb = (RatingBar)findViewById(R.id.ratingBar1);
		rb.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				Toast.makeText(tContext, ""+rating, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	private View findViewById(int id){
		return tContentView.findViewById(id);
	}
	
	

}
