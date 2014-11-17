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

	//���캯��
	public MainActivity(){
		mainActivity = this;
	}

	//����
	private int score = 0;
	public static final String SP_KEY_BEST_SCORE = "bestScore";
	
	private TextView tvScore, tvBestScore;
	private Button btnNewGame, btnPrevious;
	private GameView gameView;
	
	//����һ��MainActivity���͵ľ�̬����
	private static MainActivity mainActivity = null;
	
	//mainActivity get����
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
				
				new AlertDialog.Builder(getMainActivity()).setTitle("���").setMessage("���¿�ʼ��Ϸ��").setPositiveButton("����", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {

						gameView.startGame();
												
					}
				}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				} ).show();
				
			}
		});	
		
		btnPrevious.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//���Ի���
				if(gameView.isLoadPreviousCardsMap()){
					
					//ȡ�ÿ�Ƭ�����ֵ
					int maxCardValue = gameView.getMaxCardValue();

					new AlertDialog.Builder(getMainActivity()).setTitle("����").setMessage("���۳����ֵ��������"+ maxCardValue*2 +"��\nȷ�����ˣ�").setPositiveButton("����", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							gameView.loadPreviousCardsMap();
							
						}
					}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					}).show();
					
				}else{
					
					Toast.makeText(getMainActivity(), "�޷��˻���һ��", 500).show();
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
	
	//������0
	public void clearScore(){
		score = 0;
		showScore();
	}
	
	//���ӼƷ�
	public void addScore(int s){
		score += s;
		showScore();
		
		//���ӼƷֵ�ͬʱ�Ƚ���óɼ�
		int maxScore = Math.max(score, getBestScore());
		saveBestScore(maxScore);
		showBestScore(maxScore);
	}
	
	//��ȡ��ǰscore
	public int getScore() {
		return score;
	}
	
	//��TextView�ؼ�����ʾ����
	public void showScore(){
		tvScore.setText(score + "");
	}
	
	//������óɼ�
	public void saveBestScore(int s){
		Editor e = getPreferences(MODE_PRIVATE).edit();
		e.putInt(SP_KEY_BEST_SCORE, s);
		e.commit();
	}
	
	//ȡ����óɼ�
	public int getBestScore(){
		return getPreferences(MODE_PRIVATE).getInt(SP_KEY_BEST_SCORE, 0);
	}
	
	//��ʾ��óɼ�
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
