package com.besta.app.exerciseengine.quesprocess;

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
import org.json.JSONException;
import org.json.JSONObject;

public class BaseQuestion {

	
	public class ScoreBar{
		
		private String ScoreBarID = "";
		private int ScoreBarTotalScore = 0;
		private int ScoreBarDefaultScore = 0;
		private String SCOREBAR = "scorebar";

//		public static final String SCOREBAR_TAG = "scorebar";
		
		public ScoreBar(String ID,int totalScore,int defaultScore)
		{
			ScoreBarID = ID;
			ScoreBarTotalScore = totalScore;
			ScoreBarDefaultScore = defaultScore;
			
		}
		public String getScorebarName()
		{
			return SCOREBAR;
		}
		public String getStringBarWithResult()
		{
			String test="";
			if(ScoreBarTotalScore<ScoreBarDefaultScore)
			{
				ScoreBarDefaultScore = ScoreBarTotalScore;
			}
			test=	
					"<div name=\""+ SCOREBAR +"\" style=\"padding-top:5px;padding-bottom:0px;display:none;\">"+
					"<div style=\"background-color:rgb(255,241,183);width:95%;\">"+
					"<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">"+
					"<tr>"+
					"	<td>"+
					"		<a style=\"color:rgb(241,97,1);\">评分：本题共</a>"+
					"		<a style=\"color:rgb(241,97,1);\">"+ScoreBarTotalScore+"</a>"+
					"		<a style=\"color:rgb(241,97,1);\">分，你得了</a>"+
					"	</td>"+
					"	<td>"+	
					"		<input type=\"text\" id=\""+ScoreBarID+"\" value="+ScoreBarDefaultScore+" readOnly=\"true\" size=\"1\"	style=\"width: 40; height: 26;text-align:center;border-width:0px;background-color:rgb(255,241,183);\">"+
					"	</td>"+
					"	<td>"+		
					"		<a style=\"color:rgb(241,97,1)\">分</a>"+
					"	</td>"+
					"	</tr>"+
					"	</table>"+
					"	</div>"+
					"</div>";
			return test;
		}
		public String getStringBar()
		{
			String test="";
			if(ScoreBarTotalScore<ScoreBarDefaultScore)
			{
				ScoreBarDefaultScore = ScoreBarTotalScore;
			}
			test=	
					"<div name=\""+ SCOREBAR +"\" style=\"padding-top:5px;padding-bottom:0px;display:none;\">"+
					"<div style=\"background-color:rgb(255,241,183);width:95%;\">"+
					"<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">"+
					"<tr>"+
					"	<td>"+
					"		<a style=\"color:rgb(241,97,1);\">评分：本题共</a>"+
					"		<a style=\"color:rgb(241,97,1);\">"+ScoreBarTotalScore+"</a>"+
					"		<a style=\"color:rgb(241,97,1);\">分，你能得</a>"+
					"		<input type=\"image\" align=\"absmiddle\" height=\"30\" src=\""+assetPath+"ES/minus_default.png\" onmousedown=\"subScore('"+ScoreBarID+"');\">"+	
					"	</td>"+
					"	<td style=\"background-color:rgb(241,97,1);\">	"+	
					"		<input type=\"text\" id=\""+ScoreBarID+"\" value="+ScoreBarDefaultScore+" readOnly=\"true\" size=\"1\"	style=\"width: 40; height: 26;text-align:center;border-width:0px;\">"+
					"	</td>"+
					"	<td>"+		
					"		<input type=\"image\" align=\"absmiddle\" height=\"30\" src=\""+assetPath+"ES/plus_default.png\" onmousedown=\"addScore("+ScoreBarTotalScore+",'"+ScoreBarID+"');\">"+
					"		<a style=\"color:rgb(241,97,1)\">分</a>"+
					"	</td>"+
					"	</tr>"+
					"	</table>"+
					"	</div>"+
					"</div>";
			return test;
		}
	}
	public class AnswerBar{
		
		private String AnswerBarID = "";
		private String AnswerBarAns = "";
		private String AnswerBarExp = "";
		private String SCOREBAR = "scorebar";
	
