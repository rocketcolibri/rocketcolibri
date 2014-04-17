/**
 * Copyright 2010 Per-Erik Bergman (per-erik.bergman@jayway.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.hsr.rocketcolibri.view.widget.circleController;

import ch.hsr.rocketcolibri.R;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

/**
 * This class is the setup for the Tutorial part VI located at:
 * http://blog.jayway.com/
 * 
 * @author Per-Erik Bergman (per-erik.bergman@jayway.com)
 * 
 */
public class TutorialPartVI extends Activity {
	/** Called when the activity is first created. */
	MyGLSurfaceView view;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Remove the title bar from the window.
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Make the windows into full screen mode.
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// Create a OpenGL view.
//		view = new MyGLSurfaceView(this);

//		// Creating and attaching the renderer.
//		OpenGLRenderer renderer = new OpenGLRenderer();
//		view.setRenderer(renderer);
		setContentView(findViewById(R.layout.activity_main));
		view = (MyGLSurfaceView) findViewById(R.id.meter1);
//		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//		// Create a new plane.
//		final SimpleObject plane = new SimpleObject(1, 1);
//
//		// Move and rotate the plane.
//		plane.z = 2.1f;
//		plane.rx = -65;
		
//		new Thread(){
//			public void run(){
//				while(true){
//					Log.d("test", "test");
//					try {
//						Thread.sleep(20);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					plane.rx = plane.rx+1;
//					plane.rz = plane.rz+1;
//				}
//			}
//		}.start();

		// Load the texture.
//		plane.loadBitmap(BitmapFactory.decodeResource(getResources(),
//				R.drawable.jay));
//
//		// Add the plane to the renderer.
//		renderer.addMesh(plane);
	}
	
//	   @Override
//	   protected void onPause() {
//	      super.onPause();
//	      view.onPause();
//	   }
//	   
//	   @Override
//	   protected void onResume() {
//	      super.onResume();
//	      view.onResume();
//	   }
}