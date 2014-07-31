package ch.hsr.rocketcolibri.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

public class CacheUtil {
	private Context tContext;
	
	public CacheUtil(Context context){
		tContext = context;
	}
	
	public Bitmap centerCropBitmap(Bitmap srcBmp) {
		Bitmap dstBmp = null;
		if (srcBmp.getWidth() >= srcBmp.getHeight()) {
			dstBmp = Bitmap.createBitmap(srcBmp,
					srcBmp.getWidth() / 2 - srcBmp.getHeight() / 2, 0,
					srcBmp.getHeight(), srcBmp.getHeight());
		} else {
			dstBmp = Bitmap.createBitmap(srcBmp, 0, srcBmp.getHeight() / 2
					- srcBmp.getWidth() / 2, srcBmp.getWidth(),
					srcBmp.getWidth());
		}
		return dstBmp;
	}
	
	public Bitmap createBitmap(View viewToCache, String fileName) throws IOException{
		 Bitmap map = createBitmapFromView(viewToCache);
		 if(map!=null){
			 storeBitmap(map, fileName, 90);
		 }
         return map;
	}
	
	public Bitmap createBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getLayoutParams().width, v.getLayoutParams().height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.measure(v.getLayoutParams().width, v.getLayoutParams().width);
        v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
        Drawable bgDrawable =v.getBackground();
        if (bgDrawable!=null){
            bgDrawable.draw(c);
        }
        v.draw(c);
        return b;
    }
	
	public Bitmap loadBitmap(String fileName) throws FileNotFoundException{
		return BitmapFactory.decodeStream(load(fileName));
	}
	
	public void storeBitmap(Bitmap map, String fileName, int quality) throws IOException {
		FileOutputStream out = null;
		out = tContext.openFileOutput(fileName, Context.MODE_PRIVATE);
		map.compress(Bitmap.CompressFormat.PNG, quality, out);
		out.flush();
		out.close();
	}
	
	public InputStream load(String fileName) throws FileNotFoundException{
		return tContext.openFileInput(fileName);
	}
	
	public File createTempFile(String fileName) throws IOException{
	    File file = File.createTempFile(fileName, "", tContext.getCacheDir());
	    return file;
	}
}
