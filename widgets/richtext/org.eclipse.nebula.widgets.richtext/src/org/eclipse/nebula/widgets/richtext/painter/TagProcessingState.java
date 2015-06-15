/*****************************************************************************
 * Copyright (c) 2015 CEA LIST.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *		Dirk Fauth <dirk.fauth@googlemail.com> - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.nebula.widgets.richtext.painter;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.Point;

/**
 * This class is used to keep track of the style setting regarding opened tags. It is needed to
 * support styling ranges over several lines or rendering, because tags can be opened for rendering
 * on one line, while it is closed on another line.
 */
public class TagProcessingState {

	/**
	 * Enumeration to specify the text alignment in a paragraph.
	 */
	public enum TextAlignment {
		LEFT, CENTER, RIGHT, JUSTIFY
	}

	/**
	 * The coordinates where the drawing operations should start from.
	 */
	private Point startingPoint = new Point(0, 0);
	/**
	 * The coordinates where the drawing operations should be performed from.
	 */
	private Point pointer = new Point(0, 0);
	/**
	 * The left margin in a paragraph.
	 */
	private int marginLeft;
	/**
	 * The alignment of the current paragraph.
	 */
	private TextAlignment textAlignment = TextAlignment.LEFT;
	/**
	 * The previous set foreground color. Since there is no nesting of spans to perform color
	 * styling, a stack is not necessary.
	 */
	private Color prevColor;
	/**
	 * The previous set background color. Since there is no nesting of spans to perform color
	 * styling, a stack is not necessary.
	 */
	private Color prevBgColor;
	/**
	 * Flag that indicates whether underline styling is active or not. Necessary because the
	 * underline tag can wrap several other font or styling tags.
	 */
	private boolean underlineActive = false;
	/**
	 * Flag that indicates whether strikethrough styling is active or not. Necessary because the
	 * strikethrough tag can wrap several other font or styling tags.
	 */
	private boolean strikethroughActive = false;
	/**
	 * Stack of used fonts. Necesssary because font styling options can be nested (e.g. bold, size,
	 * type) and therefore need to be reset in the correct order on close.
	 */
	private Deque<Font> fontStack = new LinkedList<>();
	/**
	 * Flag that indicates whether the current list is an ordered or unordered list. Necessary for
	 * rendering the list item bullet.
	 */
	private Deque<Boolean> orderedListStack = new LinkedList<>();
	/**
	 * Current list item number. Necessary for rendering an ordered list.
	 */
	private Deque<Integer> listNumberStack = new LinkedList<>();
	/**
	 * The additional margin that is used on rendering a list. Necessary to align the text part of
	 * lists.
	 */
	private Deque<Integer> listMarginStack = new LinkedList<>();
	/**
	 * The {@link LinePainter} of the current rendered line.
	 */
	private LinePainter currentLine;
	/**
	 * The {@link Iterator} used to iterate over the lines that are rendered.
	 */
	private Iterator<LinePainter> lineIterator;
	/**
	 * Flag that indicates whether drawing operations should be performed or not. Necessary for
	 * embedded rendering to be able to calculate the dynamic dimensions without rendering directly.
	 */
	private boolean rendering = true;
	/**
	 * The number of paragraphs that are found in a text. Needed to calculate the preferred height.
	 */
	private int paragraphCount = 0;

	/**
	 * @return The previous set foreground color.
	 */
	public Color getPrevColor() {
		return prevColor;
	}

	/**
	 * @param prevColor
	 *            The previous set foreground color.
	 */
	public void setPrevColor(Color prevColor) {
		this.prevColor = prevColor;
	}

	/**
	 * @return The previous set background color.
	 */
	public Color getPrevBgColor() {
		return prevBgColor;
	}

	/**
	 * @param prevBgColor
	 *            The previous set background color.
	 */
	public void setPrevBgColor(Color prevBgColor) {
		this.prevBgColor = prevBgColor;
	}

	/**
	 * Add the given {@link Font} to the stack of previous set fonts.
	 * 
	 * @param font
	 *            The {@link Font} to add to the previous font stack
	 */
	public void addPreviousFont(Font font) {
		this.fontStack.addLast(font);
	}

	/**
	 * Removes and returns the last font from the previous font stack. (LIFO)
	 * 
	 * @return The last {@link Font} that was added to the previous font stack.
	 */
	public Font pollPreviousFont() {
		return this.fontStack.pollLast();
	}

