/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.view.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RCConstants;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolFsm.s;
import ch.hsr.rocketcolibri.ui_data.input.IUiInputSource;
import ch.hsr.rocketcolibri.ui_data.input.UiInputData;
import ch.hsr.rocketcolibri.ui_data.input.UiInputProtocol;
import ch.hsr.rocketcolibri.ui_data.output.ConnectionState;
import ch.hsr.rocketcolibri.ui_data.output.IUiOutputSinkChangeObserver;
import ch.hsr.rocketcolibri.ui_data.output.UiOutputDataType;
import ch.hsr.rocketcolibri.util.DrawingTools;
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.custimizable.ICustomizableView;
import ch.hsr.rocketcolibri.view.custimizable.ModusChangeListener;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;

/**
 * @short widget to display the connection status received from the ServoController
 * 
 *  The status of the connection is indicated with a icon according to the Systemschnittstellen document 
 */
public class ConnectionStatusWidget extends View implements ICustomizableView, IUiOutputSinkChangeObserver, IUiInputSource{

	protected ViewElementConfig tViewElementConfig;
	protected RCWidgetConfig tWidgetConfig;
		private RectF connectionIconRect;
	private Paint connectionIconPaint;
	private Bitmap connectionIconBitmap;
	private boolean tCustomizeModusActive = false;
	private UiInputProtocol tProtocolSettings = new UiInputProtocol();
	// to avoid null check
	private ModusChangeListener tModusChangeListener = new ModusChangeListener()
	{public void customizeModeDeactivated(){}public void customizeModeActivated(){}};
	
	public ConnectionStatusWidget(Context context, ViewElementConfig elementConfig) {
		this(context, new RCWidgetConfig(elementConfig));
	}
	
	public ConnectionStatusWidget(Context context, RCWidgetConfig widgetConfig) {
		super(context);
		tViewElementConfig = widgetConfig.viewElementConfig;
		tWidgetConfig = widgetConfig;
		setLayoutParams(tViewElementConfig.getLayoutParams());
		setAlpha(tViewElementConfig.getAlpha());
		init(context, null);
	}
	
