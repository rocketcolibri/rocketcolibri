/**
 * Rocket Colibri © 2014
 */
package ch.hsr.rocketcolibri.activity;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import ch.hsr.rocketcolibri.R;
import ch.hsr.rocketcolibri.db.RocketColibriDataHandler;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

/**
 * @author Artan Veliju
 */
public class OpenWithFileActivity extends RCActivity{
	private Semaphore waitForServiceSem = new Semaphore(0);
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new View(this));
		showLoading(false);
		checkIfOpenedWithFile(getIntent());
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		checkIfOpenedWithFile(intent);
	}
	
	private void checkIfOpenedWithFile(Intent intent){
		try{
			final Uri uri = intent.getData();
			System.out.println(uri);
			new Thread(){public void run(){
				try {
					waitForServiceSem.tryAcquire(6, TimeUnit.SECONDS);
					if(rcService!=null){
						System.out.println("got service: "+rcService);
						RocketColibriDataHandler rcDataHandler;
						rcDataHandler = new RocketColibriDataHandler(OpenWithFileActivity.this, rcService.getRocketColibriDB(), false);
						if(rcDataHandler.importData(getContentResolver().openInputStream(uri))){
							uitoast(getString(R.string.import_success_toast));
						}else{
							uitoast(getString(R.string.import_failed_toast));
						}
					}else{
						uitoast(getString(R.string.import_failed_toast));
					}
					hideLoading();
					finish();
					startRocketColibri();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}}.start();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void startRocketColibri(){
		Intent rcStarter = new Intent(OpenWithFileActivity.this, DesktopActivity.class);
		rcStarter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		OpenWithFileActivity.this.startActivity(rcStarter);
	}

	@Override
	protected void onServiceReady() {
		waitForServiceSem.release();
	}

	@Override
	protected String getClassName() {
		return OpenWithFileActivity.class.getSimpleName();
	}
}
