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

import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.customize.IXViewerCustomizations;
import org.eclipse.nebula.widgets.xviewer.customize.XViewerCustomMenu;

/**
 * @author Andrew M. Finkbeiner
 */
public interface IXViewerFactory {
   /**
    * Returns the default sorter to use for this xviewer
    * 
    * @param viewer
    * @return XViewerSorter
    */
   XViewerSorter createNewXSorter(XViewer viewer);

   /**
    * Returns the default table customization for this viewer including column definitions, sorting and filtering (if
    * any)
    * 
    * @return CustomizeData
    */
   CustomizeData getDefaultTableCustomizeData();

   /**
    * Returns the default column definition for the given id
    * 
    * @param id
    * @return XViewerColumn
    */
   XViewerColumn getDefaultXViewerColumn(String id);

   /**
    * Provides the storage mechanism for save/load of personal/global customizations
    * 
    * @return IXViewerCustomizations
    */
   IXViewerCustomizations getXViewerCustomizations();

   /**
    * Provides custom menu for XViewer
    * 
    * @return XViewerCustomMenu
    */
   XViewerCustomMenu getXViewerCustomMenu();

   /**
    * Unique namespace for this viewer
    * 
    * @return String
    */
   String getNamespace();

   /**
    * Will enable certain extra functionality based on admin validity
    * 
    * @return true if admin
    */
   boolean isAdmin();

   /**
    * Provides for ability to customize tree report that is provided
    * 
    * @return XViewerTreeReport
    */
   XViewerTreeReport getXViewerTreeReport(XViewer xViewer);

   public boolean isSearchUiAvailable();

   public boolean isFilterUiAvailable();

   public boolean isLoadedStatusLabelAvailable();

   public boolean isHeaderBarAvailable();

   public boolean isCellGradientOn();
}
