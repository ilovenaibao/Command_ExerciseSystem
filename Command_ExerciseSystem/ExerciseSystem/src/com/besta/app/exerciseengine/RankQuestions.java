package com.besta.app.exerciseengine;

import java.io.File;
import java.io.IOException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class RankQuestions {


	public String[] RankByTF(Context context,String[] args,boolean isRandom)
	{
		class Ques{
			String Id;
			int history;
		}
		if(args.length==0)
		{
			return args;
		}
//		String reques[] = null;
		String dbcommand = null;
		String history = "'null'";
//		try {
//			
//		String command = "chmod 777 " + context.getFilesDir();
//		Runtime runtime = Runtime.getRuntime();
//		
//			runtime.exec(command);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		SQLiteDatabase db = null;
		File historyfile = new File(context.getFilesDir(),"history.db");
		Ques ques[] = new Ques[args.length];
		for(int i = 0;i<args.length;i++)
		{
			ques[i] = new Ques();
			ques[i].Id = args[i];
			ques[i].history = 0;
			history = history+",'"+ques[i].Id+"'";
		}
		if(historyfile.exists())
		{
			db = SQLiteDatabase.openOrCreateDatabase(historyfile, null);
		}
		else
		{
//	        historyfile.mkdirs(); 
			db = SQLiteDatabase.openOrCreateDatabase(historyfile, null);
			dbcommand="CREATE TABLE HISTORYDATA(DataIndex TEXT,History TEXT)";
			db.execSQL(dbcommand); 
		}
	    db.beginTransaction();
	    try {
	    dbcommand = "SELECT * FROM HISTORYDATA  WHERE DataIndex IN (" + history + ")";
	    Cursor cur = db.rawQuery(dbcommand, null);	
	    if( cur != null )
		{
			if(cur.moveToFirst())
			{
				do
			    {
					for(int i = 0;i<ques.length;i++)
					{
						if(cur.getString(0).equals(ques[i].Id))
						{
							ques[i].history = cur.getInt(1);
							break;
						}
					}
			    }while(cur.moveToNext());
			}
		}
//	    boolean rightFlag = true;
//	    for(int i = 0;i<ques.length;i++)
//	    {
//	    	if(ques[i].history == 0)
//	    	{
//		    	ContentValues cv = new ContentValues();
//		    	cv.put("DataIndex", ques[i].Id);
//		    	cv.put("History", ques[i].history);
//		    	db.insert("HISTORYDATA", null, cv); 
//	    	}
//	    	if(ques[i].history != -1)
//	    	{
//	    		rightFlag = false;
//	    	}
//		} 
//	    if(rightFlag == true)
//	    {
//		    for(int i = 0;i<ques.length;i++)
//		    {
//				ContentValues cv = new ContentValues();
//				ques[i].history = 0;
//		    	cv.put("History",0);
//		    	String[] whereArgs={ques[i].Id};				    	
//		    	db.update("HISTORYDATA",cv,"DataIndex=?",whereArgs);
//		    }
//	    }
	    String questemp;
	    for(int i = 0;i<ques.length;i++)
	    {
	    	for(int j = 0;j<ques.length;j++)
	    	{
		    	if(ques[i].history>ques[j].history)
		    	{
		    		questemp = args[j];
		    		args[j] = args[i];
		    		args[i] = questemp;
		    	}
	    	}
	    }
	      db.setTransactionSuccessful();
	    } finally {
	      db.endTransaction();
	    }
	    db.close();
		return args;
	}
}
