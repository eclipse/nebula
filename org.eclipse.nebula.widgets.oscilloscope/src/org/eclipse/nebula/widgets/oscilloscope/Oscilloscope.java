/*******************************************************************************
 *  Copyright (c) 2010 Weltevree Beheer BV, Remain Software & Industrial-TSI
 * 
 * All rights reserved. 
 * This program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Wim S. Jongman - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.oscilloscope;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/**
 * Animated widget that tries to mimic an Oscilloscope.
 * 
 * <i>An oscilloscope (also known as a scope, CRO or, an O-scope) is a type of
 * electronic test instrument that allows observation of constantly varying
 * signal voltages, usually as a two-dimensional graph of one or more electrical
 * potential differences using the vertical or 'Y' axis, plotted as a function
 * of time, (horizontal or 'x' axis).</i>
 * <p/>
 * <a href="http://en.wikipedia.org/wiki/Oscilloscope">http://en.wikipedia.org/
 * wiki/Oscilloscope<a/>
 * 
 * @author Wim.Jongman (@remainsoftware.com)
 * 
 */
public class Oscilloscope extends Canvas {

	/**
	 * The default comfortable widget width.
	 */
	public static final int DEFAULT_WIDTH = 180;

	/**
	 * The default comfortable widget height.
	 */
	public static final int DEFAULT_HEIGHT = 100;

	/**
	 * The default amount of tail fading in percentages.
	 */
	public static final int DEFAULT_TAILFADE = 25;

	private Color bg;
	private Color fg;
	private int cursor = 50;
	private int width = DEFAULT_WIDTH;
	private int height = DEFAULT_HEIGHT;
	private int base;
	private IntegerFiFoCircularStack stack;
	private int tailSize;
	private int lineWidth = 1;
	private boolean percentage = false;

	/**
	 * This contains the old or historical input and is used to paint the tail
	 * of the graph.
	 */
	private int[] tail;

	/**
	 * This contains the actual values that where input by the user before
	 * scaling. If the user resized we can calculate how the tail would have
	 * looked with the new window dimensions.
	 * 
	 * @see Oscilloscope#tail
	 */
	// private int[] originalTailInput;
	private int originalTailSize;
	private boolean steady;
	private int tailFade = 25;
	private boolean fade;
	private boolean connect;
	private int originalSteadyPosition = STEADYPOSITION_75PERCENT;

	/**
	 * This set of values will draw a figure that is similar to the heart beat
	 * that you see on hospital monitors.
	 */
	public static final int[] HEARTBEAT = new int[] { 2, 10, 2, -16, 16, 44,
			49, 44, 32, 14, -16, -38, -49, -47, -32, -10, 8, 6, 6, -2, 6, 4, 2,
			0, 0, 6, 8, 6 };
	/**
	 * Will draw a maximum tail.
	 */
	public static final int TAILSIZE_MAX = -1;

	/**
	 * Will draw a tail from the left border but is only valid if the boolean in
	 * {@link #setSteady(boolean, int)} was set to true, will default to
	 * {@link #TAILSIZE_MAX} otherwise.
	 */
	public static final int TAILSIZE_FILL = -2;

	/**
	 * The default tail size is 75% of the width.
	 */
	public static final int TAILSIZE_DEFAULT = -3;

	/**
	 * Steady position @ 75% of graph.
	 */
	public static final int STEADYPOSITION_75PERCENT = -1;

	/**
	 * The base of the line is positioned at the center of the widget.
	 * 
	 * @see #setBaseOffset(int)
	 */
	public static final int BASE_CENTER = 50;
	private int baseOffset = BASE_CENTER;

	private ArrayList<OscilloscopeStackAdapter> stackListeners;
	private int progression = 1;

	private int heightBeforeResize;

	private int widthBeforeResize;

