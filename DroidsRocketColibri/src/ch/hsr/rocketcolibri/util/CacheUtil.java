package ch.hsr.rocketcolibri.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

public class CacheUtil {
	private Context tContext;
	
	public CacheUtil(Context context){
		tContext = context;
	}
	
//	public Bitmap loadTumbnail(String fileName){
//		File imgFile = new File(tContext.getCacheDir(),fileName);
//		Bitmap map = null;
//		Log.d("loadTumbnail", fileName+": "+imgFile.exists());
//		if(imgFile.exists()){
//		    map = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//		}
//		return map;
//	}
	
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
        v.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
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
