package com.besta.app.exerciseengine;

import java.io.UnsupportedEncodingException;

import com.besta.app.exerciseengine.question.Question;

import android.content.Context;


public class CreateHtml {
	
	public static final int MAXSELECTIONNUM  = 4;//
	public static final int DEFAULTFONTSIZE  = 16;//
	public static final int ANS_IMG_HEIGHT = 18;//ans button heigth	(20,20)(16,18)
	public static final int LINK_IMG_HEIGHT = 18;//knowledge link button heigth	(20,20)(16,18)
	public static final int SEL_IMG_HEIGHT = 16;//select button heigth	(20,19)(16,16)
	public static final int REALTEACH_IMG_HEIGHT = 18;//real teach link button heigth	(20,20)(16,18)

	public static final int ANS_IMG_WIDTH = 95*ANS_IMG_HEIGHT/32;//ans button width
	public static final int SEL_IMG_WIDTH = 53*SEL_IMG_HEIGHT/38;//select button width
	public static final int LINK_IMG_WIDTH = 84*LINK_IMG_HEIGHT/32;//ans button width
	public static final int REALTEACH_IMG_WIDTH = 104*REALTEACH_IMG_HEIGHT/32;//real teach button width
	String assetPath = "file:///android_asset/";
	
	private boolean isDisplayResolving = true;
	
