package com.legendleo.game2048;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.GridLayout;

public class GameView extends GridLayout {

	public GameView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initGameView();
	}

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initGameView();
	}

	public GameView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initGameView();
	}
	
	//定义一个二维数组记录方阵的值
	private Card[][] cardsMap = new Card[Config.LINES][Config.LINES];
	
	//定义临时变量记录上一步方阵的值，以便回退上一步
	private int[][] tempCardsMap1 = new int[Config.LINES][Config.LINES];
	private int tempScore1 = 0;
	private int tempBestScore1 = 0;

	private int[][] tempCardsMap2 = new int[Config.LINES][Config.LINES];
	private int tempScore2 = 0;
	private int tempBestScore2 = 0;
	
	private List<Point> emptyPoints = new ArrayList<Point>();
	
	//颜色代码
	private int bgc = 0xffbbada0;
	
	//动画效果
	private Animation zoomIn;
	
	//回退扣分
	private int minusPoints = 0;
	//当前卡片中最大值
	private int maxCardValue = 0;

	// 初始化
	public void initGameView() {
		//设置成Config.LINES列、背景颜色
		setColumnCount(Config.LINES);
		setBackgroundColor(bgc);
		
		setOnTouchListener(new OnTouchListener() {
			// 滑动起始位置和偏移量
			private float startX, startY, offsetX, offsetY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 判断触摸动作
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = event.getX();
					startY = event.getY();
					break;
				case MotionEvent.ACTION_UP:
					offsetX = event.getX() - startX;
					offsetY = event.getY() - startY;

					//水平方向、上下方向
					if (Math.abs(offsetX) > Math.abs(offsetY)) {
						if (offsetX < -5) {
							swipeLeft();
						} else if (offsetX > 5) {
							swipeRight();
						}
					} else {
						if(offsetY < -5){
							swipeUp();
						}else if(offsetY > 5){
							swipeDown();
						}
					}
					break;
				}

				return true;
			}
		});
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		
		//计算卡片宽度，左右留宽共10
		int cardWidth = (Math.min(w, h) - 10)/Config.LINES;

		//添加卡片
		addCards(cardWidth, cardWidth);
		
		//如果读取数据得分不为0,则load
		if(!loadCardsMap(cardWidth, cardWidth)){
			startGame();
		}
	}
	
	//添加卡片
	private void addCards(int cardWidth, int cardHeight){
		Card c;
		//循环添加Config.LINES*Config.LINES的卡片
		for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++) {
				c = new Card(getContext());
				c.setNum(0);
				addView(c, cardWidth, cardHeight);
				
				cardsMap[x][y] = c;
			}
		}
	}
	
	//启动游戏
	public void startGame(){
		
		//分数清0
		MainActivity aty = MainActivity.getMainActivity();
		aty.clearScore();
		aty.showBestScore(aty.getBestScore());
		
		
		for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++) {
				cardsMap[x][y].setNum(0);
			}
		}
		
		addRandomNum();
		addRandomNum();
		
		//startgame后记录当前数据到temp2，同时到temp1
		savePreviousCardsMap2(cardsMap, 0, aty.getBestScore());
		savePreviousCardsMap1();
	}
	
	//增加随机数方法
	private void addRandomNum(){
		//初始清空
		emptyPoints.clear();
		
		//把为空的方块坐标add到ArrayList
		for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++) {
				if(cardsMap[x][y].getNum() <= 0){
					emptyPoints.add(new Point(x, y));
				}
			}
		}
		//随机移除一个方块并给它附一个随机值2或4
		Point p = emptyPoints.remove((int)(Math.random() * emptyPoints.size()));
		cardsMap[p.x][p.y].setNum(Math.random() > 0.1?2:4);
		
		//增加淡入效果		
		zoomIn = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		zoomIn.setDuration(300);
		cardsMap[p.x][p.y].getLabel().startAnimation(zoomIn);
	}
	
	//向左滑动
	private void swipeLeft(){
		
		//位置改变标记
		boolean merge = false;
		
		//遍历每个方块
		for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++) {
				//判断每个方块的右一位方块的值
				for (int x1 = x+1; x1 < Config.LINES; x1++) {
					if(cardsMap[x1][y].getNum() > 0){
						if(cardsMap[x][y].getNum() == 0){
							
							cardsMap[x][y].setNum(cardsMap[x1][y].getNum());
							cardsMap[x1][y].setNum(0);
							
							//重复执行一遍，避免相邻两个没有合并
							x--;
							merge = true;
						}else if(cardsMap[x][y].equals(cardsMap[x1][y])){
							cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
							cardsMap[x1][y].setNum(0);
							
							//有合并就增加计分
							MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
							merge = true;
						}
						break;
					}
				}
			}
		}
		
		//有位置改变则添加一个随机数
		if(merge){
			addRandomNum();

			//先把temp2值赋给temp1
			savePreviousCardsMap1();
			//再把当前数据地图值赋给temp2
			savePreviousCardsMap2(cardsMap, MainActivity.getMainActivity().getScore(), MainActivity.getMainActivity().getBestScore());
			
			checkComplete();
		}
	}
	
	//向右滑动
	private void swipeRight(){
		
		//位置改变标记
		boolean merge = false;
		
		//遍历每个方块
		for (int y = 0; y < Config.LINES; y++) {
			for (int x = Config.LINES - 1; x >= 0; x--) {
				//判断每个方块的左一位方块的值
				for (int x1 = x-1; x1 >= 0; x1--) {
					if(cardsMap[x1][y].getNum() > 0){
						if(cardsMap[x][y].getNum() == 0){
							cardsMap[x][y].setNum(cardsMap[x1][y].getNum());
							cardsMap[x1][y].setNum(0);
							
							//重复执行一遍，避免相邻两个没有合并
							x++;
							merge = true;
						}else if(cardsMap[x][y].equals(cardsMap[x1][y])){
							cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
							cardsMap[x1][y].setNum(0);
							
							//有合并就增加计分
							MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
							merge = true;
						}
						break;
					}
				}
			}
		}

		//有位置改变则添加一个随机数
		if(merge){
			addRandomNum();
			
			//先把temp2值赋给temp1
			savePreviousCardsMap1();
			//再把当前数据地图值赋给temp2
			savePreviousCardsMap2(cardsMap, MainActivity.getMainActivity().getScore(), MainActivity.getMainActivity().getBestScore());
			
			checkComplete();
		}
	}
	
	//向上滑动
	private void swipeUp(){

		//位置改变标记
		boolean merge = false;
		
		//遍历每个方块
		for (int x = 0; x < Config.LINES; x++) {
			for (int y = 0; y < Config.LINES; y++) {
				//判断每个方块的下一位方块的值
				for (int y1 = y+1; y1 < Config.LINES; y1++) {
					if(cardsMap[x][y1].getNum() > 0){
						if(cardsMap[x][y].getNum() == 0){
							cardsMap[x][y].setNum(cardsMap[x][y1].getNum());
							cardsMap[x][y1].setNum(0);
							
							//重复执行一遍，避免相邻两个没有合并
							y--;
							merge = true;
						}else if(cardsMap[x][y].equals(cardsMap[x][y1])){
							cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
							cardsMap[x][y1].setNum(0);
							
							//有合并就增加计分
							MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
							merge = true;
						}
						break;
					}
				}
			}
		}

		//有位置改变则添加一个随机数
		if(merge){
			addRandomNum();

			//先把temp2值赋给temp1
			savePreviousCardsMap1();
			//再把当前数据地图值赋给temp2
			savePreviousCardsMap2(cardsMap, MainActivity.getMainActivity().getScore(), MainActivity.getMainActivity().getBestScore());
			
			checkComplete();
		}
	}
	
	//向下滑动
	private void swipeDown(){

		//位置改变标记
		boolean merge = false;
		
		//遍历每个方块
		for (int x = 0; x < Config.LINES; x++) {
			for (int y = Config.LINES - 1; y >= 0; y--) {
				//判断每个方块的上一位方块的值
				for (int y1 = y-1; y1 >= 0; y1--) {
					if(cardsMap[x][y1].getNum() > 0){
						if(cardsMap[x][y].getNum() == 0){
							cardsMap[x][y].setNum(cardsMap[x][y1].getNum());
							cardsMap[x][y1].setNum(0);
							
							//重复执行一遍，避免相邻两个没有合并
							y++;
							merge = true;
						}else if(cardsMap[x][y].equals(cardsMap[x][y1])){
							cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
							cardsMap[x][y1].setNum(0);
							
							//有合并就增加计分
							MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
							merge = true;
						}
						break;
					}
				}
			}
		}

		//有位置改变则添加一个随机数
		if(merge){
			addRandomNum();

			//先把temp2值赋给temp1
			savePreviousCardsMap1();
			//再把当前数据地图值赋给temp2
			savePreviousCardsMap2(cardsMap, MainActivity.getMainActivity().getScore(), MainActivity.getMainActivity().getBestScore());
			
			checkComplete();
		}
	}
	
	//游戏结束判断
	private void checkComplete(){
		//结束标记
		boolean complete = true;
		All: //跳出两层循环
		for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++) {
				
				//有空格或者前后左右有相等的数字时游戏继续，否则结束
				if(cardsMap[x][y].getNum() == 0 || 
						x<Config.LINES - 1 && cardsMap[x][y].equals(cardsMap[x+1][y]) ||
						x>1 && cardsMap[x][y].equals(cardsMap[x-1][y]) ||
						y<Config.LINES - 1 && cardsMap[x][y].equals(cardsMap[x][y+1]) ||
						y>1 && cardsMap[x][y].equals(cardsMap[x][y-1])){
					complete = false;
					break All;
				}
			}
		}
		
		//弹出结束对话框
		if (complete) {
			new AlertDialog.Builder(getContext()).setTitle("你好").setMessage("游戏结束").setPositiveButton("重来", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					startGame();
				}
			}).show();
		}
	}

	//保存当前数据
	public void saveCardsMap(){
		SharedPreferences sp = getContext().getSharedPreferences("cardsmapdata", Context.MODE_PRIVATE);
		String str = "";
		int currentScore = MainActivity.getMainActivity().getScore();
		
		for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++) {
				str += cardsMap[x][y].getNum() + ",";
			}
		}
		
		Editor e = sp.edit();
		e.putInt("currentScore", currentScore);
		e.putString("mapData", str);
		e.commit();
		
	}
	
	//读取保存的数据
	private boolean loadCardsMap(int cardWidth, int cardHeight){
		SharedPreferences sp = getContext().getSharedPreferences("cardsmapdata", Context.MODE_PRIVATE);

		int currentScore = sp.getInt("currentScore", 0);
		if(currentScore > 0){
			MainActivity.getMainActivity().addScore(currentScore);
		
			//参照startGame()方法
			String str = sp.getString("mapData", "");
			String[] arr = str.split(",");
			for (int x = 0; x < Config.LINES; x++) {				
				for (int y = 0; y < Config.LINES; y++) {
					cardsMap[x][y].setNum(Integer.parseInt(arr[x+Config.LINES*y]));
				}
			}

			//读取最高分
			MainActivity aty = MainActivity.getMainActivity();
			aty.showBestScore(aty.getBestScore());

			//读取保存的数据后记录当前数据到temp2，同时到temp1
			savePreviousCardsMap2(cardsMap, currentScore, aty.getBestScore());
			savePreviousCardsMap1();
			
			return true;
		}else{
			
			return false;
		}
		
	}
	
	//保存回退上一步的数据
	private void savePreviousCardsMap1(){
		//更改数组的值
		for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++) {
				tempCardsMap1[x][y] = tempCardsMap2[x][y];
			}
		}
		tempScore1 = tempScore2;
		tempBestScore1 = tempBestScore2;
	}

	private void savePreviousCardsMap2(Card[][] c, int score, int bestscore){
		//更改数组的值
		for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++) {
				tempCardsMap2[x][y] = cardsMap[x][y].getNum();
				
				//取得cards中最大值作为扣分值
				maxCardValue = Math.max(maxCardValue, cardsMap[x][y].getNum());
			}
		}
		tempScore2 = score;
		tempBestScore2 = bestscore;
	}
	
	//读取上一步的数据
	public void loadPreviousCardsMap(){
		
		//退回时把temp1的值赋给当前数据地图，同时赋给temp2
		for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++){
				
				cardsMap[x][y].setNum(tempCardsMap1[x][y]);
				tempCardsMap2[x][y] = tempCardsMap1[x][y];
			}
		}
		
		//在tempScore2被赋值之前计算，当前得分值-最大方块值*2
		minusPoints = (tempScore2 - maxCardValue*2) > 0?(tempScore2 - maxCardValue*2):0;
		//把扣分后的值赋给tempScore1
		tempScore1 = minusPoints;
		
		//先清零再ADD
		MainActivity.getMainActivity().clearScore();
		MainActivity.getMainActivity().addScore(minusPoints);
		//ADD完后清零
		minusPoints = 0;
		maxCardValue = 0;
		
		//当最高分即是当前分，则将最高分回退到之前
		if(tempBestScore2 == tempScore2){
			MainActivity.getMainActivity().saveBestScore(tempBestScore1);
			MainActivity.getMainActivity().showBestScore(tempBestScore1);
		}

		//把temp1值赋给temp2
		tempScore2 = tempScore1;
		tempBestScore2 = tempBestScore1;
	}
	
	public boolean isLoadPreviousCardsMap(){

		//temp1与temp2如果相等则不能回退（包括刚进入游戏后和连续回退）
		//多维数组比较不能用Arrays.equals()
		if(Arrays.deepEquals(tempCardsMap1, tempCardsMap2)){
			
			return false;
		}else{
			
			return true;
		}
		
	}
	
	//取得当前卡片中最大值
	public int getMaxCardValue() {
		return maxCardValue;
	}
	
}
