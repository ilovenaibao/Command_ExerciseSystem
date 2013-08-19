package com.besta.app.exerciseengine;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.nio.charset.Charset;
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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.ClipboardManager;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.besta.util.config.WebViewConfig;
import com.besta.util.titlebar.BestaTitleBar;
import com.besta.view.crosssearchwin.CrossSearchInfo;
import com.besta.view.crosssearchwin.RetrievalPopUpWindow;
import com.will.tools.WriteAndReadText;

@SuppressWarnings("deprecation")
public class kpexercise extends Activity {

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
	
	
	public static final String SUBMIT_ANSWERS = "submitanswers";
	public static final String BASETEST_TAG = "basetest";
	public static final String KNOWLEDGE_TAG = "knowledge:";
	public static final String FLASH = "flash:";
	
	
	public static final String SDCARD = Environment.getExternalStorageDirectory().getPath();
	private Thread th = null;
    private LinearLayout mLoadingLayout;
    private Context context;
	int MAXQUESNUM = 99;
	
	StringBuffer pHtmlString = new StringBuffer();
	String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
	String dbpath = null;
	String DBNAME = null;//database name
	String tag = "";
	String path = "";
	String pathtemp = "/data/data/" + "com.besta.app.knowledge.exercise" + "/files";
	String pathHistory = "/data/data/" + "com.besta.app.knowledge.exercise" + "/files/history";
	String assetPath = "file:///android_asset/";
	WebView webview;
	String mSelectedText;
	String historyRankList[];
	String flashNameTemp = null;
	String updateFlashId[];
	String quesList[];
	updateHandler myupdateHandler = new updateHandler();
	RetrievalPopUpWindow mPopUpWindow;
	ProgressDialog  prgdlg;
	Handler progressHandler;
	boolean accurateSearch = false;	
	boolean quesFlag = false;
	boolean randomRank = true;
    long Inclock = 0;
    
	class Ques{
		String Id;
		int history;
	}
    /** Called when the activity is first created. */
    @SuppressLint("SetJavaScriptEnabled")
	@Override
    public void onCreate(Bundle savedInstanceState) {

//    	SocketSender mySocketSender = new SocketSender();
//    	mySocketSender.sendData();
        super.onCreate(savedInstanceState);
        kpexercise.this.requestWindowFeature(Window.FEATURE_NO_TITLE);	
        setContentView(R.layout.exercise);   
        Intent intent = getIntent();
        if(intent.getStringArrayListExtra("unitnumberarray")==null)
        {
        	intent.putExtra("randomrank",false);
			intent.putExtra("accuratesearch",true);
			intent.putExtra("tag",BASETEST_TAG);
			intent.putExtra("dbpath","/sdcard/besta/com.besta.app.basetest.naturalscience/KE.db");
			intent.putExtra("shownum",10);
	    	ArrayList<String> IOArray = new ArrayList<String>();
//    		IOArray.add("7B-01");
//    		IOArray.add("7B-01");
//    		IOArray.add("7A030601");
//	    	IOArray.add("PC0101");
//	    	IOArray.add("8A0001-01");
//	    	IOArray.add("8A0004-07");
//	    	IOArray.add("9B0219-02");
//	    	IOArray.add("9B0219-03");
//	    	IOArray.add("8B0319-04");
//	    	IOArray.add("8B0318-01");
//	    	IOArray.add("8B0318-02");
//	    	IOArray.add("8B0318-03");
//	    	IOArray.add("8B0318-04");
//	    	IOArray.add("8B0318-05");
//	    	IOArray.add("8B0318-06");
//	    	IOArray.add("8B0318-07");
//	    	IOArray.add("8B0319-01");
//	    	IOArray.add("8B0319-02");
//	    	IOArray.add("8B0319-03");
	    	IOArray.add("n980139");
//	    	IOArray.add("n940201");
//	    	IOArray.add("n950201");
//	    	IOArray.add("n000250");
//	    	IOArray.add("n000251");
//	    	IOArray.add("7A0501-03");
//	    	IOArray.add("7A0501-04");
//	    	IOArray.add("7A0501-05");
//	    	IOArray.add("7A0501-06");
//	    	IOArray.add("7A0501-07");
//	    	IOArray.add("7A0501-08");
//	    	IOArray.add("7A0501-09");
//	    	IOArray.add("7A0502-01");
	    	intent.putStringArrayListExtra("unitnumberarray",IOArray);
        } 
        randomRank = intent.getBooleanExtra("randomrank",true);
        accurateSearch = intent.getBooleanExtra("accuratesearch",false);
        MAXQUESNUM = intent.getIntExtra("shownum",MAXQUESNUM);
        dbpath = intent.getStringExtra("dbpath");
        tag = intent.getStringExtra("tag");
        
        BestaTitleBar.clear(this);
        if(tag == null)
        {
        	tag = ""; 	
        	BestaTitleBar.set(this,getPackageName(), getString(R.string.app_name), null);
        }
        else if(BASETEST_TAG.contains(tag))
        {
        	BestaTitleBar.set(this,getPackageName(), getString(R.string.BaseTest), null);
        }
        if(dbpath==null)
        {
        	dbpath = "/sdcard/english/KE.db";
        	if(!new File(dbpath).exists())
        	{
        		dbpath = "/besta/data/com.besta.app.knowledge.exercise/KE.db";
        	}
        }
        else
        {
        	if(new File("/sdcard"+dbpath).exists())
        	{
        		dbpath = "/sdcard"+dbpath;
        	}	
        }
        historyRankList = new String[MAXQUESNUM];
   
        File tempPath = null;
        tempPath = new File(dbpath);
        if(!tempPath.exists())
        {        	
        	Toast.makeText(getApplicationContext(),getString(R.string.Notfound), Toast.LENGTH_SHORT).show();	    	
        	finish();
        	return;
        }   
        tempPath = new File(pathHistory);
        if(!tempPath.exists())
        {
        	System.out.println("SDCARD: NO history!");
            tempPath.mkdirs();
        } 
        tempPath = new File(pathtemp,"temp");
        deleteFile(tempPath);
        tempPath.mkdirs();

        System.out.println("[Intent]Exercise get packagename:"+dbpath);
        System.out.println("[Intent]If accurate search:"+ accurateSearch);
        System.out.println("[Intent]Show num:"+ MAXQUESNUM);
        System.out.println("[Intent]DATABASE PATH:" + dbpath);

        try {
        	RandomAccessFile pHtmlFile = new RandomAccessFile(pathtemp+ File.separator+"temp"+ File.separator +"kpexercise.html","rw");     
        	pHtmlFile.write(0xFF);
			pHtmlFile.write(0xFE);		
        	pHtmlFile.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        final Handler handler = new Handler(){
        	
             @Override
             public void handleMessage(Message msg) {
                  // TODO Auto-generated method stub
                super.handleMessage(msg);        
          	    if(quesFlag == false)
        	    {
        	    	Toast.makeText(getApplicationContext(),getString(R.string.Notfound), Toast.LENGTH_SHORT).show();
        	    	finish();        	    	
        	    }
          	    else
          	    {
          	    	String baseUrl = "file://" + pathtemp + "/temp/";//*************
          	    	webview.loadDataWithBaseURL(baseUrl,pHtmlString.toString(), "text/html","utf-8", null);
          	    	
//          	    	webview.reload();
//          	    	Log.d("SystemClock Thread Out",SystemClock.uptimeMillis()+""); 
//          	    	Log.d("SystemClock Count",""+(SystemClock.uptimeMillis()-Inclock));
          	    }
//                prgdlg.dismiss();  
                if (msg.what==1){
                    mLoadingLayout = (LinearLayout)findViewById(R.id.fullscreen_loading_style);
                    mLoadingLayout.setVisibility(View.GONE);
                    
                    LinearLayout mMain = (LinearLayout)findViewById(R.id.main_info);
                    mMain.setVisibility(View.VISIBLE);
                    mMain.requestFocus();
//                    mMain.setFocusableInTouchMode(true);
                } 
             }
        };      
        th = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub				
				  try {					  
					Thread.sleep(100);	//if this is no sleep or less than 100,the webview may show uncomplete content				
				  } catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				  }
				  getQuesNum();//find questions!!
	              Message msg = handler.obtainMessage(1);
	              handler.sendMessage(msg);
			} 
        });
        
        webview = (WebView)findViewById(R.id.webyytk); 
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDefaultFontSize(DEFAULTFONTSIZE);
        webview.addJavascriptInterface(this,"jsio");
