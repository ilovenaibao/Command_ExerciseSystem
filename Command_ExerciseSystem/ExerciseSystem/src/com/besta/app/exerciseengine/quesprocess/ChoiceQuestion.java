package com.besta.app.exerciseengine.quesprocess;

import org.json.JSONException;
import org.json.JSONObject;

import com.besta.app.exerciseengine.ExerciseMain;
import com.besta.app.exerciseengine.quesprocess.BaseQuestion.ScoreBar;

public class ChoiceQuestion extends BaseQuestion{

	public ChoiceQuestion(JSONObject arg0, String arg1, int arg2, int arg3,
			boolean arg4) {
		super(arg0, arg1, arg2, arg3, arg4);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getQuestion() {
		// TODO Auto-generated method stub
		String QuesBuffer = new String();
		try {
//			final String ID = String.valueOf(count);
			final String ID = HtmlSign.QUES_LOCAL_ID(QuestionID,child_count);
//			final String ID = getObjString(jsonObject,ObjName.ID);
			final String AnsID = "ans-"+ID;
			final String BtnID = "btn-"+ID;
			final String FavorID = "favor-"+ID;
			final String ScoreID = "score-"+ID;
			final String TfID = "tficon-"+ID;
			final String ChooseName = "choose-"+ID;//01,02,03.....
			
			final int TotalScore = ExerciseMain.questionArray.getTotalScorebyId(QuestionID);
			final int DefaultScore = 0;
			
			final boolean isEndQues = jsonObject.isNull("SubJSONArray");
			final String Ans = getObjString(jsonObject,ObjName.ANSWER);
			final String Que = getObjString(jsonObject,ObjName.QUESTION);
			final String Exp = getObjString(jsonObject,ObjName.RESOLVING);		
			String AnsTF = "";
			QuesBuffer = QuesBuffer+(HtmlSign.QUES_START(QuestionID,child_count));
	    	if(ScoreFlag)
	    	{
	    		if(TitleExamId!=0)
	    		{
	    			QuesBuffer = QuesBuffer+("<li start="+TitleExamId+">");
	    		}
	    		else
	    		{
	    			QuesBuffer = QuesBuffer+("<li>");
	    		}
	    	}
	    	else
	    	{
	    		QuesBuffer = QuesBuffer+("<p class=\"subli\">");
	    	}
//			QuesBuffer = QuesBuffer.append("<li>");
//			QuesBuffer = QuesBuffer+("[ID:"+G_QuestionID+"CHILD:"+child_count+"]");
			String AnsPaper = ExerciseMain.ANSPAPER_TYPE_TESTDLG+"&ID="+QuestionID+"&NUM=1";		
//			sign_right.png
			String iconPath = "";
			iconPath = assetPath+"ES/sign_wrong.png";
			QuesBuffer = QuesBuffer+("<div style=\"position:absolute; margin-left:30px\"><img id=\""+TfID+"\" name=\"scorebar\" style=\"display:none\" src=\""+iconPath+"\"></div>");
			
			
			QuesBuffer = QuesBuffer+(Que);

			if(!ExerciseMain.Action.equals(ExerciseMain.ACTION_EXAM))
			{
				iconPath = assetPath+"ES/btn_resolve_open_default.png";
				QuesBuffer = QuesBuffer+("<input class=\"btn\" type=\"image\" id=\""+BtnID+"\" src=\""+iconPath+"\"height=\""+ANS_IMG_HEIGHT+"px\" vspace=\"1\" align=\"absmiddle\" onclick=\"displayhidden(\'"+AnsID+"\','"+BtnID+"');\">");
			}
			iconPath = assetPath+"ES/btn_favorite_off_default.png";
			QuesBuffer = QuesBuffer+("<input class=\"btn\" type=\"image\" id=\""+FavorID+"\" src=\""+iconPath+"\"height=\""+ANS_IMG_HEIGHT+"px\" checked=\"1\" vspace=\"1\" align=\"absmiddle\" onclick=\"favoriteClick(\'"+FavorID+"');\">");
			iconPath = assetPath+"ES/btn_calculuspaper_default.png";
			QuesBuffer = QuesBuffer+("<a href=\"anspaper:"+AnsPaper+"\"><input class=\"btn\" type=\"image\" id=\""+0+"\" src=\""+iconPath+"\"height=\""+ANS_IMG_HEIGHT+"px\" vspace=\"1\" align=\"absmiddle\"></a>");


			
			int correctAnsNum = 0;

			for(int i = 0;i<ObjName.SELECTION.length && !jsonObject.isNull(ObjName.SELECTION[i]);i++)
			{	
				String Selection = jsonObject.getString(ObjName.SELECTION[i]);
				final String ChooseId = ChooseName+"-"+i;//0-0,0-1.....
				
//					iconPath = assetPath+AnsIconPath[i];
//				String ans = String.valueOf('A'+i);

				if(Ans.contains(Character.toString((char)('A'+i))))
				{
					AnsTF = "1";
				}
				else
				{
					AnsTF = "0";
				}
				char selectChar = (char)('A'+i);
//				QuesBuffer = QuesBuffer+("<br><input type=\"radio\" id=\""+ID+"\" name=\""+ID+"\" value=\""+AnsTF+"\" vspace=\"1\" align=\"absmiddle\">");
				QuesBuffer = QuesBuffer+("<br><input type=\"radio\" id=\""+ChooseId+"\" name=\""+ChooseName+"\" value=\""+AnsTF+"\"src=\""+iconPath+"\"width=\""+SEL_IMG_WIDTH+"px\"height=\""+SEL_IMG_HEIGHT+"px\" vspace=\"1\" align=\"absmiddle\"onclick=\"changebutton(\'"+ChooseName+"\',"+i+");\">");
//				QuesBuffer = QuesBuffer+(selectChar+".   "+Selection);
				QuesBuffer = QuesBuffer+(Selection);
//				QuesBuffer = QuesBuffer+("</input>");
			}
			QuesBuffer = QuesBuffer+(new ScoreBar(ScoreID,TotalScore,DefaultScore).getStringBarWithResult());
			QuesBuffer = QuesBuffer+(new AnswerBar(AnsID, Ans, Exp).getStringBar());
			if(ScoreFlag)
	    	{
//	    		QuesBuffer = QuesBuffer+(new ScoreBar(ScoreID,TotalScore,DefaultScore).getStringBar());
	    		QuesBuffer = QuesBuffer+("</li>");
	    	}
	    	else
	    	{
	    		QuesBuffer = QuesBuffer+("</p>");
	    	}
	    	QuesBuffer = QuesBuffer+(HtmlSign.QUES_END());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//				QuesBuffer = QuesBuffer+("</li>");
		return  signReplace(QuesBuffer);
	}

	

}
