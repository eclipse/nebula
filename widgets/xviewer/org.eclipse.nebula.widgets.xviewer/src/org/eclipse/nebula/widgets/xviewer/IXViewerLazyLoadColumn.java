/*
 * Created on Feb 25, 2015
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.nebula.widgets.xviewer;

import java.util.Collection;
import java.util.Map;

/**
 * Columns that implement this interface will be given a chance to compute their values in the background and then
 * refreshed once completed. This feature uses the XViewer.refreshColumn to only refresh a single column after it's
 * values are cached.
 *
 * @author Donald G. Dunne
 */
public interface IXViewerLazyLoadColumn {

   /**
    * Called to get index for element for retrieval of cached text value
    */
   Long getKey(Object obj);

   /**
    * Called in a background thread to populate values in XViewerColumn.preComputedValueMap. Each item in table must
    * have an entry here. Null values will show the result of the getLoadingString method below.
    */
   void populateCachedValues(Collection<?> objects, Map<Long, String> preComputedValueMap);

   /*
    * Optional method called prior to and after populateCachedValues. Used if column wants to return a loading string
    * from getText during loading or do someother set-up/clean-up.
    */
   void setLoading(boolean loading);

   /**
    * Optional method available to make other checks during loading or otherwise change the cached value
    *
    * @param key returned from getKey call
    * @param cachedValue that will be used if this method isn't overridden or returns null
    * @return string to display
    */
   String getText(Object obj, Long key, String cachedValue);
}