//        webview.requestFocusFromTouch();
        webviewOverride();
//////////////////////////////////////////////////////////////////////////////////////
//		int screenDensity = getResources().getDisplayMetrics().densityDpi ;
//		WebSettings.ZoomDensity zoomDensity = WebSettings.ZoomDensity.MEDIUM ;
//
//		switch (screenDensity){   
//
//		case DisplayMetrics.DENSITY_LOW :   
//		    zoomDensity = WebSettings.ZoomDensity.CLOSE;   
//		    break;   
//
//		case DisplayMetrics.DENSITY_MEDIUM:   
//		    zoomDensity = WebSettings.ZoomDensity.MEDIUM;   
//		    break;   
//
//		case DisplayMetrics.DENSITY_HIGH:   
//		    zoomDensity = WebSettings.ZoomDensity.FAR;   
//		    break ;   
//		}   
//		webview.getSettings().setDefaultZoom(zoomDensity);//webSettings.setDefaultZoom(zoomDensity);
/////////////////////////////////////////////////////////////////////////////////////
        
        WebViewConfig.useSelectionMenu(webview, false);//used to change the pegging UI
        webview.setOnTouchListener(new OnMyTouchListener());
        Inclock = SystemClock.uptimeMillis();
        Log.d("SystemClock Thread In",Inclock+""); 
        th.start();
    }

    private class OnMyTouchListener implements OnTouchListener {
    	
      private boolean goMultipleSearch(View v, MotionEvent event){
      boolean ret = false;      
      if (event.getAction() == MotionEvent.ACTION_UP) {
            Class<?> webViewClass = v.getClass();
            Field selectionField = null;
            try {
                  selectionField = webViewClass.getDeclaredField("mSelectingText");
            } catch (SecurityException e1) {
                  e1.printStackTrace();
            } catch (NoSuchFieldException e1) {
                  e1.printStackTrace();
            }
            
            if (selectionField != null) {
                  selectionField.setAccessible(true);
                  try {
                        boolean touchSelection = selectionField.getBoolean(v);
                        if (touchSelection) {
                              Method selectionMethod = webViewClass.getDeclaredMethod("nativeGetSelection");
                              selectionMethod.setAccessible(true);
                              mSelectedText = (String) selectionMethod.invoke(v);
                              ClipboardManager clipboard = (ClipboardManager)kpexercise.this.getSystemService(Context.CLIPBOARD_SERVICE); 
                              CharSequence backupStr = clipboard.getText();
                              clipboard.setText("");
                              ret = v.onTouchEvent(event);
                              touchSelection = selectionField.getBoolean(v);
                              if (!touchSelection) 
                              {
//                                  System.out.println("selection end!");
//                                    mSelectedText = clipboard.getText().toString();
                                    if (mSelectedText.length() != 0)
                                    {
                                    	CrossSearchInfo crossSearchInfo = new CrossSearchInfo();
                                        crossSearchInfo.setContext(kpexercise.this);
		                                crossSearchInfo.setSearchContent(mSelectedText);
		                                crossSearchInfo.setSearchLayer(0);                                  
		                                mPopUpWindow = new RetrievalPopUpWindow(crossSearchInfo);
		                                PopupWindow temp = mPopUpWindow.getPopupWindow();
		                                if (temp != null) 
		                                {
		                                	temp.showAtLocation(v, Gravity.NO_GRAVITY, 8, 300);
		                                }
                                    }
                                    else
                                    {
                                        clipboard.setText(backupStr);
                                    }
                              }
                              else 
                              {
//                                    System.out.println("selection continued!");
                              }                            
                              return ret;
                        }
                  } catch (IllegalArgumentException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                  } catch (IllegalAccessException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                  } catch (SecurityException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                  } catch (NoSuchMethodException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                  } catch (InvocationTargetException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                  }
            }else {
                  ret = v.onTouchEvent(event);
            }
      }
      
    	  return ret;
      }    	
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
	      boolean ret = false;
	      ret = goMultipleSearch(v, event); 
	      return ret;	
		} 
    }
      
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0,0,0,getString(R.string.Draft_note)).setIcon(R.drawable.ic_menu_compose);
//		return super.onCreateOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setAction("com.besta.app.memo.DrawActivity");
		if(getPackageManager().queryIntentActivities(intent,PackageManager.MATCH_DEFAULT_ONLY).size()>0)
		{
			menu.getItem(0).setVisible(true);
		}
		else
		{
			menu.getItem(0).setVisible(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case 0:
			WhiteBoard.CallBoard(this,getPackageName(),getString(R.string.app_name),true);
			break;
		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
        File tempPath = new File(pathtemp,"temp");
 //       deleteFile(tempPath);
        deleteFile(tempPath);
		super.finish();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
        if (mPopUpWindow != null) {
//        	Toast.makeText(getApplicationContext(),"Stop Pronounce !", Toast.LENGTH_SHORT).show();
        	mPopUpWindow.stopPronounce();
          }
          super.onPause();
	}
	public void webviewOverride()
    {
    	webview.setWebViewClient(new WebViewClient(){

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
//				return super.shouldOverrideUrlLoading(view, url);
				if(url.contains(KNOWLEDGE_TAG))
				{
					startKnowledge(url);
				}
				else if(url.contains(FLASH))
				{
					startFlash(url);
				}
				else if(url.contains(SUBMIT_ANSWERS))
				{
					startSaveAnswers(url);
					finish();
				}
				else
				{
//					System.out.println("getUrl:"+url);
//					webview.loadDataWithBaseURL(baseUrl,htmlReader(url,"Unicode"), "text/html", "utf-8",url);
				}
				return true;
			}	    	
	    });
    }
    public void startKnowledge(String url)
    {
    	ArrayList<String> IOArray = new ArrayList<String>();
    	String KnowledgeName[]=null;
    	url = "|"+url.substring(KNOWLEDGE_TAG.length())+"|";
    	KnowledgeName = getMidStringAll(url,"|","|");
		for (int i = 0; i < KnowledgeName.length; i++) {
			IOArray.add(KnowledgeName[i]);
		}	
		Intent intent = new Intent();
		intent.putStringArrayListExtra("unitnumberarray",IOArray);
		intent.setClass(this,PopupList.class);
		this.startActivity(intent);
    }
    public void startFlash(String url)
    {
    	String flashPath = dbpath;
    	flashPath = flashPath.substring(0,flashPath.indexOf("KE.db"));
    	String flashName = url.substring(FLASH.length());
		String BFEPath = flashPath+"BFE"+File.separator;
		String BFESPath =  flashPath + flashName.substring(0,flashName.length()-2)+".bfes";
		File BFEfile = new File(BFEPath+flashName+".bfe");
		File BFESfile = new File(BFESPath);
		if(BFEfile.exists())
		{					
			PlayFlash.ShowFlash(this,BFEPath+flashName+".bfe");
		}
		else
		{
			boolean flashflag = false;
			if(BFESfile.exists())
			{					
				String flashname[] = PlayFlash.getBfsFilenames(BFESPath);			
				for(int i = 1;i<flashname.length;i++)
				{
					if(flashname[i].equals(flashName))
					{
						flashflag = true;
						PlayFlash.ShowPackFlash(this,BFESPath,flashname[i]);
						break;
					}
				}
			}
			if(!flashflag)
			{
				startFlashDownloadWithDialog(flashName);
//				Toast.makeText(getApplicationContext(),getString(R.string.Notfound), Toast.LENGTH_SHORT).show();
			}
		}
    }
    public void startSaveAnswers(String url)
    {
    	Thread th = new Thread(new Runnable() { 			
    		@Override
    		public void run() {
    				// TODO Auto-generated method stub
    			try {
	    			JSONArray jsonAr = null;
	    			String loaclPath = "sdcard/besta";
	    			String loaclName = "ExerciseResults.json";
	    			WriteAndReadText myWriteAndReadText = new WriteAndReadText();
	    			myWriteAndReadText.setTextPath(loaclPath);
//	    			String json = myWriteAndReadText.readText(loaclName);
    				jsonAr = new JSONArray();
    				
    				for(int i = 0;i<historyRankList.length;i++)
    				{
	    				JSONObject jsonObj  = new JSONObject();
	    				jsonObj.put("ID",historyRankList[i]);
	    				jsonObj.put("Answer","A");
	    				jsonAr.put(jsonAr.length(),jsonObj);
    				}
    				myWriteAndReadText.writeText(loaclName,jsonAr.toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	});
    	th.start();
    }
	public void startFlashDownloadWithDialog(String flashName)
	{
		flashNameTemp = flashName;
	//	final String path = flashPath+"BFE"+File.separator;
	//	final String name = flashName+".bfe";
		Builder dlg = new AlertDialog.Builder(this);
		dlg.setTitle(getResources().getString(R.string.menu_Instruction));
		dlg.setMessage(getResources().getString(R.string.menu_DownloadContent));
		dlg.setPositiveButton(getResources().getString(R.string.menu_ok),
	
				new DialogInterface.OnClickListener() {
	
					public void onClick(DialogInterface dialog,
							int which) {
				    	String flashPath = dbpath;

				    	flashPath = flashPath.substring(0,flashPath.indexOf("KE.db"));
				    	if(flashPath.contains("sdcard"))
				    	{
				    		flashPath = flashPath.substring(flashPath.indexOf("besta")+new String("besta").length());
				    	}
						
						String mPackagename = getPackageName();
						String path = "Besta/"+flashPath+"/BFE/";
						String name = flashNameTemp+".bfe";
						Intent intent = new Intent();
						intent.putExtra("packagename", mPackagename);
						intent.putExtra("storagepath", path);
						intent.putExtra("downloadonefile", name);
	//					intent.putExtra("packagename", "com.besta.app.bestajapanprimercourse");
	//					intent.putExtra("storagepath", path);
	//					intent.putExtra("downloadonefile", "JPBG010101.bfe");
						intent.setComponent(new ComponentName(
										"com.besta.util.contentsdownload",
										"com.besta.util.contentsdownload.ContentsDownload"));
						startActivity(intent);
					}
				});
		dlg.setNegativeButton(getResources().getString(R.string.menu_cancel), null);
		dlg.show();
	}
    public boolean isFlashExists(String QuesId)
    {
    	String flashPath = dbpath;
    	flashPath = flashPath.substring(0,flashPath.indexOf("KE.db"));
    	String flashName = QuesId;
		String BFEPath = flashPath+"BFE"+File.separator;
		String BFESPath =  flashPath + flashName.substring(0,flashName.length()-2)+".bfes";
		File BFEfile = new File(BFEPath+flashName+".bfe");
		File BFESfile = new File(BFESPath);
		if(BFEfile.exists()||BFESfile.exists())
		{
			return true;
		}
		else
		{
			return false;
		}
		
    }
    public void updateFrameCache(){
    	webview.postDelayed(new Runnable(){
            public void run() {
               try {
                   Class<?> webViewCore = Class.forName("android.webkit.WebViewCore");
                   java.lang.reflect.Field instance;
                   instance = WebView.class.getDeclaredField("mWebViewCore");
                   instance.setAccessible(true);
                   Object last = instance.get(webview);
                   Method update = webViewCore.getDeclaredMethod("nativeUpdateFrameCache", new Class[0]);
                   update.setAccessible(true);
                   if(last != null && update != null)
                   {
                      update.invoke(last, new Object[0]);
                   }
               } catch (Exception e) {
                   // TODO Auto-generated catch block
                   e.printStackTrace();
               }
            }
        }, 1000);
 //   	Toast.makeText(getApplicationContext(),"UPDATE!", Toast.LENGTH_SHORT).show();
     }
    public void getAnsFormJs(String ans,int whichSelect,String value)
    {	
    	int ansNum = Integer.valueOf(ans);
    	int ansValue = Integer.valueOf(value);
		SQLiteDatabase db = null;
		File historyfile = new File(pathHistory,"history.db");
		db = SQLiteDatabase.openOrCreateDatabase(historyfile, null);
    	if(ansValue==1)
    	{
    		ContentValues cv = new ContentValues();
    	    cv.put("History",-1);
    	    String[] whereArgs={historyRankList[ansNum]};				    	
    	    db.update("HISTORYDATA",cv,"DataIndex=?",whereArgs);
    	}
    	else
    	{
    		
    	} 
    	db.close();
    	char selectChar = (char)('A'+whichSelect);
    	System.out.println(historyRankList[ansNum]+":"+selectChar + " | T_OR_F:" + ansValue);
    }


	public void getQuesNum()
    {   

        Intent intent = getIntent();
        File pHtmlFileDel = new File(pathtemp + File.separator + "temp" + File.separator + "kpexercise.html");
        pHtmlFileDel.delete();
    	/*****************************************************************************/
				pHtmlString = pHtmlString.append("<html>");
				pHtmlString = pHtmlString.append("<head>");
				pHtmlString = pHtmlString.append("<style type=\"text/css\">");
//			pHtmlString = pHtmlString.append("img {	max-width: 100%;}");
				pHtmlString = pHtmlString.append(getAssetsString("button.css"));
				pHtmlString = pHtmlString.append("</style>");
				pHtmlString = pHtmlString.append("<script>");
				pHtmlString = pHtmlString.append("var tempPath = \""+assetPath+"\"");
				pHtmlString = pHtmlString.append(getAssetsString("kpexercise.js"));
//			pHtmlString = pHtmlString.append(new String(reader,Charset.forName("UnicodeLittle")));
				pHtmlString = pHtmlString.append("</script>");
				pHtmlString = pHtmlString.append("</head>");
				pHtmlString = pHtmlString.append("<body>");
				pHtmlString = pHtmlString.append("<ol>");
				pHtmlString = pHtmlString.append("");
		/*****************************************************************************/

//			pHtmlFile.write(transSign("<html>"));//Html start
//			pHtmlFile.write(transSign("<head>"));//Html head
//			String cssString = "<style type=\"text/css\">img {	max-width: 100%;}</style>";
//			pHtmlFile.write(transSign(cssString));//Html CSS
//			pHtmlFile.write(transSign("<script>"));
////			pHtmlFile.write(transSign("<script src=\"kpexercise.js\"></script>"));//JS			
//			pHtmlFile.write(transSign("var tempPath = \""+assetPath+"\""));//added by Will 2011/12/14
////			pHtmlFile.write(buffer1);
//			pHtmlFile.write(reader);
//			pHtmlFile.write(transSign("</script>"));
//			pHtmlFile.write(transSign("</head>"));//Html head
//			pHtmlFile.write(transSign("<body>"));//body start
//			pHtmlFile.write(transSign("<ol>"));//list start
//			pHtmlFile.seek(pHtmlFile.length());
///////////////////////////////////////////////////////////////////////////////////////	
		        String quesId[] = null;
				ArrayList<String> IOArray = new ArrayList<String>();
				IOArray = intent.getStringArrayListExtra("unitnumberarray");
				quesList = quesId = IOArray.toArray(new String[0]);						
				Ques ques[] = new Ques[quesId.length];
				for(int i = 0;i<quesId.length;i++)
				{
					ques[i] = new Ques();
					ques[i].Id = quesId[i];
					ques[i].history = 0;
				}
//			Log.d("SystemClock getHistory",SystemClock.uptimeMillis()+"");
				ques = getHistory(ques);
//			Log.d("SystemClock standby",SystemClock.uptimeMillis()+"");
				readDatabaseNew(ques);
///////////////////////////////////////////////////////////////////////////////////////			
//	        pHtmlFile.seek(pHtmlFile.length());
		        
		        
		
//			pHtmlFile.write(transSign("</ol>"));//list start
//			pHtmlFile.write(transSign("</body>"));//body start
//			pHtmlFile.write(transSign("</html>"));//Html start
//			pJsFile.close();
//	        pHtmlFile.close();	
		        
		/*****************************************************************************/
//				pHtmlString = pHtmlString.append("<input type=\"button\" value=\"Hello world!\">");
				pHtmlString = pHtmlString.append("<a href=\""+SUBMIT_ANSWERS+"\"><input type=\"button\" value=\"Submit\" class=\"btn4\"></a>");
				pHtmlString = pHtmlString.append("</ol>");
		        pHtmlString = pHtmlString.append("</body>");
		        pHtmlString = pHtmlString.append("</html>");
		/*****************************************************************************/
    }
	public void readDatabaseNew(Ques[] ques)
    {
		String databasePath = dbpath;
		SQLiteDatabase db = null;
		String sectionindex = "'null'";
		String imgString =  "'null'";
		int QuesTotalNum = 0;
//		Cursor curtemp[] = null;
	    db = SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);	   
	    if(accurateSearch == true)
	    {
		    for(int j = 0;j<ques.length;j++)
		    {
		    	System.out.println("quesNum: "+ques[j].Id);
		    	sectionindex = sectionindex+",'"+ques[j].Id+"'";
		    }
		    String dbcommand = "SELECT * FROM KEDATA  WHERE DataIndex IN (" + sectionindex + ")";
		    System.out.println(dbcommand);
//		    "SELECT * FROM QUE  WHERE ExamIndex IN (" + examindex + ")"
		    Cursor cur = db.rawQuery(dbcommand, null);	
		    if( cur != null )
			{
//		    	Log.d("SystemClock 1",SystemClock.uptimeMillis()+"");
				if(cur.moveToFirst())
				{
					//randomRank true
//					Log.d("SystemClock 2",SystemClock.uptimeMillis()+"");
					if(randomRank)
					{										
						int random[] = randomIntArray(cur.getCount());
						while(QuesTotalNum < cur.getCount())
						{
							cur.moveToFirst();
							cur.move(random[QuesTotalNum]);
							createHtml(db,cur,QuesTotalNum);						
							imgString = imgString +","+ cur.getString(cur.getColumnIndex("ImageNameCount"));
							QuesTotalNum++;
						}
					}
					else
					{				
						do
						{
							createHtml(db,cur,QuesTotalNum);						
							imgString = imgString +","+ cur.getString(cur.getColumnIndex("ImageNameCount"));
							QuesTotalNum++;
						}while(cur.moveToNext());
					}
				}
			}
		    pHtmlString = new StringBuffer(insertImageBase64(pHtmlString.toString(), db, imgString, "IMAGEDATA"));
		    
//		    CreateImage(db,imgString);
	    }
	    else
	    {
		    for(int j = 0;j<ques.length;j++)
		    {
			    String dbcommand = "SELECT * FROM KEDATA  WHERE DataIndex LIKE \"%" + ques[j].Id + "%\"";
			    Cursor cur = db.rawQuery(dbcommand, null);	
				if( cur != null )
				{
					if(cur.moveToFirst())
					{
						do
					    {
//							createImage(db,cur);
							createHtml(db,cur,QuesTotalNum);
							pHtmlString = new StringBuffer(insertImageBase64(pHtmlString.toString(), db, cur, "IMAGEDATA"));

							
							
							QuesTotalNum++;
					    }
					    while(cur.moveToNext());
					}
				}
		    }	  
	    }
//	    GetDataFromHttpGet myGetDataFromHttpGet = new GetDataFromHttpGet(this);
//	    myGetDataFromHttpGet.getExamPaper();
	    
	    
	    checkFlashUpdate_New(quesList);
	    
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.close();
	    if(quesFlag == false)
	    {
//	    	Toast.makeText(getApplicationContext(),"Can not find any subjects!", Toast.LENGTH_SHORT).show();
	    	finish();
	    }
    }
	public void createHtml(SQLiteDatabase db,Cursor cur,int Count)
	{	
		if(Count>=MAXQUESNUM)
		{
			return;
		}
		int QuesTotalNum = Count;
//		byte buffer[];
		quesFlag = true;
		historyRankList[QuesTotalNum] = cur.getString(cur.getColumnIndex("DataIndex"));
		String QuesId = cur.getString(cur.getColumnIndex("DataIndex"));
		String Ques = replaceBuffer(cur.getString(cur.getColumnIndex("Ques")));//content need to exchange
		String Exp = replaceBuffer(cur.getString(cur.getColumnIndex("Explain")));//content need to exchange
//		String Background = "";

		if(cur.getColumnIndex("Background")>-1 && !cur.getString(cur.getColumnIndex("Background")).isEmpty() && cur.getString(cur.getColumnIndex("Background"))!="")
		{
			Ques = replaceBuffer(cur.getString(cur.getColumnIndex("Background")))+ "<br>" +Ques;//content need to exchange
		}
//		System.out.println("[Background]"+cur.isNull(cur.getColumnIndex("Background")));
//		String Background = replaceBuffer(cur.getString(cur.getColumnIndex("Background")));//content need to exchange
		String Ans = cur.getString(cur.getColumnIndex("Answer"));
		String Selection[] = new String[MAXSELECTIONNUM];
		
//		if(Background!="")
//		{
//			Ques = Background + "<br>" +Ques ;
//		}
		for(int i=0;i<MAXSELECTIONNUM;i++)
		{
			Selection[i]=replaceBuffer(cur.getString(cur.getColumnIndex("SelectionA")+i));//content need to exchange
		}
//			RandomAccessFile pHtmlFile = new RandomAccessFile(pathtemp + File.separator+"temp"+ File.separator +"kpexercise.html","rw");							
					String iconPath = null;
		//			pHtmlFile.seek(pHtmlFile.length());
		//			pHtmlFile.write(transSign("<li>"));//li start
					
					pHtmlString = pHtmlString.append("<li>");
		/////////////////////////////////////////////////
					if(tag.equals(BASETEST_TAG))
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
						pHtmlString = pHtmlString.append(baseinfo);
					}			
		//			pHtmlFile.write(transSign(Ques));
					pHtmlString = pHtmlString.append(Ques);
		/////////////////////////////////////////////////
		
					iconPath = assetPath+"ans_open.pic";
					String ButtonId = QuesTotalNum+"-ansbutton";
					String AnsId = QuesTotalNum+"-ans";
					if(Exp.length() != 0)
					{
		//				pHtmlFile.write(transSign("    <input type=\"image\" id=\""+ButtonId+"\" src=\""+iconPath+"\"width=\""+ANS_IMG_WIDTH+"px\"height=\""+ANS_IMG_HEIGHT+"px\" vspace=\"1\" align=\"top\" onclick=\"displayhidden(\'"+AnsId+"\','"+ButtonId+"');\">"));	
						pHtmlString = pHtmlString.append("    <input type=\"image\" id=\""+ButtonId+"\" src=\""+iconPath+"\"width=\""+ANS_IMG_WIDTH+"px\"height=\""+ANS_IMG_HEIGHT+"px\" vspace=\"1\" align=\"top\" onclick=\"displayhidden(\'"+AnsId+"\','"+ButtonId+"');\">");
					}
					if(tag.equals(BASETEST_TAG))
					{
						String knowledgeLink = cur.getString(cur.getColumnIndex("knowledge"));
						if(knowledgeLink.length()>0)
						{
							iconPath = assetPath+"link.pic"; 
		//					pHtmlFile.write(transSign("<a href=\"knowledge:"+knowledgeLink+"\"><img src=\""+iconPath+"\"width=\""+LINK_IMG_WIDTH+"px\"height=\""+LINK_IMG_HEIGHT+"px\" vspace=\"1\" align=\"top\" ></a>"));
							pHtmlString = pHtmlString.append("<a href=\"knowledge:"+knowledgeLink+"\"><img src=\""+iconPath+"\"width=\""+LINK_IMG_WIDTH+"px\"height=\""+LINK_IMG_HEIGHT+"px\" vspace=\"1\" align=\"top\" ></a>");
						}
					}
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
						String ChooseId = QuesTotalNum+"-"+i;//0-0,0-1.....
						String ChooseName = "0"+QuesTotalNum;//01,02,03.....
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
		//				pHtmlFile.write(transSign("<br><input type=\"image\" id=\""+ChooseId+"\" name=\""+ChooseName+"\" checked=\"0\" value=\""+AnsTF+"\"src=\""+iconPath+"\"width=\""+SEL_IMG_WIDTH+"px\"height=\""+SEL_IMG_HEIGHT+"px\" vspace=\"1\" align=\"absmiddle\"onclick=\"changebutton(\'"+ChooseName+"\',"+i+");\">      "));
		//				pHtmlFile.write(transSign(Selection[i]));
//						pHtmlString = pHtmlString.append("<br><input type=\"image\" id=\""+ChooseId+"\" name=\""+ChooseName+"\" checked=\"0\" value=\""+AnsTF+"\"src=\""+iconPath+"\"width=\""+SEL_IMG_WIDTH+"px\"height=\""+SEL_IMG_HEIGHT+"px\" vspace=\"1\" align=\"absmiddle\"onclick=\"changebutton(\'"+ChooseName+"\',"+i+");\">      ");
						pHtmlString = pHtmlString.append("<br><input type=\"radio\" id=\""+ChooseId+"\" name=\""+ChooseName+"\" value=\""+AnsTF+"\"src=\""+iconPath+"\"width=\""+SEL_IMG_WIDTH+"px\"height=\""+SEL_IMG_HEIGHT+"px\" vspace=\"1\" align=\"absmiddle\"onclick=\"changebutton(\'"+ChooseName+"\',"+i+");\">");
//						pHtmlString = pHtmlString.append("<br><input type=\"radio\" name=\"Sex\" value=\"Female\">      ");
						pHtmlString = pHtmlString.append(selectChar+".   "+Selection[i]);
					}						
		
		//			pHtmlFile.write(transSign("<h id=\""+AnsId+"\" style=\"display:none\">"));
		//			pHtmlFile.write(transSign("<table width=\"90%\" bordercolor=\"#6C6C6C\" style=\"border-style:solid;border-width:thick thin thin thin;word-break:break-all;\"> <td border=\"0\">"));//  <td background="/i/eg_bg_07.gif"> Second</td> <td>�ѪR</td>
		//			pHtmlFile.write(transSign("<font color=\"#000000\">解析</font>"));
					
					pHtmlString = pHtmlString.append("<h id=\""+AnsId+"\" style=\"display:none\">");
					pHtmlString = pHtmlString.append("<table width=\"90%\" bordercolor=\"#6C6C6C\" style=\"border-style:solid;border-width:thick thin thin thin;word-break:break-all;\"> <td border=\"0\">");
					
					
					pHtmlString = pHtmlString.append("<font color=\"#000000\">"+getString(R.string.answer)+": </font>");
					pHtmlString = pHtmlString.append(Ans);
					pHtmlString = pHtmlString.append("<br>");
//					pHtmlString = pHtmlString.append("<font color=\"#000000\">"+getString(R.string.resolving)+": </font>");
					
					if(tag.equals(BASETEST_TAG))
					{	
						if(isFlashExists(QuesId))
						{
							iconPath = assetPath+"realans.pic";
						}
						else
						{
							iconPath = "";
						}
		//				iconPath = assetPath+"realans.pic";
		//				pHtmlFile.write(transSign("  <a href=\"flash:"+QuesId+"\"><img src=\""+iconPath+"\"width=\""+REALTEACH_IMG_WIDTH+"px\"height=\""+REALTEACH_IMG_HEIGHT+"px\" vspace=\"1\" align=\"top\" ></a>"));
		//				pHtmlFile.write(transSign("<a href=\"flash:"+QuesId+"\"><input type=\"image\" id=\""+QuesId+".bfe"+"\" src=\""+iconPath+"\"height=\""+REALTEACH_IMG_HEIGHT+"px\"  vspace=\"1\" align=\"top\" ></a>"));
						pHtmlString = pHtmlString.append("<a href=\"flash:"+QuesId+"\"><input type=\"image\" id=\""+QuesId+".bfe"+"\" src=\""+iconPath+"\"height=\""+REALTEACH_IMG_HEIGHT+"px\"  vspace=\"1\" align=\"top\" ></a>");
					}

					
		//			pHtmlFile.write(transSign("<br>"));
		//			pHtmlFile.write(transSign(Exp));
		////			pHtmlFile.write(transSign("<hr noshade=\"noshade\" />"));
		//			pHtmlFile.write(transSign("</td></table>"));
		//			pHtmlFile.write(transSign("</h>"));	
		//			pHtmlFile.write(transSign("</li>"));//li end			
		//			pHtmlFile.close();	
					
					
//					pHtmlString = pHtmlString.append("<br>");
					pHtmlString = pHtmlString.append(Exp);
					pHtmlString = pHtmlString.append("</td></table>");
					pHtmlString = pHtmlString.append("</h>");
					pHtmlString = pHtmlString.append("</li>");

    }

	private String insertImageBase64(String res,SQLiteDatabase db,Cursor cur,String tableName)
	{
		
//		res = res.replace("src=\"IMAGE/", "src=\"image/");
		String imgString = cur.getString(cur.getColumnIndex("ImageNameCount"));
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
						res = res.replace("src="+ImgName,"src=\"data:image/png;base64,"+content+"\"");
					}while(imgcur.moveToNext());
				}
			}
			imgcur.close();
		}
		return res;
	}
	private String insertImageBase64(String res,SQLiteDatabase db,String imgString,String tableName)
	{
		
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
						res = res.replace("src="+ImgName,"src=\"data:image/png;base64,"+content+"\"");
					}while(imgcur.moveToNext());
				}
			}
			imgcur.close();
		}
		return res;
	}
