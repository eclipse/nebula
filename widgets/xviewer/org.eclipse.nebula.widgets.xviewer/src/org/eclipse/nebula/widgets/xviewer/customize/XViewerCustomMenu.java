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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.nebula.widgets.xviewer.Activator;
import org.eclipse.nebula.widgets.xviewer.IXViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumnLabelProvider;
import org.eclipse.nebula.widgets.xviewer.XViewerColumnSorter;
import org.eclipse.nebula.widgets.xviewer.XViewerComputedColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerText;
import org.eclipse.nebula.widgets.xviewer.action.ColumnMultiEditAction;
import org.eclipse.nebula.widgets.xviewer.action.TableCustomizationAction;
import org.eclipse.nebula.widgets.xviewer.action.ViewLoadingReportAction;
import org.eclipse.nebula.widgets.xviewer.action.ViewSelectedCellDataAction;
import org.eclipse.nebula.widgets.xviewer.action.ViewSelectedCellDataAction.Option;
import org.eclipse.nebula.widgets.xviewer.action.ViewTableReportAction;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.core.util.CollectionsUtil;
import org.eclipse.nebula.widgets.xviewer.util.internal.ArrayTreeContentProvider;
import org.eclipse.nebula.widgets.xviewer.util.internal.HtmlUtil;
import org.eclipse.nebula.widgets.xviewer.util.internal.PatternFilter;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLib;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLog;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerPatternFilter;
import org.eclipse.nebula.widgets.xviewer.util.internal.dialog.HtmlDialog;
import org.eclipse.nebula.widgets.xviewer.util.internal.dialog.XCheckFilteredTreeDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * Allow for the customization of the xViewer's right-click menus. Full menu can be used or selected Actions accessed
 * for partial implementation in existing menus
 *
 * @author Donald G. Dunne
 */
public class XViewerCustomMenu {

   protected XViewer xViewer;
   private final Clipboard clipboard = new Clipboard(null);

   protected Action filterByValue, filterByColumn, filterBySelColumn, clearAllSorting, clearAllFilters, tableProperties,
      viewTableReport, columnMultiEdit, removeSelected, removeNonSelected, viewLoadingReport, copySelected, showColumn,
      addComputedColumn, sumColumn, averageColumn, hideColumn, copySelectedColumnCells, viewSelectedCell,
      copySelectedCell, uniqueValues;
   private boolean headerMouseClick = false;

   public boolean isHeaderMouseClick() {
      return headerMouseClick;
   }

   public XViewerCustomMenu() {
      // do nothing
   }

   public XViewerCustomMenu(XViewer xViewer) {
      this.xViewer = xViewer;
   }

   public void init(final XViewer xviewer) {
      this.xViewer = xviewer;
      setupActions();
      xViewer.getTree().addListener(SWT.KeyUp, e-> {
          if (e.keyCode == 'c' && e.stateMask == (SWT.CONTROL | SWT.SHIFT)) {
              performCopyColumnCells();
           } else if (e.keyCode == 'b' && e.stateMask == (SWT.CONTROL | SWT.SHIFT)) {
              performViewOrCopyCell(Option.Copy);
           } else if (e.keyCode == 'v' && e.stateMask == (SWT.CONTROL | SWT.SHIFT)) {
              performViewOrCopyCell(Option.View);
           } else if (e.keyCode == 'c' && e.stateMask == SWT.CONTROL) {
              performCopy();
           }
      });
      xViewer.getTree().addListener(SWT.Dispose, e-> {
            if (clipboard != null) {
               clipboard.dispose();
            }
      });
      xViewer.getMenuManager().addMenuListener(new IMenuListener() {
         @Override
         public void menuAboutToShow(IMenuManager manager) {
            if (headerMouseClick) {
               setupMenuForHeader();
               xviewer.updateMenuActionsForHeader();
            } else {
               setupMenuForTable();
               xViewer.updateMenuActionsForTable();
            }
         }
      });
      xViewer.getTree().addListener(SWT.MenuDetect, event -> {
            Point pt = Display.getCurrent().map(null, xViewer.getTree(), new Point(event.x, event.y));
            Rectangle clientArea = xViewer.getTree().getClientArea();
            headerMouseClick = clientArea.y <= pt.y && pt.y < (clientArea.y + xViewer.getTree().getHeaderHeight());
      });
   }

