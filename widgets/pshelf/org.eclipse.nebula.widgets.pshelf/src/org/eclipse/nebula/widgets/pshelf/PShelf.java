/*******************************************************************************
 * Copyright (c) 2006 Chris Gross. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: schtoo@schtoo.com(Chris Gross) - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.pshelf;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TypedListener;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>
 * NOTE:  THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT.  THIS IS A PRE-RELEASE ALPHA 
 * VERSION.  USERS SHOULD EXPECT API CHANGES IN FUTURE VERSIONS.
 * </p> 
 * 
 * Instances of this class implement a selectable accordion metaphor, where each shelf contains 
 * a client area.
 * <p>
 * The item children that may be added to instances of this class
 * must be of type <code>PShelfItem</code>.
 * <code>Control</code> children are created on the body composite of each items accessed via 
 * <code>PShelfItem#getBody</code>.
 * </p><p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>BORDER, SIMPLE</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Selection</dd>
 * </dl>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 */
public class PShelf extends Canvas {
	
	private ArrayList items = new ArrayList();
	private AbstractRenderer renderer;
	private PShelfItem openItem;
	private PShelfItem focusItem;
	private PShelfItem mouseDownItem;
	private PShelfItem hoverItem;
    private int itemHeight = 0;
    
    private ArrayList yCoordinates = new ArrayList();
	
    private static int checkStyle(int style)
    {
        int mask = SWT.LEFT_TO_RIGHT | SWT.RIGHT_TO_LEFT | SWT.BORDER | SWT.SIMPLE;
        return (style & mask) | SWT.DOUBLE_BUFFERED;
    }

