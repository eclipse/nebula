/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.xviewer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.customize.IXViewerCustomizations;
import org.eclipse.nebula.widgets.xviewer.customize.XViewerCustomMenu;
import org.eclipse.nebula.widgets.xviewer.customize.XViewerCustomizations;
import org.eclipse.nebula.widgets.xviewer.customize.dialog.XViewerCustomizeDialog;

/**
 * @author Donald G. Dunne
 */
public abstract class XViewerFactory implements IXViewerFactory {

   private String namespace;

   public void setNamespace(String namespace) {
      this.namespace = namespace;
   }

   private final List<XViewerColumn> columns = new ArrayList<XViewerColumn>();
   private final Map<String, XViewerColumn> idToColumn = new HashMap<String, XViewerColumn>();

   public XViewerFactory(String namespace) {
      this.namespace = namespace;
   }

   public void registerColumns(Collection<? extends XViewerColumn> columns) {
      for (XViewerColumn col : columns) {
         registerColumns(col);
      }
   }

   public void registerColumns(XViewerColumn... columns) {
      if (columns.length == 0) {
         throw new IllegalArgumentException("columns can't be null");
      }
      for (XViewerColumn xCol : columns) {
         if (!columnRegistered(xCol)) {
            this.columns.add(xCol);
            idToColumn.put(xCol.getId(), xCol);
         }
      }
   }

   public boolean columnRegistered(XViewerColumn column) {
      return this.columns.contains(column);
   }

   public void clearColumnRegistration() {
      this.columns.clear();
      idToColumn.clear();
   }

   @Override
   public XViewerSorter createNewXSorter(XViewer xViewer) {
      return new XViewerSorter(xViewer);
   }

   @Override
   public CustomizeData getDefaultTableCustomizeData() {
      CustomizeData custData = new CustomizeData();
      custData.setNameSpace(namespace);
      custData.getColumnData().setColumns(getColumns());
      return custData;
   }

   @Override
   public XViewerColumn getDefaultXViewerColumn(String id) {
      // Return a copy so don't corrupt original definition of column
      XViewerColumn col = idToColumn.get(id);
      if (col == null) {
         return null;
      }
      return col.copy();
   }

   public void overrideShowDefault(String id, boolean show) {
      XViewerColumn col = idToColumn.get(id);
      col.setShow(show);
   }

   @Override
   public IXViewerCustomizations getXViewerCustomizations() {
      return new XViewerCustomizations();
   }

   @Override
   public XViewerCustomMenu getXViewerCustomMenu() {
      return new XViewerCustomMenu();
   }

   @Override
   public String getNamespace() {
      return namespace;
   }

   public List<XViewerColumn> getColumns() {
      // Return a copy so don't corrupt original definition of column
      List<XViewerColumn> columnCopy = new ArrayList<XViewerColumn>();
      for (XViewerColumn xCol : columns) {
         columnCopy.add(xCol.copy());
      }
      return columnCopy;
   }

   @Override
   public XViewerTreeReport getXViewerTreeReport(XViewer viewer) {
      return null;
   }

   @Override
   public XViewerLoadingReport getXViewerLoadingReport(XViewer viewer) {
      return null;
   }

   @Override
   public boolean isFilterUiAvailable() {
      return true;
   }

   @Override
   public boolean isHeaderBarAvailable() {
      return true;
   }

   @Override
   public boolean isLoadedStatusLabelAvailable() {
      return true;
   }

   @Override
   public boolean isSearchUiAvailable() {
      return true;
   }

   @Override
   public boolean isCellGradientOn() {
      return false;
   }

   @Override
   public boolean isSearhTop() {
      return false;
   }

   @Override
   public Dialog getCustomizeDialog(XViewer xViewer) {
      return new XViewerCustomizeDialog(xViewer);
   }

}