   protected void setupMenuForHeader() {
      MenuManager mm = xViewer.getMenuManager();
      setupMenuForHeader(mm);
   }

   public void setupMenuForHeader(MenuManager menuManager) {
      menuManager.add(showColumn);
      menuManager.add(hideColumn);
      menuManager.add(addComputedColumn);
      menuManager.add(copySelectedColumnCells);
      menuManager.add(new Separator());
      menuManager.add(filterBySelColumn);
      menuManager.add(filterByColumn);
      menuManager.add(clearAllFilters);
      menuManager.add(clearAllSorting);
      menuManager.add(new Separator());
      menuManager.add(sumColumn);
      menuManager.add(averageColumn);
      menuManager.add(uniqueValues);
   }

   protected void setupMenuForTable() {
      MenuManager mm = xViewer.getMenuManager();
      setupMenuForTable(mm);
   }

   public void setupMenuForTable(MenuManager menuManager) {
      menuManager.add(new GroupMarker(XViewer.MENU_GROUP_PRE));
      menuManager.add(new Separator());
      menuManager.add(tableProperties);
      menuManager.add(viewTableReport);
      if (xViewer.isColumnMultiEditEnabled()) {
         menuManager.add(columnMultiEdit);
      }
      menuManager.add(viewSelectedCell);
      menuManager.add(copySelectedCell);
      menuManager.add(copySelected);
      menuManager.add(copySelectedColumnCells);
      menuManager.add(new Separator());
      menuManager.add(filterByValue);
      menuManager.add(filterByColumn);
      menuManager.add(clearAllFilters);
      menuManager.add(clearAllSorting);
      if (xViewer.isRemoveItemsMenuOptionEnabled()) {
         menuManager.add(new Separator());
         menuManager.add(removeSelected);
         menuManager.add(removeNonSelected);
      }
      if (xViewer.isDebugLoading()) {
         menuManager.add(new Separator());
         menuManager.add(viewLoadingReport);
      }

      menuManager.add(new GroupMarker(XViewer.MENU_GROUP_POST));
   }

   public void updateEditMenu(MenuManager mm) {
      final Collection<TreeItem> selectedTreeItems = Arrays.asList(xViewer.getTree().getSelection());
      Set<TreeColumn> editableColumns = ColumnMultiEditAction.getEditableTreeColumns(xViewer, selectedTreeItems);
      MenuManager editMenuManager =
         createEditMenuManager(xViewer, XViewerText.get("menu.edit.multi"), selectedTreeItems, editableColumns); //$NON-NLS-1$
      mm.add(editMenuManager);
   }

   public static MenuManager createEditMenuManager(final XViewer xViewer, String name, final Collection<TreeItem> selectedTreeItems, Set<TreeColumn> editableColumns) {
      MenuManager editMenuManager = new MenuManager(name, XViewerText.get("menu.edit")); //$NON-NLS-1$
      if (editableColumns.isEmpty()) {
         editMenuManager.add(new Action(XViewerText.get("menu.edit.no_columns")) { //$NON-NLS-1$

            @Override
            public void run() {
               XViewerLib.popup(XViewerText.get("error"), XViewerText.get("error.no_fields.multi_editable")); //$NON-NLS-1$ //$NON-NLS-2$
            }

         });
      } else {
         Map<String, TreeColumn> nameToColumn = new HashMap<>();
         for (final TreeColumn treeColumn : editableColumns) {
            nameToColumn.put(treeColumn.getText(), treeColumn);
         }
         String[] names = nameToColumn.keySet().toArray(new String[nameToColumn.size()]);
         Arrays.sort(names);
         for (String columnName : names) {
            final TreeColumn treeColumn = nameToColumn.get(columnName);
            if (treeColumn.getData() instanceof XViewerColumn) {
               XViewerColumn xCol = (XViewerColumn) treeColumn.getData();
               if (xCol.isMultiColumnEditable()) {
                  editMenuManager.add(new Action(XViewerText.get("menu.edit") + " " + xCol.getName()) { //$NON-NLS-1$//$NON-NLS-2$

                     @Override
                     public void run() {
                        xViewer.handleColumnMultiEdit(treeColumn, selectedTreeItems);
                     }

                  });
               }
            }
         }
      }
      return editMenuManager;
   }

