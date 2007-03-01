package org.eclipse.nebula.widgets.grid.internal;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Event;

public class NullScrollBarProxy implements IScrollBarProxy
{

    public boolean getVisible()
    {
        return false;
    }

    public void setVisible(boolean visible)
    {
    }

    public int getSelection()
    {
        return 0;
    }

    public void setSelection(int selection)
    {
    }

    public void setValues(int selection, int min, int max, int thumb, int increment,
                          int pageIncrement)
    {
    }

    public void handleMouseWheel(Event e)
    {
    }

    public void setMinimum(int min)
    {
    }

    public int getMinimum()
    {
        return 0;
    }

    public void setMaximum(int max)
    {
    }

    public int getMaximum()
    {
        return 0;
    }

    public void setThumb(int thumb)
    {
    }

    public int getThumb()
    {
        return 0;
    }

    public void setIncrement(int increment)
    {
    }

    public int getIncrement()
    {
        return 0;
    }

    public void setPageIncrement(int page)
    {
    }

    public int getPageIncrement()
    {
        return 0;
    }

    public void addSelectionListener(SelectionListener listener)
    {
        // TODO Auto-generated method stub
        
    }

    public void removeSelectionListener(SelectionListener listener)
    {
        // TODO Auto-generated method stub
        
    }

}
