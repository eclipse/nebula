/*******************************************************************************
 * Copyright (c) 2015, Alex Clayton <Alex_Clayton_2000@yahoo.com>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
     * @return The primary x axis for the {@link XYGraph}, should
     * not be {@code null}
     */
    public Axis createXAxis();
    
    /**
     * Creates the primary y axis for the {@link XYGraph}
     * @return The primary y axis for the {@link XYGraph}, should
     * not be {@code null}
     */
    public Axis createYAxis();
    
}
