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
package org.eclipse.nebula.widgets.xviewer.customize;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.nebula.widgets.xviewer.IXViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerComputedColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerSorter;
import org.eclipse.nebula.widgets.xviewer.XViewerText;
import org.eclipse.nebula.widgets.xviewer.XViewerTextFilter;
import org.eclipse.nebula.widgets.xviewer.core.model.ColumnDateFilter;
import org.eclipse.nebula.widgets.xviewer.core.model.ColumnFilterData;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.core.model.DateRangeType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.core.util.Strings;
import org.eclipse.nebula.widgets.xviewer.util.Pair;
import org.eclipse.nebula.widgets.xviewer.util.XViewerException;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLib;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * This manages the default table column definitions versus the user modified column data, sorter and filters.
 *
 * @author Donald G. Dunne
 */
public class CustomizeManager {

   private final IXViewerFactory xViewerFactory;
   private final XViewer xViewer;
   private XViewerTextFilter xViewerTextFilter;
   private CustomizeData currentCustData;

   public final static String CURRENT_LABEL = XViewerText.get("label.current"); //$NON-NLS-1$
   public final static String TABLE_DEFAULT_LABEL = XViewerText.get("label.default"); //$NON-NLS-1$
   // Added to keep filter, sorter from working till finished loading
   public boolean loading = true;

   public CustomizeManager(XViewer xViewer, IXViewerFactory xViewerFactory) throws XViewerException {
      this.xViewer = xViewer;
      this.xViewerFactory = xViewerFactory;
      // Set customize to be user default, if selected, or table default
      CustomizeData userCustData = xViewerFactory.getXViewerCustomizations().getUserDefaultCustData();
      if (userCustData != null) {
         currentCustData = resolveLoadedCustomizeData(userCustData);
      } else {
         currentCustData = getTableDefaultCustData();
         currentCustData.setNameSpace(xViewerFactory.getNamespace());
      }
      xViewerFactory.getXViewerCustomMenu().init(xViewer);
   }

   private final Map<String, XViewerColumn> oldNameToColumnId = new HashMap<>();

   /**
    * Since saved customize data is stored as xml, all the columns need to be resolved to the columns available from the
    * factory
    */
   public CustomizeData resolveLoadedCustomizeData(CustomizeData loadedCustData) {
      // Otherwise, have to resolve what was saved with what is valid for this table and available from the factory
      CustomizeData resolvedCustData = new CustomizeData();
      resolvedCustData.setName(loadedCustData.getName());
      resolvedCustData.setPersonal(loadedCustData.isPersonal());
      resolvedCustData.setGuid(loadedCustData.getGuid());
      resolvedCustData.setNameSpace(loadedCustData.getNameSpace());
      /*
       * Need to resolve columns with what factory has which gets correct class/subclass of XViewerColumn and allows for
       * removal of old and addition of new columns
       */
      List<XViewerColumn> resolvedColumns = new ArrayList<>();
      for (XViewerColumn storedCol : loadedCustData.getColumnData().getColumns()) {
         XViewerColumn resolvedCol = xViewer.getXViewerFactory().getDefaultXViewerColumn(storedCol.getId());

         // Handle known stored values
         if (resolvedCol == null) {
            resolvedCol = getKnownStoredValue(storedCol, resolvedCol);
         }
         // if not found, may have been stored without namespace; try to resolve for backward compatibility
         if (resolvedCol == null) {
            resolvedCol = resolveByName(storedCol);
         }

         // Resolve computed columns
         if (resolvedCol == null) {
            resolvedCol = resolveComputedColumns(storedCol, resolvedCol);
         }

         // Only handle columns that the factory supports and only resolve shown columns (rest will be loaded later)
         if (resolvedCol != null && resolvedCol.getWidth() > 0) {
            if (storedCol.getWidth() > 0) {
               resolvedCol.setWidth(storedCol.getWidth());
            }
            resolvedCol.setName(storedCol.getName());
            resolvedCol.setShow(storedCol.isShow());
            resolvedCol.setSortForward(storedCol.isSortForward());
            resolvedColumns.add(resolvedCol);
         }
      }
      addNewlyCreatedColumns(resolvedColumns);

      resolveComputedColumnsAgain(resolvedColumns);

      resolvedCustData.getColumnData().setColumns(resolvedColumns);
      resolvedCustData.getColumnFilterData().setFromXml(loadedCustData.getColumnFilterData().getXml());
      resolvedCustData.getFilterData().setFromXml(loadedCustData.getFilterData().getXml());
      resolvedCustData.getSortingData().setFromXml(loadedCustData.getSortingData().getXml());
      return resolvedCustData;
   }

