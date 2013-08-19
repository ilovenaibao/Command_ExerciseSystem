package com.besta.app.exerciseengine;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class CountDialog extends Dialog{

	public CountDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	public static class Builder {
		 private Context context;
		 private View.OnClickListener analysisButtonClickListener = null;
		 private int Score_S = 0;//个位
		 private int Score_T = 0;//十位
		 private int Score_H = 0;//百位
		 public Builder(Context context)
		 {
			this.context = context; 
		 }
		 public void setScore(int Score)
		 {
			if(Score<1000&&Score>-1)
			{
				Score_H = Score/100;
				Score = Score%100;
				Score_T = Score/10;
				Score = Score%10;
				Score_S = Score;
			}
		 }
		 public Builder setAnalysisButton(View.OnClickListener listener) 
		 {
//			 this.positiveButtonText = (String) context.getText(positiveButtonText);
			 this.analysisButtonClickListener = listener;
			 return this;
		 }

		 
		 public CountDialog create(){
			 
			final CountDialog myCountDialog = new CountDialog(context,R.style.FullHeightDialog);
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.blg_score_count, null);
			ImageView Sdigit = (ImageView)layout.findViewById(R.id.s_score);
			ImageView Tdigit = (ImageView)layout.findViewById(R.id.t_score);
			ImageView Hdigit = (ImageView)layout.findViewById(R.id.h_score);
			Button AnalysisButton = (Button)layout.findViewById(R.id.btn_analysis);
			
			Sdigit.setImageResource(setScoreImage(Score_S));
			Tdigit.setImageResource(setScoreImage(Score_T));
			Hdigit.setImageResource(setScoreImage(Score_H));
			boolean ZeroFlag = false;
			if(Score_H==0)
			{
				ZeroFlag = true;
				Hdigit.setVisibility(View.GONE);
			}
			else
			{
				ZeroFlag = false;
			}
			if(ZeroFlag && Score_T==0)
			{
				ZeroFlag = true;
				Tdigit.setVisibility(View.GONE);
			}
			else
			{
				ZeroFlag = false;
			}
			if(analysisButtonClickListener!=null)
			{
				AnalysisButton.setOnClickListener(analysisButtonClickListener);
			}
			
			myCountDialog.setContentView(layout);
			
			return myCountDialog;
		 }
		 private int setScoreImage(int Score)
		 {
			 int ret = -1;
			 switch(Score)
			 {
			 	case 1:
			 		ret = R.drawable.no_1;
			 		break;
			 	case 2:
			 		ret = R.drawable.no_2;
			 		break;
			 	case 3:
			 		ret = R.drawable.no_3;
			 		break;
			 	case 4:
			 		ret = R.drawable.no_4;
			 		break;
			 	case 5:
			 		ret = R.drawable.no_5;
			 		break;
			 	case 6:
			 		ret = R.drawable.no_6;
			 		break;
			 	case 7:
			 		ret = R.drawable.no_7;
			 		break;
			 	case 8:
			 		ret = R.drawable.no_8;
			 		break;
			 	case 9:
			 		ret = R.drawable.no_9;
			 		break;
			 	case 0:
			 		ret = R.drawable.no_0;
			 		break;
			 	default:
			 		break;
			 }
			 return ret;
		 }
	}
}