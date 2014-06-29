package ch.hsr.rocketcolibri.view.widget;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import ch.hsr.rocketcolibri.RocketColibriDefaults;
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import ch.hsr.rocketcolibri.view.resizable.ResizeConfig;

public class DefaultViewElementConfigRepo {
	private static DefaultViewElementConfigRepo instance = null;
	private Map<Class<?>, ViewElementConfig> repo = new HashMap<Class<?>, ViewElementConfig>();
	
	public static DefaultViewElementConfigRepo getInstance(Context context){
		if(instance == null){
			instance = new DefaultViewElementConfigRepo(context);
		}
		return instance;
	}
		
	private DefaultViewElementConfigRepo(Context context){
		float density = context.getResources().getDisplayMetrics().density;
		//>>>>>>>>>>>>>>>>>>>>>>>>>>> Circle >>>>>>>>>>>>>>>>>>>>>>>>>>
		ResizeConfig rc = new ResizeConfig();
	    rc.keepRatio=true;
	    rc.maxHeight=200;
	    rc.minHeight=40;
	    rc.maxWidth=200;
	    rc.minWidth=40;
	    LayoutParams lp = new LayoutParams(180, 180 , 100, 300);
	    ViewElementConfig elementConfig = new ViewElementConfig(Circle.class.getName(), lp, rc);
	    dpToPixel(density, elementConfig);
	    repo.put(Circle.class, elementConfig);
		//<<<<<<<<<<<<<<<<<<<<<<<<<<< Circle <<<<<<<<<<<<<<<<<<<<<<<<<<

		//>>>>>>>>>>>>>>>>>>>>>>>>>>> ConnectedUserInfoWidget >>>>>>>>>
		rc = new ResizeConfig();
	    rc.maxHeight=300;
	    rc.minHeight=50;
	    rc.maxWidth=800;
	    rc.minWidth=100;
	    lp = new LayoutParams(600, 100 , 100, 0);
	    elementConfig = new ViewElementConfig(ConnectedUserInfoWidget.class.getName(), lp, rc);
	    elementConfig.setAlpha(0.5f);
	    dpToPixel(density, elementConfig);
	    repo.put(ConnectedUserInfoWidget.class, elementConfig);
		//<<<<<<<<<<<<<<<<<<<<<<<<<<< ConnectedUserInfoWidget <<<<<<<<<
	    
		//>>>>>>>>>>>>>>>>>>>>>>>>>>> ConnectionStatusWidget >>>>>>>>>>
		rc = new ResizeConfig();
	    rc.maxHeight=150;
	    rc.minHeight=50;
	    rc.maxWidth=150;
	    rc.minWidth=50;
	    lp = new LayoutParams(100, 100 , 0, 0);
	    elementConfig = new ViewElementConfig(ConnectionStatusWidget.class.getName(), lp, rc);
	    elementConfig.setAlpha(1);
	    dpToPixel(density, elementConfig);
	    repo.put(ConnectionStatusWidget.class, elementConfig);
		//<<<<<<<<<<<<<<<<<<<<<<<<<<< ConnectionStatusWidget <<<<<<<<<<
	    
		//>>>>>>>>>>>>>>>>>>>>>>>>>>> RotaryKnobWidget >>>>>>>>>>>>>>>>
		rc = new ResizeConfig();
		rc.keepRatio = true;
		rc.maxHeight = 300;
		rc.minHeight = 34;
		rc.maxWidth = 800;
		rc.minWidth = 137;
		lp = new LayoutParams(250, 250, 0, 0);
		elementConfig = new ViewElementConfig(RotaryKnobWidget.class.getName(), lp, rc);
		elementConfig.setAlpha(1);
		dpToPixel(density, elementConfig);
	    repo.put(RotaryKnobWidget.class, elementConfig);
		//<<<<<<<<<<<<<<<<<<<<<<<<<<< RotaryKnobWidget <<<<<<<<<<<<<<<<
	    
		//>>>>>>>>>>>>>>>>>>>>>>>>>>> SwitchWidget >>>>>>>>>>>>>>>>>>>>
		rc = new ResizeConfig();
		rc.keepRatio = true;
		rc.maxHeight = 300;
		rc.minHeight = 34;
		rc.maxWidth = 800;
		rc.minWidth = 137;
		lp = new LayoutParams(137, 34, 0, 0);
		elementConfig = new ViewElementConfig(SwitchWidget.class.getName(), lp, rc);
		elementConfig.setAlpha(1);
		dpToPixel(density, elementConfig);
	    repo.put(SwitchWidget.class, elementConfig);
		//<<<<<<<<<<<<<<<<<<<<<<<<<<< SwitchWidget <<<<<<<<<<<<<<<<<<<<
	    
		//>>>>>>>>>>>>>>>>>>>>>>>>>>> VideoStreamWidget >>>>>>>>>>>>>>>
		rc = new ResizeConfig();
		rc.maxHeight = 1080;
		rc.minHeight = 100;
		rc.maxWidth = 1920;
		rc.minWidth = 180;
		lp = new LayoutParams(400, 300, 100, 100);
		elementConfig = new ViewElementConfig(VideoStreamWidget.class.getName(), lp, rc);
		dpToPixel(density, elementConfig);
	    repo.put(VideoStreamWidget.class, elementConfig);
		//<<<<<<<<<<<<<<<<<<<<<<<<<<< VideoStreamWidget <<<<<<<<<<<<<<<
	}
	
	public ViewElementConfig get(Class<?> clazz){
		return repo.get(clazz);
	}
	
	public static ViewElementConfig getDefaultConfig(Class<?> clazz){
		try{
			return instance.get(clazz);
		}catch(NullPointerException e){
			throw new NullPointerException("call getInstance(Context) first!");
		}
	}
	
	private void dpToPixel(float density, ViewElementConfig vec){
		RocketColibriDefaults.dpToPixel(density, vec);
	}

}
