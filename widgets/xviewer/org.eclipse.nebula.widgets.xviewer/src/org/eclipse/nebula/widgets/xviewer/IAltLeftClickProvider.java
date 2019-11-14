/*
 * Created on Oct 29, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.nebula.widgets.xviewer;

import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

@FunctionalInterface
public interface IAltLeftClickProvider {

   boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem);

}
