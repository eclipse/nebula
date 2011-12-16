// ==================================================================
// CTabFolderStackPresentation.java
// ==================================================================
// IBM Confidential
// OCO Source Materials
// © Copyright IBM Corp. 2005
// ==================================================================

package org.eclipse.swt.nebula.presentations.shelf;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.presentations.IPresentablePart;
import org.eclipse.ui.presentations.IStackPresentationSite;
import org.eclipse.ui.presentations.StackDropResult;
import org.eclipse.ui.presentations.StackPresentation;

import java.net.URL;

/**
 * 
 *
 * @author chris.gross@us.ibm.com
 * @since 2.0.0
 */
public class CTabFolderStackPresentation extends StackPresentation
{

    private static final String DATAKEY_PART = "IPresentablePart";
    private static final String DATAKEY_ANIMATION = "Busy Anim Player";
    private Composite partParent;
    private CTabFolder tabFolder;
    private boolean ignoreSelection;
    private Image[] busyImages;

    /**
     * @param stackSite
     */
    public CTabFolderStackPresentation(IStackPresentationSite stackSite, Composite parent)
    {
        super(stackSite);
        
        partParent = parent;
        
        tabFolder = new CTabFolder(parent, SWT.BORDER | SWT.FLAT | SWT.CLOSE);
        tabFolder.setMaximizeVisible(true);
        //tabFolder.setSimple(false);
        
        tabFolder.setTabHeight(tabFolder.getTabHeight() + 6);
        
        tabFolder.setSelectionForeground(tabFolder.getDisplay().getSystemColor(SWT.COLOR_TITLE_FOREGROUND));
        Color[] colors = new Color[]{tabFolder.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT),tabFolder.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND)};
        tabFolder.setSelectionBackground(colors, new int[]{50}, true);
                
        tabFolder.addListener(SWT.Selection, new Listener()
        {        
            public void handleEvent(Event event)
            {
                if (event.item == null) return;
                
                if (!ignoreSelection)
                    getSite().selectPart((IPresentablePart)event.item.getData(DATAKEY_PART));
            }        
        });
        
        tabFolder.addCTabFolder2Listener(new CTabFolder2Listener()
        {        
            public void showList(CTabFolderEvent event)
            {
               
            }        
            public void restore(CTabFolderEvent event)
            {
                getSite().setState(IStackPresentationSite.STATE_RESTORED);
            }
        
            public void minimize(CTabFolderEvent event)
            {
                getSite().setState(IStackPresentationSite.STATE_MINIMIZED);
            }
        
            public void maximize(CTabFolderEvent event)
            {
                getSite().setState(IStackPresentationSite.STATE_MAXIMIZED);
            }
        
            public void close(CTabFolderEvent event)
            {
                IPresentablePart part = (IPresentablePart)event.item.getData(DATAKEY_PART);
                getSite().close(new IPresentablePart[]{part});
            }        
        });
        
        tabFolder.addListener(SWT.MouseDoubleClick, new Listener()
        {        
            public void handleEvent(Event event)
            {
                if (event.y > tabFolder.getTabHeight()) return;
                
                if (getSite().getState() == IStackPresentationSite.STATE_MAXIMIZED)
                {
                    getSite().setState(IStackPresentationSite.STATE_RESTORED);  
                }
                else
                {
                    getSite().setState(IStackPresentationSite.STATE_MAXIMIZED); 
                }                             
            }        
        });
        
        Menu contextMenu = new Menu(tabFolder);
        MenuItem close = new MenuItem(contextMenu,SWT.NONE);
        close.setText("Close");
        close.addListener(SWT.Selection, new Listener()
        {        
            public void handleEvent(Event event)
            {
                IPresentablePart part = getSite().getSelectedPart();
                if (part == null) return;
                getSite().close(new IPresentablePart[]{part});
            }
        });
        MenuItem closeOthers = new MenuItem(contextMenu,SWT.NONE);
        closeOthers.setText("Close Others");
        closeOthers.addListener(SWT.Selection, new Listener()
        {        
            public void handleEvent(Event event)
            {
                IPresentablePart part = getSite().getSelectedPart();
                if (part == null) return;
                
                IPresentablePart[] parts = getSite().getPartList();
                if (parts.length < 2) return;
                
                IPresentablePart[] closeParts = new IPresentablePart[parts.length - 1];
                
                int j = 0;
                for (int i = 0; i < parts.length; i++)
                {
                    if (parts[i] != part)
                    {
                        closeParts[j] = parts[i];
                        j++;
                    }
                }
                
                getSite().close(closeParts);
            }        
        });
        MenuItem closeAll = new MenuItem(contextMenu,SWT.NONE);
        closeAll.setText("Close All");
        closeAll.addListener(SWT.Selection, new Listener()
        {        
            public void handleEvent(Event event)
            {
                IPresentablePart[] parts = getSite().getPartList();
                if (parts == null) return;
                getSite().close(parts);
            }        
        });
        
        tabFolder.setMenu(contextMenu);
        
        //init busy images
        busyImages = new Image[8];
        for (int i = 0; i < 8; i++)
        {            
            URL imgURL = Platform.getBundle("org.eclipse.swt.nebula.presentations.shelf").getResource("icons/busy" + (i + 1) + ".gif");
            Image img = ImageDescriptor.createFromURL(imgURL).createImage();
            busyImages[i] = img;
        }
    }
    /** 
     * {@inheritDoc}
     */
    @Override
    public void addPart(final IPresentablePart newPart, Object cookie)
    {
        ignoreSelection = true;
        final CTabItem item = new CTabItem(tabFolder,SWT.NONE);
        ignoreSelection = false;
        item.setData(DATAKEY_PART,newPart);
        
        updateItem(newPart);
        
        newPart.addPropertyListener(new IPropertyListener()
        {        
            public void propertyChanged(Object source, int propId)
            {
                updateItem(newPart);
            }        
        });
    }
    
    private void updateItem(final IPresentablePart part)
    {
        CTabItem item = getItem(part);
        
        String dirty = "";
        if (part.isDirty())
            dirty = "*";
        
        item.setText(dirty + part.getName());
        item.setImage(part.getTitleImage());
        
        resizeSelectedPart();
        
        if (part.isBusy() && item.getData(DATAKEY_ANIMATION) == null)
        {
            BusyItemAnimation busyAnim = new BusyItemAnimation(item);
            busyAnim.setImages(busyImages);
            item.setData(DATAKEY_ANIMATION,busyAnim);
            busyAnim.start();
        }
        else if (!part.isBusy() && item.getData(DATAKEY_ANIMATION) != null)
        {
            BusyItemAnimation busyAnim = (BusyItemAnimation)item.getData(DATAKEY_ANIMATION);
            busyAnim.stop();
            item.setData(DATAKEY_ANIMATION,null);
            item.setImage(part.getTitleImage());
        }
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public void dispose()
    {
        tabFolder.dispose();
        for (int i = 0; i < busyImages.length; i++)
        {
            busyImages[i].dispose();
        }
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public StackDropResult dragOver(Control currentControl, Point location)
    {
        return null;
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public Control getControl()
    {
        return tabFolder;
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public Control[] getTabList(IPresentablePart part)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public void removePart(IPresentablePart oldPart)
    {
        getItem(oldPart).dispose();
        resizeSelectedPart();
    }
    
    private CTabItem getItem(IPresentablePart part)
    {
        CTabItem[] items = tabFolder.getItems();
        for (int i = 0; i < items.length; i++)
        {
            if (items[i].getData(DATAKEY_PART) == part)
            {
                return items[i];
            }
        }
        return null;
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public void selectPart(IPresentablePart toSelect)
    {
        tabFolder.setSelection(getItem(toSelect));
        
        IPresentablePart[] parts = getSite().getPartList();
        for (int i = 0; i < parts.length; i++)
        {
            parts[i].setVisible(parts[i] == toSelect);
        }
        
        resizeSelectedPart();
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public void setActive(int newState)
    {
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public void setBounds(Rectangle bounds)
    {
        tabFolder.setBounds(bounds);
        resizeSelectedPart(); 
    }
    
    private void resizeSelectedPart()
    {
        IPresentablePart part = getSite().getSelectedPart();
        
        if (part == null) return;
        
        CTabItem item = getItem(part);
        
        if (item == null) return;
        
        Rectangle bounds = tabFolder.getClientArea();
        
        
        bounds = item.getDisplay().map(tabFolder, partParent, bounds);

        part.setBounds(bounds);
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public void setState(int state)
    {
        if (state == IStackPresentationSite.STATE_MAXIMIZED)
            tabFolder.setMaximized(true);
        if (state == IStackPresentationSite.STATE_RESTORED)
        {
            tabFolder.setMaximized(false);
            tabFolder.setMinimized(false);
        }
        if (state == IStackPresentationSite.STATE_MINIMIZED)
            tabFolder.setMinimized(true);
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public void setVisible(boolean isVisible)
    {
        tabFolder.setVisible(isVisible);
        if (getSite().getSelectedPart() == null) return;
        getSite().getSelectedPart().setVisible(isVisible);
        if (getSite().getSelectedPart().getToolBar() != null)
            getSite().getSelectedPart().getToolBar().setVisible(isVisible);
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public void showPaneMenu()
    {
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public void showSystemMenu()
    {
    }
}
