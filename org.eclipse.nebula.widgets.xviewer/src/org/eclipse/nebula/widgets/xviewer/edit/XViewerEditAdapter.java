package org.eclipse.nebula.widgets.xviewer.edit;

import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Adapter to edit cells in the XViewer <br>
 * <br>
 * To edit your columns the columns must be ExtendedViewerColumns. <br>
 * Use the map in the ExtendedViewerColumn class to define the cells
 * 
 * @author Juergen Reichl
 */
public class XViewerEditAdapter {

   private XViewer xv;
   private ViewerCell klickedCell;
   private TreeColumn klickedColumn;

   private final XViewerControlFactory factory;
   private final XViewerConverter converter;

   /**
    * TODO MouseDoubleClick and MouseUp not implemented yet swtEvent - SWT.MouseDoubleClick or SWT.MouseDown or
    * SWT.MouseUp
    */
   private int swtEvent = 0;
   private MyMouseListener mouseListener = null;
   private Listener selListener = null;

   public XViewerEditAdapter(XViewerControlFactory factory, XViewerConverter converter) {
      this.factory = factory;
      this.converter = converter;

      this.swtEvent = SWT.MouseDown;
   }

   public void activate(final XViewer xv) {
      this.xv = xv;

      mouseListener = new MyMouseListener(swtEvent);
      xv.getTree().addMouseListener(mouseListener);

      selListener = new MyListener();
      xv.getTree().addListener(SWT.Selection, selListener);
   }

   private class MyMouseListener implements MouseListener {
      private int swtStyle = 0;

      public MyMouseListener(int swtStyle) {
         this.swtStyle = swtStyle;
      }

      @Override
      public void mouseDoubleClick(MouseEvent e) {
         // not supported yet!
         if (swtStyle == SWT.MouseDoubleClick) {
            klickedColumn = xv.getColumnUnderMouseClick(new Point(e.x, e.y));
            klickedCell = xv.getCell(new Point(e.x, e.y));
         }
      }

      @Override
      public void mouseDown(MouseEvent e) {
         if (swtStyle == SWT.MouseDown) {
            klickedColumn = xv.getColumnUnderMouseClick(new Point(e.x, e.y));
            klickedCell = xv.getCell(new Point(e.x, e.y));
         }
      }

      @Override
      public void mouseUp(MouseEvent e) {
         // not supported yet!			
         if (swtStyle == SWT.MouseUp) {
            klickedColumn = xv.getColumnUnderMouseClick(new Point(e.x, e.y));
            klickedCell = xv.getCell(new Point(e.x, e.y));
         }
      }

   }

   private class MyListener implements Listener {

      private void doHandleEvent(Event event) {
         handleEvent(event);
      }

      @Override
      public void handleEvent(Event event) {
         if (klickedColumn == null || klickedCell == null) {
            return;
         }

         final Control c;
         try {
            XViewerColumn xColumn =
               xv.getXViewerFactory().getDefaultXViewerColumn(((XViewerColumn) klickedColumn.getData()).getId());
            if (xColumn instanceof ExtendedViewerColumn) {
               ExtendedViewerColumn extendedColumn = (ExtendedViewerColumn) xColumn;
               CellEditDescriptor ced =
                  extendedColumn.getCellEditDescriptorMap().get(klickedCell.getElement().getClass());
               if (ced != null) {
                  if (ced.getControl() == null) {
                     return;
                  }
                  if (ced.getAction() != null) {
                     if (!ced.getAction().isEnabled()) {
                        return;
                     }
                  }
                  c = factory.createControl(ced, xv);
                  if (c == null) {
                     return;
                  }
               } else {
                  return;
               }
            } else {
               return;
            }

            if (((TreeItem) event.item) != null) {
               Listener myListener = new Listener() {
                  @Override
                  public void handleEvent(final Event e) {
                     switch (e.type) {
                        case SWT.FocusOut:
                           // set new value
                           getInput(c);
                           c.dispose();
                           break;
                        case SWT.Verify:
                           c.setBounds(klickedCell.getBounds());
                           break;
                        case SWT.Traverse:
                           boolean neighbor = false;
                           switch (e.detail) {
                              case SWT.TRAVERSE_RETURN:
                                 // set new value
                                 getInput(c);
                                 //$FALL-THROUGH$
                              case SWT.TRAVERSE_ESCAPE:
                                 c.dispose();
                                 e.doit = false;
                                 break;
                              case SWT.TRAVERSE_TAB_NEXT:
                                 getInput(c);
                                 neighbor = getNeighbor(ViewerCell.RIGHT, true);
                                 e.doit = false;
                                 c.dispose();
                                 Event eN = new Event();
                                 eN.type = SWT.Selection;
                                 eN.widget = xv.getTree();
                                 if (neighbor) {
                                    eN.item = klickedCell.getItem();
                                 }
                                 doHandleEvent(eN);
                                 break;
                              case SWT.TRAVERSE_TAB_PREVIOUS:
                                 getInput(c);
                                 neighbor = getNeighbor(ViewerCell.LEFT, true);
                                 e.doit = false;
                                 c.dispose();
                                 Event eP = new Event();
                                 eP.type = SWT.Selection;
                                 eP.widget = xv.getTree();
                                 if (neighbor) {
                                    eP.item = klickedCell.getItem();
                                 }
                                 doHandleEvent(eP);
                                 break;
                           }
                     }
                  }

               };
               c.addListener(SWT.FocusOut, myListener);
               c.addListener(SWT.Traverse, myListener);
               c.addListener(SWT.Verify, myListener);
               // set old value
               setInput(c);
               c.setFocus();
            }
         } catch (Exception ex) {
            return;
         }
      }

