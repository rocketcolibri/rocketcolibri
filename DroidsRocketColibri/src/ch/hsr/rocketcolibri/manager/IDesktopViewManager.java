/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.manager;

import ch.hsr.rocketcolibri.menu.CustomizeModusPopupMenu;
import ch.hsr.rocketcolibri.menu.desktop.DesktopMenu;
import ch.hsr.rocketcolibri.view.AbsoluteLayout;
import ch.hsr.rocketcolibri.view.custimizable.CustomizableView;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import android.app.Service;
import android.content.Intent;
import android.view.View;

/**
 * @author Artan Veliju
 */
public interface IDesktopViewManager {
	void resizeView(View resizeTarget);
	boolean dragView(View dagTarget);
	CustomizableView createAndAddView(ViewElementConfig vElementConfig) throws Exception;
	CustomizableView createView(ViewElementConfig vElementConfig) throws Exception;
	void deleteView(View view);
	boolean isInCustomizeModus();
	void switchCustomieModus();
	AbsoluteLayout getRootView();
	AbsoluteLayout getControlElementParentView();
	CustomizeModusPopupMenu getCustomizeModusPopupMenu();
	DesktopMenu getDesktopMenu();
	void startEditActivity(View targetView);
	void editActivityResult(int viewIndex, Intent editChannelIntent);
	void closeSpecialThings();
	void release();
	void serviceReady(Service service);
}
