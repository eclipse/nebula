package org.eclipse.nebula.widgets.xviewer.edit;

import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.swt.widgets.Control;

/**
 * interface to create the controls
 * 
 * @author Juergen Reichl
 */
public interface XViewerControlFactory {

   /**
    * with the CellEditDescriptor u can get the type of the control and the style
    */
   public abstract Control createControl(CellEditDescriptor ced, XViewer xv);

}
