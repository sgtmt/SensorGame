package com.example.sensorgame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Handler;
import android.util.Log;
import android.view.View;

public class MapView extends View implements SensorEventListener,Runnable{
	//センターのノイズ
	private static final float FILTER_FACTOR = 0.2f;
	private static final int WALL = Map.WALL_TILE;
	private static final float REBOUND = 0.5f;
	//端末の加速度
	private float mAccelX = 0.0f;
	private float mAccelY = 0.0f;
	//ボールの加速度
	private float mVectorX = 0.0f;
	private float mVectorY = 0.0f;
	private Handler mHandler = null;

	private Map map = null;
	private Ball ball = null;
	//画面サイズ
	private int mWidth;
	private int mHeight;

	//ゲーム進行状態を持たせる
	public static final int GAME_RUN = 1;
	public static final int GAME_OVER = 2;
	private static final float Of = 0;
	private int state = 0;
	

	public void stopGame() {
		state = GAME_OVER;
	}
	public void freeHandler() {
		if( mHandler != null){
			mHandler.removeCallbacks(this);
			mHandler = null;
		}
	}
	public int getState() {
		return state;
	}

	@Override
	public void run(){
		gameLoop();
	}
	private void gameLoop(){
		mVectorX = mVectorX - mAccelX;
		mVectorY = mVectorY + mAccelY;

		int nextX =ball.getX() + (int)mVectorX;
		int nextY = ball.getY() + (int)mVectorY;
		int radius = ball.getRadus();
		
		if((nextX - radius) < 0) {
			mVectorX *= -0.5f;
		}else if((nextX + radius) > mWidth) {
			mVectorX *= -0.5f;
		}
		if((nextY - radius) < 0) {
			mVectorY *= -0.5f;
		}else if((nextY + radius) > mHeight) {
			mVectorY *= -0.5f;
		}

		//壁で跳ね返るようにする
		if (radius < nextX && nextX < mWidth - radius && radius < nextY && nextY < mHeight - radius) {
			// 壁の当たり判定
			int ul = map.getCellType(nextX - radius, nextY - radius);
			int ur = map.getCellType(nextX + radius, nextY - radius);
			int dl = map.getCellType(nextX - radius, nextY + radius);
			int dr = map.getCellType(nextX + radius, nextY + radius);
			if (ul != WALL && ur != WALL && dl != WALL && dr != WALL) {
			} else if (ul != WALL && ur == WALL && dl != WALL && dr == WALL) {
				mVectorX *= -REBOUND;
			} else if (ul == WALL && ur != WALL && dl == WALL && dr != WALL) {
				mVectorX *= -REBOUND;
			} else if (ul == WALL && ur == WALL && dl != WALL && dr != WALL) {
				mVectorY *= -REBOUND;
			} else if (ul != WALL && ur != WALL && dl == WALL && dr == WALL) {
				mVectorY *= -REBOUND;
			} else if (ul == WALL && ur != WALL && dl != WALL && dr != WALL) {
				if (mVectorX < 0.0f && mVectorY > 0.0f) {
					mVectorX *= -REBOUND;
				} else if (mVectorX > 0.0f && mVectorY < 0.0f) {
					mVectorY *= -REBOUND;
				} else {
					mVectorX *= -REBOUND;
					mVectorY *= -REBOUND;
				}
			} else if (ul != WALL && ur == WALL && dl != WALL && dr != WALL) {
				if (mVectorX > 0.0f && mVectorY > 0.0f) {
					mVectorX *= -REBOUND;
				} else if (mVectorX < 0.0f && mVectorY < 0.0f) {
					mVectorY *= -REBOUND;
				} else {
					mVectorX *= -REBOUND;
					mVectorY *= -REBOUND;
				}
			} else if (ul != WALL && ur != WALL && dl == WALL && dr != WALL) {
				if (mVectorX > 0.0f && mVectorY > 0.0f) {
					mVectorY *= -REBOUND;
				} else if (mVectorX < 0.0f && mVectorY < 0.0f) {
					mVectorX *= -REBOUND;
				} else {
					mVectorX *= -REBOUND;
					mVectorY *= -REBOUND;
				}
			} else if (ul != WALL && ur != WALL && dl != WALL && dr == WALL) {
				if (mVectorX < 0.0f && mVectorY > 0.0f) {
					mVectorY *= -REBOUND;
				} else if (mVectorX > 0.0f && mVectorY < 0.0f) {
					mVectorX *= -REBOUND;
				} else {
					mVectorX *= -REBOUND;
					mVectorY *= -REBOUND;
				}
			} else {
				mVectorX *= -REBOUND;
				mVectorY *= -REBOUND;
			}
		}
		
		
		if( map.getCellType(nextX, nextY) == Map.EXIT_TILE) {
			stopGame();
		}else if( map.getCellType(nextX, nextY) == Map.VOID_TILE) {
			startGame();
		}
		
		//ボールを移動
		ball.move((int)mVectorX,(int)mVectorY);
		//再描画
		invalidate();
		mHandler.removeCallbacks(this);
		mHandler.postDelayed(this, 30);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0,int arg1) {
	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		synchronized(this){
			float[] values = event.values.clone();
			mAccelX = (mAccelX * FILTER_FACTOR) + (values[0]*(1.0f - FILTER_FACTOR));
			mAccelY = (mAccelY * FILTER_FACTOR) + (values[1]*(1.0f - FILTER_FACTOR));
		}
	}
	public MapView(Context context) {
		super(context);
		init();
	}
	private void init(){
		mHandler = new Handler();		
		map =new Map();
		ball = new Ball();

		int[][] data = makeTestData();
		map.setData(data);
		loadData();
	}

