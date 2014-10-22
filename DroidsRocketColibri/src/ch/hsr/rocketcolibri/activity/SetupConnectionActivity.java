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
 * @author Lorenz Schelling
 */
public class SetupConnectionActivity extends RCActivity{
	private Semaphore waitForServiceSem = new Semaphore(0);
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new View(this));
		showLoading(false);
		setContentView(R.layout.setup_connection);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	
	}
	

	@Override
	protected void onServiceReady() {
		waitForServiceSem.release();
	}

	@Override
	protected String getClassName() {
		return SetupConnectionActivity.class.getSimpleName();
	}
}
