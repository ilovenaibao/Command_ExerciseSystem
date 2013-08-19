package com.besta.app.exerciseengine;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;

import android.content.Context;
import android.util.Base64;
import android.webkit.WebView;

import com.besta.app.testcallactivity.StartAnswerPaperSetting;

/**
 * Interface for jsio
 * @author Will
 *
 */
public class JavaScriptInterfaceES {

	Context context;
	JavaScriptInterfaceES(Context arg1)
	{
		context = arg1;
	}
    public void updateFrameCache()
    {
//    	System.out.println("UpdateFrameCache");
    	ExerciseMain.webview.postDelayed(new Runnable(){
            public void run() {
               try {
                   Class<?> webViewCore = Class.forName("android.webkit.WebViewCore");
                   java.lang.reflect.Field instance;
                   instance = WebView.class.getDeclaredField("mWebViewCore");
                   instance.setAccessible(true);
                   Object last = instance.get(ExerciseMain.webview);
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
    	final String ChooseSign = "choose-";
    	final String ScoreSign = "score-";
    	final String TfSign = "tficon-";
    	if(ans.contains(ChooseSign))
    	{
    		final String ScoreID = ans.replace(ChooseSign,ScoreSign);
    		final String TfID = ans.replace(ChooseSign,TfSign);
    		final String Id = ans.substring(ans.indexOf(ChooseSign)+new String(ChooseSign).length(),ans.lastIndexOf("|"));
//    		if(value.equals(1))
//    		{
//    			Toast.makeText(context,"Right",Toast.LENGTH_SHORT).show();
//    		}
    		final int totalScore = ExerciseMain.questionArray.getTotalScorebyId(Id);
    		int score = Integer.valueOf(value)*totalScore;
    		ExerciseMain.webview.loadUrl("javascript:setScore('" + ScoreID + "',"+score+")");
//    		ExerciseMain.questionArray.setResultbyId(Id,value);
    		int TfType = 0;
    		if(score==0)
    		{
    			TfType = 0;
    		}
    		else if(score>0&&score<totalScore)
    		{
    			TfType = 1;
    		}
    		else if(score==totalScore)
    		{
    			TfType = 2;
    		}
    		
    		ExerciseMain.webview.loadUrl("javascript:setTFicon('" + TfID + "',"+TfType+")");//T = 2

    		ExerciseMain.questionArray.setUserScorebyId(Id,score);
    		
    		
//    		final String TfID = "tficon-"+ID;
//    		String Test = assetPath+"ES/sign_right.png";
    	}
    	else if(ans.contains(ScoreSign))
    	{
    		int score = Integer.valueOf(value);
    		final String Id = ans.substring(ans.indexOf(ScoreSign)+new String(ScoreSign).length(),ans.lastIndexOf("|"));
    		ExerciseMain.questionArray.setUserScorebyId(Id,score);
    	}
    }
	public String getContentById(String url)
	{
		String ret = null;
		String Base64buffer = insertImageBase64(ExerciseMain.setting.GetAnswerResult());
		if(Base64buffer==null)
		{		
			if (ExerciseMain.KIND_OF_ANSPAPER == StartAnswerPaperSetting.KIND_DLG) 
			{
				ret = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
			} 
			else if (ExerciseMain.KIND_OF_ANSPAPER == StartAnswerPaperSetting.KIND_NORMAL) 
			{
				ret = "";
			} 
			else if (ExerciseMain.KIND_OF_ANSPAPER == StartAnswerPaperSetting.KIND_TEST)
			{
				
			}	
		}
		else
		{
			if (ExerciseMain.KIND_OF_ANSPAPER == StartAnswerPaperSetting.KIND_DLG) 
			{
				ret = "<img height=30 align=\"bottom\" style=\"border-bottom: 1px solid black;\" src=\"data:image/png;base64,"+Base64buffer+"\"/>";
			} 
			else if (ExerciseMain.KIND_OF_ANSPAPER == StartAnswerPaperSetting.KIND_NORMAL) 
			{
				//ret = "";
	//			ret = "<img height=\"24px\" vspace=\"1\" align=\"absmiddle\" src=\"file:///android_asset/ES/btn_calculuspaper_press.png\"/>";
				ret = "<div><img width=\"60%\" src=\"data:image/png;base64,"+Base64buffer+"\"/></div>";
			} 
			else if (ExerciseMain.KIND_OF_ANSPAPER == StartAnswerPaperSetting.KIND_TEST) 
			{
				
			}
		}
		return ret;
	}

    public String insertImageBase64(String PngPath)
	{
    	File PngFile = new File(PngPath);
    	if(!PngFile.exists())
    	{
    		return null;
    	}
    	byte ImgBuffer[] = getBytes(PngPath);
		String content = Base64.encodeToString(ImgBuffer,Base64.DEFAULT);
		return content;
	}
    public static byte[] getBytes(String filePath){  
        byte[] buffer = null;  
        try {  
            File file = new File(filePath);  
            FileInputStream fis = new FileInputStream(file);  
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);  
            byte[] b = new byte[1000];  
            int n;  
            while ((n = fis.read(b)) != -1) {  
                bos.write(b, 0, n);  
            }  
            fis.close();  
            bos.close();  
            buffer = bos.toByteArray();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return buffer;  
    } 
}
