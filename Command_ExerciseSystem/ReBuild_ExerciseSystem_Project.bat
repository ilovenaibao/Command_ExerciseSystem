@echo off
:: *******************************************************************************************************************************
::     **********************************************�л{�u�\Ū�u�{�t�m�I*****************************************************
::        ******************************************�_�h�u�{�Ыإi��X��**************************************************
rem readme
rem (*) ----> �����ק�Ϊ̶�g�����e
rem (/) ----> �i�H�ϥ��q�{��
rem �p�Gapk�ͦ����\�A����|�b�u�{�ڥؿ��U�ͦ��Gmake_project_info.txt
rem �o��txt���O�����O�u�{�����H��
rem END
:: *******************************************************************************************************************************
:: �i��u�{�t�m

:: 0. �]�m�һݸ��| ----> Initialize some info

:: �u�{�t�m
:: (*) �u�{�W (*)
set Project_Name=ExerciseSystem
:: (*) �u�{�]�W (*)
set ProjectPackage_Last_Name=exerciseengine
set ProjectPackage_Name=com.besta.app.%ProjectPackage_Last_Name%
:: (*) �u�{�ҥ�adk-addon target ID (*)
set ProjectSDK_ID=40
:: (/) �u�{���]���귽���W (/)
set ProjectRes_Name=%Project_Name%.ap_
:: (/) �u�{�ͦ�����ñ�W��apk�W (/)
set ProjectUnsigedApk_Name=%Project_Name%_unsigner.apk
:: (/) �u�{�̲׿�X��apk�W (/)
set ProjectOutApk_Name=bin\%Project_Name%.apk
:: *******************************************************************************************************************************

:: keystore�ϥΪ��ӤH�H���t�m
:: (/) �u�{�ͦ���keystore�W (/)
set ProjectKeyStore_Name=%Project_Name%.keystore
:: (*) �ͦ���keystore���Ĥ���Ѽ� (*)
set ValidityDays_Count=20000
:: (/) �A���W�r�m�� (/)
set RD_Name=Gu
:: (/) �A����´���W�� (/)
set Company_Name="Besta_xi_an_Co."
:: (/) �A����´�W�� (/)
set Organization_Name=RD03
:: (/) �A�Ҧb��ΰϰ�W�� (/)
set Local_Name="xi_an"
:: (/) �A�Ҧb���{�ά٥� (/)
set Province_Name=Shannxi
:: (/) �A�Ҧb��a��2���a�N�X (/)
set Country_ID=CN
:: (/) �]�m�ͦ�ñ�W��apk���K�X (/)
set MakeApk_Pass=bestabxc
:: (/) �]�mkeystore�D�K�X(�@��i�H��apkñ�W�K�X�@�P) (/)
set KeystoreMain_Pass=%MakeApk_Pass%
:: *******************************************************************************************************************************

:: Java & Android SDK ���|�t�m
:: (/) �]�mjavac�sĶ�ɪ�*.java ��󪺽s�X�榡 (/)
set Javac_Encode=UTF-8
:: (/) �]�mjavac�sĶ�ɪ�SDK���� (/)
set JavacSDK_Ver=1.6
:: (*) �u�{���| (*)
set Project_Parent_Path=F:\no_eclipse_build\Projects\Command_ExerciseSystem\
set Project_Path=%Project_Parent_Path%%Project_Name%
:: (*) �u�{�����ҥθ��| (*)
set ProjectInner_Path=com\besta\app\%ProjectPackage_Last_Name%
:: (*) JavaSDK���| (*)
set JavaSDK_Path=K:\
:: (*) AndroidSDK���| (*)
set AndroidJar_Path=K:\android-sdk-windows_4.0\android-sdk-windows\platforms\android-15\android.jar
:: (/) �u�{�Ыئ��\�������H���W (/)
set Success_Info=%Project_Path%\make_%Project_Name%_ProjectInfo.txt
:: (/) �u�{�sĶ�H��OutPut�����| (/)
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

:: 1. �R���W�@��build���
echo.
echo delete old build files ...
cd %Project_Path%
rmdir /s /q gen
rmdir /s /q bin
mkdir gen
mkdir bin

:: 2. �ͦ�R.java���A�����ݭn�Ы�gen���
echo.
echo. >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
echo Remake "R.java" file...
echo Remake "R.java" file... >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
call aapt.exe p -f -m -J gen -S res -I %AndroidJar_Path% -M AndroidManifest.xml >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
if errorlevel 1 goto ERROR

:: 3. �ͦ�.class���
:: eg> javac -encoding UTF-8 -target 1.6 -bootclasspath %AndroidJar_Path% -d bin (-cp [path]�]�t���ĤT��jar) src\com\besta\app\cmdtest\*.java gen\com\besta\app\cmdtest\R.java -classpath libs\baidumapapi.jar
echo.
echo. >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
echo make "*.class" file...
echo make "*.class" file... >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
call javac -encoding %Javac_Encode% -target %JavacSDK_Ver% -bootclasspath %AndroidJar_Path% -d bin -cp libs\besta.android.framework.jar;libs\annotations.jar;libs\WriteAndReadText.jar;libs\jdtCompilerAdapter.jar;libs\org.eclipse.jdt.core_3.7.1.v_B76_R37x.jar;libs\org.eclipse.jdt.compiler.tool_1.0.100.v_B76_R37x.jar;libs\org.eclipse.jdt.debug.ui_3.6.1.v20110803_r371.jar src\com\besta\app\answerpaper\*.java src\com\besta\app\exerciseengine\*.java src\com\besta\app\geometry\*.java src\com\besta\app\testcallactivity\*.java src\com\besta\app\toolswindow\*.java src\com\besta\app\answerpaper\dragpicview\*.java src\com\besta\app\answerpaper\drawview\*.java src\com\besta\app\answerpaper\floatmyview\*.java src\com\besta\app\answerpaper\mywebview\*.java src\com\besta\app\answerpaper\othergraphics\*.java src\com\besta\app\answerpaper\othersclassinfo\*.java src\com\besta\app\answerpaper\redrawpng\MyPngEncode.java src\com\besta\app\answerpaper\xscan\*.java src\com\besta\app\exerciseengine\quesprocess\*.java src\com\besta\app\exerciseengine\question\*.java gen\%ProjectInner_Path%\R.java >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
if errorlevel 1 goto ERROR

:: 4. �ͦ�.dex���
echo.
echo. >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
echo make "*.dex" file...
echo make "*.dex" file... >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
call dx.bat --dex --output=%Project_Path%\bin\classes.dex %Project_Path%\bin >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
if errorlevel 1 goto ERROR

:: 5. �Q��aapt �ͦ�*.ap_���, �Y���]�귽���
:: �ѩ�S��assets��󧨡A�]�����椧�e�ݭn�إ�assets
echo.
echo. >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
echo package "*.ap_" for some resouse file...
echo package "*.ap_" for some resouse file... >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
call aapt.exe package -f -S res -I %AndroidJar_Path% -A assets -M AndroidManifest.xml -F %Project_Path%\bin\%ProjectRes_Name% >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
if errorlevel 1 goto ERROR

:: 6. �ͦ���ñ�W��apk���
echo.
echo. >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
echo make unsigner apk file...
echo make unsigner apk file... >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
call apkbuilder.bat %Project_Path%\bin\%ProjectUnsigedApk_Name% -v -u -z %Project_Path%\bin\%ProjectRes_Name% -f %Project_Path%\bin\classes.dex -rf %Project_Path%\src -nf %Project_Path%\libs -rj %Project_Path%\libs >> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
if errorlevel 1 goto ERROR

:: 8. ��jarsigner ñ�WcmdTest_unsigner.apk�ͦ�Target apk : cmdTest.apk
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
