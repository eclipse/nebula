package org.eclipse.swt.nebula.widgets.compositetable;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;


public class ResizableGridHeaderLayout extends GridRowLayout {
    private ResizableGridMouseListener mouseListener = null;
    private DisposeListener disposeListener = null;
    private Composite child = null;

    public ResizableGridHeaderLayout() {
        super();
    }
    
    public ResizableGridHeaderLayout(int[] weights, boolean fittingHorizontally) {
        super(weights, fittingHorizontally);
    }
    
    protected Point computeSize(Composite child, int wHint, int hHint, boolean flushCache) {
        addListeners(child);
        return super.computeSize(child, wHint, hHint, flushCache);
    }
    
    protected void layout(Composite child, boolean flushCache) {
        addListeners(child);
        super.layout(child, flushCache);
    }
    
    /**
     * Adds mouse and dispose listeners to child
     * 
     * @param child child composite
     */
    private void addListeners(Composite child) {
        if (this.child == null) {
            this.child = child; 
            mouseListener = new ResizableGridMouseListener();
            disposeListener = new ResizableGridDisposeListener();
            System.out.println("Adding listeners...");
            child.addMouseListener(mouseListener);
            child.addMouseMoveListener(mouseListener);
            child.addMouseTrackListener(mouseListener);
            child.addDisposeListener(disposeListener);
        }
    }
    
    private class ResizableGridMouseListener implements MouseListener, 
        MouseMoveListener, MouseTrackListener {
        public void mouseDoubleClick(MouseEvent e) {
            // NOOP
        }

        public void mouseDown(MouseEvent e) {
        }

        public void mouseUp(MouseEvent e) {
        }

        public void mouseMove(MouseEvent e) {
        }

        public void mouseEnter(MouseEvent e) {
        }

        public void mouseExit(MouseEvent e) {
        }

        public void mouseHover(MouseEvent e) {
        }
    }
    
    private class ResizableGridDisposeListener implements DisposeListener {
        public void widgetDisposed(DisposeEvent e) {
            child.removeMouseListener(mouseListener);
            child.removeMouseMoveListener(mouseListener);
            child.removeDisposeListener(disposeListener);
        }
    }
}
