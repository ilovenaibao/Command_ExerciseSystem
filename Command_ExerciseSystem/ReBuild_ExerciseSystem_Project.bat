@echo off
:: *******************************************************************************************************************************
::     **********************************************請認真閱讀工程配置！*****************************************************
::        ******************************************否則工程創建可能出錯**************************************************
rem readme
rem (*) ----> 必須修改或者填寫的內容
rem (/) ----> 可以使用默認值
rem 如果apk生成成功，那麼會在工程根目錄下生成：make_project_info.txt
rem 這個txt中記錄的是工程相關信息
rem END
:: *******************************************************************************************************************************
:: 進行工程配置

:: 0. 設置所需路徑 ----> Initialize some info

:: 工程配置
:: (*) 工程名 (*)
set Project_Name=ExerciseSystem
:: (*) 工程包名 (*)
set ProjectPackage_Last_Name=exerciseengine
set ProjectPackage_Name=com.besta.app.%ProjectPackage_Last_Name%
:: (*) 工程所用adk-addon target ID (*)
set ProjectSDK_ID=40
:: (/) 工程打包的資源文件名 (/)
set ProjectRes_Name=%Project_Name%.ap_
:: (/) 工程生成的未簽名的apk名 (/)
set ProjectUnsigedApk_Name=%Project_Name%_unsigner.apk
:: (/) 工程最終輸出的apk名 (/)
set ProjectOutApk_Name=bin\%Project_Name%.apk
:: *******************************************************************************************************************************

:: keystore使用的個人信息配置
:: (/) 工程生成的keystore名 (/)
set ProjectKeyStore_Name=%Project_Name%.keystore
:: (*) 生成的keystore有效日期天數 (*)
set ValidityDays_Count=20000
:: (/) 你的名字姓氏 (/)
set RD_Name=Gu
:: (/) 你的組織單位名稱 (/)
set Company_Name="Besta_xi_an_Co."
:: (/) 你的組織名稱 (/)
set Organization_Name=RD03
:: (/) 你所在域或區域名稱 (/)
set Local_Name="xi_an"
:: (/) 你所在的州或省份 (/)
set Province_Name=Shannxi
:: (/) 你所在國家的2位國家代碼 (/)
set Country_ID=CN
:: (/) 設置生成簽名的apk的密碼 (/)
set MakeApk_Pass=bestabxc
:: (/) 設置keystore主密碼(一般可以喝apk簽名密碼一致) (/)
set KeystoreMain_Pass=%MakeApk_Pass%
:: *******************************************************************************************************************************

:: Java & Android SDK 路徑配置
:: (/) 設置javac編譯時的*.java 文件的編碼格式 (/)
set Javac_Encode=UTF-8
:: (/) 設置javac編譯時的SDK版本 (/)
set JavacSDK_Ver=1.6
:: (*) 工程路徑 (*)
set Project_Parent_Path=F:\no_eclipse_build\Projects\Command_ExerciseSystem\
set Project_Path=%Project_Parent_Path%%Project_Name%
:: (*) 工程內部所用路徑 (*)
set ProjectInner_Path=com\besta\app\%ProjectPackage_Last_Name%
:: (*) JavaSDK路徑 (*)
set JavaSDK_Path=K:\
:: (*) AndroidSDK路徑 (*)
set AndroidJar_Path=K:\android-sdk-windows_4.0\android-sdk-windows\platforms\android-15\android.jar
:: (/) 工程創建成功的相關信息名 (/)
set Success_Info=%Project_Path%\make_%Project_Name%_ProjectInfo.txt
:: (/) 工程編譯信息OutPut文件路徑 (/)
set Compile_Info_Path=OutPutInfo
set Compile_Info_Full_Path=%Project_Parent_Path%%Compile_Info_Path%
set Compile_Info_File_Name=out_build_info.txt
set Compile_OutPut_Command=>> %Compile_Info_Full_Path%\%Compile_Info_File_Name%

:: *******************************************************************************************************************************

:: delete old compile output info 
::del /s /q %Compile_Info_Path%\*.*
echo.
echo create compile output info folder ...
cd %Project_Path%
cd ..
rmdir /s /q %Compile_Info_Path%
mkdir %Compile_Info_Path%

:: start rebuild a android project

:: 1. 刪除上一次build文件
echo.
echo delete old build files ...
cd %Project_Path%
rmdir /s /q gen
rmdir /s /q bin
mkdir gen
mkdir bin