   private void resolveComputedColumnsAgain(List<XViewerColumn> resolvedColumns) {
      /*
       * Resolve computed columns, again, to enable source column to get set
       */
      for (XViewerColumn resolveCol : resolvedColumns) {
         if (resolveCol instanceof XViewerComputedColumn) {
            ((XViewerComputedColumn) resolveCol).setSourceXViewerColumnFromColumns(resolvedColumns);
         }
      }
   }

   private void addNewlyCreatedColumns(List<XViewerColumn> resolvedColumns) {
      /*
       * Add extra columns that were added to the table since storage of this custData
       */
      for (XViewerColumn extraCol : xViewer.getXViewerFactory().getDefaultTableCustomizeData().getColumnData().getColumns()) {
         if (!resolvedColumns.contains(extraCol)) {
            // Since column wasn't saved, don't show it
            extraCol.setShow(false);
            resolvedColumns.add(extraCol);
         }
      }
   }

   private XViewerColumn resolveComputedColumns(XViewerColumn storedCol, XViewerColumn resolvedCol) {
      for (XViewerComputedColumn xViewerComputedCol : xViewer.getComputedColumns()) {
         if (xViewerComputedCol.isApplicableFor(storedCol.getId())) {
            resolvedCol = xViewerComputedCol.createFromStored(storedCol);
         }
      }
      return resolvedCol;
   }

   private XViewerColumn resolveByName(XViewerColumn storedCol) {
      XViewerColumn resolvedCol;
      String name = storedCol.getName().replaceAll(" ", ""); //$NON-NLS-1$ //$NON-NLS-2$
      resolvedCol = oldNameToColumnId.get(name);
      // First try to match by .<oldname>
      if (resolvedCol == null) {
         for (XViewerColumn xCol : xViewer.getXViewerFactory().getDefaultTableCustomizeData().getColumnData().getColumns()) {
            String colId = xCol.getId().toLowerCase();
            String oldName = "." + name.toLowerCase(); //$NON-NLS-1$
            if (colId.endsWith(oldName)) {
               resolvedCol = xCol;
               oldNameToColumnId.put(name, resolvedCol);
               oldNameToColumnId.put(storedCol.getName(), resolvedCol);
               break;
            }
         }
      }
      // Then try to match by id endswith name
      if (resolvedCol == null) {
         for (XViewerColumn xCol : xViewer.getXViewerFactory().getDefaultTableCustomizeData().getColumnData().getColumns()) {
            if (xCol.getId().endsWith(name)) {
               resolvedCol = xCol;
               oldNameToColumnId.put(name, resolvedCol);
               oldNameToColumnId.put(storedCol.getName(), resolvedCol);
               break;
            }
         }
      }
      return resolvedCol;
   }

   private XViewerColumn getKnownStoredValue(XViewerColumn storedCol, XViewerColumn resolvedCol) {
      String name = storedCol.getName();
      if (name.equals("Impacted Items")) {
         resolvedCol = xViewer.getXViewerFactory().getDefaultXViewerColumn("ats.column.actionableItems"); //$NON-NLS-1$
      } else if (name.equals("State Percent")) {
         resolvedCol = xViewer.getXViewerFactory().getDefaultXViewerColumn("ats.column.statePercentComplete"); //$NON-NLS-1$
      }
      return resolvedCol;
   }