   public void createTableCustomizationMenuItem(Menu popupMenu) {
      new ActionContributionItem(xViewer.getCustomizeActionWithoutDropDown()).fill(popupMenu, -1);
   }

   public void createViewTableReportMenuItem(Menu popupMenu) {
      setupActions();
      final MenuItem item = new MenuItem(popupMenu, SWT.CASCADE);
      item.setText(XViewerText.get("menu.view_report")); //$NON-NLS-1$
      item.addListener(SWT.Selection, e->viewTableReport.run());
   }

   public void addFilterMenuBlock(Menu popupMenu) {
      createFilterByColumnMenuItem(popupMenu);
      createClearAllFiltersMenuItem(popupMenu);
      createClearAllSortingMenuItem(popupMenu);
   }

   public void createFilterByColumnMenuItem(Menu popupMenu) {
      final MenuItem item = new MenuItem(popupMenu, SWT.CASCADE);
      item.setText(XViewerText.get("menu.column_filter")); //$NON-NLS-1$
      item.addListener(SWT.Selection, e->performFilterByColumn());
   }

   public void createFilterByValueMenuItem(Menu popupMenu) {
      final MenuItem item = new MenuItem(popupMenu, SWT.CASCADE);
      item.setText(XViewerText.get("menu.value_filter")); //$NON-NLS-1$
      item.addListener(SWT.Selection, e->performFilterByValue());
   }

   public void createClearAllFiltersMenuItem(Menu popupMenu) {
      final MenuItem item = new MenuItem(popupMenu, SWT.CASCADE);
      item.setText(XViewerText.get("menu.clear_filters")); //$NON-NLS-1$
      item.addListener(SWT.Selection, e->xViewer.getCustomizeMgr().clearFilters());
   }

   public void createClearAllSortingMenuItem(Menu popupMenu) {
      final MenuItem item = new MenuItem(popupMenu, SWT.CASCADE);
      item.setText(XViewerText.get("menu.clear_sorts")); //$NON-NLS-1$
      item.addListener(SWT.Selection, e-> xViewer.getCustomizeMgr().clearSorter());
   }

   public void addCopyViewMenuBlock(Menu popupMenu) {
      createViewSelectedCellMenuItem(popupMenu);
      createCopyRowsMenuItem(popupMenu);
      createCopyCellsMenuItem(popupMenu);
   }

   public void createCopyRowsMenuItem(Menu popupMenu) {
      final MenuItem item = new MenuItem(popupMenu, SWT.CASCADE);
      item.setText(XViewerText.get("menu.copy_row")); //$NON-NLS-1$
      item.addListener(SWT.Selection, e->performCopy());
   }

   public void createCopyCellsMenuItem(Menu popupMenu) {
      final MenuItem item = new MenuItem(popupMenu, SWT.CASCADE);
      item.setText(XViewerText.get("menu.copy_column")); //$NON-NLS-1$
      item.addListener(SWT.Selection, e->performCopyColumnCells());
   }

   public void createViewSelectedCellMenuItem(Menu popupMenu) {
      setupActions();
      final MenuItem item = new MenuItem(popupMenu, SWT.CASCADE);
      item.setText(XViewerText.get("menu.copy_celldata")); //$NON-NLS-1$
      item.addListener(SWT.Selection, e->copySelectedCell.run());
      final MenuItem item1 = new MenuItem(popupMenu, SWT.CASCADE);
      item1.setText(XViewerText.get("menu.view_celldata")); //$NON-NLS-1$
      item1.addListener(SWT.Selection, e->viewSelectedCell.run());
   }

   private static PatternFilter patternFilter = new XViewerPatternFilter();

