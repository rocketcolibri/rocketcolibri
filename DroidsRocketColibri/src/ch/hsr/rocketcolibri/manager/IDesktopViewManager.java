/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.manager;

import ch.hsr.rocketcolibri.menu.CustomizeModusPopupMenu;
import ch.hsr.rocketcolibri.menu.desktop.DesktopMenu;
import ch.hsr.rocketcolibri.view.AbsoluteLayout;
import ch.hsr.rocketcolibri.view.custimizable.ICustomizableView;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import ch.hsr.rocketcolibri.view.widget.RCWidgetConfig;
import android.content.Intent;
import android.view.View;

/**
 * @author Artan Veliju
 */
public interface IDesktopViewManager {
	void setDVMViewListener(DVMViewListener dvmVl);
	void resizeView(View resizeTarget);
	boolean dragView(View dagTarget);
	ICustomizableView createAndAddView(RCWidgetConfig vElementConfig) throws Exception;
	ICustomizableView createAndAddView(ViewElementConfig vElementConfig) throws Exception;
	ICustomizableView initCreateAndAddView(RCWidgetConfig widgetConfig) throws Exception;
	ICustomizableView createView(ViewElementConfig vElementConfig) throws Exception;
	ICustomizableView createView(RCWidgetConfig rcWidgetConfig) throws Exception;
	void viewChanged(View view);
	void deleteView(View view);
	boolean isInCustomizeModus();
	void enableCustomizeModus(boolean enabled);
	AbsoluteLayout getRootView();
	AbsoluteLayout getControlElementParentView();
	CustomizeModusPopupMenu getCustomizeModusPopupMenu();
	DesktopMenu getDesktopMenu();
	void startEditActivity(View targetView);
	void editActivityResult(int viewIndex, Intent editChannelIntent);
	void closeSpecialThings();
	void release();
}
