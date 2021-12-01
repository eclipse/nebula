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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.nebula.widgets.xviewer.action.TableCustomizationAction;
import org.eclipse.nebula.widgets.xviewer.action.TableCustomizationDropDownAction;
import org.eclipse.nebula.widgets.xviewer.column.XViewerDaysTillTodayColumn;
import org.eclipse.nebula.widgets.xviewer.column.XViewerDiffsBetweenColumnsColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.customize.ColumnFilterDataUI;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeManager;
import org.eclipse.nebula.widgets.xviewer.customize.FilterDataUI;
import org.eclipse.nebula.widgets.xviewer.customize.SearchDataUI;
import org.eclipse.nebula.widgets.xviewer.edit.XViewerEditAdapter;
import org.eclipse.nebula.widgets.xviewer.util.Pair;
import org.eclipse.nebula.widgets.xviewer.util.internal.ElapsedTime;
import org.eclipse.nebula.widgets.xviewer.util.internal.ElapsedTime.Units;
import org.eclipse.nebula.widgets.xviewer.util.internal.HtmlUtil;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLib;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLog;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerMenuDetectListener;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerMouseListener;
import org.eclipse.nebula.widgets.xviewer.util.internal.dialog.HtmlDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Donald G. Dunne
 */
public class XViewer extends TreeViewer {

   public static final String MENU_GROUP_PRE = "XVIEWER MENU GROUP PRE"; //$NON-NLS-1$
   public static final String MENU_GROUP_POST = "XVIEWER MENU GROUP POST"; //$NON-NLS-1$
   private StyledText filterText;
   private final MenuManager menuManager;
   private static boolean ctrlKeyDown = false;
   private static boolean altKeyDown = false;
   protected final IXViewerFactory xViewerFactory;
   private final FilterDataUI filterDataUI;
   private final SearchDataUI searchDataUI;
   private final ColumnFilterDataUI columnFilterDataUI;
   private static boolean ctrlKeyListenersSet = false;
   private XViewerGradient xViewerGradient = null;
   private XViewerEditAdapter editAdapter = null;
   private boolean columnMultiEditEnabled = false;
   private CustomizeManager customizeMgr;
   private TreeColumn rightClickSelectedColumn = null;
   private Integer rightClickSelectedColumnNum = null;
   private TreeItem rightClickSelectedItem = null;
   private Color searchColor;
   private boolean forcePend = false;
   private static final Map<Composite, Composite> parentToTopComposites = new HashMap<>();
   private boolean debugLoading = "true".equals(System.getProperty("DebugLoading"));
   private final Map<String, Long> preComputeElapsedTime = new HashMap<>();

   public XViewer(Composite parent, int style, IXViewerFactory xViewerFactory) {
      this(parent, style, xViewerFactory, false, false);
   }

   public XViewer(Tree tree, IXViewerFactory xViewerFactory) {
      this(tree, xViewerFactory, false, false);
   }

   public XViewer(Composite parent, int style, IXViewerFactory xViewerFactory, boolean filterRealTime, boolean searchRealTime) {
      this(new Tree(createTopComposite(xViewerFactory, parent), style), xViewerFactory, filterRealTime, searchRealTime);
   }

   /**
    * Create top if search block should be at top; return parent regardless but cache composite to use
    */
   private static Composite createTopComposite(IXViewerFactory xViewerFactory, Composite parent) {
      if (xViewerFactory.isSearhTop()) {
         Composite topComposite = new Composite(parent, SWT.NONE);
         topComposite.setLayout(XViewerLib.getZeroMarginLayout(11, false));
         topComposite.setLayoutData(new GridData(SWT.FILL, SWT.None, true, false));
         parentToTopComposites.put(parent, topComposite);
      } else {
         parentToTopComposites.put(parent, parent);
      }
      return parent;
   }