//	private void CreateImage(SQLiteDatabase db,String imgString)//added by Will 2011/12/14
//	{
//		byte ImgBuffer[];
//		String ImgName;
//		if(imgString.isEmpty())
//		{
//			return;
//		}
//		String dbcommand = "SELECT * FROM IMAGEDATA WHERE ImageName IN ("+imgString+")";
//		System.out.println(dbcommand);
//		Cursor cur = db.rawQuery(dbcommand, null);
//		if(cur != null )
//		{
//			if(cur.moveToFirst())
//			{
//				do{
//					ImgName = cur.getString(cur.getColumnIndex("ImageName"));
//					ImgBuffer = cur.getBlob(cur.getColumnIndex("Imagebuffer"));
//					RandomAccessFile pPicFile;
//					try {
//						pPicFile = new RandomAccessFile(pathtemp + File.separator +"temp" + File.separator + ImgName,"rw");
//						pPicFile.write(ImgBuffer);
//						pPicFile.close();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}while(cur.moveToNext());
//			}
//		}
//	}

//	private void createImage(SQLiteDatabase db,Cursor cur)//added by Will 2011/12/14
//	{
//		String imgString = cur.getString(cur.getColumnIndex("ImageNameCount"));
//		byte ImgBuffer[];
//		String ImgName;
//		if(imgString.isEmpty())
//		{
//			return;
//		}
//		String dbcommand = "SELECT * FROM IMAGEDATA WHERE ImageName IN ("+imgString+")";
//		System.out.println(dbcommand);
//	    cur = db.rawQuery(dbcommand, null);
//		if(cur != null )
//		{
//			if(cur.moveToFirst())
//			{
//				do{
//					ImgName = cur.getString(cur.getColumnIndex("ImageName"));
//					ImgBuffer = cur.getBlob(cur.getColumnIndex("Imagebuffer"));
//					RandomAccessFile pPicFile;
//					try {
//						pPicFile = new RandomAccessFile(pathtemp + File.separator +"temp" + File.separator + ImgName,"rw");
//						pPicFile.write(ImgBuffer);
//						pPicFile.close();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}while(cur.moveToNext());
//			}
//		}
//	}
	private void checkFlashUpdate_New(String FileName[])//For the new sever API
	{
		updateFlashId = FileName;
		context = this;

		Thread th = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
                try {  
                    Thread.sleep(100);  
                } catch (InterruptedException e) {  
                    // TODO Auto-generated catch block  
                    e.printStackTrace();  
                }    
                long localFileTime = 0;
//        		String basePath = "/sdcard/besta/";
//        		if(!new File("/sdcard/besta/").exists())
//        		{
//        			if(new File("/mnt/besta-internal/Besta/").exists())
//        			{
//        				basePath = "/mnt/besta-internal/Besta/";
//        			}
//        		}
		    	String flashPath = dbpath;
		    	flashPath = flashPath.substring(0,flashPath.indexOf("KE.db"));
                String JSON = "";
                try{     
                      
//                    HttpPost request = new HttpPost("http://www.5dkg.com/BestaExam/services");
                               
//                    List<NameValuePair> postParams = new ArrayList<NameValuePair>();  
//                    List<NameValuePair> postParams = new ArrayList<NameValuePair>();
//                    List<NameValuePair> params = new ArrayList<NameValuePair>();
//                    params.add(new BasicNameValuePair("action", "getTitle"));
//                    params.add(new BasicNameValuePair("titleId", "Mon Jul 23 10:27:09 CST 2012|8464452908998"));
                    String url = "http://10.180.1.23/BestaExam/services";
                    String paramStr = "";
                    Map params = new HashMap();
                    params.put("action", URLEncoder.encode("getTitle","utf-8") );
                    params.put("titleId", URLEncoder.encode("Mon Jul 23 10:27:09 CST 2012|8464452908998","utf-8"));
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
//                    url = URLEncoder.encode(url,"utf-8");                    
//                    url = "http://10.180.1.23/BestaExam/services?action=getTitle&titleId=Mon%20Jul%2023%2010:27:09%20CST%202012|8464452908998";
//                    url = "http://5billion.com.cn/poem.txt";
                    HttpClient client = new DefaultHttpClient();   
                    HttpGet request = new HttpGet(url);
                    request.addHeader("charset", HTTP.UTF_8);       
//                    params.add(new BasicNameValuePair("remember", "1"));
//                    HttpParams httpParams = null;
//                    httpParams.setParameter("action", "getTitle");
//                    httpParams.setParameter("titleId", "Mon Jul 23 10:27:09 CST 2012|8464452908998");
//                    postParams.add(new BasicNameValuePair("action","getTitle"));
//                    postParams.add(new BasicNameValuePair("titleId","Mon Jul 23 10:27:09 CST 2012|8464452908998")); 
//                    postParams.add(new BasicNameValuePair("machineType",android.os.Build.DEVICE));   
//                    postParams.add(new BasicNameValuePair("machineType","ca017"));   
//                    UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParams);     
//                    request.setEntity(formEntity);
//                    request.setParams(httpParams);
//                    HttpResponse httpResponse = httpClient.execute(httpRequest);
                    HttpResponse response = client.execute(request);     
                    BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));           
                    StringBuffer sb = new StringBuffer("");       
                    String line = "";
                    String NL = System.
                    getProperty("line.separator");     
                    while((line = in.readLine()) != null){
                            sb.append(line + NL);
                    }
	                in.close();  
