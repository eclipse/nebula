/*******************************************************************************
 * Copyright (c) Emil Crumhorn - Hexapixel.com - emil.crumhorn@gmail.com
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    emil.crumhorn@gmail.com - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.ganttchart;

import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

public class AdvancedTooltipDialog {

	private static Shell _shell;

	public static void makeDialog(final AdvancedTooltip toolTip, final IColorManager colorManager, final Point location) {
		makeDialog(toolTip, colorManager, location, null, null, null);
	}

	public static void makeDialog(final AdvancedTooltip toolTip, final IColorManager colorManager, final Point location, final String titleOverride, final String contentOverride,
			final String helpOverride) {
		
	    if (_shell != null && !_shell.isDisposed()) {
			_shell.dispose();
		}

		_shell = new Shell(Display.getDefault().getActiveShell(), SWT.ON_TOP | SWT.TOOL | SWT.NO_TRIM | SWT.NO_FOCUS);
		_shell.setLayout(new FillLayout());

		final Composite comp = new Composite(_shell, SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED | SWT.NO_FOCUS);

		comp.addListener(SWT.MouseMove, new Listener() {
			public void handleEvent(final Event event) {
				kill();
			}
		});

		comp.addPaintListener(new PaintListener() {
			
			public void paintControl(final PaintEvent e) {
			    final Region region = new Region(_shell.getDisplay());

			    final GC gc = e.gc;
			    final Rectangle bounds = comp.getBounds();
				
				// draw borders
				drawBorders(gc, colorManager, bounds);

				// this is the margins for all content
				int marginTop = 8;
				int marginLeft = 6;
				int marginRight = 6;
				int marginBottom = 12;

				int x = marginLeft;
				int y = marginTop;

				int xMax = 0;
				int yMax = 0;

				// == TITLE ==
				// title is bold
				Font bold = null;
				final Font old = gc.getFont();
				bold = Utils.applyBoldFont(old);

				String title = toolTip.getTitle();
				if (titleOverride != null) {
					title = titleOverride;
				}

				if (title != null && title.length() > 0) {
					gc.setForeground(colorManager.getAdvancedTooltipTextColor());
					gc.setFont(bold);
					final Point point = gc.stringExtent(title);
					drawText(gc, title, x, y);
					// gc.drawString(title, x, y, true);
					gc.setFont(old);

					y += point.y;
					xMax = Math.max(xMax, x + point.x);
					yMax = Math.max(yMax, y);
				}

				final Image bigImage = toolTip.getImage();
				int imageY = y;
				if (bigImage != null) {
					// draw the image, as well as tell the normal text where it
					// will have to go depending on image size
					// space it first, regardless of size
					x += 9;
					final Rectangle imBounds = bigImage.getBounds();
					// we push it down a bit, but these don't add to the overall
					// y position as the image
					// is (somewhat) horizontally aligned with the content text
					gc.drawImage(bigImage, x, y + 12);
					x += imBounds.width;

					imageY += imBounds.height + 12;
				}

				// == DRAW TEXT ==
				int textY = y;

				String content = toolTip.getContent();
				if (contentOverride != null) {
					content = contentOverride;
				}

				if (content != null && content.length() > 0) {
					// if we had an image, space out this text, otherwise a
					// little less
					if (bigImage == null) {
						x += 8;
					}
					else {
						x += 13;
					}

					// first we space it vertically
					textY += 13;

					final StringTokenizer tokenizer = new StringTokenizer(content, "\n");

					int widestLine = 0;
					while (tokenizer.hasMoreTokens()) {
					    final String token = tokenizer.nextToken();
					    final Point extent = drawText(gc, token, x, textY);
						textY += extent.y;
						widestLine = Math.max(widestLine, extent.x);
					}
					x += widestLine;
				}

				// now add the image height to Y unless the text Y was bigger
				y = Math.max(textY, imageY);

				xMax = Math.max(xMax, x);
				yMax = Math.max(yMax, y);

				if (toolTip.getHelpImage() != null || toolTip.getHelpText() != null) {
					y += 8;

					// draw divider
					gc.setForeground(colorManager.getAdvancedTooltipDividerColor());
					gc.drawLine(marginLeft, y, marginLeft + xMax, y);
					y++;
					gc.setForeground(colorManager.getAdvancedTooltipDividerShadowColor());
					gc.drawLine(marginLeft, y, marginLeft + xMax, y);
					y += 7;
					gc.setForeground(colorManager.getAdvancedTooltipTextColor());
					gc.setFont(bold);

					int curX = marginLeft;

					int widthUsed = 0;

					if (toolTip.getHelpImage() != null) {
						gc.drawImage(toolTip.getHelpImage(), marginLeft, y);
						curX += toolTip.getHelpImage().getBounds().width;
						curX += 9;
						widthUsed += curX - marginLeft;
					}

					if (toolTip.getHelpText() != null) {
					    final Point helpSize = gc.stringExtent(toolTip.getHelpText());
						gc.drawString(toolTip.getHelpText(), curX, y, true);
						widthUsed += helpSize.x;
					}

					xMax = Math.max(xMax, widthUsed);
					yMax = Math.max(yMax, y);
				}

				xMax += marginLeft + marginRight;
				yMax += marginTop + marginBottom;

				region.add(0, 0, xMax, yMax);
				region.subtract(0, 0, 1, 1);
				region.subtract(xMax - 1, yMax - 1, 1, 1);
				region.subtract(0, yMax - 1, 1, 1);
				region.subtract(xMax - 1, 0, 1, 1);
				
				// bug fix #240164 - Macs redraw when you set a region, guess OS X will just have
				// square shells instead, no big deal
				if (GanttComposite._osType != Constants.OS_MAC) {
					_shell.setRegion(region);
				}
		
				final Rectangle size = region.getBounds();
				_shell.setSize(size.width, size.height);
				if (bold != null) {
					bold.dispose();
				}
				
				Monitor active = null;
				try {
				    active = Display.getDefault().getActiveShell().getMonitor();
				}
				catch (Exception err) {
				    active = Display.getDefault().getPrimaryMonitor();
				}
				int totalXBounds = 0;
				final Monitor [] all = Display.getDefault().getMonitors();
				for (int i = 0; i < all.length; i++) {
				    if (all[i] == active) {
				        break;
				    }
				    totalXBounds += all[i].getBounds().width;
				}
				
				final Rectangle maxBounds = active.getBounds();
				final int shellHeight = _shell.getSize().y;
				final int shellWidth = _shell.getSize().x;
		        
				final Point location = _shell.getLocation();
		        if ((location.y + shellHeight) > maxBounds.height) {
		            location.y = maxBounds.height-shellHeight;
		        }
		        if ((location.x + shellWidth) > totalXBounds) {
		            location.x = totalXBounds - shellWidth;
		        }
		        
                _shell.setLocation(location);
		        
			}

		});

		_shell.pack();
		
		
        _shell.setLocation(location);
		
		_shell.setVisible(true);
		
		// bug fix #240164 - for some reason the bounds fetched at the beginning are off on Macs,
		// it seems it calls the redraw much sooner than on windows (before creating the shell, which rather
		// makes sense, but it's not what we had planned)
		// which causes things to become only drawn in a corner of the shell, thus, we force a redraw
		// after displaying the shell, which fixes the issue as it now can fetch the right bounds
		if (GanttComposite._osType == Constants.OS_MAC) {
			Display.getDefault().asyncExec(new Runnable() {

				public void run() {
					_shell.redraw();
					
				}
				
			});
		}
	}

	private static void drawBorders(final GC gc, final IColorManager colorManager, final Rectangle bounds) {
		gc.setForeground(colorManager.getAdvancedTooltipInnerFillTopColor());
		gc.setBackground(colorManager.getAdvancedTooltipInnerFillBottomColor());

		gc.fillGradientRectangle(bounds.x, bounds.y, bounds.width, bounds.height, true);

		// draw border
		gc.setForeground(colorManager.getAdvancedTooltipBorderColor());
		gc.drawRectangle(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1);

		// what would would the world be without faded gradient corners? boring!
		// so let's draw a few.

		// draw corners
		gc.setForeground(colorManager.getAdvancedTooltipShadowCornerOuterColor());
		// top left
		gc.drawLine(bounds.x + 1, bounds.y, bounds.x + 1, bounds.y);
		gc.drawLine(bounds.x, bounds.y + 1, bounds.x, bounds.y + 1);
		// top right
		gc.drawLine(bounds.x + bounds.width - 2, bounds.y, bounds.x + bounds.width - 2, bounds.y);
		gc.drawLine(bounds.x + bounds.width - 1, bounds.y + 1, bounds.x + bounds.width - 1, bounds.y + 1);
		// bottom right
		gc.drawLine(bounds.x + bounds.width - 1, bounds.y + bounds.height - 2, bounds.x + bounds.width - 1, bounds.y + bounds.height - 2);
		gc.drawLine(bounds.x + bounds.width - 2, bounds.y + bounds.height - 1, bounds.x + bounds.width - 2, bounds.y + bounds.height - 1);
		// bottom left
		gc.drawLine(bounds.x + 1, bounds.y + bounds.height - 1, bounds.x + 1, bounds.y + bounds.height - 1);
		gc.drawLine(bounds.x, bounds.y + bounds.height - 2, bounds.x, bounds.y + bounds.height - 2);

		// shadowed corner inside the above
		gc.setForeground(colorManager.getAdvancedTooltipShadowCornerInnerColor());
		// top left
		gc.drawLine(bounds.x + 2, bounds.y, bounds.x + 2, bounds.y);
		gc.drawLine(bounds.x, bounds.y + 2, bounds.x, bounds.y + 2);
		// top right
		gc.drawLine(bounds.x + bounds.width - 3, bounds.y, bounds.x + bounds.width - 3, bounds.y);
		gc.drawLine(bounds.x + bounds.width - 1, bounds.y + 2, bounds.x + bounds.width - 1, bounds.y + 2);
		// bottom right
		gc.drawLine(bounds.x + bounds.width - 1, bounds.y + bounds.height - 3, bounds.x + bounds.width - 1, bounds.y + bounds.height - 3);
		gc.drawLine(bounds.x + bounds.width - 3, bounds.y + bounds.height - 1, bounds.x + bounds.width - 3, bounds.y + bounds.height - 1);
		// bottom left
		gc.drawLine(bounds.x + 2, bounds.y + bounds.height - 1, bounds.x + 2, bounds.y + bounds.height - 1);
		gc.drawLine(bounds.x, bounds.y + bounds.height - 3, bounds.x, bounds.y + bounds.height - 3);

		// draw inner corner pixel in each corner
		gc.setForeground(colorManager.getAdvancedTooltipShadowInnerCornerColor());
		// top left
		gc.drawLine(bounds.x + 1, bounds.y + 1, bounds.x + 1, bounds.y + 1);
		// top right
		gc.drawLine(bounds.x + bounds.width - 2, bounds.y + 1, bounds.x + bounds.width - 2, bounds.y + 1);
		// bottom right
		gc.drawLine(bounds.x + bounds.width - 2, bounds.y + bounds.height - 2, bounds.x + bounds.width - 2, bounds.y + bounds.height - 2);
		// bottom left
		gc.drawLine(bounds.x + 1, bounds.y + bounds.height - 2, bounds.x + 1, bounds.y + bounds.height - 2);

	}

	private static Point drawText(final GC gc, final String text, final int x, final int y) {
        try {
            final Font old = gc.getFont();
            Font used = null;
            final String oldName = old.getFontData()[0].getName();
            final int oldSize = (int) old.getFontData()[0].height;

            int curX = x;
            boolean bold = false;
            boolean italic = false;
            int size = oldSize;
            Color fg = ColorCache.getBlack();

            int maxWidth = 0;
            int maxHeight = 0;

            //int tokens = sub.countTokens();
            final char[] all = text.toCharArray();

            for (int i = 0; i < all.length; i++) {
                String token = Character.toString(all[i]);
                
                if ("\\".equals(token)) {
                    final StringBuffer buf = new StringBuffer(token); // NOPMD
                    buf.append(Character.toString(all[i + 1]));
                    token = buf.toString();
                    i++;
                }

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

                    if (text.length() > (i + 10) && "\\c".equals(token)) {
                        final String colTxt = text.substring(i - 1, i + 10);
                        final Color newColor = getColor(colTxt);
                        if (newColor != null) {
                            i += colTxt.length() - 2; // -2 is length of \c
                            token = colTxt;
                        }
                        if (!newColor.equals(fg)) {
                            fg = newColor;
                        }
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

                if (all[i] == '\t') {
                    curX += gc.stringExtent(" ").x * 4;
                    token = " ";
                }

                used = new Font(Display.getDefault(), oldName, size, style); // NOPMD
                gc.setFont(used);

                if (token.length() != 0) {
                    gc.drawString(token, curX, y, true);
                    final int extX = gc.stringExtent(token).x;// + ((cnt != all.length - 1) ? gc.stringExtent(token).x : 0);
                    final int extY = gc.stringExtent(token).y;
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

	private static String cleanUp(final String string) {
	    String str = string;
	    final int start = str.indexOf("\\s");
		if (start != -1) {
		    final String left = str.substring(0, start);
		    final String right = str.substring(start + 4, str.length());

			str = left + right;
		}
		//start = str.indexOf("\\c");
		str = str.replaceAll("\\\\ce", "");
		str = str.replaceAll("\\\\c[0-9]{9}", "");
		str = str.replaceAll("\\\\x", "");
		str = str.replaceAll("\\\\b", "");
		return str;
	}

	private static boolean isNormalize(final String str) {
		return str.indexOf("\\x") > -1;
	}

	private static boolean isBold(final String str) {
		return str.indexOf("\\b") > -1;
	}

	private static boolean isItalic(final String str) {
		return str.indexOf("\\i") > -1;
	}

	private static int getSize(final String str) {
	    final int start = str.indexOf("\\s");
		if (start == -1) {
			return -1;
		}

		final String size = str.substring(start + 2, start + 4);

		try {
			return Integer.parseInt(size);
		} catch (Exception badParse) {
			SWT.error(SWT.ERROR_UNSPECIFIED, badParse);
		}

		return -1;
	}

	private static Color getColor(final String str) {
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

	public static void kill() {
		if (_shell != null && !_shell.isDisposed()) {
			_shell.dispose();
		}
	}

	public static boolean isActive() {
		return (_shell != null && !_shell.isDisposed());
	}
}