	/**
	 * @return <code>true</code> if underline styling is active, <code>false</code> if not.
	 */
	public boolean isUnderlineActive() {
		return underlineActive;
	}

	/**
	 * @param underlineActive
	 *            <code>true</code> if underline styling should be active, <code>false</code> if
	 *            not.
	 */
	public void setUnderlineActive(boolean underlineActive) {
		this.underlineActive = underlineActive;
	}

	/**
	 * @return <code>true</code> if strikethrough styling is active, <code>false</code> if not.
	 */
	public boolean isStrikethroughActive() {
		return strikethroughActive;
	}

	/**
	 * @param strikethroughActive
	 *            <code>true</code> if strikethrough styling should be active, <code>false</code> if
	 *            not.
	 */
	public void setStrikethroughActive(boolean strikethroughActive) {
		this.strikethroughActive = strikethroughActive;
	}

	public void setStartingPoint(int startX, int startY) {
		this.startingPoint.x = startX;
		this.startingPoint.y = startY;

		this.pointer.x = startX;
		this.pointer.y = startY;
	}

	public Point getPointer() {
		return pointer;
	}

	public void increaseX(int x) {
		this.pointer.x += x;
	}

	public void increaseY(int y) {
		this.pointer.y += y;
	}

	public void setX(int x) {
		this.pointer.x = x;
	}

	public void setY(int y) {
		this.pointer.y = y;
	}

	/**
	 * Reset the x coordinate of the pointer to the value of the left margin. This is used to start
	 * a new line.
	 */
	public void resetX() {
		this.pointer.x = this.startingPoint.x + this.marginLeft + getListMargin();
	}

	public void calculateX(int areaWidth) {
		if (textAlignment.equals(TextAlignment.LEFT)
				|| textAlignment.equals(TextAlignment.JUSTIFY)) {
			this.pointer.x = this.startingPoint.x + this.marginLeft + getListMargin();
		}
		else if (textAlignment.equals(TextAlignment.RIGHT)) {
			int space = areaWidth - (this.marginLeft + getListMargin());
			this.pointer.x = this.startingPoint.x + this.marginLeft + getListMargin() + (space - getCurrentLine().getContentWidth());
		}
		else if (textAlignment.equals(TextAlignment.CENTER)) {
			int space = areaWidth - (this.marginLeft + getListMargin());
			this.pointer.x = this.startingPoint.x + this.marginLeft + getListMargin() + ((space - getCurrentLine().getContentWidth()) / 2);
		}
	}

	public int getMarginLeft() {
		return marginLeft;
	}

	public void setMarginLeft(int marginLeft) {
		this.marginLeft = marginLeft;
	}

	public TextAlignment getTextAlignment() {
		return textAlignment;
	}

	public void setTextAlignment(TextAlignment textAlignment) {
		this.textAlignment = textAlignment;
	}

	public LinePainter getCurrentLine() {
		return currentLine;
	}

	public void setLineIterator(Iterator<LinePainter> lineIterator) {
		this.lineIterator = lineIterator;
	}

	public void activateNextLine() {
		currentLine = lineIterator.next();
	}

	public int getCurrentLineHeight() {
		return this.currentLine.getLineHeight();
	}

	public FontMetrics getCurrentBiggestFontMetrics() {
		return this.currentLine.getBiggestMetrics();
	}

	public boolean isRendering() {
		return rendering;
	}

	public void setRendering(boolean render) {
		this.rendering = render;
	}

	public boolean isOrderedList() {
		return this.orderedListStack.peekLast();
	}

	public void setOrderedList(boolean orderedList) {
		this.orderedListStack.add(orderedList);
	}

	public int getListMargin() {
		int result = 0;
		for (Integer margin : this.listMarginStack) {
			result += margin;
		}
		return result;
	}

	public void setListMargin(int listMargin) {
		this.listMarginStack.add(listMargin);
	}

	public Integer getCurrentListNumber() {
		return this.listNumberStack.peekLast();
	}

	public void initCurrentListNumber() {
		this.listNumberStack.add(1);
	}

	public void increaseCurrentListNumber() {
		int current = this.listNumberStack.pollLast();
		current++;
		this.listNumberStack.add(current);
	}

	public int getListDepth() {
		return this.listNumberStack.size();
	}

	public void resetListConfiguration() {
		this.listNumberStack.removeLast();
		this.listMarginStack.removeLast();
		this.orderedListStack.removeLast();
	}

	public int getParagraphCount() {
		return paragraphCount;
	}

	public void increaseParagraphCount() {
		this.paragraphCount++;
	}
}
