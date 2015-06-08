/*******************************************************************************
 * Copyright (c) 2015, Alex Clayton <Alex_Clayton_2000@yahoo.com>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.eclipse.nebula.visualization.xygraph.figures;

import org.eclipse.nebula.visualization.xygraph.linearscale.AbstractScale.LabelSide;
import org.eclipse.nebula.visualization.xygraph.linearscale.LinearScale.Orientation;

/**
 * Default {@link IAxesFactory} just produces standard
 * {@link Axis}
 * 
 * @author Alex Clayton
 *
 */
public class DefaultAxesFactory implements IAxesFactory {

    @Override
    public Axis createXAxis() {
        Axis newAxis = new Axis("X-Axis", false);
        newAxis.setOrientation(Orientation.HORIZONTAL);
        newAxis.setTickLableSide(LabelSide.Primary);
        return newAxis;
    }

    @Override
    public Axis createYAxis() {
        Axis newAxis = new Axis("Y-Axis", true);
        newAxis.setOrientation(Orientation.VERTICAL);
        newAxis.setTickLableSide(LabelSide.Primary);
        newAxis.setAutoScaleThreshold(0.1);
        return newAxis;
    }

}
