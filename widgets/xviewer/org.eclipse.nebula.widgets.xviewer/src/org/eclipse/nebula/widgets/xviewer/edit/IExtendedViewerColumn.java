package org.eclipse.nebula.widgets.xviewer.edit;

import java.util.Map;

/**
 * @author Juergen Reichl
 */
public interface IExtendedViewerColumn {

   /**
    * get a copy -- to manipulate the map use the methods (addMapEntry, removeMapEntry, clearMap)
    */
   public abstract Map<Class<?>, CellEditDescriptor> getCellEditDescriptorMap();

   public abstract void setCellEditDescriptorMap(Map<Class<?>, CellEditDescriptor> map);

   public abstract void addMapEntry(Class<?> key, CellEditDescriptor ced);

   public abstract void removeMapEntry(Class<?> key);

   public abstract void clearMap();

}
