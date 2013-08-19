package com.besta.app.toolswindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.besta.app.answerpaper.drawview.DrawViewEraser;
import com.besta.app.answerpaper.othersclassinfo.AnswerPaperFun;
import com.besta.app.exerciseengine.R;

public class MyToolsWindow {
	public final static int TOOL_BAOPEN = 0;
	public final static int TOOL_PEN = 1;
	public final static int TOOL_ERASER = 2;
	public final static int TOOL_DRAW_LINE = 3;
	public final static int TOOL_DRAW_OVAL = 4;
	public final static int TOOL_DRAW_RECTANGLE = 5;
	public final static int TOOL_DRAW_TRIANGLE = 6;
	public final static int TOOL_DRAW_UNDO = 7;
	public final static int TOOL_DRAW_CLEAN = 8;
	public final static int TOOL_DRAW_OTHERS = 9;

	public final int tools_size = 10;

	private Context parentContext = null;
	public ImageButton[] imgBt_tool = null;
	public boolean[] isDisable = null;
	public int nowToolChos = 0;
	public int preToolChos = 0;
	public LinearLayout myToolsLayout = null;
	private int oneToolSize = 0;
	public int eraser_click_count = 0;

	public PopupWindow toolsWindow = null;
	public boolean toolsWindowIsShowing;

	public PopupWindow eraseWindow;
	private MyCanvas eCanvas;
	private SeekBar eSeekbar;

	public MyToolsWindow(Context context, int activity_width,
			int activity_heigth, int kind, boolean clearAllData) {
		parentContext = context;
		toolsWindowIsShowing = false;
		InitializeToolsImageButton(context, activity_width, activity_heigth,
				kind, clearAllData);
		Initialize(context);
	}

	private void Initialize(Context context) {
		if (myToolsLayout != null) {
			View tmpV = (View) ((LinearLayout) myToolsLayout);
			toolsWindow = new PopupWindow(tmpV,
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			toolsWindow.setContentView(tmpV);
			toolsWindow.setAnimationStyle(R.style.PopWindowAnimation);

			View eraseView = ((Activity) parentContext).getLayoutInflater()
					.inflate(R.layout.eraser_window, null);
			eraseWindow = new PopupWindow(eraseView, dipTopx(205.33f),
					dipTopx(119.33f));
			eraseWindow.setContentView(eraseView);
			eCanvas = (MyCanvas) eraseView.findViewById(R.id.eCanvas);
			eCanvas.Init(dipTopx(159.33f), dipTopx(60.66f));
			eCanvas.paint.setColor(Color.WHITE);
			eSeekbar = (SeekBar) eraseView.findViewById(R.id.eSeekbar);
			eraseWindow.setFocusable(true);
			eraseWindow.setBackgroundDrawable(new BitmapDrawable());
			eraseWindow.setAnimationStyle(R.style.Erazer_PopWindowAnimation);

			eCanvas.paint.setStrokeWidth(DrawViewEraser.eraseWidth);
			eSeekbar.setProgress(DrawViewEraser.eraseWidth
					- DrawViewEraser.MinEraseSize);

			eSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					eCanvas.paint.setStrokeWidth(progress
							+ DrawViewEraser.MinEraseSize);
					eCanvas.postInvalidate();
					DrawViewEraser.eraseWidth = progress
							+ DrawViewEraser.MinEraseSize;
				}
			});

