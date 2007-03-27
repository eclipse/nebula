/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    chris.gross@us.ibm.com - initial API and implementation
 *******************************************************************************/ 
package org.eclipse.nebula.widgets.grid.internal;

import org.eclipse.nebula.widgets.grid.GridCellRenderer;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.nebula.widgets.grid.IInternalWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextLayout;

/**
 * The renderer for a cell in Grid.
 *
 * @author chris.gross@us.ibm.com
 * @since 2.0.0
 */
public class DefaultCellRenderer extends GridCellRenderer
{

    int leftMargin = 4;

    int rightMargin = 4;

    int topMargin = 2;

    int bottomMargin = 2;

    private int insideMargin = 3;

    private ToggleRenderer toggleRenderer;

    private CheckBoxRenderer checkRenderer;

    private TextLayout textLayout;

    /**
     * {@inheritDoc}
     */
    public void paint(GC gc, Object value)
    {
        GridItem item = (GridItem)value;

        gc.setFont(item.getFont(getColumn()));
        
        boolean drawAsSelected = isSelected();
        
        boolean drawBackground = true;
        
        if (isCellSelected())
        {
            drawAsSelected = true;//(!isCellFocus());        
        }

        if (drawAsSelected)
        {
            gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION));
            gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT));
        }
        else
        {
            if (item.getParent().isEnabled())
            {
                Color back = item.getBackground(getColumn());
                
                if (back != null)
                {
                    gc.setBackground(back);
                }
                else
                {
                    drawBackground = false;
                }
            }
            else
            {
                gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
            }
            gc.setForeground(item.getForeground(getColumn()));
        }

        if (drawBackground)
            gc.fillRectangle(getBounds().x, getBounds().y, getBounds().width,
                         getBounds().height);

        
        int x = leftMargin;

        if (isTree())
        {
            x += getToggleIndent(item);

            toggleRenderer.setExpanded(item.isExpanded());

            toggleRenderer.setHover(getHoverDetail().equals("toggle"));

            toggleRenderer.setLocation(getBounds().x + x, (getBounds().height - toggleRenderer
                .getBounds().height)
                                                          / 2 + getBounds().y);
            if (item.hasChildren())
                toggleRenderer.paint(gc, null);

            x += toggleRenderer.getBounds().width + insideMargin;

        }

        if (isCheck())
        {

            checkRenderer.setChecked(item.getChecked(getColumn()));
            checkRenderer.setGrayed(item.getGrayed(getColumn()));
            if (!item.getParent().isEnabled())
            {
                checkRenderer.setGrayed(true);
            }
            checkRenderer.setHover(getHoverDetail().equals("check"));

            checkRenderer.setBounds(getBounds().x + x, (getBounds().height - checkRenderer
                .getBounds().height)
                                                       / 2 + getBounds().y, checkRenderer
                .getBounds().width, checkRenderer.getBounds().height);
            checkRenderer.paint(gc, null);

            x += checkRenderer.getBounds().width + insideMargin;
        }

        Image image = item.getImage(getColumn());
        if (image != null)
        {
            int y = getBounds().y;
 
            y += (getBounds().height - image.getBounds().height)/2;
                            
            gc.drawImage(image, getBounds().x + x, y);
            
            x += image.getBounds().width + insideMargin;
        }
        
        int width = getBounds().width - x - rightMargin;

        if (drawAsSelected)
        {
            gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT));
        }
        else
        {
            gc.setForeground(item.getForeground(getColumn()));
        }
        
        if (!isWordWrap())
        {
            String text = TextUtils.getShortString(gc, item.getText(getColumn()), width);

            if (getAlignment() == SWT.RIGHT)
            {
                int len = gc.stringExtent(text).x;
                if (len < width)
                {
                    x += width - len;
                }
            }
            else if (getAlignment() == SWT.CENTER)
            {
                int len = gc.stringExtent(text).x;
                if (len < width)
                {
                    x += (width - len) / 2;
                }
            }

            gc.drawString(text, getBounds().x + x, getBounds().y + topMargin, true);        
        }
        else
        {
            if (textLayout == null)
            {
                textLayout = new TextLayout(gc.getDevice());
                item.getParent().addDisposeListener(new DisposeListener()
                {                
                    public void widgetDisposed(DisposeEvent e)
                    {
                        textLayout.dispose();
                    }                
                });
            }
            textLayout.setFont(gc.getFont());
            textLayout.setText(item.getText(getColumn()));
            textLayout.setAlignment(getAlignment());
            textLayout.setWidth(width);
            
            textLayout.draw(gc, getBounds().x + x, getBounds().y + topMargin);
        }
        

        if (item.getParent().getLinesVisible())
        {
            if (isCellSelected())
            {
                //XXX: should be user definable?
                gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));
            }
            else
            {
                gc.setForeground(item.getParent().getLineColor());
            }
            gc.drawLine(getBounds().x, getBounds().y + getBounds().height, getBounds().x
                                                                           + getBounds().width -1,
                        getBounds().y + getBounds().height);
            gc.drawLine(getBounds().x + getBounds().width - 1, getBounds().y, 
                        getBounds().x + getBounds().width - 1, getBounds().y + getBounds().height);
        }
        
        if (isCellFocus())
        {
            Rectangle focusRect = new Rectangle(getBounds().x -1, getBounds().y - 1, getBounds().width,
                                                getBounds().height + 1);
            
            gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND));
            gc.drawRectangle(focusRect);
      
            if (isFocus())
            {
                focusRect.x ++;
                focusRect.width -= 2;
                focusRect.y ++;
                focusRect.height -= 2;
                
                gc.drawRectangle(focusRect);       
            }
        }        
    }

    /** 
     * {@inheritDoc}
     */
    public Point computeSize(GC gc, int wHint, int hHint, Object value)
    {
        GridItem item = (GridItem)value;

        gc.setFont(item.getFont(getColumn()));

        int x = 0;

        x += leftMargin;

        if (isTree())
        {
            x += getToggleIndent(item);

            x += toggleRenderer.getBounds().width + insideMargin;

        }

        if (isCheck())
        {
            x += checkRenderer.getBounds().width + insideMargin;
        }
        
        int y = 0;
        
        Image image = item.getImage(getColumn());
        if (image != null)
        {  
            y = topMargin + image.getBounds().height + bottomMargin;
            
            x += image.getBounds().width + insideMargin;
        }

        x += gc.stringExtent(item.getText(getColumn())).x + rightMargin;

        y = Math.max(y,topMargin + gc.getFontMetrics().getHeight() + bottomMargin);

        return new Point(x, y);
    }

    /** 
     * {@inheritDoc}
     */
    public boolean notify(int event, Point point, Object value)
    {

        GridItem item = (GridItem)value;

        if (isCheck())
        {
            if (event == IInternalWidget.MouseMove)
            {
                if (overCheck(item, point))
                {
                    setHoverDetail("check");
                    return true;
                }
            }

            if (event == IInternalWidget.LeftMouseButtonDown)
            {
                if (overCheck(item, point))
                {
                    if (!item.getCheckable(getColumn()))
                    {
                        return false;
                    }
                    
                    item.setChecked(getColumn(), !item.getChecked(getColumn()));
                    item.getParent().redraw();

                    item.fireCheckEvent(getColumn());

                    return true;
                }
            }
        }

        if (isTree() && item.hasChildren())
        {
            if (event == IInternalWidget.MouseMove)
            {
                if (overToggle(item, point))
                {
                    setHoverDetail("toggle");
                    return true;
                }
            }

            if (event == IInternalWidget.LeftMouseButtonDown)
            {
                if (overToggle(item, point))
                {
                    item.setExpanded(!item.isExpanded());
                    item.getParent().redraw();

                    if (item.isExpanded())
                    {
                        item.fireEvent(SWT.Expand);
                    }
                    else
                    {
                        item.fireEvent(SWT.Collapse);
                    }

                    return true;
                }
            }
        }

        return false;
    }

    private boolean overCheck(GridItem item, Point point)
    {

        point = new Point(point.x, point.y);
        point.x -= getBounds().x - 1;
        point.y -= getBounds().y - 1;

        int x = leftMargin;
        if (isTree())
        {
            x += getToggleIndent(item);
            x += toggleRenderer.getSize().x + insideMargin;
        }

        if (point.x >= x && point.x < (x + checkRenderer.getSize().x))
        {
            int yStart = ((getBounds().height - checkRenderer.getBounds().height) / 2);
            if (point.y >= yStart && point.y < yStart + checkRenderer.getSize().y)
            {
                return true;
            }
        }

        return false;
    }

    private int getToggleIndent(GridItem item)
    {
        return item.getLevel() * 20;
    }

    private boolean overToggle(GridItem item, Point point)
    {

        point = new Point(point.x, point.y);

        point.x -= getBounds().x - 1;
        point.y -= getBounds().y - 1;

        int x = leftMargin;
        x += getToggleIndent(item);

        if (point.x >= x && point.x < (x + toggleRenderer.getSize().x))
        {
            // return true;
            int yStart = ((getBounds().height - toggleRenderer.getBounds().height) / 2);
            if (point.y >= yStart && point.y < yStart + toggleRenderer.getSize().y)
            {
                return true;
            }
        }

        return false;
    }

    /** 
     * {@inheritDoc}
     */
    public void setTree(boolean tree)
    {
        super.setTree(tree);

        if (tree)
        {
            toggleRenderer = new ToggleRenderer();
            toggleRenderer.setDisplay(getDisplay());
        }
    }

    /** 
     * {@inheritDoc}
     */
    public void setCheck(boolean check)
    {
        super.setCheck(check);

        if (check)
        {
            checkRenderer = new CheckBoxRenderer();
            checkRenderer.setDisplay(getDisplay());
        }
        else
        {
            checkRenderer = null;
        }
    }

    /** 
     * {@inheritDoc}
     */
    public Rectangle getTextBounds(GridItem item, boolean preferred)
    {
        int x = leftMargin;

        if (isTree())
        {
            x += getToggleIndent(item);

            x += toggleRenderer.getBounds().width + insideMargin;

        }

        if (isCheck())
        {
            x += checkRenderer.getBounds().width + insideMargin;
        }

        Image image = item.getImage(getColumn());
        if (image != null)
        {
            x += image.getBounds().width + insideMargin;
        }
        
        Rectangle bounds = new Rectangle(x,topMargin,0,0);
        
        GC gc = new GC(item.getParent());
        gc.setFont(item.getFont(getColumn()));
        Point size = gc.stringExtent(item.getText(getColumn()));
        
        bounds.height = size.y;
        
        if (preferred)
        {
            bounds.width = size.x;
        }
        else
        {
            bounds.width = getBounds().width - x - rightMargin;
        }
        
        gc.dispose();
        
        return bounds;
    }
    
}
