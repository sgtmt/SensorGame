package com.example.sensorgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Map {
	//用意するマップの意味
	public final static int PATH_TILE = 0;
	public final static int	WALL_TILE = 1;
	public final static int	EXIT_TILE = 2;
	public final static int	VOID_TILE = 3;
	
	//マップのマスの数
	public final static int MAP_ROWS = 32;
	public final static int MAP_COLS = 20;
	
	//マップデータ
	private int[][] mData;
	
	//１マスの縦横サイズ
	private int mTileWidth;
	private int mTileHeight;

	//マス表示用フィールド
	private Paint mPathPaint = new Paint();
	private Paint mWallPaint = new Paint();
	private Paint mExitPaint = new Paint();
	private Paint mVoidPaint = new Paint();
	
	public Map(){
		mData = new int[MAP_ROWS][MAP_COLS];
		mPathPaint.setColor(Color.BLACK);
		mWallPaint.setColor(Color.WHITE);
		mExitPaint.setColor(Color.CYAN);
		mVoidPaint.setColor(Color.YELLOW);
	}
	
	//マップデータをセットする
	public void setData(int[][] data){
		mData = data;
	}
	
	//画面サイズかた１マスのサイズを決定する
	public void setSize(int w,int h){
		mTileWidth = w / MAP_COLS;
		mTileHeight = h / MAP_ROWS;
	}
	
	//マップの描画処理
	public void draw(Canvas canvas){
		for (int i = 0; i < MAP_ROWS; i++) {
			for (int j = 0; j < MAP_COLS; j++) {
				int x = j * mTileWidth;					//表示位置(x座標)
				int y = i * mTileHeight;				//表示位置(y座標)
				
				int width = (j + 1) * mTileWidth;		//表示幅
				int height = (i + 1) * mTileHeight;		//表示高さ
				
				switch(mData[i][j]){
				case PATH_TILE:
					canvas.drawRect(x, y,width,height,mPathPaint);
					break;
				case WALL_TILE:
					canvas.drawRect(x, y,width,height,mWallPaint);
					break;
				case EXIT_TILE:
					canvas.drawRect(x, y,width,height,mExitPaint);
					break;
				case VOID_TILE:
					canvas.drawRect(x, y,width,height,mVoidPaint);
					break;
				}
			}
		}
	}
	//座標にあるセルのタイプを取得
	public int getCellType(int x,int y){
		if (mTileHeight == 0 || mTileWidth == 0){
			return PATH_TILE;
		}
		int j = x / mTileWidth;
		int i = y / mTileHeight;
		if (i < MAP_ROWS && j < MAP_COLS) 
			return mData[i][j];
		return PATH_TILE;
	}
}