   public void setFilterText(String text, boolean regex) {
      currentCustData.getFilterData().setFilterText(text, regex);
      try {
         xViewer.getTree().setRedraw(false);
         xViewerTextFilter.update();
         xViewer.refresh();
      } finally {
         xViewer.getTree().setRedraw(true);
      }
   }

   public String getFilterText() {
      return currentCustData.getFilterData().getFilterText();
   }

   public void setColumnFilterText(String colId, String text) {
      if (text == null || text.equals("")) { //$NON-NLS-1$
         currentCustData.getColumnFilterData().removeFilterText(colId);
      } else {
         currentCustData.getColumnFilterData().setFilterText(colId, text);
      }
      xViewerTextFilter.update();
      xViewer.refresh();
   }

   public void clearFilters() {
      xViewer.getFilterDataUI().clear();
      currentCustData.getColumnFilterData().clear();
      xViewerTextFilter.update();
      xViewer.refresh();
   }

   public void clearAllColumnFilters() {
      currentCustData.getColumnFilterData().clear();
      xViewerTextFilter.update();
      xViewer.refresh();
   }

   public String getColumnFilterText(String colId) {
      return currentCustData.getColumnFilterData().getFilterText(colId);
   }

   public ColumnFilterData getColumnFilterData() {
      return currentCustData.getColumnFilterData();
   }

   /**
    * Clears out current columns, sorting and filtering and loads table customization
    */
   public void loadCustomization() {
      loadCustomization(currentCustData);
   }

   public void resetDefaultSorter() {
      XViewerSorter sorter = xViewer.getXViewerFactory().createNewXSorter(xViewer);
      xViewer.setSorter(sorter);
   }

   public void clearSorter() {
      currentCustData.getSortingData().clearSorter();
      xViewer.setSorter(null);
      xViewer.updateStatusLabel();
   }

   public void handleTableCustomization() {
      Dialog dialog = xViewerFactory.getCustomizeDialog(xViewer);
      if (dialog != null) {
         dialog.open();
      }
   }

   public void appendToStatusLabel(StringBuilder sb) {
      if (currentCustData != null && currentCustData.getName() != null) {
         sb.append(XViewerText.get("label.custom", currentCustData.getName())); //$NON-NLS-1$
      }
   }

   /**
    * @return the currentCustData; makes a copy of columns so they don't collide with each other
    */
   public CustomizeData generateCustDataFromTable() {
      CustomizeData custData = new CustomizeData();
      custData.setName(CustomizeManager.CURRENT_LABEL);
      custData.setNameSpace(xViewer.getXViewerFactory().getNamespace());
      List<XViewerColumn> columns = new ArrayList<>(15);
      for (Integer index : xViewer.getTree().getColumnOrder()) {
         TreeColumn treeCol = xViewer.getTree().getColumn(index);
         XViewerColumn xCol = (XViewerColumn) treeCol.getData();
         XViewerColumn newXCol = xCol.copy();
         newXCol.setWidth(treeCol.getWidth());
         newXCol.setShow(treeCol.getWidth() > 0);
         columns.add(newXCol);
      }
      // Add all columns that are not visible
      for (XViewerColumn xCol : xViewer.getCustomizeMgr().getCurrentTableColumns()) {
         if (!columns.contains(xCol)) {
            XViewerColumn newXCol = xCol.copy();
            newXCol.setShow(false);
            columns.add(newXCol);
         }
      }
      custData.getColumnData().setColumns(columns);
      custData.getSortingData().setFromXml(currentCustData.getSortingData().getXml());
      custData.getFilterData().setFromXml(currentCustData.getFilterData().getXml());
      custData.getColumnFilterData().setFromXml(currentCustData.getColumnFilterData().getXml());
      return custData;
   }

   public List<XViewerColumn> getCurrentTableColumns() {
      return currentCustData.getColumnData().getColumns();
   }

