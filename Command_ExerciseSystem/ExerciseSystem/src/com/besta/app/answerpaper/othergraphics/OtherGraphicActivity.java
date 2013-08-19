package com.besta.app.answerpaper.othergraphics;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;

import com.besta.app.answerpaper.othersclassinfo.AnswerPaperFun;
import com.besta.app.answerpaper.othersclassinfo.GetScreenWidthHeight;
import com.besta.app.answerpaper.xscan.ScanFiles;
import com.besta.app.exerciseengine.R;

public class OtherGraphicActivity extends Activity {
	final static String Tag = "OtherGraphicActivity";
	public final static String LoadingPreOneData = "LoadingPreOneData";
	public final static String LoadingNextOneData = "LoadingNextOneData";
	// 搜索全盤
	ScanGraphicFiles[] scanner = null;
	// 當前選擇的介面類型： 系統、 自定義、 收藏
	int nowChosListCount = 0;
	// 刷新List是否結束
	public boolean[] reFreshListEndFlag = null;
	// 當前使用的List
	// System List
	public ArrayList<MyOneData> nowList_in_system = new ArrayList<MyOneData>();
	// MyDefine List
	public ArrayList<MyOneData> nowList_in_mydefine = new ArrayList<MyOneData>();
	// Collect List
	public ArrayList<MyOneData> nowList_in_collect = new ArrayList<MyOneData>();
	// 當前List 顯示數量
	public final static int maxListShowCount = 30;
	// 當前List 起始位置
	public int[] currenListShowPos = null;
	// 搜索的List
	public ArrayList<MyOneData> searchData = new ArrayList<MyOneData>();
	// 当前搜索List 起始位置
	public int currenSearchLstShowPos = 0;
	// 選擇搜索，當前使用list的變量
	public int chooseSearchOrOldAdapter = 0;
	// ListView
	ListView[] lstViw = null;

	MyListAdapter[] myAdapter = null;

