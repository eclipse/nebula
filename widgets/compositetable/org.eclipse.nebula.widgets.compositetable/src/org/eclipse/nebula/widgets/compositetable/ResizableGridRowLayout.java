package org.eclipse.nebula.widgets.compositetable;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Widget;

/**
 * ResizableGridRowLayout works with HeaderLayout to implement column resizing
 * semantics for CompositeTable UIs.
 * <p>
 * Use a ResizableGridRowLayout when you have used a ResizableGridHeaderLayout
 * on the Header object. ResizableGridRowLayout gets all of its layout settings
 * from the ResizableGridHeaderLayout object, so there is no need to set any
 * additional layout information on the ResizableGridRowLayout itself.
 * 
 * @author djo
 */
public class ResizableGridRowLayout extends GridRowLayout {
    
    private AbstractGridRowLayout delegate = null;
    private int[] columnOrder;
    private Composite control;
    
    /**
     * Constructor ResizableGridRowLayout.  Create a ResizableGridRowLayout
     * object.  Since a ResizableGridRowLayout object will automatically find
     * the associated HeaderLayout, no properties need to be set on a 
     * ResizableGridRowLayout.
     */
    public ResizableGridRowLayout() {}
    
    protected Point computeSize(Composite composite, int wHint, int hHint,
            boolean flushCache) {
        this.control = composite;
        getLayoutDelegate(composite);
        return super.computeSize(composite, wHint, hHint, flushCache);
    }
    
    protected void layout(Composite composite, boolean flushCache) {
        this.control = composite;
        getLayoutDelegate(composite);
        super.layout(composite, flushCache);
    }
    
    public int[] getWeights() {
        if (delegate == null) {
            return super.getWeights();
        }
        return delegate.getWeights();
    }
    
    public AbstractGridRowLayout setWeights(int[] weights) {
        if (delegate == null) {
            return super.setWeights(weights);
        }
        return delegate.setWeights(weights);
    }
    
    public int getSumOfAllWeights() {
        if (delegate == null) {
            return super.getSumOfAllWeights();
        }
        return delegate.getSumOfAllWeights();
    }
    
    public boolean isFittingHorizontally() {
        if (delegate == null) {
            return super.isFittingHorizontally();
        }
        return delegate.isFittingHorizontally();
    }
    
    public AbstractGridRowLayout setFittingHorizontally(boolean fittingHorizontally) {
        if (delegate == null) {
            return super.setFittingHorizontally(fittingHorizontally);
        }
        return delegate.setFittingHorizontally(fittingHorizontally);
    }
    
    protected Widget getColumnAt(Composite rowOrHeader, int offset) {
        if (columnOrder == null) {
            return super.getColumnAt(rowOrHeader, offset);
        }
        Control[] children = rowOrHeader.getChildren();
        return children[columnOrder[offset]];
    }
    
    private CompositeTableLayout getLayoutDelegate(Composite composite) {
        if (delegate != null) return delegate;
        
        findHeader(composite);
        if (delegate != null) {
            return delegate;
        }
        createNullLayout(composite);
        return delegate;
    }

    private void createNullLayout(Composite composite) {
        int numChildren = composite.getChildren().length;
        delegate = new GridRowLayout(new int[numChildren], true);
    }
    
    private void findHeader(Composite row) {
        Control[] children = row.getParent().getChildren();
        for (int i = 0; i < children.length; i++) {
            if (children[i] instanceof Composite) {
                Composite child = (Composite) children[i];
                Layout layout = child.getLayout();
                
                if (layout instanceof HeaderLayout) {
                    delegate = (HeaderLayout) layout;
                    addListenersToDelegate(row, (HeaderLayout) layout);
                    return;
                }
            }
        }
    }

    private void addListenersToDelegate(Composite row, HeaderLayout delegate) {
        delegate.addColumnControlListener(new GridColumnControlListener(row));
    }
    
    private class GridColumnControlListener extends ColumnControlListener {
        private final Composite row;
        
        private int savedResizedColNum;
        private int savedResizedColWidth;
        private int savedColToRightOfResizedColWidth;
        boolean runnableQueueIsClear = true;

        public GridColumnControlListener(Composite row) {
            this.row = row;
        }
        public void columnMoved(int[] newColumnOrder) {
            ResizableGridRowLayout.this.columnOrder = newColumnOrder;
            control.layout(true);
        }
        public void columnResized(int resizedColumnPosition, 
                int resizedColumnWidth, 
                int columnToTheRightOfResizedColumnWidth) 
        {
            /*
             * FEATURE IN WINDOWS: If we resize the row immediately, Windows
             * won't allow the row to repaint, resulting in cheese (ugly
             * draw artifacts) all over the place until the user releases the
             * mouse button. The workaround is to resize the row columns in the
             * next idle event during an async runnable. Since this code will
             * run on every mouseMove event, we are careful to queue at most one
             * async runnable at a time.
             */
            this.savedResizedColNum = resizedColumnPosition;
            this.savedResizedColWidth = resizedColumnWidth;
            this.savedColToRightOfResizedColWidth = columnToTheRightOfResizedColumnWidth;
            
            if (runnableQueueIsClear) {
                runnableQueueIsClear = false;
                row.getDisplay().asyncExec(new Runnable() {
                    public void run() {
                        if (row.isDisposed()) return;
                        
                        Control[] children = row.getChildren();
                        
                        Control resizedColumn = (Control) getColumnAt(row, savedResizedColNum);
                        // Control resizedColumn = children[savedResizedColNum];
                        Point resizedColumnSize = resizedColumn.getSize();
                        int adjustedResizedColumnWidth = savedResizedColWidth - 2 * 
                            CELL_BORDER_WIDTH;
                        int resizedColumnWidthChange = adjustedResizedColumnWidth
                                - resizedColumnSize.x;
                        resizedColumn.setSize(adjustedResizedColumnWidth,
                                resizedColumnSize.y);
                        Control columnToTheRightOfResizedColumn = (Control) getColumnAt(row, savedResizedColNum+1);
                        // Control columnToTheRightOfResizedColumn = children[savedResizedColNum + 1];
                        Rectangle rightBounds = columnToTheRightOfResizedColumn.getBounds();
                        columnToTheRightOfResizedColumn.setBounds(rightBounds.x
                                + resizedColumnWidthChange, rightBounds.y,
                                savedColToRightOfResizedColWidth,
                                rightBounds.height);
                        runnableQueueIsClear = true;
                    }
                });
            }
        }
    };
}
