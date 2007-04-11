package org.eclipse.swt.nebula.presentations.shelf.tab;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;

public class TabBarItemRenderer
{
    private boolean selected = false;
    private Rectangle bounds = new Rectangle(0,0,0,0);
    

    public Point computeSize(TabBarItem item)
    {
        int height = 10;
        int width = 40;
        
        GC gc = new GC(item.getParent());
        Point p = gc.stringExtent(item.getText());
        height += p.y;
        width += p.x;
        gc.dispose();
        
        return new Point(width,height);
    }
    
    public void paint(GC gc, TabBarItem item)
    {
        gc.setAntialias(SWT.ON);
        
        Region reg = item.getParent().createRoundedTopRegion(getBounds().x,getBounds().y,getBounds().width,getBounds().height);
        gc.setClipping(reg);
        if (isSelected())
        {
            gc.setForeground(item.getParent().getSelectedTabGradient1());
            gc.setBackground(item.getParent().getSelectedTabGradient2());            
        }
        else
        {
            gc.setForeground(item.getParent().getTabGradient1());
            gc.setBackground(item.getParent().getTabGradient2());            
        }
        
        gc.fillGradientRectangle(getBounds().x,getBounds().y,getBounds().width,getBounds().height,true);
        
        if (isSelected())
        {
            gc.setForeground(item.getParent().getSelectedTabBorder());
        }
        else
        {
            gc.setForeground(item.getParent().getTabBorder());
        }
        gc.drawRoundRectangle(getBounds().x,getBounds().y,getBounds().width -1,getBounds().height + 100,14,14);

        gc.setClipping((Region)null);
        reg.dispose();
        
        
        Point p = gc.stringExtent(item.getText());
        
        int x = (getBounds().width - p.x)/2;
        int y = (getBounds().height - p.y)/2 + 1;
        
        gc.setAntialias(SWT.ON);

        gc.setAlpha(128);      
        int yOffset = 0;
        if (!isSelected())
        {
            yOffset = 1;
            gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_WHITE));        
        }
        else
        {
            yOffset = -1;
            gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_BLACK));        
        }
        gc.drawString(item.getText(), getBounds().x + x + 0, getBounds().y + y + yOffset,true);
        
        gc.setAlpha(255);
        
        if (isSelected())
        {
            gc.setForeground(item.getParent().getSelectedForeground());            
        }
        else
        {
            gc.setForeground(item.getParent().getForeground());         
        }        
        gc.drawString(item.getText(), getBounds().x + x, getBounds().y + y,true);
        
    }

    /**
     * @return the selected
     */
    public boolean isSelected()
    {
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }

    /**
     * @return the bounds
     */
    public Rectangle getBounds()
    {
        return bounds;
    }

    /**
     * @param bounds the bounds to set
     */
    public void setBounds(Rectangle bounds)
    {
        this.bounds = bounds;
    }
}