   public XViewer(Tree tree, IXViewerFactory xViewerFactory, boolean filterRealTime, boolean searchRealTime) {
      super(tree);
      this.xViewerFactory = xViewerFactory;
      this.menuManager = new MenuManager();
      this.menuManager.setRemoveAllWhenShown(true);
      this.menuManager.createContextMenu(tree.getParent());
      if (xViewerFactory.isFilterUiAvailable()) {
         this.filterDataUI = new FilterDataUI(this, filterRealTime);
      } else {
         this.filterDataUI = null;
      }
      if (xViewerFactory.isSearchUiAvailable()) {
         this.searchDataUI = new SearchDataUI(this, searchRealTime);
      } else {
         this.searchDataUI = null;
      }
      this.columnFilterDataUI = new ColumnFilterDataUI(this);
      try {
         customizeMgr = new CustomizeManager(this, xViewerFactory);
      } catch (Exception ex) {
         XViewerLog.logAndPopup(Activator.class, Level.SEVERE, ex);
      }
      createSupportWidgets(parentToTopComposites.get(tree.getParent()));
      parentToTopComposites.remove(tree.getParent());

      tree.setHeaderVisible(true);
      tree.setLinesVisible(true);
      setUseHashlookup(true);
      setupCtrlKeyListener();
      if (xViewerFactory.isCellGradientOn()) {
         xViewerGradient = new XViewerGradient(this);
      }
   }

   @Override
   protected ColumnViewerEditor createViewerEditor() {
      return super.createViewerEditor();
   }
   private static List<XViewerComputedColumn> computedColumns = new ArrayList<>();

   public Collection<XViewerComputedColumn> getComputedColumns() {
      if (computedColumns.size() == 0) {
         computedColumns.add(new XViewerDaysTillTodayColumn());
         computedColumns.add(new XViewerDiffsBetweenColumnsColumn());
      }
      return computedColumns;
   }

   public Collection<XViewerComputedColumn> getComputedColumns(XViewerColumn xCol) {
      List<XViewerComputedColumn> matchCols = new ArrayList<>();
      for (XViewerColumn computedCol : getComputedColumns()) {
         if (((XViewerComputedColumn) computedCol).isApplicableFor(xCol)) {
            matchCols.add((XViewerComputedColumn) computedCol);
         }
      }
      return matchCols;
   }

   public void dispose() {
      if (filterText != null && !filterText.isDisposed()) {
         filterText.dispose();
      }
      if (searchComp != null && !searchComp.isDisposed()) {
         searchComp.dispose();
      }

      //Remote Display filter
      Display.getCurrent().removeFilter(SWT.KeyDown, displayKeysListener);
      Display.getCurrent().removeFilter(SWT.KeyUp, displayKeysListener);
      Display.getCurrent().removeFilter(SWT.FocusOut, displayFocusListener);

      //Dispose Menu
      if (menuManager != null) {
         menuManager.removeAll();
         menuManager.dispose();
      }
   }

   @Override
   public void setLabelProvider(IBaseLabelProvider labelProvider) {
      if (!(labelProvider instanceof IXViewerLabelProvider)) {
         throw new IllegalArgumentException(
            "Label Provider must extend XViewerLabelProvider or XViewerStyledTextLabelProvider"); //$NON-NLS-1$
      }
      super.setLabelProvider(labelProvider);
   }

   public void addCustomizeToViewToolbar(final ViewPart viewPart) {
      addCustomizeToViewToolbar(viewPart.getViewSite().getActionBars().getToolBarManager());
   }

   public void updateMenuActionsForTable() {
      // provided for subclass implementation
   }

   public void updateMenuActionsForHeader() {
      // provided for subclass implementation
   }

   public Action getCustomizeAction() {
      return new TableCustomizationDropDownAction(this);
   }

   public Action getCustomizeActionWithoutDropDown() {
      return new TableCustomizationAction(this);
   }

   public void addCustomizeToViewToolbar(IToolBarManager toolbarManager) {
      toolbarManager.add(new TableCustomizationDropDownAction(this));
   }

