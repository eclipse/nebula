package org.eclipse.swt.nebula.widgets.compositetable;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

/**
 * (non-API) An abstract superclass for all layout managers in the 
 * CompositeTable package.
 *
 * @author djo
 */
abstract class CompositeTableLayout extends Layout {
    abstract protected Point computeSize(Composite composite, int wHint, 
            int hHint, boolean flushCache);
    
    abstract protected void layout(Composite composite, boolean flushCache);
}
