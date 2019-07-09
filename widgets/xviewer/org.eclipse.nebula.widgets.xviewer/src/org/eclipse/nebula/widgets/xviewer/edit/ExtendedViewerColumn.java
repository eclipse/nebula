package org.eclipse.nebula.widgets.xviewer.edit;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;

/**
 * XViewerColumns with a description map(key is the inputObject.class)
 *
 * @author Juergen Reichl
 */
public class ExtendedViewerColumn extends XViewerColumn implements IExtendedViewerColumn {

   private Map<Class<?>, CellEditDescriptor> map = new HashMap<>();

   public ExtendedViewerColumn(String id, String name, int width, XViewerAlign align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
   }

   /**
    * @see IExtendedViewerColumn#getCellEditDescriptorMap()
    */
   @Override
   public Map<Class<?>, CellEditDescriptor> getCellEditDescriptorMap() {
      return new HashMap<>(map);
   }

   @Override
   public void setCellEditDescriptorMap(Map<Class<?>, CellEditDescriptor> map) {
      this.map = map;
   }

   /**
    * @see org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn#copy()
    */
   @Override
   public XViewerColumn copy() {
      ExtendedViewerColumn copyColumn = new ExtendedViewerColumn(super.id, super.name, super.getWidth(), super.getAlign(),
         super.isShow(), super.getSortDataType(), super.isMultiColumnEditable(), super.getDescription());
      copyColumn.setCellEditDescriptorMap(map);
      copyColumn.setSortForward(isSortForward());
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