   protected void handleShowColumn() {
      TreeColumn insertTreeCol = xViewer.getRightClickSelectedColumn();
      XViewerColumn insertXCol = insertTreeCol != null ? (XViewerColumn) insertTreeCol.getData() : null;
      XCheckFilteredTreeDialog dialog = new XCheckFilteredTreeDialog(XViewerText.get("dialog.show_columns.title"), //$NON-NLS-1$
         XViewerText.get("dialog.show_columns.prompt"), patternFilter, new ArrayTreeContentProvider(), //$NON-NLS-1$
         new XViewerColumnLabelProvider(), new XViewerColumnSorter());
      dialog.setInput(xViewer.getCustomizeMgr().getCurrentTableColumns());
      if (dialog.open() == 0) {
         //         System.out.println("Selected " + dialog.getChecked());
         //         System.out.println("Selected column to add before " + insertXCol);
         CustomizeData custData = xViewer.getCustomizeMgr().generateCustDataFromTable();
         List<XViewerColumn> xCols = custData.getColumnData().getColumns();
         List<XViewerColumn> newXCols = new ArrayList<>();
         // if insert col == null; insert new columns at end; set insert col to first non-shown col
         if (insertXCol == null) {
            for (XViewerColumn currXCol : xCols) {
               if (!currXCol.isShow()) {
                  insertXCol = currXCol;
                  break;
               }
            }
         }
         // else insert before selected insert col
         for (XViewerColumn currXCol : xCols) {
            if (currXCol.equals(insertXCol)) {
               for (Object obj : dialog.getChecked()) {
                  XViewerColumn newXCol = (XViewerColumn) obj;
                  newXCol.setShow(true);
                  newXCols.add(newXCol);
               }
            }
            if (!dialog.getChecked().contains(currXCol)) {
               newXCols.add(currXCol);
            }
         }
         custData.getColumnData().setColumns(newXCols);
         xViewer.getCustomizeMgr().loadCustomization(custData);
         xViewer.refreshColumnsWithPreCompute();
      }
   }

   protected void handleAddComputedColumn() {
      TreeColumn insertTreeCol = xViewer.getRightClickSelectedColumn();
      XViewerColumn insertXCol = (XViewerColumn) insertTreeCol.getData();
      XCheckFilteredTreeDialog dialog = new XCheckFilteredTreeDialog("", //$NON-NLS-1$
         XViewerText.get("dialog.add_column.prompt", insertXCol.getName(), //$NON-NLS-1$
            insertXCol.getId()),
         patternFilter, new ArrayTreeContentProvider(), new XViewerColumnLabelProvider(), new XViewerColumnSorter());
      Collection<XViewerComputedColumn> computedCols = xViewer.getComputedColumns(insertXCol);
      if (computedCols.isEmpty()) {
         XViewerLib.popup(XViewerText.get("error"), XViewerText.get("error.no_computed")); //$NON-NLS-1$ //$NON-NLS-2$
         return;
      }
      dialog.setInput(computedCols);
      if (dialog.open() == 0) {
         //         System.out.println("Selected " + dialog.getChecked());
         //         System.out.println("Selected column to add before " + insertXCol);
         CustomizeData custData = xViewer.getCustomizeMgr().generateCustDataFromTable();
         List<XViewerColumn> xCols = custData.getColumnData().getColumns();
         List<XViewerColumn> newXCols = new ArrayList<>();
         for (XViewerColumn currXCol : xCols) {
            if (currXCol.equals(insertXCol)) {
               for (Object obj : dialog.getChecked()) {
                  XViewerComputedColumn newComputedXCol = ((XViewerComputedColumn) obj).copy();
                  newComputedXCol.setShow(true);
                  newComputedXCol.setSourceXViewerColumn(insertXCol);
                  newComputedXCol.setXViewer(xViewer);
                  newXCols.add(newComputedXCol);
               }
            }
            newXCols.add(currXCol);
         }
         custData.getColumnData().setColumns(newXCols);
         xViewer.getCustomizeMgr().loadCustomization(custData);
         xViewer.refresh();
      }
   }

