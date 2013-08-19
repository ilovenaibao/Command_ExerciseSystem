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
set ProjectPackage_Name=com.besta.app.exerciseengine
:: (*) 工程所用adk-addon target ID (*)
set ProjectSDK_ID=39
:: (/) 工程打包的資源文件名 (/)
set ProjectRes_Name=%Project_Name%.ap_
:: (/) 工程生成的未簽名的apk名 (/)
set ProjectUnsigedApk_Name=%Project_Name%_unsigner.apk
:: (/) 工程最終輸出的apk名 (/)
set ProjectOutApk_Name=.\bin\%Project_Name%.apk
:: *******************************************************************************************************************************

:: keystore使用的個人信息配置
:: (/) 工程生成的keystore名 (/)
set ProjectKeyStore_Name=%Project_Name%.keystore
:: (*) 生成的keystore有效日期天數 (*)
set ValidityDays_Count=20000
:: (/) 你的名字姓氏 (/)
set RD_Name=Gu
:: (/) 你的組織單位名稱 (/)
set Company_Name="TaylorGu_Co."
:: (/) 你的組織名稱 (/)
set Organization_Name=home
:: (/) 你所在域或區域名稱 (/)
set Local_Name="xi_an"
:: (/) 你所在的州或省份 (/)
set Province_Name=Shannxi
:: (/) 你所在國家的2位國家代碼 (/)
set Country_ID=CN
:: (/) 設置生成簽名的apk的密碼 (/)
set MakeApk_Pass=taylorzero
:: (/) 設置keystore主密碼(一般可以喝apk簽名密碼一致) (/)
set KeystoreMain_Pass=%MakeApk_Pass%
:: *******************************************************************************************************************************

:: Java & Android SDK 路徑配置
:: (/) 設置javac編譯時的*.java 文件的編碼格式 (/)
set Javac_Encode=UTF-8
:: (/) 設置javac編譯時的SDK版本 (/)
set JavacSDK_Ver=1.6
:: (*) 工程路徑 (*)
set Project_Parent_Path=G:\no_eclipse_build\Projects\Command_ExerciseSystem\
set Project_Path=%Project_Parent_Path%%Project_Name%
:: (*) 工程內部所用路徑 (*)
set ProjectInner_Path=com\besta\app\%ProjectPackage_Last_Name%
:: (*) JavaSDK路徑 (*)
set JavaSDK_Path=I:\
:: (*) AndroidSDK路徑 (*)
set AndroidJar_Path=I:\android-sdk-windows_4.0\android-sdk-windows\platforms\android-15\android.jar
:: (/) 工程創建成功的相關信息名 (/)
set Success_Info=%Project_Path%\make_%Project_Name%_ProjectInfo.txt
:: (/) 工程編譯信息OutPut文件路徑 (/)
set Compile_Info_Path=OutPutInfo
set Compile_Info_Full_Path=%Project_Parent_Path%%Compile_Info_Path%
set Compile_Info_File_Name=out_build_info.txt
set Compile_OutPut_Command=>> %Compile_Info_Full_Path%\%Compile_Info_File_Name%
:: (/) 傳遞參數集 (/)
set Input_args=%Project_Name% %ProjectPackage_Last_Name% %ProjectPackage_Name% %ProjectSDK_ID% %ProjectRes_Name% %ProjectUnsigedApk_Name% %ProjectOutApk_Name% %ProjectKeyStore_Name% %ValidityDays_Count% %RD_Name% %Company_Name% %Organization_Name% %Local_Name% %Province_Name% %Country_ID% %MakeApk_Pass% %KeystoreMain_Pass% %Javac_Encode% %JavacSDK_Ver% %Project_Parent_Path% %Project_Path% %ProjectInner_Path% %JavaSDK_Path% %AndroidJar_Path% %Success_Info% %Compile_Info_Path% %Compile_Info_Full_Path% %Compile_Info_File_Name%

:: *******************************************************************************************************************************
if %1 == rebuild_project ( call PROJECT_REBUILD.bat %Input_args%)
if %1 == create_project ( call PROJECT_CREATE.bat %Input_args%)
if %1 == install_apk ( call PROJECT_INSTALL_APK.bat %Input_args% %2)
if %1 == uninstall_apk ( call PROJECT_UNINSTALL_APK.bat %Input_args%)

:END
pause
exit
