/*******************************************************************************
 *  Copyright (c) 2010, 2012 Weltevree Beheer BV, Remain Software & Industrial-TSI
 * 
 * All rights reserved. 
 * This program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Wim S. Jongman - initial API and implementation
 *   Jantje- split oscilloscope in plotter and oscilloscope
 ******************************************************************************/
package org.eclipse.nebula.widgets.oscilloscope.multichannel;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Point;
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
public class Oscilloscope extends Plotter {
	private class Data {

		private OscilloscopeDispatcher dispatcher;
		private ArrayList<OscilloscopeStackAdapter> stackListeners;
		private IntegerFiFoCircularStack stack;
		private int progression = PROGRESSION_DEFAULT;
	}

	/**
	 * This method can be called outside of the UI thread.
	 * 
	 * @return the number of internal calculation steps at each draw request.
	 * @see #setProgression(int)
	 */
	public int getProgression(int channel) {
		return chan[channel].progression;
	}

	/**
	 * The number of internal steps that must be made before drawing. Normally
	 * this will slide the graph one pixel. Setting this to a higher value will
	 * speed up the animation at the cost of a more jerky motion.
	 * <p/>
	 * This method can be called outside of the UI thread.
	 * 
	 * @param progression
	 */
	public void setProgression(int channel, int progression) {
		if (progression > 0) {
			chan[channel].progression = progression;
		}

	}

	/**
	 * The stack can hold a limited number of values but will never overflow.
	 * Think of the stack as a tube with a given length. If you push too many
	 * values in, the oldest will be pushed out at the end.
	 * 
	 */
	public class IntegerFiFoCircularStack {
		private int bottom;
		private final int capacity;
		private final int[] stack;
		private int storedValues;
		private int top;

		/**
		 * Creates a stack with the indicated capacity.
		 * 
		 * @param capacity
		 *            must be greater than 1
		 */
		public IntegerFiFoCircularStack(int capacity) {
			if (capacity <= 1) {
				throw new RuntimeException("Stack capacity must be > 1");
			}
			this.capacity = capacity;
			stack = new int[capacity];
			top = 0;
			bottom = 0;
		}

		/**
		 * Creates stack with the indicated capacity and copies the old stack
		 * into the new stack and the old stack will be empty after this action.
		 * 
		 * @param capacity
		 *            must be greater than 1
		 * @param oldStack
		 */
		public IntegerFiFoCircularStack(int capacity, IntegerFiFoCircularStack oldStack) {
			this(capacity);
			while (!oldStack.isEmpty()) {
				push(oldStack.pop(0));
			}
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

		public int getCapacity() {
			return capacity;
		}

		/**
		 * 
		 * @return boolean
		 */
		public int getLoad() {
			return storedValues;
		}

		/**
		 * 
		 * @return boolean
		 */
		public boolean isEmpty() {
			return storedValues == 0;
		}

		/**
		 * 
		 * @return boolean
		 */
		public boolean isFull() {
			return storedValues == capacity;
		}

		/**
		 * Returns the oldest value from the stack without removing the value
		 * from the stack. Returns the supplied entry if the stack is empty.
		 * 
		 * @param valueIfEmpty
		 * @return int
		 */
		public int peek(int valueIfEmpty) {
			if (storedValues > 0) {
				return stack[bottom];
			}
			return valueIfEmpty;
		}

		/**
		 * Returns the oldest value from the stack. Returns the supplied entry
		 * if the stack is empty.
		 * 
		 * @param valueIfEmpty
		 * @return int
		 */
		public int pop(int valueIfEmpty) {
			if (isEmpty()) {
				return valueIfEmpty;
			}

			storedValues--;
			int result = stack[bottom++];

			if (bottom == capacity) {
				bottom = 0;
			}

			return result;
		}

		/**
		 * Returns the oldest value from the stack and negates the value.
		 * Returns the supplied entry if the stack is empty.
		 * 
		 * @param valueIfEmpty
		 * @return int
		 */
		public int popNegate(int valueIfEmpty) {
			return pop(valueIfEmpty) * -1;
		}

		/**
		 * Puts a value on the stack.
		 * 
		 * @param value
		 */
		public void push(int value) {
			if (storedValues == capacity) {
				top = bottom;
				bottom++;
				if (bottom == capacity) {
					bottom = 0;
				}
			} else {
				storedValues++;
			}

			if (top == capacity) {
				top = 0;
			}

			stack[top++] = value;
		}
	}

	/**
	 * This set of values will draw a figure that is similar to the heart beat
	 * that you see on hospital monitors.
	 */
	public static final int[] HEARTBEAT = new int[] { 2, 10, 2, -16, 16, 44, 49, 44, 32, 14, -16, -38, -49, -47, -32,
			-10, 8, 6, 6, -2, 6, 4, 2, 0, 0, 6, 8, 6 };

	private Data[] chan;

	private int width;

	/**
	 * Creates a scope with one channel.
	 * 
	 * @param parent
	 * @param style
	 */
	public Oscilloscope(Composite parent, int style) {
		this(1, null, parent, style);
	}

