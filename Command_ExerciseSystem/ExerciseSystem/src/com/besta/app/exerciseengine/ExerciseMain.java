package com.besta.app.exerciseengine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.res.AssetManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.test.ActivityTestCase;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.besta.app.exerciseengine.quesprocess.HtmlSign;
import com.besta.app.exerciseengine.question.Question;
import com.besta.app.exerciseengine.question.QuestionArray;
import com.besta.app.testcallactivity.StartAnswerPaperSetting;
import com.besta.app.testcallactivity.IdConvert;

@SuppressWarnings("deprecation")
public class ExerciseMain extends Activity {

	class Ques {
		String Id;
		int history;
	}
	final static class RequestCode{
		final static int ANSWER_PAPER = 0;
		final static int MENU_SELECTOR = 1;
	}
	final static class ResultCode{
		final static int SHUTDOWN = 1;
		final static int MENU_SELECTOR = 2;
	}
	public static final int DEFAULTFONTSIZE = 16;//font size
	public static final String SUBMIT_ANSWERS = "submitanswers";
	public static final String BASETEST_TAG = "basetest";
	public static final String KNOWLEDGE_TAG = "knowledge:";
	public static final String ANSPAPER_TAG = "anspaper:";
	//	public static final String SCOREBAR_TAG = "scorebar";

	public static final String FLASH = "flash:";

	public static final String ANSPAPER_TYPE_ANSWERNORMAL = "anspaper_type_answernormal";
	public static final String ANSPAPER_TYPE_ANSWERDLG = "anspaper_type_answerdlg";
	public static final String ANSPAPER_TYPE_TESTDLG = "anspaper_type_testdlg";
	
	public static final String ACTION_BROWSE = "com.besta.app.exerciseengine.ACTION_READ";
	public static final String ACTION_TEST = "com.besta.app.exerciseengine.ACTION_EXERCISE";
	public static final String ACTION_EXAM = "com.besta.app.exerciseengine.ACTION_TEST";
	public static String Action;
	public static boolean isOpenPanel = false;
	public static boolean isQuesCollectEnable = false;
	public static boolean isMenuSelectorEnable = false;
	public static int KIND_OF_ANSPAPER = 0;
	public static float X = 0;
	public static float Y = 0;
	public static final String SDCARD = Environment.getExternalStorageDirectory().getPath();
	private Thread th = null;
	private LinearLayout mLoadingLayout;
	//	private Context context;
	//	private Intent MenuSelectIntent;
	int MAXQUESNUM = 99;
	public static StartAnswerPaperSetting setting;
	static public String AnswerResultHtmlData = new String();


	String sdcard = Environment.getExternalStorageDirectory().getPath();
	String dbpath = null;
	String DBNAME = null;// database name
	String tag = "";
	String path = "";
	String pathtemp = "/data/data/" + "com.besta.app.knowledge.exercise"
	+ "/files";
	String pathHistory = "/data/data/" + "com.besta.app.knowledge.exercise"
	+ "/files/history";
	String assetPath = "file:///android_asset/";
	static WebView webview;
	String mSelectedText;
	// String historyRankList[];
	String flashNameTemp = null;
	String updateFlashId[];
	String quesList[];
	String ExamValue;
	String CallingBuffer;
	// updateHandler myupdateHandler = new updateHandler();
	// RetrievalPopUpWindow mPopUpWindow;
	ProgressDialog prgdlg;
	Handler progressHandler;
	boolean accurateSearch = false;
	boolean quesFlag = false;
	boolean randomRank = true;
	boolean isExam = false;
	boolean isDisplayResolving = true;
	long Inclock = 0;

	private StringBuffer pHtmlString;
	public static QuestionArray questionArray;
	//	private Question[] questions;



	/** Called when the activity is first created. */
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//		ActivityManager AMG = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		//		//获得系统运行的进程  
		//		System.out.println("========获得系统运行的进程========");
		//		List<ActivityManager.RunningAppProcessInfo> appList1 = AMG  
		//		        .getRunningAppProcesses();  
		//		for(int i = 0;i<appList1.size();i++)
		//		{
		//			if(appList1.get(i).processName.equals("com.besta.app.exerciseengine"))
		//			{
		//				android.os.Process.killProcess(appList1.get(i).pid);
		//			}
		//		}
		//		System.out.println("===============================");
		
		//		System.out.println("=======获得当前正在运行的service======");  
		//		  
		//		//获得当前正在运行的service  
		//		List<ActivityManager.RunningServiceInfo> appList2 = AMG  
		//		        .getRunningServices(100);  
		//		for (ActivityManager.RunningServiceInfo running : appList2) {  
		//		    System.out.println(running.service.getClassName());  
		//		}  
		//		  
		//		System.out.println("=========获得当前正在运行的activity=========");  
		//		  
		//		//获得当前正在运行的activity  
		//		List<ActivityManager.RunningTaskInfo> appList3 = AMG.getRunningTasks(1000);  
		//		for (ActivityManager.RunningTaskInfo running : appList3) {  
		//		    System.out.println(running.baseActivity.getClassName());  
		//		}  
		//		System.out.println("=========获得最近运行的应用========");  
		//		  
		//		//获得最近运行的应用  
		//		List<ActivityManager.RecentTaskInfo> appList4 = AMG.getRecentTasks(100,2);  
		//		for (ActivityManager.RecentTaskInfo running : appList4) { 
		//		    System.out.println(running.origActivity.getClassName());  
		//		    }
		
