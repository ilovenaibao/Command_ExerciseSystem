package com.besta.app.geometry;

public class GeometryDataStruct {
	public class SpecificPoint {
		public float x, y;

		public SpecificPoint() {
			x = y = 0;
		}
	}

	public class TrianglePoint {
		public float x1, y1, x2, y2, x3, y3;

		public TrianglePoint() {
			x1 = y1 = x2 = y2 = x3 = y3 = -10;
		}

		public void ResetTrianglePoint() {
			x1 = y1 = x2 = y2 = x3 = y3 = -10;
		}
	}
}