   protected void handleUniqeValuesColumn() {
      TreeColumn treeCol = xViewer.getRightClickSelectedColumn();
      XViewerColumn xCol = (XViewerColumn) treeCol.getData();

      TreeItem[] items = xViewer.getTree().getSelection();
      if (items.length == 0) {
         items = xViewer.getTree().getItems();
      }
      if (items.length == 0) {
         XViewerLib.popup(XViewerText.get("error"), XViewerText.get("error.no_items.sum")); //$NON-NLS-1$ //$NON-NLS-2$
         return;
      }
      Set<String> values = new HashSet<>();
      for (TreeItem item : items) {
         for (int x = 0; x < xViewer.getTree().getColumnCount(); x++) {
            if (xViewer.getTree().getColumn(x).equals(treeCol)) {
               values.add(((IXViewerLabelProvider) xViewer.getLabelProvider()).getColumnText(item.getData(), x));
            }
         }
      }
      String html = HtmlUtil.simplePage(HtmlUtil.textToHtml(CollectionsUtil.toString("\n", values))); //$NON-NLS-1$
      new HtmlDialog(XViewerText.get("dialog.unique.title"), XViewerText.get("dialog.unique.prompt", xCol.getName()), //$NON-NLS-1$ //$NON-NLS-2$
         html).open();
   }

   protected void handleSumColumn() {
      TreeColumn treeCol = xViewer.getRightClickSelectedColumn();
      XViewerColumn xCol = (XViewerColumn) treeCol.getData();
      if (!xCol.isSummable()) {
         XViewerLib.popup("Invalid Selection for Sum",
            String.format("Sum not available for column [%s]", xCol.getName()));
         return;
      }

      TreeItem[] items = xViewer.getTree().getSelection();
      if (items.length == 0) {
         items = xViewer.getTree().getItems();
      }
      if (items.length == 0) {
         XViewerLib.popup(XViewerText.get("error"), XViewerText.get("error.no_items.sum")); //$NON-NLS-1$ //$NON-NLS-2$
         return;
      }
      List<String> values = new ArrayList<>();
      for (TreeItem item : items) {
         for (int x = 0; x < xViewer.getTree().getColumnCount(); x++) {
            if (xViewer.getTree().getColumn(x).equals(treeCol)) {
               values.add(((IXViewerLabelProvider) xViewer.getLabelProvider()).getColumnText(item.getData(), x));
            }
         }
      }
      XViewerLib.popup(XViewerText.get("menu.sum.prompt"), xCol.sumValues(values)); //$NON-NLS-1$
   }

   protected void handleAverageColumn() {
      TreeColumn treeCol = xViewer.getRightClickSelectedColumn();
      XViewerColumn xCol = (XViewerColumn) treeCol.getData();
      if (!xCol.isSummable()) {
         XViewerLib.popup("Invalid Selection for Average",
            String.format("Average not available for column [%s]", xCol.getName()));
         return;
      }

      TreeItem[] items = xViewer.getTree().getSelection();
      if (items.length == 0) {
         items = xViewer.getTree().getItems();
      }
      if (items.length == 0) {
         XViewerLib.popup(XViewerText.get("error"), XViewerText.get("error.no_items.average")); //$NON-NLS-1$ //$NON-NLS-2$
         return;
      }
      List<String> values = new ArrayList<>();
      for (TreeItem item : items) {
         for (int x = 0; x < xViewer.getTree().getColumnCount(); x++) {
            if (xViewer.getTree().getColumn(x).equals(treeCol)) {
               values.add(((IXViewerLabelProvider) xViewer.getLabelProvider()).getColumnText(item.getData(), x));
            }
         }
      }
      XViewerLib.popup(XViewerText.get("menu.sum.prompt"), xCol.averageValues(values)); //$NON-NLS-1$
   }

