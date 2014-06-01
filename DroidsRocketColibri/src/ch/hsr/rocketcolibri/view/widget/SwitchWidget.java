package ch.hsr.rocketcolibri.view.widget;

import java.util.HashMap;
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
import android.view.View;
import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.RCConstants;
import ch.hsr.rocketcolibri.channel.Channel;
import ch.hsr.rocketcolibri.view.AbsoluteLayout.LayoutParams;
import ch.hsr.rocketcolibri.view.custimizable.ViewElementConfig;
import ch.hsr.rocketcolibri.view.resizable.ResizeConfig;

public class SwitchWidget extends RCWidget {

	private RectF switchIconRect;
	private Paint switchIconPaint;
	private Bitmap switchIconBitmap;
	private Channel tChannelH = new Channel();
	private int bitmapResource;
	private int tPosition = 0;
	private OnChannelChangeListener tControlModusListener;
	private Map<String, String> protocolMap = new HashMap<String, String>();
	private boolean switchSetOn = false;

	public SwitchWidget(Context context, ViewElementConfig elementConfig) {
		super(context, elementConfig);
		bitmapResource = R.drawable.switch_off;
		init(context, null);
	}
	
	public SwitchWidget(Context context, RCWidgetConfig widgetConfig) {
		super(context, widgetConfig);
		bitmapResource = R.drawable.switch_off;
		init(context, null);
	}
	
	private boolean isChannelValid(){
		return tChannelH.getDefaultChannelValue() > -1;
	}

	OnClickListener onclicklistener = new OnClickListener() {

	    @Override
	    public void onClick(View v) {
	    	if (switchSetOn) {
	    		switchSetOn = false;
	    		bitmapResource = R.drawable.switch_off;
	    		tPosition = 0;
	        }
	    	else {
	    		switchSetOn = true;
	    		bitmapResource = R.drawable.switch_on;
	    		tPosition = 1;
	    	}
	    	setSwitchState();
	    	
	    	if (isChannelValid()) {
	    		tControlModusListener.onChannelChange(tChannelH.getDefaultChannelValue(), tPosition);
	    	}
	    }
	};

	private void init(Context context, Object object) {
		switchIconRect = new RectF(0.0f, 0.0f, 1.0f, 1.0f);
		this.createWidget();

		this.setOnClickListener(onclicklistener);

		// init protocol mapping
		protocolMap.put(RCConstants.CHANNEL_H, "");
		protocolMap.put(RCConstants.INVERTED_H, "");
		protocolMap.put(RCConstants.MAX_RANGE_H, "");
		protocolMap.put(RCConstants.MIN_RANGE_H, "");
		protocolMap.put(RCConstants.TRIMM_H, "");
		
		protocolMap.put(RCConstants.CHANNEL_V, "");
		protocolMap.put(RCConstants.INVERTED_V, "");
		protocolMap.put(RCConstants.MAX_RANGE_V, "");
		protocolMap.put(RCConstants.MIN_RANGE_V, "");
		protocolMap.put(RCConstants.TRIMM_V, "");
	}

	@Override
	protected void onDraw(Canvas canvas) 
	{
		float scale = (float) getWidth();
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.scale(scale, scale);
		canvas.drawRect(switchIconRect, switchIconPaint);
		canvas.restore();
		super.onDraw(canvas);
	}

	@Override
	public Map<String, String> getProtocolMap() {
		return protocolMap;
	}

	@Override
	public void updateProtocolMap() {
		try{
			tChannelH.setDefaultChannelValue(getInt(RCConstants.CHANNEL_H));
			tChannelH.setInverted(getBoolean(RCConstants.INVERTED_H));
			tChannelH.setMaxRange(getInt(RCConstants.MAX_RANGE_H));
			tChannelH.setMinRange(getInt(RCConstants.MIN_RANGE_H));
			tChannelH.setTrimm(getInt(RCConstants.TRIMM_H));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private int getInt(String key){
		try{
			return Integer.parseInt(tWidgetConfig.protocolMap.get(key));
		}catch(NumberFormatException e){
			return -1;
		}
	}

	public boolean getBoolean(String key){
		try{
			return Boolean.parseBoolean(tWidgetConfig.protocolMap.get(key));
		}catch(Exception e){
			return false;
		}
	}

	@Override
	public int getNumberOfChannelListener() {
		return 1; 
	}

	private void setSwitchState() {
		this.createWidget();
		postInvalidate();
	}

	private void createWidget() {
		switchIconBitmap = BitmapFactory.decodeResource(getContext().getResources(), bitmapResource);
		BitmapShader paperShader = new BitmapShader(switchIconBitmap, Shader.TileMode.MIRROR, Shader.TileMode.MIRROR);
		Matrix paperMatrix = new Matrix();
		paperMatrix.setScale(1.0f / switchIconBitmap.getWidth(), 1.0f / switchIconBitmap.getHeight());
		paperShader.setLocalMatrix(paperMatrix);
		switchIconPaint = new Paint();
		switchIconPaint.setFilterBitmap(false);
		switchIconPaint.setStyle(Paint.Style.FILL);
		switchIconPaint.setShader(paperShader);
	}

	public static ViewElementConfig getDefaultViewElementConfig() {
		ResizeConfig rc = new ResizeConfig();
	    rc.keepRatio = true;
		rc.maxHeight = 300;
	    rc.minHeight = 34;
	    rc.maxWidth = 800;
	    rc.minWidth = 137;

	    LayoutParams lp = new LayoutParams(137, 34 , 0, 0);

	    ViewElementConfig elementConfig = new ViewElementConfig(SwitchWidget.class.getName(), lp, rc);
	    elementConfig.setAlpha(1);
	    return elementConfig;
	}
}