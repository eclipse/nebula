package org.eclipse.nebula.jface.cdatetime;

import java.util.Date;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;

public class CDateTimeSelectionProvider implements ISelectionProvider, SelectionListener {

	private CDateTime cdt;
	private ListenerList selectionChangedListeners = new ListenerList(ListenerList.IDENTITY);
	
	public CDateTimeSelectionProvider(CDateTime cdt) {
		this.cdt = cdt;
		cdt.addSelectionListener(this);
	}

	public CDateTimeSelectionProvider(Composite parent, int style) {
		cdt = new CDateTime(parent, style);
		cdt.addSelectionListener(this);
	}
	
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.add(listener);
	}

    /**
     * Notifies any selection changed listeners that the viewer's selection has changed.
     * Only listeners registered at the time this method is called are notified.
     *
     * @param event a selection changed event
     *
     * @see ISelectionChangedListener#selectionChanged
     */
    private void fireSelectionChanged(final SelectionChangedEvent event) {
        Object[] listeners = selectionChangedListeners.getListeners();
        for (int i = 0; i < listeners.length; ++i) {
            final ISelectionChangedListener l = (ISelectionChangedListener) listeners[i];
            SafeRunnable.run(new SafeRunnable() {
                public void run() {
                    l.selectionChanged(event);
                }
            });
        }
    }

    public CDateTime getCDateTime() {
		return cdt;
	}
	
	public ISelection getSelection() {
		return new StructuredSelection(cdt.getSelection());
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);
	}

	public void setSelection(ISelection selection) {
		if(!selection.isEmpty() && (selection instanceof IStructuredSelection)) {
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			if(obj instanceof Date) {
				cdt.setSelection((Date) obj);
			}
		}
	}

	public void widgetDefaultSelected(SelectionEvent event) {
		IStructuredSelection sel = new StructuredSelection(cdt.getSelection());
		SelectionChangedEvent sce = new SelectionChangedEvent(this, sel);
		fireSelectionChanged(sce);
	}

	public void widgetSelected(SelectionEvent event) {
		IStructuredSelection sel = new StructuredSelection(cdt.getSelection());
		SelectionChangedEvent e = new SelectionChangedEvent(this, sel);
		fireSelectionChanged(e);
	}

}