   protected void handleHideColumn() {
      TreeColumn insertTreeCol = xViewer.getRightClickSelectedColumn();
      XViewerColumn insertXCol = (XViewerColumn) insertTreeCol.getData();
      //      System.out.println("Hide column " + insertXCol);
      CustomizeData custData = xViewer.getCustomizeMgr().generateCustDataFromTable();
      List<XViewerColumn> xCols = custData.getColumnData().getColumns();
      List<XViewerColumn> newXCols = new ArrayList<>();
      for (XViewerColumn currXCol : xCols) {
         if (currXCol.equals(insertXCol)) {
            currXCol.setShow(false);
         }
         newXCols.add(currXCol);
      }
      custData.getColumnData().setColumns(newXCols);
      xViewer.getCustomizeMgr().loadCustomization(custData);
      xViewer.refresh();
   }

   protected void setupActions() {
      showColumn = new Action(XViewerText.get("menu.show")) { //$NON-NLS-1$
         @Override
         public void run() {
            handleShowColumn();
         }
      };
      addComputedColumn = new Action(XViewerText.get("menu.add")) { //$NON-NLS-1$
         @Override
         public void run() {
            handleAddComputedColumn();
         }
      };
      sumColumn = new Action(XViewerText.get("menu.sum")) { //$NON-NLS-1$
         @Override
         public void run() {
            handleSumColumn();
         }
      };
      averageColumn = new Action(XViewerText.get("menu.average")) { //$NON-NLS-1$
         @Override
         public void run() {
            handleAverageColumn();
         }
      };
      uniqueValues = new Action(XViewerText.get("menu.unique")) { //$NON-NLS-1$
         @Override
         public void run() {
            handleUniqeValuesColumn();
         }
      };
      hideColumn = new Action(XViewerText.get("menu.hide")) { //$NON-NLS-1$
         @Override
         public void run() {
            handleHideColumn();
         }
      };
      removeSelected = new Action(XViewerText.get("menu.remove_selected")) { //$NON-NLS-1$
         @Override
         public void run() {
            performRemoveSelectedRows();
         }
      };
      removeNonSelected = new Action(XViewerText.get("menu.remove_non_selected")) { //$NON-NLS-1$
         @Override
         public void run() {
            performRemoveNonSelectedRows();
         }
      };
      copySelected = new Action(XViewerText.get("menu.copy_row")) { //$NON-NLS-1$
         @Override
         public void run() {
            performCopy();
         }
      };
      viewSelectedCell = new ViewSelectedCellDataAction(xViewer, null, Option.View);
      copySelectedCell = new ViewSelectedCellDataAction(xViewer, clipboard, Option.Copy);
      copySelectedColumnCells = new Action(XViewerText.get("menu.copy_column")) { //$NON-NLS-1$
         @Override
         public void run() {
            performCopyColumnCells();
         };
      };
      clearAllSorting = new Action(XViewerText.get("menu.clear_sorts")) { //$NON-NLS-1$
         @Override
         public void run() {
            xViewer.getCustomizeMgr().clearSorter();
         }
      };
      clearAllFilters = new Action(XViewerText.get("menu.clear_filters")) { //$NON-NLS-1$
         @Override
         public void run() {
            xViewer.getCustomizeMgr().clearFilters();
         }
      };
      filterBySelColumn = new Action(XViewerText.get("menu.column_filter_sel")) { //$NON-NLS-1$
         @Override
         public void run() {
            performFilterBySelColumn();
         }
      };
      filterByColumn = new Action(XViewerText.get("menu.column_filter")) { //$NON-NLS-1$
         @Override
         public void run() {
            performFilterByColumn();
         }
      };
      filterByValue = new Action(XViewerText.get("menu.value_filter")) { //$NON-NLS-1$
         @Override
         public void run() {
            performFilterByValue();
         }
      };
      tableProperties = new TableCustomizationAction(xViewer);
      viewTableReport = new ViewTableReportAction(xViewer);
      viewLoadingReport = new ViewLoadingReportAction(xViewer);
      columnMultiEdit = new ColumnMultiEditAction(xViewer);
   }



   private void performRemoveSelectedRows() {
      try {
         TreeItem[] items = xViewer.getTree().getSelection();
         if (items.length == 0) {
            XViewerLib.popup(XViewerText.get("error"), XViewerText.get("error.no_items")); //$NON-NLS-1$ //$NON-NLS-2$
            return;
         }
         Set<Object> objs = new HashSet<>();
         for (TreeItem item : items) {
            objs.add(item.getData());
         }
         xViewer.remove(objs);
      } catch (Exception ex) {
         XViewerLog.logAndPopup(Activator.class, Level.SEVERE, ex);
      }
   }

