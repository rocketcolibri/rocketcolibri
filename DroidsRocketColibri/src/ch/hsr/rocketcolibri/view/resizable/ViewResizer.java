package ch.hsr.rocketcolibri.view.resizable;

import ch.hsr.rocketcolibri.view.resizable.layer.ResizableMainLayer;
import android.content.Context;
import android.view.View;

public class ViewResizer extends ResizableMainLayer {
    
    
    public ViewResizer(Context context, View resizeTarget, IResizeListener listener) {
        super(context, resizeTarget, listener);
    }

    

}