   protected void createSupportWidgets(Composite parent) {
      searchColor = Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);
      searchComp = null;
      if (searchDataUI != null || filterDataUI != null || xViewerFactory.isLoadedStatusLabelAvailable()) {
         searchComp = new Composite(parent, SWT.NONE);
         searchComp.setLayout(XViewerLib.getZeroMarginLayout(11, false));
         searchComp.setLayoutData(new GridData(SWT.FILL, SWT.None, true, false));

         if (filterDataUI != null) {
            filterDataUI.createWidgets(searchComp);
            Label sep1 = new Label(searchComp, SWT.SEPARATOR);
            GridData gd = new GridData(SWT.RIGHT, SWT.NONE, false, false);
            gd.heightHint = 16;
            sep1.setLayoutData(gd);
         }

         if (searchDataUI != null) {
            searchDataUI.createWidgets(searchComp);
            Label sep2 = new Label(searchComp, SWT.SEPARATOR);
            GridData gd = new GridData(SWT.RIGHT, SWT.NONE, false, false);
            gd.heightHint = 16;
            sep2.setLayoutData(gd);
         }

         if (xViewerFactory.isLoadedStatusLabelAvailable()) {
            filterText = new StyledText(searchComp, SWT.NONE);
            filterText.setText(" "); //$NON-NLS-1$
            filterText.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
            filterText.addListener(SWT.MouseUp, getCustomizationMouseListener());
         }
      }

      this.addDoubleClickListener(new IDoubleClickListener() {
         @Override
         public void doubleClick(org.eclipse.jface.viewers.DoubleClickEvent event) {
            handleDoubleClick();
         };
      });
      mouseListener = new XViewerMouseListener(this);
      getTree().addMouseListener(mouseListener);
      getTree().addListener(SWT.MenuDetect, new XViewerMenuDetectListener(this));
      getTree().setMenu(getMenuManager().getMenu());
      columnFilterDataUI.createWidgets();