   public XViewerColumn getCurrentTableColumn(String id) {
      return currentCustData.getColumnData().getXColumn(id);
   }

   public List<XViewerColumn> getCurrentTableColumnsInOrder() {
      List<XViewerColumn> columns = new ArrayList<>(15);
      for (Integer index : xViewer.getTree().getColumnOrder()) {
         TreeColumn treeCol = xViewer.getTree().getColumn(index);
         XViewerColumn xCol = (XViewerColumn) treeCol.getData();
         columns.add(xCol);
      }
      return columns;
   }

   public List<XViewerColumn> getCurrentVisibleTableColumns() {
      List<XViewerColumn> columns = new ArrayList<>(15);
      for (XViewerColumn xCol : getCurrentTableColumns()) {
         if (xCol.isShow()) {
            columns.add(xCol);
         }
      }
      return columns;
   }

   public List<XViewerColumn> getCurrentHiddenTableColumns() {
      List<XViewerColumn> columns = new ArrayList<>(15);
      for (XViewerColumn xCol : getCurrentTableColumns()) {
         if (!xCol.isShow()) {
            columns.add(xCol);
         }
      }
      return columns;
   }

   /**
    * Return index of XColumn to original column index on creation of table. Since table allows drag re-ordering of
    * columns, this index will provide the map back to the original column index. Used for label providers
    * getColumnText(object, index)
    */
   public Map<XViewerColumn, Integer> getCurrentTableColumnsIndex() {
      int[] index = xViewer.getTree().getColumnOrder();
      Map<XViewerColumn, Integer> xColToColumnIndex = new HashMap<>(index.length);
      for (int x = 0; x < index.length; x++) {
         TreeColumn treeCol = xViewer.getTree().getColumn(index[x]);
         XViewerColumn xCol = (XViewerColumn) treeCol.getData();
         xColToColumnIndex.put(xCol, index[x]);
      }
      return xColToColumnIndex;
   }

   public int getColumnNumFromXViewerColumn(XViewerColumn xCol) {
      for (Integer index : xViewer.getTree().getColumnOrder()) {
         TreeColumn treeCol = xViewer.getTree().getColumn(index);
         XViewerColumn treeXCol = (XViewerColumn) treeCol.getData();
         if (xCol.equals(treeXCol)) {
            return index;
         }
      }
      return 0;
   }

   public Pair<XViewerColumn, Integer> getColumnNumFromXViewerColumn(String columnId) {
      if (!xViewer.getTree().isDisposed()) {
         for (Integer index : xViewer.getTree().getColumnOrder()) {
            TreeColumn treeCol = xViewer.getTree().getColumn(index);
            XViewerColumn treeXCol = (XViewerColumn) treeCol.getData();
            if (treeXCol.getId().equals(columnId)) {
               return new Pair<>(treeXCol, index);
            }
         }
      }
      return null;
   }

   public CustomizeData getTableDefaultCustData() {
      CustomizeData custData = xViewer.getXViewerFactory().getDefaultTableCustomizeData();
      if (custData.getName() == null || custData.getName().equals("")) { //$NON-NLS-1$
         custData.setName(TABLE_DEFAULT_LABEL);
      }
      custData.setNameSpace(xViewer.getViewerNamespace());
      return custData;
   }

   public void getSortingStr(StringBuilder sb) {
      if (currentCustData.getSortingData().isSorting()) {
         List<XViewerColumn> cols = getSortXCols();
         if (cols.isEmpty()) {
            return;
         }
         sb.append(XViewerText.get("label.status.sort")); //$NON-NLS-1$
         for (XViewerColumn col : getSortXCols()) {
            if (col != null) {
               sb.append(XViewerText.get("label.status.sort.start")); //$NON-NLS-1$
               sb.append(col.getName());
               sb.append(col.isSortForward() ? XViewerText.get("label.status.sort.fwd") : XViewerText.get( //$NON-NLS-1$
                  "label.status.sort.rev")); //$NON-NLS-1$
            }
         }
      }
   }

