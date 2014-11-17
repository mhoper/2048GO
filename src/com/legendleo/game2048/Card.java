package com.legendleo.game2048;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class Card extends FrameLayout {

	public Card(Context context) {
		super(context);

		//����������������
		LayoutParams lp = new LayoutParams(-1,-1);
		//���ò��ּ��
		lp.setMargins(10, 10, 0, 0);
		
		//label����ı���ɫ�����������label����Ч��ʱ���ֱ���Ϊ�յ�����
		background = new View(getContext());
		background.setBackgroundResource(R.drawable.bgc);
		addView(background, lp);
		
		label = new TextView(getContext());

		//�������������С
		label.setTextSize(32);
		
		//����label����
		label.setGravity(Gravity.CENTER);
		
		addView(label, lp);
		
		setNum(0);
	}
	
	
	//��Card�󶨵�num
	private int num = 0;
	
	public int getNum() {
		return num;
	}
	
	public void setNum(int num) {
		this.num = num;

		//��label���ֵ
		if(num <= 0){
			label.setText("");
		}else{
			label.setText(num + "");
		}
		
		//���ò�ͬ������ɫ����ɫ
		switch (num) {
		case 0:
			label.setBackgroundResource(R.drawable.bgc0);
			break;
		case 2:
			label.setBackgroundResource(R.drawable.bgc2);
			break;
		case 4:
			label.setBackgroundResource(R.drawable.bgc4);
			break;
		case 8:
			label.setBackgroundResource(R.drawable.bgc8);
			break;
		case 16:
			label.setBackgroundResource(R.drawable.bgc16);
			break;
		case 32:
			label.setBackgroundResource(R.drawable.bgc32);
			break;
		case 64:
			label.setBackgroundResource(R.drawable.bgc64);
			break;
		case 128:
			label.setBackgroundResource(R.drawable.bgc128);
			break;
		case 256:
			label.setBackgroundResource(R.drawable.bgc256);
			break;
		case 512:
			label.setBackgroundResource(R.drawable.bgc512);
			break;
		case 1024:
			label.setBackgroundResource(R.drawable.bgc1024);
			break;
		case 2048:
			label.setBackgroundResource(R.drawable.bgc2048);
			break;
		case 4096:
			label.setBackgroundResource(R.drawable.bgc4096);
			break;

		default:
			label.setBackgroundResource(R.drawable.bgc8192);
			break;
		}
		
		//��label���������ӵ�4λ��ʱ������Ӧ��С�����ݲ���
		if(String.valueOf(num).length() < 4){
			label.setTextSize(32);
		}else if(String.valueOf(num).length() == 4){
			label.setTextSize(24);
		}else{
			label.setTextSize(20);
		}
		
	}

	//�ж����ſ�Ƭ�Ƿ���ͬ
	public boolean equals(Card o) {
		return getNum() == o.getNum();
	}
	
	//ȡlabel������Ч��
	public TextView getLabel(){
		return label;
	}
	
	private TextView label;
	private View background;
}
