package com.stephen.collision2;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.Button;

public class MainActivity extends Activity {

	private AnimationView av;
	private Button rButton;
	private Button lButton;
	private Button cButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // This
																				// needs
																				// to
																				// be
																				// removed
																				// before
																				// final
																				// submission
		
		
		
		
		
		setContentView(R.layout.activity_main);
		
		av = (AnimationView) findViewById(R.id.animationView1);
		rButton = (Button) findViewById(R.id.rbutton);
		lButton = (Button) findViewById(R.id.lbutton);
		cButton = (Button) findViewById(R.id.cbutton);
		
		rButton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if(event.getAction() != MotionEvent.ACTION_UP){
					av.rightThrusterFiring = true;
				}
				else
				{
					av.rightThrusterFiring = false;
				}
				return false;
			}
		});
		
		
		lButton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if(event.getAction() != MotionEvent.ACTION_UP){
					av.leftThrusterFiring = true;
				}
				else
				{
					av.leftThrusterFiring = false;
				}
				return false;
			}
		});
		
		cButton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if(event.getAction() != MotionEvent.ACTION_UP){
					av.mainRocketFiring = true;
				}
				else
				{
					av.mainRocketFiring = false;
				}
				return false;
			}
		});
		
		
	}
	@Override
	protected void onResume(){
		super.onResume();
		av.resume();
		
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		av.pause();
	}
	
	
}
