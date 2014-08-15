/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.menu.desktop;

import android.content.Context;
import android.app.Activity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RocketColibriService;
import ch.hsr.rocketcolibri.manager.DesktopViewManager;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolFsm.s;
import ch.hsr.rocketcolibri.view.widget.SwipeInMenu;
import ch.hsr.rocketcolibri.view.widget.SwipeInMenu.OnDrawerOpenListener;

/**
 * This Class represents the DesktopMenu
 * @author Artan Veliju
 */
public class DesktopMenu {
	private SwipeInMenu tSwipeInMenu;
	private Context tContext;
	private DesktopViewManager tDesktopViewManager;
	private RocketColibriService tService;
	private int[] tServiceDependentItemIds = {R.id.menu_action_main_settings,R.id.menu_action_main_wifi, R.id.menu_action_observe_mode, R.id.menu_action_operate_mode, R.id.shareModelsBtn};
	private View[] tServiceDependentItems;
	private ControlModusContent tControlModusContent;
	private CustomizeModusContent tCustomizeModusContent;
	private Switch tModeSwitcher;
	private ToggleButton tWifi;
	private ToggleButton tObserveMode;
	private ToggleButton tOperateMode;
	private TextView tDesktopMenuBottomTv;
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
			
			//There is no way to set something like match_parent on the default Android Switch
			//and no way to get the size of the screen in xml for setting switchMinWidth
			//Therefore we set the switchMinWidth programmatically to stretch the Switch on a
			// time after layouting. This is a good place to do it!
			tModeSwitcher.setSwitchMinWidth(((View)tModeSwitcher.getParent()).getWidth());
		}
		setServiceDependentButtonsEnabled(true);
	}
	
	public void setTextOnBottom(String text){
		tDesktopMenuBottomTv.setText(text);
	}
	
	private void onCreate(){
		initServiceDependentItems();
		tModeSwitcher = (Switch)findViewById(R.id.menu_action_main_settings);
		tModeSwitcher.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				tDesktopViewManager.enableCustomizeModus(isChecked);
				switchModusContent();
			}
		});
		tWifi = (ToggleButton)findViewById(R.id.menu_action_main_wifi);
		tWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
	    		if (isChecked) { 
	    			tService.tWifi.connectRocketColibriSSID(tService);

	    			// If WiFi is enabled, we are in observer mode by default
	    			// setting observer button active and disabling click because
	    			// in this case only operate button can be clicked
		        	tObserveMode.setChecked(true);
		        	tObserveMode.setClickable(false);

		        	tOperateMode.setClickable(true);
	    		} else {
		        	tService.tWifi.disconnectRocketColibriSSID(tService);

		        	// If WiFi is disabled, none of the modes
		        	// can be active, disabling both buttons
		        	tObserveMode.setChecked(false);
		        	tOperateMode.setChecked(false);
		        	tObserveMode.setClickable(false);
		        	tOperateMode.setClickable(false);
		        }
		    }
		});
		
		tObserveMode = (ToggleButton)findViewById(R.id.menu_action_observe_mode);
		tObserveMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if (isChecked){
                    tService.tProtocol.eventUserStopControl();

                    // Observe mode is activated deactivate operate button.
                    // Click will be enabled for starting operate mode
                    tOperateMode.setChecked(false);
		        	tOperateMode.setClickable(true);
		        	tObserveMode.setClickable(false);
		        }
		    }
		});
		tObserveMode.setClickable(false);
		tObserveMode.setChecked(false);

		tOperateMode = (ToggleButton)findViewById(R.id.menu_action_operate_mode);
		tOperateMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if (isChecked){
                    if (tService.tProtocol.tConnState.getState() == s.CONN_LCK_OUT) {
                    	Toast.makeText(tContext, "Operate mode locked by another user!", Toast.LENGTH_SHORT).show();

                    	// We have to stay in observer mode and click must be active for operate button only
                    	tObserveMode.setChecked(true);
			        	tObserveMode.setClickable(false);
                    	tOperateMode.setChecked(false);
			        	tOperateMode.setClickable(true);
                    } else {
	                	tService.tProtocol.eventUserStartControl();

	                	// Operate mode is activated deactivate observe button.
	                    // Click will be enabled for starting observe mode
			        	tObserveMode.setChecked(false);
			        	tObserveMode.setClickable(true);
			        	tOperateMode.setClickable(false);
                    }
		        }
		    }
		});
		tOperateMode.setClickable(false);
		tOperateMode.setChecked(false);
		
		tDesktopMenuBottomTv = (TextView)findViewById(R.id.desktopMenuBottomTv);
		setServiceDependentButtonsEnabled(false);
	}
	
	private void switchModusContent() {
		if(tDesktopViewManager.isInCustomizeModus()){
			tControlModusContent.setVisibility(View.GONE);
			tCustomizeModusContent.setVisibility(View.VISIBLE);
		}else{
			tCustomizeModusContent.setVisibility(View.GONE);
			tControlModusContent.setVisibility(View.VISIBLE);
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
