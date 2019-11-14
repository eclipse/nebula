/*******************************************************************************
 *  Copyright (c) 2010 Weltevree Beheer BV, Remain Software & Industrial-TSI
 * 
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Wim S. Jongman - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.oscilloscope;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.nebula.widgets.oscilloscope.multichannel.OscilloscopeStackAdapter;
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
 * @deprecated use
 *             {@link org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope}
 * 
 * @author Wim.Jongman (@remainsoftware.com)
 * 
 */
public class Oscilloscope extends
		org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope {

	/**
	 * The default delay in milliseconds before the dispatcher is asked to
	 * redraw.
	 */
	public static final int DEFAULT_DELAY = 30;

	/**
	 * The default number of ticks before a new value is asked.
	 */
	public static final int PULSE_DEFAULT = 40;

	/**
	 * @deprecated use
	 *             {@link org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope#HEARTBEAT}
	 */
	public static final int[] HEARTBEAT = org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope.HEARTBEAT;

	/**
	 * The default constructor.
	 * 
	 * @param parent
	 * @param style
	 * @deprecated use
	 *             {@link org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope}
	 */
	public Oscilloscope(Composite parent, int style) {
		super(parent, style);
	}

	// Used to map the new listener to the old listener.
	private Map<org.eclipse.nebula.widgets.oscilloscope.OscilloscopeStackAdapter, OscilloscopeStackAdapter> listeners;

	private int scope = 0;

	/**
	 * @deprecated use
	 *             {@link org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope}
	 */
	public int getTailSize() {
		return super.getTailSize(scope);
	}

	/**
	 * @deprecated use
	 *             {@link org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope}
	 */
	public int getBaseOffset() {
		return super.getBaseOffset(scope);
	}

	/**
	 * @deprecated use
	 *             {@link org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope}
	 */
	public void setBaseOffset(int baseOffset) {
		super.setBaseOffset(scope, baseOffset);
	}

	/**
	 * @deprecated use
	 *             {@link org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope}
	 */
	public int getBase() {
		return super.getBase(scope);
	}

	/**
	 * @deprecated use
	 *             {@link org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope}
	 */
	public int getProgression() {
		return super.getProgression(scope);
	}

	/**
	 * @deprecated use
	 *             {@link org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope}
	 */
	public void setProgression(int progression) {
		super.setProgression(scope, progression);
	}

	/**
	 * @deprecated use
	 *             {@link org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope}
	 */
	public boolean isConnect() {
		return super.isConnect(scope);
	}

	/**
	 * @deprecated use
	 *             {@link org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope}
	 */
	public void setConnect(boolean connectHeadAndTail) {
		super.setConnect(scope, connectHeadAndTail);
	}

	/**
	 * @deprecated use
	 *             {@link org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope}
	 */
	public boolean isFade() {
		return super.isFade(scope);
	}

	/**
	 * @deprecated use
	 *             {@link org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope}
	 */
	public void setFade(boolean fade) {
		super.setFade(scope, fade);
	}

	/**
	 * @deprecated use
	 *             {@link org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope}
	 */
	public int getTailFade() {
		return super.getTailFade(scope);
	}

	/**
	 * @deprecated use
	 *             {@link org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope}
	 */
	public boolean isSteady() {
		return super.isSteady(scope);
	}

	/**
	 * @deprecated use
	 *             {@link org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope}
	 */
	public synchronized void setValues(int[] values) {
		super.setValues(scope, values);
	}

	/**
	 * @deprecated use
	 *             {@link org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope}
	 */
	public void setValue(int value) {
		super.setValue(scope, value);
	}

	/**
	 * @deprecated use
	 *             {@link org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope}
	 */
	public void setTailSize(int size) {
		super.setTailSize(scope, size);
	}

	/**
	 * @deprecated use
	 *             {@link org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope}
	 */
	public void setLineWidth(int lineWidth) {
		super.setLineWidth(scope, lineWidth);
	}

	/**
	 * @deprecated use
	 *             {@link org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope}
	 */
	public int getLineWidth() {
		return super.getLineWidth(scope);
	}

	/**
	 * @deprecated use
	 *             {@link org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope}
	 */
	public void setPercentage(boolean percentage) {
		super.setPercentage(scope, percentage);
	}

	/**
	 * @deprecated use
	 *             {@link org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope}
	 */
	public boolean isPercentage() {
		return super.isPercentage(scope);
	}

	/**
	 * @deprecated use
	 *             {@link org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope}
	 */
	public void setSteady(boolean steady, int steadyPosition) {
		super.setSteady(scope, steady, steadyPosition);
	}

	/**
	 * @deprecated use
	 *             {@link org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope}
	 */
	public void setTailFade(int tailFade) {
		super.setTailFade(scope, tailFade);
	}

	/**
	 * This method keeps its own record of listeners but also delegates to the
	 * instance of the superclass.
	 * 
	 * @param listener
	 * @deprecated use
	 *             {@link org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope}
	 */
	public synchronized void addStackListener(
			final org.eclipse.nebula.widgets.oscilloscope.OscilloscopeStackAdapter listener) {

		if (listeners == null) {
			listeners = new HashMap<org.eclipse.nebula.widgets.oscilloscope.OscilloscopeStackAdapter, OscilloscopeStackAdapter>();
		}

		OscilloscopeStackAdapter adapter = listeners.get(listener);
		if (adapter == null) {

			adapter = new OscilloscopeStackAdapter() {
				@Override
				public void stackEmpty(
						org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope scope,
						int channel) {
					listener.stackEmpty(Oscilloscope.this);
				}
			};

			listeners.put(listener, adapter);
		}

		super.addStackListener(scope, adapter);
	}

	/**
	 * This method removes our own copy of the stack listeners.
	 * 
	 * @param listener
	 * 
	 * @deprecated use
	 *             {@link org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope}
	 */
	public void removeStackListener(
			org.eclipse.nebula.widgets.oscilloscope.OscilloscopeStackAdapter listener) {
		checkWidget();
		if (listeners != null) {
			OscilloscopeStackAdapter adapter = listeners.get(listener);
			if (adapter != null) {
				super.removeStackListener(scope, adapter);
			}
			listeners.remove(listener);
			if (listeners.size() == 0) {
				synchronized (listeners) {
					listeners = null;
				}
			}
		}
	}
}
