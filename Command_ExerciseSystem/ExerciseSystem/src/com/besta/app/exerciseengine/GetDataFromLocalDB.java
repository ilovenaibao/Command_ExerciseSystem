package com.besta.app.exerciseengine;

import com.besta.app.exerciseengine.question.Question;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;

public class GetDataFromLocalDB {
	
//	public static final String SDCARD = Environment.getExternalStorageDirectory().getPath();
//	String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
//	private String pathtemp = "/data/data/" + "com.besta.app.knowledge.exercise" + "/files";
//	private String pathHistory = "/data/data/" + "com.besta.app.knowledge.exercise" + "/files/History";
	private int MAXQUESNUM = 99;
	private int MAXSELECTIONNUM = 4;
	private boolean accurateSearch = false;
//	private boolean randomRank = true;
	
	public void setMaxQuesNum(int arg)
	{
		MAXQUESNUM = arg;
	}
	public void setAccurateSearch(boolean arg)
	{
		accurateSearch = arg;
	}
//	public void setRandomRank(boolean arg)
//	{
//		randomRank = arg;
//	}
	public Question[] getQuestions(String DbPath,String[] quesList)
	{
		Question[] questions = null;
		SQLiteDatabase db = null;
		String sectionindex = "'null'";
//		String imgString =  "'null'";
//		Cursor curtemp[] = null;
	    db = SQLiteDatabase.openDatabase(DbPath, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
	    if(accurateSearch)
	    {
		    for(int i = 0;i<quesList.length;i++)
		    {
		    	System.out.println("quesNum: "+quesList[i]);
		    	sectionindex = sectionindex+",'"+quesList[i]+"'";
		    }
//		    db.beginTransaction();
//		    try {
		    String dbcommand = "SELECT * FROM KEDATA  WHERE DataIndex IN (" + sectionindex + ")";
		    Cursor cur = db.rawQuery(dbcommand, null);	
		    if( cur != null )
			{
				if(cur.moveToFirst())
				{
					if(cur.getCount()<MAXQUESNUM)
					{
						questions = new Question[cur.getCount()];
					}
					else
					{
						questions = new Question[MAXQUESNUM];
					}
					for(int count = 0;count<questions.length;count++,cur.moveToNext())
					{
						questions[count] = new Question();
						questions[count].ID = cur.getString(cur.getColumnIndex("DataIndex"));
						questions[count].Answer = cur.getString(cur.getColumnIndex("Answer"));
						questions[count].Level = cur.getString(cur.getColumnIndex("Difficulty"));
						questions[count].Question = replaceBuffer(cur.getString(cur.getColumnIndex("Ques")));
						questions[count].Resolving = replaceBuffer(cur.getString(cur.getColumnIndex("Explain")));
						questions[count].ImageString = replaceBuffer(cur.getString(cur.getColumnIndex("ImageNameCount")));

						
						
						
						questions[count].Selections = new String[MAXSELECTIONNUM];
						for(int i=0;i<MAXSELECTIONNUM;i++)
						{
							questions[count].Selections[i] = replaceBuffer(cur.getString(cur.getColumnIndex("SelectionA")+i));//content need to exchange
						}
						if(cur.getColumnIndex("Background")>-1 && !cur.getString(cur.getColumnIndex("Background")).isEmpty() && cur.getString(cur.getColumnIndex("Background"))!="")
						{
							questions[count].Background = replaceBuffer(cur.getString(cur.getColumnIndex("Background")));
						}
//						questions[count] = insertImageBase64(db,questions[count],cur.getString(cur.getColumnIndex("ImageNameCount")),"IMAGEDATA");
					}
				}
			}
		    cur.close();
		    db.close();

//	        db.setTransactionSuccessful();
//		      } finally {
//		        db.endTransaction();
//		      }
//		    pHtmlString = new StringBuffer(insertImageBase64(pHtmlString.toString(), db, imgString, "IMAGEDATA"));		    
//		    CreateImage(db,imgString);
		    
	    }
	    else
	    {
		    for(int j = 0;j<quesList.length;j++)
		    {
			    String dbcommand = "SELECT * FROM KEDATA  WHERE DataIndex LIKE \"%" + quesList[j] + "%\"";
			    Cursor cur = db.rawQuery(dbcommand, null);	
				if( cur != null )
				{
					if(cur.moveToFirst())
					{
						do
					    {
//							createHtml(db,cur,QuesTotalNum);
//							pHtmlString = new StringBuffer(insertImageBase64(pHtmlString.toString(), db, cur, "IMAGEDATA"));
					    }
					    while(cur.moveToNext());
					}
				}
				cur.close();
		    }	  
	    }

//	    GetDataFromHttpGet myGetDataFromHttpGet = new GetDataFromHttpGet();
//	    myGetDataFromHttpGet.getExamPaper();
	    
	    
//	    checkFlashUpdate_New(quesList);

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.close();
//	    if(quesFlag == false)
//	    {
//	    	Toast.makeText(getApplicationContext(),"Can not find any subjects!", Toast.LENGTH_SHORT).show();
//	    	finish();
//	    }
		return questions;		
	}
    public String replaceBuffer(String buffer)
    {

    	buffer = buffer.replaceAll("</LI>", "");
    	buffer = buffer.replaceAll("<P>", "");
    	buffer = buffer.replaceAll("</P>", "");
    	buffer = buffer.replaceAll("</FONT>", "");
//		Ques = Ques.replaceAll("</FONT><P>", "");
    	buffer = buffer.replaceAll("<FONT size=5>", "");		
    	buffer = buffer.replaceAll("</UL>", "");	
    	buffer = buffer.replaceAll("<B>", "<B style=\"color:red\">");
//    	buffer = buffer.replace("<IMG>", "<img align=\"absmiddle\" src="+pathtemp+"/temp/");
//    	buffer = buffer.replace("</IMG>", "\">");
    	buffer = buffer.replace("<IMG>", "<img align=\"absmiddle\" src=");
    	buffer = buffer.replace("</IMG>", ">");
    	buffer = buffer.replace("<NEWLINE>", "<br>");
    	return buffer;
    }
    public Question insertImageBase64(String DbPath,Question question,String tableName)
	{
		
		SQLiteDatabase db = null;
		String imgString = question.ImageString;
//		String sectionindex = "'null'";
//		Cursor curtemp[] = null;
	    db = SQLiteDatabase.openDatabase(DbPath, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
//		res = res.replace("src=\"IMAGE/", "src=\"image/");
//		String imgString = cur.getString(cur.getColumnIndex("ImageNameCount"));
		byte ImgBuffer[];
		String ImgName;
		if(!imgString.isEmpty())
		{
			String dbcommand = "SELECT * FROM "+tableName+" WHERE ImageName IN ("+imgString+")";
//			System.out.println(dbcommand);
			Cursor imgcur = db.rawQuery(dbcommand, null);
			if(imgcur != null )
			{
				if(imgcur.moveToFirst())
				{
					do{
						ImgName = imgcur.getString(imgcur.getColumnIndex("ImageName"));
						ImgBuffer = imgcur.getBlob(imgcur.getColumnIndex("Imagebuffer"));
						String content = Base64.encodeToString(ImgBuffer,Base64.DEFAULT);
						question.Question = question.Question.replace("src="+ImgName,"src=\"data:image/png;base64,"+content+"\"");
						question.Resolving = question.Resolving.replace("src="+ImgName,"src=\"data:image/png;base64,"+content+"\"");
						question.Answer = question.Answer.replace("src="+ImgName,"src=\"data:image/png;base64,"+content+"\"");
						for(int i=0;i<MAXSELECTIONNUM;i++)
						{
							question.Selections[i] = question.Selections[i].replace("src="+ImgName,"src=\"data:image/png;base64,"+content+"\"");//content need to exchange
						}
						if(question.Background!=null)
						{
							question.Background = question.Background.replace("src="+ImgName,"src=\"data:image/png;base64,"+content+"\"");

						}
//						question.Question = question.Question.replace("src="+ImgName,"src=\"data:image/png;base64,"+content+"\"");
					}while(imgcur.moveToNext());
				}
			}
			imgcur.close();
		}
		db.close();
		return question;
	}
}
