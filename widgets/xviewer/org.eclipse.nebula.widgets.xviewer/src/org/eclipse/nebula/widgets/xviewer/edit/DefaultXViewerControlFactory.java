package org.eclipse.nebula.widgets.xviewer.edit;

import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Text;

/**
 * Possible controls in the DefaultXViewerControlFactory(07.10.2010): <br>
 * - org.eclipse.swt.widgets.Text <br>
 * - org.eclipse.swt.widgets.Combo <br>
 * - org.eclipse.swt.widgets.DateTime <br>
 * <br>
 * if you need other controls create your own class implementing the XViewerControlFactory
 * 
 * @author Juergen Reichl
 */
public class DefaultXViewerControlFactory implements XViewerControlFactory {

   /**
    * @see net.uniopt.jface.viewers.XViewerControlFactory#createControl(net.uniopt.jface.viewers.CellEditDescriptor,
    * org.eclipse.nebula.widgets.xviewer.XViewer)
    */
   @Override
   public Control createControl(CellEditDescriptor ced, XViewer xv) {
      if (ced.getControl().equals(Text.class)) {
         return new Text(xv.getTree(), ced.getSwtStyle());
      } else if (ced.getControl().equals(Combo.class)) {
         return new Combo(xv.getTree(), ced.getSwtStyle());
      } else if (ced.getControl().equals(DateTime.class)) {
         return new DateTime(xv.getTree(), ced.getSwtStyle());
      } else {
         return null;
      }
   }

}
