package ch.hsr.rocketcolibri.menu;

import ch.hsr.rocketcolibri.R;
import android.content.Context;
import android.view.View;
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
	private View tContentView;
	private Context tContext;
	public DesktopMenu(Context context, View contentView){
		tContext = context;
		tContentView = contentView;
		onCreate();
	}
	
	private void onCreate(){
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
