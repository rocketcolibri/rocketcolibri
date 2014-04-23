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
import ch.hsr.rocketcolibri.RocketColibriService;
import ch.hsr.rocketcolibri.manager.IDesktopViewManager;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolFsm.e;

/**
 * This Class is for the Instantiations of the Children's of the Menu
 * and the Listener of them
 * @author artvel
 */
public class DesktopMenu {
	private View tContentView;
	private Context tContext;
	private IDesktopViewManager tDesktopViewManager;
	private RocketColibriService tRcService;
	private int[] serviceDependentButtons = {R.id.connectWifi,R.id.disconnectWifi, R.id.observe, R.id.operate};
	
	public DesktopMenu(Context context, View contentView, IDesktopViewManager desktopViewManager) {
		tRcService = null;
		tContext = context;
		tContentView = contentView;
		tDesktopViewManager = desktopViewManager;
		onCreate();	
	}
	
	private void setServiceDependentButtonsEnabled(boolean enabled){
		Button b = null;
		for(int i = 0; i < serviceDependentButtons.length;++i){
			b = (Button)findViewById(serviceDependentButtons[i]);
			b.setEnabled(enabled);
		}
	}
	
	public void setService(RocketColibriService rcService) {
		tRcService = rcService;
		setServiceDependentButtonsEnabled(true);
	}
	
	
	private void onCreate(){
		Button b = (Button)findViewById(R.id.switchModus);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tDesktopViewManager.switchCustomieModus();
			}
		});
		
		b = (Button)findViewById(R.id.connectWifi);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(null != tRcService) tRcService.wifi.Connect();
			}
		});

		b = (Button)findViewById(R.id.disconnectWifi);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(null != tRcService) tRcService.wifi.Disconnect();
			}
		});
		
		b = (Button)findViewById(R.id.observe);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(null != tRcService) {
					tRcService.protocolFsm.queue(e.E6_USR_CONNECT);
					tRcService.protocolFsm.processOutstandingEvents();
				}
			}
		});
		
		b = (Button)findViewById(R.id.operate);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(null != tRcService) {
					tRcService.protocolFsm.queue(e.E7_USR_OBSERVE);
		    		tRcService.protocolFsm.processOutstandingEvents();
				}
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
		
		setServiceDependentButtonsEnabled(false);
	}
	
	private View findViewById(int id){
		return tContentView.findViewById(id);
	}
	
	

}