    /**
     * Constructs a new instance of this class given its parent
     * and a style value describing its behavior and appearance.
     * <p>
     * The style value is either one of the style constants defined in
     * class <code>SWT</code> which is applicable to instances of this
     * class, or must be built by <em>bitwise OR</em>'ing together 
     * (that is, using the <code>int</code> "|" operator) two or more
     * of those <code>SWT</code> style constants. The class description
     * lists the style constants that are applicable to the class.
     * Style bits are also inherited from superclasses.
     * </p>
     *
     * @param parent a composite control which will be the parent of the new instance (cannot be null)
     * @param style the style of control to construct
     *
     * @exception IllegalArgumentException <ul>
     *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
     * </ul>
     * @exception SWTException <ul>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
     *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
     * </ul>
     */
	public PShelf(Composite parent, int style) {
		super(parent,checkStyle(style));
		setRenderer(new PaletteShelfRenderer());
		
		this.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				onPaint(e.gc);
			}		
		});
		
		this.addListener(SWT.Resize, new Listener()
        {        
            public void handleEvent(Event event)
            {
                onResize();
            }        
        });
		
		this.addMouseListener(new MouseListener() {
			public void mouseUp(MouseEvent e) {
				PShelfItem item = getItem(new Point(1,e.y));
				if ((item) == null)
					return;
				if (item == mouseDownItem && item != openItem){
					openItem(item,true);
				}
			}
			public void mouseDown(MouseEvent e) {
				mouseDownItem = getItem(new Point(1,e.y));
			}
			public void mouseDoubleClick(MouseEvent arg0) {
			}		
		});
		
		this.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent arg0) {
				onDispose();
			}		
		});
		
		this.addMouseTrackListener(new MouseTrackListener() {
			public void mouseHover(MouseEvent arg0) {
			}
			public void mouseExit(MouseEvent arg0) {
				hoverItem = null;
				redraw();
			}
			public void mouseEnter(MouseEvent arg0) {
			}		
		});
		
        this.addMouseMoveListener(new MouseMoveListener(){
            public void mouseMove(MouseEvent e) {
				PShelfItem item = getItem(new Point(1,e.y));
				if (item != hoverItem){
					hoverItem = item;
					redraw();
				}
            }});
	}
	
    /**
     * Sets the renderer.
     *
     * @param renderer the new renderer
     *
     * @exception IllegalArgumentException <ul>
     *    <li>ERROR_NULL_ARGUMENT - if the renderer is null</li>
     * </ul>
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver or the renderer has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     */
    public void setRenderer(AbstractRenderer renderer)
    {
        checkWidget();
        
        if (renderer == null)
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        
        if (renderer.isDisposed())
            SWT.error(SWT.ERROR_WIDGET_DISPOSED);
        
        if (this.renderer != null)
            this.renderer.dispose();
        
        this.renderer = renderer;
        renderer.initialize(this);
        
        computeItemHeight();
        onResize();
        redraw();
    }
    
    /**
     * Returns the renderer.
     *
     * @return the renderer
     *
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     */
    public AbstractRenderer getRenderer()
    {
        checkWidget();
        return renderer;
    }
    
	/** 
     * {@inheritDoc}
     */
    public Point computeSize(int wHint, int hHint, boolean changed)
    {
        checkWidget();
        
        Point size = new Point(wHint,hHint);
        
        if (wHint == SWT.DEFAULT || hHint == SWT.DEFAULT)
        {
            if (openItem != null)
            {
                Point prefSize = openItem.getBody().computeSize(SWT.DEFAULT,SWT.DEFAULT);
                if (wHint == SWT.DEFAULT)
                    size.x = prefSize.x;
                
                if (hHint == SWT.DEFAULT)
                {
                    size.y = prefSize.y + (items.size() * itemHeight);
                }
            }
            else
            {
                return super.computeSize(wHint,hHint,changed);
            }
        }
        
        return size;
    }

    private void onDispose() {
		renderer.dispose();
	}

	private void onPaint(GC gc) {
		
		gc.setAdvanced(true);
		if (gc.getAdvanced())
			gc.setTextAntialias(SWT.ON);
		
		Color back = getBackground();
		Color fore = getForeground();
		
		int index = 0;

		for (Iterator iter = items.iterator(); iter.hasNext();) {
			PShelfItem item = (PShelfItem) iter.next();
						
			gc.setBackground(back);
			gc.setForeground(fore);
            
            Integer y = (Integer) yCoordinates.get(index);
			
            renderer.setBounds(0,y.intValue(),getClientArea().width,itemHeight);
            renderer.setSelected(item == openItem);
            renderer.setFocus(this.isFocusControl() && focusItem == item);
            renderer.setHover(item == hoverItem);
            renderer.paint(gc,item);

			index ++;
		}
		
	}
    
    private void computeItemYCoordinates()
    {
        yCoordinates.clear();
        
        int y = getClientArea().y;
        int i = 0;
        
        for (Iterator iter = items.iterator(); iter.hasNext();)
        {
            i ++;
            
            PShelfItem item = (PShelfItem)iter.next();
            
            yCoordinates.add(new Integer(y));
            
            y += itemHeight;
            
            if (item == openItem)
                y = getClientArea().y + getClientArea().height - (itemHeight * (items.size() - i));
        }
    }

	void createItem(PShelfItem item,int index){
		if (index == -1){
			items.add(item);
		} else {
			items.add(index,item);
		}
		
		computeItemHeight();
		
		if (openItem == null){
			openItem(item,false);
		}	
		//need to recompute ycoords and heights and such
		onResize();
	}
	
	void removeItem(PShelfItem item){
        computeItemHeight();
		items.remove(item);
		if (openItem == item)
		{
			openItem = null;
			if (items.size() > 0)
			{
				openItem((PShelfItem) items.get(0),false);
			}
		}			
        
        onResize();
	}
	
	private void openItem(PShelfItem item, boolean animation){
		PShelfItem previousOpen = openItem;
		openItem = item;
		focusItem = item;
		
		item.getBodyParent().setBounds(0,0,0,0);
		item.getBodyParent().setVisible(true);
		item.getBody().layout();
		
		if (animation && (getStyle() & SWT.SIMPLE) == 0){
			if (items.indexOf(item) < items.indexOf(previousOpen)){
				animateOpenFromTop(previousOpen,item);
			} else {
				animateOpenFromBottom(previousOpen,item);
			}
		} else {
			if (previousOpen != null && !previousOpen.isDisposed())
				previousOpen.getBodyParent().setBounds(0,0,0,0);
			if ((getStyle() & SWT.SIMPLE) != 0){
				//reorder the items
				items.remove(item);
				items.add(0,item);
			}
			onResize();
		}
		if (previousOpen != null)
			previousOpen.getBodyParent().setVisible(false);
		
		redraw();
		getDisplay().update();
        
        Event e = new Event();
        e.item = openItem;

        this.notifyListeners(SWT.Selection,e);
        
        computeItemYCoordinates();
        onResize();
	}
	
    private void animateOpenFromTop(PShelfItem previousItem, PShelfItem newItem)
    {
        double percentOfWork = 0;        
                
        while (percentOfWork < 1)
        {            
            yCoordinates.clear();
            
            int yTop = getClientArea().y;
            
            int yBottom = getClientArea().y + getClientArea().height - (itemHeight * (items.size() - (items.indexOf(previousItem) + 1)));
            
            int totalGrowingArea = getClientArea().height - (itemHeight * items.size());
            
            int growingSpace = (int)(totalGrowingArea * percentOfWork);
            boolean addingGrowingSpace = false;
            
            for (int i = 0; i < items.size(); i++)
            {
                if (i <= items.indexOf(newItem))
                {
                    //put on top
                    yCoordinates.add(new Integer(yTop));
                    yTop += itemHeight;                    
                }
                else if (i > items.indexOf(previousItem))
                {
                    //put on bottom
                    yCoordinates.add(new Integer(yBottom));
                    yBottom += itemHeight;
                }
                else
                {
                    if (!addingGrowingSpace)
                    {
                        yTop += growingSpace;
                        addingGrowingSpace = true;
                    }
                    yCoordinates.add(new Integer(yTop));
                    yTop += itemHeight;                        
                }
                
            }
            
            sizeClients();
            redraw();
            update();
            //workaround for SWT bug 193357
            if (SWT.getPlatform().equals("carbon"))
            {
            	getDisplay().readAndDispatch();
            }
            percentOfWork += .02;
        } 
        
        computeItemYCoordinates();
        redraw();
    }
    
    private void animateOpenFromBottom(PShelfItem previousItem, PShelfItem newItem)
    {
        double percentOfWork = 0;        
                
        while (percentOfWork < 1)
        {            
            yCoordinates.clear();
            
            int yTop = getClientArea().y;
            
            int yBottom = getClientArea().y + getClientArea().height - (itemHeight * (items.size() - (items.indexOf(newItem) + 1)));
            
            int totalShrinkingArea = getClientArea().height - (itemHeight * items.size());
            
            int collapsingSpace = (int)(totalShrinkingArea * (1 - percentOfWork));
            boolean addedCollapsingSpace = false;
            
            for (int i = 0; i < items.size(); i++)
            {
                if (i <= items.indexOf(previousItem))
                {
                    //put on top
                    yCoordinates.add(new Integer(yTop));
                    yTop += itemHeight;                    
                }
                else if (i > items.indexOf(newItem))
                {
                    //put on bottom
                    yCoordinates.add(new Integer(yBottom));
                    yBottom += itemHeight;
                }
                else
                {
                    if (!addedCollapsingSpace)
                    {
                        yTop += collapsingSpace;
                        addedCollapsingSpace = true;
                    }
                    yCoordinates.add(new Integer(yTop));
                    yTop += itemHeight;                        
                }
                
            }
            
            sizeClients();
            redraw(getClientArea().x,getClientArea().y, getClientArea().width, getClientArea().height, false);
            update();
            //workaround for SWT bug 193357
            if (SWT.getPlatform().equals("carbon"))
            {
            	getDisplay().readAndDispatch();
            }
            percentOfWork += .02;
        } 
        
        computeItemYCoordinates();
        redraw();
    }
	
	void onResize(){        
        computeItemYCoordinates();
        sizeClients();
        
        int clientHeight = getClientArea().height - (itemHeight * items.size());
        
        for (Iterator iter = items.iterator(); iter.hasNext();)
        {
            PShelfItem item = (PShelfItem)iter.next();
            item.getBody().setBounds(0,0,getClientArea().width,clientHeight);            
        }
	}
    
    private void sizeClients()
    {
        if (openItem == null)
            return;
        
        if (items.size() == 0)
            return;
        
        for(int i = 0; i < items.size(); i ++)
        {
            PShelfItem item = (PShelfItem)items.get(i);
            int y = ((Integer)yCoordinates.get(i)).intValue();
            int nextY = 0;
            if (i + 1 < items.size())
            {
                nextY = ((Integer)yCoordinates.get(i + 1)).intValue();    
            }
            else
            {
                nextY = getClientArea().y + getClientArea().height;
            }
            
            int clientHeight = nextY - y - itemHeight;
            
            if (clientHeight > 0)
            {
                item.getBodyParent().setVisible(true);
                item.getBodyParent().setBounds(0,y + itemHeight,getClientArea().width,clientHeight);
            }
            else
            {
                item.getBodyParent().setVisible(false);
            }            
        }        
    }
    

	void computeItemHeight(){
		GC gc = new GC(this);
		
		for (Iterator iter = items.iterator(); iter.hasNext();) {
			PShelfItem item = (PShelfItem) iter.next();
			itemHeight = Math.max(renderer.computeSize(gc, 0, SWT.DEFAULT, item).y,itemHeight);
		}

		gc.dispose();
	}
	
    /**
     * Returns the item at the given location.
     *
     * @param point location
     *
     * @exception IllegalArgumentException <ul>
     *    <li>ERROR_NULL_ARGUMENT - if the point is null</li>
     * </ul>
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver or the renderer has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     */
	public PShelfItem getItem(Point point){	
        checkWidget();
        
		int y1 = 0;
		int y2 = 0;
		int c = 0;
        
        if (point == null)
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        
        for (Iterator iter = items.iterator(); iter.hasNext();)
        {
            PShelfItem item = (PShelfItem)iter.next();
            
            y2 += itemHeight;
            
            if (point.y >= y1 && point.y <= y2 -1){
                return item;
            }
            
            y1 += itemHeight;
            
            if (item == openItem){            	
                y1 += openItem.getBodyParent().getSize().y;
                y2 += openItem.getBodyParent().getSize().y;
            }
            c ++;
        }
        	
		return null;
	}
	
    /**
     * Sets the receiver's selection to the given item.
     *
     * @param item the item to select
     *
     * @exception IllegalArgumentException <ul>
     *    <li>ERROR_NULL_ARGUMENT - if the item is null</li>
     * </ul>
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     */
	public void setSelection(PShelfItem item){
        checkWidget();
        
        if (item == null)
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        
		if (!items.contains(item))
			return;
		
		if (openItem == item)
			return;
		
		openItem(item,true);
	}
	
    /**
     * Returns the <code>PShelfItem</code> that is currently
     * selected in the receiver. 
     * 
     * @return the currently selected item
     *
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     */
	public PShelfItem getSelection(){
        checkWidget();
		return openItem;
	}

    /**
     * Returns an array of <code>PShelfItem</code>s which are the items
     * in the receiver. 
     * <p>
     * Note: This is not the actual structure used by the receiver
     * to maintain its list of items, so modifying the array will
     * not affect the receiver. 
     * </p>
     *
     * @return the items in the receiver
     *
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     */
    public PShelfItem[] getItems()
    {
        checkWidget();
        return (PShelfItem[])items.toArray(new PShelfItem[items.size()]);
    }
    
    /**
     * Adds the listener to the collection of listeners who will
     * be notified when the receiver's selection changes, by sending
     * it one of the messages defined in the <code>SelectionListener</code>
     * interface.
     * <p>
     * When <code>widgetSelected</code> is called, the item field of the event object is valid.
     * <code>widgetDefaultSelected</code> is not called.
     * </p>
     *
     * @param listener the listener which should be notified
     *
     * @exception IllegalArgumentException <ul>
     *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
     * </ul>
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     *
     * @see SelectionListener
     * @see #removeSelectionListener
     * @see SelectionEvent
     */
    public void addSelectionListener(SelectionListener listener) {
        checkWidget ();
        if (listener == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
        TypedListener typedListener = new TypedListener(listener);
        addListener(SWT.Selection,typedListener);
        addListener(SWT.DefaultSelection,typedListener);
    }
    
    /**
     * Removes the listener from the collection of listeners who will
     * be notified when the receiver's selection changes.
     *
     * @param listener the listener which should no longer be notified
     *
     * @exception IllegalArgumentException <ul>
     *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
     * </ul>
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     *
     * @see SelectionListener
     * @see #addSelectionListener
     */
    public void removeSelectionListener (SelectionListener listener) {
        checkWidget ();
        if (listener == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);

        removeListener(SWT.Selection, listener);
        removeListener(SWT.DefaultSelection,listener);  
    }
}