   public int getDefaultWidth(String id) {
      XViewerColumn xCol = xViewerFactory.getDefaultXViewerColumn(id);
      if (xCol == null) {
         return 75;
      } else {
         return xCol.getWidth();
      }
   }

   public boolean isCustomizationUserDefault(CustomizeData custData) throws XViewerException {
      return xViewerFactory.getXViewerCustomizations().isCustomizationUserDefault(custData);
   }

   public List<XViewerColumn> getSortXCols() {
      // return sort columns depending on default/customize
      return currentCustData.getSortingData().getSortXCols(oldNameToColumnId);
   }

   public boolean isLoading() {
      return loading;
   }

   public List<CustomizeData> getSavedCustDatas() throws Exception {
      List<CustomizeData> custDatas = new ArrayList<>();
      for (CustomizeData savedCustData : xViewerFactory.getXViewerCustomizations().getSavedCustDatas()) {
         custDatas.add(resolveLoadedCustomizeData(savedCustData));
      }
      return custDatas;
   }

   public void saveCustomization(CustomizeData custData) throws Exception {
      xViewerFactory.getXViewerCustomizations().saveCustomization(custData);
   }

   /**
    * Set to newName or clear if newName == ""
    */
   public void customizeColumnName(XViewerColumn xCol, String newName) {
      if (newName.equals("")) { //$NON-NLS-1$
         XViewerColumn defaultXCol = xViewerFactory.getDefaultXViewerColumn(xCol.getId());
         if (defaultXCol == null) {
            XViewerLib.popup(XViewerText.get("error"), XViewerText.get("error.column_undefined")); //$NON-NLS-1$ //$NON-NLS-2$
            return;
         }
         xCol.setName(defaultXCol.getName());
      } else {
         xCol.setName(newName);
      }
   }

   public void setUserDefaultCustData(CustomizeData newCustData, boolean set) throws Exception {
      xViewerFactory.getXViewerCustomizations().setUserDefaultCustData(newCustData, set);
   }

   public void deleteCustomization(CustomizeData custData) throws Exception {
      xViewerFactory.getXViewerCustomizations().deleteCustomization(custData);

   }

   public boolean isSorting() {
      return currentCustData.getSortingData().isSorting();
   }

   /**
    * Clears out current columns, sorting and filtering and loads table customization
    */
   public void loadCustomization(final CustomizeData newCustData) {
      loading = true;
      if (xViewerTextFilter == null) {
         xViewerTextFilter = xViewer.getXViewerTextFilter();
         xViewer.addFilter(xViewerTextFilter);
      }
      if (xViewer.getTree().isDisposed()) {
         return;
      }
      currentCustData = newCustData;
      if (currentCustData.getName() == null || currentCustData.getName().equals("")) { //$NON-NLS-1$
         currentCustData.setName(CURRENT_LABEL);
      }
      currentCustData.setNameSpace(xViewer.getViewerNamespace());
      if (currentCustData.getSortingData().isSorting()) {
         xViewer.resetDefaultSorter();
      } else {
         xViewer.setSorter(null);
      }
      if (xViewer.getFilterDataUI() != null) {
         xViewer.getFilterDataUI().update();
      }
      xViewerTextFilter.update();
      // Dispose all existing columns
      for (TreeColumn treeCol : xViewer.getTree().getColumns()) {
         treeCol.dispose();
      }
      // Create new columns
      addColumns();
      xViewer.updateStatusLabel();
      if (xViewer.getLabelProvider() instanceof IXViewerLabelProvider) {
         ((IXViewerLabelProvider) xViewer.getLabelProvider()).clearXViewerColumnIndexCache();
      }
      loading = false;
   }

