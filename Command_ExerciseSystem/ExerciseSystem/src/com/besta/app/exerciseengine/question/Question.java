package com.besta.app.exerciseengine.question;

import org.json.JSONObject;

public class Question {
	
	public String ID;
	public String Subject;
	public String QType;
	public String SType;
	public String Knowledge;
	public String Background;
	public String Question;
	public String Selections[];
	public String Resolving;
	public String Answer;
	public String Level;
	public String Unit;
	
	public String RecommendedGrade;
	public String Frequency;
	public String VerUnit;
	public String MockID;
	public String BookNameID;
	
	public String ImageString;
	public String Result = "null";
	
	public int TotalScore = 0;
	public int UserScore = 0;
	
	public int History = 0;
	public boolean BASETEST_TAG = false;
	
	public Question() {
		// TODO Auto-generated constructor stub
	}
	public Question(JSONObject jsonObject) {
		// TODO Auto-generated constructor stub
//		final boolean isEndQues = jsonObject.isNull("SubJSONArray");
//		final String Ans = getObjString(jsonObject,ObjName.ANSWER);
//		final String Que = getObjString(jsonObject,ObjName.QUESTION);
//		final String Exp = getObjString(jsonObject,ObjName.RESOLVING);
	}
	

//	ID|序號
//	Subject|學科
//	DisplayType|題型
//	AnswerType|類型
//	Knowledge|知識點名稱
//	Question|題目
//	Selections|選項
//	Resolving|解析
//	Answer|答案
//	Level|難易度
//	Unit|對應單元
	
	
//	"ID":"BXC2010018|20121011141141963|7089",
//	"Subject":"数学",
//	"QType":"选择题",
//	"SType":"选择题",
//	"Knowledge":"抽签方法合理吗",
//	"Question":"26352<\/IMG>",
//	"Explain":"根据概率定义，从5张卡片中任取一张，共有5种不同的取法，抽出的卡片是吉祥物的有2种情况，所以抽出的卡片正面图案恰好是吉祥物（福娃）的概率是2/5，故应选B。",
//	"Answer":"B",
//	"Level":"C",
//	"RecommendedGrade":"4",
//	"Frequency":"4",
//	"VerUnit":"SJ09B0900",
//	"MockID":"SJ98",
//	"BookNameID":"SJ_CZSX_09B"

	
}