		public AnswerBar(String ID,String Ans,String Exp)
		{
			AnswerBarID = ID;
			AnswerBarAns = Ans;
			AnswerBarExp = Exp;
		}
		public String getAnswerBarName()
		{
			return SCOREBAR;
		}
		public String getStringBar()
		{
			String test=
					"<span name=\"scorebar\" id=\""+AnswerBarID+"\" style=\"display:none;\">"+
//					"<div style=\"width:90%;\">"+
					"<table width=\"95%\" bordercolor=\"rgb(255,241,183)\" style=\"border-style:solid;border-width:thick thin thin thin;\"> "+
					"<td border=\"0\">"+
					"<font color=\"#000000\">"+AnsString+": </font>"+
					AnswerBarAns+"<br>"+AnswerBarExp+
					"</td>"+
					"</table>"+
//					"</div>"+
					"</span>";
			return test;
		}
	}
	
	public static class ObjName{
		public static String UNITE				="UnitE";
		public static String ID					="ID";
		public static String SUBJECT			="Subject";
		public static String TITLE_TYPE			="QType";
		public static String DISPLAY_TYPE		="SType";
		public static String KNOWLEDGE			="Knowledge";
		public static String QUESTION			="Question";
		public static String SELECTION[]		={"SelectionA","SelectionB","SelectionC","SelectionD","SelectionE"};
		public static String RESOLVING			="Resolving";
		public static String ANSWER				="Answer";
		public static String LEVEL				="Level";
		public static String UNIT				="Unit";
		public static String KNOWLEDGE_CODE		="KnowledgeCode";
		public static String KEYWORD			="Keyword";
		public static String RECOMMENDEDGRADE	="RecommendedGrade";
		public static String FREQUENCY			="Frequency";
		public static String VERUNIT			="VerUnit";
		public static String MOCKID				="MockID";
		public static String BOOKNAMEID			="BookNameID";
	}


	
	protected final int MAXSELECTIONNUM  = 4;//
	protected final int DEFAULTFONTSIZE  = 16;//
	protected final int ANS_IMG_HEIGHT = 24;//ans button heigth	(20,20)(16,18)
	protected final int LINK_IMG_HEIGHT = 18;//knowledge link button heigth	(20,20)(16,18)
	protected final int SEL_IMG_HEIGHT = 16;//select button heigth	(20,19)(16,16)
	protected final int REALTEACH_IMG_HEIGHT = 18;//real teach link button heigth	(20,20)(16,18)

	protected final int ANS_IMG_WIDTH = 95*ANS_IMG_HEIGHT/32;//ans button width
	protected final int SEL_IMG_WIDTH = 53*SEL_IMG_HEIGHT/38;//select button width
	protected final int LINK_IMG_WIDTH = 84*LINK_IMG_HEIGHT/32;//ans button width
	protected final int REALTEACH_IMG_WIDTH = 104*REALTEACH_IMG_HEIGHT/32;//real teach button width
	
	protected final String assetPath = "file:///android_asset/";
	protected final String BaseUrl = "http://10.180.6.46:8080";
	
	
	protected String AnsString = "Answer";	
	protected JSONObject jsonObject;
	protected String QuestionID;
	protected int child_count = 0;
	protected int TitleExamId = 0;
	protected boolean ScoreFlag = false;
	
	
	
//	protected boolean ScoreBarEndFlag = false;

	/**
	 * 
	 * @param jsonObject
	 * @param QuestionID
	 * @param child_count
	 * @param TitleExamId
	 * @param ScoreFlag
	 */
	public BaseQuestion(JSONObject arg0,String arg1,int arg2,int arg3,boolean arg4)
	{
		jsonObject = arg0;
		QuestionID = arg1;
		child_count = arg2;
		TitleExamId = arg3;
		ScoreFlag = arg4;
//		ScoreBarEndFlag = arg3;
	}

