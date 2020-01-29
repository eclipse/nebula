/*******************************************************************************
 * Copyright (c) 2006 Chris Gross.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: schtoo@schtoo.com(Chris Gross) - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.pgroup;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

/**
 * AbstractGroupStrategy is a convenient starting point for all
 * IGroupStrategy's.
 * <P>
 * The AbstractGroupStrategy handles most behavior for you. All that is required
 * of extending classes, is to implement painting and sizing.
 *
 * @author chris
 */
public abstract class AbstractGroupStrategy
{

    private PGroup group;

    public AbstractGroupStrategy(PGroup g) {
    	group = g;
    }
    
    /**
     * 
     */
    public void initialize()
    {
        update();
    }

    /**
     * @param x
     * @param y
     * @return
     */
    public boolean isToggleLocation(int x, int y)
    {
        if (getGroup().getToggleRenderer() != null)
        {
            Rectangle r = new Rectangle(getGroup().getToggleRenderer().getBounds().x, getGroup()
                .getToggleRenderer().getBounds().y,
                                        getGroup().getToggleRenderer().getBounds().width,
                                        getGroup().getToggleRenderer().getBounds().height);
            if (r.contains(x, y))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * @return the area where toolitems can be drawn
     */
    public Rectangle getToolItemArea() {
    	return null;
    }

    /**
     * Paints the actual group widget. This method is to be implemented by
     * extending classes.
     *
     * @param gc
     */
    public abstract void paint(GC gc);

    public abstract void dispose();

    /**
     * @return Returns the PGroup.
     */
    public PGroup getGroup()
    {
        return group;
    }

    public abstract Rectangle computeTrim(int x, int y, int width, int height);

    public abstract Rectangle getClientArea();

    public abstract void update();
}
