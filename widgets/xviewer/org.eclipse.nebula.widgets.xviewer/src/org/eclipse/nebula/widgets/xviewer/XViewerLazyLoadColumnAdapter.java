/*
 * Created on May 19, 2015
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.nebula.widgets.xviewer;

/**
 * @author Donald G. Dunne
 */
public abstract class XViewerLazyLoadColumnAdapter implements IXViewerLazyLoadColumn {

   @Override
   public void setLoading(boolean loading) {
      // do nothing
   }

   @Override
   public String getText(Object obj, Long key, String cachedValue) {
      return null;
   }

}
