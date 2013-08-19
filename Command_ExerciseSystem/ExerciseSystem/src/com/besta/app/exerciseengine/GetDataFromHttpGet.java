package com.besta.app.exerciseengine;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;

import com.besta.app.exerciseengine.quesprocess.ApplicationQuestion;
import com.besta.app.exerciseengine.quesprocess.BaseQuestion.ObjName;
import com.besta.app.exerciseengine.quesprocess.BlanksQuestion;
import com.besta.app.exerciseengine.quesprocess.ChoiceQuestion;
import com.besta.app.exerciseengine.quesprocess.HtmlSign;
import com.besta.app.exerciseengine.question.Question;

public class GetDataFromHttpGet {
	
	public StringBuffer QuesBuffer = new StringBuffer();
	public StringBuffer AnsBuffer = new StringBuffer();
	
	
//	public static class ObjName{
//		public static String UNITE				="UnitE";
//		public static String ID					="ID";
//		public static String SUBJECT			="Subject";
//		public static String TITLE_TYPE			="QType";
//		public static String DISPLAY_TYPE		="SType";
//		public static String KNOWLEDGE			="Knowledge";
//		public static String QUESTION			="Question";
//		public static String SELECTION[]		={"SelectionA","SelectionB","SelectionC","SelectionD","SelectionE"};
//		public static String RESOLVING			="Resolving";
//		public static String ANSWER				="Answer";
//		public static String LEVEL				="Level";
//		public static String UNIT				="Unit";
//		public static String KNOWLEDGE_CODE		="KnowledgeCode";
//		public static String KEYWORD			="Keyword";
//	}
	private static class DisplayType{
		public static String SELECT				="选择题";
		public static String BLANK				="填空题";
		public static String COMPOSITE			="应用题";
		public static String OTHER				="其他";
	}
//	public static class HtmlSign{
//		public static String IMG_START = "<IMG>";
//		public static String IMG_END = "</IMG>";
//		public static String BLANK = "<BLANK>";//<BLANK> <BRACKET>
//		public static String BRACKET = "<BRACKET>";
//		public static String NEWLINE = "<NEWLINE>";
////		public static String QUES_START = "<QUES>";
////		public static String QUES_END = "</QUES>";
//		public static String ANS_START = "<ANS>";
//		public static String ANS_END = "</ANS>";
//		static public String QUES_START(String Id,int Num)
//		{
//			return "<QUES id="+Id+"num="+Num+">";
//		}
//		static public String QUES_END()
//		{
//			return "</QUES>";
//		}
//		static public String ANS_DISPLAY_START(String Id,int Num)
//		{
////			<div id=\""+formularIdtemp+"\" style=\"display:none\"></div>
//			String id = QUES_LOCAL_ID(Id,Num);
//			return "<div id=\""+id+"\" style=\"display:none\">";
//		}
//
//		static public String ANS_DISPLAY_END()
//		{
//			return "</div>";
//		}
//		
//		static public String ANS_DISPLAY_BLANK_START(String Id,int Num)
//		{
////			<div id=\""+formularIdtemp+"\" style=\"display:none\"></div>
//			String id = QUES_LOCAL_ID(Id,Num);
//			return "<a id=\""+id+"\" style=\"display:none\">";
//		}
//		static public String ANS_DISPLAY_BLANK_END()
//		{
//			return "</a>";
//		}
//		
//		static public String QUES_LOCAL_ID(String Id,int Num)
//		{
//			return Id+"|"+Num;
//		}
//	}
//	單元|UnitE -> Unit
//	序號|ID
//	學科|Subject
//	題型|TitleTypeQType
//	類型|DisplayTypeSType
//	知識點名稱|KnowledgeKonwledgeName
//	題目|Question
//	選項A|SelectionA
//	選項B|SelectionB
//	選項C|SelectionC
//	選項D|SelectionD
//	解析|ResolvingExplain
//	答案|Answer
//	難易度|Level
//	對應單元|Unit –>VersionUnit
//	知識點編碼|KnowledgeCodeKnowledgeID
//	關鍵字|Keyword
	public static final int MAXSELECTIONNUM  = 4;//
	public static final int DEFAULTFONTSIZE  = 16;//
	public static final int ANS_IMG_HEIGHT = 24;//ans button heigth	(20,20)(16,18)
	public static final int LINK_IMG_HEIGHT = 18;//knowledge link button heigth	(20,20)(16,18)
	public static final int SEL_IMG_HEIGHT = 16;//select button heigth	(20,19)(16,16)
	public static final int REALTEACH_IMG_HEIGHT = 18;//real teach link button heigth	(20,20)(16,18)

