/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.menu.desktop;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RocketColibriService;
import ch.hsr.rocketcolibri.manager.IDesktopViewManager;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolFsm.e;
import ch.hsr.rocketcolibri.view.widget.SwipeInMenu;
import ch.hsr.rocketcolibri.view.widget.SwipeInMenu.OnDrawerOpenListener;

/**
 * This Class is for the Instantiations of the Children's of the Menu
 * and the Listener of them
 * @author Artan Veliju
 */
public class DesktopMenu {
	private SwipeInMenu tSwipeInMenu;
	private Context tContext;
	private IDesktopViewManager tDesktopViewManager;
	private RocketColibriService tRcService;
	private int[] serviceDependentItemIds = {R.id.menu_action_main_settings,R.id.menu_action_main_wifi, R.id.menu_action_main_mode};
	private View[] serviceDependentItems;
	
	public DesktopMenu(Context context, View contentView, IDesktopViewManager desktopViewManager) {
		tContext = context;
		tSwipeInMenu = (SwipeInMenu) contentView;
		tDesktopViewManager = desktopViewManager;
		tSwipeInMenu.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			@Override
			public void onDrawerOpened() {
				tDesktopViewManager.closeSpecialThings();
			}
		});
		onCreate();	
	}
	
	public void toggle(){
		tSwipeInMenu.animateToggle();
	}
	
	public void animateToggle(){
		tSwipeInMenu.animateToggle();
	}
	
	public void setService(RocketColibriService rcService) {
		tRcService = rcService;
		setServiceDependentButtonsEnabled(true);
	}
	
	
	private void onCreate(){
		initServiceDependentItems();
		ToggleButton b = (ToggleButton)findViewById(R.id.menu_action_main_settings);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tDesktopViewManager.switchCustomieModus();
			}
		});

		b = (ToggleButton)findViewById(R.id.menu_action_main_wifi);
		b.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
	    		// TODO not set to null from here!
	    		// tRcService.users.setActiveUser(null);
	    		if (isChecked) 
	    			tRcService.tWifi.connectRocketColibriSSID(tRcService);
		        else
		        	tRcService.tWifi.disconnectRocketColibriSSID(tRcService);
		    }
		});
		

		b = (ToggleButton)findViewById(R.id.menu_action_main_mode);
		b.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
	    		if (isChecked){
					tRcService.tProtocolFsm.queue(e.E6_USR_CONNECT);
					tRcService.tProtocolFsm.processOutstandingEvents();
	    		}else{
					tRcService.tProtocolFsm.queue(e.E7_USR_OBSERVE);
					tRcService.tProtocolFsm.processOutstandingEvents();
		        }
		    }
		});
		setServiceDependentButtonsEnabled(false);
	}
	
	private void initServiceDependentItems(){
		serviceDependentItems = new View[serviceDependentItemIds.length];
		for(int i = 0; i < serviceDependentItemIds.length;++i){
			serviceDependentItems[i] = findViewById(serviceDependentItemIds[i]);
		}
	}
	
	private void setServiceDependentButtonsEnabled(boolean enabled){
		for(int i = 0; i < serviceDependentItems.length;++i){
			serviceDependentItems[i].setEnabled(enabled);
		}
	}
	
	private View findViewById(int id){
		return tSwipeInMenu.findViewById(id);
	}

}
