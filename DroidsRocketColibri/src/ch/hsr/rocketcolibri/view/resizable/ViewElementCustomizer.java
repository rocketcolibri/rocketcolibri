package ch.hsr.rocketcolibri.view.resizable;

import android.view.View;
import ch.hsr.rocketcolibri.view.AbsoluteLayout;
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.custimizable.ICustomizableView;

public class ViewElementCustomizer {
	private View tTargetView;
	
	public void setTargetView(View targetView){
		tTargetView = targetView;
	}
	
	public void maximize(){
		LayoutParams resizeTargetLP = (LayoutParams) tTargetView.getLayoutParams();
		ResizeConfig config = ((ICustomizableView)tTargetView).getWidgetConfig().viewElementConfig.getResizeConfig();
		AbsoluteLayout parent = (AbsoluteLayout) tTargetView.getParent();
		
		if (config.maxHeight > parent.getHeight()) {
			resizeTargetLP.height = parent.getHeight();
		}else{
			resizeTargetLP.height = config.maxHeight;
		}
		if (config.maxWidth > parent.getWidth()) {
			resizeTargetLP.width = parent.getWidth();
		}else{
			resizeTargetLP.width = config.maxWidth;
		}
		
		if(resizeTargetLP.height+resizeTargetLP.y>parent.getHeight())
			resizeTargetLP.y=parent.getHeight()-resizeTargetLP.height;
		if(resizeTargetLP.width+resizeTargetLP.x>parent.getWidth())
			resizeTargetLP.x=parent.getWidth()-resizeTargetLP.width;
		if (resizeTargetLP.y < 0) {resizeTargetLP.y = 0;}
		if (resizeTargetLP.x < 0) {resizeTargetLP.x = 0;}

		parent.updateViewLayout(tTargetView, resizeTargetLP);
	}

	public void minimize() {
		LayoutParams resizeTargetLP = (LayoutParams) tTargetView.getLayoutParams();
		ResizeConfig config = ((ICustomizableView)tTargetView).getWidgetConfig().viewElementConfig.getResizeConfig();
		AbsoluteLayout parent = (AbsoluteLayout) tTargetView.getParent();
		resizeTargetLP.height = config.minHeight;
		resizeTargetLP.width = config.minWidth;
		parent.updateViewLayout(tTargetView, resizeTargetLP);
	}
	
	public boolean isMaximized(){
		LayoutParams resizeTargetLP = (LayoutParams) tTargetView.getLayoutParams();
		ResizeConfig config = ((ICustomizableView)tTargetView).getWidgetConfig().viewElementConfig.getResizeConfig();
		AbsoluteLayout parent = (AbsoluteLayout) tTargetView.getParent();
		if((resizeTargetLP.height==config.maxHeight && resizeTargetLP.width==config.maxWidth)
				|| (resizeTargetLP.height==parent.getHeight() && resizeTargetLP.width==parent.getWidth())){
			return true;
		}
		return false;
	}
	
	public boolean isMinimized(){
		LayoutParams resizeTargetLP = (LayoutParams) tTargetView.getLayoutParams();
		ResizeConfig config = ((ICustomizableView)tTargetView).getWidgetConfig().viewElementConfig.getResizeConfig();
		if(resizeTargetLP.height==config.minHeight && resizeTargetLP.width==config.minWidth){
			return true;
		}
		return false;
	}
	
	public void enableView(View view, boolean enable){
		view.setAlpha(enable?1f:0.3f);
	}
}