	public void setDisplayResolving(boolean arg)
	{
		isDisplayResolving = arg;
	}
	public StringBuffer createHtml(Context context,Question[] questions,int maxDisplay)//
	{	
		StringBuffer retString = new StringBuffer();
//		quesFlag = true;
		for(int count = 0;count<(questions.length<maxDisplay?questions.length:maxDisplay);count++)
		{
//		historyRankList[QuesTotalNum] = cur.getString(cur.getColumnIndex("DataIndex"));
		String QuesId = questions[count].ID;
		String Ques = questions[count].Question;//content need to exchange
		String Exp = questions[count].Resolving;//content need to exchange
//		String Background = "";
		if(questions[count].Background!=null)
		{
			Ques = questions[count].Background+ "<br>" +Ques;//content need to exchange
		}
//		System.out.println("[Background]"+cur.isNull(cur.getColumnIndex("Background")));
//		String Background = replaceBuffer(cur.getString(cur.getColumnIndex("Background")));//content need to exchange
		String Ans = questions[count].Answer;
		String Selection[] = new String[MAXSELECTIONNUM];
		
//		if(Background!="")
//		{
//			Ques = Background + "<br>" +Ques ;
//		}
		for(int i=0;i<MAXSELECTIONNUM;i++)
		{
			Selection[i]= questions[count].Selections[i];//content need to exchange
		}
//			RandomAccessFile pHtmlFile = new RandomAccessFile(pathtemp + File.separator+"temp"+ File.separator +"kpexercise.html","rw");							
					String iconPath = null;
		//			pHtmlFile.seek(pHtmlFile.length());
		//			pHtmlFile.write(transSign("<li>"));//li start
					
					retString = retString.append("<li>");
		/////////////////////////////////////////////////
					if(questions[count].BASETEST_TAG)
					{
						String year ="";
						String type = "";
						String num = QuesId.substring(5,7);
						String subject = "";
						
//						if(QuesId.startsWith("m"))
//						{
//							subject = "數學";
//						}
//						else if(QuesId.startsWith("n"))
//						{
//							subject = "自然科學";
//						}
						if(QuesId.substring(3,5).equals("01"))
						{
							type = "第一次";
						}
						else if(QuesId.substring(3,5).equals("02"))
						{
							type = "第二次";
						}
						else if(QuesId.substring(3,5).equals("03"))
						{
							type = "第三次";
						}
						if( QuesId.substring(1,3).startsWith("0"))
						{
							year =  "1"+QuesId.substring(1,3);
						}
						else
						{
							year =  QuesId.substring(1,3);
						}
						String baseinfo = "<font color=\"#000000\">【"+subject+year+"年"+type+"第"+num+"題】</font>";
		//				pHtmlFile.write(transSign(baseinfo));				
						retString = retString.append(baseinfo);
					}			
		//			pHtmlFile.write(transSign(Ques));
					retString = retString.append(Ques);
		/////////////////////////////////////////////////
		
					iconPath = assetPath+"ans_open.pic";
					String ButtonId = questions[count].ID+"-ansbutton";
					String AnsId = questions[count].ID+"-ans";
					if(Exp.length() != 0 && isDisplayResolving)
					{
		//				pHtmlFile.write(transSign("    <input type=\"image\" id=\""+ButtonId+"\" src=\""+iconPath+"\"width=\""+ANS_IMG_WIDTH+"px\"height=\""+ANS_IMG_HEIGHT+"px\" vspace=\"1\" align=\"top\" onclick=\"displayhidden(\'"+AnsId+"\','"+ButtonId+"');\">"));	
						retString = retString.append("    <input type=\"image\" id=\""+ButtonId+"\" src=\""+iconPath+"\"width=\""+ANS_IMG_WIDTH+"px\"height=\""+ANS_IMG_HEIGHT+"px\" vspace=\"1\" align=\"top\" onclick=\"displayhidden(\'"+AnsId+"\','"+ButtonId+"');\">");
					}
//					if(tag.equals(BASETEST_TAG))
//					{
//						String knowledgeLink = cur.getString(cur.getColumnIndex("knowledge"));
//						if(knowledgeLink.length()>0)
//						{
//							iconPath = assetPath+"link.pic"; 
//		//					pHtmlFile.write(transSign("<a href=\"knowledge:"+knowledgeLink+"\"><img src=\""+iconPath+"\"width=\""+LINK_IMG_WIDTH+"px\"height=\""+LINK_IMG_HEIGHT+"px\" vspace=\"1\" align=\"top\" ></a>"));
//							retString = retString.append("<a href=\"knowledge:"+knowledgeLink+"\"><img src=\""+iconPath+"\"width=\""+LINK_IMG_WIDTH+"px\"height=\""+LINK_IMG_HEIGHT+"px\" vspace=\"1\" align=\"top\" ></a>");
//						}
//					}
					int correctAnsNum = transSign(Ans)[0] -'A';
		//			String AnsTF[] = new String[MAXSELECTIONNUM];
		//			for(int i = 0;i<MAXSELECTIONNUM;i++)
		//			{
		//				AnsTF[i]="0";
		//			}
		//			AnsTF[correctAnsNum] = "1";
					String AnsTF = "0";
					String AnsIconPath[] = {"a_select_ex.pic",
											"b_select_ex.pic",
											"c_select_ex.pic",
											"d_select_ex.pic",
											"e_select_ex.pic",
											"f_select_ex.pic"};
					for(int i = 0;i<MAXSELECTIONNUM && Selection[i]!="";i++)
					{							
						String ChooseId = questions[count].ID+"-"+i;//0-0,0-1.....
						String ChooseName = questions[count].ID;//01,02,03.....
//						iconPath = assetPath+AnsIconPath[i];
						if(i == correctAnsNum)
						{
							AnsTF = "1";
						}
						else
						{
							AnsTF = "0";
						}
						char selectChar = (char)('A'+i);
						retString = retString.append("<br><input type=\"radio\" id=\""+ChooseId+"\" name=\""+ChooseName+"\" value=\""+AnsTF+"\"src=\""+iconPath+"\"width=\""+SEL_IMG_WIDTH+"px\"height=\""+SEL_IMG_HEIGHT+"px\" vspace=\"1\" align=\"absmiddle\"onclick=\"changebutton(\'"+ChooseName+"\',"+i+");\">");
						retString = retString.append(selectChar+".   "+Selection[i]);
					}						

					retString = retString.append("<h id=\""+AnsId+"\" style=\"display:none\">");
					retString = retString.append("<table width=\"90%\" bordercolor=\"#6C6C6C\" style=\"border-style:solid;border-width:thick thin thin thin;word-break:break-all;\"> <td border=\"0\">");
					retString = retString.append("<font color=\"#000000\">"+context.getString(R.string.answer)+": </font>");
					retString = retString.append("<b>"+Ans+"</b>");
					retString = retString.append("<br>");
					retString = retString.append(Exp);
					retString = retString.append("</td></table>");
					retString = retString.append("</h>");
					
					retString = retString.append("</li>");
		}
		return retString;

    }
    public byte transSign(String str)[]//String to Unicode byte like 3C to 3C 00
    {
    	byte strbyte[] = new byte[str.length()*2];
    	byte strbyteTemp[] = new byte[str.length()*2+2];
    	try {   		
    		strbyteTemp = str.getBytes("unicode");
	    	for(int i = 0;i<str.length()*2;i++)
	    	{
	    		strbyte[i]=strbyteTemp[i+2];
	    	}   	
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
		}
		return strbyte;
    }
}
