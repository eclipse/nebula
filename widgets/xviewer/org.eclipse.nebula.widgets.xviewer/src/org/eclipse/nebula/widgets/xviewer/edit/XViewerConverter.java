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
    * - check with the CellEditDescriptor(getInputField) what to put into the Control
    */
   public abstract void setInput(Control c, CellEditDescriptor ced, Object selObject);

   /**
    * Method to put the new Input(from the Control c) into the selObject <br>
    * - check with the CellEditDescriptor(getInputField) where to save the new Input
    */
   public abstract void getInput(Control c, CellEditDescriptor ced, Object selObject);

}
