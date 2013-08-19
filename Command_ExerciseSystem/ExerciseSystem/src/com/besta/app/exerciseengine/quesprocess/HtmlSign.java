package com.besta.app.exerciseengine.quesprocess;

public class HtmlSign {

	public static String IMG_START = "<IMG>";
	public static String IMG_END = "</IMG>";
	public static String BLANK = "<BLANK>";//<BLANK> <BRACKET>
	public static String BRACKET = "<BRACKET>";
	public static String NEWLINE = "<NEWLINE>";
//	public static String QUES_START = "<QUES>";
//	public static String QUES_END = "</QUES>";
	public static String ANS_START = "<ANS>";
	public static String ANS_END = "</ANS>";
	static public String QUES_START(String Id,int Num)
	{
		return "<QUES id="+Id+"num="+Num+">";
	}
	static public String QUES_END()
	{
		return "</QUES>";
	}
	static public String ANS_DISPLAY_START(String Id,int Num)
	{
//		<div id=\""+formularIdtemp+"\" style=\"display:none\"></div>
		String id = QUES_LOCAL_ID(Id,Num);
		return "<div id=\""+id+"\" style=\"display:none\">";
	}

	static public String ANS_DISPLAY_END()
	{
		return "</div>";
	}
	
	static public String ANS_DISPLAY_BLANK_START(String Id,int Num)
	{
//		<div id=\""+formularIdtemp+"\" style=\"display:none\"></div>
		String id = QUES_LOCAL_ID(Id,Num);
		return "<a id=\""+id+"\" style=\"display:none\">";
	}
	static public String ANS_DISPLAY_BLANK_END()
	{
		return "</a>";
	}
	
	static public String QUES_LOCAL_ID(String Id,int Num)
	{
		return Id+"|"+Num;
	}
//	static public String GET_ID_BY_LOCAL(String Id)
//	{
////		Id.substring(0,Id.lastIndexOf("|"));
//		return Id.substring(0,Id.lastIndexOf("|"));
//	}
}
