package org.eclipse.swt.nebula.widgets.compositetable;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

/**
 * ResizableGridRowLayout works with ResizableGridHeaderLayout to implement
 * column resizing semantics for CompositeTable UIs.
 * <p>
 * Use a ResizableGridRowLayout when you have used a ResizableGridHeaderLayout
 * on the Header object.  ResizableGridRowLayout gets all of its layout settings
 * from the ResizableGridHeaderLayout object, so there is no need to set any 
 * additional layout information on the ResizableGridRowLayout itself.
 *
 * @author djo
 */
public class ResizableGridRowLayout extends CompositeTableLayout {
    
    private ResizableGridHeaderLayout header;
    private GridRowLayout nullLayout = null;
    
    protected Point computeSize(Composite composite, int wHint, int hHint,
            boolean flushCache) {
        return getLayoutDelegate(composite).computeSize(composite, wHint, hHint, flushCache);
    }
    
    protected void layout(Composite composite, boolean flushCache) {
        getLayoutDelegate(composite).layout(composite, flushCache);
    }
    
    private CompositeTableLayout getLayoutDelegate(Composite composite) {
        findHeader(composite);
        if (header != null) {
            return header;
        }
        createNullLayout(composite);
        return nullLayout;
    }

    private void createNullLayout(Composite composite) {
        if (nullLayout == null) {
            int numChildren = composite.getChildren().length;
            nullLayout = new GridRowLayout(new int[numChildren], true);
        }
    }
    
    private void findHeader(Composite row) {
        if (header != null) {
            return;
        }
        Control[] children = row.getParent().getChildren();
        for (int i = 0; i < children.length; i++) {
            if (children[i] instanceof Composite) {
                Composite child = (Composite) children[i];
                Layout layout = child.getLayout();
                
                if (layout instanceof ResizableGridHeaderLayout) {
                    header = (ResizableGridHeaderLayout) layout;
                    return;
                }
            }
        }
    }
}
