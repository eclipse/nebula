package org.eclipse.nebula.widgets.xviewer.example;

import org.eclipse.nebula.widgets.xviewer.edit.CellEditDescriptor;
import org.eclipse.nebula.widgets.xviewer.edit.XViewerConverter;
import org.eclipse.nebula.widgets.xviewer.example.model.SomeTask;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class MyXViewerConverter implements XViewerConverter {

   @Override
   public void setInput(Control c, CellEditDescriptor ced, Object selObject) {

      if (c instanceof Text) {
         Text text = (Text) c;
         if (selObject instanceof SomeTask) {
            SomeTask someTask = (SomeTask) selObject;
            if (ced.getInputField().equals("completed")) { //$NON-NLS-1$
               text.setText(String.valueOf(someTask.getPercentComplete()));
            }
         }
      }

   }

   @Override
   public void getInput(Control c, CellEditDescriptor ced, Object selObject) {

      if (c instanceof Text) {
         Text text = (Text) c;
         if (selObject instanceof SomeTask) {
            SomeTask someTask = (SomeTask) selObject;
            if (ced.getInputField().equals("completed")) { //$NON-NLS-1$
               someTask.setPercentComplete(Integer.valueOf(text.getText()));
            }
         }
      }

   }

}
