package com.legendleo.game2048;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	//构造函数
	public MainActivity(){
		mainActivity = this;
	}

	//分数
	private int score = 0;
	public static final String SP_KEY_BEST_SCORE = "bestScore";
	
	private TextView tvScore, tvBestScore;
	private Button btnNewGame, btnPrevious;
	private GameView gameView;
	
	//声明一个MainActivity类型的静态变量
	private static MainActivity mainActivity = null;
	
	//mainActivity get方法
	public static MainActivity getMainActivity() {
		return mainActivity;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tvScore = (TextView) findViewById(R.id.tvScore);
		tvBestScore = (TextView) findViewById(R.id.tvBestScore);
		btnNewGame = (Button) findViewById(R.id.btnNewGame);
		gameView = (GameView) findViewById(R.id.gameView);
		btnPrevious = (Button) findViewById(R.id.btnPreviousStep);
		
		btnNewGame.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				new AlertDialog.Builder(getMainActivity()).setTitle("你好").setMessage("重新开始游戏？").setPositiveButton("重来", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {

						gameView.startGame();
												
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				} ).show();
				
			}
		});	
		
		btnPrevious.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//可以回退
				if(gameView.isLoadPreviousCardsMap()){
					
					//取得卡片中最大值
					int maxCardValue = gameView.getMaxCardValue();

					new AlertDialog.Builder(getMainActivity()).setTitle("回退").setMessage("将扣除最大值的两倍："+ maxCardValue*2 +"分\n确定回退？").setPositiveButton("回退", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							gameView.loadPreviousCardsMap();
							
						}
					}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					}).show();
					
				}else{
					
					Toast.makeText(getMainActivity(), "无法退回上一步", 500).show();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return false;
	}
	
	//分数清0
	public void clearScore(){
		score = 0;
		showScore();
	}
	
	//增加计分
	public void addScore(int s){
		score += s;
		showScore();
		
		//增加计分的同时比较最好成绩
		int maxScore = Math.max(score, getBestScore());
		saveBestScore(maxScore);
		showBestScore(maxScore);
	}
	
	//获取当前score
	public int getScore() {
		return score;
	}
	
	//在TextView控件中显示出来
	public void showScore(){
		tvScore.setText(score + "");
	}
	
	//保存最好成绩
	public void saveBestScore(int s){
		Editor e = getPreferences(MODE_PRIVATE).edit();
		e.putInt(SP_KEY_BEST_SCORE, s);
		e.commit();
	}
	
	//取得最好成绩
	public int getBestScore(){
		return getPreferences(MODE_PRIVATE).getInt(SP_KEY_BEST_SCORE, 0);
	}
	
	//显示最好成绩
	public void showBestScore(int s){
		tvBestScore.setText(s + "");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		gameView.saveCardsMap();
	}
	
}