	public static final int ANS_IMG_WIDTH = 95*ANS_IMG_HEIGHT/32;//ans button width
	public static final int SEL_IMG_WIDTH = 53*SEL_IMG_HEIGHT/38;//select button width
	public static final int LINK_IMG_WIDTH = 84*LINK_IMG_HEIGHT/32;//ans button width
	public static final int REALTEACH_IMG_WIDTH = 104*REALTEACH_IMG_HEIGHT/32;//real teach button width
	
	private String assetPath = "file:///android_asset/";
	private boolean flag_Li = false;
	private static final String BaseUrl = "http://10.180.6.46:8080";
	private Context context;
//	private int count = 0;
	private int child_count = 0;
	private String G_QuestionID = "";
	private String ScoreTemp[];
	int ScoreCount = 0;
//	private String G_count = "count";
	public GetDataFromHttpGet(Context arg1)
	{
		context = arg1;
	}
	public void getQuestion(String QuestionID,int TitleExamId)//For the new sever API
	{
                String JSON = "";
                try{
                    String url = BaseUrl+"/BestaExam/services";
                    String paramStr = "";
                    Map params = new HashMap();
                    params.put("action", URLEncoder.encode("getTitle","utf-8") );
                    params.put("titleId", URLEncoder.encode(QuestionID,"utf-8"));
                    Iterator iter = params.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        Object key = entry.getKey();
                        Object val = entry.getValue();               
                        paramStr += paramStr = "&" + key + "=" + val;
                    }
                    if (!paramStr.equals("")) {
                        paramStr = paramStr.replaceFirst("&", "?");
                        url += paramStr;
                    }
                    HttpClient client = new DefaultHttpClient();   
                    HttpGet request = new HttpGet(url);
                    request.addHeader("charset", HTTP.UTF_8);       
                    HttpResponse response = client.execute(request);     
                    BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));           
                    StringBuffer sb = new StringBuffer("");       
                    String line = "";
                    String NL = System.getProperty("line.separator");     
                    while((line = in.readLine()) != null){
                            sb.append(line + NL);
                    }
	                in.close();  
	                JSON = sb.toString();
//	                System.out.println("[Out JSON]"+JSON);
//	    			flag_Li = false;
//	    	    	flag_Li = true;
	                child_count = 0;
	                G_QuestionID = QuestionID;
	                
	                
	                
//	                ExerciseMain.AnswerJsonArray = ;
//	                JSONObject jsonObj = new JSONObject();
//	    			jsonObj.put("ID",QuestionID);
//	    			jsonObj.put("Answer", "");
//	    			
//	    			ExerciseMain.AnswerJsonArray.put(ExerciseMain.AnswerJsonArray.length(),jsonObj);
//	    			ExerciseMain.AnswerJsonArray.get
	    			
	    			
	    			
	    			resolvingQuestion(JSON,TitleExamId,true);
                }catch(Exception e){
                	e.printStackTrace();
	            }
	}
	public void getExamPaper(String ExamValue)
	{
                String JSON = "";
                try{     
                	String url = BaseUrl+"/BestaExam/services";
                    String paramStr = "";
                    Map params = new HashMap();
                    params.put("action", URLEncoder.encode("getExamPaper","utf-8"));
                    params.put("examValue", URLEncoder.encode(ExamValue,"utf-8"));
//                    params.put("examValue",ExamValue);
                    Iterator iter = params.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        Object key = entry.getKey();
                        Object val = entry.getValue();
                        paramStr += paramStr = "&" + key + "=" + val;
                    }
                    if (!paramStr.equals("")) {
                        paramStr = paramStr.replaceFirst("&", "?");
                        url += paramStr;
                    }
                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet(url);
                    request.addHeader("charset", HTTP.UTF_8);       
                    HttpResponse response = client.execute(request);     
                    BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));           
                    StringBuffer sb = new StringBuffer("");       
                    String line = "";
                    String NL = System.getProperty("line.separator");     
                    while((line = in.readLine()) != null){
                            sb.append(line + NL);
                    }
	                in.close();
	                JSON = sb.toString();
	                System.out.println("[Out JSON]"+JSON);
	                
