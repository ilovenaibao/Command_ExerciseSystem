package com.besta.app.exerciseengine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class PopupList extends Activity{

	static final String KNOWLEDGE_twchemistry	= "com.besta.app.knowledge.twchemistry";
	static final String KNOWLEDGE_twphysics 	= "com.besta.app.knowledge.twphysics";
	static final String KNOWLEDGE_twearthscience= "com.besta.app.knowledge.twearthscience";
	static final String KNOWLEDGE_twbiology		= "com.besta.app.knowledge.twbiology";
	static final String KNOWLEDGE_twmath		= "com.besta.app.knowledge.twmath";
	
	static final String KNOWLEDGE_cnchemistry	= "com.besta.app.knowledge.cnchemistry";
	static final String KNOWLEDGE_cnphysics 	= "com.besta.app.knowledge.cnphysics";
	static final String KNOWLEDGE_cnmath 		= "com.besta.app.knowledge.cnmath";

	
	List<Map<String, Object>> list;
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
    	setContentView(R.layout.popuplist);
    	ListView myListView = (ListView)findViewById(R.id.ListView);  	
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        LinearLayout.LayoutParams localLayoutParams1 = new LinearLayout.LayoutParams(-1, -1);
        localLayoutParams1.width = localDisplayMetrics.widthPixels/2;
    	myListView.setOnItemClickListener(new ListOnClickListener());
    	List<String> data = initData();
		if(data.size()==1)
		{
			finish();
			Map<String, Object> map = list.get(0);
			startKnowledge((String)map.get("knowledgeid"),(String)map.get("packagename"));
		}
		else if(data.size()>1)
    	{
    		if(data.size()>5)
    		{
    			localLayoutParams1.height = getWindowManager().getDefaultDisplay().getHeight()-200;
    		}
        	myListView.setLayoutParams(localLayoutParams1);
    		ArrayAdapter<String> myPopupListAdapter = new ArrayAdapter<String>(this,R.layout.listtext,initData());
    		myListView.setAdapter(myPopupListAdapter);
    	}
    	else
    	{
    		Toast.makeText(this,getString(R.string.Notfound),Toast.LENGTH_SHORT).show();
    	}
	}
	private List<String> initData()
	{
		Intent intent = getIntent();
		ArrayList<String> IOArray = new ArrayList<String>();
		IOArray = intent.getStringArrayListExtra("unitnumberarray");
		String KnowledgeId[] = IOArray.toArray(new String[0]);

	    list = new ArrayList<Map<String, Object>>();
		SQLiteDatabase db = null;
		List<String> data = new ArrayList<String>();
		
	    for(int j = 0;j<KnowledgeId.length;j++)
	    {
	    	String PackageName = getKPackageName(KnowledgeId[j]);
			String dbPath = "/sdcard/besta/"+PackageName+"/knowledge.db"; //*************
			Intent intentTemp = new Intent();
			intentTemp.setAction(PackageName+".KNOWLEDGE");
			if(new File(dbPath).exists()&&getPackageManager().queryIntentActivities(intentTemp,PackageManager.MATCH_DEFAULT_ONLY).size()>0)
			{
			    db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);	   
			    String dbcommand = "SELECT * FROM KDATA  WHERE number LIKE\"" + KnowledgeId[j] + "\"";
			    Cursor cur = db.rawQuery(dbcommand, null);	
			    if( cur != null )
				{	    	
					if(cur.moveToFirst())
					{
						data.add(Html.fromHtml(htmlDataProcess(cur.getString(cur.getColumnIndex("title")))).toString());
						Map<String, Object> map =  new HashMap<String, Object>();
						map.put("packagename",PackageName);
						map.put("knowledgeid", cur.getString(cur.getColumnIndex("number")));	
						list.add(map);
					}
				}
			    db.close();
			}
	    }
	    return data;
	}
	private String getKPackageName(String KnowledgeId)
	{
		String ret = "";
		if(KnowledgeId.startsWith("C"))
		{
			ret = KNOWLEDGE_twchemistry;
		}
		else if(KnowledgeId.startsWith("P"))
		{
			ret = KNOWLEDGE_twphysics;
		}
		else if(KnowledgeId.startsWith("G"))
		{
			ret = KNOWLEDGE_twearthscience;
		}
		else if(KnowledgeId.startsWith("B"))
		{
			ret = KNOWLEDGE_twbiology;
		}
		else if(KnowledgeId.startsWith("M"))
		{
			ret = KNOWLEDGE_twmath;
		}
		return ret;
	}
	private String htmlDataProcess(String buffer)
	{
//	    buffer = buffer.replaceAll("</LI>", "");
//	    buffer = buffer.replaceAll("<P>", "");
//	    buffer = buffer.replaceAll("</P>", "");
//	    buffer = buffer.replaceAll("</FONT>", "");
//	    buffer = buffer.replaceAll("<FONT size=5>", "");		
//	    buffer = buffer.replaceAll("</UL>", "");	
//	    buffer = buffer.replaceAll("<B>", "<B style=\"color:red\">");
//		buffer = buffer.replace("</IMG>", "\">");
//	    buffer = buffer.replace("<NEWLINE>", "<br>");
//	    buffer = buffer.replace("<u>", "<font color=\"red\">");
//	    buffer = buffer.replace("</u>", "</font>");
		return buffer;
	}
	private void startKnowledge(String knowledgeId,String packageName)
	{
		Intent intent = new Intent();
		intent.putExtra("knowledgeid",knowledgeId);
		intent.setAction(packageName+".KNOWLEDGE");
		if(getPackageManager().queryIntentActivities(intent,PackageManager.MATCH_DEFAULT_ONLY).size()>0)
		{
			startActivity(intent);
		}
		else
		{
			Toast.makeText(PopupList.this,getString(R.string.Notfound),Toast.LENGTH_SHORT).show();
		}
	}
	class ListOnClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Map<String, Object> map = list.get(arg2);
			startKnowledge((String)map.get("knowledgeid"),(String)map.get("packagename"));
		}
	}
}
