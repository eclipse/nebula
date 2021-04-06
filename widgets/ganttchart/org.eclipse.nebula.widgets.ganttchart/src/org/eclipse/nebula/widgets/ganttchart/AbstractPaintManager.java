/*******************************************************************************
 * Copyright (c) Emil Crumhorn - Hexapixel.com - emil.crumhorn@gmail.com
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    emil.crumhorn@gmail.com - initial API and implementation
 *    ziogiannigmail.com - Bug 464509 - Minute View Implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.ganttchart;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

public abstract class AbstractPaintManager implements IPaintManager {

    public void redrawStarting() { // NOPMD        
    }

    public void drawEvent(final GanttComposite ganttComposite, final ISettings settings, final IColorManager colorManager, final GanttEvent event, final GC gc, final boolean isSelected, final boolean threeDee, final int dayWidth, final int xStart, final int y, final int eventWidth, final Rectangle bounds) {

        final boolean alpha = colorManager.useAlphaDrawing();

        int xLoc = xStart;

        // draw the border
        gc.setForeground(colorManager.getEventBorderColor());

        if (isSelected && settings.drawSelectionMarkerAroundSelectedEvent()) {
            gc.setLineStyle(SWT.LINE_DOT);

            // this is _extremely_ slow to draw, so we need to check bounds here, which is probably a good idea anyway
            final boolean oobLeft = (xLoc < bounds.x);
            final boolean oobRight = (xLoc + eventWidth > bounds.width);
            if (oobLeft || oobRight) {
                if (!oobLeft || !oobRight) { //NOPMD
                    if (oobLeft) {
                    	if (ganttComposite.getCurrentView() != ISettings.VIEW_MINUTE) {//Control added since this part was leading a deadlock with minute_view
                        // left side out of bounds 
                        gc.drawLine(xLoc, y, xLoc + eventWidth, y);
                        gc.drawLine(xLoc + eventWidth, y, xLoc + eventWidth, y + event.getHeight());
                        gc.drawLine(xLoc, y + event.getHeight(), xLoc + eventWidth, y + event.getHeight());
                    	}
                    } else {
                        // right side out of bounds
                        gc.drawLine(xLoc, y, bounds.width, y);
                        gc.drawLine(xLoc, y, xLoc, y + event.getHeight());
                        gc.drawLine(xLoc, y + event.getHeight(), bounds.width, y + event.getHeight());
                    }
                } else {
                    // double out of bounds
                    gc.drawLine(bounds.x, y, bounds.x + bounds.width, y);
                    gc.drawLine(bounds.x, y + event.getHeight(), bounds.x + bounds.width, y + event.getHeight());
                }
            } else {
                gc.drawRectangle(xLoc, y, eventWidth, settings.getEventHeight());
            }

            gc.setLineStyle(SWT.LINE_SOLID);
        } else {
            gc.drawRectangle(xLoc, y, eventWidth, settings.getEventHeight());
        }

        Color cEvent = event.getStatusColor();
        Color gradient = event.getGradientStatusColor();

        if (cEvent == null) {
            cEvent = settings.getDefaultEventColor();
        }
        if (gradient == null) {
            gradient = settings.getDefaultGradientEventColor();
        }

        // draw the insides
        gc.setBackground(cEvent);

        if (settings.showGradientEventBars()) {
            gc.setForeground(gradient);
            gc.fillGradientRectangle(xLoc + 1, y + 1, eventWidth - 1, settings.getEventHeight() - 1, true);
            gc.setForeground(colorManager.getEventBorderColor()); // re-set foreground color
        } else {
            gc.fillRectangle(xLoc + 1, y + 1, eventWidth - 1, settings.getEventHeight() - 1);
        }

        // if 3D effect, draw drop-shadow
        if (threeDee) {
            final boolean subAlpha = colorManager.useAlphaDrawingOn3DEventDropShadows();
            if (subAlpha) {
                gc.setAlpha(200);
            }
            gc.setForeground(colorManager.getFadeOffColor1());
            // horizontal line.. ends a few pixles right of bottom right corner
            gc.drawLine(xLoc, y + settings.getEventHeight() + 1, xLoc + eventWidth + 1, y + settings.getEventHeight() + 1);
            // vertical line at end, starts slightly below top right corner
            gc.drawLine(xLoc + eventWidth + 1, y + 2, xLoc + eventWidth + 1, y + settings.getEventHeight());

            if (subAlpha) {
                gc.setAlpha(100);
            }
            gc.setForeground(colorManager.getFadeOffColor2());
            gc.drawLine(xLoc, y + settings.getEventHeight() + 2, xLoc + eventWidth + 1, y + settings.getEventHeight() + 2); // h

            if (subAlpha) {
                gc.setAlpha(50);
            }
            gc.setForeground(colorManager.getFadeOffColor1());
            gc.drawLine(xLoc, y + settings.getEventHeight() + 3, xLoc + eventWidth + 1, y + settings.getEventHeight() + 3); // h
            // next vertical starts 1 pixel further down and 1 pixel further right and dips 1 pixel below bottom
            gc.drawLine(xLoc + eventWidth + 2, y + 3, xLoc + eventWidth + 2, y + settings.getEventHeight() + 1); // v
            if (subAlpha) {
                gc.setAlpha(255);
                gc.setAdvanced(false);
            }
        }

        // whacky % completes don't get to play
        if (event.getPercentComplete() > 0 && event.getPercentComplete() <= 100) {
            final int yStart = y + (settings.getEventHeight() / 2) - 1;

            // xEnd is how long the event box is
            // how much of that in % are we showing?
            final float perc = event.getPercentComplete() / 100f;
            // and how many pixels is that?
            float toDraw = eventWidth * perc;
            // remainder
            final int remainder = eventWidth - (int) toDraw;

            if (alpha) {
                gc.setAlpha(settings.getPercentageBarAlpha());
            }

            xLoc += 1;
            toDraw -= 1;

            // draw the inner bar
            gc.setForeground(colorManager.getPercentageBarColorTop());
            gc.setBackground(colorManager.getPercentageBarColorBottom());
            gc.fillGradientRectangle(xLoc, yStart - settings.getEventPercentageBarHeight() / 2 + 1, (int) toDraw, settings.getEventPercentageBarHeight(), true);

            if (settings.drawFullPercentageBar()) {
                if (alpha) {
                    gc.setAlpha(settings.getRemainderPercentageBarAlpha());
                }

                gc.setForeground(colorManager.getPercentageBarRemainderColorTop());
                gc.setBackground(colorManager.getPercentageBarRemainderColorBottom());
                gc.fillGradientRectangle(xLoc + (int) toDraw, yStart - settings.getEventPercentageBarHeight() / 2 + 1, remainder, settings.getEventPercentageBarHeight(), true);
            }

            if (alpha) {
                gc.setAlpha(255);
                gc.setAdvanced(false);
            }
        }
    }

    public void drawCheckpoint(final GanttComposite ganttComposite, final ISettings settings, final IColorManager colorManager, final GanttEvent event, final GC gc, final boolean threeDee, final int dayWidth, final int x, final int y, final Rectangle bounds) {
        Color cEvent = event.getStatusColor();

        if (cEvent == null) {
            cEvent = settings.getDefaultEventColor();
        }

        gc.setBackground(cEvent);

        final int height = settings.getEventHeight();

        // draw a special fun thing! (tm)
        final long days = DateHelper.daysBetween(event.getActualStartDate(), event.getActualEndDate());

        drawCheckpointMarker(gc, settings, colorManager, event, threeDee, x, y, dayWidth, height, bounds);

        // multi day checkpoint
        if (days != 0) {
            final int width = (int) days * dayWidth;
            drawCheckpointMarker(gc, settings, colorManager, event, threeDee, x + width, y, dayWidth, height, bounds);

            // draw center
            final int neg = height / 2 - 1;
            final Rectangle rect = new Rectangle(x + dayWidth, y + neg, width - dayWidth, neg);
            gc.setForeground(colorManager.getBlack());
            gc.fillRectangle(rect);
            gc.drawRectangle(rect);

            if (settings.showBarsIn3D()) {
                final boolean alpha = (colorManager.useAlphaDrawing() || colorManager.useAlphaDrawingOn3DEventDropShadows());
                if (alpha) {
                    gc.setAlpha(200);
                }

                gc.setForeground(colorManager.getFadeOffColor1());
                gc.drawLine(rect.x + 1, rect.y + rect.height + 1, rect.x + rect.width - 1, rect.y + rect.height + 1);

                if (alpha) {
                    gc.setAlpha(100);
                }

                gc.setForeground(colorManager.getFadeOffColor2());
                gc.drawLine(rect.x + 1, rect.y + rect.height + 2, rect.x + rect.width - 1, rect.y + rect.height + 2);

                if (alpha) {
                    gc.setAlpha(50);
                }

                gc.setForeground(colorManager.getFadeOffColor3());
                gc.drawLine(rect.x + 1, rect.y + rect.height + 3, rect.x + rect.width - 1, rect.y + rect.height + 3);

                if (alpha) {
                    gc.setAlpha(255);
                    gc.setAdvanced(false);
                }
            }
        }
    }

    private void drawCheckpointMarker(final GC gc, final ISettings settings, final IColorManager colorManager, final GanttEvent event, final boolean threeDee, final int x, final int y, final int width, final int height, final Rectangle bounds) {
        final float fHoriSpacer = width * 0.17f;
        final int hSpacer = (int) fHoriSpacer;

        final float fVertiSpacer = height * 0.23f;
        final int vSpacer = (int) fVertiSpacer;

        final Rectangle topToBottom = new Rectangle(x + hSpacer, y, width - (hSpacer * 2), height + vSpacer);
        final Rectangle leftToRight = new Rectangle(x, y + vSpacer, width, height - vSpacer);
        final Rectangle inner = new Rectangle(x + hSpacer, y + vSpacer, width - (hSpacer * 2), height - (vSpacer * 2));

        Color cEvent = event.getStatusColor();
        Color gradient = event.getGradientStatusColor();

        if (cEvent == null) {
            cEvent = settings.getDefaultEventColor();
        }
        if (gradient == null) {
            gradient = settings.getDefaultGradientEventColor();
        }

        gc.setForeground(gradient);
        gc.setBackground(cEvent);
        gc.fillRectangle(topToBottom);
        gc.fillRectangle(leftToRight);
        gc.fillGradientRectangle(inner.x, inner.y, inner.width, inner.height, true);

        gc.setForeground(colorManager.getBlack());

        gc.drawRectangle(topToBottom);
        gc.drawRectangle(leftToRight);

        if (threeDee) {
            final boolean alpha = (colorManager.useAlphaDrawing() || colorManager.useAlphaDrawingOn3DEventDropShadows());
            if (alpha) {
                gc.setAlpha(200);
            }

            gc.setForeground(colorManager.getFadeOffColor1());
            // horizontal line.. ends a few pixles right of bottom right corner
            gc.drawLine(leftToRight.x, leftToRight.y + leftToRight.height + 1, leftToRight.x + hSpacer - 1, leftToRight.y + leftToRight.height + 1);
            gc.drawLine(leftToRight.x + hSpacer, leftToRight.y + leftToRight.height + vSpacer + 1, leftToRight.x - hSpacer + leftToRight.width, leftToRight.y + leftToRight.height + vSpacer + 1);
            gc.drawLine(leftToRight.x + leftToRight.width - hSpacer + 1, leftToRight.y + leftToRight.height + 1, leftToRight.x + leftToRight.width + 1, leftToRight.y + leftToRight.height + 1);

            // vertical line at end, starts slightly below top right corner
            gc.drawLine(leftToRight.x + leftToRight.width + 1, leftToRight.y + 2, leftToRight.x + leftToRight.width + 1, leftToRight.y + leftToRight.height + 1);

            if (alpha) {
                gc.setAlpha(100);
            }

            gc.setForeground(colorManager.getFadeOffColor2());
            gc.drawLine(leftToRight.x, leftToRight.y + leftToRight.height + 2, leftToRight.x + hSpacer - 1, leftToRight.y + leftToRight.height + 2);
            gc.drawLine(leftToRight.x + hSpacer, leftToRight.y + leftToRight.height + vSpacer + 2, leftToRight.x - hSpacer + leftToRight.width, leftToRight.y + leftToRight.height + vSpacer + 2);
            gc.drawLine(leftToRight.x + leftToRight.width - hSpacer + 1, leftToRight.y + leftToRight.height + 2, leftToRight.x + leftToRight.width + 1, leftToRight.y + leftToRight.height + 2);

            gc.drawLine(leftToRight.x + leftToRight.width + 2, leftToRight.y + 3, leftToRight.x + leftToRight.width + 2, leftToRight.y + leftToRight.height + 1);

            if (alpha) {
                gc.setAlpha(50);
            }

            gc.setForeground(colorManager.getFadeOffColor3());
            gc.drawLine(leftToRight.x, leftToRight.y + leftToRight.height + 3, leftToRight.x + hSpacer - 1, leftToRight.y + leftToRight.height + 3);
            gc.drawLine(leftToRight.x + hSpacer, leftToRight.y + leftToRight.height + vSpacer + 3, leftToRight.x - hSpacer + leftToRight.width, leftToRight.y + leftToRight.height + vSpacer + 3);
            gc.drawLine(leftToRight.x + leftToRight.width - hSpacer + 1, leftToRight.y + leftToRight.height + 3, leftToRight.x + leftToRight.width + 1, leftToRight.y + leftToRight.height + 3);

            gc.drawLine(leftToRight.x + leftToRight.width + 3, leftToRight.y + 4, leftToRight.x + leftToRight.width + 3, leftToRight.y + leftToRight.height + 1);

            if (alpha) {
                gc.setAlpha(255);
                gc.setAdvanced(false);
            }

        }
    }

    public void drawPlannedDates(final GanttComposite ganttComposite, final ISettings settings, final IColorManager colorManager, final GanttEvent event, final GC gc, final boolean threeDee, final int x, final int y, final int eventWidth, final Rectangle bounds) {

        final int spacer = settings.getRevisedLineSpacer();

        if (event.isScope()) {
            return;
        }

        if (event.getStartDate() != null) {
            final int xs = ganttComposite.getStartingXFor(event.getStartDate());
            // commenting this out July 2, 2009, if we draw the marker, draw it regardless if it's same or not
            // otherwise doesn't make much sense
            //if (xs != ge.getX()) {				
            final int ys = y - spacer;
            gc.setForeground(colorManager.getRevisedStartColor());
            gc.drawLine(xs, ys, x, ys);
            gc.drawLine(xs, ys - 3, xs, ys + 3);
            gc.drawLine(x, ys - 3, x, ys + 3);
            //}
        }
        if (event.getEndDate() != null) {
            int xe = ganttComposite.getStartingXFor(event.getEndDate());
            xe += ganttComposite.getDayWidth();
            //if (xe != ge.getXEnd()) {
            final int ys = y + settings.getEventHeight() + spacer;
            gc.setForeground(colorManager.getRevisedEndColor());
            gc.drawLine(xe, ys, x + eventWidth, ys);
            gc.drawLine(xe, ys - 3, xe, ys + 3);
            gc.drawLine(x + eventWidth, ys - 3, x + eventWidth, ys + 3);
            //}
        }
    }

    public void drawDaysOnChart(final GanttComposite ganttComposite, final ISettings settings, final IColorManager colorManager, final GanttEvent event, final GC gc, final boolean threeDee, final int x, final int y, final int eventWidth, final int daysNumber, final Rectangle bounds) {
        if (event.isImage()) {
            return;
        }

        final int top = y - 2;
        final int xE = x + eventWidth;
        final int middle = x + ((xE - x) / 2);
        int yMiddle = event.getY() + (event.getHeight() / 2);

        final StringBuffer buf = new StringBuffer();
        buf.append(daysNumber);
        final String dayString = buf.toString();

        final Point extent = gc.stringExtent(dayString);
        final Point unmodified = new Point(extent.x, extent.y);
        extent.x = extent.x + (2 * 2) + 2; // 2 pixel spacing on 2 sides, for clarity's sake

        Color gradient = event.getGradientStatusColor();

        if (gradient == null) {
            gradient = settings.getDefaultGradientEventColor();
        }

        if ((middle - extent.x) > x) {
            gc.setBackground(gradient);
            gc.fillRectangle(middle - extent.x / 2, top, extent.x, settings.getEventHeight() + 4);
            gc.setForeground(colorManager.getTextColor());
            gc.drawRectangle(middle - extent.x / 2, top, extent.x, settings.getEventHeight() + 4);

            yMiddle -= unmodified.y / 2;
            gc.drawString(dayString, middle - unmodified.x + (unmodified.x / 2) + 1, yMiddle, true);
        }
    }

    public void drawEventString(final GanttComposite ganttComposite, final ISettings settings, final IColorManager colorManager, final GanttEvent event, final GC gc, final String toDraw, final boolean threeDee, final int x, final int y, final int eventWidth, final Rectangle bounds) {
        int textEndX = 0;
        int yTextPos = y + (event.getHeight() / 2);

        Font oldFont = null;

        gc.setForeground(colorManager.getTextColor());
        if (event.showBoldText()) {
            oldFont = gc.getFont();
            final FontData[] old = oldFont.getFontData();
            old[0].setStyle(SWT.BOLD);
            final Font f = new Font(Display.getDefault(), old);
            gc.setFont(f);
            // DISPOSE FONT or we'll run out of handles
            f.dispose();
        }

        // font overrides a bold setting
        if (event.getTextFont() != null) {
            gc.setFont(event.getTextFont());
        }

        final Point toDrawExtent = event.getNameExtent();

        final int textSpacer = ganttComposite.isConnected(event) ? settings.getTextSpacerConnected() : settings.getTextSpacerNonConnected();

        int textXStart = 0;

        // find the horizontal text location
        switch (event.getHorizontalTextLocation()) {
            case SWT.LEFT:
                textXStart = x - textSpacer - toDrawExtent.x;
                break;
            case SWT.CENTER:
            	if (!settings.shiftHorizontalCenteredEventString() || toDrawExtent.x < eventWidth) {
                textXStart = x + (eventWidth / 2) - (toDrawExtent.x / 2);
                break;
            	}
            case SWT.RIGHT:
                //textXStart = x + eventWidth + textSpacer;
            	int eventOrPictureWidth = eventWidth;

            	// bugzilla feature request #309808
            	if (!settings.scaleImageToDayWidth() && event.getPicture() != null) {
            		// the image is drawn centered, therefore consider only half of its width
            		eventOrPictureWidth = Math.max(eventOrPictureWidth, event.getPicture().getImageData().width / 2);
            	}
            	
            	textXStart = x + eventOrPictureWidth + textSpacer;
                break;
            default:
                break;
        }

        // find the vertical text location
        switch (event.getVerticalTextLocation()) {
            case SWT.TOP:
                yTextPos = event.getY() - toDrawExtent.y;
                break;
            case SWT.CENTER:
                yTextPos -= (toDrawExtent.y / 2) - 1;
                break;
            case SWT.BOTTOM:
                yTextPos = event.getBottomY();
                break;
            default:
                break;
        }

        gc.drawString(toDraw, textXStart, yTextPos, true);
        int extra = textSpacer + toDrawExtent.x;
        textEndX = x + eventWidth + extra;

        // draw lock icon if parent phase is locked
        if (event.isLocked()) {
            final Image lockImage = settings.getLockImage();
            if (textEndX != 0 && lockImage != null) {
                gc.drawImage(lockImage, textEndX, y);
                extra += lockImage.getBounds().width;
            }
        }

        // regardless of horizontal alignment, it will still add on, so we can leave this as is
        event.setWidthWithText(event.getWidth() + extra);

        // reset font
        gc.setFont(oldFont);
    }

    public void drawScope(final GanttComposite ganttComposite, final ISettings settings, final IColorManager colorManager, final GanttEvent event, final GC gc, final boolean threeDee, final int dayWidth, final int x, final int y, final int eventWidth, final Rectangle bounds) {
        final List scopeEvents = event.getScopeEvents();

        // empty scope
        if (scopeEvents.isEmpty()) {
            return;
        }

        gc.setForeground(colorManager.getScopeGradientColorTop());
        gc.setBackground(colorManager.getScopeGradientColorBottom());

        gc.fillGradientRectangle(x, y, eventWidth, settings.getEventHeight() - 5, true);
        gc.setForeground(colorManager.getScopeBorderColor());
        gc.drawRectangle(new Rectangle(x, y, eventWidth, settings.getEventHeight() - 5));

        gc.setForeground(colorManager.getScopeGradientColorTop());
        gc.setBackground(colorManager.getScopeGradientColorBottom());
        gc.fillGradientRectangle(x, y, dayWidth / 2, settings.getEventHeight(), true);
        gc.fillGradientRectangle(x + eventWidth + (dayWidth / 2) - dayWidth, y, dayWidth / 2, settings.getEventHeight(), true);

        gc.setForeground(colorManager.getScopeBorderColor());
        gc.drawRectangle(x, y, dayWidth / 2, settings.getEventHeight());
        gc.drawRectangle(x + eventWidth + (dayWidth / 2) - dayWidth, y, dayWidth / 2, settings.getEventHeight());

        if (threeDee) {
            final boolean alpha = (colorManager.useAlphaDrawing() || colorManager.useAlphaDrawingOn3DEventDropShadows());
            if (alpha) {
                gc.setAlpha(200);
            }

            gc.setForeground(colorManager.getFadeOffColor1());
            gc.drawLine(x, y + settings.getEventHeight() + 1, x + dayWidth / 2, y + settings.getEventHeight() + 1);
            gc.drawLine(x + eventWidth - (dayWidth / 2), y + settings.getEventHeight() + 1, x + eventWidth, y + settings.getEventHeight() + 1);
            gc.drawLine(x + (dayWidth / 2) + 1, y + settings.getEventHeight() - 4, x + eventWidth - (dayWidth / 2) - 1, y + settings.getEventHeight() - 4);

            if (alpha) {
                gc.setAlpha(100);
            }

            gc.setForeground(colorManager.getFadeOffColor2());
            gc.drawLine(x, y + settings.getEventHeight() + 2, x + dayWidth / 2, y + settings.getEventHeight() + 2);
            gc.drawLine(x + eventWidth - (dayWidth / 2), y + settings.getEventHeight() + 2, x + eventWidth, y + settings.getEventHeight() + 2);
            gc.drawLine(x + (dayWidth / 2) + 1, y + settings.getEventHeight() - 3, x + eventWidth - (dayWidth / 2) - 1, y + settings.getEventHeight() - 3);

            if (alpha) {
                gc.setAlpha(50);
            }

            gc.setForeground(colorManager.getFadeOffColor3());
            gc.drawLine(x, y + settings.getEventHeight() + 3, x + dayWidth / 2, y + settings.getEventHeight() + 3);
            gc.drawLine(x + eventWidth - (dayWidth / 2), y + settings.getEventHeight() + 3, x + eventWidth, y + settings.getEventHeight() + 3);
            gc.drawLine(x + (dayWidth / 2) + 1, y + settings.getEventHeight() - 2, x + eventWidth - (dayWidth / 2) - 1, y + settings.getEventHeight() - 2);

            if (alpha) {
                gc.setAlpha(255);
                gc.setAdvanced(false);
            }

        }

    }

    public void drawImage(final GanttComposite ganttComposite, final ISettings settings, final IColorManager colorManager, final GanttEvent event, final GC gc, final Image image, final boolean threeDee, final int dayWidth, final int xLoc, final int yStart, final Rectangle fullBounds) {

        int y = yStart;
        int x = xLoc;

        // draw a cross in a box if image is null
        if (image == null) {
            gc.setForeground(colorManager.getBlack());
            gc.drawRectangle(x, y, dayWidth, settings.getEventHeight());
            gc.drawLine(x, y, x + dayWidth, y + settings.getEventHeight());
            gc.drawLine(x + dayWidth, y, x, y + settings.getEventHeight());
            return;
        }

        // can it fit?
        final Rectangle bounds = image.getBounds();
        if (settings.scaleImageToDayWidth() && bounds.width > dayWidth) {
            // shrink image
            ImageData id = image.getImageData();
            final int diff = id.width - dayWidth;
            id.width -= diff;
            id.height -= diff;
            final Image temp = new Image(Display.getDefault(), id);

            final int negY = (bounds.height - settings.getEventHeight());
            if (negY > 0) {
                y += negY / 2;
            }

            gc.drawImage(temp, x, y);
            temp.dispose();
            return;
        } else {
            // center it x-wise
            x -= (bounds.width - dayWidth) / 2;
            if (settings.getEventHeight() > bounds.height) {
            	y += (settings.getEventHeight() - bounds.height);
            }
        }

        gc.drawImage(image, x, y);
    }

    public void drawArrowHead(final int x, final int y, final int face, final GC gc) {
        switch (face) {
            case SWT.UP:
                gc.drawLine(x, y + 3, x, y + 3);
                gc.drawLine(x - 1, y + 4, x + 1, y + 4);
                gc.drawLine(x - 2, y + 5, x + 2, y + 5);
                gc.drawLine(x - 3, y + 6, x + 3, y + 6);
                gc.drawLine(x - 4, y + 7, x + 4, y + 7);
                break;
            case SWT.DOWN:
                gc.drawLine(x, y + 7, x, y + 7);
                gc.drawLine(x - 1, y + 6, x + 1, y + 6);
                gc.drawLine(x - 2, y + 5, x + 2, y + 5);
                gc.drawLine(x - 3, y + 4, x + 3, y + 4);
                gc.drawLine(x - 4, y + 3, x + 4, y + 3);
                break;
            case SWT.RIGHT:
                // don't need 1 as a line will be on it
                gc.drawLine(x + 3, y - 4, x + 3, y + 4);
                gc.drawLine(x + 4, y - 3, x + 4, y + 3);
                gc.drawLine(x + 5, y - 2, x + 5, y + 2);
                gc.drawLine(x + 6, y - 1, x + 6, y + 1);
                break;
            case SWT.LEFT:
                // don't need 1 as a line will be on it
                gc.drawLine(x - 3, y - 4, x - 3, y + 4);
                gc.drawLine(x - 4, y - 3, x - 4, y + 3);
                gc.drawLine(x - 5, y - 2, x - 5, y + 2);
                gc.drawLine(x - 6, y - 1, x - 6, y + 1);
                break;
            default:
                break;
        }
    }

    public void drawLockedDateRangeMarker(final GanttComposite ganttComposite, final ISettings settings, final IColorManager colorManager, final GanttEvent ge, final GC gc, final boolean threeDee, final int dayWidth, final int y, final int startLoc, final int end, final Rectangle bounds) {
        int start = startLoc;
        final int maxY = settings.getEventHeight();
        final int topY = y - 2;
        int xEnd = end;

        gc.setForeground(ColorCache.getColor(188, 188, 188));
        gc.setLineStyle(SWT.LINE_SOLID);
        gc.setLineWidth(1);

        // we don't draw any extras on the hours view, it doesn't behave like the others
        int extra = dayWidth;
        if (ganttComposite.getCurrentView() == ISettings.VIEW_DAY) {
            extra = 0;
        }

        if (start != -1 && xEnd != -1) {
            // no need to draw beyond what we can see, and it's extremely slow to draw dots anyway, so we need this to be as fast as can be
            if (start < 0) {
                start = -1;
            }
            if (xEnd > bounds.width) {
                xEnd = bounds.width + 1;
            }

            // space it slightly or we'll draw on top of event borders
            gc.drawRectangle(start - 1, topY, xEnd - start + extra + 2, maxY + 4);
        } else {
            //gc.setLineStyle(SWT.LINE_SOLID);
            if (start != -1) {
                gc.drawRectangle(start - 4, topY + 1, 2, 3 + maxY);
                gc.drawLine(start - 2, topY + 1, start + 5, topY + 1);
                gc.drawLine(start - 2, topY + 4 + maxY, start + 5, topY + 4 + maxY);
            }
            if (xEnd != -1) {
                xEnd += extra;
                gc.drawRectangle(xEnd + 2, topY + 1, 2, 3 + maxY);
                gc.drawLine(xEnd + 2, topY + 1, xEnd - 5, topY + 1);
                gc.drawLine(xEnd + 2, topY + 4 + maxY, xEnd - 5, topY + 4 + maxY);
            }
        }

        //gc.setLineWidth(1);
        //gc.setLineStyle(SWT.LINE_SOLID);
    }

}