//	                flag_UL = false;
	    			JSONTokener jsonParser = new JSONTokener(JSON);
	    			JSONObject jsonObject = (JSONObject)jsonParser.nextValue();
	    			
	                resolvingExamPaper(jsonObject);
                }catch(Exception e){
                	e.printStackTrace();
	            }
	}
	private String getImageByBase64(String ImageID)
	{
	        String ImageBase64 = "";
	        try{     
	            String url = BaseUrl+"/BestaExam/services";
	            String paramStr = "";
	            Map params = new HashMap();
	            params.put("action", URLEncoder.encode("getImage","utf-8") );
	            params.put("imageId", URLEncoder.encode(ImageID,"utf-8"));
	            Iterator iter = params.entrySet().iterator();
	            while (iter.hasNext()) {
	                Map.Entry entry = (Map.Entry) iter.next();
	                Object key = entry.getKey();
	                Object val = entry.getValue();               
	                paramStr += paramStr = "&" + key + "=" + val;
	            }
	            if (!paramStr.equals("")) {
	                paramStr = paramStr.replaceFirst("&", "?");
	                url += paramStr;
	            }
	            HttpClient client = new DefaultHttpClient();   
	            HttpGet request = new HttpGet(url);
	            request.addHeader("charset", HTTP.UTF_8);       
	            HttpResponse response = client.execute(request);     
	            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));           
	            StringBuffer sb = new StringBuffer("");       
	            String line = "";
	            String NL = System.getProperty("line.separator");     
	            while((line = in.readLine()) != null){
	                    sb.append(line + NL);
	            }
	            in.close();  
	            ImageBase64 = sb.toString();
	        }catch(Exception e){
	        	e.printStackTrace();
	        }
	    	return ImageBase64;
	}
    private String resolvingExamPaper(JSONObject jsonObject)
    {
//    	if(ScoreTemp!=null&&!(ScoreTemp.length<count))
//    	{
//    		Score = Integer.valueOf(ScoreTemp[count]);
//    	}
		try {
//			JSONTokener jsonParser = new JSONTokener(JSON);
//			JSONObject jsonObject = (JSONObject)jsonParser.nextValue();
			if(!jsonObject.isNull("ExamName"))
			{
				QuesBuffer = QuesBuffer.append("<center class=\"examname\">"+jsonObject.getString("ExamName")+"</center>");		
			}
			if(!jsonObject.isNull("BlockName"))
			{
				QuesBuffer = QuesBuffer.append("<p class=\"title\">"+jsonObject.getString("BlockName")+"</p>");			
			}
			if(!jsonObject.isNull("TitleScore"))
			{
				final int TotalNum = Integer.valueOf(jsonObject.getString("TotalNum"));
				final String TitleScore = jsonObject.getString("TitleScore");
				if(TitleScore.contains("|"))
				{
					ScoreTemp = getMidStringAll("|"+TitleScore+"|","|","|");
				}
				else
				{
					ScoreTemp = new String[TotalNum];
					for(int i = 0;i<ScoreTemp.length;i++)
					{
						ScoreTemp[i] = TitleScore;
					}
				}
			}
			if(!jsonObject.isNull("TitleSerialNum"))
			{
				int TitleExamId = 0;
				if(!jsonObject.isNull("TitleExamId"))
				{
					TitleExamId = jsonObject.getInt("TitleExamId");
				}				
				getQuestion(jsonObject.getString("TitleSerialNum"),TitleExamId);
				ScoreCount++;
			}
			if(!jsonObject.isNull("SubJSONArray"))
			{
				ScoreCount = 0;
				JSONArray jsonNArray_i = new JSONArray(jsonObject.getString("SubJSONArray"));
				for(int i = 0;i<jsonNArray_i.length();i++)
				{
					JSONObject jsonObjectTemp_i = (JSONObject) jsonNArray_i.get(i);
					resolvingExamPaper(jsonObjectTemp_i);
				}
			}
//			System.out.println("[ExamPaper]"+jsonObject.getString("ExamName"));			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return "";
    }
    private String resolvingQuestion(String JSON,int TitleExamId,boolean flag_Li)
    {
		try {
			JSONTokener jsonParser = new JSONTokener(JSON);
			JSONObject jsonObject = (JSONObject)jsonParser.nextValue();
//			System.out.println("[JSON]"+jsonObject.getString("Question"));
			QuesBuffer = QuesBuffer.append(createHtml(jsonObject,TitleExamId,flag_Li));
						
			if(!jsonObject.isNull("SubJSONArray"))
			{
				JSONArray jsonNArray_i = new JSONArray(jsonObject.getString("SubJSONArray"));
				for(int i = 0;i<jsonNArray_i.length();i++)
				{
					JSONObject jsonObjectTemp_i = (JSONObject) jsonNArray_i.get(i);
					resolvingQuestion(jsonObjectTemp_i.toString(),TitleExamId,false);
				}
//				QuesBuffer = QuesBuffer.append("<br>END");
			}			
//			System.out.println("[ExamPaper]"+jsonObject.getString("ExamName"));			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return "";
    }
    private String createHtml(JSONObject jsonObject,int TitleExamId,boolean flag_Li)
    {
    	child_count++;
    	StringBuffer QuesBuffer = new StringBuffer();
//    	QuesBuffer = QuesBuffer.append(HtmlSign.QUES_START(G_QuestionID,child_count));
//    	if(flag_Li)
//    	{
//    		if(TitleExamId!=0)
//    		{
//    			QuesBuffer = QuesBuffer.append("<li start="+TitleExamId+">");
//    		}
//    		else
//    		{
//    			QuesBuffer = QuesBuffer.append("<li>");
//    		}
//    	}
//    	else
//    	{
//    		QuesBuffer = QuesBuffer.append("<p class=\"subli\">");
//    	}
    	try {
    		if(!jsonObject.isNull(ObjName.DISPLAY_TYPE))
    		{
	    		String type = jsonObject.getString(ObjName.DISPLAY_TYPE);
	    		if(type.equals(DisplayType.SELECT))
	    		{
	    			ChoiceQuestion myChoiceQuestion = new ChoiceQuestion(jsonObject, G_QuestionID, child_count,TitleExamId,flag_Li);
	    			if(!myChoiceQuestion.getStringFromJSON(ObjName.ID).equals(""))
	    			{
	    				Question question = new Question(); 			
		    			question.ID 		= myChoiceQuestion.getStringFromJSON(ObjName.ID);
		    			question.Knowledge 	= myChoiceQuestion.getStringFromJSON(ObjName.KNOWLEDGE);
		    			question.BookNameID = myChoiceQuestion.getStringFromJSON(ObjName.BOOKNAMEID);
		    			question.Subject 	= myChoiceQuestion.getStringFromJSON(ObjName.SUBJECT);
		    			question.Level 		= myChoiceQuestion.getStringFromJSON(ObjName.LEVEL);
		    			if(ScoreTemp!=null)
		    			{
		    				question.TotalScore	= Integer.valueOf(ScoreTemp[ScoreCount]);
		    			}
		    			ExerciseMain.questionArray.AddQuestion(question);
	    			}
	    			myChoiceQuestion.setAnsString("答案");
	    			QuesBuffer = QuesBuffer.append(myChoiceQuestion.getQuestion());
	    		}
	    		else if(type.equals(DisplayType.BLANK))
	    		{
	    			BlanksQuestion myBlanksQuestion = new BlanksQuestion(jsonObject, G_QuestionID, child_count,TitleExamId,flag_Li);
	    			if(!myBlanksQuestion.getStringFromJSON(ObjName.ID).equals(""))
	    			{
	    				Question question = new Question();
		    			question.ID 		= myBlanksQuestion.getStringFromJSON(ObjName.ID);
		    			question.Knowledge 	= myBlanksQuestion.getStringFromJSON(ObjName.KNOWLEDGE);
		    			question.BookNameID = myBlanksQuestion.getStringFromJSON(ObjName.BOOKNAMEID);
		    			question.Subject 	= myBlanksQuestion.getStringFromJSON(ObjName.SUBJECT);
		    			question.Level 		= myBlanksQuestion.getStringFromJSON(ObjName.LEVEL);
		    			if(ScoreTemp!=null)
		    			{
		    				question.TotalScore	= Integer.valueOf(ScoreTemp[ScoreCount]);
		    			}
		    			ExerciseMain.questionArray.AddQuestion(question);	
	    			}
	    			myBlanksQuestion.setAnsString("答案");
	    			QuesBuffer = QuesBuffer.append(myBlanksQuestion.getQuestion());
	    		}
	    		else if(type.equals(DisplayType.COMPOSITE))
	    		{
	    			ApplicationQuestion myApplicationQuestion = new ApplicationQuestion(jsonObject, G_QuestionID, child_count,TitleExamId,flag_Li);
	    			if(!myApplicationQuestion.getStringFromJSON(ObjName.ID).equals(""))
	    			{
	    				Question question = new Question();
		    			question.ID 		= myApplicationQuestion.getStringFromJSON(ObjName.ID);
		    			question.Knowledge 	= myApplicationQuestion.getStringFromJSON(ObjName.KNOWLEDGE);
		    			question.BookNameID = myApplicationQuestion.getStringFromJSON(ObjName.BOOKNAMEID);
		    			question.Subject 	= myApplicationQuestion.getStringFromJSON(ObjName.SUBJECT);
		    			question.Level 		= myApplicationQuestion.getStringFromJSON(ObjName.LEVEL);
		    			if(ScoreTemp!=null)
		    			{
		    				question.TotalScore	= Integer.valueOf(ScoreTemp[ScoreCount]);
		    			}
		    			ExerciseMain.questionArray.AddQuestion(question);	
	    			}
	    			myApplicationQuestion.setAnsString("答案");
	    			QuesBuffer = QuesBuffer.append(myApplicationQuestion.getQuestion());
	    		}
    		}
    		else
    		{
    			ApplicationQuestion myApplicationQuestion = new ApplicationQuestion(jsonObject, G_QuestionID, child_count,TitleExamId,flag_Li);
    			if(!myApplicationQuestion.getStringFromJSON(ObjName.ID).equals(""))
    			{
    				Question question = new Question();
	    			question.ID 		= myApplicationQuestion.getStringFromJSON(ObjName.ID);
	    			question.Knowledge 	= myApplicationQuestion.getStringFromJSON(ObjName.KNOWLEDGE);
	    			question.BookNameID = myApplicationQuestion.getStringFromJSON(ObjName.BOOKNAMEID);
	    			question.Subject 	= myApplicationQuestion.getStringFromJSON(ObjName.SUBJECT);
	    			question.Level 		= myApplicationQuestion.getStringFromJSON(ObjName.LEVEL);
	    			if(ScoreTemp!=null)
	    			{
	    				question.TotalScore	= Integer.valueOf(ScoreTemp[ScoreCount]);
	    			}
	    			ExerciseMain.questionArray.AddQuestion(question);	
    			}
    			myApplicationQuestion.setAnsString("答案");
    			QuesBuffer = QuesBuffer.append(myApplicationQuestion.getQuestion());
    		}
    	} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//    	if(flag_Li)
//    	{
//    		QuesBuffer = QuesBuffer.append("</li>");
//    	}
//    	else
//    	{
//    		QuesBuffer = QuesBuffer.append("</p>");
//    	}
//    	QuesBuffer = QuesBuffer.append(HtmlSign.QUES_END());
    	return QuesBuffer.toString();
    }
	public String[] getMidStringAll(String initiativeStr, String beginStr,
			String endStr)// get the mid String between beginStr and endStr
	{
		ArrayList<String> array = new ArrayList<String>();
		int begin = 0;
		int end = 0;
		@SuppressWarnings("unused")
		int imgCount = 0;

		int beginStrLength = beginStr.length();
		while (begin != -1) {
			begin = initiativeStr.indexOf(beginStr, end);
			end = initiativeStr.indexOf(endStr, begin + beginStrLength);
			if (begin < end && begin != -1 && end != -1) {
				array.add(initiativeStr.substring(begin + beginStrLength, end));
			} else {
				break;
			}
			imgCount++;
		}
		return array.toArray(new String[0]);
	}
}
