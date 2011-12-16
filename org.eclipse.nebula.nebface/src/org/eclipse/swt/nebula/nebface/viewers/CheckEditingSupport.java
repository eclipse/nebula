package org.eclipse.swt.nebula.nebface.viewers;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;

public abstract class CheckEditingSupport extends EditingSupport
{

    protected boolean canEdit(Object element)
    {
        // TODO Auto-generated method stub
        return false;
    }

    protected CellEditor getCellEditor(Object element)
    {
        // TODO Auto-generated method stub
        return null;
    }

    protected Object getValue(Object element)
    {
        // TODO Auto-generated method stub
        return null;
    }

    protected abstract void setValue(Object element, Object value);

}