	/**
	 * The stack will not overflow if you push too many values into it but it
	 * will rotate and overwrite the older values. Think of the stack as a
	 * closed ring with one hole to push values in and one that lets them out.
	 * 
	 */
	public class IntegerFiFoCircularStack {
		final private int[] stack;
		private int top;
		private int bottom;
		private final int capacity;

		/**
		 * Creates a stack with the indicated capacity.
		 * 
		 * @param capacity
		 */
		public IntegerFiFoCircularStack(int capacity) {
			this.capacity = capacity;
			stack = new int[capacity];
			top = 0;
			bottom = 0;
		}

		/**
		 * Creates stack with the indicated capacity and copies the old stack
		 * into the new stack.
		 * 
		 * @param capacity
		 * @param oldStack
		 */
		public IntegerFiFoCircularStack(int capacity,
				IntegerFiFoCircularStack oldStack) {
			this(capacity);
			while (!oldStack.isEmpty())
				push(oldStack.pop(0));
		}

		/**
		 * Clears the stack.
		 */
		public void clear() {
			synchronized (stack) {
				for (int i = 0; i < stack.length; i++) {
					stack[i] = 0;
				}
				top = 0;
				bottom = 0;
			}
		}

		/**
		 * Puts a value on the stack.
		 * 
		 * @param value
		 */
		public void push(int value) {
			if (top == capacity - 1)
				top = 0;
			stack[top++] = value * -1;
		}

		/**
		 * Returns the oldest value from the stack. Returns the supplied entry
		 * if the stack is empty.
		 * 
		 * @param valueIfEmpty
		 * @return int
		 */
		public int pop(int valueIfEmpty) {
			if (bottom == top)
				return bottom = top = valueIfEmpty;
			if (bottom == capacity - 1)
				bottom = 0;
			return stack[bottom++];
		}

		/**
		 * Returns the oldest value from the stack without removing the value
		 * from the stack. Returns the supplied entry if the stack is empty.
		 * 
		 * @param valueIfEmpty
		 * @return int
		 */
		public int peek(int valueIfEmpty) {
			if (bottom == top)
				return valueIfEmpty;
			if (bottom == capacity - 1)
				return stack[0];
			return stack[bottom];
		}

		/**
		 * 
		 * @return boolean
		 */
		public boolean isEmpty() {
			return bottom == top;
		}

	}

