package com.legendleo.game2048;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class Card extends FrameLayout {

	public Card(Context context) {
		super(context);

		//填满整个父级容器
		LayoutParams lp = new LayoutParams(-1,-1);
		//设置布局间隔
		lp.setMargins(10, 10, 0, 0);
		
		//label方块的背景色，避免在添加label动画效果时出现背景为空的问题
		background = new View(getContext());
		background.setBackgroundResource(R.drawable.bgc);
		addView(background, lp);
		
		label = new TextView(getContext());

		//设置数字字体大小
		label.setTextSize(32);
		
		//设置label居中
		label.setGravity(Gravity.CENTER);
		
		addView(label, lp);
		
		setNum(0);
	}
	
	
	//与Card绑定的num
	private int num = 0;
	
	public int getNum() {
		return num;
	}
	
	public void setNum(int num) {
		this.num = num;

		//给label填充值
		if(num <= 0){
			label.setText("");
		}else{
			label.setText(num + "");
		}
		
		//设置不同数字颜色背景色
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
		
		//当label的数字增加到4位数时，字体应变小否则容不下
		if(String.valueOf(num).length() < 4){
			label.setTextSize(32);
		}else if(String.valueOf(num).length() == 4){
			label.setTextSize(24);
		}else{
			label.setTextSize(20);
		}
		
	}

	//判断两张卡片是否相同
	public boolean equals(Card o) {
		return getNum() == o.getNum();
	}
	
	//取label做动画效果
	public TextView getLabel(){
		return label;
	}
	
	private TextView label;
	private View background;
}
