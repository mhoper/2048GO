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
	
	//����һ����ά�����¼�����ֵ
	private Card[][] cardsMap = new Card[Config.LINES][Config.LINES];
	
	//������ʱ������¼��һ�������ֵ���Ա������һ��
	private int[][] tempCardsMap1 = new int[Config.LINES][Config.LINES];
	private int tempScore1 = 0;
	private int tempBestScore1 = 0;

	private int[][] tempCardsMap2 = new int[Config.LINES][Config.LINES];
	private int tempScore2 = 0;
	private int tempBestScore2 = 0;
	
	private List<Point> emptyPoints = new ArrayList<Point>();
	
	//��ɫ����
	private int bgc = 0xffbbada0;
	
	//����Ч��
	private Animation zoomIn;
	
	//���˿۷�
	private int minusPoints = 0;
	//��ǰ��Ƭ�����ֵ
	private int maxCardValue = 0;

	// ��ʼ��
	public void initGameView() {
		//���ó�Config.LINES�С�������ɫ
		setColumnCount(Config.LINES);
		setBackgroundColor(bgc);
		
		setOnTouchListener(new OnTouchListener() {
			// ������ʼλ�ú�ƫ����
			private float startX, startY, offsetX, offsetY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// �жϴ�������
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = event.getX();
					startY = event.getY();
					break;
				case MotionEvent.ACTION_UP:
					offsetX = event.getX() - startX;
					offsetY = event.getY() - startY;

					//ˮƽ�������·���
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
		
		//���㿨Ƭ��ȣ���������10
		int cardWidth = (Math.min(w, h) - 10)/Config.LINES;

		//��ӿ�Ƭ
		addCards(cardWidth, cardWidth);
		
		//�����ȡ���ݵ÷ֲ�Ϊ0,��load
		if(!loadCardsMap(cardWidth, cardWidth)){
			startGame();
		}
	}
	
	//��ӿ�Ƭ
	private void addCards(int cardWidth, int cardHeight){
		Card c;
		//ѭ�����Config.LINES*Config.LINES�Ŀ�Ƭ
		for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++) {
				c = new Card(getContext());
				c.setNum(0);
				addView(c, cardWidth, cardHeight);
				
				cardsMap[x][y] = c;
			}
		}
	}
	
	//������Ϸ
	public void startGame(){
		
		//������0
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
		
		//startgame���¼��ǰ���ݵ�temp2��ͬʱ��temp1
		savePreviousCardsMap2(cardsMap, 0, aty.getBestScore());
		savePreviousCardsMap1();
	}
	
	//�������������
	private void addRandomNum(){
		//��ʼ���
		emptyPoints.clear();
		
		//��Ϊ�յķ�������add��ArrayList
		for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++) {
				if(cardsMap[x][y].getNum() <= 0){
					emptyPoints.add(new Point(x, y));
				}
			}
		}
		//����Ƴ�һ�����鲢������һ�����ֵ2��4
		Point p = emptyPoints.remove((int)(Math.random() * emptyPoints.size()));
		cardsMap[p.x][p.y].setNum(Math.random() > 0.1?2:4);
		
		//���ӵ���Ч��		
		zoomIn = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		zoomIn.setDuration(300);
		cardsMap[p.x][p.y].getLabel().startAnimation(zoomIn);
	}
	
	//���󻬶�
	private void swipeLeft(){
		
		//λ�øı���
		boolean merge = false;
		
		//����ÿ������
		for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++) {
				//�ж�ÿ���������һλ�����ֵ
				for (int x1 = x+1; x1 < Config.LINES; x1++) {
					if(cardsMap[x1][y].getNum() > 0){
						if(cardsMap[x][y].getNum() == 0){
							
							cardsMap[x][y].setNum(cardsMap[x1][y].getNum());
							cardsMap[x1][y].setNum(0);
							
							//�ظ�ִ��һ�飬������������û�кϲ�
							x--;
							merge = true;
						}else if(cardsMap[x][y].equals(cardsMap[x1][y])){
							cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
							cardsMap[x1][y].setNum(0);
							
							//�кϲ������ӼƷ�
							MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
							merge = true;
						}
						break;
					}
				}
			}
		}
		
		//��λ�øı������һ�������
		if(merge){
			addRandomNum();

			//�Ȱ�temp2ֵ����temp1
			savePreviousCardsMap1();
			//�ٰѵ�ǰ���ݵ�ͼֵ����temp2
			savePreviousCardsMap2(cardsMap, MainActivity.getMainActivity().getScore(), MainActivity.getMainActivity().getBestScore());
			
			checkComplete();
		}
	}
	
	//���һ���
	private void swipeRight(){
		
		//λ�øı���
		boolean merge = false;
		
		//����ÿ������
		for (int y = 0; y < Config.LINES; y++) {
			for (int x = Config.LINES - 1; x >= 0; x--) {
				//�ж�ÿ���������һλ�����ֵ
				for (int x1 = x-1; x1 >= 0; x1--) {
					if(cardsMap[x1][y].getNum() > 0){
						if(cardsMap[x][y].getNum() == 0){
							cardsMap[x][y].setNum(cardsMap[x1][y].getNum());
							cardsMap[x1][y].setNum(0);
							
							//�ظ�ִ��һ�飬������������û�кϲ�
							x++;
							merge = true;
						}else if(cardsMap[x][y].equals(cardsMap[x1][y])){
							cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
							cardsMap[x1][y].setNum(0);
							
							//�кϲ������ӼƷ�
							MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
							merge = true;
						}
						break;
					}
				}
			}
		}

		//��λ�øı������һ�������
		if(merge){
			addRandomNum();
			
			//�Ȱ�temp2ֵ����temp1
			savePreviousCardsMap1();
			//�ٰѵ�ǰ���ݵ�ͼֵ����temp2
			savePreviousCardsMap2(cardsMap, MainActivity.getMainActivity().getScore(), MainActivity.getMainActivity().getBestScore());
			
			checkComplete();
		}
	}
	
	//���ϻ���
	private void swipeUp(){

		//λ�øı���
		boolean merge = false;
		
		//����ÿ������
		for (int x = 0; x < Config.LINES; x++) {
			for (int y = 0; y < Config.LINES; y++) {
				//�ж�ÿ���������һλ�����ֵ
				for (int y1 = y+1; y1 < Config.LINES; y1++) {
					if(cardsMap[x][y1].getNum() > 0){
						if(cardsMap[x][y].getNum() == 0){
							cardsMap[x][y].setNum(cardsMap[x][y1].getNum());
							cardsMap[x][y1].setNum(0);
							
							//�ظ�ִ��һ�飬������������û�кϲ�
							y--;
							merge = true;
						}else if(cardsMap[x][y].equals(cardsMap[x][y1])){
							cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
							cardsMap[x][y1].setNum(0);
							
							//�кϲ������ӼƷ�
							MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
							merge = true;
						}
						break;
					}
				}
			}
		}

		//��λ�øı������һ�������
		if(merge){
			addRandomNum();

			//�Ȱ�temp2ֵ����temp1
			savePreviousCardsMap1();
			//�ٰѵ�ǰ���ݵ�ͼֵ����temp2
			savePreviousCardsMap2(cardsMap, MainActivity.getMainActivity().getScore(), MainActivity.getMainActivity().getBestScore());
			
			checkComplete();
		}
	}
	
	//���»���
	private void swipeDown(){

		//λ�øı���
		boolean merge = false;
		
		//����ÿ������
		for (int x = 0; x < Config.LINES; x++) {
			for (int y = Config.LINES - 1; y >= 0; y--) {
				//�ж�ÿ���������һλ�����ֵ
				for (int y1 = y-1; y1 >= 0; y1--) {
					if(cardsMap[x][y1].getNum() > 0){
						if(cardsMap[x][y].getNum() == 0){
							cardsMap[x][y].setNum(cardsMap[x][y1].getNum());
							cardsMap[x][y1].setNum(0);
							
							//�ظ�ִ��һ�飬������������û�кϲ�
							y++;
							merge = true;
						}else if(cardsMap[x][y].equals(cardsMap[x][y1])){
							cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
							cardsMap[x][y1].setNum(0);
							
							//�кϲ������ӼƷ�
							MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
							merge = true;
						}
						break;
					}
				}
			}
		}

		//��λ�øı������һ�������
		if(merge){
			addRandomNum();

			//�Ȱ�temp2ֵ����temp1
			savePreviousCardsMap1();
			//�ٰѵ�ǰ���ݵ�ͼֵ����temp2
			savePreviousCardsMap2(cardsMap, MainActivity.getMainActivity().getScore(), MainActivity.getMainActivity().getBestScore());
			
			checkComplete();
		}
	}
	
	//��Ϸ�����ж�
	private void checkComplete(){
		//�������
		boolean complete = true;
		All: //��������ѭ��
		for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++) {
				
				//�пո����ǰ����������ȵ�����ʱ��Ϸ�������������
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
		
		//���������Ի���
		if (complete) {
			new AlertDialog.Builder(getContext()).setTitle("���").setMessage("��Ϸ����").setPositiveButton("����", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					startGame();
				}
			}).show();
		}
	}

	//���浱ǰ����
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
	
	//��ȡ���������
	private boolean loadCardsMap(int cardWidth, int cardHeight){
		SharedPreferences sp = getContext().getSharedPreferences("cardsmapdata", Context.MODE_PRIVATE);

		int currentScore = sp.getInt("currentScore", 0);
		if(currentScore > 0){
			MainActivity.getMainActivity().addScore(currentScore);
		
			//����startGame()����
			String str = sp.getString("mapData", "");
			String[] arr = str.split(",");
			for (int x = 0; x < Config.LINES; x++) {				
				for (int y = 0; y < Config.LINES; y++) {
					cardsMap[x][y].setNum(Integer.parseInt(arr[x+Config.LINES*y]));
				}
			}

			//��ȡ��߷�
			MainActivity aty = MainActivity.getMainActivity();
			aty.showBestScore(aty.getBestScore());

			//��ȡ��������ݺ��¼��ǰ���ݵ�temp2��ͬʱ��temp1
			savePreviousCardsMap2(cardsMap, currentScore, aty.getBestScore());
			savePreviousCardsMap1();
			
			return true;
		}else{
			
			return false;
		}
		
	}
	
	//���������һ��������
	private void savePreviousCardsMap1(){
		//���������ֵ
		for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++) {
				tempCardsMap1[x][y] = tempCardsMap2[x][y];
			}
		}
		tempScore1 = tempScore2;
		tempBestScore1 = tempBestScore2;
	}

	private void savePreviousCardsMap2(Card[][] c, int score, int bestscore){
		//���������ֵ
		for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++) {
				tempCardsMap2[x][y] = cardsMap[x][y].getNum();
				
				//ȡ��cards�����ֵ��Ϊ�۷�ֵ
				maxCardValue = Math.max(maxCardValue, cardsMap[x][y].getNum());
			}
		}
		tempScore2 = score;
		tempBestScore2 = bestscore;
	}
	
	//��ȡ��һ��������
	public void loadPreviousCardsMap(){
		
		//�˻�ʱ��temp1��ֵ������ǰ���ݵ�ͼ��ͬʱ����temp2
		for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++){
				
				cardsMap[x][y].setNum(tempCardsMap1[x][y]);
				tempCardsMap2[x][y] = tempCardsMap1[x][y];
			}
		}
		
		//��tempScore2����ֵ֮ǰ���㣬��ǰ�÷�ֵ-��󷽿�ֵ*2
		minusPoints = (tempScore2 - maxCardValue*2) > 0?(tempScore2 - maxCardValue*2):0;
		//�ѿ۷ֺ��ֵ����tempScore1
		tempScore1 = minusPoints;
		
		//��������ADD
		MainActivity.getMainActivity().clearScore();
		MainActivity.getMainActivity().addScore(minusPoints);
		//ADD�������
		minusPoints = 0;
		maxCardValue = 0;
		
		//����߷ּ��ǵ�ǰ�֣�����߷ֻ��˵�֮ǰ
		if(tempBestScore2 == tempScore2){
			MainActivity.getMainActivity().saveBestScore(tempBestScore1);
			MainActivity.getMainActivity().showBestScore(tempBestScore1);
		}

		//��temp1ֵ����temp2
		tempScore2 = tempScore1;
		tempBestScore2 = tempBestScore1;
	}
	
	public boolean isLoadPreviousCardsMap(){

		//temp1��temp2���������ܻ��ˣ������ս�����Ϸ����������ˣ�
		//��ά����Ƚϲ�����Arrays.equals()
		if(Arrays.deepEquals(tempCardsMap1, tempCardsMap2)){
			
			return false;
		}else{
			
			return true;
		}
		
	}
	
	//ȡ�õ�ǰ��Ƭ�����ֵ
	public int getMaxCardValue() {
		return maxCardValue;
	}
	
}
