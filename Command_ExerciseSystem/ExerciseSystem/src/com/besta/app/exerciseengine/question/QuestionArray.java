package com.besta.app.exerciseengine.question;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class QuestionArray {
	
	ArrayList<Question> questionArray;
	public QuestionArray() {
		// TODO Auto-generated constructor stub
		questionArray = new ArrayList<Question>();
	}
	public void AddQuestion(Question question)
	{
		questionArray.add(question);
	}
	public int getUserScorebyId(String ID)
	{
		int ret = -1;
		int index = -1;
		if((index = getQuestionById(ID))>-1)
		{
			ret = questionArray.get(index).UserScore;
		}
		return ret;
	}
	public int getTotalScorebyId(String ID)
	{
		int ret = -1;
		int index = -1;
		if((index = getQuestionById(ID))>-1)
		{
			ret = questionArray.get(index).TotalScore;
		}
		return ret;
	}
	public int getAllUserScore()
	{
		int ret = 0;
		for(int i = 0;i<questionArray.size();i++)
		{
			ret += questionArray.get(i).UserScore;
		}
		return ret;
	}
	public int getAllTotalScore()
	{
		int ret = 0;
		for(int i = 0;i<questionArray.size();i++)
		{
			ret += questionArray.get(i).TotalScore;
		}
		return ret;
	}
	public String getDataByJson()
	{
		String ret = "";
		try {	
			JSONArray jsonAr = new JSONArray();
			for(int i = 0;i<questionArray.size();i++)
			{
				Question temp = questionArray.get(i);
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("ID", temp.ID);
				jsonObj.put("BookNameID",temp.BookNameID);
				jsonObj.put("Level",temp.Level);
				jsonObj.put("Knowledge",temp.Knowledge);
				jsonObj.put("TotalScore",temp.TotalScore);
				jsonObj.put("UserScore",temp.UserScore);
				jsonAr.put(jsonAr.length(), jsonObj);
			}
			ret = jsonAr.toString(1);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	private int getQuestionById(String ID)
	{
		for(int i = 0;i<questionArray.size();i++)
		{
//			Question temp = questionArray.get(i);
			if(questionArray.get(i).ID.equals(ID))
			{
				return i;	
			}
		}
		return -1;
	}
	public boolean setResultbyId(String ID,String Result)
	{
		boolean ret = false;
		int index = -1;
		if((index = getQuestionById(ID))>-1)
		{
			Question temp = questionArray.get(index);
			temp.Result = Result;
			questionArray.set(index, temp);
			ret = true; 
		}
		return ret;
	}
	public boolean setUserScorebyId(String ID,int UserScore)
	{
		boolean ret = false;
		int index = -1;
		if((index = getQuestionById(ID))>-1)
		{
			Question temp = questionArray.get(index);
			temp.UserScore = UserScore;
			questionArray.set(index, temp);
			ret = true; 
		}
		return ret;
	}
	private boolean removeQuestionById(String ID)
	{
		boolean ret = false;
		for(int i = 0;i<questionArray.size();i++)
		{
			Question temp = questionArray.get(i);
			if(temp.ID.equals(ID))
			{
				questionArray.remove(i);
				ret = true;
			}
		}
		return ret;
	}
}
