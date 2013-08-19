package com.besta.app.exerciseengine.quesprocess;

import org.json.JSONObject;

import com.besta.app.exerciseengine.ExerciseMain;
import com.besta.app.exerciseengine.quesprocess.BaseQuestion.AnswerBar;
import com.besta.app.exerciseengine.quesprocess.BaseQuestion.ScoreBar;

public class ApplicationQuestion extends BaseQuestion{

	public ApplicationQuestion(JSONObject arg0, String arg1, int arg2,
			int arg3, boolean arg4) {
		super(arg0, arg1, arg2, arg3, arg4);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getQuestion() {
		// TODO Auto-generated method stub
    	String QuesBuffer = new String();
    	String AnsPaper = new String();
    	final String ID = HtmlSign.QUES_LOCAL_ID(QuestionID,child_count);
		final String Ans = getObjString(jsonObject,ObjName.ANSWER);
		final String Que = getObjString(jsonObject,ObjName.QUESTION);
		final String Exp = getObjString(jsonObject,ObjName.RESOLVING);
		final String AnsID = "ans-"+ID;
		final String BtnID = "btn-"+ID;
		final String FavorID = "favor-"+ID;
		final String ScoreID = "score-"+ID;
		final int TotalScore = ExerciseMain.questionArray.getTotalScorebyId(QuestionID);;
		final int DefaultScore = 0;		
		final boolean isEndQues = jsonObject.isNull("SubJSONArray");
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
		QuesBuffer = QuesBuffer+(Que);
		if(isEndQues)
		{
			AnsPaper = ExerciseMain.ANSPAPER_TYPE_ANSWERNORMAL+"&ID="+QuestionID+"&NUM="+child_count;	
			QuesBuffer = QuesBuffer+(HtmlSign.ANS_START);
			String iconPath = "";
			if(!ExerciseMain.Action.equals(ExerciseMain.ACTION_EXAM))
			{
				iconPath = assetPath+"ES/btn_resolve_open_default.png";
				QuesBuffer = QuesBuffer+("<input class=\"btn\" type=\"image\" id=\""+BtnID+"\" src=\""+iconPath+"\"height=\""+ANS_IMG_HEIGHT+"px\" vspace=\"1\" align=\"absmiddle\" onclick=\"displayhidden(\'"+AnsID+"\','"+BtnID+"');\">");
			}
			iconPath = assetPath+"ES/btn_favorite_off_default.png";
			QuesBuffer = QuesBuffer+("<input class=\"btn\" type=\"image\" id=\""+FavorID+"\" src=\""+iconPath+"\"height=\""+ANS_IMG_HEIGHT+"px\" checked=\"1\" vspace=\"1\" align=\"absmiddle\" onclick=\"favoriteClick(\'"+FavorID+"');\">");
			iconPath = assetPath+"ES/btn_calculuspaper_default.png";
//				QuesBuffer = QuesBuffer+("<a href=\"anspaper:"+ExerciseActivity.ANSPAPER_TYPE_TESTDLG+"\"><input type=\"image\" id=\""+0+"\" src=\""+iconPath+"\"height=\""+ANS_IMG_HEIGHT+"px\" vspace=\"1\" align=\"absmiddle\"></a>");
			QuesBuffer = QuesBuffer+("<a href=\"anspaper:"+AnsPaper+"\"><input class=\"btn\" type=\"image\" id=\"\" src=\""+iconPath+"\"height=\""+ANS_IMG_HEIGHT+"px\" vspace=\"1\" align=\"absmiddle\"></a>");

			
			QuesBuffer = QuesBuffer+("<a id=\""+HtmlSign.QUES_LOCAL_ID(QuestionID,child_count)+"\"href=\"anspaper:"+AnsPaper+"\"></a>");
			QuesBuffer = QuesBuffer+(new AnswerBar(AnsID, Ans, Exp).getStringBar());
			QuesBuffer = QuesBuffer+(HtmlSign.ANS_END);
		}
		if(ScoreFlag)
		{
    		QuesBuffer = QuesBuffer+(new ScoreBar(ScoreID,TotalScore,DefaultScore).getStringBar());
    		QuesBuffer = QuesBuffer+("</li>");
    	}
    	else
    	{
    		QuesBuffer = QuesBuffer+("</p>");
    	}
    	QuesBuffer = QuesBuffer+(HtmlSign.QUES_END());
    	
    	return  signReplace(QuesBuffer);
	}
}