	private void init(Context context, AttributeSet attrs) {
		connectionIconRect = new RectF(0.0f, 0.0f, 1.0f, 1.0f);
		connectionIconBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.connection_status_disconneted);
		BitmapShader paperShader = new BitmapShader(connectionIconBitmap, Shader.TileMode.MIRROR, Shader.TileMode.MIRROR);
		Matrix paperMatrix = new Matrix();
		paperMatrix.setScale(1.0f / connectionIconBitmap.getWidth(), 1.0f / connectionIconBitmap.getHeight());
		paperShader.setLocalMatrix(paperMatrix);
		connectionIconPaint = new Paint();
		connectionIconPaint.setFilterBitmap(false);
		connectionIconPaint.setStyle(Paint.Style.FILL);
		connectionIconPaint.setShader(paperShader);
		initDefaultProtocolConfig();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		float scale = (float) getWidth();
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.scale(scale, scale);
		canvas.drawRect(connectionIconRect, connectionIconPaint);
		canvas.restore();
		if (tCustomizeModusActive) 
			DrawingTools.drawCustomizableForground(this, canvas);
	}
	
	private void setConnectionState(s state)
	{
		if (connectionIconBitmap != null) {
		    connectionIconBitmap.recycle();
		}
		 
		switch(state)
		{
		case DISC:
			connectionIconBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.connection_status_disconneted);	
			break;
		case CONN_CONTROL:
			connectionIconBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.connection_status_control);
			break;
		case CONN_LCK_OUT:
			connectionIconBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.connection_status_connected_locked);
			break;
		case CONN_OBSERVE:
			connectionIconBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.connection_status_connected_observe);
			break;
		default:
			connectionIconBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.connection_status_connected);
			break;
		}
		BitmapShader paperShader = new BitmapShader(connectionIconBitmap, Shader.TileMode.MIRROR, Shader.TileMode.MIRROR);
		Matrix paperMatrix = new Matrix();
		paperMatrix.setScale(1.0f / connectionIconBitmap.getWidth(), 1.0f / connectionIconBitmap.getHeight());
		paperShader.setLocalMatrix(paperMatrix);
		connectionIconPaint = new Paint();
		connectionIconPaint.setFilterBitmap(false);
		connectionIconPaint.setStyle(Paint.Style.FILL);
		connectionIconPaint.setShader(paperShader);
		postInvalidate();
	}

	public static ViewElementConfig getDefaultViewElementConfig() {
		return DefaultViewElementConfigRepo.getDefaultConfig(ConnectionStatusWidget.class);
	}

	/**
	 * Implementation of the interfaces
	 */
	@Override
	public void create(RCWidgetConfig rcWidgetConfig) {
		tWidgetConfig = rcWidgetConfig;
		init(getContext(), null);
	}

	@Override
	public void create(ViewElementConfig vElementConfig) {
		tWidgetConfig = new RCWidgetConfig(vElementConfig);
		init(getContext(), null);
	}

	@Override
	public RCWidgetConfig getWidgetConfig() {
		tWidgetConfig.viewElementConfig = this.getViewElementConfig();
		return tWidgetConfig;
	}

	@Override
	public Map<String, String> getProtocolMap() {
		return tWidgetConfig.protocolMap;
	}

	@Override
	public void setProtocolMap(Map<String, String> protocolMap) {
		tWidgetConfig.protocolMap = protocolMap;
		updateProtocolMap();
	}

	@Override
	public void updateProtocolMap() {
		try{
			tProtocolSettings.setAuto(getProtocolMapBoolean(RCConstants.AUTOCONNECT));
			tProtocolSettings.setIpAddress(getProtocolMapString(RCConstants.IP_SERVOCONTROLLER));
			tProtocolSettings.setPort(getProtocolMapInt(RCConstants.PORT_SERVOCONTROLLER));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	protected int getProtocolMapInt(String key){
		try{
			return Integer.parseInt(tWidgetConfig.protocolMap.get(key));
		}catch(NumberFormatException e){
			return -1;
		}
	}
	protected String getProtocolMapString(String key){
		try{
			return tWidgetConfig.protocolMap.get(key);
		}catch(NumberFormatException e){
			return "";
		}
	}
	protected boolean getProtocolMapBoolean(String key){
		try{
			return Boolean.parseBoolean(tWidgetConfig.protocolMap.get(key));
		}catch(Exception e){
			return false;
		}
	}
	@Override
	public void onNotifyUiOutputSink(Object p) {
		ConnectionState data = (ConnectionState)p;
		setConnectionState(data.getState());
	}

	@Override
	public UiOutputDataType getType(){
		return UiOutputDataType.ConnectionState;
	}

	@Override
	public void setCustomizeModusListener(OnTouchListener customizeModusListener) {
		setOnTouchListener(customizeModusListener);
	}

	@Override
	public void setCustomizeModus(boolean enabled) {
		if (tCustomizeModusActive != enabled) {
			if (enabled) {
				tModusChangeListener.customizeModeActivated();
			} else {
				tModusChangeListener.customizeModeDeactivated();
			}
			invalidate();
			tCustomizeModusActive = enabled;
		}
	}
	private void initDefaultProtocolConfig(){
		if(tWidgetConfig.protocolMap==null){
			tWidgetConfig.protocolMap = new HashMap<String, String>();
			tWidgetConfig.protocolMap.put(RCConstants.AUTOCONNECT, Boolean.valueOf(tProtocolSettings.getAutoMode()).toString());
			tWidgetConfig.protocolMap.put(RCConstants.IP_SERVOCONTROLLER, String.valueOf(tProtocolSettings.getIpAddress()).toString());
			tWidgetConfig.protocolMap.put(RCConstants.PORT_SERVOCONTROLLER, Integer.valueOf(tProtocolSettings.getPort()).toString());
		}else{
			updateProtocolMap();
		}
	}

	@Override
	public ViewElementConfig getViewElementConfig() {
		tWidgetConfig.viewElementConfig.setLayoutParams((LayoutParams) getLayoutParams());
		tWidgetConfig.viewElementConfig.setAlpha(getAlpha());
		return tWidgetConfig.viewElementConfig;
	}

	@Override
	public List<UiInputData> getUiInputSourceList() {
		List<UiInputData> list = new ArrayList<UiInputData>();
		list.add(tProtocolSettings);
	    return list;
	}

}