	/**
	 * Creates a scope with <code>channels</code> channels.
	 * 
	 * @param channels
	 * @param parent
	 * @param style
	 */
	public Oscilloscope(int channels, Composite parent, int style) {
		this(channels, null, parent, style);
	}

	/**
	 * Creates a new scope with <code>channels</code> channels and adds attaches
	 * it to the supplied <code>dispatcher</code>.
	 * 
	 * @param channels
	 * @param dispatcher
	 *            may be null
	 * @param parent
	 * @param style
	 */
	public Oscilloscope(int channels, OscilloscopeDispatcher dispatcher, Composite parent, int style) {
		super(channels, parent, style);

		chan = new Data[channels];

		for (int i = 0; i < chan.length; i++) {
			chan[i] = new Data();
			if (dispatcher == null) {
				chan[i].dispatcher = new OscilloscopeDispatcher(i, this);
			} else {
				chan[i].dispatcher = dispatcher;
				dispatcher.setOscilloscope(this);
			}

		}

	}

	/**
	 * Adds a new stack listener to the collection of stack listeners. Adding
	 * the same listener twice will have no effect.
	 * <p/>
	 * This method can be called outside of the UI thread.
	 * 
	 * @param listener
	 */
	public synchronized void addStackListener(int channel, OscilloscopeStackAdapter listener) {
		if (chan[channel].stackListeners == null) {
			chan[channel].stackListeners = new ArrayList<OscilloscopeStackAdapter>();
		}
		if (!chan[channel].stackListeners.contains(listener)) {
			chan[channel].stackListeners.add(listener);
		}
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		checkWidget();

		int width;
		int height;

		if (wHint != SWT.DEFAULT) {
			width = wHint;
		} else {
			width = DEFAULT_WIDTH;
		}

		if (hHint != SWT.DEFAULT) {
			height = hHint;
		} else {
			height = DEFAULT_HEIGHT;
		}

		return new Point(width + 2, height + 2);
	}

	/**
	 * This method can be called outside of the UI thread.
	 * 
	 * @param channel
	 * @return the dispatcher associated with this channel.
	 */
	public OscilloscopeDispatcher getDispatcher(int channel) {
		return chan[channel].dispatcher;
	}

	public boolean needsRedraw() {
		checkWidget();
		return isDisposed() ? false : true;
	}

	private void notifyListeners(int channel) {
		if (chan[channel].stackListeners == null || chan[channel].stackListeners.size() == 0) {
			return;
		}
		for (int i = 0; i < chan[channel].stackListeners.size(); i++) {
			chan[channel].stackListeners.get(i).stackEmpty(this, channel);
		}
	}

	protected void paintControl(PaintEvent e) {
		super.paintControl(e);
		for (int c = 0; c < chan.length; c++) {
			for (int progress = 0; progress < getProgression(c); progress++) {

				if (chan[c].stack.isEmpty() && chan[c].stackListeners != null) {
					notifyListeners(c);
				}
				setValue(c, chan[c].stack.popNegate(0));
			}
			// I think the next code can be removed
			// if (getTailSize(c) <= 0) {
			// chan[c].stack.popNegate(0);
			// continue;
			// }
		}
	}

	/**
	 * Removes a stack listener from the collection of stack listeners. This
	 * method can be called outside of the UI thread.
	 * 
	 * @param listener
	 */
	public void removeStackListener(int channel, OscilloscopeStackAdapter listener) {
		if (chan[channel].stackListeners != null) {
			chan[channel].stackListeners.remove(listener);
			if (chan[channel].stackListeners.size() == 0) {
				synchronized (chan[channel].stackListeners) {
					chan[channel].stackListeners = null;
				}
			}
		}
	}

	/**
	 * Sets the dispatcher that is associated with the supplied channel. This
	 * method can be called outside of the UI thread.
	 * 
	 * @param channel
	 * @param dispatcher
	 */
	public void setDispatcher(int channel, OscilloscopeDispatcher dispatcher) {
		chan[channel].dispatcher = dispatcher;
	}

	/**
	 * Sets a bunch of values that will be drawn. See
	 * {@link #setValue(int, int)} for details.
	 * <p/>
	 * This method can be called outside of the UI thread.
	 * 
	 * @param channel
	 * @param values
	 * 
	 * @see #setValue(int, int)
	 */
	public synchronized void setValues(int channel, int[] values) {
		checkWidget();

		if (getBounds().width <= 0)
			return;

		if (!super.isVisible())
			return;

		if (this.chan[channel].stack == null)
			this.chan[channel].stack = new IntegerFiFoCircularStack(width);

		for (int i = 0; i < values.length; i++) {
			this.chan[channel].stack.push(values[i]);
		}
	}

	protected void controlResized(ControlEvent e) {
		super.controlResized(e);
		width = getSize().x;

		if (width > 1) {
			for (int c = 0; c < this.chan.length; c++) {
				if (this.chan[c].stack == null) {
					this.chan[c].stack = new IntegerFiFoCircularStack(width);
				} else {
					this.chan[c].stack = new IntegerFiFoCircularStack(width, this.chan[c].stack);
				}
			}
		}
	}
}
