/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.xviewer;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.customize.IXViewerCustomizations;
import org.eclipse.nebula.widgets.xviewer.customize.XViewerCustomMenu;

/**
 * @author Andrew M. Finkbeiner
 */
public interface IXViewerFactory {
   /**
    * Returns the default sorter to use for this xviewer
    */
   XViewerSorter createNewXSorter(XViewer viewer);

   /**
    * Returns the default table customization for this viewer including column definitions, sorting and filtering (if
    * any)
    */
   CustomizeData getDefaultTableCustomizeData();

   /**
    * Returns the default column definition for the given id
    */
   XViewerColumn getDefaultXViewerColumn(String id);

   /**
    * Provides the storage mechanism for save/load of personal/shared customizations
    */
   IXViewerCustomizations getXViewerCustomizations();

   /**
    * Provides custom menu for XViewer
    */
   XViewerCustomMenu getXViewerCustomMenu();

   /**
    * Unique namespace for this viewer
    */
   String getNamespace();

   /**
    * Will enable certain extra functionality based on admin validity
    */
   boolean isAdmin();

   /**
    * Provides for ability to customize tree report that is provided
    */
   XViewerTreeReport getXViewerTreeReport(XViewer xViewer);

   public boolean isSearchUiAvailable();

   public boolean isFilterUiAvailable();

   public boolean isLoadedStatusLabelAvailable();

   public boolean isHeaderBarAvailable();

   public boolean isCellGradientOn();

   /**
    * @return true if search/filter should be before the table; false it is located after the table
    */
   boolean isSearhTop();

   /**
    * @return dialog to customize the XViewer. if null, nothing will be opened.
    */
   public Dialog getCustomizeDialog(XViewer xViewer);

}
