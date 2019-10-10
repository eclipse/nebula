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
package org.eclipse.nebula.widgets.xviewer.customize.dialog;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.nebula.widgets.xviewer.Activator;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumnLabelProvider;
import org.eclipse.nebula.widgets.xviewer.XViewerColumnSorter;
import org.eclipse.nebula.widgets.xviewer.XViewerText;
import org.eclipse.nebula.widgets.xviewer.core.model.ColumnFilterData;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.core.model.SortingData;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.core.util.CollectionsUtil;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeDataLabelProvider;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeManager;
import org.eclipse.nebula.widgets.xviewer.util.XViewerException;
import org.eclipse.nebula.widgets.xviewer.util.internal.ArrayTreeContentProvider;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerFilteredTree;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLib;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLog;
import org.eclipse.nebula.widgets.xviewer.util.internal.dialog.DialogWithEntry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Provides dialog for table customization
 *
 * @author Donald G. Dunne
 */
public class XViewerCustomizeDialog extends MessageDialog {
   private static String buttons[] =
      new String[] {XViewerText.get("button.ok"), XViewerText.get("button.apply"), XViewerText.get("button.cancel")}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
   private final XViewer xViewerToCustomize;
   private XViewerFilteredTree custTable;
   protected XViewerFilteredTree hiddenColTable;
   protected XViewerFilteredTree visibleColTable;
   private Text sorterText;
   private Text filterText;
   private Button filterRegExCheckBox;
   private Text columnFilterText;
   // Select Customization Buttons
   Button setDefaultButton, deleteButton;
   // Config Customization Buttons - Moving items
   Button addItemButton, addAllItemButton, removeItemButton, removeAllItemButton, moveUpButton, moveDownButton;
   // Config Customization Buttons
   Button saveButton, renameButton;
   private final static String SET_AS_DEFAULT = XViewerText.get("button.set_default"); //$NON-NLS-1$
   private final static String REMOVE_DEFAULT = XViewerText.get("button.remove_default"); //$NON-NLS-1$
   private String title = XViewerText.get("XViewerCustomizeDialog.title"); //$NON-NLS-1$
   boolean isFeedbackAfter = false;
   boolean isShowSorterBlock = true;
   boolean isShowFilterTextBlock = true;
   boolean isShowColumnFilterTextBlock = true;

   public XViewerCustomizeDialog(XViewer xViewer) {
      this(xViewer, Display.getCurrent().getActiveShell());
   }

   private XViewerCustomizeDialog(XViewer xViewer, Shell parentShell) {
      super(parentShell, "", null, "", MessageDialog.NONE, buttons, 0); //$NON-NLS-1$ //$NON-NLS-2$
      this.xViewerToCustomize = xViewer;
      setShellStyle(getShellStyle() | SWT.RESIZE);
   }

   public void setTitle(String title) {
      this.title = title;
   }

