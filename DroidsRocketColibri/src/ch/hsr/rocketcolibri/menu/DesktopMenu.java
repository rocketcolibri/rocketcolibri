package ch.hsr.rocketcolibri.menu;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
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
	private int[] serviceDependentButtons = {R.id.menu_action_main_settings,R.id.menu_action_main_wifi, R.id.menu_action_main_mode};
	
	public DesktopMenu(Context context, View contentView, IDesktopViewManager desktopViewManager) {
		tRcService = null;
		tContext = context;
		tContentView = contentView;
		tDesktopViewManager = desktopViewManager;
		onCreate();	
	}
	
	private void setServiceDependentButtonsEnabled(boolean enabled){
		ToggleButton b = null;
		for(int i = 0; i < serviceDependentButtons.length;++i){
			b = (ToggleButton)findViewById(serviceDependentButtons[i]);
			b.setEnabled(enabled);
		}
	}
	
	public void setService(RocketColibriService rcService) {
		tRcService = rcService;
		setServiceDependentButtonsEnabled(true);
	}
	
	
	private void onCreate(){
		ToggleButton b = (ToggleButton)findViewById(R.id.menu_action_main_settings);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tDesktopViewManager.switchCustomieModus();
			}
		});
		

		b = (ToggleButton)findViewById(R.id.menu_action_main_wifi);
		b.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
		    {
		    	if(null != tRcService)
		    	{
		    		if (isChecked) 
		    			tRcService.wifi.Connect();
			        else
			        	tRcService.wifi.Disconnect();	    		
		    	}
		        
		    }
		});
		

		b = (ToggleButton)findViewById(R.id.menu_action_main_mode);
		b.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
		    {
		    	if(null != tRcService)
		    	{
		    		if (isChecked) 
		    		{
						tRcService.protocolFsm.queue(e.E6_USR_CONNECT);
						tRcService.protocolFsm.processOutstandingEvents();
		    		}
			        else
			        {
						tRcService.protocolFsm.queue(e.E7_USR_OBSERVE);
						tRcService.protocolFsm.processOutstandingEvents();
			        }
		    	}
		        
		    }
		});

		
		
		
		setServiceDependentButtonsEnabled(false);
	}
	
	private View findViewById(int id){
		return tContentView.findViewById(id);
	}
	
	

}
