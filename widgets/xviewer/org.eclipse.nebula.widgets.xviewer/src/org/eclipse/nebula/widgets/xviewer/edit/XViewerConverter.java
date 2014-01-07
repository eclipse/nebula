package org.eclipse.nebula.widgets.xviewer.edit;

import org.eclipse.swt.widgets.Control;

/**
 * interface to manage the input
 * 
 * @author Juergen Reichl
 */
public interface XViewerConverter {

   /**
    * Method to fill the Controls <br>
    * <br>
    * - check with the CellEditDescriptor(getInputField) what to put into the Control
    * 
    * @param c - current control
    * @param ced - the cell edit descriptor
    * @param selObject - the selected object
    */
   public abstract void setInput(Control c, CellEditDescriptor ced, Object selObject);

   /**
    * Method to put the new Input(from the Control c) into the selObject <br>
    * <br>
    * - check with the CellEditDescriptor(getInputField) where to save the new Input
    * 
    * @param c - current control
    * @param ced - the cell edit descriptor
    * @param selObject - the selected object
    * @return the object if it has a new reference otherwise null -> next step is the automatic xviewer.refresh(object).
    * if return null it will refresh the old reference.
    */
   public abstract Object getInput(Control c, CellEditDescriptor ced, Object selObject);

   /**
    * is this object valid for this cell?
    * 
    * @param ced - the cell edit descriptor
    * @param selObject - the selected object
    * @return true if you want to allow to modify this object
    */
   public abstract boolean isValid(CellEditDescriptor ced, Object selObject);
}
