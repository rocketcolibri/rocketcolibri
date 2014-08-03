/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.view.widget;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.protocol.RocketColibriProtocolFsm.s;
import ch.hsr.rocketcolibri.ui_data.input.UiInputSourceChannel;
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
public class ConnectionStatusWidget extends View implements ICustomizableView, IRCWidget, IUiOutputSinkChangeObserver {

	protected ViewElementConfig tViewElementConfig;
	protected RCWidgetConfig tWidgetConfig;
	private RectF connectionIconRect;
	private Paint connectionIconPaint;
	private Bitmap connectionIconBitmap;
	private boolean tCustomizeModusActive = false;
	private Context tContext;
	// to avoid null check
	private ModusChangeListener tModusChangeListener = new ModusChangeListener()
	{public void customizeModeDeactivated(){}public void customizeModeActivated(){}};
	
	public ConnectionStatusWidget(Context context, ViewElementConfig elementConfig) {
		this(context, new RCWidgetConfig(elementConfig));
	}
	
	public ConnectionStatusWidget(Context context, RCWidgetConfig widgetConfig) {
		super(context);
		tContext = context;
		tViewElementConfig = widgetConfig.viewElementConfig;
		tWidgetConfig = widgetConfig;
		setLayoutParams(tViewElementConfig.getLayoutParams());
		setAlpha(tViewElementConfig.getAlpha());
		init(context, null);
	}
	
	@Override protected void finalize() throws Throwable
	{
	  try {
		  connectionIconBitmap.recycle();
		  connectionIconBitmap = null;
	  } catch (Exception e) {}
	  finally {
	    super.finalize();
	  }
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
	public void updateProtocolMap() {}

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

	@Override
	public ViewElementConfig getViewElementConfig() {
		tWidgetConfig.viewElementConfig.setLayoutParams((LayoutParams) getLayoutParams());
		tWidgetConfig.viewElementConfig.setAlpha(getAlpha());
		return tWidgetConfig.viewElementConfig;
	}
	
	private void setupConnectionStateReceiver(){
		AsyncTask<Void, Void, Void> connectionStatusUpdater = new AsyncTask<Void, Void, Void>(){
			protected Void doInBackground(Void... params) {
				ConnectivityManager myConnManager = (ConnectivityManager) tContext.getSystemService(Activity.CONNECTIVITY_SERVICE);
				NetworkInfo myNetworkInfo = myConnManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				WifiManager myWifiManager = (WifiManager)tContext.getSystemService(Context.WIFI_SERVICE);
				WifiInfo myWifiInfo = myWifiManager.getConnectionInfo();  
				Log.d("",String.valueOf(myWifiInfo.getLinkSpeed()) + " " + WifiInfo.LINK_SPEED_UNITS);
				return null;
			}
		};
	}

	@Override
	public List<UiInputSourceChannel> getUiInputSourceList() {
		return null;
	}	
}