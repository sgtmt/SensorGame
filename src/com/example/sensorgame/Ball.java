package com.example.sensorgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Ball {
	private int mX = 0;			//x���W
	private int mY = 0;			//y���W
	private int mRadius = 0;	//���a
	
	//�{�[���`��p
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
	//�`��
	public void draw(Canvas canvas){
		canvas.drawCircle(mX, mY, mRadius, mBallPaint);
		
	}
	//�ړ�
	public void move(int dx,int dy){
		mX += dx;
		mY += dy;
	}
	
}