      customizeMgr.loadCustomization();
   }

   public void handleDoubleClick(TreeColumn col, TreeItem item) {
      // provided for subclass implementation
   }

   public void handleDoubleClick() {
      // provided for subclass implementation
   }

   public int getCurrentColumnWidth(XViewerColumn xCol) {
      for (TreeColumn col : getTree().getColumns()) {
         if (col.getText().equals(xCol.getName())) {
            return col.getWidth();
         }
      }
      return 0;
   }

   /**
    * Called to set the input to the XViewer. This method MUST be used to ensure that XViewer loads properly. Especially
    * with the use of IXViewerPreComputedColumn.
    */
   public final void setInputXViewer(Object input) {
      // Allow pre-computed columns to prior to supplying Viewer with new input
      refreshColumnsWithPreCompute(input);
   }

   /**
    * This is called after all preComputed columns are done loading.
    */
   private void superInputChanged(Object input) {
      if (getTree() != null && !getTree().isDisposed()) {
         super.setInput(input);
      }
   }

   private List<Object> getInputObjects(Object input) {
      List<Object> objects = new LinkedList<>();
      if (input instanceof Collection) {
         Collection<?> collection = (Collection<?>) input;
         for (Object obj : collection) {
            objects.add(obj);
         }
      } else if (input instanceof Object[]) {
         Object[] arr = (Object[]) input;
         for (Object obj : arr) {
            objects.add(obj);
         }
      }
      return objects;
   }

   public void refreshColumnsWithPreCompute() {
      refreshColumnsWithPreCompute(getInput());
   }

   public void refreshColumnsWithPreCompute(final Object input) {
      final List<Object> inputObjects = getInputObjects(input);
      final XViewer xViewer = this;
      this.loading = true;

      if (!inputObjects.isEmpty()) {
         if (forcePend) {
            performPreCompute(inputObjects);
            performLoad(inputObjects, xViewer);
         } else {
            Job job = new Job("Refreshing Columns") {

               @Override
               protected IStatus run(IProgressMonitor monitor) {
                  ElapsedTime time = new ElapsedTime("performPreCompute");
                  performPreCompute(inputObjects);
                  time.end(Units.SEC);
                  return Status.OK_STATUS;
               }

            };
            job.setSystem(false);
            job.addJobChangeListener(new JobChangeAdapter() {

               @Override
               public void done(IJobChangeEvent event) {
                  Display.getDefault().asyncExec(() -> {
                     ElapsedTime time = new ElapsedTime("performLoad");
                     performLoad(input, xViewer);
                     time.end(Units.SEC);
                  });
               }
            });
            job.schedule();
         }
      }
   }

   private void performPreCompute(final List<Object> inputObjects) {
      List<XViewerColumn> currentVisibleTableColumns = getCustomizeMgr().getCurrentVisibleTableColumns();
      for (XViewerColumn column : currentVisibleTableColumns) {
         if (column instanceof IXViewerPreComputedColumn) {
            IXViewerPreComputedColumn preComputedColumn = (IXViewerPreComputedColumn) column;
            if (column.getPreComputedValueMap() == null) {
               column.setPreComputedValueMap(new HashMap<Long, String>(inputObjects.size()));
            } else {
               column.getPreComputedValueMap().clear();
            }
            if (!inputObjects.isEmpty()) {
               try {
                  Long startTime = isDebugLoading() ? (new Date()).getTime() : 0L;
                  preComputedColumn.populateCachedValues(inputObjects, column.getPreComputedValueMap());
                  if (isDebugLoading()) {
                     Long elapsedTime = preComputeElapsedTime.get("PRE - " + column.getName());
                     if (elapsedTime == null) {
                        elapsedTime = (new Date()).getTime() - startTime;
                     } else {
                        elapsedTime += (new Date()).getTime() - startTime;
                     }
                     preComputeElapsedTime.put("PRE - " + column.getName(), elapsedTime);
                  }
               } catch (Exception ex) {
                  XViewerLog.log(Activator.class, Level.SEVERE,
                     String.format("Error performing pre-compute for column %s", column), ex);
               }
            }
         }
      }
   }

   private void performLoad(final Object input, final XViewer xViewer) {
      if (xViewer.getTree() != null && !xViewer.getTree().isDisposed()) {
         xViewer.superInputChanged(input);
         loading = false;
         updateStatusLabel();
      }
   }

   /**
    * Will be called when Alt-Left-Click is done within table cell
    *
    * @return true if handled
    */
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      if (treeColumn.getData() instanceof XViewerColumn) {
         if ((XViewerColumn) treeColumn.getData() instanceof IAltLeftClickProvider) {
            return ((IAltLeftClickProvider) (XViewerColumn) treeColumn.getData()).handleAltLeftClick(treeColumn,
               treeItem);
         }
      }
      return false;
   }

   /**
    * Will be called when click is within the first 18 pixels of the cell rectangle where the icon would be. This method
    * will be called in addition to handleLeftClick since both are true.
    *
    * @return true if handled
    */
   public boolean handleLeftClickInIconArea(TreeColumn treeColumn, TreeItem treeItem) {
      return false;
   }

   /**
    * Will be called when a cell obtains a mouse left-click. This method will be called in addition to
    * handleLeftClickInIconArea if both are true
    */
   public boolean handleLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      return false;
   }

   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      if (treeColumn.getData() instanceof XViewerColumn) {
         if ((XViewerColumn) treeColumn.getData() instanceof IMultiColumnEditProvider) {
            ((IMultiColumnEditProvider) (XViewerColumn) treeColumn.getData()).handleColumnMultiEdit(treeColumn,
               treeItems);
         }
      }
   }

   public boolean isColumnMultiEditable(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      if (!isColumnMultiEditEnabled()) {
         return false;
      }
      return (((XViewerColumn) treeColumn.getData()).isMultiColumnEditable());
   }

   /**
    * If true, "Remove Selected from View" and "Remove Non-Selected from View" menu options will be shown.
    */
   public boolean isRemoveItemsMenuOptionEnabled() {
      return true;
   }

   public XViewerColumn getXTreeColumn(int columnIndex) {
      return (XViewerColumn) getTree().getColumn(columnIndex).getData();
   }

   // Only create one listener for all XViewers
   private void setupCtrlKeyListener() {
      if (ctrlKeyListenersSet == false) {
         ctrlKeyListenersSet = true;
         Display.getCurrent().addFilter(SWT.KeyDown, displayKeysListener);
         Display.getCurrent().addFilter(SWT.KeyUp, displayKeysListener);
         Display.getCurrent().addFilter(SWT.FocusOut, displayFocusListener);
      }
   }
   Listener displayKeysListener = event -> {
      if (event.keyCode == SWT.CTRL) {
         if (event.type == SWT.KeyDown) {
            ctrlKeyDown = true;
         } else if (event.type == SWT.KeyUp) {
            ctrlKeyDown = false;
         }
      }
      if (event.keyCode == SWT.ALT) {
         if (event.type == SWT.KeyDown) {
            altKeyDown = true;
         } else if (event.type == SWT.KeyUp) {
            altKeyDown = false;
         }
      }
   };
   Listener displayFocusListener = event -> {
      // Clear when focus is lost
      ctrlKeyDown = false;
      altKeyDown = false;
   };
   private Composite searchComp;
   private XViewerMouseListener mouseListener;
   private boolean loading;

   public void resetDefaultSorter() {
      customizeMgr.resetDefaultSorter();
   }

   /**
    * Override this method if need to perform other tasks upon remove
    */
   @SuppressWarnings("unchecked")
   public void remove(Collection<Object> objects) {
      Object input = getInput();
      if (input instanceof Collection<?>) {
         Collection<Object> inputObj = (Collection<Object>) getInput();
         for (Object obj : objects) {
            super.remove(obj);
            inputObj.remove(obj);
         }
      }
   }

   /**
    * setInputXViewer(Object input) should be called for setting input to XViewer.
    *
    * @param objects
    */
   @Deprecated
   public void load(Collection<Object> objects) {
      setInputXViewer(objects);
   }

   @Override
   public void setSorter(ViewerSorter sorter) {
      super.setSorter(sorter);
      updateStatusLabel();
   }

   public MenuManager getMenuManager() {
      return this.menuManager;
   }

   public int getVisibleItemCount(TreeItem items[]) {
      int cnt = items.length;
      for (TreeItem item : items) {
         if (item.getExpanded()) {
            cnt += getVisibleItemCount(item.getItems());
         }
      }
      return cnt;
   }

   public int getVisibleItemCount() {
      TreeItem[] items = getTree().getItems();
      int cnt = items.length;
      for (TreeItem item : items) {
         if (item.getExpanded()) {
            cnt += getVisibleItemCount(item.getItems());
         }
      }
      return cnt;
   }

   public List<TreeItem> getVisibleItems() {
      List<TreeItem> toReturn = new ArrayList<>();
      getVisibleItems(toReturn, getTree().getItems());
      return toReturn;
   }

   private void getVisibleItems(List<TreeItem> toReturn, TreeItem items[]) {
      for (TreeItem item : items) {
         toReturn.add(item);
         if (item.getExpanded()) {
            getVisibleItems(toReturn, item.getItems());
         }
      }
   }

   @Override
   public void refresh() {
      if (getTree() == null || getTree().isDisposed()) {
         return;
      }
      super.refresh();
      // do not need to updateStatusLabel cause super.refresh will call refresh(element);
   }

   public boolean isFiltered() {
      return getFilters().length > 0;
   }

   @Override
   public void refresh(boolean updateLabels) {
      super.refresh(updateLabels);
      // do not need to updateStatusLabel cause super.refresh will call refresh(element, updateLabels);
   }

   @Override
   public void refresh(Object element, boolean updateLabels) {
      super.refresh(element, updateLabels);
      updateStatusLabel();
   }

   @Override
   public void refresh(Object element) {
      super.refresh(element);
      updateStatusLabel();
   }

   /**
    * Override this to add information to the status string. eg. extra filters etc.
    */
   public String getStatusString() {
      return ""; //$NON-NLS-1$
   }

   public void updateStatusLabel() {
      if (!xViewerFactory.isLoadedStatusLabelAvailable()) {
         return;
      }
      if (getTree().isDisposed() || filterText.isDisposed()) {
         return;
      }
      StringBuilder sb = new StringBuilder();
      boolean allItemsFiltered = false;
      boolean filtered = false;

      if (loading) {
         sb.append("Loading...");
      } else {
         // Status Line 1
         int loadedNum = 0;
         int visibleNum = getVisibleItemCount(getTree().getItems());
         if (getRoot() != null && ((ITreeContentProvider) getContentProvider()) != null) {
            loadedNum = ((ITreeContentProvider) getContentProvider()).getChildren(getRoot()).length;
         }
         allItemsFiltered = loadedNum > 0 && visibleNum == 0;
         if (allItemsFiltered) {
            sb.append(XViewerText.get("status.all_filtered")); //$NON-NLS-1$
         }

         filtered = visibleNum < loadedNum;
         if (!allItemsFiltered && filtered) {
            sb.append(XViewerText.get("status.filtered")); //$NON-NLS-1$
         }
         sb.append(MessageFormat.format(XViewerText.get("status"), loadedNum, visibleNum, //$NON-NLS-1$
            ((IStructuredSelection) getSelection()).size()));
         customizeMgr.appendToStatusLabel(sb);
         if (filterDataUI != null) {
            filterDataUI.appendToStatusLabel(sb);
         }
         columnFilterDataUI.appendToStatusLabel(sb);
         sb.append(getStatusString());
         if (sb.length() > 0) {
            sb.append("\n"); //$NON-NLS-1$
         }

         // Status Line 2
         customizeMgr.getSortingStr(sb);
      }

      // Display status lines
      String str = sb.toString();
      filterText.setText(str);
      filterText.getParent().getParent().layout();
      filterText.setToolTipText(str);
      filterText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
      filterText.setWordWrap(false);
      FontDescriptor boldDescriptor = FontDescriptor.createFrom(filterText.getFont()).setStyle(SWT.BOLD);
      filterText.setFont(boldDescriptor.createFont(Display.getCurrent()));

      if (loading) {
         filterText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
      } else if (allItemsFiltered) {
         StyleRange filterStyleRange = new StyleRange();
         filterStyleRange.start = 0;
         filterStyleRange.length = 18;
         filterStyleRange.foreground = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
         filterText.setStyleRange(filterStyleRange);
      } else if (filtered && !allItemsFiltered) {
         StyleRange filterStyleRange = new StyleRange();
         filterStyleRange.start = 0;
         filterStyleRange.length = 8;
         filterStyleRange.foreground = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
         filterText.setStyleRange(filterStyleRange);
      } else {
         filterText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
      }
   }

   public void setLoading(boolean loading) {
      this.loading = loading;
      updateStatusLabel();
   }

   private Listener getCustomizationMouseListener() {
      return event -> {
         if (event.button == 3 && event.count == 1) {
            CustomizeData custData = getCustomizeMgr().getCurrentCustomizeData();
            List<XViewerColumn> currentVisibleTableColumns = getCustomizeMgr().getCurrentVisibleTableColumns();
            custData.getColumnData().getColumns().clear();
            custData.getColumnData().getColumns().addAll(currentVisibleTableColumns);
            String custStr = custData.toString();
            custStr = custStr.replaceAll("XView", "\nXView");
            custStr = custStr.replaceFirst("guid", "\nguid");
            String html = HtmlUtil.simplePage(HtmlUtil.getPreData(custStr));
            String title = String.format("Customization [%s]-[%s]", custData.getName(), custData.getGuid());
            new HtmlDialog(title, title, html).open();
         }
      };
   }

   public String getViewerNamespace() {
      return getXViewerFactory().getNamespace();
   }

   public IXViewerFactory getXViewerFactory() {
      return xViewerFactory;
   }

   public StyledText getStatusLabel() {
      return filterText;
   }

   public FilterDataUI getFilterDataUI() {
      return filterDataUI;
   }

   public boolean isColumnMultiEditEnabled() {
      return columnMultiEditEnabled;
   }

   public void setColumnMultiEditEnabled(boolean columnMultiEditEnabled) {
      this.columnMultiEditEnabled = columnMultiEditEnabled;
   }

   public void setEnabled(boolean arg) {
      this.getControl().setEnabled(arg);
   }

   public TreeColumn getRightClickSelectedColumn() {
      return rightClickSelectedColumn;
   }

   public TreeItem getRightClickSelectedItem() {
      return rightClickSelectedItem;
   }

   public Integer getRightClickSelectedColumnNum() {
      return rightClickSelectedColumnNum;
   }

   public CustomizeManager getCustomizeMgr() {
      return customizeMgr;
   }

   public boolean isCtrlKeyDown() {
      return ctrlKeyDown;
   }

   public boolean isAltKeyDown() {
      return altKeyDown;
   }

   boolean searchMatch(String text) {
      if (searchDataUI == null) {
         return false;
      }
      return searchDataUI.match(text);
   }

   Color getSearchMatchColor() {
      return searchColor;
   }

   public boolean isSearch() {
      if (searchDataUI == null) {
         return false;
      }
      return searchDataUI.isSearch();
   }

   public String getColumnText(Object element, int col) {
      return ((IXViewerLabelProvider) getLabelProvider()).getColumnText(element, col);
   }

   /**
    * Mouse clicks can happen in table via XViewerMouseListener or in menu area via XViewerMenuDetectListener. Both are
    * processed here to use in UI
    */
   public void processRightClickMouseEvent(Point point) {
      rightClickSelectedColumn = null;
      rightClickSelectedColumnNum = null;
      rightClickSelectedItem = null;

      rightClickSelectedItem = getItemUnderMouseClick(point);
      rightClickSelectedColumn = getColumnUnderMouseClick(point);
      rightClickSelectedColumnNum = getColumnNumberUnderMouseClick(point);
   }

   public TreeColumn getColumnUnderMouseClick(Point point) throws ArrayIndexOutOfBoundsException {
      Integer columnNumber = getColumnNumberUnderMouseClick(point);
      if (columnNumber == null) {
         return null;
      }
      return getTree().getColumn(columnNumber);
   }

   public Integer getColumnNumberUnderMouseClick(Point point) {
      int[] columnOrder = getTree().getColumnOrder();
      int sum = 0;
      int columnCount = 0;
      for (int column : columnOrder) {
         TreeColumn col = getTree().getColumn(column);
         sum = sum + col.getWidth();
         if (sum > point.x) {
            break;
         }
         columnCount++;
      }

      if (columnCount > columnOrder.length - 1) {
         return null;
      }
      return columnOrder[columnCount];
   }

   public TreeItem getItemUnderMouseClick(Point point) throws ArrayIndexOutOfBoundsException {
      TreeItem itemToReturn = getTree().getItem(point);

      if (itemToReturn == null) {
         int sum;
         sum = 0;
         TreeItem[] allItems = getTree().getItems();
         for (TreeItem item : allItems) {
            sum = sum + getTree().getItemHeight();
            if (sum > point.y) {
               itemToReturn = item;
               break;
            }
         }
      }
      return itemToReturn;
   }

   /**
    * Refresh only single column using normal label provider mechanism. This can be called after normal loading and
    * after columns compute their input in the background.
    */
   public void refreshColumn(XViewerColumn column) {
      refreshColumn(column.getId());
   }

   /**
    * Refresh only single column using normal label provider mechanism. This can be called after normal loading and
    * after columns compute their input in the background.
    */
   public void refreshColumn(String columnId) {
      Pair<XViewerColumn, Integer> column = getCustomizeMgr().getColumnNumFromXViewerColumn(columnId);
      IBaseLabelProvider baseLabelProvider = getLabelProvider();
      if (baseLabelProvider instanceof XViewerLabelProvider) {
         XViewerLabelProvider labelProvider = (XViewerLabelProvider) baseLabelProvider;

         TreeItem[] items = getTree().getItems();
         for (TreeItem item : items) {

            ViewerRow viewerRow = getViewerRowFromItem(item);
            if (viewerRow != null) {
               try {
                  ViewerCell cell = viewerRow.getCell(column.getSecond());
                  String value = null;
                  try {
                     value = labelProvider.getColumnText(item.getData(), column.getSecond());
                  } catch (Exception ex) {
                     value = String.format("Exception getting value from column [%s][%s]", column.getFirst().getId(),
                        ex.getLocalizedMessage());
                  }
                  if (value != null) {
                     cell.setText(value);
                  }
               } catch (NullPointerException ex) {
                  // do nothing
               }
            }
         }
      }
   }

   /**
    * Override to provide extended filter capabilities
    */
   public XViewerTextFilter getXViewerTextFilter() {
      return new XViewerTextFilter(this);
   }

   public XViewerGradient getxViewerGradient() {
      return xViewerGradient;
   }

   public void setxViewerGradient(XViewerGradient xViewerGradient) {
      this.xViewerGradient = xViewerGradient;
   }

   public void setXViewerEditAdapter(XViewerEditAdapter editAdapter) {
      this.editAdapter = editAdapter;
      this.editAdapter.activate(this);
   }

   public ColumnFilterDataUI getColumnFilterDataUI() {
      return columnFilterDataUI;
   }

   public XViewerMouseListener getMouseListener() {
      return mouseListener;
   }

   public boolean isForcePend() {
      return forcePend;
   }

   public void setForcePend(boolean forcePend) {
      this.forcePend = forcePend;
   }

   public boolean isDebugLoading() {
      return debugLoading;
   }

   public void setDebugLoading(boolean debugLoading) {
      this.debugLoading = debugLoading;
   }

   public Map<String, Long> getPreComputeElapsedTime() {
      return preComputeElapsedTime;
   }

}
