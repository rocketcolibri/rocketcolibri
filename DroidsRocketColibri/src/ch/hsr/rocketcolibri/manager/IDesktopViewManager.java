package ch.hsr.rocketcolibri.manager;

import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import android.view.View;

public interface IDesktopViewManager {
	void resizeView(View resizeTarget);
	boolean dragView(View dagTarget);
	View createView(ViewElementConfig cElementConfig) throws Exception;
	boolean isInCustomizeModus();
	void switchCustomieModus();
	void release();
}
