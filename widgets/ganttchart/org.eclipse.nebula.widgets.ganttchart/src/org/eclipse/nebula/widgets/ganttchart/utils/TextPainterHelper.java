package org.eclipse.nebula.widgets.ganttchart.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.nebula.widgets.ganttchart.ColorCache;
import org.eclipse.nebula.widgets.ganttchart.Utils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * Helper class to draw texts with markups within the GanttChart.
 * Currently used for rendering tooltips and section details.
 * Following markups are supported:
 * <ul>
 * <li>\\ce - render the following text in black</li>
 * <li>\\c[0-9]{9} - render the following text in the specified rbg color (Note that there need to be nine digits)</li>
 * <li>\\s[0-9]{1,3} - render the following text with the specified font size</li>
 * <li>\\i - render the following text italic</li>
 * <li>\\b - render the following text bold</li>
 * <li>\\x - normalize, which means to reset the previous assigned markups and render with the default font and color</li>
 * </ul>
 */
@SuppressWarnings("nls")
public class TextPainterHelper {

	/**
	 * Will draw the given text to the given GC. Before drawing the containing
	 * markups will be interpreted to apply color and font settings.
	 * @param gc The GC to draw the text to
	 * @param text The text to draw
	 * @param x The x coordinate where the text should be drawed to the GC
	 * @param y The y coordinate where the text should be drawed to the GC
	 * @return The end point of the drawed text.
	 */
	public static Point drawText(final GC gc, final String text, final int x, final int y) {
		Pattern pattern = Pattern.compile("(\\\\(ce|c[0-9]{9}|s[0-9]{1,3}|[xbi]))*[^\\\\]*");

        try {
            final Font old = gc.getFont();
            final int oldSize = (int) old.getFontData()[0].height;

            int curX = x;
            boolean bold = false;
            boolean italic = false;
            int size = oldSize;
            Color fg = ColorCache.getBlack();

            int maxWidth = 0;
            int maxHeight = 0;

    		Matcher matcher = pattern.matcher(text);

    		while (matcher.find()) {
    			String token = matcher.group();

                if (isNormalize(token)) {
                    bold = false;
                    italic = false;
                    size = oldSize;
                    fg = ColorCache.getBlack();
                }
                else {
                    final int newSize = getSize(token);
                    if (newSize != size && newSize != -1) {
                        size = newSize;
                    }

                    final boolean newBold = isBold(token);
                    if (bold && !newBold) {
                        bold = true;
                    }
                    else {
                        bold = newBold;
                    }

                    final boolean newItalic = isItalic(token);
                    if (italic && !newItalic) {
                        italic = true;
                    }
                    else {
                        italic = newItalic;
                    }

                    final Color newColor = getColor(token);
                    if (newColor != null && !newColor.equals(fg)) {
                            fg = newColor;
                        }
                    }

                if (fg != null) {
                    gc.setForeground(fg);
                }

                token = cleanUp(token);

                int style = SWT.NORMAL;
                if (bold) {
                    style |= SWT.BOLD;
                }
                if (italic) {
                    style |= SWT.ITALIC;
                }

                Font used = Utils.applyFontData(old, style, size);
                gc.setFont(used);

                if (token.length() != 0) {
                    gc.drawText(token, curX, y, true);
                    final int extX = gc.textExtent(token).x;
                    final int extY = gc.textExtent(token).y;
                    curX += extX;

                    maxWidth = Math.max(maxWidth, curX);
                    maxHeight = Math.max(maxHeight, extY);
                }

                used.dispose();
            }

            gc.setFont(old);
            return new Point(maxWidth - x, maxHeight);
        }
        catch (Exception err) {
            SWT.error(SWT.ERROR_UNSPECIFIED, err);
        }

        return null;
    }

	/**
	 * Removes all markups out of the given string. Needed to render
	 * after all markups have been resolved.
	 * @param string The string whose markups should be removed.
	 * @return The given string without any further markups.
	 */
	public static String cleanUp(final String string) {
	    String str = string;
		str = str.replaceAll("\\\\ce", "");
		str = str.replaceAll("\\\\c[0-9]{9}", "");
		str = str.replaceAll("\\\\s[0-9]{1,3}", "");
		str = str.replaceAll("\\\\x", "");
		str = str.replaceAll("\\\\b", "");
		str = str.replaceAll("\\\\i", "");
		return str;
	}

	/**
	 * Checks for the existence of the normalize markup.
	 * The normalize markup can be used to reset all former assigned
	 * markups for the following text to render.
	 * The normalize markup is \\x
	 * @param str The string to check for the normalize markup
	 * @return <code>true</code> if the given string contains the
	 * 			normalize markup
	 */
	public static boolean isNormalize(final String str) {
		return str.indexOf("\\x") > -1;
	}

	/**
	 * Checks for the existence of the bold markup.
	 * The bold markup is \\b
	 * @param str The string to check for the bold markup
	 * @return <code>true</code> if the given string contains the
	 * 			bold markup
	 */
	public static boolean isBold(final String str) {
		return str.indexOf("\\b") > -1;
	}

	/**
	 * Checks for the existence of the italic markup.
	 * The italic markup is \\i
	 * @param str The string to check for the italic markup
	 * @return <code>true</code> if the given string contains the
	 * 			italic markup
	 */
	public static boolean isItalic(final String str) {
		return str.indexOf("\\i") > -1;
	}

	/**
	 * Gets the font size out of a size markup in the given string.
	 * A size markup is specified \\s[0-9]{1,3}
	 * @param str The string containing the size markup
	 * @return The font size to use for rendering
	 */
	public static int getSize(final String str) {
		Pattern pattern = Pattern.compile("\\\\s[0-9]{1,3}");
		Matcher matcher = pattern.matcher(str);
		if (matcher.find()) {
			String sizeString = matcher.group();
			sizeString = sizeString.substring(2);

			try {
				return Integer.parseInt(sizeString);
			} catch (Exception badParse) {
				SWT.error(SWT.ERROR_UNSPECIFIED, badParse);
			}
		}

		return -1;
	}

	/**
	 * Gets the color for a color markup in the given string.
	 * A color markup is either \\ce for black or \\c[0-9]{9}
	 * to specify a custom color by rgb code
	 * @param str The string containing a color markup
	 * @return The Color that is specified by the color markup
	 */
	public static Color getColor(final String str) {
	    final int start = str.indexOf("\\c");
		if (start == -1) {
			return null;
		}

		if (str.indexOf("\\ce") != -1) {
			return ColorCache.getBlack();
		}

		try {
		    final int red = Integer.parseInt(str.substring(start + 2, start + 5));
		    final int green = Integer.parseInt(str.substring(start + 5, start + 8));
		    final int blue = Integer.parseInt(str.substring(start + 8, start + 11));

			return ColorCache.getColor(red, green, blue);
		} catch (Exception err) {
			SWT.error(SWT.ERROR_UNSPECIFIED, err);
		}

		return ColorCache.getBlack();
	}
}