   public void addColumns() {
      for (final XViewerColumn xCol : currentCustData.getColumnData().getColumns()) {
         // Only add visible columns
         if (!xCol.isShow()) {
            continue;
         }
         xCol.setXViewer(xViewer);
         TreeColumn column = new TreeColumn(xViewer.getTree(), getSwtAlign(xCol.getAlign()));
         column.setMoveable(true);
         column.setData(xCol);
         StringBuilder sb = new StringBuilder();
         sb.append(xCol.getName());
         if (Strings.isValid(xCol.getDescription()) && !xCol.getDescription().equals(xCol.getName())) {
            sb.append("\n"); //$NON-NLS-1$
            sb.append(xCol.getDescription());
         }
         if (Strings.isValid(xCol.getToolTip()) && !xCol.getToolTip().equals(
            xCol.getName()) && !xCol.getToolTip().equals(xCol.getDescription())) {
            sb.append("\n"); //$NON-NLS-1$
            sb.append(xCol.getToolTip());
         }
         // Only show id if different from name and non-null
         if (xCol.getId() != null && !"".equals(xCol.getId()) && !xCol.getName().equals(xCol.getId())) { //$NON-NLS-1$
            sb.append("\n"); //$NON-NLS-1$
            sb.append(xCol.getId());
         }
         column.setToolTipText(sb.toString());
         column.setText(xCol.getName());
         column.setWidth(xCol.getWidth());
         column.addListener(SWT.Selection, e-> {
               // Add sorter if doesn't exist
               if (xViewer.getSorter() == null) {
                  resetDefaultSorter();
               }
               if (xViewer.isAltKeyDown()) {
                  xViewer.getColumnFilterDataUI().promptSetFilter(xCol);
               } else if (xViewer.isCtrlKeyDown()) {
                  List<XViewerColumn> currSortCols = currentCustData.getSortingData().getSortXCols(oldNameToColumnId);
                  if (currSortCols == null) {
                     currSortCols = new ArrayList<>();
                     currSortCols.add(xCol);
                  } else {
                     // If already selected this item, reverse the sort
                     if (currSortCols.contains(xCol)) {
                        for (XViewerColumn currXCol : currSortCols) {
                           if (currXCol.equals(xCol)) {
                              currXCol.reverseSort();
                           }
                        }
                     } else {
                        currSortCols.add(xCol);
                     }
                  }
                  currentCustData.getSortingData().setSortXCols(currSortCols);
               } else {

                  List<XViewerColumn> cols = new ArrayList<>();
                  cols.add(xCol);
                  // If sorter already has this column sorted, reverse the sort
                  List<XViewerColumn> currSortCols = currentCustData.getSortingData().getSortXCols(oldNameToColumnId);
                  if (currSortCols != null && currSortCols.size() == 1 && currSortCols.iterator().next().equals(xCol)) {
                     xCol.reverseSort();
                  }
                  // Set the newly sorted column
                  currentCustData.getSortingData().setSortXCols(cols);
               }
               xViewer.refresh();
         });
      }
   }

   public static int getSwtAlign(XViewerAlign align) {
      if (align == XViewerAlign.Center) {
         return SWT.CENTER;
      } else if (align == XViewerAlign.Right) {
         return SWT.RIGHT;
      }
      return SWT.LEFT;
   }

   public boolean isFilterTextRegularExpression() {
      return currentCustData.getFilterData().isRegularExpression();
   }

   public void setColumnDateFilter(String columnId, DateRangeType dateRangeType, Date date1, Date date2) {
      if (dateRangeType == null || dateRangeType == DateRangeType.None) { //$NON-NLS-1$
         currentCustData.getColumnFilterData().removeDateFilter(columnId);
      } else {
         currentCustData.getColumnFilterData().setDateFilter(columnId, dateRangeType, date1, date2);
      }
      xViewerTextFilter.update();
      xViewer.refresh();

   }

   public ColumnDateFilter getColumnDateFilter(String columnId) {
      return currentCustData.getColumnFilterData().getDateFilter(columnId);
   }

   public CustomizeData getCurrentCustomizeData() {
      return currentCustData;
   }
}
