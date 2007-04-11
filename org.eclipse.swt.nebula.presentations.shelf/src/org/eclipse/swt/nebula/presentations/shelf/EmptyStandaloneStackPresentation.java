// ==================================================================
// EmptyStandaloneStackPresentation.java
// ==================================================================
// IBM Confidential
// OCO Source Materials
// © Copyright IBM Corp. 2005
// ==================================================================

package org.eclipse.swt.nebula.presentations.shelf;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.presentations.IPresentablePart;
import org.eclipse.ui.presentations.IStackPresentationSite;
import org.eclipse.ui.presentations.StackDropResult;
import org.eclipse.ui.presentations.StackPresentation;

/**
 * 
 *
 * @author chris.gross@us.ibm.com
 * @since 2.0.0
 */
public class EmptyStandaloneStackPresentation extends StackPresentation
{
    private Composite partParent;
    private Composite borderComposite;
    
    public EmptyStandaloneStackPresentation(IStackPresentationSite stackSite, Composite parent)
    {
        super(stackSite);
        
        partParent = parent;
        
        borderComposite = new Composite(parent,SWT.BORDER);
        
    }
    
    /** 
     * {@inheritDoc}
     */
    @Override
    public void addPart(IPresentablePart newPart, Object cookie)
    {

    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public void dispose()
    {
        borderComposite.dispose();
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public StackDropResult dragOver(Control currentControl, Point location)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public Control getControl()
    {
        return borderComposite;
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public Control[] getTabList(IPresentablePart part)
    {
        return null;
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public void removePart(IPresentablePart oldPart)
    {        
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public void selectPart(IPresentablePart toSelect)
    {
        toSelect.setVisible(true);
        if (toSelect.getToolBar() != null)
            toSelect.getToolBar().setVisible(true);
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
        borderComposite.setBounds(bounds);
        
        Rectangle clientArea = borderComposite.getClientArea();
        
        IPresentablePart part = getSite().getSelectedPart();
        
        if (part == null) return;
        
        Control partTB = part.getToolBar();
        
        Rectangle tbBounds = new Rectangle(0,0,0,0);
        
        if (partTB != null)
        {
            Point size = partTB.computeSize(clientArea.width, SWT.DEFAULT);
            
            tbBounds.width = size.x;
            tbBounds.height = Math.min(size.y, clientArea.height);

            tbBounds = borderComposite.getDisplay().map(borderComposite, partTB.getParent(), tbBounds);
            
            partTB.setBounds(tbBounds);
            
            clientArea.y += tbBounds.height;
            clientArea.height -= tbBounds.height;  
        }
        
        clientArea = borderComposite.getDisplay().map(borderComposite, partParent, clientArea);

        part.setBounds(clientArea);
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public void setState(int state)
    {
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public void setVisible(boolean isVisible)
    {        
        getSite().getSelectedPart().setVisible(true);
        if (getSite().getSelectedPart().getToolBar() != null)
            getSite().getSelectedPart().getToolBar().setVisible(true);
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public void showPaneMenu()
    {
        // TODO Auto-generated method stub

    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public void showSystemMenu()
    {
        // TODO Auto-generated method stub

    }

}