   private void performRemoveNonSelectedRows() {
      try {
         TreeItem[] items = xViewer.getTree().getSelection();
         if (items.length == 0) {
            XViewerLib.popup(XViewerText.get("error"), XViewerText.get("error.no_items")); //$NON-NLS-1$ //$NON-NLS-2$
            return;
         }
         Set<Object> keepObjects = new HashSet<>();
         for (TreeItem item : items) {
            keepObjects.add(item.getData());
         }
         xViewer.setInputXViewer(keepObjects);
      } catch (Exception ex) {
         XViewerLog.logAndPopup(Activator.class, Level.SEVERE, ex);
      }
   }

   private void performFilterByValue() {
      TreeColumn treeCol = xViewer.getRightClickSelectedColumn();
      String colId = ((XViewerColumn) treeCol.getData()).getId();
      int colIndex = xViewer.getRightClickSelectedColumnNum();
      TreeItem treeItem = xViewer.getRightClickSelectedItem();
      String cellVal = treeItem.getText(colIndex);
      xViewer.getCustomizeMgr().setColumnFilterText(colId, cellVal);
   }

   private void performFilterByColumn() {
      Set<TreeColumn> visibleColumns = new HashSet<>();
      for (TreeColumn treeCol : xViewer.getTree().getColumns()) {
         if (treeCol.getWidth() > 0) {
            visibleColumns.add(treeCol);
         }
      }
      if (visibleColumns.isEmpty()) {
         XViewerLib.popup(XViewerText.get("error"), XViewerText.get("error.no_columns")); //$NON-NLS-1$ //$NON-NLS-2$
         return;
      }
      ListDialog ld = new ListDialog(xViewer.getTree().getShell()) {
         @Override
         protected Control createDialogArea(Composite container) {
            Control control = super.createDialogArea(container);
            getTableViewer().setSorter(treeColumnSorter);
            return control;
         }
      };
      ld.setMessage(XViewerText.get("dialog.column_filter.title")); //$NON-NLS-1$
      ld.setInput(visibleColumns);
      ld.setLabelProvider(treeColumnLabelProvider);
      ld.setContentProvider(new ArrayContentProvider());
      ld.setTitle(XViewerText.get("dialog.column_filter.title")); //$NON-NLS-1$
      int result = ld.open();
      if (result != 0) {
         return;
      }
      TreeColumn treeCol = (TreeColumn) ld.getResult()[0];
      XViewerColumn col = (XViewerColumn) treeCol.getData();
      xViewer.getColumnFilterDataUI().promptSetFilter(col);

   }

   private void performFilterBySelColumn() {
      XViewerColumn col = (XViewerColumn) xViewer.getRightClickSelectedColumn().getData();
      xViewer.getColumnFilterDataUI().promptSetFilter(col);
   }

