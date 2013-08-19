package com.besta.app.answerpaper.mywebview;

import com.besta.app.answerpaper.drawview.DrawView;

import android.graphics.Bitmap;
import android.view.View;

public class MyWebViewCaller {
	public interface MyCallInterface {
		public Bitmap GetSelectPic();

		public String GetSelectPicPath();

		public DrawView GetMyView();

		public String RefreshView(DrawView view);
	}

	public class Caller {
		private MyCallInterface mc;
		public Bitmap selectPic = null;
		public String selectPic_path = null;
		DrawView myView = null;

		public Caller() {
		}

		public void SetI(MyCallInterface mc) {
			this.mc = mc;
		}

		// Caller的调用方法
		public Bitmap GetSelectPic() {
			if (mc != null) {
				selectPic = mc.GetSelectPic();
				myView = mc.GetMyView();
			}
			return selectPic;
		}

		public void setDrawView(DrawView dv) {
			this.myView = dv;
		}

		public String GetSelectPicPath() {
			if (mc != null) {
				selectPic_path = mc.GetSelectPicPath();
			}
			return selectPic_path;
		}

		public void RefreshMyView() {
			if (mc != null) {
				mc.RefreshView(myView);
			}
		}
	}

	public class Callee implements MyCallInterface {
		public Bitmap selectPic = null;
		public String selectPic_path = null;
		public DrawView myView = null;

		public Callee() {

		}

		public void SetSelectPic(Bitmap src, String src_path, DrawView myView) {
			selectPic_path = new String(src_path);
			if (null != src) {
				selectPic = Bitmap.createBitmap(src);
			}
			this.myView = myView;
		}

		@Override
		public Bitmap GetSelectPic() {
			return selectPic;
		}

		@Override
		public String GetSelectPicPath() {
			return selectPic_path;
		}

		@Override
		public String RefreshView(DrawView view) {
			// TODO Auto-generated method stub
			if (null != view) {
				view.invalidate();
			}
			return null;
		}

		@Override
		public DrawView GetMyView() {
			// TODO Auto-generated method stub
			return myView;
		}
	}
}
