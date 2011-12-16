package org.eclipse.swt.nebula.presentations.shelf.tab;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.nebula.widgets.pgroup.internal.GraphicUtils;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TabBar extends Canvas
{

    private List items = new ArrayList();
    
    private TabBarItem selectedItem;
    
    private Color tabBorder;
    private Color tabGradient1;
    private Color tabGradient2;
    
    private Color selectedTabBorder;
    private Color selectedTabGradient1;
    private Color selectedTabGradient2;
    
    private Color bottom;
    
    private Color initialForeground;
    private Color selectedForeground;

    private Font initialFont;
    
    private TabBarItemRenderer renderer = new TabBarItemRenderer();
    
    private int horzMargin = 15;
    private int itemSpacing = 6;

    private int itemHeight = 0;
    private int itemWidth = 0;
    
    private int barHeight = 10;
    
    private static int checkStyle(int style)
    {
        int mask = SWT.LEFT_TO_RIGHT | SWT.RIGHT_TO_LEFT | SWT.RIGHT;
        return (style & mask) | SWT.DOUBLE_BUFFERED;
    }
    
    
    public TabBar(Composite parent, int style)
    {
        super(parent, checkStyle(style));

        initListeners();
        initColors();
    }
    
    private void initColors()
    {
        Color baseColor = getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT);
        
        //tabGradient1 = GraphicUtils.createNewBlendedColor(baseColor,getDisplay().getSystemColor(SWT.COLOR_WHITE),30);
        tabGradient1 = getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
        
        baseColor = GraphicUtils.createNewBlendedColor(getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT),getDisplay().getSystemColor(SWT.COLOR_WHITE),80);
        
        tabGradient2 = GraphicUtils.createNewSaturatedColor(baseColor,.01f);
        
        baseColor.dispose();
       
        baseColor = getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION);
        
        tabBorder = GraphicUtils.createNewBlendedColor(baseColor,getDisplay().getSystemColor(SWT.COLOR_WHITE),70);
        

        selectedTabGradient1 = GraphicUtils.createNewBlendedColor(baseColor,getDisplay().getSystemColor(SWT.COLOR_WHITE),70);
        //selectedTabGradient1 = getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
        
        baseColor = GraphicUtils.createNewBlendedColor(getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION),getDisplay().getSystemColor(SWT.COLOR_BLACK),80);
        
        selectedTabGradient2 = GraphicUtils.createNewSaturatedColor(baseColor,.02f);
        //selectedTabGradient2 = getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
        
        //setBackground(GraphicUtils.createNewBlendedColor(selectedTabGradient2, getDisplay().getSystemColor(SWT.COLOR_WHITE),20));
        
        baseColor.dispose();
        
        selectedTabBorder = getDisplay().getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW);
        bottom = selectedTabGradient2;//getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
        
        initialFont = new Font(getDisplay(),"Arial",11,SWT.BOLD);
        setFont(initialFont);
        
        initialForeground = GraphicUtils.createNewBlendedColor(getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION),getDisplay().getSystemColor(SWT.COLOR_BLACK),80);
        //foreground = getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION);
        setForeground(initialForeground);
        selectedForeground = GraphicUtils.createNewBlendedColor(tabGradient1,getDisplay().getSystemColor(SWT.COLOR_WHITE),10);
        
        
    }

    private void initListeners()
    {
        addPaintListener(new PaintListener()
        {
            public void paintControl(PaintEvent e)
            {
                onPaint(e);
            }    
        });
        
        addDisposeListener(new DisposeListener()
        {
            public void widgetDisposed(DisposeEvent e)
            {
                onDispose();
            }        
        });
        
        addListener(SWT.MouseMove, new Listener()
        {        
            public void handleEvent(Event e)
            {
                onMouseMove(e);
            }        
        });
        
        addListener(SWT.MouseDown, new Listener()
        {
            public void handleEvent(Event e)
            {
                onMouseDown(e);
            }        
        });
    }
    
    private void onMouseDown(Event e)
    {
        if (e.button != 1) return;
        
        TabBarItem item = getItem(new Point(e.x,e.y));
        if (item == null) return;
        
        selectedItem = item;
        redraw();
        
        e = new Event();
        e.item = item;
        
        item.notifyListeners(SWT.Selection, e);
    }
    
    private void onMouseMove(Event e)
    {
        TabBarItem item = getItem(new Point(e.x,e.y));
        if (item == null)
        {
            setCursor(null);
        }
        else
        {
            if (item == selectedItem)
            {
                setCursor(null);
            }
            else
            {
                setCursor(getDisplay().getSystemCursor(SWT.CURSOR_HAND));
            }
        }
    }
    
    private void onDispose()
    {
        tabBorder.dispose();
        tabGradient1.dispose();
        tabGradient2.dispose();
        
        selectedTabBorder.dispose();
        selectedTabGradient1.dispose();
        selectedTabGradient2.dispose();
        
        bottom.dispose();

        initialFont.dispose();
        initialForeground.dispose();
        selectedForeground.dispose();
    }
    
    private void onPaint(PaintEvent e)
    {
        GC gc = e.gc;
        
        int y = getBounds().height - barHeight;
        
        int totalWidth = horzMargin + (itemWidth * items.size()) + (itemSpacing * items.size()) - itemSpacing + horzMargin;
        int croppedWidth = Math.max(totalWidth,getBounds().width);
        
//        Region reg = createRoundedTopRegion(0,y,croppedWidth,getClientArea().height - y);

//        gc.setClipping(reg);

        Pattern p = new Pattern(getDisplay(), 0, y, 0, y + barHeight,
                                bottom, 255, getBackground(), 0);
        gc.setBackgroundPattern(p);
        
        gc.fillRectangle(0, y,croppedWidth, barHeight);
        
        p.dispose();
//        reg.dispose();
        
        gc.setClipping((Region)null);
        gc.setBackgroundPattern(null);
        
        
        int x = horzMargin; 
        if ((getStyle() & SWT.RIGHT) != 0)
        {
            if (getBounds().width > totalWidth)
            {
                x += getBounds().width - totalWidth;
            }
        }
        
        y = (getBounds().height - barHeight) - itemHeight;
        
        for (Iterator iterator = items.iterator(); iterator.hasNext();)
        {
            TabBarItem item = (TabBarItem)iterator.next();
            
            renderer.setBounds(new Rectangle(x,y,itemWidth,itemHeight));
            renderer.setSelected(item == selectedItem);
            renderer.paint(gc, item);
            
            x += itemWidth + itemSpacing;
        }
    }
    
    public TabBarItem getItem(Point point)
    {
        int tabStartY = getBounds().height - barHeight - itemHeight;
        if (point.y < tabStartY)
            return null;
        
        if (point.y > tabStartY + itemHeight - 1)
            return null;
        
        int totalWidth = horzMargin + (itemWidth * items.size()) + (itemSpacing * items.size()) - itemSpacing + horzMargin;
        
        int x = horzMargin; 
        if ((getStyle() & SWT.RIGHT) != 0)
        {
            if (getBounds().width > totalWidth)
            {
                x += getBounds().width - totalWidth;
            }
        }
        
        for (Iterator iterator = items.iterator(); iterator.hasNext();)
        {
            TabBarItem item = (TabBarItem)iterator.next();
            
            if (point.x >= x && point.x < x + itemWidth)
            {
                return item;
            }
            
            x += itemWidth + itemSpacing;
        }
        
        return null;
    }
    
    public TabBarItem[] getItems()
    {
        return (TabBarItem[])items.toArray(new TabBarItem[]{});
    }
    
    void updateItemSize()
    {        
        for (Iterator iterator = items.iterator(); iterator.hasNext();)
        {
            TabBarItem item = (TabBarItem)iterator.next();
            Point size = renderer.computeSize(item);
            itemWidth = Math.max(itemWidth,size.x);
            itemHeight = Math.max(itemHeight,size.y);
        }
    }
    
    Region createRoundedTopRegion(int xOffset, int yOffset, int width, int height)
    {
        Region reg = new Region(getDisplay());
        reg.add(xOffset, yOffset, width, height);
        reg.subtract(xOffset + 0, yOffset + 0, 5, 1);
        reg.subtract(xOffset + 0, yOffset + 1, 3, 1);
        reg.subtract(xOffset + 0, yOffset + 2, 2, 1);
        reg.subtract(xOffset + 0, yOffset + 3, 1, 1);
        reg.subtract(xOffset + 0, yOffset + 4, 1, 1);

        reg.subtract(xOffset + width - 5, yOffset + 0, 5, 1);
        reg.subtract(xOffset + width - 3, yOffset + 1, 3, 1);
        reg.subtract(xOffset + width - 2, yOffset + 2, 2, 1);
        reg.subtract(xOffset + width - 1, yOffset + 3, 1, 1);
        reg.subtract(xOffset + width - 1, yOffset + 4, 1, 1);
        
        return reg;
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public Point computeSize(int wHint, int hHint, boolean changed)
    {
        int w = wHint;
        int h = hHint;
        
        if (wHint == SWT.DEFAULT) w = 400;
        if (hHint == SWT.DEFAULT) h = barHeight + itemHeight + 5;
        
        return new Point(w,h);
    }    
    
    void newItem(TabBarItem item, int index)
    {
        if (index != -1)
        {
            items.add(index, item);
        }
        else
        {
            items.add(item);
        }
        
        if (selectedItem == null)
            selectedItem = item;
        redraw();
    }
    
    public void setSelection(TabBarItem item)
    {
        checkWidget();
        selectedItem = item;
        redraw();
    }

    /**
     * @return the tabBorder
     */
    Color getTabBorder()
    {
        return tabBorder;
    }


    /**
     * @param tabBorder the tabBorder to set
     */
    void setTabBorder(Color tabBorder)
    {
        this.tabBorder = tabBorder;
    }


    /**
     * @return the tabGradient1
     */
    Color getTabGradient1()
    {
        return tabGradient1;
    }


    /**
     * @param tabGradient1 the tabGradient1 to set
     */
    void setTabGradient1(Color tabGradient1)
    {
        this.tabGradient1 = tabGradient1;
    }


    /**
     * @return the tabGradient2
     */
    Color getTabGradient2()
    {
        return tabGradient2;
    }


    /**
     * @param tabGradient2 the tabGradient2 to set
     */
    void setTabGradient2(Color tabGradient2)
    {
        this.tabGradient2 = tabGradient2;
    }


    /**
     * @return the selectedTabBorder
     */
    Color getSelectedTabBorder()
    {
        return selectedTabBorder;
    }


    /**
     * @param selectedTabBorder the selectedTabBorder to set
     */
    void setSelectedTabBorder(Color selectedTabBorder)
    {
        this.selectedTabBorder = selectedTabBorder;
    }


    /**
     * @return the selectedTabGradient1
     */
    Color getSelectedTabGradient1()
    {
        return selectedTabGradient1;
    }


    /**
     * @param selectedTabGradient1 the selectedTabGradient1 to set
     */
    void setSelectedTabGradient1(Color selectedTabGradient1)
    {
        this.selectedTabGradient1 = selectedTabGradient1;
    }


    /**
     * @return the selectedTabGradient2
     */
    Color getSelectedTabGradient2()
    {
        return selectedTabGradient2;
    }


    /**
     * @param selectedTabGradient2 the selectedTabGradient2 to set
     */
    void setSelectedTabGradient2(Color selectedTabGradient2)
    {
        this.selectedTabGradient2 = selectedTabGradient2;
    }

    /**
     * @return the selectedForeground
     */
    Color getSelectedForeground()
    {
        return selectedForeground;
    }


    /** 
     * {@inheritDoc}
     */
    @Override
    public Rectangle getClientArea()
    {
        Rectangle client = new Rectangle(5,5,getSize().x,getSize().y - 5 - barHeight);
        
        int totalWidth = horzMargin + (itemWidth * items.size()) + (itemSpacing * items.size()) - itemSpacing + horzMargin;
        
        client.width -= totalWidth;
        client.height -= 5;
        
        return client;
    }
    
}