	public String getQuestion()
	{
		return "";		
	}
//	public boolean getScoreBarEndFlag()
//	{
//		return ScoreBarEndFlag;
//	}
	public void setAnsString(String arg0)
	{
		AnsString = arg0;
	}
//	public String getAnsString()
//	{
//		return AnsString;
//	}
	public String getStringFromJSON(String ObjName)
	{
		String ret = "";
		ret = getObjString(jsonObject,ObjName);
		return ret;
	}
/*  Delete at 2012/11/12
 *   protected String signReplace(String buffer)
    {
    	String ret = buffer;
    	String[] ImageList = getMidStringAll(ret,HtmlSign.IMG_START,HtmlSign.IMG_END);
    	for(int i = 0;i<ImageList.length;i++)
    	{
    		String ImageString = getImageByBase64(ImageList[i]);
//    		ImageString = "<img align=\"absmiddle\" src=";
    		ImageString = "<a href=\"image:"+ImageList[i]+"\"><img align=\"absmiddle\" src=\"data:image/png;base64,"+ImageString+"\"></a>";
    		ret = ret.replace(HtmlSign.IMG_START+ImageList[i]+HtmlSign.IMG_END,ImageString);
    	}
    	ret = ret.replace(HtmlSign.NEWLINE, "<br>");
    	return ret;
    }*/
    protected String signReplace(String buffer)
    {
    	String ret = buffer;
    	String[] ImageList = getMidStringAll(ret,HtmlSign.IMG_START,HtmlSign.IMG_END);
    	String ImageArray = getImgArray(ret,HtmlSign.IMG_START,HtmlSign.IMG_END);
    	System.out.println("[IMAGE ARRAY]:"+ImageArray);
        String ImageBase64 = "";
        try{     
            String url = BaseUrl+"/BestaExam/services";
            String paramStr = "";
            Map params = new HashMap();
            params.put("action", URLEncoder.encode("getImage","utf-8") );
            params.put("imageArray", URLEncoder.encode(ImageArray,"utf-8"));
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
//            StringBuffer sb = new StringBuffer("");       
            String line = "";
            String NL = System.getProperty("line.separator");  
            int count = 0;
            while((line = in.readLine()) != null){
//                    sb.append(line + NL);
            	
//            		String ImageString = getImageByBase64(ImageList[count]);
//            		ImageString = "<img align=\"absmiddle\" src=";
            		String ImageString = "<a href=\"image:"+ImageList[count]+"\"><img align=\"absmiddle\" src=\"data:image/png;base64,"+line+"\"></a>";
            		ret = ret.replace(HtmlSign.IMG_START+ImageList[count]+HtmlSign.IMG_END,ImageString);
            		count++;
            }
            in.close();  
//            ImageBase64 = sb.toString();
        }catch(Exception e){
        	e.printStackTrace();
        }
    	ret = ret.replace(HtmlSign.NEWLINE, "<br>");
    	return ret;
    }
    protected String getImageByBase64(String ImageID)
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
    protected String[] getMidStringAll(String initiativeStr,String beginStr,String endStr)//get the mid String between beginStr and endStr
    {
    	ArrayList<String> array = new ArrayList<String>();
    	int begin = 0;
    	int end = 0;
    	@SuppressWarnings("unused")
		int imgCount = 0; 
    	
    	int beginStrLength = beginStr.length();
    	while(begin != -1)
    	{
	    	begin = initiativeStr.indexOf(beginStr,end);
	    	end = initiativeStr.indexOf(endStr, begin + beginStrLength);
	    	if(begin < end && begin != -1 && end != -1)
	    	{
	    		array.add(initiativeStr.substring(begin + beginStrLength,end));
	    	}
	    	else
	    	{
	    		break;
	    	}
	    	imgCount++;
    	}
    	return array.toArray(new String[0]);
    }
    protected String getImgArray(String initiativeStr,String beginStr,String endStr)//get the mid String between beginStr and endStr
    {
    	ArrayList<String> array = new ArrayList<String>();
    	int begin = 0;
    	int end = 0;
    	@SuppressWarnings("unused")
		int imgCount = 0; 
    	
    	int beginStrLength = beginStr.length();
    	while(begin != -1)
    	{
	    	begin = initiativeStr.indexOf(beginStr,end);
	    	end = initiativeStr.indexOf(endStr, begin + beginStrLength);
	    	if(begin < end && begin != -1 && end != -1)
	    	{
	    		array.add("\""+initiativeStr.substring(begin + beginStrLength,end)+"\"");
	    	}
	    	else
	    	{
	    		break;
	    	}
	    	imgCount++;
    	}
    	return array.toString();
    }
    protected String getObjString(JSONObject jsonObject,String Name)
    {
    	String ret = "";
    	try {
    		ret = jsonObject.getString(Name);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret; 	
    }
}
