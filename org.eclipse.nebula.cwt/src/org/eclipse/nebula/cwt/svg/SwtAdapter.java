package org.eclipse.nebula.cwt.svg;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Pattern;

public class SwtAdapter {

	static final boolean carbon = "carbon".equals(SWT.getPlatform());
	static final boolean gtk = "gtk".equals(SWT.getPlatform());
	static final boolean win32 = "win32".equals(SWT.getPlatform());

	static final String CAIRO = "org.eclipse.swt.internal.cairo.Cairo";
	static final String GDIP = "org.eclipse.swt.internal.gdip.Gdip";

	private static Class<?> cairo;

	private static Method cairo_matrix_init;
	private static Method cairo_matrix_invert;
	private static Method cairo_matrix_multiply;
	private static Method cairo_pattern_add_color_stop_rgba;
	private static Method cairo_pattern_create_linear;
	private static Method cairo_pattern_create_radial;
	private static Method cairo_pattern_destroy;
	private static Method cairo_pattern_set_extend;
	private static Method cairo_pattern_set_matrix;
	static {
		if(gtk) {
			try {
				cairo = Class.forName(CAIRO, true, SWT.class.getClassLoader());

				cairo_matrix_init = cairo.getDeclaredMethod("cairo_matrix_init", double[].class, double.class, double.class, double.class,
						double.class, double.class, double.class);
				cairo_matrix_init.setAccessible(true);

				cairo_matrix_invert = cairo.getDeclaredMethod("cairo_matrix_invert", double[].class);
				cairo_matrix_invert.setAccessible(true);

				cairo_matrix_multiply = cairo.getDeclaredMethod("cairo_matrix_multiply", double[].class, double[].class, double[].class);
				cairo_matrix_multiply.setAccessible(true);

				cairo_pattern_add_color_stop_rgba = cairo.getDeclaredMethod("cairo_pattern_add_color_stop_rgba", int.class, double.class,
						double.class, double.class, double.class, double.class);
				cairo_pattern_add_color_stop_rgba.setAccessible(true);

				cairo_pattern_create_linear = cairo.getDeclaredMethod("cairo_pattern_create_linear", double.class, double.class,
						double.class, double.class);
				cairo_pattern_create_linear.setAccessible(true);

				cairo_pattern_create_radial = cairo.getDeclaredMethod("cairo_pattern_create_radial", double.class, double.class,
						double.class, double.class, double.class, double.class);
				cairo_pattern_create_radial.setAccessible(true);

				cairo_pattern_destroy = cairo.getDeclaredMethod("cairo_pattern_destroy", int.class);
				cairo_pattern_destroy.setAccessible(true);

				cairo_pattern_set_extend = cairo.getDeclaredMethod("cairo_pattern_set_extend", int.class, int.class);
				cairo_pattern_set_extend.setAccessible(true);

				cairo_pattern_set_matrix = cairo.getDeclaredMethod("cairo_pattern_set_matrix", int.class, double[].class);
				cairo_pattern_set_matrix.setAccessible(true);
			} catch(Exception e) {
				e.printStackTrace();
			}
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

	private static void cairo_pattern_add_color_stop_rgba(int pattern, double offset, double red, double green, double blue, double alpha) {
		try {
			cairo_pattern_add_color_stop_rgba.invoke(cairo, pattern, offset, red, green, blue, alpha);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static int cairo_pattern_create_linear(double x0, double y0, double x1, double y1) {
		try {
			return (Integer) cairo_pattern_create_linear.invoke(cairo, x0, y0, x1, y1);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static int cairo_pattern_create_radial(double cx0, double cy0, double radius0, double cx1, double cy1, double radius1) {
		try {
			return (Integer) cairo_pattern_create_radial.invoke(cairo, cx0, cy0, radius0, cx1, cy1, radius1);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static void cairo_pattern_destroy(int handle) {
		try {
			cairo_pattern_destroy.invoke(cairo, handle);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static void cairo_pattern_set_extend(int pattern, int extend) {
		try {
			cairo_pattern_set_extend.invoke(cairo, pattern, extend);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static void cairo_pattern_set_matrix(int pattern, double[] matrix) {
		try {
			cairo_pattern_set_matrix.invoke(cairo, pattern, matrix);
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
			Color color = gradient.gc.getDevice().getSystemColor(SWT.COLOR_BLACK);
			Pattern pattern = new Pattern(gradient.gc.getDevice(), 0, 0, 1, 1, color, color);
			cairo_pattern_destroy(pattern.handle);
			float[] gdata = gradient.data;
			if(gdata.length == 5) {
				pattern.handle = cairo_pattern_create_radial(gdata[0], gdata[1], 0, gdata[2], gdata[3], gdata[4]);
			} else {
				pattern.handle = cairo_pattern_create_linear(gdata[0], gdata[1], gdata[2], gdata[3]);
			}
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
		} else {
			System.out.println("TODO: createPattern for gradient on " + SWT.getPlatform());
			return null;
		}
	}

	private static void gtk_setSpreadMethod(int handle, int spreadMethod) {
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

	//	private static void win32_setSpreadMethod(int handle, int spreadMethod, SvgGradientStop[] stops) {
	//		switch(spreadMethod) {
	//		case SvgGradient.REFLECT:
	//			execute(GDIP, "LinearGradientBrush_SetWrapMode", new Class<?>[] { int.class, int.class }, new Object[] { handle, 3 });
	//			break;
	//		case SvgGradient.REPEAT:
	//			execute(GDIP, "LinearGradientBrush_SetWrapMode", new Class<?>[] { int.class, int.class }, new Object[] { handle, 0 });
	//			break;
	//		default: // PAD (gdi+ does not support pad - fake it with extra stops)
	//			execute(GDIP, "LinearGradientBrush_SetWrapMode", new Class<?>[] { int.class, int.class }, new Object[] { handle, 4 });
	//
	//			int color1 = (int) (255 * stops[0].opacity) << 24 | stops[0].color;
	//			color1 = (Integer) execute(GDIP, "Color_new", new Class<?>[] { int.class }, new Object[] { color1 });
	//			int color2 = (int) (255 * stops[1].opacity) << 24 | stops[1].color;
	//			color2 = (Integer) execute(GDIP, "Color_new", new Class<?>[] { int.class }, new Object[] { color2 });
	//
	//			float offset1 = stops[0].offset;
	//			float offset2 = stops[1].offset;
	//
	//			execute(GDIP, "LinearGradientBrush_SetInterpolationColors",
	//					new Class<?>[] { int.class, int[].class, float[].class, int.class }, new Object[] { handle,
	//							new int[] { color1, color1, color2, color2 }, new float[] { 0, offset1, offset2, 1 }, 4 });
	//
	//			execute(GDIP, "Color_delete", new Class<?>[] { int.class }, new Object[] { color1 });
	//			execute(GDIP, "Color_delete", new Class<?>[] { int.class }, new Object[] { color2 });
	//			break;
	//		}
	//	}

	private SwtAdapter() {
		// class should not be instantiated
	}

}
