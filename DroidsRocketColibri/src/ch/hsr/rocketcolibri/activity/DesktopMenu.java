package ch.hsr.rocketcolibri.activity;

import ch.hsr.rocketcolibri.R;
import android.app.Activity;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

/**
 * This Class is for the Instantiations of the Children's of the Menu
 * and the Listener of them
 * @author artvel
 */
public class DesktopMenu {
	private Activity tActivity;
	
	public DesktopMenu(Activity activity, int resourceId){
		tActivity = activity;
		SeekBar sBar = (SeekBar)activity.findViewById(R.id.seekBar1);
		sBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				Toast.makeText(tActivity, ""+progress, Toast.LENGTH_SHORT).show();
			}
		});
		RatingBar rb = (RatingBar)activity.findViewById(R.id.ratingBar1);
		rb.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				Toast.makeText(tActivity, ""+rating, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	

}
