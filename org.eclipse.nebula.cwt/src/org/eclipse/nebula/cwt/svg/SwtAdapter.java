/****************************************************************************
 * Copyright (c) 2008, 2009 Jeremy Dowdall
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *****************************************************************************/
package org.eclipse.nebula.cwt.svg;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Pattern;

class SwtAdapter {

	static final boolean carbon = "carbon".equals(SWT.getPlatform()); //$NON-NLS-1$
	static final boolean gtk = "gtk".equals(SWT.getPlatform()); //$NON-NLS-1$
	static final boolean win32 = "win32".equals(SWT.getPlatform()); //$NON-NLS-1$

	static final String CAIRO = "org.eclipse.swt.internal.cairo.Cairo"; //$NON-NLS-1$
	static final String GDIP = "org.eclipse.swt.internal.gdip.Gdip"; //$NON-NLS-1$
	static final String POINTF = "org.eclipse.swt.internal.gdip.PointF"; //$NON-NLS-1$

	private static Class<?> cairo;
	private static Class<?> gdip;
	private static Class<?> pointf;

	private static Field pointfX;
	private static Field pointfY;

	private static Method cairo_matrix_init;
	private static Method cairo_matrix_invert;
	private static Method cairo_matrix_multiply;
	private static Method cairo_pattern_add_color_stop_rgba;
	private static Method cairo_pattern_create_linear;
	private static Method cairo_pattern_create_radial;
	private static Method cairo_pattern_destroy;
	private static Method cairo_pattern_set_extend;
	private static Method cairo_pattern_set_matrix;

	private static Method gdip_Color_delete;
	private static Method gdip_Color_new;
	private static Method gdip_Matrix_delete;
	private static Method gdip_Matrix_GetElements;
	private static Method gdip_Matrix_Multiply;
	private static Method gdip_Matrix_new;
	private static Method gdip_GraphicsPath_AddArc;
	private static Method gdip_GraphicsPath_delete;
	private static Method gdip_GraphicsPath_new;
	private static Method gdip_GraphicsPath_Transform;
	private static Method gdip_PathGradientBrush_new;
	private static Method gdip_PathGradientBrush_SetInterpolationColors;
	private static Method gdip_LinearGradientBrush_new;
	private static Method gdip_LinearGradientBrush_TranslateTransform;
	private static Method gdip_LinearGradientBrush_ScaleTransform;
	private static Method gdip_LinearGradientBrush_SetInterpolationColors;
	private static Method gdip_LinearGradientBrush_SetWrapMode;
	private static Method gdip_SolidBrush_delete;

	private static Class<?> handleClass;

