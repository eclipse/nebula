package org.eclipse.nebula.widgets.xviewer.example;

import java.util.List;
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
   public Object getInput(Control c, CellEditDescriptor ced, Object selObject) {

      if (ced.getInputField().equals("completed")) { //$NON-NLS-1$
         if (c instanceof Text) {
            Text text = (Text) c;
            Integer intValue = Integer.valueOf(text.getText());
            if (selObject instanceof SomeTask) {
               SomeTask someTask = (SomeTask) selObject;
               someTask.setPercentComplete(intValue);
            } else if (selObject instanceof List<?>) {
               List<?> list = (List<?>) selObject;
               if (list.isEmpty()) {
                  return null;
               }
               if (((SomeTask) list.get(0)).getPercentComplete() == intValue) {
                  return null;
               }
               for (Object o : list) {
                  if (o instanceof SomeTask) {
                     SomeTask someTask = (SomeTask) o;
                     someTask.setPercentComplete(intValue);
                  }
               }
               return selObject;
            }
         }
      }

      return null;
   }

   @Override
   public boolean isValid(CellEditDescriptor ced, Object selObject) {
      return true;
   }

}
