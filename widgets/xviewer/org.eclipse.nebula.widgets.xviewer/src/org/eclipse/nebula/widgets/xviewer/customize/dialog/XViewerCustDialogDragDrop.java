/*
 * Created on Nov 2, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.nebula.widgets.xviewer.customize.dialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.core.util.CollectionsUtil;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class XViewerCustDialogDragDrop {

   private final XViewerCustomizeDialog xViewerCustomizeDialog;

   public XViewerCustDialogDragDrop(XViewerCustomizeDialog xViewerCustomizeDialog) {
      this.xViewerCustomizeDialog = xViewerCustomizeDialog;

      xViewerCustomizeDialog.visibleColTable.getViewer().addDragSupport(DND.DROP_MOVE,
         new Transfer[] {TextTransfer.getInstance()}, visibleTableDragListener);
      xViewerCustomizeDialog.visibleColTable.getViewer().addDropSupport(DND.DROP_MOVE,
         new Transfer[] {TextTransfer.getInstance()}, visibleTableDropListener);
      xViewerCustomizeDialog.hiddenColTable.getViewer().addDragSupport(DND.DROP_MOVE,
         new Transfer[] {TextTransfer.getInstance()}, hiddenTableDragListener);
      xViewerCustomizeDialog.hiddenColTable.getViewer().addDropSupport(DND.DROP_MOVE,
         new Transfer[] {TextTransfer.getInstance()}, hiddenTableDropListener);

   }

   DragSourceAdapter hiddenTableDragListener = new DragSourceAdapter() {
      @Override
      public void dragStart(DragSourceEvent event) {
         if (xViewerCustomizeDialog.hiddenColTable.getViewer().getSelection().isEmpty()) {
            event.doit = false;
         }
      }

      /**
       * @see org.eclipse.swt.dnd.DragSourceAdapter#dragSetData(org.eclipse.swt.dnd.DragSourceEvent)
       */
      @Override
      public void dragSetData(DragSourceEvent event) {
         if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
            List<XViewerColumn> selCols = xViewerCustomizeDialog.getHiddenTableSelection();
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
      if (event.widget != xViewerCustomizeDialog.visibleColTable) {
         return;
      }

      event.detail = DND.DROP_MOVE;
   }

   @SuppressWarnings("unchecked")
   public void performHiddenTableTextDrop(DropTargetEvent event) {

      String droppedIds = (String) event.data;

      List<XViewerColumn> droppedVisibleTableXCols = new ArrayList<>();
      List<XViewerColumn> orderCols =
         (List<XViewerColumn>) xViewerCustomizeDialog.visibleColTable.getViewer().getInput();
      for (XViewerColumn xCol : orderCols) {
         if (droppedIds.contains(xCol.getId())) {
            droppedVisibleTableXCols.add(xCol);
         }
      }

      xViewerCustomizeDialog.moveFromVisibleToHidden(droppedVisibleTableXCols);
   }

   DragSourceAdapter visibleTableDragListener = new DragSourceAdapter() {
      @Override
      public void dragStart(DragSourceEvent event) {
         if (xViewerCustomizeDialog.visibleColTable.getViewer().getSelection().isEmpty()) {
            event.doit = false;
         }
      }

      /**
       * @see org.eclipse.swt.dnd.DragSourceAdapter#dragSetData(org.eclipse.swt.dnd.DragSourceEvent)
       */
      @Override
      public void dragSetData(DragSourceEvent event) {
         if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
            List<XViewerColumn> selCols = xViewerCustomizeDialog.getVisibleTableSelection();
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
      Tree tree = xViewerCustomizeDialog.visibleColTable.getViewer().getTree();
      TreeItem dragOverTreeItem =
         tree.getItem(xViewerCustomizeDialog.visibleColTable.getViewer().getTree().toControl(event.x, event.y));

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
      List<XViewerColumn> orderCols =
         (List<XViewerColumn>) xViewerCustomizeDialog.visibleColTable.getViewer().getInput();
      for (XViewerColumn xCol : orderCols) {
         if (droppedIds.contains(xCol.getId())) {
            droppedXCols.add(xCol);
         }
      }
      for (XViewerColumn xCol : (List<XViewerColumn>) xViewerCustomizeDialog.hiddenColTable.getViewer().getInput()) {
         if (droppedIds.contains(xCol.getId())) {
            droppedXCols.add(xCol);
         }
      }
      orderCols.removeAll(droppedXCols);

      int dropXColOrderColsIndex = 0;
      for (XViewerColumn xCol : (List<XViewerColumn>) xViewerCustomizeDialog.visibleColTable.getViewer().getInput()) {
         if (dragOverXCol != null && xCol.getId().equals(dragOverXCol.getId())) {
            break;
         }
         dropXColOrderColsIndex++;
      }

      if (xViewerCustomizeDialog.isFeedbackAfter) {
         orderCols.addAll(dropXColOrderColsIndex + 1, droppedXCols);
      } else {
         orderCols.addAll(dropXColOrderColsIndex, droppedXCols);
      }
      xViewerCustomizeDialog.visibleColTable.getViewer().setInput(orderCols);

      List<XViewerColumn> hiddenCols =
         (List<XViewerColumn>) xViewerCustomizeDialog.hiddenColTable.getViewer().getInput();
      hiddenCols.removeAll(droppedXCols);
      xViewerCustomizeDialog.hiddenColTable.getViewer().setInput(hiddenCols);
   }

   public void performVisibleTableDragOver(DropTargetEvent event) {
      if (!TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
         event.detail = DND.DROP_NONE;
         return;
      }

      Tree tree = xViewerCustomizeDialog.visibleColTable.getViewer().getTree();
      TreeItem dragOverTreeItem =
         tree.getItem(xViewerCustomizeDialog.visibleColTable.getViewer().getTree().toControl(event.x, event.y));
      if (dragOverTreeItem == null) {
         return;
      }

      event.feedback = DND.FEEDBACK_EXPAND;
      event.detail = DND.DROP_NONE;

      IStructuredSelection selectedItem =
         (IStructuredSelection) xViewerCustomizeDialog.visibleColTable.getViewer().getSelection();
      if (selectedItem == null || selectedItem.isEmpty()) {
         selectedItem = (IStructuredSelection) xViewerCustomizeDialog.hiddenColTable.getViewer().getSelection();
      }
      if (selectedItem == null) {
         return;
      }
      Object obj = selectedItem.getFirstElement();
      if (obj instanceof XViewerColumn) {
         if (xViewerCustomizeDialog.isFeedbackAfter) {
            event.feedback = DND.FEEDBACK_INSERT_AFTER;
         } else {
            event.feedback = DND.FEEDBACK_INSERT_BEFORE;
         }
         event.detail = DND.DROP_MOVE;
      }
   }

}