	private int[][] loadData() {
		int[][] data = new int[32][20];

		InputStream is = null;
		try {
			is = getContext().getAssets().open("stage1.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line;
			while((line = br.readLine()) != null) {
				Log.d("hoge",line);
			}
			br.close();
		} catch ( IOException e) {
			e.printStackTrace();
		}finally {
			try {
				is.close();
			}catch ( IOException e) {
				e.printStackTrace();
			}
		}
		return data;
	}
	//テストダータ生成
	private int[][] makeTestData() {
		int[][] data = new int[][]{
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1},
				{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1},
				{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 0, 1, 1, 1},
				{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 2, 2, 0, 1},
				{1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 2, 2, 0, 1},
				{1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1},
				{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 1},
				{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1},
				{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1},
				{1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1},
				{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1},
				{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1},
				{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1},
				{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1},
				{1, 0, 0, 0, 3, 3, 3, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1},
				{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 3, 0, 0, 0, 0, 0, 0, 0, 1},
				{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 3, 0, 0, 0, 0, 0, 0, 0, 1},
				{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 3, 0, 0, 0, 0, 0, 0, 0, 1},
				{1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 3, 3, 3, 1, 1, 1, 0, 0, 1},
				{1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 3, 0, 0, 0, 0, 0, 0, 0, 1},
				{1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 3, 0, 0, 0, 0, 0, 0, 0, 1},
				{1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1},
				{1, 0, 0, 1, 1, 1, 1, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 3, 3, 1},
				{1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1},
				{1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1},
				{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1},
				{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
				{1, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
				{1, 3, 3, 3, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
				{1, 3, 3, 3, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}};
		return data;
	}

	Paint fullScr = new Paint();
	Paint message = new Paint();
	@Override
	protected void onDraw(Canvas canvas) {
		map.draw(canvas);
		ball.draw(canvas);
		
		if( state == GAME_OVER ) {
			fullScr.setColor(0xDD000000);
			canvas.drawRect(Of, Of,(float)mWidth,(float)mHeight,fullScr);

			message.setColor(Color.GREEN);
			message.setAntiAlias(true);
			message.setTextSize(80);
			message.setTextAlign(Paint.Align.CENTER);
			canvas.drawText("やったぞう！！お疲れ様", mWidth/2, mHeight/2, message);
		}
	}
	@Override
	protected void onSizeChanged(int w,int h,int oldw,int oldh){
		super.onSizeChanged(w, h, oldw, oldh);
		mWidth = w;
		mHeight =h;
		map.setSize(w, h);
		ball.setRadus(w / (2 * Map.MAP_COLS));
		initGame();
	}
	public void initGame(){
		ball.setPosition(ball.getRadus() * 6, ball.getRadus() * 6);
		invalidate(); //再描画される
	}
	public void startGame(){
		mHandler.post(this);	
		ball.setPosition(ball.getRadus() * 6,ball.getRadus() * 6);		
	}
}
