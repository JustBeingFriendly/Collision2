package com.stephen.collision2;

import java.util.ArrayList;
import java.util.Random;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class AnimationView extends SurfaceView implements Runnable, SurfaceHolder.Callback {

	private Thread animation = null;
	private boolean running;
	
	//Needed for ground
	private float height;
	private float width;
	private Bitmap mars;
	private BitmapShader marsShade;
	//private Paint paint = new Paint();
	private Paint marsBackground = new Paint();
	
	
	//Needed for spaceship
	private Bitmap spaceShip;
	
	protected boolean rightThrusterFiring;
	protected boolean leftThrusterFiring;
	protected boolean mainRocketFiring;
	
	private int shipXPos;
	private int shipYPos;
	
	private Random random = new Random();
	
	float bottomThirdScreen;
	
	ArrayList<Integer> initalxPos = new ArrayList<Integer>();
	ArrayList<Integer> initalyPos = new ArrayList<Integer>();
	
	int[] xCor;
	int[] yCor;
	
	boolean arrayInitialised;
	
	private boolean mapped;
	
	public AnimationView(Context context) {
		super(context);
		
		//animation = null;
		
		//Needed for ground
		//marsBackground = new Paint();
		mars = BitmapFactory.decodeResource(getResources(), R.drawable.mars);
		marsShade = (new BitmapShader(mars, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
		marsBackground.setShader(marsShade);
		
		
		
		//Needed for spaceship
		spaceShip = BitmapFactory.decodeResource(getResources(), R.drawable.craftmain);
		rightThrusterFiring = false;
		leftThrusterFiring = false;
		mainRocketFiring = false;
		shipXPos = 0;
		shipYPos = 0;
		//random = new Random();
		//Collision Detection
		
/*		xCor = new int[56];
		yCor = new int[56];*/
		
		arrayInitialised = false;
		//initalxPos = new ArrayList<float[]>();
		//initalyPos = new ArrayList<float[]>();
        
		
		mapped = false;
		getHolder().addCallback(this);
		

	}

	public AnimationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		//animation = null;
		
		//Needed for ground
		//marsBackground = new Paint();
		mars = BitmapFactory.decodeResource(getResources(), R.drawable.mars);
		marsShade = (new BitmapShader(mars, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
		marsBackground.setShader(marsShade);
		
		
		//Needed for spaceship
		spaceShip = BitmapFactory.decodeResource(getResources(), R.drawable.craftmain);
		rightThrusterFiring = false;
		leftThrusterFiring = false;
		mainRocketFiring = false;
		shipXPos = 0;
		shipYPos = 0;
		//random = new Random();
		
//		xCor = new int[56];
//		yCor = new int[56];
		
		arrayInitialised = false;
		
		mapped = false;
		getHolder().addCallback(this);
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// start the animation thread once the surface has been created
		animation = new Thread(this);
		running = true;
		animation.start(); // start a new thread to handle this activities
							// animation
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		running = false;
		if (animation != null) {
			try {
				animation.join(); // finish the animation thread and let the
									// animation thread die a natural death
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		//Finds screen width and height (only works after screen initialised otherwise values are 0
		if (width == 0){			
			width = this.getWidth();
			height = this.getHeight();
			bottomThirdScreen = (height / 3) * 2; //Finds bottom third of screen, use as baseline x position for ground drawing			
		}
		
		shipYPos =  0;
		shipXPos =(int) width / 2;
		int gravity = 0;
		int verticalThrust = 0;
		
		GroundGeneration();
		
		while (running) {
			Canvas canvas = null;
			SurfaceHolder holder = getHolder();

			synchronized (holder) {
				initalxPos.clear();
				initalyPos.clear();
				
				if(rightThrusterFiring){
					shipXPos-=6;
				}
				if(leftThrusterFiring){
					shipXPos+=6;
				}
				
				if(mainRocketFiring){
					if(verticalThrust <= 1){
						verticalThrust += 1;
					}
					shipYPos -= verticalThrust;
					if(gravity > 3){
						gravity -= verticalThrust;
					}
				}else{
					verticalThrust = 0;
					
					if( gravity < 3) {					
						gravity++;
					}
					shipYPos += gravity;
				}
				
				canvas = holder.lockCanvas();
				
				/*if(crashList.size() > 0){
					for(Crash c : crashList){
						canvas.drawCircle(c.getX(), c.getY(), 20, c.getPaint());
					}
				}*/
				
				canvas.drawColor(Color.BLACK); //Background Colour
				
				if(shipXPos >= width){
					shipXPos = 1;
				}
				else if (shipXPos < 0){
					shipXPos = (int) (width -1);
				}
				
				canvas.drawBitmap(spaceShip, shipXPos, shipYPos, null);
				////////////////////////////////////
				drawGround(canvas);
				////////////////////////////////////
				if(crashList.size() > 0){
					for(Crash c : crashList){
						canvas.drawCircle(c.getX(), c.getY(),  (spaceShip.getWidth() *2), c.getPaint());
					}
				}
				
				if(!arrayInitialised){
					xCor = new int[initalxPos.size()];
					yCor = new int[initalyPos.size()];
					
					for (int i = 0; i < xCor.length; i++){
						xCor[i] = initalxPos.get(i);
						yCor[i] = initalyPos.get(i);
						arrayInitialised = true;
					}
				}
				contains = false;
				contains = contains(xCor, yCor, (double)shipXPos, (double)(shipYPos + (spaceShip.getHeight())));
				if(contains){
					try{
						crashList.add(new Crash(shipXPos, shipYPos));
					}
					catch(NullPointerException e){
						
					}
					
					shipYPos = 0;
				}
			}
			holder.unlockCanvasAndPost(canvas);
		}
	}
	
	ArrayList<Crash> crashList = new ArrayList<Crash>();
	
	class Crash{
		private float xCrashCor;
		private float yCrashCor;
		private Paint crashPaint;
		
		public Crash(int x, int y){
			xCrashCor = (float) x;
			yCrashCor = (float) y;
			crashPaint = new Paint(Color.BLACK);
		}
		

		
		public float getX(){
			return xCrashCor;
		}
		
		public float getY(){
			return yCrashCor;
		}
		
		public Paint getPaint(){
			return crashPaint;
		}
		
	}
	
	public boolean contains(int[] xcor, int[] ycor, double x0, double y0) {
		int crossings = 0;
		
		for (int i = 0; i < xcor.length - 1; i++) {
			int x1 = xcor[i];
			int x2 = xcor[i + 1];

			int y1 = ycor[i];
			int y2 = ycor[i + 1];

			int dy = y2 - y1;
			int dx = x2 - x1;

			double slope = 0;
			if (dx != 0) {
				slope = (double)dy / dx;
				//Log.e("slope",Double.toString(slope)+"["+i+"]");
			}

			boolean cond1 = (x1 <= x0) && (x0 < x2); // is it in the range?
			boolean cond2 = (x2 <= x0) && (x0 < x1); // is it in the reverse
														// range?
			boolean above = (y0 < slope * (x0 - x1) + y1); // point slope y - y1
															// = m(x -x1)
															// -(check if y0
															// below line of
															// closed polygon)

			if ((cond1 || cond2) && above) {
				crossings++;
			}
		}
		return (crossings % 2 == 0); // even or odd
	}
	
	boolean contains;
	
	
	
	private void drawGround(Canvas canvas){
		float nextXPos = 0;
		//Draw baseline for ground
		Path mainPath = new Path();		
		mainPath.moveTo(nextXPos, bottomThirdScreen); //Paths starting position
		nextXPos = GroundAssembler(mainPath, "FeatureArray");
		//nextXPos = GroundGeneration(nextXPos);
		//Draws from far right, to bottom right, bottom left and back to start, effectively closing the shape
		//mainPath.lineTo(width, bottomThirdScreen);
		mainPath.lineTo(width, height);
		mainPath.lineTo(0, height);
		mainPath.lineTo(0, bottomThirdScreen);
		canvas.drawPath(mainPath, marsBackground);
		mapped = true;
	}
	
	ArrayList<Integer> intList;
	
	private int[]FeatureArray;
	
	
	private void GroundGeneration(){
		intList = new ArrayList<Integer>();
		int ranNum = random.nextInt(6); //Generate number between 0 and 5
		Path path = new Path();
		//float nextX = 0;
		float screenCheck = 0;
		path.moveTo(screenCheck, bottomThirdScreen);
		
		intList.add(ranNum);
		screenCheck = GroundAssembler(path, "intList");
		boolean filled = false;
		while(!filled){
			
			if(screenCheck < width){
				
				ranNum = random.nextInt(6);
				intList.add(ranNum);
				screenCheck = GroundAssembler(path, "intList");
			}
			else{
				filled = true;
			}

		}
		//Check landing pad is spawned, and if so is fully spawned on screen
		boolean zeroFound = false;
		boolean restart = false;
		
		for(int i = 0 ; i < intList.size(); i++){
			//As long as the landing pad isn't in the last position
			if(intList.get(i) != intList.size()){
				
				if(intList.get(i) == 0){
					//Only spawn one landing pad
					if(zeroFound){
						restart = true;
					}else{
					zeroFound = true;
					}
				}
			}
		}
		//If zero (landing pad) not spawned or restart conditions met, restart
		if(zeroFound == false || restart == true){
			GroundGeneration();		
			//Otherwise generate landscape
		}else{
			FeatureArray = new int[intList.size()];
			
			for(int i = 0; i < FeatureArray.length; i++){
				FeatureArray[i] = intList.get(i);
			}
		}
		
		
	}
	
	private float GroundAssembler(Path path, String id ){
		Path sharedPath = path;
		float nextX = 0;
		if(id.equals("intList")){
			for(int i : intList){
				switch(i){
				case 0: nextX = landingPad(sharedPath, nextX);
					break;
				case 1: nextX = InversePyramidFeature(sharedPath, nextX);
					break;
				case 2: nextX = JaggedCliffFeature(sharedPath, nextX);
					break;
				case 3: nextX = TreeFeature(sharedPath, nextX);
					break;	
				case 4: nextX = SpireFeature(sharedPath, nextX);
					break;
				case 5: nextX = PyramidFeature(sharedPath, nextX);
					break;
				}
			}
		}
		
		else{
			for(int i : FeatureArray){
				switch(i){
				case 0: nextX = landingPad(sharedPath, nextX);
					break;
				case 1: nextX = InversePyramidFeature(sharedPath, nextX);
					break;
				case 2: nextX = JaggedCliffFeature(sharedPath, nextX);
					break;
				case 3: nextX = TreeFeature(sharedPath, nextX);
					break;	
				case 4: nextX = SpireFeature(sharedPath, nextX);
					break;
				case 5: nextX = PyramidFeature(sharedPath, nextX);
					break;
				}
			}
		}
		return nextX;		
	}
	

	
	private float JaggedCliffFeature(Path path, float xPos){
		float sideLength = width / 9;
		float yPos = bottomThirdScreen;
		if(!mapped){
		xPos += (sideLength /3);
		yPos -= sideLength;
			path.lineTo(xPos, yPos);
			initalxPos.add((int)xPos);
			initalyPos.add((int)yPos);
		xPos -= (sideLength /3);
			path.lineTo(xPos, yPos);
			initalxPos.add((int)xPos);
			initalyPos.add((int)yPos);
		xPos += (sideLength /3);
		yPos -= sideLength;
			path.lineTo(xPos, yPos);
			initalxPos.add((int)xPos);
			initalyPos.add((int)yPos);
		xPos += (sideLength /9);
		yPos += sideLength;
			path.lineTo(xPos, yPos);
			initalxPos.add((int)xPos);
			initalyPos.add((int)yPos);
		xPos += (sideLength /3);		
			path.lineTo(xPos, yPos);
			initalxPos.add((int)xPos);
			initalyPos.add((int)yPos);
		xPos += (sideLength /9);
		yPos += sideLength;
			path.lineTo(xPos, yPos);
			initalxPos.add((int)xPos);
			initalyPos.add((int)yPos);
		xPos += (sideLength /3);		
			path.lineTo(xPos, yPos);
			initalxPos.add((int)xPos);
			initalyPos.add((int)yPos);
		xPos += (sideLength /3);
		yPos += sideLength;
			path.lineTo(xPos, yPos);
			initalxPos.add((int)xPos);
			initalyPos.add((int)yPos);
		}
		else{
			xPos += (sideLength /3);
			yPos -= sideLength;
				path.lineTo(xPos, yPos);

			xPos -= (sideLength /3);
				path.lineTo(xPos, yPos);

			xPos += (sideLength /3);
			yPos -= sideLength;
				path.lineTo(xPos, yPos);

			xPos += (sideLength /9);
			yPos += sideLength;
				path.lineTo(xPos, yPos);

			xPos += (sideLength /3);		
				path.lineTo(xPos, yPos);

			xPos += (sideLength /9);
			yPos += sideLength;
				path.lineTo(xPos, yPos);

			xPos += (sideLength /3);		
				path.lineTo(xPos, yPos);

			xPos += (sideLength /3);
			yPos += sideLength;
				path.lineTo(xPos, yPos);

		}
			
		/*float radius = width / 9;
		xPos += radius;
		
		path.addCircle(xPos, bottomThirdScreen, radius, Direction.CW);
		xPos += radius;*/
		return xPos;
	}
	
	private float InversePyramidFeature(Path path, float xPos){
		float pyramidSide = height / 3;
		float yPos = bottomThirdScreen;
		if(!mapped){
			path.lineTo(xPos, yPos);
			initalxPos.add((int)xPos);
			initalyPos.add((int)yPos);
		xPos +=  (pyramidSide/2);
		yPos +=  pyramidSide;
			path.lineTo(xPos, yPos);
			initalxPos.add((int)xPos);
			initalyPos.add((int)yPos);
		xPos +=  (pyramidSide/2);
		yPos -=  pyramidSide;
			path.lineTo(xPos, yPos);
			initalxPos.add((int)xPos);
			initalyPos.add((int)yPos);
		}
		else{
			path.lineTo(xPos, yPos);
		xPos +=  (pyramidSide/2);
		yPos +=  pyramidSide;
			path.lineTo(xPos, yPos);
		xPos +=  (pyramidSide/2);
		yPos -=  pyramidSide;
			path.lineTo(xPos, yPos);
		}
		return xPos;		
	}
		
	private float PyramidFeature(Path path, float xPos){
		float pyramidSide = height / 3;
		float yPos = bottomThirdScreen;
		if(!mapped){
			path.lineTo(xPos, yPos);
			initalxPos.add((int)xPos);
			initalyPos.add((int)yPos);
		xPos +=  (pyramidSide/2);
		yPos -=  pyramidSide;
			path.lineTo(xPos, yPos);
			initalxPos.add((int)xPos);
			initalyPos.add((int)yPos);
		xPos +=  (pyramidSide/2);
		yPos +=  pyramidSide;
			path.lineTo(xPos, yPos);
			initalxPos.add((int)xPos);
			initalyPos.add((int)yPos);
		}
		else{
			path.lineTo(xPos, yPos);
		xPos +=  (pyramidSide/2);
		yPos -=  pyramidSide;
			path.lineTo(xPos, yPos);
		xPos +=  (pyramidSide/2);
		yPos +=  pyramidSide;
			path.lineTo(xPos, yPos);
		}
			
		return xPos;		
	}
		
	private float TreeFeature(Path path, float xPos){
		float treeBranch = height / 9;
		float yPos = bottomThirdScreen;
		if(!mapped){
		//Left under branch feature feature
			xPos += (treeBranch / 4);
			yPos += (treeBranch / 4);
				path.lineTo(xPos, yPos);
				initalxPos.add((int)xPos);
				initalyPos.add((int)yPos);
			xPos += (treeBranch / 4);
			yPos -= (treeBranch / 4);
				path.lineTo(xPos, yPos);
				initalxPos.add((int)xPos);
				initalyPos.add((int)yPos);
			xPos += (treeBranch / 4);
			yPos += (treeBranch / 4);
				path.lineTo(xPos, yPos);
				initalxPos.add((int)xPos);
				initalyPos.add((int)yPos);
			xPos += (treeBranch / 4);
			yPos -= (treeBranch / 4);
				path.lineTo(xPos, yPos);
				initalxPos.add((int)xPos);
				initalyPos.add((int)yPos);
		//Left stump
			yPos -=  treeBranch;
				path.lineTo(xPos, yPos);
				initalxPos.add((int)xPos);
				initalyPos.add((int)yPos);
		//Left branch
			xPos -= treeBranch;
			yPos -= (treeBranch/9);
				path.lineTo(xPos, yPos);
				initalxPos.add((int)xPos);
				initalyPos.add((int)yPos);
			xPos += treeBranch;
				path.lineTo(xPos, yPos);
				initalxPos.add((int)xPos);
				initalyPos.add((int)yPos);
		//top branch
			xPos += (treeBranch /9);
			yPos -= treeBranch;
				path.lineTo(xPos, yPos);
				initalxPos.add((int)xPos);
				initalyPos.add((int)yPos);
			xPos += (treeBranch /9);
			yPos += treeBranch;
				path.lineTo(xPos, yPos);
				initalxPos.add((int)xPos);
				initalyPos.add((int)yPos);
		//Right branch
			xPos += treeBranch;
			yPos += (treeBranch/9);
				path.lineTo(xPos, yPos);
				initalxPos.add((int)xPos);
				initalyPos.add((int)yPos);
			xPos -= treeBranch;
				path.lineTo(xPos, yPos);
				initalxPos.add((int)xPos);
				initalyPos.add((int)yPos);
		//Right stump
			yPos += treeBranch;
				path.lineTo(xPos, yPos);
				initalxPos.add((int)xPos);
				initalyPos.add((int)yPos);
		//Right under branch feature	
			xPos += (treeBranch / 4);
			yPos -= (treeBranch / 4);
				path.lineTo(xPos, yPos);
				initalxPos.add((int)xPos);
				initalyPos.add((int)yPos);
			xPos += (treeBranch / 4);
			yPos += (treeBranch / 4);
				path.lineTo(xPos, yPos);
				initalxPos.add((int)xPos);
				initalyPos.add((int)yPos);
			xPos += (treeBranch / 4);
			yPos -= (treeBranch / 4);
				path.lineTo(xPos, yPos);
				initalxPos.add((int)xPos);
				initalyPos.add((int)yPos);
			xPos += (treeBranch / 4);
			yPos += (treeBranch / 4);
				path.lineTo(xPos, yPos);
				initalxPos.add((int)xPos);
				initalyPos.add((int)yPos);
		}
		else{
		//Left under branch feature feature
			xPos += (treeBranch / 4);
			yPos += (treeBranch / 4);
				path.lineTo(xPos, yPos);
			xPos += (treeBranch / 4);
			yPos -= (treeBranch / 4);
				path.lineTo(xPos, yPos);
			xPos += (treeBranch / 4);
			yPos += (treeBranch / 4);
				path.lineTo(xPos, yPos);
			xPos += (treeBranch / 4);
			yPos -= (treeBranch / 4);
				path.lineTo(xPos, yPos);
		//Left stump
			yPos -=  treeBranch;
				path.lineTo(xPos, yPos);
		//Left branch
			xPos -= treeBranch;
			yPos -= (treeBranch/9);
				path.lineTo(xPos, yPos);
			xPos += treeBranch;
				path.lineTo(xPos, yPos);
		//top branch
			xPos += (treeBranch /9);
			yPos -= treeBranch;
				path.lineTo(xPos, yPos);
			xPos += (treeBranch /9);
			yPos += treeBranch;
				path.lineTo(xPos, yPos);
		//Right branch
			xPos += treeBranch;
			yPos += (treeBranch/9);
				path.lineTo(xPos, yPos);
			xPos -= treeBranch;
				path.lineTo(xPos, yPos);
		//Right stump
			yPos += treeBranch;
				path.lineTo(xPos, yPos);
		//Right under branch feature	
			xPos += (treeBranch / 4);
			yPos -= (treeBranch / 4);
				path.lineTo(xPos, yPos);
			xPos += (treeBranch / 4);
			yPos += (treeBranch / 4);
				path.lineTo(xPos, yPos);
			xPos += (treeBranch / 4);
			yPos -= (treeBranch / 4);
				path.lineTo(xPos, yPos);
			xPos += (treeBranch / 4);
			yPos += (treeBranch / 4);
				path.lineTo(xPos, yPos);
		}
		return xPos;	
	}

	private float SpireFeature(Path path, float xPos){
			float spireSide = height / 6;
			float yPos = bottomThirdScreen;
			if(!mapped){
/*		//Move one tree branch away from previous feature
			xPos += spireSide;
			path.lineTo(xPos, yPos);*/
		//Left stump
			yPos -=  spireSide;
				path.lineTo(xPos, yPos);
				initalxPos.add((int)xPos);
				initalyPos.add((int)yPos);
		//top branch
			xPos += (spireSide /9);
			yPos -= spireSide;
				path.lineTo(xPos, yPos);
				initalxPos.add((int)xPos);
				initalyPos.add((int)yPos);
			xPos += (spireSide /9);
			yPos += spireSide;
				path.lineTo(xPos, yPos);
				initalxPos.add((int)xPos);
				initalyPos.add((int)yPos);
		//Right stump
			yPos += spireSide;
				path.lineTo(xPos, yPos);
				initalxPos.add((int)xPos);
				initalyPos.add((int)yPos);
			}
			else{
				//Left stump
				yPos -=  spireSide;
					path.lineTo(xPos, yPos);
			//top branch
				xPos += (spireSide /9);
				yPos -= spireSide;
					path.lineTo(xPos, yPos);
				xPos += (spireSide /9);
				yPos += spireSide;
					path.lineTo(xPos, yPos);
			//Right stump
				yPos += spireSide;
					path.lineTo(xPos, yPos);
			}
				
		return xPos;
	}
	
	private float landingPad(Path path, float xPos) {
			float bumpHeight = (height /60);
			float yPos = bottomThirdScreen;	
			if(!mapped){
		//Left Rise
				path.lineTo(xPos, yPos);
				initalxPos.add((int)xPos);
				initalyPos.add((int)yPos);
			xPos += (width /30);
			yPos -= bumpHeight;		
				path.lineTo(xPos, yPos);
				initalxPos.add((int)xPos);
				initalyPos.add((int)yPos);
			yPos += bumpHeight;
				path.lineTo(xPos, yPos);
				initalxPos.add((int)xPos);
				initalyPos.add((int)yPos);
		//Land Site
			xPos += (width /10);
				path.lineTo(xPos, yPos);
				initalxPos.add((int)xPos);
				initalyPos.add((int)yPos);
		//Right Rise
			yPos -= bumpHeight;
				path.lineTo(xPos, yPos);
				initalxPos.add((int)xPos);
				initalyPos.add((int)yPos);
			xPos += (width /30);
			yPos += bumpHeight;		
				path.lineTo(xPos, yPos);
				initalxPos.add((int)xPos);
				initalyPos.add((int)yPos);
			}
			else {
				//Left Rise
				path.lineTo(xPos, yPos);
			xPos += (width /30);
			yPos -= bumpHeight;		
				path.lineTo(xPos, yPos);
			yPos += bumpHeight;
				path.lineTo(xPos, yPos);
		//Land Site
			xPos += (width /10);
				path.lineTo(xPos, yPos);
		//Right Rise
			yPos -= bumpHeight;
				path.lineTo(xPos, yPos);
			xPos += (width /30);
			yPos += bumpHeight;		
				path.lineTo(xPos, yPos);
			}
		return xPos;
	}
	
}