			View v = eraseView.findViewById(R.id.erase_close);
			v.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					eraseWindow.dismiss();
				}
			});
		}
	}

	public int dipTopx(float dipValue) {
		final float scale = parentContext.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public int pxTodip(float pxValue) {
		final float scale = parentContext.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale);
	}

	public int GetChosItem() {
		return nowToolChos;
	}

	public void SetOthersToolFocus(int tool_chos, int tools_count,
			int pre_count, boolean cleanUp) {
		int count = 0;
		int tmpDrawable = 0;
		boolean tmpFocus = false;
		if (tool_chos == TOOL_ERASER) {
			pre_count = -1;
		}
		for (count = 0; count < tools_count; count++) {
			switch (count) {
			case TOOL_BAOPEN:
				if (count == tool_chos || count == pre_count) {
					tmpDrawable = R.drawable.baopen_press;
					tmpFocus = true;
				} else {
					tmpDrawable = R.drawable.baopen_default;
					tmpFocus = false;
				}
				break;
			case TOOL_PEN:
				if (count == tool_chos || count == pre_count) {
					tmpDrawable = R.drawable.pen_press;
					tmpFocus = true;
				} else {
					tmpDrawable = R.drawable.pen_default;
					tmpFocus = false;
				}
				break;
			case TOOL_ERASER:
				if (count == tool_chos) {
					tmpDrawable = R.drawable.eraser_press;
					tmpFocus = true;
				} else {
					tmpDrawable = R.drawable.eraser_default;
					tmpFocus = false;
				}
				break;
			case TOOL_DRAW_LINE:
				if (count == tool_chos) {
					tmpDrawable = R.drawable.line_press;
					tmpFocus = true;
				} else {
					tmpDrawable = R.drawable.line_default;
					tmpFocus = false;
				}
				break;
			case TOOL_DRAW_OVAL:
				if (count == tool_chos) {
					tmpDrawable = R.drawable.oval_press;
					tmpFocus = true;
				} else {
					tmpDrawable = R.drawable.oval_default;
					tmpFocus = false;
				}
				break;
			case TOOL_DRAW_RECTANGLE:
				if (count == tool_chos) {
					tmpDrawable = R.drawable.rectangle_press;
					tmpFocus = true;
				} else {
					tmpDrawable = R.drawable.rectangle_default;
					tmpFocus = false;
				}
				break;
			case TOOL_DRAW_TRIANGLE:
				if (count == tool_chos) {
					tmpDrawable = R.drawable.triangle_press;
					tmpFocus = true;
				} else {
					tmpDrawable = R.drawable.triangle_default;
					tmpFocus = false;
				}
				break;
			case TOOL_DRAW_UNDO:
				if (cleanUp) {
					tmpDrawable = R.drawable.undo_disabled;
				} else {
					if (isDisable[TOOL_DRAW_CLEAN]) {
						tmpDrawable = R.drawable.undo_disabled;
					} else {
						tmpDrawable = R.drawable.undo_default;
						isDisable[TOOL_DRAW_UNDO] = false;
					}
				}
				break;
			case TOOL_DRAW_CLEAN:
				if (cleanUp) {
					tmpDrawable = R.drawable.delete_disabled;
				} else {
					if (isDisable[TOOL_DRAW_CLEAN]) {
						tmpDrawable = R.drawable.delete_disabled;
					} else {
						tmpDrawable = R.drawable.delete_default;
					}
				}
				break;
			case TOOL_DRAW_OTHERS:
				if (count == tool_chos || count == pre_count) {
					tmpDrawable = R.drawable.tools_default;
					tmpFocus = true;
				} else {
					tmpDrawable = R.drawable.tools_disabled;
					tmpFocus = false;
				}
				break;
			}

			imgBt_tool[count].setBackgroundResource(tmpDrawable);
			imgBt_tool[count].setFocusable(tmpFocus);
		}
	}

	private void InitializeToolsImageButton(Context context,
			int activity_width, int activity_heigth, int kind,
			boolean clearAllData) {
		myToolsLayout = new LinearLayout(context);
		ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		if (kind == AnswerPaperFun.ACTIVITY_SOLUTION) {
			lp.bottomMargin = 10;
		} else if (kind == AnswerPaperFun.ACTIVITY_FILL
				|| kind == AnswerPaperFun.ACTIVITY_CHOICE) {
			lp.bottomMargin = 18;
		}

		myToolsLayout.setLayoutParams(param);
		myToolsLayout.setBackgroundDrawable(context.getResources().getDrawable(
				R.drawable.layout_xml));
		myToolsLayout.setOrientation(LinearLayout.VERTICAL);
		myToolsLayout.setGravity(Gravity.CENTER_HORIZONTAL);

		LinearLayout oneLineLayout = new LinearLayout(context);
		oneLineLayout.setLayoutParams(lp);
		oneLineLayout.setBackgroundDrawable(context.getResources().getDrawable(
				R.drawable.layout_xml2));
		oneLineLayout.setOrientation(LinearLayout.HORIZONTAL);
		myToolsLayout.addView(oneLineLayout);

		imgBt_tool = new ImageButton[tools_size];
		isDisable = new boolean[tools_size];
		if (activity_width > activity_heigth) {
			oneToolSize = activity_heigth / 10;
		} else {
			oneToolSize = activity_width / 10;
		}
		if (oneToolSize < 48) {
			oneToolSize = 48;
		} else if (oneToolSize > 60) {
			oneToolSize = 60;
		}
		int count = 0;
		for (count = 0; count < tools_size; count++) {
			imgBt_tool[count] = new ImageButton(context);
			param.width = param.height = oneToolSize;
			imgBt_tool[count].setLayoutParams(param);
			isDisable[count] = false;
			int tmp = 0;
			switch (count) {
			case TOOL_BAOPEN:
				tmp = R.drawable.baopen_press;
				break;
			case TOOL_PEN:
				tmp = R.drawable.pen_default;
				break;
			case TOOL_ERASER:
				tmp = R.drawable.eraser_default;
				break;
			case TOOL_DRAW_LINE:
				tmp = R.drawable.line_default;
				break;
			case TOOL_DRAW_OVAL:
				tmp = R.drawable.oval_default;
				break;
			case TOOL_DRAW_RECTANGLE:
				tmp = R.drawable.rectangle_default;
				break;
			case TOOL_DRAW_TRIANGLE:
				tmp = R.drawable.triangle_default;
				break;
			case TOOL_DRAW_UNDO:
				if (clearAllData) {
					tmp = R.drawable.undo_disabled;
					isDisable[TOOL_DRAW_UNDO] = true;
				} else {
					tmp = R.drawable.undo_default;
					isDisable[TOOL_DRAW_UNDO] = false;
				}
				break;
			case TOOL_DRAW_CLEAN:
				if (clearAllData) {
					tmp = R.drawable.delete_disabled;
					isDisable[TOOL_DRAW_CLEAN] = true;
				} else {
					tmp = R.drawable.delete_default;
					isDisable[TOOL_DRAW_CLEAN] = false;
				}
				break;
			case TOOL_DRAW_OTHERS:
				tmp = R.drawable.tools_default;
				break;
			}
			imgBt_tool[count].setBackgroundDrawable(context.getResources()
					.getDrawable(tmp));
			oneLineLayout.addView(imgBt_tool[count], count);
		}

		preToolChos = nowToolChos = 0;
	}

	public void SetListener() {
		imgBt_tool[TOOL_BAOPEN]
				.setOnTouchListener(new Button.OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (!isDisable[TOOL_BAOPEN]) {
							if (nowToolChos != TOOL_BAOPEN) {
								if (event.getAction() == MotionEvent.ACTION_DOWN) {
									imgBt_tool[TOOL_BAOPEN]
											.setBackgroundResource(R.drawable.baopen_press);
								} else if (event.getAction() == MotionEvent.ACTION_UP) {
									imgBt_tool[TOOL_BAOPEN]
											.setBackgroundResource(R.drawable.baopen_default);
								}
							}
						}
						return false;
					}
				});

		imgBt_tool[TOOL_BAOPEN]
				.setOnClickListener(new Button.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (!isDisable[TOOL_BAOPEN]) {
							preToolChos = nowToolChos = TOOL_BAOPEN;
							SetOthersToolFocus(nowToolChos, tools_size, -1,
									false);
							eraser_click_count = 0;
						}
					}
				});

		imgBt_tool[TOOL_PEN].setOnTouchListener(new Button.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (!isDisable[TOOL_PEN]) {
					if (nowToolChos != TOOL_PEN) {
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							imgBt_tool[TOOL_PEN]
									.setBackgroundResource(R.drawable.pen_press);
						} else if (event.getAction() == MotionEvent.ACTION_UP) {
							imgBt_tool[TOOL_PEN]
									.setBackgroundResource(R.drawable.pen_default);
						}
					}
				}
				return false;
			}
		});

		imgBt_tool[TOOL_PEN].setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isDisable[TOOL_PEN]) {
					preToolChos = nowToolChos = TOOL_PEN;
					SetOthersToolFocus(nowToolChos, tools_size, -1, false);
					eraser_click_count = 0;
				}
			}
		});

		imgBt_tool[TOOL_ERASER]
				.setOnTouchListener(new Button.OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (!isDisable[TOOL_ERASER]) {
							if (nowToolChos != TOOL_ERASER) {
								if (event.getAction() == MotionEvent.ACTION_DOWN) {
									imgBt_tool[TOOL_ERASER]
											.setBackgroundResource(R.drawable.eraser_press);
								} else if (event.getAction() == MotionEvent.ACTION_UP) {
									imgBt_tool[TOOL_ERASER]
											.setBackgroundResource(R.drawable.eraser_default);
								}
							}
						}
						return false;
					}
				});

		imgBt_tool[TOOL_DRAW_LINE]
				.setOnTouchListener(new Button.OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (!isDisable[TOOL_DRAW_LINE]) {
							if (nowToolChos != TOOL_DRAW_LINE) {
								if (event.getAction() == MotionEvent.ACTION_DOWN) {
									imgBt_tool[TOOL_DRAW_LINE]
											.setBackgroundResource(R.drawable.line_press);
								} else if (event.getAction() == MotionEvent.ACTION_UP) {
									imgBt_tool[TOOL_DRAW_LINE]
											.setBackgroundResource(R.drawable.line_default);
								}
							}
						}
						return false;
					}
				});

		imgBt_tool[TOOL_DRAW_LINE]
				.setOnClickListener(new Button.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (!isDisable[TOOL_DRAW_LINE]) {
							nowToolChos = TOOL_DRAW_LINE;
							SetOthersToolFocus(nowToolChos, tools_size,
									preToolChos, false);
							eraser_click_count = 0;
						}
					}
				});

		imgBt_tool[TOOL_DRAW_OVAL]
				.setOnTouchListener(new Button.OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (!isDisable[TOOL_DRAW_OVAL]) {
							if (nowToolChos != TOOL_DRAW_OVAL) {
								if (event.getAction() == MotionEvent.ACTION_DOWN) {
									imgBt_tool[TOOL_DRAW_OVAL]
											.setBackgroundResource(R.drawable.oval_press);
								} else if (event.getAction() == MotionEvent.ACTION_UP) {
									imgBt_tool[TOOL_DRAW_OVAL]
											.setBackgroundResource(R.drawable.oval_default);
								}
							}
						}
						return false;
					}
				});

		imgBt_tool[TOOL_DRAW_OVAL]
				.setOnClickListener(new Button.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (!isDisable[TOOL_DRAW_OVAL]) {
							nowToolChos = TOOL_DRAW_OVAL;
							SetOthersToolFocus(nowToolChos, tools_size,
									preToolChos, false);
							eraser_click_count = 0;
						}
					}
				});

		imgBt_tool[TOOL_DRAW_RECTANGLE]
				.setOnTouchListener(new Button.OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (!isDisable[TOOL_DRAW_RECTANGLE]) {
							if (nowToolChos != TOOL_DRAW_RECTANGLE) {
								if (event.getAction() == MotionEvent.ACTION_DOWN) {
									imgBt_tool[TOOL_DRAW_RECTANGLE]
											.setBackgroundResource(R.drawable.rectangle_press);
								} else if (event.getAction() == MotionEvent.ACTION_UP) {
									imgBt_tool[TOOL_DRAW_RECTANGLE]
											.setBackgroundResource(R.drawable.rectangle_default);
								}
							}
						}
						return false;
					}
				});

		imgBt_tool[TOOL_DRAW_RECTANGLE]
				.setOnClickListener(new Button.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (!isDisable[TOOL_DRAW_RECTANGLE]) {
							nowToolChos = TOOL_DRAW_RECTANGLE;
							SetOthersToolFocus(nowToolChos, tools_size,
									preToolChos, false);
							eraser_click_count = 0;
						}
					}
				});

		imgBt_tool[TOOL_DRAW_TRIANGLE]
				.setOnTouchListener(new Button.OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (!isDisable[TOOL_DRAW_TRIANGLE]) {
							if (nowToolChos != TOOL_DRAW_TRIANGLE) {
								if (event.getAction() == MotionEvent.ACTION_DOWN) {
									imgBt_tool[TOOL_DRAW_TRIANGLE]
											.setBackgroundResource(R.drawable.triangle_press);
								} else if (event.getAction() == MotionEvent.ACTION_UP) {
									imgBt_tool[TOOL_DRAW_TRIANGLE]
											.setBackgroundResource(R.drawable.triangle_default);
								}
							}
						}
						return false;
					}
				});

		imgBt_tool[TOOL_DRAW_TRIANGLE]
				.setOnClickListener(new Button.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (!isDisable[TOOL_DRAW_TRIANGLE]) {
							nowToolChos = TOOL_DRAW_TRIANGLE;
							SetOthersToolFocus(nowToolChos, tools_size,
									preToolChos, false);
							eraser_click_count = 0;
						}
					}
				});

		imgBt_tool[TOOL_DRAW_OTHERS]
				.setOnTouchListener(new Button.OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (!isDisable[TOOL_DRAW_OTHERS]) {
							if (nowToolChos != TOOL_DRAW_OTHERS) {
								if (event.getAction() == MotionEvent.ACTION_DOWN) {
									imgBt_tool[TOOL_DRAW_OTHERS]
											.setBackgroundResource(R.drawable.tools_default);
								} else if (event.getAction() == MotionEvent.ACTION_UP) {
									imgBt_tool[TOOL_DRAW_OTHERS]
											.setBackgroundResource(R.drawable.tools_disabled);
								}
							}
						}
						return false;
					}
				});

		imgBt_tool[TOOL_DRAW_OTHERS]
				.setOnClickListener(new Button.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (!isDisable[TOOL_DRAW_OTHERS]) {
							nowToolChos = TOOL_DRAW_OTHERS;
							SetOthersToolFocus(nowToolChos, tools_size,
									preToolChos, false);
							eraser_click_count = 0;
						}
					}
				});

	}
}