:: 2. 生成R.java文件，首先需要創建gen文件夾
echo.
echo. >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
echo Remake "R.java" file...
echo Remake "R.java" file... >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
call aapt.exe p -f -m -J gen -S res -I %AndroidJar_Path% -M AndroidManifest.xml >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
if errorlevel 1 goto ERROR

:: 3. 生成.class文件
:: eg> javac -encoding UTF-8 -target 1.6 -bootclasspath %AndroidJar_Path% -d bin (-cp [path]包含的第三方jar) src\com\besta\app\cmdtest\*.java gen\com\besta\app\cmdtest\R.java -classpath libs\baidumapapi.jar
echo.
echo. >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
echo make "*.class" file...
echo make "*.class" file... >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
call javac -encoding %Javac_Encode% -target %JavacSDK_Ver% -bootclasspath %AndroidJar_Path% -d bin -cp libs\besta.android.framework.jar;libs\annotations.jar;libs\WriteAndReadText.jar;libs\jdtCompilerAdapter.jar;libs\org.eclipse.jdt.core_3.7.1.v_B76_R37x.jar;libs\org.eclipse.jdt.compiler.tool_1.0.100.v_B76_R37x.jar;libs\org.eclipse.jdt.debug.ui_3.6.1.v20110803_r371.jar src\com\besta\app\answerpaper\*.java src\com\besta\app\exerciseengine\*.java src\com\besta\app\geometry\*.java src\com\besta\app\testcallactivity\*.java src\com\besta\app\toolswindow\*.java src\com\besta\app\answerpaper\dragpicview\*.java src\com\besta\app\answerpaper\drawview\*.java src\com\besta\app\answerpaper\floatmyview\*.java src\com\besta\app\answerpaper\mywebview\*.java src\com\besta\app\answerpaper\othergraphics\*.java src\com\besta\app\answerpaper\othersclassinfo\*.java src\com\besta\app\answerpaper\redrawpng\MyPngEncode.java src\com\besta\app\answerpaper\xscan\*.java src\com\besta\app\exerciseengine\quesprocess\*.java src\com\besta\app\exerciseengine\question\*.java gen\%ProjectInner_Path%\R.java >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
if errorlevel 1 goto ERROR

:: 4. 生成.dex文件
echo.
echo. >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
echo make "*.dex" file...
echo make "*.dex" file... >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
call dx.bat --dex --output=%Project_Path%\bin\classes.dex %Project_Path%\bin >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
if errorlevel 1 goto ERROR

:: 5. 利用aapt 生成*.ap_文件, 即打包資源文件
:: 由於沒有assets文件夾，因此執行之前需要建立assets
echo.
echo. >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
echo package "*.ap_" for some resouse file...
echo package "*.ap_" for some resouse file... >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
call aapt.exe package -f -S res -I %AndroidJar_Path% -A assets -M AndroidManifest.xml -F %Project_Path%\bin\%ProjectRes_Name% >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
if errorlevel 1 goto ERROR

:: 6. 生成未簽名的apk文件
echo.
echo. >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
echo make unsigner apk file...
echo make unsigner apk file... >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
call apkbuilder.bat %Project_Path%\bin\%ProjectUnsigedApk_Name% -v -u -z %Project_Path%\bin\%ProjectRes_Name% -f %Project_Path%\bin\classes.dex -rf %Project_Path%\src -nf %Project_Path%\libs -rj %Project_Path%\libs >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
if errorlevel 1 goto ERROR

:: 8. 用jarsigner 簽名cmdTest_unsigner.apk生成Target apk : cmdTest.apk
echo.
echo. >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
echo signed that unsigner apk file to make a *.apk file...
echo signed that unsigner apk file to make a *.apk file... >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
call jarsigner.exe -verbose -keystore %ProjectKeyStore_Name% -signedjar %ProjectOutApk_Name% %Project_Path%\bin\%ProjectUnsigedApk_Name% %ProjectKeyStore_Name% >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
if errorlevel 1 goto ERROR
goto EXIT_SUCCESS

:ERROR
echo.
echo. >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
echo some error occurred, please check!
echo some error occurred, please check! >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
pause
goto EXIT_END

:EXIT_SUCCESS
echo.
echo. >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
echo Rebuild project Success!
echo Rebuild project Success! >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
::pause
goto EXIT_END

:EXIT_END
cd %Project_Path%
cd ..
echo.
::pause
::exit
