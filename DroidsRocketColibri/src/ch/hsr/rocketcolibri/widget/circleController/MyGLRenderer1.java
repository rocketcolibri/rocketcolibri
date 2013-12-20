package ch.hsr.rocketcolibri.widget.circleController;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
  
public class MyGLRenderer1 implements GLSurfaceView.Renderer {
    
   private Context context;
   private TextureCube cube;
   // For controlling cube's z-position, x and y angles and speeds
   float angleX = 0;
   float angleY = 0;
   float speedX = 0;
   float speedY = 0;
   float z = -6.0f;
   
   int currentTextureFilter = 0;  // Texture filter

   // Lighting (NEW)
   boolean lightingEnabled = false;   // Is lighting on? (NEW)
   private float[] lightAmbient = {0.5f, 0.5f, 0.5f, 1.0f};
   private float[] lightDiffuse = {1.0f, 1.0f, 1.0f, 1.0f};
   private float[] lightPosition = {0.0f, 0.0f, 2.0f, 1.0f};
  
   // Constructor
   public MyGLRenderer1(Context context) {
      this.context = context;
      cube = new TextureCube();
   }
  
   // Call back when the surface is first created or re-created.
   @Override
   public void onSurfaceCreated(GL10 gl, EGLConfig config) {
      gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);  // Set color's clear-value to black
      gl.glClearDepthf(1.0f);            // Set depth's clear-value to farthest
      gl.glEnable(GL10.GL_DEPTH_TEST);   // Enables depth-buffer for hidden surface removal
      gl.glDepthFunc(GL10.GL_LEQUAL);    // The type of depth testing to do
      gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);  // nice perspective view
      gl.glShadeModel(GL10.GL_SMOOTH);   // Enable smooth shading of color
      gl.glDisable(GL10.GL_DITHER);      // Disable dithering for better performance

      // Setup Texture, each time the surface is created
      cube.loadTexture(gl, context);    // Load image into Texture
      gl.glEnable(GL10.GL_TEXTURE_2D);  // Enable texture
      
      // Setup lighting GL_LIGHT1 with ambient and diffuse lights (NEW)
      gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, lightAmbient, 0);
      gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_DIFFUSE, lightDiffuse, 0);
      gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, lightPosition, 0);
      gl.glEnable(GL10.GL_LIGHT1);   // Enable Light 1 (NEW)
      gl.glEnable(GL10.GL_LIGHT0);   // Enable the default Light 0 (NEW)
   }
   
   // Call back after onSurfaceCreated() or whenever the window's size changes.
   @Override
   public void onSurfaceChanged(GL10 gl, int width, int height) {
      // NO CHANGE - SKIP
//      .......
   }
  
   // Call back to draw the current frame.
   @Override
   public void onDrawFrame(GL10 gl) {
      // Clear color and depth buffers
      gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
  
      // Enable lighting? (NEW)
      if (lightingEnabled) {
         gl.glEnable(GL10.GL_LIGHTING);
      } else {
         gl.glDisable(GL10.GL_LIGHTING);
      }
      
      // ----- Render the Cube -----
      gl.glLoadIdentity();              // Reset the model-view matrix
      gl.glTranslatef(0.0f, 0.0f, z);   // Translate into the screen
      gl.glRotatef(angleX, 1.0f, 0.0f, 0.0f); // Rotate
      gl.glRotatef(angleY, 0.0f, 1.0f, 0.0f); // Rotate
      cube.draw(gl, currentTextureFilter);
      
      // Update the rotational angle after each refresh
      angleX += speedX;
      angleY += speedY;
   }
}