package org.eclipse.nebula.widgets.xviewer.edit;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;

/**
 * XViewerColumns with a description map(key is the inputObject.class)
 * 
 * @author Juergen Reichl
 */
public class ExtendedViewerColumn extends XViewerColumn implements IExtendedViewerColumn {

   private Map<Class<?>, CellEditDescriptor> map = new HashMap<Class<?>, CellEditDescriptor>();

   public ExtendedViewerColumn(String id, String name, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
   }

   /**
    * @see IExtendedViewerColumn#getCellEditDescriptorMap()
    */
   @Override
   public Map<Class<?>, CellEditDescriptor> getCellEditDescriptorMap() {
      return new HashMap<Class<?>, CellEditDescriptor>(map);
   }

   @Override
   public void setCellEditDescriptorMap(Map<Class<?>, CellEditDescriptor> map) {
      this.map = map;
   }

   /**
    * @see org.eclipse.nebula.widgets.xviewer.XViewerColumn#copy()
    */
   @Override
   public XViewerColumn copy() {
      XViewerColumn copyColumn =
         new ExtendedViewerColumn(super.id, super.name, super.getWidth(), super.getAlign(), super.isShow(),
            super.getSortDataType(), super.isMultiColumnEditable(), super.getDescription());
      ((ExtendedViewerColumn) copyColumn).setCellEditDescriptorMap(map);
      return copyColumn;
   }

   @Override
   public void addMapEntry(Class<?> key, CellEditDescriptor ced) {
      map.put(key, ced);
   }

   @Override
   public void removeMapEntry(Class<?> key) {
      map.remove(key);
   }

   @Override
   public void clearMap() {
      map.clear();
   }

}
