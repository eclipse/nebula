/*******************************************************************************
 * Copyright (c) 2006-2009 Nicolas Richeton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors :
 *    Nicolas Richeton (nicolas.richeton@gmail.com) - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.cwt.animation;

import org.eclipse.nebula.cwt.animation.effects.IEffect;
import org.eclipse.nebula.cwt.animation.effects.MoveScrollBar;
import org.eclipse.nebula.cwt.animation.movement.IMovement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Scrollable;

/**
 * <p>
 * This class replace the default scrolling behavior by an animation effect.
 * </p>
 * 
 * <p>
 * Compatible with :
 * </p>
 * <ul>
 * <li>Shell</li>
 * <li>StyledText</li>
 * <li>Canvas</li>
 * <li>Gallery</li>
 * </ul>
 * 
 * @author Nicolas Richeton
 * 
 */
public class ScrollingSmoother {

	Scrollable component;

	ScrollBar verticalScrollBar;

	ScrollBar horizontalScrollBar;

	MoveScrollBar me = null;

	IMovement movement = null;

	/**
	 * @param c2
	 * @param movement
	 */
	public ScrollingSmoother(final Scrollable c2, IMovement movement) {
		this.component = c2;
		verticalScrollBar = c2.getVerticalBar();
		horizontalScrollBar = c2.getHorizontalBar();
		this.movement = movement;
	}

	protected ScrollBar getScrollbar(Event event) {
		ScrollBar result = verticalScrollBar;

		if (result == null) {
			result = horizontalScrollBar;
		}

		return result;
	}

	Listener mouseWheelListener = new Listener() {

		public void handleEvent(Event event) {
			// Remove standard behavior
			event.doit = false;

			// Get scrollbar on which the event occurred.
			ScrollBar currentScrollBar = getScrollbar(event);

			int start = currentScrollBar.getSelection();
			int end = start;

			// If an effect is currently running, get the current and target
			// values.
			if (me != null) {
				start = me.getCurrent();
				end = me.getEnd();
			}

			end -= event.count * currentScrollBar.getIncrement();

			if (end > currentScrollBar.getMaximum()
					- currentScrollBar.getThumb()) {
				end = currentScrollBar.getMaximum()
						- currentScrollBar.getThumb();
			}

			if (end < currentScrollBar.getMinimum()) {
				end = currentScrollBar.getMinimum();
			}

			startEffect(new MoveScrollBar(currentScrollBar, start, end,
					2000, movement, null, null));

		}
	};

	/**
	 * Enable or disable scrolling efeect.
	 * 
	 * @param enable
	 *            true or false.
	 */
	public void smoothControl(boolean enable) {
		if (enable) {
			component.addListener(SWT.MouseWheel, mouseWheelListener);

			if (verticalScrollBar != null)
				verticalScrollBar
						.addSelectionListener(cancelEffectIfUserSelection);

			if (horizontalScrollBar != null)
				horizontalScrollBar
						.addSelectionListener(cancelEffectIfUserSelection);

		} else {
			component.removeListener(SWT.MouseWheel, mouseWheelListener);

			if (verticalScrollBar != null)
				verticalScrollBar
						.removeSelectionListener(cancelEffectIfUserSelection);

			if (horizontalScrollBar != null)
				horizontalScrollBar
						.removeSelectionListener(cancelEffectIfUserSelection);

		}
	}

	SelectionListener cancelEffectIfUserSelection = new SelectionListener() {
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}

		public void widgetSelected(SelectionEvent e) {
			if (!(e.data instanceof MoveScrollBar))
				me = null;

		}
	};

	protected void startEffect(MoveScrollBar moveScrollBarEffect) {
		IEffect oldEffect = me;
		me = moveScrollBarEffect;

		if (oldEffect == null) {
			Display.getCurrent().syncExec(new Runnable() {

				public void run() {
					if (me != null && !me.isDone()) {
						me.doEffect();
						Display.getCurrent().timerExec(10, this);
					} else {
						me = null;
					}
				}

			});
		}

	}

}
