/*
 * Created on Feb 25, 2015
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.nebula.widgets.xviewer;

import java.util.Collection;
import java.util.Map;

/**
 * Columns that implement this interface will be given a chance to compute their values in the background before the
 * XViewer is loaded. Any item that does not have a value (null or otherwise) in preComputedValueMap will call getText
 * as normal.<br/>
 * <br/>
 * Important: XViewer.setInputXViewer(Object input) should be called instead of XViewer.setInput for precomputed columns
 * to work
 *
 * @author Donald G. Dunne
 */
public interface IXViewerPreComputedColumn {

   /**
    * Called to get index for element for retrieval of cached text value
    */
   Long getKey(Object obj);

   /**
    * Called in a background thread to populate values in XViewerColumn.preComputedValueMap. Each item in table must
    * have an entry here. Null values will show the result of the getLoadingString method below.
    */
   void populateCachedValues(Collection<?> objects, Map<Long, String> preComputedValueMap);

   /**
    * Optional method available to make other checks during loading or otherwise change the cached value
    *
    * @param key returned from getKey call
    * @param cachedValue that will be used if this method isn't overridden or returns null
    * @return string to display
    */
   String getText(Object obj, Long key, String cachedValue);
}
