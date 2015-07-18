package com.example.sensorgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Map {
	//�p�ӂ���}�b�v�̈Ӗ�
	public final static int PATH_TILE = 0;
	public final static int	WALL_TILE = 1;
	public final static int	EXIT_TILE = 2;
	public final static int	VOID_TILE = 3;
	
	//�}�b�v�̃}�X�̐�
	public final static int MAP_ROWS = 32;
	public final static int MAP_COLS = 20;
	
	//�}�b�v�f�[�^
	private int[][] mData;
	
	//�P�}�X�̏c���T�C�Y
	private int mTileWidth;
	private int mTileHeight;

	//�}�X�\���p�t�B�[���h
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
	
	//�}�b�v�f�[�^���Z�b�g����
	public void setData(int[][] data){
		mData = data;
	}
	
	//��ʃT�C�Y�����P�}�X�̃T�C�Y�����肷��
	public void setSize(int w,int h){
		mTileWidth = w / MAP_COLS;
		mTileHeight = h / MAP_ROWS;
	}
	
	//�}�b�v�̕`�揈��
	public void draw(Canvas canvas){
		for (int i = 0; i < MAP_ROWS; i++) {
			for (int j = 0; j < MAP_COLS; j++) {
				int x = j * mTileWidth;					//�\���ʒu(x���W)
				int y = i * mTileHeight;				//�\���ʒu(y���W)
				
				int width = (j + 1) * mTileWidth;		//�\����
				int height = (i + 1) * mTileHeight;		//�\������
				
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
	//���W�ɂ���Z���̃^�C�v���擾
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