	static {
		try {
			handleClass = Pattern.class.getField("handle").getType(); //$NON-NLS-1$
			if(gtk) {
				cairo = Class.forName(CAIRO, true, SWT.class.getClassLoader());
				cairo_matrix_init = cairo.getDeclaredMethod("cairo_matrix_init", double[].class, double.class, double.class, double.class, //$NON-NLS-1$
						double.class, double.class, double.class);
				cairo_matrix_invert = cairo.getDeclaredMethod("cairo_matrix_invert", double[].class); //$NON-NLS-1$
				cairo_matrix_multiply = cairo.getDeclaredMethod("cairo_matrix_multiply", double[].class, double[].class, double[].class); //$NON-NLS-1$
				cairo_pattern_add_color_stop_rgba = cairo.getDeclaredMethod("cairo_pattern_add_color_stop_rgba", handleClass, double.class, //$NON-NLS-1$
						double.class, double.class, double.class, double.class);
				cairo_pattern_create_linear = cairo.getDeclaredMethod("cairo_pattern_create_linear", double.class, double.class, //$NON-NLS-1$
						double.class, double.class);
				cairo_pattern_create_radial = cairo.getDeclaredMethod("cairo_pattern_create_radial", double.class, double.class, //$NON-NLS-1$
						double.class, double.class, double.class, double.class);
				cairo_pattern_destroy = cairo.getDeclaredMethod("cairo_pattern_destroy", handleClass); //$NON-NLS-1$
				cairo_pattern_set_extend = cairo.getDeclaredMethod("cairo_pattern_set_extend", handleClass, int.class); //$NON-NLS-1$
				cairo_pattern_set_matrix = cairo.getDeclaredMethod("cairo_pattern_set_matrix", handleClass, double[].class); //$NON-NLS-1$
			} else if(win32) {
				gdip = Class.forName(GDIP, true, SWT.class.getClassLoader());
				pointf = Class.forName(POINTF, true, SWT.class.getClassLoader());
				pointfX = pointf.getDeclaredField("X"); //$NON-NLS-1$
				pointfY = pointf.getDeclaredField("Y"); //$NON-NLS-1$
				gdip_Color_delete = gdip.getDeclaredMethod("Color_delete", int.class); //$NON-NLS-1$
				gdip_Color_new = gdip.getDeclaredMethod("Color_new", int.class); //$NON-NLS-1$
				gdip_Matrix_delete = gdip.getDeclaredMethod("Matrix_delete", int.class); //$NON-NLS-1$
				gdip_Matrix_GetElements = gdip.getDeclaredMethod("Matrix_GetElements", int.class, float[].class); //$NON-NLS-1$
				gdip_Matrix_Multiply = gdip.getDeclaredMethod("Matrix_Multiply", int.class, int.class, int.class); //$NON-NLS-1$
				gdip_Matrix_new = gdip.getDeclaredMethod("Matrix_new", float.class, float.class, float.class, float.class, float.class, //$NON-NLS-1$
						float.class);
				gdip_GraphicsPath_AddArc = gdip.getDeclaredMethod("GraphicsPath_AddArc", int.class, float.class, float.class, float.class, //$NON-NLS-1$
						float.class, float.class, float.class);
				gdip_GraphicsPath_delete = gdip.getDeclaredMethod("GraphicsPath_delete", int.class); //$NON-NLS-1$
				gdip_GraphicsPath_new = gdip.getDeclaredMethod("GraphicsPath_new", int.class); //$NON-NLS-1$
				gdip_GraphicsPath_Transform = gdip.getDeclaredMethod("GraphicsPath_Transform", int.class, int.class); //$NON-NLS-1$
				gdip_PathGradientBrush_new = gdip.getDeclaredMethod("PathGradientBrush_new", int.class); //$NON-NLS-1$
				gdip_PathGradientBrush_SetInterpolationColors = gdip.getDeclaredMethod("PathGradientBrush_SetInterpolationColors", //$NON-NLS-1$
						int.class, int[].class, float[].class, int.class);
				gdip_LinearGradientBrush_new = gdip.getDeclaredMethod("LinearGradientBrush_new", pointf, pointf, int.class, int.class); //$NON-NLS-1$
				gdip_LinearGradientBrush_TranslateTransform = gdip.getDeclaredMethod("LinearGradientBrush_TranslateTransform", int.class, //$NON-NLS-1$
						float.class, float.class, int.class);
				gdip_LinearGradientBrush_ScaleTransform = gdip.getDeclaredMethod("LinearGradientBrush_ScaleTransform", int.class, //$NON-NLS-1$
						float.class, float.class, int.class);
				gdip_LinearGradientBrush_SetInterpolationColors = gdip.getDeclaredMethod("LinearGradientBrush_SetInterpolationColors", //$NON-NLS-1$
						int.class, int[].class, float[].class, int.class);
				gdip_LinearGradientBrush_SetWrapMode = gdip.getDeclaredMethod("LinearGradientBrush_SetWrapMode", int.class, int.class); //$NON-NLS-1$
				gdip_SolidBrush_delete = gdip.getDeclaredMethod("SolidBrush_delete", int.class); //$NON-NLS-1$
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static void cairo_matrix_init(double[] matrix, double xx, double yx, double xy, double yy, double x0, double y0) {
		try {
			cairo_matrix_init.invoke(cairo, matrix, xx, yx, xy, yy, x0, y0);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static void cairo_matrix_invert(double[] matrix) {
		try {
			cairo_matrix_invert.invoke(cairo, matrix);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static void cairo_matrix_multiply(double[] result, double[] a, double[] b) {
		try {
			cairo_matrix_multiply.invoke(cairo, result, a, b);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static void cairo_pattern_add_color_stop_rgba(long pattern, double offset, double red, double green, double blue, double alpha) {
		try {
			if(int.class == handleClass) {
				cairo_pattern_add_color_stop_rgba.invoke(cairo, (int) pattern, offset, red, green, blue, alpha);
			} else {
				cairo_pattern_add_color_stop_rgba.invoke(cairo, pattern, offset, red, green, blue, alpha);
			}
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static long cairo_pattern_create_linear(double x0, double y0, double x1, double y1) {
		try {
			if(int.class == handleClass) {
				return (Integer) cairo_pattern_create_linear.invoke(cairo, x0, y0, x1, y1);
			} else {
				return (Long) cairo_pattern_create_linear.invoke(cairo, x0, y0, x1, y1);
			}
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static long cairo_pattern_create_radial(double cx0, double cy0, double radius0, double cx1, double cy1, double radius1) {
		try {
			if(int.class == handleClass) {
				return (Integer) cairo_pattern_create_radial.invoke(cairo, cx0, cy0, radius0, cx1, cy1, radius1);
			} else {
				return (Long) cairo_pattern_create_radial.invoke(cairo, cx0, cy0, radius0, cx1, cy1, radius1);
			}
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static void cairo_pattern_destroy(long handle) {
		try {
			if(int.class == handleClass) {
				cairo_pattern_destroy.invoke(cairo, (int) handle);
			} else {
				cairo_pattern_destroy.invoke(cairo, handle);
			}
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static void cairo_pattern_set_extend(long pattern, int extend) {
		try {
			if(int.class == handleClass) {
				cairo_pattern_set_extend.invoke(cairo, (int) pattern, extend);
			} else {
				cairo_pattern_set_extend.invoke(cairo, pattern, extend);
			}
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static void cairo_pattern_set_matrix(long pattern, double[] matrix) {
		try {
			if(int.class == handleClass) {
				cairo_pattern_set_matrix.invoke(cairo, (int) pattern, matrix);
			} else {
				cairo_pattern_set_matrix.invoke(cairo, pattern, matrix);
			}
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	static Pattern createPattern(SvgGradient gradient) {
		if(gtk) {
			return gtk_createPattern(gradient);
		} else if(win32) {
			return win32_createPattern(gradient);
		} else {
			System.out.println("TODO: createPattern for gradient on " + SWT.getPlatform()); //$NON-NLS-1$
			return null;
		}
	}

	private static void gdip_Color_delete(int color) {
		try {
			gdip_Color_delete.invoke(gdip, color);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static int gdip_Color_new(int argb) {
		try {
			return (Integer) gdip_Color_new.invoke(gdip, argb);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static void gdip_GraphicsPath_AddArc(int /* long */path, float x, float y, float width, float height, float startAngle,
			float sweepAngle) {
		try {
			gdip_GraphicsPath_AddArc.invoke(gdip, path, x, y, width, height, startAngle, sweepAngle);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static void gdip_GraphicsPath_delete(int /* long */path) {
		try {
			gdip_GraphicsPath_delete.invoke(gdip, path);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static int gdip_GraphicsPath_new(int fillMode) {
		try {
			return (Integer) gdip_GraphicsPath_new.invoke(gdip, fillMode);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static void gdip_GraphicsPath_Transform(int /* long */path, int /* long */matrix) {
		try {
			gdip_GraphicsPath_Transform.invoke(gdip, path, matrix);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static int gdip_LinearGradientBrush_new(float[] point1, float[] point2, int /* long */color1, int /* long */color2) {
		try {
			Object p1 = pointf.newInstance();
			pointfX.setFloat(p1, point1[0]);
			pointfY.setFloat(p1, point1[1]);
			Object p2 = pointf.newInstance();
			pointfX.setFloat(p2, point2[0]);
			pointfY.setFloat(p2, point2[1]);
			return (Integer) gdip_LinearGradientBrush_new.invoke(gdip, p1, p2, color1, color2);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		} catch(InstantiationException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static void gdip_LinearGradientBrush_ScaleTransform(long brush, float sx, float sy, int order) {
		try {
			gdip_LinearGradientBrush_ScaleTransform.invoke(gdip, brush, sx, sy, order);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static void gdip_LinearGradientBrush_SetInterpolationColors(long brush, int /* long */[] presetColors,
			float[] blendPositions, int count) {
		try {
			gdip_LinearGradientBrush_SetInterpolationColors.invoke(gdip, brush, presetColors, blendPositions, count);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static void gdip_LinearGradientBrush_SetWrapMode(long brush, int wrapMode) {
		try {
			gdip_LinearGradientBrush_SetWrapMode.invoke(gdip, brush, wrapMode);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static void gdip_LinearGradientBrush_TranslateTransform(long brush, float dx, float dy, int order) {
		try {
			gdip_LinearGradientBrush_TranslateTransform.invoke(gdip, brush, dx, dy, order);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static void gdip_Matrix_delete(int matrix) {
		try {
			gdip_Matrix_delete.invoke(gdip, matrix);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static void gdip_Matrix_GetElements(int matrix, float[] m) {
		try {
			gdip_Matrix_GetElements.invoke(gdip, matrix, m);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static void gdip_Matrix_Multiply(int /* long */matrix, int /* long */matrix1, int order) {
		try {
			gdip_Matrix_Multiply.invoke(gdip, matrix, matrix1, order);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static int gdip_Matrix_new(float m11, float m12, float m21, float m22, float dx, float dy) {
		try {
			return (Integer) gdip_Matrix_new.invoke(gdip, m11, m12, m21, m22, dx, dy);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static int gdip_PathGradientBrush_new(int /* long */path) {
		try {
			return (Integer) gdip_PathGradientBrush_new.invoke(gdip, path);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static void gdip_PathGradientBrush_SetInterpolationColors(long brush, int /* long */[] presetColors,
			float[] blendPositions, int count) {
		try {
			gdip_PathGradientBrush_SetInterpolationColors.invoke(gdip, brush, presetColors, blendPositions, count);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static void gdip_SolidBrush_delete(long brush) {
		try {
			gdip_SolidBrush_delete.invoke(gdip, brush);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static Pattern gtk_createPattern(SvgGradient gradient) {
		Color color = gradient.gc.getDevice().getSystemColor(SWT.COLOR_BLACK);
		Pattern pattern = new Pattern(gradient.gc.getDevice(), 0, 0, 1, 1, color, color);
		long handle = pattern.handle;
		cairo_pattern_destroy(handle);
		float[] gdata = gradient.data;
		if(gdata.length == 5) {
			handle = cairo_pattern_create_radial(gdata[0], gdata[1], 0, gdata[2], gdata[3], gdata[4]);
		} else {
			handle = cairo_pattern_create_linear(gdata[0], gdata[1], gdata[2], gdata[3]);
		}
		setPatternHandle(pattern, handle);
		double[] matrix = new double[6];
		if(gradient.boundingBox) {
			float minx = gradient.bounds[0];
			float miny = gradient.bounds[1];
			float maxx = gradient.bounds[0] + gradient.bounds[2];
			float maxy = gradient.bounds[1] + gradient.bounds[3];
			double[] bt = new double[] { (maxx - minx), 0, 0, (maxy - miny), minx, miny };
			double[] data = new double[6];
			for(int i = 0; i < 6; i++) {
				data[i] = gradient.transform.data[i];
			}
			double[] result = new double[6];
			cairo_matrix_multiply(result, data, bt);
			cairo_matrix_init(matrix, result[0], result[1], result[2], result[3], result[4], result[5]);
		} else {
			float[] data = gradient.transform.data;
			cairo_matrix_init(matrix, data[0], data[1], data[2], data[3], data[4], data[5]);
		}
		cairo_matrix_invert(matrix);
		cairo_pattern_set_matrix(pattern.handle, matrix);
		SvgGradientStop[] stops = gradient.getStops();
		for(SvgGradientStop stop : stops) {
			double red = (double) stop.red() / (double) 255;
			double green = (double) stop.green() / (double) 255;
			double blue = (double) stop.blue() / (double) 255;
			cairo_pattern_add_color_stop_rgba(pattern.handle, stop.offset, red, green, blue, stop.opacity);
		}
		gtk_setSpreadMethod(pattern.handle, gradient.spreadMethod);
		return pattern;
	}
	
	private static void gtk_setSpreadMethod(long handle, int spreadMethod) {
		switch(spreadMethod) {
		case SvgGradient.REFLECT:
			cairo_pattern_set_extend(handle, 2);
			break;
		case SvgGradient.REPEAT:
			cairo_pattern_set_extend(handle, 1);
			break;
		default: // PAD
			cairo_pattern_set_extend(handle, 3);
			break;
		}
	}

	private static void setPatternHandle(Pattern pattern, long handle) {
		try {
			Field field = pattern.getClass().getDeclaredField("handle"); //$NON-NLS-1$
			if(int.class == handleClass) {
				field.set(pattern, (int) handle);
			} else {
				field.set(pattern, handle);
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private static Pattern win32_createPattern(SvgGradient gradient) {
		Color color = gradient.gc.getDevice().getSystemColor(SWT.COLOR_BLACK);
		Pattern pattern = new Pattern(gradient.gc.getDevice(), 0, 0, 0, 0, color, color);
		gdip_SolidBrush_delete(pattern.handle);
		float[] gdata = gradient.data;
		if(gdata.length == 5) {
			// TODO we need a way of disposing this gradient brush and path
			// after they've been used...
			SvgGradientStop[] stops = gradient.getStops();
			int[] colors = new int[stops.length + 2];
			float[] offsets = new float[stops.length + 2];
			offsets[0] = 0;
			offsets[colors.length - 1] = 1;
			for(int i = 0; i < stops.length; i++) {
				SvgGradientStop stop = stops[stops.length - i - 1];
				colors[i + 1] = gdip_Color_new((stop.alpha() & 0xFF) << 24 | stop.color);
				offsets[i + 1] = 1 - stop.offset;
			}
			colors[0] = gdip_Color_new((stops[stops.length - 1].alpha() & 0xFF) << 24 | stops[stops.length - 1].color);
			colors[colors.length - 1] = gdip_Color_new((stops[0].alpha()) << 24 | stops[0].color);

			float[] data = new float[6];
			System.arraycopy(gradient.transform.data, 0, data, 0, 6);
			int matrix = gdip_Matrix_new(data[0], data[1], data[2], data[3], data[4], data[5]);
			if(gradient.boundingBox) {
				float minx = gradient.bounds[0];
				float miny = gradient.bounds[1];
				float maxx = gradient.bounds[0] + gradient.bounds[2];
				float maxy = gradient.bounds[1] + gradient.bounds[3];
				float[] bt = new float[] { (maxx - minx), 0, 0, (maxy - miny), minx, miny };
				int matrix1 = gdip_Matrix_new(bt[0], bt[1], bt[2], bt[3], bt[4], bt[5]);
				gdip_Matrix_Multiply(matrix, matrix1, 0);
			}

			int path = gdip_GraphicsPath_new(0);
			gdip_GraphicsPath_AddArc(path, gdata[0] - gdata[4], gdata[1] - gdata[4], 2 * gdata[4], 2 * gdata[4], 0, 360);
			gdip_GraphicsPath_Transform(path, matrix);

			pattern.handle = gdip_PathGradientBrush_new(path);
			gdip_PathGradientBrush_SetInterpolationColors(pattern.handle, colors, offsets, colors.length);

			// gdip_GraphicsPath_delete(path);
			gdip_Matrix_delete(matrix);
			gdip_GraphicsPath_delete(path);
			for(int c : colors) {
				gdip_Color_delete(c);
			}
		} else {
			float[] point1 = new float[] { gdata[0], gdata[1] };
			float[] point2 = new float[] { gdata[2], gdata[3] };

			SvgGradientStop[] stops = gradient.getStops();
			int[] colors = new int[stops.length + 2];
			float[] offsets = new float[stops.length + 2];
			colors[0] = gdip_Color_new((stops[0].alpha() & 0xFF) << 24 | stops[0].color);
			colors[colors.length - 1] = gdip_Color_new((stops[stops.length - 1].alpha() & 0xFF) << 24 | stops[stops.length - 1].color);
			offsets[0] = 0;
			offsets[colors.length - 1] = 1;
			for(int i = 0; i < stops.length; i++) {
				SvgGradientStop stop = stops[i];
				colors[i + 1] = gdip_Color_new((stop.alpha() & 0xFF) << 24 | stop.color);
				offsets[i + 1] = stop.offset;
			}

			pattern.handle = gdip_LinearGradientBrush_new(point1, point2, colors[0], colors[colors.length - 1]);
			gdip_LinearGradientBrush_SetInterpolationColors(pattern.handle, colors, offsets, colors.length);
			win32_setSpreadMethod(pattern.handle, gradient.spreadMethod);

			for(int c : colors) {
				gdip_Color_delete(c);
			}

			float[] data = new float[6];
			System.arraycopy(gradient.transform.data, 0, data, 0, 6);
			int matrix = gdip_Matrix_new(data[0], data[1], data[2], data[3], data[4], data[5]);
			if(gradient.boundingBox) {
				float minx = gradient.bounds[0];
				float miny = gradient.bounds[1];
				float maxx = gradient.bounds[0] + gradient.bounds[2];
				float maxy = gradient.bounds[1] + gradient.bounds[3];
				float[] bt = new float[] { (maxx - minx), 0, 0, (maxy - miny), minx, miny };
				int matrix1 = gdip_Matrix_new(bt[0], bt[1], bt[2], bt[3], bt[4], bt[5]);
				gdip_Matrix_Multiply(matrix, matrix1, 0);
			}
			gdip_Matrix_GetElements(matrix, data);
			gdip_LinearGradientBrush_TranslateTransform(pattern.handle, data[4], data[5], 0);
			gdip_LinearGradientBrush_ScaleTransform(pattern.handle, data[0], data[3], 0);
			gdip_Matrix_delete(matrix);
		}
		return pattern;
	}

	private static void win32_setSpreadMethod(long handle, int spreadMethod) {
		switch(spreadMethod) {
		case SvgGradient.REFLECT:
			gdip_LinearGradientBrush_SetWrapMode(handle, 3); // WrapModeTileFlipXY
			break;
		case SvgGradient.REPEAT:
			gdip_LinearGradientBrush_SetWrapMode(handle, 0); // WrapModeTile
			break;
		default: // PAD
			// TODO gdi+ does not support pad
			gdip_LinearGradientBrush_SetWrapMode(handle, 3); // WrapModeTileFlipXY
			break;
		}
	}

	private SwtAdapter() {
		// class should not be instantiated
	}

}
