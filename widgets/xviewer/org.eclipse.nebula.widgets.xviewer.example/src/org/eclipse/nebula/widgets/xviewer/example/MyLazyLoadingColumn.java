/*
 * Created on Apr 13, 2015
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.nebula.widgets.xviewer.example;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import org.eclipse.nebula.widgets.xviewer.IXViewerLazyLoadColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.example.model.SomeTask;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class MyLazyLoadingColumn extends XViewerColumn implements IXViewerLazyLoadColumn {

   private boolean loading;

   public MyLazyLoadingColumn() {
      super(MyXViewerFactory.COLUMN_NAMESPACE + ".lazyLoadExample", "Lazy Loaded Column", 130, SWT.LEFT, true,
         SortDataType.String, false, "Background loaded column that refreshes itself when completed.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public MyLazyLoadingColumn copy() {
      MyLazyLoadingColumn newXCol = new MyLazyLoadingColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public void populateCachedValues(Collection<?> objects, Map<Long, String> lazyLoadingValueMap) {
      try {
         Thread.sleep(2000);
         long time = (new Date()).getTime();
         for (Object obj : objects) {
            lazyLoadingValueMap.put(getKey(obj), "value " + time++);
         }
      } catch (InterruptedException ex) {
         // do nothing
      }
   }

   @Override
   public Long getKey(Object obj) {
      return new Long(((SomeTask) obj).getId().hashCode());
   }

   @Override
   public void setLoading(boolean loading) {
      this.loading = loading;
   }

   @Override
   public String getText(Object obj, Long key, String cachedValue) {
      if (loading) {
         return "loading...";
      }
      return cachedValue;
   }

}