   private void performCopyColumnCells() {
      Set<TreeColumn> visibleColumns = new HashSet<>();
      TreeItem[] items = xViewer.getTree().getSelection();
      if (items.length == 0) {
         XViewerLib.popup(XViewerText.get("error"), XViewerText.get("error.no_selection")); //$NON-NLS-1$ //$NON-NLS-2$
         return;
      }
      ArrayList<String> textTransferData = new ArrayList<>();
      IXViewerLabelProvider labelProv = (IXViewerLabelProvider) xViewer.getLabelProvider();
      for (TreeColumn treeCol : xViewer.getTree().getColumns()) {
         if (treeCol.getWidth() > 0) {
            visibleColumns.add(treeCol);
         }
      }
      if (visibleColumns.isEmpty()) {
         XViewerLib.popup(XViewerText.get("error"), XViewerText.get("error.no_columns")); //$NON-NLS-1$ //$NON-NLS-2$
         return;
      }
      ListDialog ld = new ListDialog(xViewer.getTree().getShell()) {
         @Override
         protected Control createDialogArea(Composite container) {
            Control control = super.createDialogArea(container);
            getTableViewer().setSorter(treeColumnSorter);
            return control;
         }
      };
      ld.setMessage(XViewerText.get("dialog.copy_column.title")); //$NON-NLS-1$
      ld.setInput(visibleColumns);
      ld.setLabelProvider(treeColumnLabelProvider);
      ld.setContentProvider(new ArrayContentProvider());
      ld.setTitle(XViewerText.get("dialog.copy_column.title")); //$NON-NLS-1$
      int result = ld.open();
      if (result != 0) {
         return;
      }
      TreeColumn treeCol = (TreeColumn) ld.getResult()[0];
      StringBuilder sb = new StringBuilder();
      for (TreeItem item : items) {
         for (int x = 0; x < xViewer.getTree().getColumnCount(); x++) {
            if (xViewer.getTree().getColumn(x).equals(treeCol)) {
               sb.append(labelProv.getColumnText(item.getData(), x));
               sb.append("\n"); //$NON-NLS-1$
            }
         }
      }
      textTransferData.add(sb.toString());

      if (textTransferData.size() > 0) {
         clipboard.setContents(new Object[] {CollectionsUtil.toString(textTransferData, null, ", ", null)}, //$NON-NLS-1$
            new Transfer[] {TextTransfer.getInstance()});
      }
   }

   private void performCopy() {
      TreeItem[] items = xViewer.getTree().getSelection();
      if (items.length == 0) {
         XViewerLib.popup(XViewerText.get("error"), XViewerText.get("error.no_items")); //$NON-NLS-1$ //$NON-NLS-2$
         return;
      }
      ArrayList<String> textTransferData = new ArrayList<>();
      IXViewerLabelProvider labelProv = (IXViewerLabelProvider) xViewer.getLabelProvider();
      if (items.length > 0) {
         StringBuilder sb = new StringBuilder();
         for (TreeItem item : items) {
            List<String> strs = new ArrayList<>();
            for (int x = 0; x < xViewer.getTree().getColumnCount(); x++) {
               if (xViewer.getTree().getColumn(x).getWidth() > 0) {
                  String data = labelProv.getColumnText(item.getData(), x);
                  if (data != null) {
                     strs.add(data);
                  }
               }
            }
            sb.append(CollectionsUtil.toString("\t", strs)); //$NON-NLS-1$
            sb.append("\n"); //$NON-NLS-1$
         }
         textTransferData.add(sb.toString());

         if (textTransferData.size() > 0) {
            clipboard.setContents(new Object[] {CollectionsUtil.toString(textTransferData, null, ", ", null)}, //$NON-NLS-1$
               new Transfer[] {TextTransfer.getInstance()});
         }
      }
   }

   private void performViewOrCopyCell(Option option) {
      try {
         MouseEvent event = xViewer.getMouseListener().getLeftClickEvent();
         TreeItem item = xViewer.getItemUnderMouseClick(new Point(event.x, event.y));
         TreeColumn column = xViewer.getColumnUnderMouseClick(new Point(event.x, event.y));
         if (item != null && column != null) {
            int columnNum = 0;
            for (int x = 0; x < xViewer.getTree().getColumnCount(); x++) {
               if (xViewer.getTree().getColumn(x).equals(column)) {
                  columnNum = x;
                  break;
               }
            }
            ViewSelectedCellDataAction action = new ViewSelectedCellDataAction(xViewer, clipboard, option);
            action.run(column, item, columnNum);
         }
      } catch (Exception ex) {
         // do nothing
      }
   }

   static LabelProvider treeColumnLabelProvider = new LabelProvider() {
      @Override
      public String getText(Object element) {
         if (element instanceof TreeColumn) {
            return ((TreeColumn) element).getText();
         }
         return XViewerText.get("error.unknown_element"); //$NON-NLS-1$
      }
   };

   static ViewerSorter treeColumnSorter = new ViewerSorter() {
      @SuppressWarnings("unchecked")
      @Override
      public int compare(Viewer viewer, Object e1, Object e2) {
         return getComparator().compare(((TreeColumn) e1).getText(), ((TreeColumn) e2).getText());
      }
   };

}
