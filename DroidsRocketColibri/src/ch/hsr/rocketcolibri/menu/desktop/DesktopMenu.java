/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.menu.desktop;

import android.content.Context;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RocketColibriService;
import ch.hsr.rocketcolibri.manager.DesktopViewManager;
import ch.hsr.rocketcolibri.manager.IDesktopViewManager;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolFsm.s;
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
	private DesktopViewManager tDesktopViewManager;
	private RocketColibriService tService;
	private int[] tServiceDependentItemIds = {R.id.menu_action_main_settings,R.id.menu_action_main_wifi, R.id.menu_action_observe_mode, R.id.menu_action_operate_mode};
	private View[] tServiceDependentItems;
	private ControlModusContent tControlModusContent;
	private CustomizeModusContent tCustomizeModusContent;
	private boolean initOnce = true;
	public DesktopMenu(Context context, DesktopViewManager desktopViewManager) {
		tContext = context;
		tSwipeInMenu = (SwipeInMenu) ((Activity)tContext).findViewById(R.id.swipeInMenu);
		tDesktopViewManager = desktopViewManager;
		tSwipeInMenu.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			@Override
			public void onDrawerOpened() {
				tDesktopViewManager.closeSpecialThings();
			}
		});
		onCreate();
	}
	
	private void initContents(){
		tControlModusContent = (ControlModusContent)tSwipeInMenu.findViewById(R.id.controlModusContent);
		tCustomizeModusContent = (CustomizeModusContent)tSwipeInMenu.findViewById(R.id.customizeModusContent);
		tControlModusContent.create(null, this);
		tCustomizeModusContent.create(tService.getWidgetEntries(), this);
		switchModusContent();
	}
	
	public void toggle(){
		tSwipeInMenu.animateToggle();
	}
	
	public void animateToggle(){
		tSwipeInMenu.animateToggle();
	}
	
	public void animateClose(){
		tSwipeInMenu.animateClose();
	}
	
	public RocketColibriService getService(){
		return tService;
	}
	
	public DesktopViewManager getDesktopViewManager(){
		return tDesktopViewManager;
	}
	
	public void setService(RocketColibriService rcService) {
		tService = rcService;
		if(initOnce){
			initOnce = false;
			initContents();
		}
		setServiceDependentButtonsEnabled(true);
	}
	
	
	private void onCreate(){
		initServiceDependentItems();
		ToggleButton b = (ToggleButton)findViewById(R.id.menu_action_main_settings);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tDesktopViewManager.switchCustomieModus();
				switchModusContent();
			}

		});

		b = (ToggleButton)findViewById(R.id.menu_action_main_wifi);
		b.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
	    		if (isChecked) { 
	    			tService.tWifi.connectRocketColibriSSID(tService);

	    			// If WiFi is enabled, we are in observer mode by default
	    			// setting observer button active and disabling click because
	    			// in this case only operate button can be clicked
		        	((ToggleButton)findViewById(R.id.menu_action_observe_mode)).setChecked(true);
		        	((ToggleButton)findViewById(R.id.menu_action_observe_mode)).setClickable(false);

		        	((ToggleButton)findViewById(R.id.menu_action_operate_mode)).setClickable(true);
	    		} else {
		        	tService.tWifi.disconnectRocketColibriSSID(tService);

		        	// If WiFi is disabled, none of the modes
		        	// can be active, disabling both buttons
		        	((ToggleButton)findViewById(R.id.menu_action_observe_mode)).setChecked(false);
		        	((ToggleButton)findViewById(R.id.menu_action_operate_mode)).setChecked(false);
		        	((ToggleButton)findViewById(R.id.menu_action_observe_mode)).setClickable(false);
		        	((ToggleButton)findViewById(R.id.menu_action_operate_mode)).setClickable(false);
		        }
		    }
		});
		
		b = (ToggleButton)findViewById(R.id.menu_action_observe_mode);
		b.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if (isChecked){
                    tService.tProtocol.eventUserStopControl();

                    // Observe mode is activated deactivate operate button.
                    // Click will be enabled for starting operate mode
                    ((ToggleButton)findViewById(R.id.menu_action_operate_mode)).setChecked(false);
		        	((ToggleButton)findViewById(R.id.menu_action_operate_mode)).setClickable(true);
		        	((ToggleButton)findViewById(R.id.menu_action_observe_mode)).setClickable(false);
		        }
		    }
		});
		b.setClickable(false);
		b.setChecked(false);

		b = (ToggleButton)findViewById(R.id.menu_action_operate_mode);
		b.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if (isChecked){
                    if (tService.tProtocol.tConnState.getState() == s.CONN_LCK_OUT) {
                    	Toast.makeText(tContext, "Operate mode locked by another user!", Toast.LENGTH_SHORT).show();

                    	// We have to stay in observer mode and click must be active for operate button only
                    	((ToggleButton)findViewById(R.id.menu_action_observe_mode)).setChecked(true);
			        	((ToggleButton)findViewById(R.id.menu_action_observe_mode)).setClickable(false);
                    	((ToggleButton)findViewById(R.id.menu_action_operate_mode)).setChecked(false);
			        	((ToggleButton)findViewById(R.id.menu_action_operate_mode)).setClickable(true);
                    } else {
	                	tService.tProtocol.eventUserStartControl();

	                	// Operate mode is activated deactivate observe button.
	                    // Click will be enabled for starting observe mode
			        	((ToggleButton)findViewById(R.id.menu_action_observe_mode)).setChecked(false);
			        	((ToggleButton)findViewById(R.id.menu_action_observe_mode)).setClickable(true);
			        	((ToggleButton)findViewById(R.id.menu_action_operate_mode)).setClickable(false);
                    }
		        }
		    }
		});
		b.setClickable(false);
		b.setChecked(false);

		setServiceDependentButtonsEnabled(false);
	}
	
	private void switchModusContent() {
		if(tDesktopViewManager.isInCustomizeModus()){
			tCustomizeModusContent.setVisibility(View.VISIBLE);
			tControlModusContent.setVisibility(View.GONE);
		}else{
			tControlModusContent.setVisibility(View.VISIBLE);
			tCustomizeModusContent.setVisibility(View.GONE);
		}
	}
	
	private void initServiceDependentItems(){
		tServiceDependentItems = new View[tServiceDependentItemIds.length];
		for(int i = 0; i < tServiceDependentItemIds.length;++i){
			tServiceDependentItems[i] = findViewById(tServiceDependentItemIds[i]);
		}
	}
	
	private void setServiceDependentButtonsEnabled(boolean enabled){
		for(int i = 0; i < tServiceDependentItems.length;++i){
			tServiceDependentItems[i].setEnabled(enabled);
		}
	}
	
	private View findViewById(int id){
		return tSwipeInMenu.findViewById(id);
	}
}
