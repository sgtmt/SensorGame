package com.example.sensorgame;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.os.Build;

public class LabyrinthActivity extends Activity implements OnTouchListener{
	
	private MapView view = null;
	private SensorManager manager;
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		view = new MapView(this);
		setContentView(view);
		
		manager = (SensorManager)getSystemService(SENSOR_SERVICE);
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		Sensor sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		manager.registerListener(view,sensor,SensorManager.SENSOR_DELAY_GAME);
		view.startGame();
	}
	@Override
	protected void onPause(){
		super.onPause();
		manager.unregisterListener(view);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.labyrinth, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event){
		if(view.getState() == MapView.GAME_OVER){
			view.freeHandler();
			finish();
		}
		return true;
	}
	public boolean onTouch(View arg0,MotionEvent arg1){
		return false;
	}
}