   DragSourceAdapter hiddenTableDragListener = new DragSourceAdapter() {
      @Override
      public void dragStart(DragSourceEvent event) {
         if (hiddenColTable.getViewer().getSelection().isEmpty()) {
            event.doit = false;
         }
      }

      /**
       * @see org.eclipse.swt.dnd.DragSourceAdapter#dragSetData(org.eclipse.swt.dnd.DragSourceEvent)
       */
      @Override
      public void dragSetData(DragSourceEvent event) {
         if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
            List<XViewerColumn> selCols = getHiddenTableSelection();
            Collection<String> ids = new ArrayList<>(selCols.size());

            for (XViewerColumn xCol : selCols) {
               ids.add(xCol.getId());
            }

            event.data = CollectionsUtil.toString(ids, null, ", ", null); //$NON-NLS-1$
         }
      }
   };

   DropTargetAdapter hiddenTableDropListener = new DropTargetAdapter() {

      @Override
      public void dragOperationChanged(DropTargetEvent event) {
         // do nothing
      }

      @Override
      public void drop(DropTargetEvent event) {
         if (event.data instanceof String) {
            performHiddenTableTextDrop(event);
         }
      }

      @Override
      public void dragOver(DropTargetEvent event) {
         performHiddenTableDragOver(event);
      }

      @Override
      public void dropAccept(DropTargetEvent event) {
         // do nothing
      }
   };

   /**
    * Drag should only be from visible table
    */
   public void performHiddenTableDragOver(DropTargetEvent event) {
      if (!TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
         event.detail = DND.DROP_NONE;
         return;
      }
      // Only allow drag from visibleColTable
      if (event.widget != visibleColTable) {
         return;
      }

      event.detail = DND.DROP_MOVE;
   }

   @SuppressWarnings("unchecked")
   public void performHiddenTableTextDrop(DropTargetEvent event) {

      String droppedIds = (String) event.data;

      List<XViewerColumn> droppedVisibleTableXCols = new ArrayList<>();
      List<XViewerColumn> orderCols = (List<XViewerColumn>) visibleColTable.getViewer().getInput();
      for (XViewerColumn xCol : orderCols) {
         if (droppedIds.contains(xCol.getId())) {
            droppedVisibleTableXCols.add(xCol);
         }
      }

      moveFromVisibleToHidden(droppedVisibleTableXCols);
   }

   DragSourceAdapter visibleTableDragListener = new DragSourceAdapter() {
      @Override
      public void dragStart(DragSourceEvent event) {
         if (visibleColTable.getViewer().getSelection().isEmpty()) {
            event.doit = false;
         }
      }

      /**
       * @see org.eclipse.swt.dnd.DragSourceAdapter#dragSetData(org.eclipse.swt.dnd.DragSourceEvent)
       */
      @Override
      public void dragSetData(DragSourceEvent event) {
         if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
            List<XViewerColumn> selCols = getVisibleTableSelection();
            Collection<String> ids = new ArrayList<>(selCols.size());

            for (XViewerColumn xCol : selCols) {
               ids.add(xCol.getId());
            }

            event.data = CollectionsUtil.toString(ids, null, ", ", null); //$NON-NLS-1$
         }
      }
   };
   DropTargetAdapter visibleTableDropListener = new DropTargetAdapter() {

      @Override
      public void dragOperationChanged(DropTargetEvent event) {
         // do nothing
      }

      @Override
      public void drop(DropTargetEvent event) {
         if (event.data instanceof String) {
            performVisibleTableTextDrop(event);
         }
      }

      @Override
      public void dragOver(DropTargetEvent event) {
         performVisibleTableDragOver(event);
      }

      @Override
      public void dropAccept(DropTargetEvent event) {
         // do nothing
      }
   };

   @SuppressWarnings("unchecked")
   public void performVisibleTableTextDrop(DropTargetEvent event) {
      Tree tree = visibleColTable.getViewer().getTree();
      TreeItem dragOverTreeItem = tree.getItem(visibleColTable.getViewer().getTree().toControl(event.x, event.y));

      String droppedIds = (String) event.data;

      // Determine dragOverXCol, if any
      XViewerColumn dragOverXCol = null;
      if (dragOverTreeItem != null) {
         dragOverXCol = (XViewerColumn) dragOverTreeItem.getData();
         // Don't allow dropping on same item as dragging
         if (droppedIds.contains(dragOverXCol.getId())) {
            return;
         }
      }

      List<XViewerColumn> droppedXCols = new ArrayList<>();
      List<XViewerColumn> orderCols = (List<XViewerColumn>) visibleColTable.getViewer().getInput();
      for (XViewerColumn xCol : orderCols) {
         if (droppedIds.contains(xCol.getId())) {
            droppedXCols.add(xCol);
         }
      }
      for (XViewerColumn xCol : (List<XViewerColumn>) hiddenColTable.getViewer().getInput()) {
         if (droppedIds.contains(xCol.getId())) {
            droppedXCols.add(xCol);
         }
      }
      orderCols.removeAll(droppedXCols);

      int dropXColOrderColsIndex = 0;
      for (XViewerColumn xCol : (List<XViewerColumn>) visibleColTable.getViewer().getInput()) {
         if (dragOverXCol != null && xCol.getId().equals(dragOverXCol.getId())) {
            break;
         }
         dropXColOrderColsIndex++;
      }

      if (isFeedbackAfter) {
         orderCols.addAll(dropXColOrderColsIndex + 1, droppedXCols);
      } else {
         orderCols.addAll(dropXColOrderColsIndex, droppedXCols);
      }
      visibleColTable.getViewer().setInput(orderCols);

      List<XViewerColumn> hiddenCols = (List<XViewerColumn>) hiddenColTable.getViewer().getInput();
      hiddenCols.removeAll(droppedXCols);
      hiddenColTable.getViewer().setInput(hiddenCols);
   }

   public void performVisibleTableDragOver(DropTargetEvent event) {
      if (!TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
         event.detail = DND.DROP_NONE;
         return;
      }

      Tree tree = visibleColTable.getViewer().getTree();
      TreeItem dragOverTreeItem = tree.getItem(visibleColTable.getViewer().getTree().toControl(event.x, event.y));
      if (dragOverTreeItem == null) {
         return;
      }

      event.feedback = DND.FEEDBACK_EXPAND;
      event.detail = DND.DROP_NONE;

      IStructuredSelection selectedItem = (IStructuredSelection) visibleColTable.getViewer().getSelection();
      if (selectedItem == null || selectedItem.isEmpty()) {
         selectedItem = (IStructuredSelection) hiddenColTable.getViewer().getSelection();
      }
      if (selectedItem == null) {
         return;
      }
      Object obj = selectedItem.getFirstElement();
      if (obj instanceof XViewerColumn) {
         if (isFeedbackAfter) {
            event.feedback = DND.FEEDBACK_INSERT_AFTER;
         } else {
            event.feedback = DND.FEEDBACK_INSERT_BEFORE;
         }
         event.detail = DND.DROP_MOVE;
      }
   }

   @Override
   protected Control createDialogArea(Composite parent) {
      if (getShell() != null) {
         getShell().setText(title);
      }
      parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      Composite comp = new Composite(parent, SWT.NONE);
      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd.heightHint = 700;
      comp.setLayoutData(gd);
      GridLayout gridLayout_2 = new GridLayout();
      gridLayout_2.numColumns = 2;
      comp.setLayout(gridLayout_2);

      Label namespaceLabel = new Label(comp, SWT.NONE);
      GridData gridData = new GridData(SWT.CENTER, SWT.CENTER, false, false);
      gridData.horizontalSpan = 2;
      namespaceLabel.setLayoutData(gridData);
      namespaceLabel.setText(MessageFormat.format(XViewerText.get("namespace"), //$NON-NLS-1$
         xViewerToCustomize.getXViewerFactory().getNamespace()));

      createSelectCustomizationSection(comp);

      // Column Configuration
      Group configureColumnsGroup = new Group(comp, SWT.NONE);
      configureColumnsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
      configureColumnsGroup.setText(XViewerText.get("XViewerCustomizeDialog.text")); //$NON-NLS-1$
      GridLayout gridLayout = new GridLayout();
      gridLayout.marginWidth = 3;
      gridLayout.marginHeight = 3;
      gridLayout.numColumns = 3;
      configureColumnsGroup.setLayout(gridLayout);

      createHiddenButtonsComposition(configureColumnsGroup);
      createMoveButtons(configureColumnsGroup);
      createVisibleButtonsComposition(configureColumnsGroup);

      gridLayout.numColumns = 3;
      gridLayout.numColumns = 3;

      // Create Sorter and Filter Blocks
      final Composite composite_2 = new Composite(configureColumnsGroup, SWT.NONE);
      composite_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
      final GridLayout gridLayout_3 = new GridLayout();
      gridLayout_3.numColumns = 3;
      composite_2.setLayout(gridLayout_3);

      if (isShowSorterBlock) {
         createSorterTextBlock(composite_2);
      }
      if (isShowFilterTextBlock) {
         createFilterTextBlock(composite_2);
      }
      if (isShowColumnFilterTextBlock) {
         createColumnFilterTextBlock(composite_2);
      }

      createConfigCustomizationButtonBar(composite_2);

      try {
         loadCustomizeTable();
      } catch (Exception ex) {
         XViewerLog.logAndPopup(Activator.class, Level.SEVERE, ex);
      }
      updateButtonEnablements();

      return comp;
   }

   private void createSelectCustomizationSection(Composite comp) {
      // Customization Table and Buttons
      final Composite custComp = new Composite(comp, SWT.NONE);
      final GridData gd_composite_6 = new GridData(SWT.FILL, SWT.FILL, true, true);
      custComp.setLayoutData(gd_composite_6);
      final GridLayout gridLayout_1 = new GridLayout();
      gridLayout_1.marginWidth = 0;
      gridLayout_1.marginHeight = 0;
      custComp.setLayout(gridLayout_1);

      Label selectCustomizationLabel = new Label(custComp, SWT.NONE);
      selectCustomizationLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
      selectCustomizationLabel.setText(XViewerText.get("XViewerCustomizeDialog.prompt")); //$NON-NLS-1$

      // Customization Table
      custTable = new XViewerFilteredTree(custComp, SWT.BORDER);
      final Tree table_2 = custTable.getViewer().getTree();
      final GridData gd_table_2 = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd_table_2.heightHint = 270;
      gd_table_2.widthHint = 200;
      table_2.setLayoutData(gd_table_2);
      custTable.getViewer().setLabelProvider(new CustomizeDataLabelProvider(xViewerToCustomize));
      custTable.getViewer().setContentProvider(new ArrayTreeContentProvider());
      custTable.getViewer().setSorter(new ViewerSorter() {
         @SuppressWarnings("unchecked")
         @Override
         public int compare(Viewer viewer, Object e1, Object e2) {
            if (((CustomizeData) e1).getName().startsWith("-")) { //$NON-NLS-1$
               return -1;
            } else if (((CustomizeData) e2).getName().startsWith("-")) { //$NON-NLS-1$
               return 1;
            } else {
               return getComparator().compare(((CustomizeData) e1).getName(), ((CustomizeData) e2).getName());
            }
         }
      });
		custTable.getViewer().addSelectionChangedListener(event -> {
			handleCustTableSelectionChanged();
			updateButtonEnablements();
			storeCustTableSelection();
		});

      // Customization Table Buttons
      final Composite composite = new Composite(custComp, SWT.NONE);
      composite.setLayoutData(new GridData());
      final GridLayout gridLayout_7 = new GridLayout();
      gridLayout_7.numColumns = 4;
      composite.setLayout(gridLayout_7);

      setDefaultButton = new Button(composite, SWT.NONE);
      setDefaultButton.setLayoutData(new GridData());
      setDefaultButton.setText(SET_AS_DEFAULT);
		setDefaultButton.addListener(SWT.Selection, e -> {
			handleSetDefaultButton();
			updateButtonEnablements();
		});

      deleteButton = new Button(composite, SWT.NONE);
      deleteButton.setLayoutData(new GridData());
      deleteButton.setText(XViewerText.get("button.delete")); //$NON-NLS-1$
      deleteButton.addListener(SWT.Selection, e -> {
            handleDeleteButton();
            updateButtonEnablements();
      });
   }

   private void createConfigCustomizationButtonBar(final Composite composite) {
      // Button block
      final Composite composite_1 = new Composite(composite, SWT.NONE);
      composite_1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
      final GridLayout gridLayout_10 = new GridLayout();
      gridLayout_10.numColumns = 5;
      composite_1.setLayout(gridLayout_10);

      // Customization Buttons
      renameButton = new Button(composite_1, SWT.NONE);
      renameButton.setText(XViewerText.get("button.rename")); //$NON-NLS-1$
      renameButton.addListener(SWT.Selection, e -> handleRenameButton());

      saveButton = new Button(composite_1, SWT.NONE);
      saveButton.setText(XViewerText.get("button.save")); //$NON-NLS-1$
      saveButton.addListener(SWT.Selection, e -> handleSaveButton());
   }

   private void createColumnFilterTextBlock(final Composite composite) {
      // Filter text block
      final Composite composite_8 = new Composite(composite, SWT.NONE);
      composite_8.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
      final GridLayout gridLayout_14 = new GridLayout();
      gridLayout_14.numColumns = 3;
      composite_8.setLayout(gridLayout_14);

      final Label columnFilterLabel = new Label(composite_8, SWT.NONE);
      columnFilterLabel.setText(XViewerText.get("XViewerCustomizeDialog.filter.column")); //$NON-NLS-1$

      columnFilterText = new Text(composite_8, SWT.BORDER);
      columnFilterText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

      final Label clearColumnFilterLabel = new Label(composite_8, SWT.PUSH);
      clearColumnFilterLabel.setImage(XViewerLib.getImage("clear.gif")); //$NON-NLS-1$
      clearColumnFilterLabel.addListener(SWT.MouseUp, e -> columnFilterText.setText("")); //$NON-NLS-1$
   }

   private void createFilterTextBlock(final Composite composite) {
      // Filter text block
      final Composite composite_7 = new Composite(composite, SWT.NONE);
      composite_7.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
      final GridLayout gridLayout_13 = new GridLayout();
      gridLayout_13.numColumns = 5;
      composite_7.setLayout(gridLayout_13);

      final Label filterLabel = new Label(composite_7, SWT.NONE);
      filterLabel.setText(XViewerText.get("XViewerCustomizeDialog.filter.text")); //$NON-NLS-1$

      filterText = new Text(composite_7, SWT.BORDER);
      filterText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

      Label filterLabel2 = new Label(composite_7, SWT.NONE);
      filterLabel2.setText(XViewerText.get("XViewerCustomizeDialog.filter.expression")); //$NON-NLS-1$

      filterRegExCheckBox = new Button(composite_7, SWT.CHECK);
      filterRegExCheckBox.setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, false, false));

      final Label clearFilterLabel = new Label(composite_7, SWT.PUSH);
      clearFilterLabel.setImage(XViewerLib.getImage("clear.gif")); //$NON-NLS-1$
      clearFilterLabel.addListener(SWT.MouseUp, e -> filterText.setText("")); //$NON-NLS-1$

   }

   private void createSorterTextBlock(final Composite composite) {
      final Label sorterLabel = new Label(composite, SWT.NONE);
      sorterLabel.setText(XViewerText.get("XViewerCustomizeDialog.sorter")); //$NON-NLS-1$

      sorterText = new Text(composite, SWT.BORDER);
      sorterText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

      final Label clearSorterLabel = new Label(composite, SWT.PUSH);
      clearSorterLabel.setImage(XViewerLib.getImage("clear.gif")); //$NON-NLS-1$
      clearSorterLabel.addListener(SWT.MouseUp, e -> sorterText.setText("")); //$NON-NLS-1$
   }

   private void createVisibleButtonsComposition(Composite parent) {
      final Composite visibleTableComp = new Composite(parent, SWT.NONE);
      visibleTableComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      final GridLayout gridLayout_6 = new GridLayout();
      gridLayout_6.marginWidth = 0;
      gridLayout_6.marginHeight = 0;
      visibleTableComp.setLayout(gridLayout_6);

      final Label visibleColumnsLabel = new Label(visibleTableComp, SWT.NONE);
      visibleColumnsLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
      visibleColumnsLabel.setText(XViewerText.get("heading.visible")); //$NON-NLS-1$

      // Visible Column Table
      visibleColTable = new XViewerFilteredTree(visibleTableComp);
      final Tree table = visibleColTable.getViewer().getTree();
      final GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd_table.widthHint = 300;
      table.setLayoutData(gd_table);
      visibleColTable.getViewer().setLabelProvider(new XViewerColumnLabelProvider());
      visibleColTable.getViewer().setContentProvider(new ArrayTreeContentProvider());
      visibleColTable.getViewer().addSelectionChangedListener(event ->updateButtonEnablements());

      visibleColTable.getViewer().addDragSupport(DND.DROP_MOVE, new Transfer[] {TextTransfer.getInstance()},
         visibleTableDragListener);
      visibleColTable.getViewer().addDropSupport(DND.DROP_MOVE, new Transfer[] {TextTransfer.getInstance()},
         visibleTableDropListener);
      hiddenColTable.getViewer().addDragSupport(DND.DROP_MOVE, new Transfer[] {TextTransfer.getInstance()},
         hiddenTableDragListener);
      hiddenColTable.getViewer().addDropSupport(DND.DROP_MOVE, new Transfer[] {TextTransfer.getInstance()},
         hiddenTableDropListener);

   }

   private void createHiddenButtonsComposition(Composite parent) {
      Composite hiddenTableComp = new Composite(parent, SWT.NONE);
      hiddenTableComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      GridLayout gridLayout_4 = new GridLayout();
      gridLayout_4.marginWidth = 0;
      gridLayout_4.marginHeight = 0;
      hiddenTableComp.setLayout(gridLayout_4);

      Label hiddenColumnsLabel = new Label(hiddenTableComp, SWT.NONE);
      hiddenColumnsLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
      hiddenColumnsLabel.setText(XViewerText.get("heading.hidden")); //$NON-NLS-1$

      // Hidden Column Table
      hiddenColTable = new XViewerFilteredTree(hiddenTableComp);
      Tree table_1 = hiddenColTable.getViewer().getTree();
      GridData gd_table_1 = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2);
      gd_table_1.widthHint = 300;
      table_1.setLayoutData(gd_table_1);
      hiddenColTable.getViewer().setLabelProvider(new XViewerColumnLabelProvider());
      hiddenColTable.getViewer().setContentProvider(new ArrayTreeContentProvider());
      hiddenColTable.getViewer().setSorter(new XViewerColumnSorter());
      hiddenColTable.getViewer().addSelectionChangedListener(event -> updateButtonEnablements());
   }

   private void createMoveButtons(Composite parent) {
      Composite moveButtonComp = new Composite(parent, SWT.NONE);
      moveButtonComp.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
      GridLayout gridLayout_5 = new GridLayout();
      gridLayout_5.marginWidth = 0;
      gridLayout_5.marginHeight = 0;
      moveButtonComp.setLayout(gridLayout_5);

      addItemButton = new Button(moveButtonComp, SWT.NONE);
      addItemButton.setText(">"); //$NON-NLS-1$
      addItemButton.setToolTipText(XViewerText.get("button.add.tip")); //$NON-NLS-1$
      addItemButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
      addItemButton.addListener(SWT.Selection, e-> handleAddItemButton());

      addAllItemButton = new Button(moveButtonComp, SWT.NONE);
      addAllItemButton.setText(">>"); //$NON-NLS-1$
      addAllItemButton.setToolTipText(XViewerText.get("button.add_all.tip")); //$NON-NLS-1$
      addAllItemButton.addListener(SWT.Selection, e-> handleAddAllItemButton());

      removeItemButton = new Button(moveButtonComp, SWT.NONE);
      removeItemButton.setText("<"); //$NON-NLS-1$
      removeItemButton.setToolTipText(XViewerText.get("button.remove.tip")); //$NON-NLS-1$
      removeItemButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
      removeItemButton.addListener(SWT.Selection, e-> handleRemoveItemButton());

      removeAllItemButton = new Button(moveButtonComp, SWT.NONE);
      removeAllItemButton.setText("<<"); //$NON-NLS-1$
      removeAllItemButton.setToolTipText(XViewerText.get("button.remove_all.tip")); //$NON-NLS-1$
      removeAllItemButton.addListener(SWT.Selection, e-> handleRemoveAllItemButton());

      moveUpButton = new Button(moveButtonComp, SWT.NONE);
      moveUpButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
      moveUpButton.setText("^"); //$NON-NLS-1$
      moveUpButton.setToolTipText(XViewerText.get("button.move_up.tip")); //$NON-NLS-1$
      moveUpButton.addListener(SWT.Selection, e-> handleMoveUpButton());

      moveDownButton = new Button(moveButtonComp, SWT.NONE);
      moveDownButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
      moveDownButton.setText("v"); //$NON-NLS-1$
      moveDownButton.setToolTipText(XViewerText.get("button.move_down.tip")); //$NON-NLS-1$
      moveDownButton.addListener(SWT.Selection, e-> handleMoveDownButton());
   }

   @SuppressWarnings("unchecked")
   private void handleAddItemButton() {
      // Remove from hidden
      List<XViewerColumn> hiddenSelCols = getHiddenTableSelection();
      if (hiddenSelCols == null) {
         return;
      }
      List<XViewerColumn> hiddenCols = (List<XViewerColumn>) hiddenColTable.getViewer().getInput();
      hiddenCols.removeAll(hiddenSelCols);
      hiddenColTable.getViewer().setInput(hiddenCols);

      // Add to visible
      List<XViewerColumn> visibleCols = (List<XViewerColumn>) visibleColTable.getViewer().getInput();
      visibleCols.addAll(hiddenSelCols);
      visibleColTable.getViewer().setInput(visibleCols);
   }

   private void handleRemoveItemButton() {
      List<XViewerColumn> visibleSelCols = getVisibleTableSelection();
      if (visibleSelCols != null && visibleSelCols.size() != 0) {
         moveFromVisibleToHidden(visibleSelCols);
      }
   }

   @SuppressWarnings("unchecked")
   protected void moveFromVisibleToHidden(List<XViewerColumn> visibleSelCols) {
      // Remove from visible
      if (visibleSelCols == null || visibleSelCols.isEmpty()) {
         return;
      }
      List<XViewerColumn> visibleCols = (List<XViewerColumn>) visibleColTable.getViewer().getInput();
      visibleCols.removeAll(visibleSelCols);
      visibleColTable.getViewer().setInput(visibleCols);

      // Add to hidden
      List<XViewerColumn> hiddenCols = (List<XViewerColumn>) hiddenColTable.getViewer().getInput();
      hiddenCols.addAll(visibleSelCols);
      hiddenColTable.getViewer().setInput(hiddenCols);

      updateSortTextField();
      updateColumnFilterField();
   }

   @SuppressWarnings("unchecked")
   private void updateSortTextField() {
      if (sorterText == null) {
         return;
      }
      // get visible column ids
      List<String> visibleColumnIds = new ArrayList<>();
      for (XViewerColumn xCol : (List<XViewerColumn>) visibleColTable.getViewer().getInput()) {
         visibleColumnIds.add(xCol.getId());
      }
      // get current sortIds
      SortingData sortingData = new SortingData(sorterText.getText());
      List<String> currentSortIds = sortingData.getSortingIds();

      // get complement to determine ids that are sorted but not visible == invalid
      for (String invalidId : CollectionsUtil.setComplement(currentSortIds, visibleColumnIds)) {
         sortingData.removeSortingName(invalidId);
      }
      if (sorterText != null && !sorterText.isDisposed()) {
         sorterText.setText(sortingData.getXml());
      }
   }

   @SuppressWarnings("unchecked")
   private void updateColumnFilterField() {
      if (columnFilterText == null) {
         return;
      }
      // get visible column ids
      List<String> visibleColumnIds = new ArrayList<>();
      for (XViewerColumn xCol : (List<XViewerColumn>) visibleColTable.getViewer().getInput()) {
         visibleColumnIds.add(xCol.getId());
      }
      // get current columnFilterIds
      ColumnFilterData columnFilterData = new ColumnFilterData();
      columnFilterData.setFromXml(columnFilterText.getText());
      Set<String> currentSortIds = columnFilterData.getColIds();

      // get complement to determine ids that are sorted but not visible == invalid
      for (String invalidId : CollectionsUtil.setComplement(currentSortIds, visibleColumnIds)) {
         columnFilterData.removeFilterText(invalidId);
      }
      if (columnFilterText != null && !columnFilterText.isDisposed()) {
         columnFilterText.setText(columnFilterData.getXml());
      }
   }

   @SuppressWarnings("unchecked")
   private void handleAddAllItemButton() {

      List<XViewerColumn> hiddenCols = (List<XViewerColumn>) hiddenColTable.getViewer().getInput();

      List<XViewerColumn> visibleCols = (List<XViewerColumn>) visibleColTable.getViewer().getInput();

      visibleCols.addAll(hiddenCols);
      visibleColTable.getViewer().setInput(visibleCols);

      hiddenCols.clear();
      hiddenColTable.getViewer().setInput(hiddenCols);

      updateSortTextField();
      updateColumnFilterField();
   }

   /**
    * for testing purposes
    */
   protected void handleAddAllItem() {
      handleAddAllItemButton();
   }

   @SuppressWarnings("unchecked")
   private void handleRemoveAllItemButton() {

      List<XViewerColumn> visibleCols = (List<XViewerColumn>) visibleColTable.getViewer().getInput();

      List<XViewerColumn> hiddenCols = (List<XViewerColumn>) hiddenColTable.getViewer().getInput();
      hiddenCols.addAll(visibleCols);
      hiddenColTable.getViewer().setInput(hiddenCols);

      // Add to visible
      visibleCols.clear();
      visibleColTable.getViewer().setInput(visibleCols);

      updateSortTextField();
      updateColumnFilterField();
   }

   @SuppressWarnings("unchecked")
   private void handleMoveUpButton() {
      List<XViewerColumn> selCols = getVisibleTableSelection();
      if (selCols == null) {
         return;
      }
      List<XViewerColumn> orderCols = (List<XViewerColumn>) visibleColTable.getViewer().getInput();
      int index = orderCols.indexOf(selCols.iterator().next());
      if (index > 0) {
         orderCols.removeAll(selCols);
         orderCols.addAll(index - 1, selCols);
         visibleColTable.getViewer().setInput(orderCols);
      } else {
         return;
      }
      ArrayList<XViewerColumn> selected = new ArrayList<>();
      selected.addAll(selCols);
      visibleColTable.getViewer().setSelection(new StructuredSelection(selected.toArray(new Object[selected.size()])));
      visibleColTable.getViewer().getTree().setFocus();
      updateButtonEnablements();
   }

   @SuppressWarnings("unchecked")
   private void handleMoveDownButton() {
      List<XViewerColumn> selCols = getVisibleTableSelection();
      if (selCols == null) {
         return;
      }
      List<XViewerColumn> orderCols = (List<XViewerColumn>) visibleColTable.getViewer().getInput();
      int index = orderCols.indexOf(selCols.iterator().next());
      if (index < (orderCols.size() - selCols.size())) {
         orderCols.removeAll(selCols);
         orderCols.addAll(index + 1, selCols);
         visibleColTable.getViewer().setInput(orderCols);
      } else {
         return;
      }
      ArrayList<XViewerColumn> selected = new ArrayList<>();
      selected.addAll(selCols);
      visibleColTable.getViewer().setSelection(new StructuredSelection(selected.toArray(new Object[selected.size()])));
      visibleColTable.getViewer().getTree().setFocus();
      updateButtonEnablements();
   }

   /**
    * @return xColumns from hidden and visible customization lists
    */
   private List<XViewerColumn> getConfigCustXViewerColumns() {
      List<XViewerColumn> xCols = new ArrayList<>();
      List<XViewerColumn> currColumns = xViewerToCustomize.getCustomizeMgr().getCurrentTableColumns();
      for (XViewerColumn xCol : getTableXViewerColumns(visibleColTable.getViewer())) {
         xCol.setShow(true);
         xCol.setXViewer(xViewerToCustomize);

         for (XViewerColumn currColumn : currColumns) {
            if (currColumn.getId().equals(xCol.getId())) {
               if (!currColumn.isSortForward()) {
                  xCol.setSortForward(false);
               }
            }
         }
         xCols.add(xCol);
      }
      for (XViewerColumn xCol : getTableXViewerColumns(hiddenColTable.getViewer())) {
         xCol.setShow(false);
         xCol.setXViewer(xViewerToCustomize);
         xCols.add(xCol);
      }
      return xCols;
   }

   private void handleSaveButton() {
      try {
         List<CustomizeData> custDatas = new ArrayList<>();
         for (CustomizeData custData : xViewerToCustomize.getCustomizeMgr().getSavedCustDatas()) {
            if (custData.isPersonal()) {
               custDatas.add(custData);
            } else if (xViewerToCustomize.getXViewerFactory().isAdmin()) {
               custDatas.add(custData);
            }
         }
         CustomizationDataSelectionDialog diag = new CustomizationDataSelectionDialog(xViewerToCustomize, custDatas);
         if (diag.open() == 0) {
            String name = diag.getEnteredName();
            try {
               CustomizeData diagSelectedCustomizeData = diag.getSelectedCustData();
               String diagEnteredNewName = diag.getEnteredName();
               CustomizeData custData = getConfigCustomizeCustData();
               if (diagEnteredNewName != null) {
                  custData.setName(name);
                  // Set currently selected to newly saved custData
                  selectedCustTableCustData = custData;
               } else {
                  custData.setName(diagSelectedCustomizeData.getName());
                  custData.setGuid(diagSelectedCustomizeData.getGuid());
               }
               custData.setPersonal(!diag.isSaveShared());
               xViewerToCustomize.getCustomizeMgr().saveCustomization(custData);
            } catch (Exception ex) {
               XViewerLog.logAndPopup(Activator.class, Level.SEVERE, ex);
            }
         }
         loadCustomizeTable();
      } catch (Exception ex) {
         XViewerLog.logAndPopup(Activator.class, Level.SEVERE, ex);
      }
   }

   private void handleRenameButton() {
      XViewerColumn xCol = getVisibleTableSelection().iterator().next();
      DialogWithEntry ed = new DialogWithEntry(Display.getCurrent().getActiveShell(), XViewerText.get("button.rename"), //$NON-NLS-1$
         null, XViewerText.get("XViewerCustomizeDialog.rename.new"), //$NON-NLS-1$
         MessageDialog.QUESTION,
         new String[] {
            XViewerText.get("button.ok"), //$NON-NLS-1$
            XViewerText.get("XViewerCustomizeDialog.rename.default"), //$NON-NLS-1$
            XViewerText.get("button.cancel")}, //$NON-NLS-1$
         0);
      int result = ed.open();
      if (result == 2) {
         return;
      }
      if (result == 0) {
         xViewerToCustomize.getCustomizeMgr().customizeColumnName(xCol, ed.getEntry());
      } else if (result == 1) {
         xViewerToCustomize.getCustomizeMgr().customizeColumnName(xCol, ""); //$NON-NLS-1$
      }
      visibleColTable.getViewer().update(xCol, null);
   }

   /**
    * @return CustomizeData represented by the configuration area
    */
   private CustomizeData getConfigCustomizeCustData() {
      CustomizeData custData = new CustomizeData();
      custData.resetGuid();
      custData.setNameSpace(xViewerToCustomize.getXViewerFactory().getNamespace());
      custData.getColumnData().setColumns(getConfigCustXViewerColumns());
      if (sorterText != null) {
         custData.getSortingData().setFromXml(sorterText.getText());
      }
      if (filterText != null && filterRegExCheckBox != null) {
         custData.getFilterData().setFilterText(filterText.getText(), filterRegExCheckBox.getSelection());
      }
      if (columnFilterText != null) {
         custData.getColumnFilterData().setFromXml(columnFilterText.getText());
      }

      CustomizeData custTableSelection = getCustTableSelection();
      if (custData.getColumnData().getColumns().equals(custTableSelection.getColumnData().getColumns())) {
         custData.setName(custTableSelection.getName());
      } else {
         custData.setName("Unsaved Custom");
      }
      return custData;
   }

   private void handleLoadConfigCustButton() {
      xViewerToCustomize.getCustomizeMgr().loadCustomization(getConfigCustomizeCustData());
      xViewerToCustomize.refreshColumnsWithPreCompute();
   }

   /**
    * for testing purposes
    */
   protected void handleLoadConfigCust() {
      handleLoadConfigCustButton();
   }

   private void handleSetDefaultButton() {
      try {
         CustomizeData custData = getCustTableSelection();
         if (custData.getName().equals(CustomizeManager.TABLE_DEFAULT_LABEL) || custData.getName().equals(
            CustomizeManager.CURRENT_LABEL)) {
            XViewerLib.popup(XViewerText.get("error"), XViewerText.get("error.set_default")); //$NON-NLS-1$ //$NON-NLS-2$
            return;
         }
         if (xViewerToCustomize.getCustomizeMgr().isCustomizationUserDefault(custData)) {
            if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
               XViewerText.get("button.remove_default"), //$NON-NLS-1$
               MessageFormat.format(XViewerText.get("XViewerCustomizeDialog.prompt.remove_default"), //$NON-NLS-1$
                  custData.getName()))) {
               xViewerToCustomize.getCustomizeMgr().setUserDefaultCustData(custData, false);
            }
         } else if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
            XViewerText.get("button.set_default"), //$NON-NLS-1$
            MessageFormat.format(XViewerText.get("XViewerCustomizeDialog.prompt.set_default"), custData.getName()))) { //$NON-NLS-1$
            xViewerToCustomize.getCustomizeMgr().setUserDefaultCustData(custData, true);
         }
         loadCustomizeTable();
      } catch (Exception ex) {
         XViewerLog.logAndPopup(Activator.class, Level.SEVERE, ex);
      }
   }

   private void handleDeleteButton() {
      try {
         CustomizeData custSel = getCustTableSelection();
         if (custSel.getName().equals(CustomizeManager.TABLE_DEFAULT_LABEL) || custSel.getName().equals(
            CustomizeManager.CURRENT_LABEL)) {
            XViewerLib.popup(XViewerText.get("error"), XViewerText.get("error.delete_default")); //$NON-NLS-1$ //$NON-NLS-2$
            return;
         }
         if (!custSel.isPersonal() && !xViewerToCustomize.getXViewerFactory().isAdmin()) {
            XViewerLib.popup(XViewerText.get("error"), XViewerText.get("error.delete_global")); //$NON-NLS-1$ //$NON-NLS-2$
            return;
         }
         String dialogTitle = XViewerText.get("XViewerCustomizeDialog.prompt.delete.title"); //$NON-NLS-1$
         String dialogMessage = XViewerText.get("XViewerCustomizeDialog.prompt.delete"); //$NON-NLS-1$
         if (!custSel.isPersonal()) {
            dialogTitle = XViewerText.get("XViewerCustomizeDialog.prompt.delete.shared.title"); //$NON-NLS-1$
            dialogMessage = XViewerText.get("XViewerCustomizeDialog.prompt.delete.shared"); //$NON-NLS-1$
         }
         if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), dialogTitle,
            MessageFormat.format(dialogMessage, custSel.getName()))) {
            xViewerToCustomize.getCustomizeMgr().deleteCustomization(custSel);
            loadCustomizeTable();
            updateButtonEnablements();
         }
      } catch (Exception ex) {
         XViewerLog.logAndPopup(Activator.class, Level.SEVERE, ex);
      }
   }

   private void updateButtonEnablements() {
      CustomizeData custData = getCustTableSelection();
      setDefaultButton.setEnabled(
         xViewerToCustomize.getXViewerFactory().getXViewerCustomizations().isCustomizationPersistAvailable() && custTable.getViewer().getTree().isFocusControl() && custData != null && !custData.getName().equals(
            CustomizeManager.TABLE_DEFAULT_LABEL) && !custData.getName().equals(CustomizeManager.CURRENT_LABEL));
      if (custTable.getViewer().getTree().isFocusControl() && custData != null) {
         try {
            setDefaultButton.setText(xViewerToCustomize.getCustomizeMgr().isCustomizationUserDefault(
               custData) ? REMOVE_DEFAULT : SET_AS_DEFAULT);
         } catch (XViewerException ex) {
            XViewerLog.log(Activator.class, Level.SEVERE, ex);
         }
         setDefaultButton.getParent().layout();
      }
      deleteButton.setEnabled(
         xViewerToCustomize.getXViewerFactory().getXViewerCustomizations().isCustomizationPersistAvailable() && custTable.getViewer().getTree().isFocusControl() && custData != null);
      addItemButton.setEnabled(
         hiddenColTable.getViewer().getTree().isFocusControl() && getHiddenTableSelection() != null);
      removeItemButton.setEnabled(
         visibleColTable.getViewer().getTree().isFocusControl() && getVisibleTableSelection() != null);
      renameButton.setEnabled(
         visibleColTable.getViewer().getTree().isFocusControl() && getVisibleTableSelection() != null && getVisibleTableSelection().size() == 1);
      moveDownButton.setEnabled(
         visibleColTable.getViewer().getTree().isFocusControl() && getVisibleTableSelection() != null);
      moveUpButton.setEnabled(
         visibleColTable.getViewer().getTree().isFocusControl() && getVisibleTableSelection() != null);
      saveButton.setEnabled(
         xViewerToCustomize.getXViewerFactory().getXViewerCustomizations() != null && xViewerToCustomize.getXViewerFactory().getXViewerCustomizations().isCustomizationPersistAvailable());
   }

   private void loadCustomizeTable() throws Exception {
      // Add stored customization data
      List<CustomizeData> custDatas = xViewerToCustomize.getCustomizeMgr().getSavedCustDatas();

      // Add table default customization data
      CustomizeData defaultTableCustData = xViewerToCustomize.getCustomizeMgr().getTableDefaultCustData();
      defaultTableCustData.setName(CustomizeManager.TABLE_DEFAULT_LABEL);
      custDatas.add(defaultTableCustData);

      // Add current customization data generated from actual table
      CustomizeData currentCustData = xViewerToCustomize.getCustomizeMgr().generateCustDataFromTable();
      currentCustData.setName(CustomizeManager.CURRENT_LABEL);
      custDatas.add(currentCustData);

      custTable.getViewer().setInput(custDatas);

      restoreCustTableSelection();

      // If selection not restored, select default
      if (getCustTableSelection() == null) {
         ArrayList<Object> sel = new ArrayList<>();
         sel.add(currentCustData);
         custTable.getViewer().setSelection(new StructuredSelection(sel.toArray(new Object[sel.size()])));
         custTable.getViewer().getTree().setFocus();
      }

      updateSortTextField();
      updateColumnFilterField();
      updateButtonEnablements();
   }

   private CustomizeData getCustTableSelection() {
      IStructuredSelection selection = (IStructuredSelection) custTable.getViewer().getSelection();
      if (selection.isEmpty()) {
         return null;
      }
      Iterator<?> i = selection.iterator();
      return (CustomizeData) i.next();
   }

   protected List<XViewerColumn> getVisibleTableSelection() {
      return getTableSelection(visibleColTable.getViewer());
   }

   protected List<XViewerColumn> getHiddenTableSelection() {
      return getTableSelection(hiddenColTable.getViewer());
   }

   private List<XViewerColumn> getTableSelection(TreeViewer xColTableViewer) {
      List<XViewerColumn> xCols = new ArrayList<>();
      IStructuredSelection selection = (IStructuredSelection) xColTableViewer.getSelection();
      if (selection.isEmpty()) {
         return null;
      }
      Iterator<?> i = selection.iterator();
      while (i.hasNext()) {
         xCols.add((XViewerColumn) i.next());
      }
      return xCols;
   }

   @SuppressWarnings("unchecked")
   private List<XViewerColumn> getTableXViewerColumns(TreeViewer xColTableViewer) {
      return (List<XViewerColumn>) xColTableViewer.getInput();
   }

   private void handleCustTableSelectionChanged() {
      if (getCustTableSelection() == null) {
         return;
      }
      CustomizeData custData = getCustTableSelection();
      if (custData == null) {
         XViewerLog.logAndPopup(Activator.class, Level.SEVERE, new IllegalStateException("Can't obtain selection Xml")); //$NON-NLS-1$
         return;
      }

      List<XViewerColumn> hideXCols = new ArrayList<>();
      List<XViewerColumn> showXCols = new ArrayList<>();
      for (XViewerColumn xCol : custData.getColumnData().getColumns()) {
         if (xCol.isShow()) {
            showXCols.add(xCol);
         } else {
            hideXCols.add(xCol);
         }
      }

      hiddenColTable.getViewer().setInput(hideXCols);
      visibleColTable.getViewer().setInput(showXCols);

      if (sorterText != null) {
         sorterText.setText(custData.getSortingData().getXml());
         sorterText.setData(custData);
      }
      if (filterText != null && filterRegExCheckBox != null) {
         filterText.setText(custData.getFilterData().getFilterText());
         filterText.setData(custData);
         filterRegExCheckBox.setSelection(custData.getFilterData().isRegularExpression());
      }
      if (columnFilterText != null) {
         columnFilterText.setText(custData.getColumnFilterData().getXml());
         columnFilterText.setData(custData);
      }
      updateSortTextField();
      updateColumnFilterField();
   }
   private CustomizeData selectedCustTableCustData = null;

   public void storeCustTableSelection() {
      // Store selected so can re-select after event re-draw
      if (getCustTableSelection() != null) {
         selectedCustTableCustData = getCustTableSelection();
      }
   }

   public void restoreCustTableSelection() {
      if (selectedCustTableCustData != null) {
         ArrayList<Object> selected = new ArrayList<>();
         selected.add(selectedCustTableCustData);
         custTable.getViewer().setSelection(new StructuredSelection(selected.toArray(new Object[selected.size()])));
      }
   }

   @Override
   protected void buttonPressed(int buttonId) {
      // Ok
      if (buttonId == 0) {
         handleLoadConfigCustButton();
         close();
      }
      // Apply
      else if (buttonId == 1) {
         handleLoadConfigCustButton();
      }
      // Cancel
      else {
         close();
      }
   }

   public String getTitle() {
      return title;
   }

   public void setShowSorterBlock(boolean isShowSorterBlock) {
      this.isShowSorterBlock = isShowSorterBlock;
   }

   public void setShowFilterTextBlock(boolean isShowFilterTextBlock) {
      this.isShowFilterTextBlock = isShowFilterTextBlock;
   }

   public void setShowColumnFilterTextBlock(boolean isShowColumnFilterTextBlock) {
      this.isShowColumnFilterTextBlock = isShowColumnFilterTextBlock;
   }

}