      private boolean getNeighbor(int directionMask, boolean sameLevel) {
         try {
            if (klickedCell == null) {
               return false;
            }
            Point cellPosition = new Point(klickedCell.getBounds().x, klickedCell.getBounds().y);
            klickedCell = xv.getCell(cellPosition).getNeighbor(directionMask, sameLevel);
            klickedColumn =
               xv.getColumnUnderMouseClick(new Point(klickedCell.getBounds().x, klickedCell.getBounds().y));
            XViewerColumn xColumn =
               xv.getXViewerFactory().getDefaultXViewerColumn(((XViewerColumn) klickedColumn.getData()).getId());
            if (xColumn instanceof ExtendedViewerColumn) {
               ExtendedViewerColumn extendedColumn = (ExtendedViewerColumn) xColumn;
               CellEditDescriptor ced =
                  extendedColumn.getCellEditDescriptorMap().get(klickedCell.getElement().getClass());
               if (ced == null) {
                  return getNeighbor(directionMask, sameLevel);
               }
               if (ced.getControl() == null) {
                  return getNeighbor(directionMask, sameLevel);
               }
            } else {
               return getNeighbor(directionMask, sameLevel);
            }
            return true;
         } catch (Exception ex) {
            return false;
         }
      }
   }

   private static boolean InInput = false;

   private void getInput(Control c) {
      if (InInput) {
         return;
      }
      if (klickedCell == null) {
         return;
      }
      XViewerColumn xCol =
         xv.getXViewerFactory().getDefaultXViewerColumn(((XViewerColumn) klickedColumn.getData()).getId());
      if (xCol instanceof ExtendedViewerColumn) {
         ExtendedViewerColumn extendedCol = (ExtendedViewerColumn) xCol;
         CellEditDescriptor ced = extendedCol.getCellEditDescriptorMap().get(klickedCell.getElement().getClass());
         if (ced == null || ced.getControl() == null) {
            return;
         }
         InInput = true;
         try {
            converter.getInput(c, ced, klickedCell.getElement());
            xv.refresh(klickedCell.getElement());
         } catch (Exception ex) {
            InInput = false;
         }
         InInput = false;
      }
   }

   private void setInput(Control c) {
      if (klickedCell == null) {
         return;
      }
      XViewerColumn xCol =
         xv.getXViewerFactory().getDefaultXViewerColumn(((XViewerColumn) klickedColumn.getData()).getId());
      if (xCol instanceof ExtendedViewerColumn) {
         ExtendedViewerColumn extendedCol = (ExtendedViewerColumn) xCol;
         CellEditDescriptor ced = extendedCol.getCellEditDescriptorMap().get(klickedCell.getElement().getClass());
         if (ced == null || ced.getControl() == null) {
            return;
         }
         converter.setInput(c, ced, klickedCell.getElement());
      }
      c.setBounds(klickedCell.getBounds());
   }
}
