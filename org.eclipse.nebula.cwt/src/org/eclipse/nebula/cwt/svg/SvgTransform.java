package org.eclipse.nebula.cwt.svg;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

public class SvgTransform {

	enum Type {
		Matrix, Translate, Scale, Rotate, SkewX, SkewY
	};

	SvgTransform next;
	float[] data;

	SvgTransform() {
		data = new float[] { 1, 0, 0, 1, 0, 0 };
	}
	
	float[] apply(float x, float y) {
//		switch(type) {
//		case Matrix:
//		case Translate:
//		case Scale:
		float[] v = new float[2];
		v[0] = data[0] * x + data[2] * y + data[4];
		v[1] = data[1] * x + data[3] * y + data[5];
		return v;
//			break;
//		case Rotate:
//			v[0] = (float)(cos(data[0]) * v[0] - sin(data[2]) * v[1] + data[4]);
//			v[1] = (float)(sin(data[1]) * v[0] + cos(data[3]) * v[1] + data[5]);
//			break;
//		case SkewX:
//			v[0] = (float)(data[0] * v[0] + tan(data[2]) * v[1] + data[4]);
//			v[1] = data[1] * v[0] + data[3] * v[1] + data[5];
//			break;
//		case SkewY:
//			v[0] = data[0] * v[0] + data[2] * v[1] + data[4];
//			v[1] = (float)(tan(data[1]) * v[0] + data[3] * v[1] + data[5]);
//			break;
//		}
	}

	public boolean isIdentity() {
		return data.length == 6 && data[0] == 1 && data[1] == 0 && data[2] == 0 && data[3] == 1 && data[4] == 0 && data[5] == 0;
	}
	
	void scale(float s) {
		scale(s, s);
	}

	void scale(float x, float y) {
		data[0] = x;
		data[3] = y;
	}

	void setData(Type type, String[] sa) {
		data = new float[] { 1, 0, 0, 1, 0, 0 };
		switch(type) {
		case Matrix: {
			for(int i = 0; i < sa.length; i++) {
				data[i] = Float.parseFloat(sa[i]);
			}
			break;
		}
		case Translate: {
			data[4] = Float.parseFloat(sa[0]);
			if(sa.length > 1) {
				data[5] = Float.parseFloat(sa[1]);
			}
			break;
		}
		case Scale: {
			if(sa.length == 1) {
				scale(Float.parseFloat(sa[0]));
			} else if(sa.length == 2) {
				scale(Float.parseFloat(sa[0]), Float.parseFloat(sa[1]));
			}
			break;
		}
		case Rotate: {
			float angle = Float.parseFloat(sa[0]);
			data[0] = (float) cos(toRadians(angle));
			data[1] = (float) sin(toRadians(angle));
			data[2] = (float) -sin(toRadians(angle));
			data[3] = (float) cos(toRadians(angle));
			if(sa.length > 1) {
				data[4] = Float.parseFloat(sa[1]);
				data[5] = Float.parseFloat(sa[2]);
			}
			break;
		}
		case SkewX: {
			data[2] = Float.parseFloat(sa[0]);
			break;
		}
		case SkewY: {
			data[1] = Float.parseFloat(sa[0]);
			break;
		}
		}
	}

}