//	                JSON = sb.toString().substring(new String("bestamarket").length());
	                JSON = sb.toString();
	                System.out.println("[Out JSON]"+JSON);
					JSONTokener jsonParser = new JSONTokener(JSON);
					JSONArray jsonNArray = (JSONArray) jsonParser.nextValue();
					for(int i = 0;i<jsonNArray.length();i++)
					{
						JSONObject jsonObject = (JSONObject) jsonNArray.get(i);
						if(jsonObject.getBoolean("isNeedUpdate"))
						{
	    	        		File localFlashFile = new File(flashPath + "BFE/"+jsonObject.getString("fileName"));//updateFlashId[i]//packagename
//	    	        		localFileTime = localFlashFile.lastModified();
							if(localFlashFile.lastModified()!=0)
							{
								Map<String,Object> map = new HashMap<String, Object>();
								map.put("flashid",jsonObject.getString("fileName"));
						        map.put("result",4);
								Message msg = new Message();
								msg.what = 0;
								msg.obj = map;
								myupdateHandler.sendMessage(msg);
							}
							else
							{
								Map<String,Object> map = new HashMap<String, Object>();
								map.put("flashid",jsonObject.getString("fileName"));
						        map.put("result",1);
								Message msg = new Message();
								msg.what = 0;
								msg.obj = map;
								myupdateHandler.sendMessage(msg);
							}
						}
//				        0---nothing_playSchedule
//				        1---download_playSchedule
//				        2---noUpdate_playFlash
//				        3---noUpdate_playFlash
//				        4---update_playFlash
					}				
                }catch(Exception e){
                	e.printStackTrace();
	            }
			}
		});
		th.start();
	}
	class updateHandler extends Handler{
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
//			super.handleMessage(msg);
//			System.out.println("Th end!");
//			List<Map<String,Object>> mylist = new ArrayList<Map<String,Object>>();
//			mylist = (List<Map<String, Object>>)
//			for(int i = 0;i<mylist.size();i++)
//			{
//				mylist.get(i).
			if(msg.what==0)
			{
				Map<String,Object> map =  (Map<String, Object>) msg.obj;
				int value =  (Integer)map.get("result");
				String flashID = (String)map.get("flashid");
				System.out.println("[Flash]"+flashID + ":" + value);
				map.clear();
		        switch(value)
		        {
		        case 0://nothing_playSchedule
		        	break;
		        case 1://download_playSchedule
		        	webview.loadUrl("javascript:setFlashState('" +flashID+"','download')");
		        	break;
		        case 2://noUpdate_playFlash
		        	break;
		        case 3://noUpdate_playFlash
		        	break;
		        case 4://update_playFlash
		        	webview.loadUrl("javascript:setFlashState('" +flashID+"','update')");
		        	break;
		        	default:
		        		break;
		        }
			}
		}
	}
	private Ques[] getHistory(Ques[] ques)
	{
		if(ques.length==0)
		{
			return ques;
		}
		Ques reques[] = null;
		String dbcommand = null;
		String history = "'null'";
		SQLiteDatabase db = null;
		File historyfile = new File(pathHistory,"history.db");
		if(historyfile.exists())
		{
			db = SQLiteDatabase.openOrCreateDatabase(new File(pathHistory,"history.db"), null);
		}
		else
		{
			db = SQLiteDatabase.openOrCreateDatabase(new File(pathHistory,"history.db"), null);
			dbcommand="CREATE TABLE HISTORYDATA(DataIndex TEXT,History TEXT)";
			db.execSQL(dbcommand); 
		}
	    db.beginTransaction();
	    try {
	    for(int i = 0;i<ques.length;i++)
	    {
	    	history = history+",'"+ques[i].Id+"'";
	    }
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
	    boolean rightFlag = true;
	    for(int i = 0;i<ques.length;i++)
	    {
	    	if(ques[i].history == 0)
	    	{
		    	ContentValues cv = new ContentValues();
		    	cv.put("DataIndex", ques[i].Id);
		    	cv.put("History", ques[i].history);
		    	db.insert("HISTORYDATA", null, cv); 
	    	}
	    	if(ques[i].history != -1)
	    	{
	    		rightFlag = false;
	    	}
		} 
	    if(rightFlag == true)
	    {
		    for(int i = 0;i<ques.length;i++)
		    {
				ContentValues cv = new ContentValues();
				ques[i].history = 0;
		    	cv.put("History",0);
		    	String[] whereArgs={ques[i].Id};				    	
		    	db.update("HISTORYDATA",cv,"DataIndex=?",whereArgs);
		    }
	    }
	    Ques questemp;
	    for(int i = 0;i<ques.length;i++)
	    {
	    	for(int j = 0;j<ques.length;j++)
	    	{
		    	if(ques[i].history>ques[j].history)
		    	{
		    		questemp = ques[j];
		    		ques[j] = ques[i];
		    		ques[i] = questemp;
		    	}
	    	}
	    }   
	    if(ques.length<MAXQUESNUM)
	    {
	    	reques = new Ques[ques.length];
	    }
	    else
	    {
	    	reques = new Ques[MAXQUESNUM];
	    }
	    for(int i = 0;i<ques.length&&i<MAXQUESNUM;i++)
	    {
	    	reques[i] = new Ques();
	    	reques[i] = ques[i];
			ContentValues cv = new ContentValues();
	    	cv.put("History",1);
	    	String[] whereArgs={reques[i].Id}; 	
	    	db.update("HISTORYDATA",cv,"DataIndex=?",whereArgs);
	    } 
	      db.setTransactionSuccessful();
	    } finally {
	      db.endTransaction();
	    }
	    db.close();
		return reques;
	}
	private int[] randomIntArray(int num)
	{
	    int random[] = new int[num];
	    int randomres[] = new int[num];
	    int randomtemp = 0;
	    for(int i = 0;i<num;i++)
	    {		    
	    	random[i]=(int)(Math.random()*num);
	    	randomres[i] = i;
	    }
	    for(int i = 0;i<num;i++)
	    {
	    	for(int j = 0;j<num;j++)
	    	{
	    		if(random[i]<random[j])
	    		{
	    			randomtemp = randomres[j];
	    			randomres[j] = randomres[i];
	    			randomres[i] = randomtemp;
	    		}
	    	}
	    }
	    return randomres;
	}
    public int byteToInt(byte intTemp[])//4 small bytes or big bytes to 1 big int or 1 small int 
    {
    	int addTemp[] = new int[4];
    	int intR = 0;
    	if(intTemp.length != 4)
    	{
    		return 0;
    	}
    	addTemp[0] = intTemp[0];
    	addTemp[1] = intTemp[1];
    	addTemp[2] = intTemp[2];
    	addTemp[3] = intTemp[3];
    	addTemp[0] = (addTemp[0]<<0) & 0x000000FF;
    	addTemp[1] = (addTemp[1]<<8) & 0x0000FF00;
    	addTemp[2] = (addTemp[2]<<16)& 0x00FF0000;
    	addTemp[3] = (addTemp[3]<<24)& 0xFF000000;
    	intR = addTemp[0]+addTemp[1]+addTemp[2]+addTemp[3];   	
    	return intR;
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
    public int transInt(int intS)//big int to small int or small int to big int
    {
    	int intR = 0;
    	int temp1 = intS;
    	int temp2 = intS;
    	int temp3 = intS;
	    int temp4 = intS;	
    	temp1 = (temp1<<24)& 0xFF000000;
    	temp2 = (temp2<<8) & 0x00FF0000;
    	temp3 = (temp3>>8) & 0x0000FF00;
	    temp4 = (temp4>>24)& 0x000000FF;		
    	intR = temp1 + temp2 + temp3 + temp4;	
    	return intR;
    }
    public byte transBytes(byte buffer1[])[]//big bytes to small bytes or small bytes to big bytes 3C 00 3B 00 to 00 3C 00 3B
    {  	
		for(int i_tans = 0;i_tans<buffer1.length;i_tans = i_tans+2)
		{
			byte bufferTemp = buffer1[i_tans];
			buffer1[i_tans] = buffer1[i_tans+1];
			buffer1[i_tans+1] = bufferTemp;
		}
    	return buffer1;
    }
    public byte bufferExchange(byte buffer1[])[]//replace bytes with given bytes
    {
    	String strBuffer1;
    	buffer1 = transBytes(buffer1);		
    	try {
    		
			strBuffer1 = new String(buffer1, "unicode");
			strBuffer1 = strBuffer1.replace("<NEWLINE>","<br>");
			buffer1 = strBuffer1.getBytes("unicode");

			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return buffer1;
    	
    }
    private void deleteFile(File file)//delete a file with all files in it
    { 
    	if(file.exists())
    	{                     
    		if(file.isFile())
    		{                    
    	     file.delete();                      
    	    }
    		else if(file.isDirectory())
    		{              
    			File files[] = file.listFiles();              
    			for(int i=0;i<files.length;i++)
    			{            
    				this.deleteFile(files[i]);             
    			} 
    	    } 
    	    file.delete(); 
    	}
    	else
    	{ 
//    	    System.out.println("NOT FOUND!"+'\n'); 
    	} 
    }
    public String[] getMidStringAll(String initiativeStr,String beginStr,String endStr)//get the mid String between beginStr and endStr
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
	public String getAssetsString(String fileName)
	{
		String ret = "";
		try {
			InputStream reader = getAssets().open(fileName);
			byte buffer[] = new byte[reader.available()];
			reader.read(buffer);
			ret = new String(buffer,Charset.forName("UnicodeLittle"));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
}