	/**
	 * Creates a new Oscilloscope.
	 * 
	 * @param parent
	 * @param style
	 */
	public Oscilloscope(Composite parent, int style) {
		super(parent, SWT.DOUBLE_BUFFERED | style);

		bg = new Color(null, 0, 0, 0);
		setBackground(bg);

		fg = new Color(null, 255, 255, 255);
		setForeground(fg);

		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				Oscilloscope.this.widgetDisposed(e);
			}
		});

		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				Oscilloscope.this.paintControl(e);
			}
		});

		addControlListener(new ControlListener() {
			public void controlResized(ControlEvent e) {
				Oscilloscope.this.controlResized(e);
			}

			public void controlMoved(ControlEvent e) {
				Oscilloscope.this.controlMoved(e);
			}
		});

		setTailSize(TAILSIZE_DEFAULT);
	}

	protected void controlMoved(ControlEvent e) {

	}

	protected void controlResized(ControlEvent e) {

		heightBeforeResize = height;
		widthBeforeResize = width;
		setSizeInternal(getSize().x, getSize().y);

		if (getBounds().width > 0) {
			setSteady(steady, originalSteadyPosition);
			setTailSizeInternal();
		}

		heightBeforeResize = height;
		widthBeforeResize = width;
	}

	/**
	 * Returns the size of the tail.
	 * 
	 * @return int
	 * @see #setTailSize(int)
	 * @see #TAILSIZE_DEFAULT
	 * @see #TAILSIZE_FILL
	 * @see #TAILSIZE_MAX
	 * 
	 */
	public int getTailSize() {
		checkWidget();
		return tailSize;
	}

	private void setSizeInternal(int width, int height) {
		this.width = width;
		this.height = height;

		// calculate the base of the line
		calculateBase();

		if (stack == null)
			stack = new IntegerFiFoCircularStack(width);
		else
			stack = new IntegerFiFoCircularStack(width, stack);
	}

	/**
	 * Gets the relative location where the line is drawn in the widget.
	 * 
	 * @return baseOffset
	 */
	public int getBaseOffset() {
		return baseOffset;
	}

	/**
	 * Gets the relative location where the line is drawn in the widget, the
	 * default is <code>BASE_CENTER</code> which is in the middle of the scope.
	 * 
	 * @param baseOffset
	 *            must be between 0 and 0, exceeding values are rounded to the
	 *            closest allowable value.
	 */
	public void setBaseOffset(int baseOffset) {

		if (baseOffset > 100)
			baseOffset = 100;

		if (baseOffset < -100)
			baseOffset = -100;

		this.baseOffset = baseOffset;

		calculateBase();
	}

	private void calculateBase() {
		if (height > 2)
			base = (height * +(100 - getBaseOffset())) / 100;
	}

	protected void widgetDisposed(DisposeEvent e) {
		bg.dispose();
		fg.dispose();
	}

	protected void paintControl(PaintEvent e) {

		if (tailSize <= 0) {
			stack.pop(0);
			return;
		}

		// long start = System.currentTimeMillis();

		// Go calculate the line
		Object[] result = calculate();
		int[] l1 = (int[]) result[0];
		int[] l2 = (int[]) result[1];

		// System.out.print(System.currentTimeMillis() - start + "-");

		// Draw it
		GC gc = e.gc;
		gc.setAdvanced(true);
		gc.setAntialias(SWT.ON);
		gc.setLineWidth(getLineWidth());

		// Fade tail
		if (isFade()) {
			gc.setAlpha(0);
			double fade = 0;
			double fadeOutStep = (double) 125
					/ (double) ((getTailSize() * (getTailFade()) / 100));
			for (int i = 0; i < l1.length - 4;) {
				fade += (fadeOutStep / 2);
				setAlpha(gc, fade);
				gc.drawLine(l1[i], l1[i + 1], l1[i + 2], l1[i + 3]);
				i += 2;
			}

			for (int i = 0; i < l2.length - 4;) {
				fade += (fadeOutStep / 2);
				setAlpha(gc, fade);
				gc.drawLine(l2[i], l2[i + 1], l2[i + 2], l2[i + 3]);
				i += 2;
			}

		} else {
			gc.drawPolyline(l1);
			gc.drawPolyline(l2);
		}

		// Connects the head with the tail
		if (originalTailSize == TAILSIZE_MAX && l1.length > 0 && l2.length > 0
				&& !isFade() && isConnect()) {
			gc.drawLine(l2[l2.length - 2], l2[l2.length - 1], l1[0], l1[1]);
		}

		// System.out.println(System.currentTimeMillis() - start);

	}

	/**
	 * This method calculates the progression of the line.
	 * 
	 * @return
	 */
	private Object[] calculate() {

		int[] line1 = null;
		int[] line2 = null;
		int splitPos = 0;

		for (int progress = 0; progress < getProgression(); progress++) {

			if (stack.isEmpty() && stackListeners != null)
				notifyListeners();

			splitPos = tailSize * 4;

			if (!isSteady())
				cursor++;
			if (cursor >= width)
				cursor = 0;

			// Draw
			int tailIndex = 1;
			line1 = new int[tailSize * 4];
			line2 = new int[tailSize * 4];

			tail[tailSize] = transform(width, height, stack.pop(0));

			for (int i = 0; i < tailSize; i++) {

				int posx = cursor - tailSize + i;
				int pos = i * 4;
				if (posx < 0) {
					posx += width;
					line1[pos] = posx - 1;

					line1[pos + 1] = getBase()
							+ (isSteady() ? 0 : tail[tailIndex - 1]);
					line1[pos + 2] = posx;
					line1[pos + 3] = getBase()
							+ (isSteady() ? 0 : tail[tailIndex]);
				}

				else {
					if (splitPos == tailSize * 4)
						splitPos = pos;
					line2[pos] = posx - 1;
					line2[pos + 1] = getBase() + tail[tailIndex - 1];
					line2[pos + 2] = posx;
					line2[pos + 3] = (getBase() + tail[tailIndex]);
				}
				tail[tailIndex - 1] = tail[tailIndex++];
			}
		}

		int[] l1 = new int[splitPos];
		System.arraycopy(line1, 0, l1, 0, l1.length);
		int[] l2 = new int[(tailSize * 4) - splitPos];
		System.arraycopy(line2, splitPos, l2, 0, l2.length);

		return new Object[] { l1, l2 };
	}

	// private void printTails() {
	// if (originalTailInput != null) {
	// System.out.print("x " + tail[0]);
	// for (int i = 1; i < tail.length; i++) {
	// System.out.print(", " + tail[i]);
	// }
	// System.out.println();
	// System.out.print("o " + originalTailInput[0]);
	// for (int i = 1; i < tail.length; i++) {
	// System.out.print(", " + originalTailInput[i]);
	// }
	// System.out.println();
	// System.out.println("----");
	// System.out.println();
	// }
	// }

	/**
	 * Transforms the value before it is drawn.
	 * 
	 * @param value
	 *            the next value to be processed
	 * @return the transformed value
	 */
	private int transform(int vWidth, int vHeight, int value) {
		if (isPercentage())
			return (((vHeight / 2) * value) / 100);
		return value;
	}

	private int unTransform(int vWidth, int vHeight, int value) {
		if (isPercentage()) {
			if (value > 0)
				value++;
			if (value < 0)
				value--;

			return (value == 0 ? value : (value * 100 / (vHeight / 2)));
		}
		return value;
	}

	/**
	 * @return the base of the line
	 */
	public int getBase() {
		return base;
	}

	/**
	 * @return the number of internal calculation steps at each draw request.
	 * @see #setProgression(int)
	 */
	public int getProgression() {
		return progression;
	}

	/**
	 * The number of internal steps that must be made before drawing. Normally
	 * this will slide the graph one pixel. Setting this to a higher value will
	 * speed up the animation at the cost of a more jerky motion.
	 * 
	 * @param progression
	 */
	public void setProgression(int progression) {
		if (progression > 0)
			this.progression = progression;

	}

	private void notifyListeners() {
		if (stackListeners == null || stackListeners.size() == 0)
			return;
		for (int i = 0; i < stackListeners.size(); i++) {
			((OscilloscopeStackAdapter) stackListeners.get(i)).stackEmpty(this);
		}
	}

	/**
	 * @return boolean, true if the tail and the head of the graph must be
	 *         connected if tail size is {@link #TAILSIZE_MAX} no fading graph.
	 */
	public boolean isConnect() {
		checkWidget();
		return connect;
	}

	/**
	 * Connects head and tail only if tail size is {@link #TAILSIZE_MAX} and no
	 * fading.
	 * 
	 * @param connectHeadAndTail
	 */
	public void setConnect(boolean connectHeadAndTail) {
		checkWidget();
		this.connect = connectHeadAndTail;
	}

	/**
	 * @see #setFade(boolean)
	 * @return boolean fade
	 */
	public boolean isFade() {
		checkWidget();
		return fade;
	}

	/**
	 * Sets fade mode so that a percentage of the tail will be faded out at the
	 * costs of extra CPU utilization (no beauty without pain or as the Dutch
	 * say: "Wie mooi wil gaan moet pijn doorstaan"). The reason for this is
	 * that each pixel must be drawn separately with alpha faded in instead of
	 * the elegant {@link GC#drawPolygon(int[])} routine which does not support
	 * alpha blending.
	 * <p>
	 * In addition to this, set the percentage of tail that must be faded out
	 * {@link #setTailFade(int)}.
	 * 
	 * @param fade
	 *            true or false
	 * @see #setTailFade(int)
	 */
	public void setFade(boolean fade) {
		checkWidget();
		this.fade = fade;
	}

	private void setAlpha(GC gc, double fade) {

		if (gc.getAlpha() == fade)
			return;
		if (fade >= 255)
			gc.setAlpha(255);
		else
			gc.setAlpha((int) fade);
	}

	/**
	 * gets the percentage of tail that must be faded out.
	 * 
	 * @return int percentage
	 * @see #setFade(boolean)
	 */
	public int getTailFade() {
		checkWidget();
		return tailFade;
	}

	/**
	 * @return boolean steady indicator
	 * @see Oscilloscope#setSteady(boolean, int)
	 */
	public boolean isSteady() {
		checkWidget();
		return steady;
	}

	/**
	 * Set a bunch of values that will be drawn. The values will be stored in a
	 * stack and popped once a value is needed. The size of the stack is the
	 * width of the widget. If you resize the widget, the old stack will be
	 * copied into a new stack with the new capacity.
	 * 
	 * @param values
	 */
	public synchronized void setValues(int[] values) {
		checkWidget();

		if (getBounds().width <= 0)
			return;

		if (!super.isVisible())
			return;

		if (stack == null)
			stack = new IntegerFiFoCircularStack(width);

		for (int i = 0; i < values.length; i++) {
			stack.push(values[i]);
		}
	}

	/**
	 * Sets a value to be drawn relative to the middle of the widget. Supply a
	 * positive or negative value.
	 * 
	 * @param value
	 */
	public void setValue(int value) {
		checkWidget();
		if (getBounds().width <= 0)
			return;

		if (!super.isVisible())
			return;

		if (stack.capacity > 0)
			stack.push(value);
	}

	/**
	 * The tail size defaults to TAILSIZE_DEFAULT which is 75% of the width.
	 * Setting it with TAILSIZE_MAX will leave one pixel between the tail and
	 * the head. All values are absolute except TAILSIZE*. If the width is
	 * smaller then the tail size then the tail size will behave like
	 * TAILSIZE_MAX.
	 * 
	 * @param size
	 *            the size of the tail
	 * @see #getTailSize()
	 * @see #TAILSIZE_DEFAULT
	 * @see #TAILSIZE_FILL
	 * @see #TAILSIZE_MAX
	 */
	public void setTailSize(int size) {
		checkWidget();

		if (size == TAILSIZE_FILL && !isSteady())
			size = TAILSIZE_MAX;

		if (originalTailSize != size) {
			tailSizeCheck(size);
			originalTailSize = size;
			setTailSizeInternal();
		}
	}

	private void tailSizeCheck(int size) {
		if (size < -3 || size == 0)
			throw new RuntimeException("Invalid tail size " + size);
	}

	private void setTailSizeInternal() {

		if (originalTailSize == TAILSIZE_DEFAULT) {
			// tail = new int[(width / 4) * 3];
			tailSize = (width / 4) * 3;
			tailSize--;
		} else if (originalTailSize == TAILSIZE_FILL) {
			if (isSteady()) {
				tailSize = originalSteadyPosition - 1;
			} else { // act as if TAILSIZE_MAX
				// tail = new int[width - 2 + 1];
				tailSize = width - 2;
			}
		} else if (originalTailSize == TAILSIZE_MAX || originalTailSize > width) {
			// tail = new int[width - 2 + 1];
			tailSize = width - 2;
		} else if (tailSize != originalTailSize) {
			// tail = new int[originalTailSize + 1];
			tailSize = originalTailSize;
		}

		// Transform the old tail. This is we want to see sort of the same form
		// after resize.
		int[] oldTail = tail;
		if (oldTail == null) {
			tail = new int[tailSize + 1];
		} else {
			tail = new int[tailSize + 1];
			if (tail.length >= oldTail.length) {
				for (int i = 0; i < oldTail.length; i++) {

					tail[i] = transform(
							width,
							height,
							unTransform(widthBeforeResize, heightBeforeResize,
									oldTail[i]));
				}
			} else {
				for (int i = 0; i < tail.length; i++) {
					tail[i] = transform(
							width,
							height,
							unTransform(widthBeforeResize, heightBeforeResize,
									oldTail[i]));
				}
			}
		}
	}

	public Point computeSize(int wHint, int hHint, boolean changed) {
		checkWidget();

		int width;
		int height;

		if (wHint != SWT.DEFAULT)
			width = wHint;
		else
			width = DEFAULT_WIDTH;

		if (hHint != SWT.DEFAULT)
			height = hHint;
		else
			height = DEFAULT_HEIGHT;

		return new Point(width + 2, height + 2);
	}

	public boolean needsRedraw() {
		checkWidget();
		return isDisposed() ? false : true;
	}

	/**
	 * Sets the line width. A value equal or below zero is ignored. The default
	 * width is 1.
	 * 
	 * @param lineWidth
	 */
	public void setLineWidth(int lineWidth) {
		checkWidget();
		if (lineWidth > 0)
			this.lineWidth = lineWidth;
	}

	/**
	 * @return int, the width of the line.
	 * @see #setLineWidth(int)
	 */
	public int getLineWidth() {
		checkWidget();
		return lineWidth;
	}

	/**
	 * If set to true then the values are treated as percentages of the
	 * available space rather than absolute values. This will scale the
	 * amplitudes if the control is resized. Default is false.
	 * 
	 * @param percentage
	 *            true if percentages
	 */
	public void setPercentage(boolean percentage) {
		checkWidget();
		this.percentage = percentage;
	}

	/**
	 * @return boolean
	 * @see #setPercentage(boolean)
	 */
	public boolean isPercentage() {
		checkWidget();
		return percentage;
	}

	/**
	 * If steady is true the graph will draw on a steady position instead of
	 * advancing.
	 * 
	 * @param steady
	 * @param steadyPosition
	 */
	public void setSteady(boolean steady, int steadyPosition) {
		checkWidget();
		this.steady = steady;
		this.originalSteadyPosition = steadyPosition;
		if (steady)
			if (steadyPosition == STEADYPOSITION_75PERCENT)
				this.cursor = (int) ((double) width * (double) 0.75);
			else if (steadyPosition > 0 && steadyPosition < width)
				this.cursor = steadyPosition;
		// setTailSizeInternal();
	}

	/**
	 * Sets the percentage of tail that must be faded out. If you supply 100
	 * then the tail is faded out all the way to the top. The effect will become
	 * increasingly less obvious.
	 * 
	 * @param tailFade
	 */
	public void setTailFade(int tailFade) {
		checkWidget();
		if (tailFade > 100)
			tailFade = 100;
		if (tailFade < 1)
			tailFade = 1;
		this.tailFade = tailFade;
	}

	/**
	 * Adds a new stack listener to the collection of stack listeners. Adding
	 * the same listener twice will have no effect.
	 * 
	 * @param listener
	 */
	public synchronized void addStackListener(OscilloscopeStackAdapter listener) {
		checkWidget();
		if (stackListeners == null)
			stackListeners = new ArrayList<OscilloscopeStackAdapter>();
		if (!stackListeners.contains(listener))
			stackListeners.add(listener);
	}

	/**
	 * Removes a stack listener from the collection of stack listeners.
	 * 
	 * @param listener
	 */
	public void removeStackListener(OscilloscopeStackAdapter listener) {
		checkWidget();
		if (stackListeners != null) {
			stackListeners.remove(listener);
			if (stackListeners.size() == 0)
				synchronized (stackListeners) {
					stackListeners = null;
				}
		}
	}
}