	// onSelectListener value
	boolean checkOne = false;
	// 前一次點擊item
	int[] prePosition = null;
	// 是否在touch 狀態
	boolean touchMove = false;
	// Activity 主佈局
	LinearLayout windowLayout = null;
	// 顯示的圖形listview 主佈局
	TableLayout wndLayout = null;
	// 三個類型定義
	View[] imgViw = null;
	//
	OtherGraphicAcitivityHandler otherGarphicHandler = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.other_graphic_window2);
		nowChosListCount = 0;
		currenSearchLstShowPos = 0;

		windowLayout = (LinearLayout) findViewById(R.id.other_graphic_main);
		wndLayout = (TableLayout) findViewById(R.id.dlg_size);
		// LinearLayout wndLayout = (LinearLayout) findViewById(R.id.dlg_size);
		if (null != wndLayout) {
			// wndLayout.setBackgroundColor(OtherGraphicSetting.mainWndColor);
			createImgViews();
			wndLayout.setBackgroundColor(0xFF000000);
			GetScreenWidthHeight screen_set = new GetScreenWidthHeight(this);
			LayoutParams lp = new LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT);
			lp = wndLayout.getLayoutParams();
			nowChosListCount = OtherGraphicSetting.IN_SYSTEM;
			// new ScanFiles().inSystemSearch_Test(this,
			// OtherGraphicSetting.IN_SYSTEM);
			// reScanAllFiles();
			otherGarphicHandler = new OtherGraphicAcitivityHandler();
			Message msg = new Message();
			msg.what = HANDLER_MSG_SCANFILES;
			otherGarphicHandler.sendMessage(msg);
		}
	}

	// handler msg
	private final int HANDLER_MSG_SCANFILES = 0;

	private class OtherGraphicAcitivityHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_MSG_SCANFILES:
				reScanAllFiles();
				break;
			}
			// super.handleMessage(msg);
		}
	}

	/**
	 * 構造title bar
	 */
	public void createImgViews() {
		int chosKind = 0;
		int kindTotalCount = OtherGraphicSetting.TOTAL_KIND_COUNT;
		imgViw = new View[kindTotalCount];
		lstViw = new ListView[kindTotalCount];
		myAdapter = new MyListAdapter[kindTotalCount];
		scanner = new ScanGraphicFiles[kindTotalCount];
		currenListShowPos = new int[kindTotalCount];
		reFreshListEndFlag = new boolean[kindTotalCount];
		prePosition = new int[kindTotalCount];

		for (int i = 0; i < kindTotalCount; i++) {
			currenListShowPos[i] = 0;
			reFreshListEndFlag[i] = true;
			prePosition[i] = 0;
		}

		// 創建 系統 介面
		chosKind = OtherGraphicSetting.IN_SYSTEM;
		imgViw[chosKind] = (View) findViewById(R.id.in_system_frame);
		lstViw[chosKind] = (ListView) findViewById(R.id.my_listview_in_system);
		// 創建 自定義介面
		chosKind = OtherGraphicSetting.IN_MYDEFINE;
		imgViw[chosKind] = (View) findViewById(R.id.in_myDefine_frame);
		lstViw[chosKind] = (ListView) findViewById(R.id.my_listview_in_mydefine);
		// 創建 收藏介面
		chosKind = OtherGraphicSetting.IN_COLLECT;
		imgViw[chosKind] = (View) findViewById(R.id.in_myCollect_frame);
		lstViw[chosKind] = (ListView) findViewById(R.id.my_listview_in_collect);

		// 設置 listener
		for (int count = 0; count < OtherGraphicSetting.TOTAL_KIND_COUNT; count++) {
			if (null != imgViw[count]) {
				setViewListener(imgViw[count], count);
			}
		}
	}

	public void setViewListener(View v, int focusView) {
		if (null != v) {
			v.setOnClickListener(new OtherGraphicTitleBarOnClickListener(this,
					imgViw, 3, v, focusView) {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					for (int count = 0; count < viwCount; count++) {
						if (count == focusCount) {
							OtherGraphicFunc.setOtherGraphicUI(parentContext,
									focusCount);
							nowChosListCount = focusCount;
							continue;
						} else {
							imgViw[count]
									.setBackgroundColor(OtherGraphicSetting.titleBarUnFocusColor);
						}
					}

					v.setBackgroundColor(OtherGraphicSetting.titleBarFocusColor);

				}
			});

			v.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						v.setBackgroundColor(OtherGraphicSetting.titleBarFocusColor);
						break;
					case MotionEvent.ACTION_UP:
						v.setBackgroundColor(OtherGraphicSetting.titleBarUnFocusColor);
						break;
					}
					return false;
				}
			});
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_BACK:
			Intent intent = new Intent();
			intent.putExtra(OtherGraphicSetting.GRAPHIC_RESULT_PICNAME, "");
			intent.putExtra(OtherGraphicSetting.GRAPHIC_RESULT_PATH, "");
			setResult(AnswerPaperFun.RESULT_CODE_OTHERGRAPHIC, intent);
			myFinish(intent);
			break;
		}
		// return false;
		return super.onKeyDown(keyCode, event);
	}

	private void myFinish(Intent intent) {
		setResult(AnswerPaperFun.RESULT_CODE_OTHERGRAPHIC, intent);
		finish();
	}

	/**
	 * 重新掃描盤符
	 * 
	 * @param inputKind
	 *            掃描類型
	 */
	public void reScanAllFiles() {
		int chosKind = 0;

		initializeList();
		// Loading 系統list 介面
		chosKind = OtherGraphicSetting.IN_SYSTEM;
		// scanner[chosKind] = new ScanGraphicFiles(OtherGraphicActivity.this,
		// chosKind);
		new LoadingList(scanner[chosKind], chosKind, 1).start();
		// Loading 自定義list 介面
		chosKind = OtherGraphicSetting.IN_MYDEFINE;
		// scanner[chosKind] = new ScanGraphicFiles(OtherGraphicActivity.this,
		// chosKind);
		new LoadingList(scanner[chosKind], chosKind, 1).start();
		// Loading 收藏List 介面
	}

	/**
	 * 初始化搜索Loading
	 */
	public void initializeList() {
		// system list
		View loadingBar = (View) findViewById(R.id.loading_bar_in_system);
		View listLayout = (View) findViewById(R.id.list_in_system);
		if (null != loadingBar && null != listLayout) {
			loadingBar.setVisibility(View.VISIBLE);
			listLayout.setVisibility(View.GONE);
		}

		loadingBar = (View) findViewById(R.id.loading_bar_in_mydefine);
		listLayout = (View) findViewById(R.id.list_in_mydefine);
		if (null != loadingBar && null != listLayout) {
			loadingBar.setVisibility(View.VISIBLE);
			listLayout.setVisibility(View.GONE);
		}
		loadingBar = (View) findViewById(R.id.loading_bar_in_collect);
		listLayout = (View) findViewById(R.id.list_in_collect);
		if (null != loadingBar && null != listLayout) {
			loadingBar.setVisibility(View.VISIBLE);
			listLayout.setVisibility(View.GONE);
		}
	}

	/**
	 * 點擊item 獲取圖像信息
	 * 
	 * @param data
	 *            click item
	 * @param id
	 *            position
	 */
	public void addOrDelListMenu(MyOneData data, long id, int chosListKind) {
		Intent intent = new Intent();
		intent.putExtra(OtherGraphicSetting.GRAPHIC_RESULT_PICNAME,
				data.pic_name);
		intent.putExtra(OtherGraphicSetting.GRAPHIC_RESULT_PATH,
				data.absolute_path);
		intent.putExtra(OtherGraphicSetting.GRAPHIC_RESULT_LIST_KIND,
				chosListKind);
		setResult(AnswerPaperFun.RESULT_CODE_OTHERGRAPHIC, intent);
		myFinish(intent);
	}

	public class LoadingList extends Thread {
		ScanGraphicFiles scanner; // 搜索出的info
		int inputKind; // 系統？ 自定義？ 收藏？
		int chosLoadingKind; // 選擇是第一次加載還是之後的刷新加載

		public LoadingList(ScanGraphicFiles scanner, int inputKind,
				int chosLoadingKind) {
			this.scanner = scanner;
			this.inputKind = inputKind;
			this.chosLoadingKind = chosLoadingKind;
		}

		public void run() {
			// TODO Auto-generated method stub
			reFreshListEndFlag[this.inputKind] = false;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			scanner = new ScanGraphicFiles(OtherGraphicActivity.this, inputKind);
			switch (this.inputKind) {
			case OtherGraphicSetting.IN_SYSTEM:
				prepareList_In_System(this.scanner, this.inputKind, 0);
				break;
			case OtherGraphicSetting.IN_MYDEFINE:
				prepareList_In_MyDefine(this.scanner, this.inputKind, 0);
				break;
			case OtherGraphicSetting.IN_COLLECT:
				break;
			}

			Message msg = new Message();
			msg.what = 0;
			msg.arg1 = this.chosLoadingKind;
			msg.arg2 = this.inputKind;
			mHandler.sendMessage(msg);
			// super.run();
		}
	}

	// 所有線程返回的message
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if (null != lstViw && 1 == msg.arg1) {
					int chosKind = msg.arg2;
					lstViw[chosKind].setAdapter(myAdapter[chosKind]);
					lstViw[chosKind].setDividerHeight(1);
					lstViw[chosKind].setCacheColorHint(Color.TRANSPARENT);

					lstViw[chosKind]
							.setOnTouchListener(new OtherGraphicListOnTouchListener(
									chosKind) {

								@Override
								public boolean onTouch(View v, MotionEvent event) {
									// TODO Auto-generated method stub
									switch (event.getAction()) {
									case MotionEvent.ACTION_DOWN:
										Log.d("========", "DOWN");
										touchMove = false;
										// lstViw.setOnItemLongClickListener(long_listener);
										break;
									case MotionEvent.ACTION_UP:
										Log.d("========", "UP");
										// lstViw[nowChosListCount]
										// .setOnItemClickListener(listener_OnClick);
										break;
									case MotionEvent.ACTION_MOVE:
										if (!touchMove) {
											Log.d("========", "MOVE");
											touchMove = true;
											if (chooseSearchOrOldAdapter == 0) {
												switch (chosKind) {
												case OtherGraphicSetting.IN_SYSTEM:
													if (prePosition[chosKind] < nowList_in_system
															.size()) {
														nowList_in_system
																.get(prePosition[chosKind]).isScrollFocuses = false;
														nowList_in_system
																.get(prePosition[chosKind]).isAdditonScroll = false;
													}
													break;
												case OtherGraphicSetting.IN_MYDEFINE:
													break;
												case OtherGraphicSetting.IN_COLLECT:
													break;
												}
											} else if (chooseSearchOrOldAdapter == 1) {
												switch (chosKind) {
												case OtherGraphicSetting.IN_SYSTEM:
													if (prePosition[chosKind] < searchData
															.size()) {
														searchData
																.get(prePosition[chosKind]).isScrollFocuses = false;
														searchData
																.get(prePosition[chosKind]).isAdditonScroll = false;
													}
													break;
												case OtherGraphicSetting.IN_MYDEFINE:
													break;
												case OtherGraphicSetting.IN_COLLECT:
													break;
												}
											}

											myAdapter[chosKind]
													.notifyDataSetChanged();
										}
										break;
									}
									return false;
								}
							});
					lstViw[chosKind]
							.setOnItemClickListener(new OtherGraphicListOnItemClickListener(
									chosKind) {

								@Override
								public void onItemClick(AdapterView<?> arg0,
										View arg1, int arg2, long arg3) {
									// TODO Auto-generated method stub
									Log.d(Tag, "" + arg2);
									if (!checkOne) {
										checkOne = true;
										switch (chosKind) {
										case OtherGraphicSetting.IN_SYSTEM:
											if (nowList_in_system.get(arg2).pic_name
													.equals(LoadingPreOneData)) {
												if (reFreshListEndFlag[chosKind]) {
													reFreshListEndFlag[chosKind] = false;
													if (null != lstViw[chosKind]) {
														lstViw[chosKind]
																.setSelectionAfterHeaderView();
													}
													reFreshListEndFlag[chosKind] = true;
												}
											} else if (nowList_in_system
													.get(arg2).pic_name
													.equals(LoadingNextOneData)) {
												if (reFreshListEndFlag[chosKind]) {
													reFreshListEndFlag[chosKind] = false;
													if (null != lstViw[chosKind]) {
														lstViw[chosKind]
																.setSelectionAfterHeaderView();
													}
													reFreshListEndFlag[chosKind] = true;
												}
											} else if (chooseSearchOrOldAdapter == 0) {
												addOrDelListMenu(
														nowList_in_system
																.get(arg2),
														arg3, chosKind);
											} else if (chooseSearchOrOldAdapter == 1) {
												addOrDelListMenu(
														searchData.get(arg2),
														arg3, chosKind);
											}
											break;
										case OtherGraphicSetting.IN_MYDEFINE:
											// 解析文件
											if (nowList_in_mydefine.get(arg2).pic_name
													.equals(LoadingPreOneData)) {
												if (reFreshListEndFlag[chosKind]) {
													reFreshListEndFlag[chosKind] = false;
													if (null != lstViw[chosKind]) {
														lstViw[chosKind]
																.setSelectionAfterHeaderView();
													}
													reFreshListEndFlag[chosKind] = true;
												}
											} else if (nowList_in_mydefine
													.get(arg2).pic_name
													.equals(LoadingNextOneData)) {
												if (reFreshListEndFlag[chosKind]) {
													reFreshListEndFlag[chosKind] = false;
													if (null != lstViw[chosKind]) {
														lstViw[chosKind]
																.setSelectionAfterHeaderView();
													}
													reFreshListEndFlag[chosKind] = true;
												}
											} else if (chooseSearchOrOldAdapter == 0) {
												addOrDelListMenu(
														nowList_in_mydefine
																.get(arg2),
														arg3, chosKind);
											} else if (chooseSearchOrOldAdapter == 1) {
												addOrDelListMenu(
														searchData.get(arg2),
														arg3, chosKind);
											}
											break;
										case OtherGraphicSetting.IN_COLLECT:
											break;
										}
									}
									checkOne = false;
								}
							});
					lstViw[chosKind]
							.setOnItemSelectedListener(new OtherGraphicListOnItemSelectListener(
									chosKind) {

								@Override
								public void onItemSelected(AdapterView<?> arg0,
										View arg1, int arg2, long arg3) {
									// TODO Auto-generated method stub
									Log.d(Tag, "onItemSelected");
									switch (chosKind) {
									case OtherGraphicSetting.IN_SYSTEM:
										if (chooseSearchOrOldAdapter == 0) {
											if (prePosition[chosKind] < nowList_in_system
													.size()) {
												nowList_in_system
														.get(prePosition[chosKind]).isScrollFocuses = false;
												nowList_in_system
														.get(prePosition[chosKind]).isAdditonScroll = false;
											}
											prePosition[chosKind] = arg2;
											nowList_in_system.get(arg2).isScrollFocuses = true;
											nowList_in_system.get(arg2).isAdditonScroll = true;
										} else if (chooseSearchOrOldAdapter == 1) {
											if (prePosition[chosKind] < searchData
													.size()) {
												searchData
														.get(prePosition[chosKind]).isScrollFocuses = false;
												searchData
														.get(prePosition[chosKind]).isAdditonScroll = false;
											}
											prePosition[chosKind] = arg2;
											searchData.get(arg2).isScrollFocuses = true;
											searchData.get(arg2).isAdditonScroll = true;
										}

										myAdapter[chosKind]
												.notifyDataSetChanged();
										break;
									case OtherGraphicSetting.IN_MYDEFINE:
										break;
									case OtherGraphicSetting.IN_COLLECT:
										break;
									}

								}

								@Override
								public void onNothingSelected(
										AdapterView<?> arg0) {
									// TODO Auto-generated method stub

								}

							});
					// lstViw[chosKind]
					// .setOnItemLongClickListener(onLongTouchListener);
					// lstViw[chosKind].setOnScrollListener(onScrollListener);
				}
				if (null != myAdapter[nowChosListCount]) {
					myAdapter[nowChosListCount].notifyDataSetChanged();
				}
				View loadingBar = null;
				View listLayout = null;
				switch (msg.arg2) {
				case OtherGraphicSetting.IN_SYSTEM:
					loadingBar = (View) findViewById(R.id.loading_bar_in_system);
					listLayout = (View) findViewById(R.id.list_in_system);
					if (null != loadingBar && null != listLayout) {
						loadingBar.setVisibility(View.GONE);
						listLayout.setVisibility(View.VISIBLE);
						reFreshListEndFlag[msg.arg2] = true;
					}
					break;
				case OtherGraphicSetting.IN_MYDEFINE:
					loadingBar = (View) findViewById(R.id.loading_bar_in_mydefine);
					listLayout = (View) findViewById(R.id.list_in_mydefine);
					if (null != loadingBar && null != listLayout) {
						loadingBar.setVisibility(View.GONE);
						listLayout.setVisibility(View.VISIBLE);
						reFreshListEndFlag[msg.arg2] = true;
					}
					break;
				case OtherGraphicSetting.IN_COLLECT:
					loadingBar = (View) findViewById(R.id.loading_bar_in_collect);
					listLayout = (View) findViewById(R.id.list_in_collect);
					if (null != loadingBar && null != listLayout) {
						loadingBar.setVisibility(View.GONE);
						listLayout.setVisibility(View.VISIBLE);
						reFreshListEndFlag[msg.arg2] = true;
					}
					break;
				}

				break;
			}
			super.handleMessage(msg);
		}
	};

	/**
	 * 創建System List準備的data
	 * 
	 * @param scanner
	 * @param inputKind
	 */
	public void prepareList_In_System(ScanGraphicFiles scanner, int inputKind,
			int loadingPreOrNext) {
		int chosKind = OtherGraphicSetting.IN_SYSTEM;
		int totalCount = scanner.scanFile.searchPath.size();
		int nowCount = currenListShowPos[chosKind];
		int showCount = 0;
		OtherGraphicFileAnalyse oneFileAnalyse = new OtherGraphicFileAnalyse(
				this, inputKind);

		if (null != nowList_in_system) {
			if (0 < nowList_in_system.size()) {
				nowList_in_system.clear();
				if (1 == loadingPreOrNext) {
					currenListShowPos[chosKind] = currenListShowPos[chosKind]
							- maxListShowCount * 2;
				}
				if (currenListShowPos[chosKind] > 0) {
					MyOneData loadingOneData = new MyOneData();
					loadingOneData.pic_name = LoadingPreOneData;
					nowList_in_system.add(loadingOneData);
				} else {
					currenListShowPos[chosKind] = 0;
				}
				for (nowCount = currenListShowPos[chosKind]; nowCount < totalCount
						&& showCount < maxListShowCount; nowCount++, showCount++) {
					MyOneData tmpOneData = oneFileAnalyse
							.analyseInSystem(scanner.scanFile.searchPath
									.get(nowCount));

					if (null != tmpOneData) {
						Log.i("refresh pictures==========", "" + nowCount);
						nowList_in_system.add(tmpOneData);
					}
					currenListShowPos[chosKind] = nowCount;
				}
				if (currenListShowPos[chosKind] < totalCount - 1) {
					MyOneData loadingOneData = new MyOneData();
					loadingOneData.pic_name = LoadingNextOneData;
					nowList_in_system.add(loadingOneData);
				}
				if (null != myAdapter[inputKind]) {
					myAdapter[inputKind].notifyDataSetChanged();
				}
			} else {
				for (; nowCount < totalCount && showCount < maxListShowCount; nowCount++, showCount++) {
					MyOneData tmpOneData = oneFileAnalyse
							.analyseInSystem(scanner.scanFile.searchPath
									.get(nowCount));

					if (null != tmpOneData) {
						Log.i("scanner pictures==========", "" + nowCount);
						nowList_in_system.add(tmpOneData);
					}

					currenListShowPos[chosKind] = nowCount;
				}
				if (maxListShowCount <= totalCount) {
					MyOneData loadingOneData = new MyOneData();
					loadingOneData.pic_name = LoadingNextOneData;
					nowList_in_system.add(loadingOneData);
				}
				myAdapter[inputKind] = new MyListAdapter(
						OtherGraphicActivity.this, nowList_in_system,
						chooseSearchOrOldAdapter, nowList_in_system);
			}
		}
	}

	/**
	 * 創建MyDefine List準備的data
	 * 
	 * @param scanner
	 * @param inputKind
	 */
	public void prepareList_In_MyDefine(ScanGraphicFiles scanner,
			int inputKind, int loadingPreOrNext) {
		int chosKind = OtherGraphicSetting.IN_MYDEFINE;
		int totalCount = scanner.scanFile.searchPath.size();
		int nowCount = currenListShowPos[chosKind];
		int showCount = 0;
		OtherGraphicFileAnalyse oneFileAnalyse = new OtherGraphicFileAnalyse(
				this, inputKind);

		if (null != nowList_in_mydefine) {
			if (0 < nowList_in_mydefine.size()) {
				nowList_in_mydefine.clear();
				if (1 == loadingPreOrNext) {
					currenListShowPos[chosKind] = currenListShowPos[chosKind]
							- maxListShowCount * 2;
				}
				if (currenListShowPos[chosKind] > 0) {
					MyOneData loadingOneData = new MyOneData();
					loadingOneData.pic_name = LoadingPreOneData;
					nowList_in_mydefine.add(loadingOneData);
				} else {
					currenListShowPos[chosKind] = 0;
				}
				for (nowCount = currenListShowPos[chosKind]; nowCount < totalCount
						&& showCount < maxListShowCount; nowCount++, showCount++) {
					MyOneData tmpOneData = oneFileAnalyse
							.analyseInMyDefine(scanner.scanFile.searchPath
									.get(nowCount));

					if (null != tmpOneData) {
						Log.i("refresh pictures==========", "" + nowCount);
						nowList_in_mydefine.add(tmpOneData);
					}
					currenListShowPos[chosKind] = nowCount;
				}
				if (currenListShowPos[chosKind] < totalCount - 1) {
					MyOneData loadingOneData = new MyOneData();
					loadingOneData.pic_name = LoadingNextOneData;
					nowList_in_mydefine.add(loadingOneData);
				}
				if (null != myAdapter[inputKind]) {
					myAdapter[inputKind].notifyDataSetChanged();
				}
			} else {
				for (; nowCount < totalCount && showCount < maxListShowCount; nowCount++, showCount++) {
					MyOneData tmpOneData = oneFileAnalyse
							.analyseInMyDefine(scanner.scanFile.searchPath
									.get(nowCount));

					if (null != tmpOneData) {
						Log.i("scanner pictures==========", "" + nowCount);
						nowList_in_mydefine.add(tmpOneData);
					}

					currenListShowPos[chosKind] = nowCount;
				}
				if (maxListShowCount <= totalCount) {
					MyOneData loadingOneData = new MyOneData();
					loadingOneData.pic_name = LoadingNextOneData;
					nowList_in_mydefine.add(loadingOneData);
				}
				myAdapter[inputKind] = new MyListAdapter(
						OtherGraphicActivity.this, nowList_in_mydefine,
						chooseSearchOrOldAdapter, nowList_in_mydefine);
			}
		}
	}
}
