/*******************************************************************************
 * Copyright (c) 2012, 2017 Diamond Light Source Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.eclipse.nebula.visualization.xygraph.linearscale;

/**
 * A Tick Mark object
 *
 */
public class Tick {

	private String text;
	private double value;
	private double position;
	private int tPosition;

	/**
	 * @param tickText
	 */
	public void setText(String tickText) {
		text = tickText;
	}

	/**
	 * @return the tick text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param tickValue
	 */
	public void setValue(double tickValue) {
		value = tickValue;
	}

	/**
	 * @return the tick value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @param tickPosition
	 *            in pixels
	 */
	public void setPosition(double tickPosition) {
		position = tickPosition;
	}

	/**
	 * @return the tick position in pixels
	 */
	public double getPosition() {
		return position;
	}

	/**
	 * @param textPosition
	 *            in pixels
	 */
	public void setTextPosition(int textPosition) {
		tPosition = textPosition;
	}

	/**
	 * @return the text position in pixels
	 */
	public int getTextPosition() {
		return tPosition;
	}

	@Override
	public String toString() {
		return text + " (" + value + ", " + position + ", " + tPosition + ")";
	}
}