		initActivity(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);	
		initActivity(intent);
	}
	protected void initActivity(Intent intent)
	{
		setIntent(intent);
		// SocketSender mySocketSender = new SocketSender();
		// mySocketSender.sendData();
		// ExerciseActivity.this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.exercise);
		questionArray = new QuestionArray();
		initState(getIntent());
		//		initView();
		//		Intent intent = getIntent();
		//		System.out.println(intent.getAction());
		//		if (intent.hasExtra("searchCondition")) {
		//			System.out.println(intent.getStringExtra("searchCondition"));
		//		}
		// System.out.println(intent.getStringExtra());

		/**
		* // Environment.getExternalStorageDirectory();
		* if(intent.getStringArrayListExtra("unitnumberarray")==null) {
		* intent.putExtra("randomrank",false);
		* intent.putExtra("accuratesearch",true);
		* intent.putExtra("displayresolving",true);
		* intent.putExtra("exam",false); intent.putExtra("tag",BASETEST_TAG);
		* intent.putExtra("dbpath",sdcard+
		* "/besta/com.besta.app.basetest.naturalscience/KE.db"); //
		* intent.putExtra("dbpath",sdcard+"/besta/cnmath/KE.db");
		* intent.putExtra("shownum",10); ArrayList<String> IOArray = new
		* ArrayList<String>(); // IOArray.add("7B-01"); //
		* IOArray.add("7B-01"); // IOArray.add("7A030601"); //
		* IOArray.add("PC0101"); // IOArray.add("8A0001-01"); //
		* IOArray.add("8A0004-07"); // IOArray.add("9B0219-02"); //
		* IOArray.add("9B0219-03"); // IOArray.add("8B0319-04"); //
		* IOArray.add("8B0318-01"); // IOArray.add("8B0318-02"); //
		* IOArray.add("8B0318-03"); // IOArray.add("8B0318-04"); //
		* IOArray.add("8B0318-05"); // IOArray.add("8B0318-06"); //
		* IOArray.add("8B0318-07"); // IOArray.add("8B0319-01"); //
		* IOArray.add("8B0319-02"); // IOArray.add("8B0319-03");
		* IOArray.add("n980101"); IOArray.add("n980102");
		* IOArray.add("n980103");
		* 
		* IOArray.add("n980104"); IOArray.add("n980105");
		* IOArray.add("n980106"); IOArray.add("n980107");
		* IOArray.add("n980108"); IOArray.add("n980109");
		* IOArray.add("n980110"); IOArray.add("n980111");
		* IOArray.add("n980112"); IOArray.add("n980113");
		* IOArray.add("n980114"); IOArray.add("n980115");
		* IOArray.add("n980116"); IOArray.add("n980117");
		* 
		* IOArray.add("n980118"); IOArray.add("n980119");
		* 
		* IOArray.add("n940201"); IOArray.add("n950201");
		* IOArray.add("n000250"); IOArray.add("n000251"); //
		* IOArray.add("7A0501-03"); // IOArray.add("7A0501-04"); //
		* IOArray.add("7A0501-05"); // IOArray.add("7A0501-06"); //
		* IOArray.add("7A0501-07"); // IOArray.add("7A0501-08"); //
		* IOArray.add("7A0501-09"); // IOArray.add("7A0502-01");
		* intent.putStringArrayListExtra("unitnumberarray",IOArray); }
		* randomRank = intent.getBooleanExtra("randomrank",true);
		* accurateSearch = intent.getBooleanExtra("accuratesearch",false);
		* MAXQUESNUM = intent.getIntExtra("shownum",MAXQUESNUM); dbpath =
		* intent.getStringExtra("dbpath"); tag = intent.getStringExtra("tag");
		* isExam = intent.getBooleanExtra("exam",true); isDisplayResolving =
		* intent.getBooleanExtra("displayresolving",true);
		* 
		* // BestaTitleBar.clear(this); if(tag == null) { tag = ""; //
		* BestaTitleBar.set(this,getPackageName(),
		* getString(R.string.app_name), null); } else
		* if(BASETEST_TAG.contains(tag)) { //
		* BestaTitleBar.set(this,getPackageName(),
		* getString(R.string.BaseTest), null); } if(dbpath==null) { dbpath =
		* "/sdcard/english/KE.db"; if(!new File(dbpath).exists()) { dbpath =
		* "/besta/data/com.besta.app.knowledge.exercise/KE.db"; } } else {
		* if(new File("/sdcard"+dbpath).exists()) { dbpath = "/sdcard"+dbpath;
		* } } // historyRankList = new String[MAXQUESNUM];
		* 
		* File tempPath = null; tempPath = new File(dbpath);
		* if(!tempPath.exists()) {
		* Toast.makeText(getApplicationContext(),getString(R.string.Notfound),
		* Toast.LENGTH_SHORT).show(); // finish(); // return; } tempPath = new
		* File(pathHistory); if(!tempPath.exists()) {
		* System.out.println("SDCARD: NO history!"); tempPath.mkdirs(); }
		* tempPath = new File(pathtemp,"temp"); deleteFile(tempPath);
		* tempPath.mkdirs();
		* 
		* System.out.println("[Intent]Exercise get packagename:"+dbpath);
		* System.out.println("[Intent]If accurate search:"+ accurateSearch);
		* System.out.println("[Intent]Show num:"+ MAXQUESNUM);
		* System.out.println("[Intent]DATABASE PATH:" + dbpath);
		* 
		* try { RandomAccessFile pHtmlFile = new RandomAccessFile(pathtemp+
		* File.separator+"temp"+ File.separator +"kpexercise.html","rw");
		* pHtmlFile.write(0xFF); pHtmlFile.write(0xFE); pHtmlFile.close(); }
		* catch (IOException e1) { // TODO Auto-generated catch block
		* e1.printStackTrace(); }
		**/
		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if (quesFlag == false) {
					Toast.makeText(getApplicationContext(),
					getString(R.string.Notfound), Toast.LENGTH_SHORT)
					.show();
					finish();
				} else {
					
					// Debug by Taylor
					String fileName = new String("js_math_test.html");
					String newFilePath = new String("");
					String cacheDir = getCacheDir().toString();
					
					newFilePath = cacheDir + File.separator + fileName;
					webview.loadUrl("file://" + newFilePath);
					mLoadingLayout = (LinearLayout)
					findViewById(R.id.fullscreen_loading_style);
					mLoadingLayout.setVisibility(View.GONE);
					
					LinearLayout mMain = (LinearLayout)
					findViewById(R.id.main_info);
					mMain.setVisibility(View.VISIBLE);
					mMain.requestFocus();
					// Debug End
					
					// String baseUrl = "file://" + pathtemp + "/temp/";// *************
					// // webview.loadDataWithBaseURL(baseUrl,
					// // pHtmlString.toString(), "text/html", "utf-8", null);

					// // webview.reload();
					// // Log.d("SystemClock Thread Out",SystemClock.uptimeMillis()+"");
					// // Log.d("SystemClock Count",""+(SystemClock.uptimeMillis()-Inclock));
				}
				// prgdlg.dismiss();
				if (msg.what == 1) {
					mLoadingLayout = (LinearLayout) findViewById(R.id.fullscreen_loading_style);
					mLoadingLayout.setVisibility(View.GONE);

					LinearLayout mMain = (LinearLayout) findViewById(R.id.main_info);
					mMain.setVisibility(View.VISIBLE);
					mMain.requestFocus();
					// mMain.setFocusableInTouchMode(true);
				}
			}
		};
		th = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(100); // if this is no sleep or less than
					// 100,the webview may show uncomplete
					// content
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// // 服務器查找時調用
				// getQuesNum();// find questions!!
				// Debug
				String filePath = new String("test_data");
				String newFilePath = new String("");
				AssetManager am = getResources().getAssets();
				try {
					String fileName[] = am.list(filePath);
					for (int i = 0; i < fileName.length; i++) {
						String cacheDir = getCacheDir().toString();
						InputStream is = am.open(filePath + File.separator
						+ fileName[i]);
						newFilePath = cacheDir + File.separator + fileName[i];
						File file = new File(cacheDir);
						file.mkdirs();
						file = new File(newFilePath);
						if (file.exists()) {
							file.delete();
						}
						RandomAccessFile rf = new RandomAccessFile(file, "rw");
						byte[] tmpBytes = new byte[1024];
						while (is.read(tmpBytes) != -1) {
							rf.write(tmpBytes);
							tmpBytes = null;
							tmpBytes = new byte[1024];
						}
						is.close();
						rf.close();
					}
					
					quesFlag = true;
					pHtmlString = new StringBuffer();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				// mMain.setFocusableInTouchMode(true);
				// Debug End
				Message msg = handler.obtainMessage(1);
				handler.sendMessage(msg);
			}
		});

		webview = (WebView) findViewById(R.id.webyytk);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setDefaultFontSize(DEFAULTFONTSIZE);
		webview.addJavascriptInterface(new JavaScriptInterfaceES(this), "jsio");
		// webview.requestFocusFromTouch();
		webviewOverride();
		webviewOnTouch();

		// ////////////////////////////////////////////////////////////////////////////////////
		// WebSettings.ZoomDensity zoomDensity = WebSettings.ZoomDensity.CLOSE ;
		// webview.getSettings().setDefaultZoom(zoomDensity);//webSettings.setDefaultZoom(zoomDensity);
		// ////////////////////////////////////////////////////////////////////////////////////
		int screenDensity = getResources().getDisplayMetrics().densityDpi;
		WebSettings.ZoomDensity zoomDensity = WebSettings.ZoomDensity.MEDIUM;

		switch (screenDensity) {

		case DisplayMetrics.DENSITY_LOW:
			zoomDensity = WebSettings.ZoomDensity.CLOSE;
			break;

		case DisplayMetrics.DENSITY_MEDIUM:
			zoomDensity = WebSettings.ZoomDensity.CLOSE;
			break;

		case DisplayMetrics.DENSITY_HIGH:
			zoomDensity = WebSettings.ZoomDensity.MEDIUM;
			break;
		}
		webview.getSettings().setDefaultZoom(zoomDensity);// webSettings.setDefaultZoom(zoomDensity);
		// ///////////////////////////////////////////////////////////////////////////////////

		// WebViewConfig.useSelectionMenu(webview, false);//used to change the
		// pegging UI
		// webview.setOnTouchListener(new OnMyTouchListener());
		Inclock = SystemClock.uptimeMillis();
		Log.d("SystemClock Thread In", Inclock + "");
		th.start();
	}

	public void webviewOnTouch() {
		webview.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					X = event.getX();
					Y = event.getY();
					// setting.ChosStartAnswerPaper(testFirstActivity.this, 2);
					break;
				case MotionEvent.ACTION_MOVE:

					break;
				case MotionEvent.ACTION_UP:
					break;
				}
				return false;
			}
		});
	}
	/**
	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		if (VERSION.SDK_INT > 10) {
			menu.add(0, 0, 0, getString(R.string.Draft_note))
					.setIcon(R.drawable.ic_menu_compose)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			menu.add(0, 1, 0, "提交").setShowAsAction(
					MenuItem.SHOW_AS_ACTION_ALWAYS);
		} else {
			menu.add(0, 0, 0, getString(R.string.Draft_note)).setIcon(
					R.drawable.ic_menu_compose);
			menu.add(0, 1, 0, "提交");
		}
		return true;
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setAction("com.besta.app.memo.DrawActivity");
		if (getPackageManager().queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY).size() > 0) {
			menu.getItem(0).setVisible(true);
		} else {
			menu.getItem(0).setVisible(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case 0:
			WhiteBoard.CallBoard(this, getPackageName(),
					getString(R.string.app_name), true);
			break;
		case 1: {
			Builder dlg = new AlertDialog.Builder(this);
			dlg.setTitle("提交");
			dlg.setMessage("确认提交并退出吗？");
			dlg.setPositiveButton(getResources().getString(R.string.menu_ok),

			new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					startSaveAnswers("");
					finish();
				}
			});
			dlg.setNegativeButton(getResources()
					.getString(R.string.menu_cancel), null);
			dlg.show();
		}
			break;
		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
**/
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		// TODO Auto-generated method stub
		
		// String result_pic_path = setting.GetResultBackgroundPic();
		switch(requestCode)
		{
		case RequestCode.MENU_SELECTOR:
			{
				switch(resultCode)
				{
				case ResultCode.SHUTDOWN:
					{
						Intent intent = new Intent();
						intent.setComponent(new ComponentName("com.besta.app.syncoursescontrol.main",                         "com.besta.app.syncoursescontrol.main.HDMSBookSelect"));
						startActivity(intent);
						finish();
					}
				case ResultCode.MENU_SELECTOR:
					{
						initActivity(data);
					}
					break;
				default:
					break;
				}
			}	
			break;
		case RequestCode.ANSWER_PAPER:
			{
				setting.AnswerActivityResult(this, requestCode, resultCode, data);
				AnswerResultHtmlData = setting.GetAnswerResult();
				String AnsId = setting.answer_id;
				String Url = AnsId;

				// int KIND_OF_ANSPAPER = 0;
				if (KIND_OF_ANSPAPER == StartAnswerPaperSetting.KIND_DLG) 
				{
					webview.loadUrl("javascript:replaceContent('" + AnsId + "','" + Url + "')");
				} 
				else if (KIND_OF_ANSPAPER == StartAnswerPaperSetting.KIND_NORMAL) 
				{
					webview.loadUrl("javascript:replaceContent('" + AnsId + "','" + Url + "')");
				} 
				else if (KIND_OF_ANSPAPER == StartAnswerPaperSetting.KIND_TEST)
				{
					// webview.loadUrl("javascript:displayhiddenContent('"
					// +AnsId+"','"+Url+"')");
				}

				// webview.loadUrl("javascript:replaceContent('" +AnsId+"','"+Url+"')");
				// webview.loadUrl("javascript:displayhiddenContent('"
				// +AnsId+"','"+Url+"')");
			}	
			break;
		default:
			break;
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	public void webviewOverride() {
		webview.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				if(Action.equals(ACTION_BROWSE))
				{

				}
				else if(Action.equals(ACTION_TEST))
				{

				}
				else if(Action.equals(ACTION_EXAM))
				{
					Button ButtonSubmit	= (Button)findViewById(R.id.btn_submit);
					ButtonSubmit.setVisibility(View.VISIBLE);
					//					Button ButtonSubmit	= (Button)findViewById(R.id.btn_submit);
					//					ButtonSubmit.setEnabled(true);
				}

			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				// return super.shouldOverrideUrlLoading(view, url);
				if (url.contains(KNOWLEDGE_TAG)) {
					startKnowledge(url);
				} else if (url.contains(FLASH)) {
					//					startFlash(url);
				} else if (url.contains(SUBMIT_ANSWERS)) {
					startSaveAnswers(url);
					finish();
				} else if (url.contains(ANSPAPER_TAG)) {
					startAnswerPaper(url);
				} else {
					//					 System.out.println("getUrl:"+url);
					//					 webview.loadDataWithBaseURL(baseUrl,htmlReader(url,"Unicode"),
					//					 "text/html", "utf-8",url);
				}
				return true;
			}
		});
	}

	public void startKnowledge(String url) {
		ArrayList<String> IOArray = new ArrayList<String>();
		String KnowledgeName[] = null;
		url = "|" + url.substring(KNOWLEDGE_TAG.length()) + "|";
		KnowledgeName = getMidStringAll(url, "|", "|");
		for (int i = 0; i < KnowledgeName.length; i++) {
			IOArray.add(KnowledgeName[i]);
		}
		Intent intent = new Intent();
		intent.putStringArrayListExtra("unitnumberarray", IOArray);
		intent.setClass(this, PopupList.class);
		this.startActivity(intent);
	}

	public void startSaveAnswers(String url) {
		
		Intent intent = new Intent();
		String JSON = questionArray.getDataByJson();
		intent.putExtra("resultjson",JSON);
		System.out.println(JSON);
		this.setResult(RESULT_OK, intent);
		
		// Thread th = new Thread(new Runnable() {
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		

		/**		
		try {
//			JSONArray jsonAr = null;
			// String loaclPath = "sdcard/besta";
			// String loaclName = "ExerciseResults.json";
			// WriteAndReadText myWriteAndReadText = new WriteAndReadText();
			// myWriteAndReadText.setTextPath(loaclPath);
			// String json = myWriteAndReadText.readText(loaclName);
			JSONArray jsonAr = new JSONArray();

			for (int i = 0; i < questions.length; i++) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("ID", questions[i].ID);
				jsonObj.put("Answer", questions[i].Result);
				jsonAr.put(jsonAr.length(), jsonObj);
			}
			Intent intent = new Intent();
			// intent.put
			intent.putExtra("resultjson", jsonAr.toString());

			String JSON = jsonAr.toString();
			JSONTokener jsonParser = new JSONTokener(JSON);
			JSONArray jsonNArray = (JSONArray) jsonParser.nextValue();
			for (int i = 0; i < jsonNArray.length(); i++) {
				JSONObject jsonObject = (JSONObject) jsonNArray.get(i);
				System.out.println("[JSON]" + jsonObject.getString("ID"));
				System.out.println("[JSON]" + jsonObject.getString("Answer"));
			}

			this.setResult(RESULT_OK, intent);
			// myWriteAndReadText.writeText(loaclName,jsonAr.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// }
		// });
		// th.start();
**/

	}

	public void startAnswerPaper(String url) {
		// String ID = getMidStringAll(url, "&ID=","&");

		// String ID = url.substring(url.indexOf("&ID=")+new
		// String("&ID=").length(),url.indexOf("&",url.indexOf("&ID=")+new
		// String("&ID=").length()));
		String ID = subStringByFrist(url, "&ID=", "&");
		int NUM = Integer.valueOf(subStringByFrist(url, "&NUM=", null));

		StringBuffer sendData = new StringBuffer();

		sendData = sendData.append("<html>");
		sendData = sendData.append("<head>");
		sendData = sendData.append("<style type=\"text/css\">");
		// sendData = sendData.append("img {	max-width: 100%;}");
		sendData = sendData.append(getAssetsString("button.css"));
		sendData = sendData.append(getAssetsString("list.css"));
		sendData = sendData.append("img{max-width:90%;}");
		sendData = sendData
		.append("center{font-size:130%;margin:10px 0px 10px 0px;}");
		sendData = sendData.append("*{ padding:0; margin:0px 2px 0px 2px;}");
		sendData = sendData
		.append(".examname{font-size:180%;margin:10px 0px 10px 0px;}");
		sendData = sendData
		.append(".title{font-size:110%;margin:10px 0px 10px 0px;}");

		// sendData =
		// sendData.append(".list{ padding:0; margin:10px -30px 15px 0px;}");
		sendData = sendData.append("li{ padding:0; margin:5px 0px 10px 35px;}");
		sendData = sendData
		.append(".subli{ padding:0; margin:5px 0px 10px 30px;}");
		sendData = sendData.append("</style>");
		sendData = sendData.append("<script>");
		sendData = sendData.append("var tempPath = \"" + assetPath + "\"");
		sendData = sendData.append(getAssetsString("kpexercise.js"));
		// sendData = sendData.append(new
		// String(reader,Charset.forName("UnicodeLittle")));
		sendData = sendData.append("</script>");
		sendData = sendData.append("</head>");
		sendData = sendData.append("<body>");
		sendData = sendData.append("<ul class=\"decimal\">");

		// pHtmlString.indexOf(HtmlSign.QUES_START(ID, NUM))+new
		// String(HtmlSign.QUES_START(ID, NUM));
		for (int i = 1; i <= NUM; i++) {
			sendData = sendData.append(subStringByFrist(pHtmlString.toString(),
			HtmlSign.QUES_START(ID, i), com.besta.app.exerciseengine.quesprocess.HtmlSign.QUES_END()));
		}

		sendData = sendData.append("</ul>");
		sendData = sendData.append("</body>");
		sendData = sendData.append("</html>");
		String sendDataString = deleteStringByAll(sendData.toString(),
		HtmlSign.ANS_START, HtmlSign.ANS_END);
		
		// Debug by Taylor
		sendDataString = "";
		String fileName = new IdConvert(this, HtmlSign.QUES_LOCAL_ID(ID,
		NUM))
		.getAbsolutePath();
		File myReadFile = new File(fileName);
		fileName = myReadFile.getParent().toString();
		sendDataString = fileName;
		byte[] nameBytes = fileName.getBytes();
		int i = 0;
		for (i = nameBytes.length - 1; i >= 0; i--) {
			if ('/' == nameBytes[i]) {
				break;
			}
		}
		sendDataString = sendDataString.substring(i + 1) + ".html";
		// Debug End

		// int KIND_OF_ANSPAPER = 0;
		if (url.contains(ExerciseMain.ANSPAPER_TYPE_ANSWERDLG)) {
			KIND_OF_ANSPAPER = StartAnswerPaperSetting.KIND_DLG;
		} else if (url.contains(ExerciseMain.ANSPAPER_TYPE_ANSWERNORMAL)) {
			KIND_OF_ANSPAPER = StartAnswerPaperSetting.KIND_NORMAL;
		} else if (url.contains(ExerciseMain.ANSPAPER_TYPE_TESTDLG)) {
			KIND_OF_ANSPAPER = StartAnswerPaperSetting.KIND_TEST;
		}
		setting = new StartAnswerPaperSetting(this);
		setting.ShowAnswerDlgActivityPos((int) X, (int) Y);

		// setting real answer paper size
		setting.ShowAnswerPaperSize(setting.VIEW_WIDTH, 2 * setting.VIEW_HEIGHT);
		// ID+"|"+NUM
		setting.ChosStartAnswerPaper(this, HtmlSign.QUES_LOCAL_ID(ID, NUM),
		sendDataString, KIND_OF_ANSPAPER);
	}

	//	 public void updateFrameCache(){
	//	 webview.postDelayed(new Runnable(){
	//	 public void run() {
	//	 try {
	//	 Class<?> webViewCore = Class.forName("android.webkit.WebViewCore");
	//	 java.lang.reflect.Field instance;
	//	 instance = WebView.class.getDeclaredField("mWebViewCore");
	//	 instance.setAccessible(true);
	//	 Object last = instance.get(webview);
	//	 Method update = webViewCore.getDeclaredMethod("nativeUpdateFrameCache",
	//	 new Class[0]);
	//	 update.setAccessible(true);
	//	 if(last != null && update != null)
	//	 {
	//	 update.invoke(last, new Object[0]);
	//	 }
	//	 } catch (Exception e) {
	//	 // TODO Auto-generated catch block
	//	 e.printStackTrace();
	//	 }
	//	 }
	//	 }, 1000);
	//	 // Toast.makeText(getApplicationContext(),"UPDATE!",
	//	 Toast.LENGTH_SHORT).show();
	//	 }
	//	 public void getAnsFormJs(String ans,int whichSelect,String value)
	//	 {
	//	 System.out.println("[ID:"+ans+"]"+"[Select:"+whichSelect+"]"+"[TF:"+value+"]");
	//	 // char selectChar = (char)('A'+whichSelect);
	//	 //
	//	 // for(int i = 0;i<questions.length;i++)
	//	 // {
	//	 // if(questions[i].ID.equals(ans))
	//	 // {
	//	 // questions[i].Result = Character.toString(selectChar);
	//	 // }
	//	 // }
	//	 }

	public void getQuesNum() {
		
		File pHtmlFileDel = new File(pathtemp + File.separator + "temp"
		+ File.separator + "kpexercise.html");
		pHtmlFileDel.delete();
		/*****************************************************************************/
		pHtmlString = new StringBuffer();
		pHtmlString = pHtmlString.append("<html>");
		pHtmlString = pHtmlString.append("<head>");
		pHtmlString = pHtmlString.append("<style type=\"text/css\">");
		//		pHtmlString = pHtmlString.append(getAssetsString("button.css"));
		pHtmlString = pHtmlString.append(getAssetsString("list.css"));
		pHtmlString = pHtmlString.append("img{max-width:90%;}");
		pHtmlString = pHtmlString.append("center{font-size:130%;margin:10px 0px 10px 0px;}");
		pHtmlString = pHtmlString.append("li{ padding:0; margin:5px 5px 10px 45px;}");
		pHtmlString = pHtmlString.append("*{ padding:0; margin:0px 0px 0px 0px;}");
		pHtmlString = pHtmlString.append(".btn{margin:0px 2px 0px 2px;}");
		pHtmlString = pHtmlString.append(".examname{font-size:180%;margin:10px 0px 10px 0px;}");
		pHtmlString = pHtmlString.append(".title{font-size:110%;margin:10px 5px 10px 10px;}");
		pHtmlString = pHtmlString.append(".subli{ padding:0; margin:5px 5px 10px 40px;}");
		pHtmlString = pHtmlString.append("</style>");
		pHtmlString = pHtmlString.append("<script>");
		pHtmlString = pHtmlString.append("var tempPath = \"" + assetPath + "\"");
		pHtmlString = pHtmlString.append(getAssetsString("kpexercise.js"));
		pHtmlString = pHtmlString.append("</script>");
		pHtmlString = pHtmlString.append("</head>");
		pHtmlString = pHtmlString.append("<body>");
		/*****************************************************************************/
		// /////////////////////////////////////////////////////////////////////////////////////

		// SQLiteDatabase db = null;
		// String FileName = "History.db";
		// File Historyfile = getFilesDir();
		// if(Historyfile.exists())
		// {
		// db = SQLiteDatabase.openOrCreateDatabase(new
		// File(Historyfile,FileName), null);
		// }
		// quesFlag = true;
		// ArrayList<String> IOArray = new ArrayList<String>();
		// IOArray = intent.getStringArrayListExtra("unitnumberarray");
		// String[] QuesList = IOArray.toArray(new String[0]);

		/******************************* Read Data ****************************************/
		//		Debug.startMethodTracing("ESDebug");

		if(CallingBuffer!=null)
		{
			pHtmlString = pHtmlString.append("<ul class=\"decimal\">");
			GetDataFromLocalCalling myGetDataFromLocalCalling = new GetDataFromLocalCalling(this);
			myGetDataFromLocalCalling.getQuestion(CallingBuffer);
			StringBuffer QuesBuffer = new StringBuffer();
			pHtmlString = pHtmlString.append(QuesBuffer = myGetDataFromLocalCalling.QuesBuffer);
			pHtmlString = pHtmlString.append("</ul>");
			if (QuesBuffer.length() > 0) {
				quesFlag = true;
			}
		}
		else
		{
			pHtmlString = pHtmlString.append("<ul class=\"decimal\">");
			GetDataFromHttpGet myGetDataFromHttpGet = new GetDataFromHttpGet(this);
			myGetDataFromHttpGet.getExamPaper(ExamValue);
			StringBuffer QuesBuffer = new StringBuffer();
			pHtmlString = pHtmlString.append(QuesBuffer = myGetDataFromHttpGet.QuesBuffer);
			pHtmlString = pHtmlString.append("</ul>");
			if (QuesBuffer.length() > 0) {
				quesFlag = true;
			}
		}

		//        Debug.stopMethodTracing();
		// /******************************* Read Data
		// ****************************************/
		// pHtmlString = pHtmlString.append("<ol>");
		// GetDataFromLocalDB myGetDataFromLocalDB = new GetDataFromLocalDB();
		// myGetDataFromLocalDB.setAccurateSearch(true);
		// questions = myGetDataFromLocalDB.getQuestions(dbpath, QuesList);
		// /******************************* Rank Data
		// ****************************************/
		//
		// // RankQuestions myRankQuestions = new RankQuestions();
		// // String[] rankList =
		// myRankQuestions.RankByTF(this,IOArray.toArray(new String[0]), true);
		// /******************************* Create Html
		// **************************************/
		//
		// for(int i = 0;i<(questions.length<20?questions.length:20);i++)
		// {
		// myGetDataFromLocalDB.insertImageBase64(dbpath, questions[i],
		// "IMAGEDATA");
		// }
		// CreateHtml myCreateHtml = new CreateHtml();
		// myCreateHtml.setDisplayResolving(isDisplayResolving);
		// pHtmlString = pHtmlString.append(myCreateHtml.createHtml(this,
		// questions,20));
		// pHtmlString =
		// pHtmlString.append("<a href=\""+SUBMIT_ANSWERS+"\"><input type=\"button\" value=\"Submit\" class=\"btn4\"></a>");
		// pHtmlString = pHtmlString.append("</ol>");
		// /*****************************************************************************/

		// pHtmlString =
		// pHtmlString.append("<a href=\""+SUBMIT_ANSWERS+"\"><input type=\"button\" value=\"Submit\" class=\"btn4\"></a>");
		// pHtmlString = pHtmlString.append("</ol>");
		pHtmlString = pHtmlString.append("</body>");
		pHtmlString = pHtmlString.append("</html>");
	}

	/**
* 
		直接顯示答案和解析？ key:"isAnswerAndExplainExpanded"/value:boolean
		計時器的時間值？ key:"examMilliseconds"/value:long
		錯題自動保存？ key:"wrongQuesAutoSave"/value:boolean
		提交后顯示題目相關知識點的視頻連結？ key:"showQuesVideoLinkAfterCommit"/value:boolean
		提交后打分？ key:"scoreAfterCommit"/value:boolean
		提交后評判？ key:"judgeAfterCommit"/value:boolean
		
		
		工具之計算器？ key:"isCalculatorEnable"/value:boolean
		工具之元素週期表？ key:"isPeriodicTableEnable"/value:boolean
		工具之牛津詞典？ key:"isOxfordDictEnable"/value:boolean
		工具之英漢詞典？ key:"isECDictEnable"/value:boolean
		工具之生詞本？ key:"isNewWordBookEnable"/value:boolean
		工具之現漢規範？ key:"isChineseDictEnable"/value:boolean
		工具之漢英詞典？ key:"isCEDictEnable"/value:boolean
		工具之歷年大事表？ key:"isKeyEventTableEnable"/value:boolean
		工具之學習筆記？ key:"isStudyNoteEnable"/value:boolean
		工具之同步教輔？ key:"isSyncTutorEnable"/value:boolean
		工具之視頻連結？ key:"isVideoLinkEnable"/value:boolean
		
		
		支持題目收藏？ key:"isQuesCollectEnable"/value:boolean
		支持章節選擇？ key:"isMenuSelectorEnable"/value:boolean
		打分后弱點分析？ key:"isAnalysisAfterScore"/value:boolean
		傳入的題目數據？ key:"quesBuffer"/value:String
		服務器得題參數？ key:"searchCondition"/value:String
		答題記錄保存時使用的關鍵字？ key:"recordKey"/value:String
		答題記錄保存的最大條數？ key:"recordKeyMax"/value:long
		此次載入第幾條記錄？ key:"recordIndex"/value:long
* @param intent
*/
	private void initState(Intent intent)
	{
		Action = intent.getAction();
		//		Action = ACTION_EXAM;
		isOpenPanel = intent.getBooleanExtra("IsopenPanel", false);
		isMenuSelectorEnable = intent.getBooleanExtra("isMenuSelectorEnable", false);
		isQuesCollectEnable = intent.getBooleanExtra("isQuesCollectEnable", false);
		
		ExamValue = intent.getStringExtra("searchCondition");
		/***********************************************************/
		if(ExamValue==null)
		{
			//			myGetDataFromHttpGet.getExamPaper("同步练习|化学|初中|人教版|null|null|null|null|null");
			//			myGetDataFromHttpGet.getExamPaper("历年真题|高考|语文|null|2009|null");
			ExamValue = "历年真题|中考|数学|上海|null|null";
			Action = ACTION_EXAM;
		}
		/***********************************************************/
		CallingBuffer = intent.getStringExtra("quesBuffer");
		
		String BookFileAddress = intent.getStringExtra("BookFileAddress");
		String CallType = intent.getStringExtra("CallType");
		
		final Intent MenuSelectIntent = new Intent();
		MenuSelectIntent.putExtra("BookFileAddress", BookFileAddress);
		MenuSelectIntent.putExtra("CallType", CallType);
		MenuSelectIntent.setComponent(new ComponentName("com.besta.app.SyncExercise","com.besta.app.SyncExercise.panelmain.Panelmain"));
		
		

		ImageButton ButtonCalculator	= (ImageButton)findViewById(R.id.btn_calculator);
		ImageButton ButtonHycd			= (ImageButton)findViewById(R.id.btn_hycd);
		ImageButton ButtonNjgj			= (ImageButton)findViewById(R.id.btn_njgj);
		ImageButton ButtonScb			= (ImageButton)findViewById(R.id.btn_scb);
		ImageButton ButtonScbsave		= (ImageButton)findViewById(R.id.btn_scbsave);
		ImageButton ButtonSpjf			= (ImageButton)findViewById(R.id.btn_spjf);
		ImageButton ButtonTbjf			= (ImageButton)findViewById(R.id.btn_tbjf);
		ImageButton ButtonTblx			= (ImageButton)findViewById(R.id.btn_tblx);
		ImageButton ButtonXhgf			= (ImageButton)findViewById(R.id.btn_xhgf);
		ImageButton ButtonXxbj			= (ImageButton)findViewById(R.id.btn_xxbj);
		ImageButton ButtonXxbjdetail	= (ImageButton)findViewById(R.id.btn_xxbjdetail);
		ImageButton ButtonYearstable	= (ImageButton)findViewById(R.id.btn_yearstable);
		ImageButton ButtonYhcd			= (ImageButton)findViewById(R.id.btn_yhcd);
		ImageButton ButtonYszqb			= (ImageButton)findViewById(R.id.btn_yszqb);
		Button MenuSelectButton 		= (Button)findViewById(R.id.btn_menu_select);
		MenuSelectButton.setVisibility(View.GONE);

		
		if(intent.getBooleanExtra("isCalculatorEnable",false))
		{
			ButtonCalculator.setVisibility(View.VISIBLE);
		}
		if(intent.getBooleanExtra("isPeriodicTableEnable",false))
		{
			ButtonHycd.setVisibility(View.VISIBLE);
		}
		if(intent.getBooleanExtra("isOxfordDictEnable",false))
		{
			ButtonNjgj.setVisibility(View.VISIBLE);
		}
		if(intent.getBooleanExtra("isECDictEnable",false))
		{
			ButtonScb.setVisibility(View.VISIBLE);
		}
		if(intent.getBooleanExtra("isNewWordBookEnable",false))
		{
			ButtonSpjf.setVisibility(View.VISIBLE);
		}
		if(intent.getBooleanExtra("isChineseDictEnable",false))
		{
			ButtonTbjf.setVisibility(View.VISIBLE);
		}
		if(intent.getBooleanExtra("isCEDictEnable",false))
		{
			ButtonTblx.setVisibility(View.VISIBLE);
		}
		if(intent.getBooleanExtra("isKeyEventTableEnable",false))
		{
			ButtonXhgf.setVisibility(View.VISIBLE);
		}
		if(intent.getBooleanExtra("isStudyNoteEnable",false))
		{
			ButtonXxbj.setVisibility(View.VISIBLE);
		}		
		if(intent.getBooleanExtra("isSyncTutorEnable",false))
		{
			ButtonXxbjdetail.setVisibility(View.VISIBLE);
		}
		if(intent.getBooleanExtra("isVideoLinkEnable",false))
		{
			ButtonYearstable.setVisibility(View.VISIBLE);
		}
		if(isOpenPanel)
		{
			startActivityForResult(MenuSelectIntent, RequestCode.MENU_SELECTOR);
		}
		if(isMenuSelectorEnable)
		{
			MenuSelectButton.setVisibility(View.VISIBLE);
			MenuSelectButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					try {
						startActivityForResult(MenuSelectIntent, RequestCode.MENU_SELECTOR);
					} catch (Error e) {
						Log.e("MenuSelectIntent", e.toString());
					}
				}
			});
		}


		if(ExamValue!=null)
		{
			try {
				if(Action.equals(ACTION_BROWSE))
				{

				}
				else if(Action.equals(ACTION_TEST))
				{

				}
				else if(Action.equals(ACTION_EXAM))
				{
					Button ButtonSubmit	= (Button)findViewById(R.id.btn_submit);
					ButtonSubmit.setVisibility(View.GONE);
					ButtonSubmit.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Button ButtonScore	= (Button)findViewById(R.id.btn_score);
							ButtonScore.setVisibility(View.VISIBLE);
							Button ButtonSubmit	= (Button)findViewById(R.id.btn_submit);
							ButtonSubmit.setVisibility(View.GONE);
							webview.loadUrl("javascript:displayhiddenByName('" + "scorebar" + "')");
							//							webview.loadUrl("javascript:displayhiddenByName_noblock('" + "resolve" + "')");
							webview.loadUrl("javascript:scroll(0,0)");
						}
					});
					Button ButtonScore	= (Button)findViewById(R.id.btn_score);
					ButtonScore.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							CountDialog.Builder myCdlg = new CountDialog.Builder(v.getContext());
							myCdlg.setScore(questionArray.getAllUserScore());
							myCdlg.setAnalysisButton(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									startSaveAnswers("");
									finish();
								}
							});
							myCdlg.create().show();
						}
					});
					//					MenuSelectButton.setVisibility(View.GONE);
					//					Button ButtonScore	= (Button)findViewById(R.id.btn_score);
					//					ButtonScore.setEnabled(false);
					//					Button ButtonSubmit	= (Button)findViewById(R.id.btn_submit);
					//					ButtonSubmit.setEnabled(false);
				}
			} catch (Error e) {
				Log.e("MenuSelectIntent", e.toString());
			}
		}
	}
	private void deleteFile(File file)// delete a file with all files in it
	{
		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					this.deleteFile(files[i]);
				}
			}
			file.delete();
		} else {
			// System.out.println("NOT FOUND!"+'\n');
		}
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

	public String subStringByFrist(String initiativeStr, String beginStr,
	String endStr) {
		String ret = "";
		if (!initiativeStr.contains(beginStr)) {
			return ret;
		}
		// if(initiativeStr.indexOf(endStr,initiativeStr.indexOf(beginStr)+beginStr.length())<0)
		// {
		// endStr = null;
		// }
		if (endStr == null || endStr.isEmpty()) {
			ret = initiativeStr.substring(initiativeStr.indexOf(beginStr)
			+ new String(beginStr).length(), initiativeStr.length());
		} else {
			ret = initiativeStr.substring(
			initiativeStr.indexOf(beginStr)
			+ new String(beginStr).length(),
			initiativeStr.indexOf(endStr,
			initiativeStr.indexOf(beginStr)
			+ new String(beginStr).length()));
		}
		return ret;
	}

	public String deleteStringByAll(String initiativeStr, String beginStr,
	String endStr) {
		String ret = initiativeStr;
		while (ret.contains(beginStr)) {
			String deleteString = ret.substring(
			ret.indexOf(beginStr),
			ret.indexOf(endStr, ret.indexOf(beginStr))
			+ endStr.length());
			ret = ret.replace(deleteString, "");
		}
		return ret;
	}

	public String getAssetsString(String fileName) {
		String ret = "";
		try {
			InputStream reader = getAssets().open(fileName);
			byte buffer[] = new byte[reader.available()];
			reader.read(buffer);
			ret = new String(buffer, Charset.forName("UnicodeLittle"));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
}
