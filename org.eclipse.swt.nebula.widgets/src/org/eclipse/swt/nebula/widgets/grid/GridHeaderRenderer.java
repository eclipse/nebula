package org.eclipse.swt.nebula.widgets.grid;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public abstract class GridHeaderRenderer extends AbstractInternalWidget
{
    /**
     * Returns the bounds of the text in the header.  This is used when displaying in-place tooltips.
     * If <code>null</code> is returned here, in-place tooltips will not be displayed.
     * 
     * @param item item to calculate text bounds.
     * @return bounds of the text.
     */
    public Rectangle getTextBounds(GridItem item)
    {
        return null;
    }
    
    /**
     * Returns true if the given text area would be truncated within the current bounds.
     * 
     * @param textBounds the bounds of text
     * @return true if the text would be truncated, false otherwise.
     */
    public boolean isTruncated(Rectangle textBounds)
    {
        return false;
    }
}
