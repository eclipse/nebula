/*******************************************************************************
 * Copyright (c) 2015, Alex Clayton <Alex_Clayton_2000@yahoo.com>.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package org.eclipse.nebula.visualization.xygraph.figures;

/**
 * Factory for producing the primary {@link Axis} in an {@link XYGraph}
 * 
 * @author Alex Clayton
 *
 */
public interface IAxesFactory {

	/**
	 * Creates the primary x axis for the {@link XYGraph}
	 * 
	 * @return The primary x axis for the {@link XYGraph}, should not be
	 *         {@code null}
	 */
	public Axis createXAxis();

	/**
	 * Creates the primary y axis for the {@link XYGraph}
	 * 
	 * @return The primary y axis for the {@link XYGraph}, should not be
	 *         {@code null}
	 */
	public Axis createYAxis();

}
