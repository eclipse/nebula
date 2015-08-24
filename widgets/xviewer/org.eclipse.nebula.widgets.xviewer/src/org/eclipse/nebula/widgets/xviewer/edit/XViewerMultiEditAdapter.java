package org.eclipse.nebula.widgets.xviewer.edit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;

/**
 * simple multi edit adapter <br>
 * - Converter.getInput() gives you the current selection in the selObject (first object is the clicked one)
 *
 * @author Juergen Reichl
 */
public class XViewerMultiEditAdapter extends XViewerEditAdapter {

   private IStructuredSelection currentSelection;
   private IStructuredSelection oldSelection;

   /**
    * @param xv
    */
   public XViewerMultiEditAdapter(XViewerControlFactory factory, XViewerConverter converter) {
      super(factory, converter);
   }

   @Override
   boolean handleEditEvent(Event event) {
      IStructuredSelection temp = (IStructuredSelection) xv.getSelection();
      oldSelection = currentSelection;
      currentSelection = temp;

      if (oldSelection == null || oldSelection.toList().size() == 1 || !oldSelection.toList().contains(
         currentSelection.getFirstElement())) {
         oldSelection = null;
      }

      if ((event.stateMask & SWT.CTRL) == SWT.CTRL || (event.stateMask & SWT.SHIFT) == SWT.SHIFT) {
         return false;
      }
      boolean handled = super.handleEditEvent(event);

      if (handled && oldSelection != null) {
         xv.setSelection(oldSelection);
         currentSelection = oldSelection;
      }
      return handled;
   }

   /**
    * @param c
    */
   @Override
   void getInput(Control c) {
      super.getInput(c);
      if (oldSelection != null) {
         xv.setSelection(oldSelection);
      }
      oldSelection = null;
   }

   @SuppressWarnings("unchecked")
   @Override
   Object getInputToModify() {
      List<Object> selection;
      if (oldSelection != null) {
         selection = oldSelection.toList();
         Collections.sort(selection, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
               if (klickedCell.getElement().equals(o1)) {
                  return -1;
               }
               if (klickedCell.getElement().equals(o2)) {
                  return 1;
               }
               return 0;
            }
         });
      } else {
         selection = new ArrayList<Object>();
         selection.add(super.getInputToModify());
      }

      return selection;
   }

   @Override
   protected void refreshElement(Object toRefresh) {
      if (toRefresh instanceof List<?>) {
         for (Object o : (List<?>) toRefresh) {
            super.refreshElement(o);
         }
      } else {
         super.refreshElement(toRefresh);
      }
   }

}
