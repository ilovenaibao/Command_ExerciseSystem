package com.besta.app.answerpaper.othergraphics;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.besta.app.exerciseengine.R;

public class MyListAdapter extends BaseAdapter {

	Context parentContext;
	ArrayList<MyOneData> totalList;
	ArrayList<MyOneData> dataList;
	int iconWidth;
	int iconHeight;
	int searchOrList = 0;
	int padding = 0;

	public MyListAdapter(Context view, ArrayList<MyOneData> list, int chos,
			ArrayList<MyOneData> dataList) {
		parentContext = view;
		searchOrList = chos;
		totalList = list;
		this.dataList = dataList;
		iconWidth = parentContext.getResources().getDrawable(R.drawable.close)
				.getIntrinsicWidth();
		iconHeight = parentContext.getResources().getDrawable(R.drawable.close)
				.getIntrinsicHeight();

		padding = 0;
	}

	@Override
	public int getCount() {

		return totalList.size();
	}

	public void SetSearchOrList(int chos) {
		searchOrList = chos;
	}

	@Override
	public Object getItem(int position) {
		return totalList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(this.parentContext);
		View view = inflater.inflate(R.layout.other_graphic_list, null);

		MyOneData oneData = new MyOneData();
		oneData = totalList.get(position);

		if (oneData.pic_name.equals(OtherGraphicActivity.LoadingNextOneData)
				|| oneData.pic_name
						.equals(OtherGraphicActivity.LoadingPreOneData)) {
			MyTextViewSet tv = (MyTextViewSet) view
					.findViewById(R.id.my_list_tv_1);
			if (oneData.pic_name
					.equals(OtherGraphicActivity.LoadingNextOneData)) {
				tv.setPadding(0, 10, 0, 0);
			}
			tv.setText("加載更多項");
			tv.setTextColor(Color.WHITE);
			tv.setGravity(Gravity.CENTER);
			// tv.setLayoutParams(new LayoutParams(
			// ViewGroup.LayoutParams.MATCH_PARENT,
			// ViewGroup.LayoutParams.WRAP_CONTENT));
			if (oneData.isScrollFocuses) {
				tv.scrollFlag = true;
			} else {
				tv.scrollFlag = false;
			}
			tv.isFocusable();
			tv.setHorizontallyScrolling(true);
			tv.setEllipsize(TruncateAt.MARQUEE);
			tv.setMarqueeRepeatLimit(-1);
		} else {
			ImageView imgViw = (ImageView) view.findViewById(R.id.imgView);
			imgViw.setPadding((oneData.layer * iconWidth), 2, 0, 2);
			if (null != oneData.picInfo.bmp) {
				imgViw.setImageBitmap(oneData.picInfo.bmp);
			}

			MyTextViewSet tv = (MyTextViewSet) view
					.findViewById(R.id.my_list_tv_1);
			MyTextViewSet additionTv = (MyTextViewSet) view
					.findViewById(R.id.my_list_tv_2);
			// oneData.additionName =
			// "jfsdljfljsdlfksfdsfdsdfewrewwrwrwrewrewr32435435dfjfsdljfljsdlfksfdsfdsdfewrewwrwrwrewrewr32435435dfjfsdljfljsdlfksfdsfdsdfewrewwrwrwrewrewr32435435dfjfsdljfljsdlfksfdsfdsdfewrewwrwrwrewrewr32435435dfjfsdljfljsdlfksfdsfdsdfewrewwrwrwrewrewr32435435dfjfsdljfljsdlfksfdsfdsdfewrewwrwrwrewrewr32435435dfjfsdljfljsdlfksfdsfdsdfewrewwrwrwrewrewr32435435dfjfsdljfljsdlfksfdsfdsdfewrewwrwrwrewrewr32435435dfjfsdljfljsdlfksfdsfdsdfewrewwrwrwrewrewr32435435df";
			if (oneData.additionName.equals("")) {
				tv.setPadding((oneData.layer * iconWidth) + 5, 12, 0, 0);
				additionTv.setTextSize(0);
				additionTv.setPadding(0, 0, 0, 0);
			} else {
				if (searchOrList == 0) {
					tv.setPadding((oneData.layer * iconWidth) + 5, 6, 0, 0);
					additionTv.setTextSize(12);
					// additionTv.setPadding((oneData.layer * iconWidth) + 5, 0,
					// 0,
					// 6);
				} else if (searchOrList == 1) {
					tv.setPadding(iconWidth + 5, 6, 0, 0);
					additionTv.setTextSize(12);
					additionTv.setPadding(iconWidth + 5, 0, 0, 6);
				}
			}
			additionTv.setText(oneData.additionName);
			if (oneData.isAdditonScroll) {
				additionTv.additionScrollFlag = true;
			} else {
				additionTv.additionScrollFlag = false;
			}
			additionTv.isFocusable();
			additionTv.setHorizontallyScrolling(true);
			additionTv.setEllipsize(TruncateAt.MARQUEE);
			additionTv.setMarqueeRepeatLimit(-1);

			if (!oneData.isChild) {
				if (searchOrList == 0) {
					tv.setPadding((oneData.layer * iconWidth) + 5, 12, 0, 0);
				} else if (searchOrList == 1) {
					tv.setPadding(iconWidth + 5, 12, 0, 0);
				}
			} else {
				tv.setPadding(5, 12, 0, 0);
			}
			tv.setText(oneData.pic_name);
			tv.setTextColor(Color.WHITE);
			if (oneData.isScrollFocuses) {
				tv.scrollFlag = true;
			} else {
				tv.scrollFlag = false;
			}
			tv.isFocusable();
			tv.setHorizontallyScrolling(true);
			tv.setEllipsize(TruncateAt.MARQUEE);
			tv.setMarqueeRepeatLimit(-1);
		}

		return view;
	}
}
