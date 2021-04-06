package org.eclipse.nebula.widgets.xviewer.edit;

import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
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

   XViewer xv;
   ViewerCell klickedCell;
   TreeColumn klickedColumn;

   int orientationStyle;

   final XViewerControlFactory factory;
   final XViewerConverter converter;

   /**
    * TODO MouseDoubleClick and MouseUp not implemented yet swtEvent - SWT.MouseDoubleClick or SWT.MouseDown or
    * SWT.MouseUp
    */
   private int swtEvent = 0;
   private MyMouseListener mouseListener = null;

   public XViewerEditAdapter(XViewerControlFactory factory, XViewerConverter converter) {
      this.factory = factory;
      this.converter = converter;
      this.orientationStyle = SWT.RIGHT;

      this.swtEvent = SWT.MouseDown;
   }

   public void activate(final XViewer xv) {
      this.xv = xv;

      mouseListener = new MyMouseListener(swtEvent);
      xv.getTree().addMouseListener(mouseListener);

      xv.getTree().addListener(SWT.Selection, event -> handleEditEvent(event));
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

   private void doHandleEvent(Event event) {
      handleEditEvent(event);
   }

   boolean handleEditEvent(Event event) {
      if (klickedColumn == null || klickedCell == null) {
         return false;
      }

      final Control c;
      try {
         XViewerColumn xColumn =
            xv.getXViewerFactory().getDefaultXViewerColumn(((XViewerColumn) klickedColumn.getData()).getId());
         if (xColumn instanceof ExtendedViewerColumn) {
            ExtendedViewerColumn extendedColumn = (ExtendedViewerColumn) xColumn;
            CellEditDescriptor ced = extendedColumn.getCellEditDescriptorMap().get(klickedCell.getElement().getClass());
            if (ced != null) {
               if (ced.getControl() == null) {
                  return false;
               }
               if (ced.getAction() != null && !ced.getAction().isEnabled()) {
                  return false;
               }
               if (!converter.isValid(ced, klickedCell.getElement())) {
                  return false;
               }
               c = factory.createControl(ced, xv);
               if (c == null) {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }

         if (((TreeItem) event.item) != null) {
            Listener myListener = e-> {
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
            };
            c.addListener(SWT.FocusOut, myListener);
            c.addListener(SWT.Traverse, myListener);
            c.addListener(SWT.Verify, myListener);
            // set old value
            setInput(c);
            c.setFocus();
            return true;
         }
      } catch (Exception ex) {
         return false;
      }
      return false;
   }

   private boolean getNeighbor(int directionMask, boolean sameLevel) {
      try {
         if (klickedCell == null) {
            return false;
         }
         Point cellPosition = new Point(klickedCell.getBounds().x, klickedCell.getBounds().y);
         klickedCell = xv.getCell(cellPosition).getNeighbor(directionMask, sameLevel);
         klickedColumn = xv.getColumnUnderMouseClick(new Point(klickedCell.getBounds().x, klickedCell.getBounds().y));
         XViewerColumn xColumn =
            xv.getXViewerFactory().getDefaultXViewerColumn(((XViewerColumn) klickedColumn.getData()).getId());
         if (xColumn instanceof ExtendedViewerColumn) {
            ExtendedViewerColumn extendedColumn = (ExtendedViewerColumn) xColumn;
            CellEditDescriptor ced = extendedColumn.getCellEditDescriptorMap().get(klickedCell.getElement().getClass());
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

   private static boolean InInput = false;

   void getInput(Control c) {
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
            Object toModify = getInputToModify();
            Object obj = converter.getInput(c, ced, toModify);
            if (obj == null) {
               refreshElement(toModify);
            } else {
               refreshElement(obj);
            }
         } catch (Exception ex) {
            // do nothing
         } finally {
            InInput = false;
         }
      }
   }

   void refreshElement(Object toRefresh) {
      xv.refresh(toRefresh);
   }

   Object getInputToModify() {
      return klickedCell.getElement();
   }

   void setInput(Control c) {
      if (klickedCell == null) {
         return;
      }
      boolean fitInCell = true;
      XViewerColumn xCol =
         xv.getXViewerFactory().getDefaultXViewerColumn(((XViewerColumn) klickedColumn.getData()).getId());
      if (xCol instanceof ExtendedViewerColumn) {
         ExtendedViewerColumn extendedCol = (ExtendedViewerColumn) xCol;
         CellEditDescriptor ced = extendedCol.getCellEditDescriptorMap().get(klickedCell.getElement().getClass());
         if (ced == null || ced.getControl() == null) {
            return;
         }
         converter.setInput(c, ced, klickedCell.getElement());
         fitInCell = ced.isFitInCell();
      }
      if (fitInCell) {
    	  //if there is an image then enable editor control
    	  //only if clicked outside image (after the 18th pixel)
    	  if (klickedCell.getImage() != null) {
    		  Rectangle bounds = klickedCell.getBounds();
    		  bounds.x = bounds.x + 18;
    		  c.setBounds(bounds);
    	  } else {
    		  c.setBounds(klickedCell.getBounds());
    	  }
      } else {
         Rectangle bounds = klickedCell.getBounds();
         Point point = c.getSize();
         //Point point = c.computeSize(SWT.DEFAULT, SWT.DEFAULT);
         //consider size from left to write in x-axis
         if (orientationStyle == SWT.RIGHT_TO_LEFT
        		 || orientationStyle == SWT.RIGHT) {
        	 bounds.x = bounds.x + bounds.width - point.x;
         }
         bounds.width = point.x;
         bounds.height = point.y;
         c.setBounds(bounds);
      }
   }

   /**
    * controls the positioning of  the input control in the
    * case the CellEditDescriptor Control does not take up
    * the whole cell space. Default value assumes right
    * placement.
    *
    * @param style
    */
   public void setInputControlOrientation(int style) {
	   orientationStyle = style;
   }
}
