package com.example.sensorgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Ball {
	private int mX = 0;			//x座標
	private int mY = 0;			//y座標
	private int mRadius = 0;	//半径
	
	//ボール描画用
	private Paint mBallPaint;
	
	public Ball(){
		mBallPaint = new Paint();
		mBallPaint.setColor(Color.RED);
		mBallPaint.setAntiAlias(true);
	}
	
	public void setPosition(int x,int y){
		mX = x;
		mY = y;
	}
	
	public int getX(){
		return mX;
	}
	public int getY(){
		return mY;
	}
	
	public int getRadus(){
		return mRadius;
	}
	
	public void setRadus(int radius){
		mRadius = radius;
		
	}
	//描画
	public void draw(Canvas canvas){
		canvas.drawCircle(mX, mY, mRadius, mBallPaint);
		
	}
	//移動
	public void move(int dx,int dy){
		mX += dx;
		mY += dy;
	}
	
}
