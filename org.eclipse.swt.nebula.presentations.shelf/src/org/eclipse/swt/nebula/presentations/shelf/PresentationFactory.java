// ==================================================================
// PresentationFactory.java
// ==================================================================
// IBM Confidential
// OCO Source Materials
// © Copyright IBM Corp. 2005
// ==================================================================

package org.eclipse.swt.nebula.presentations.shelf;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.presentations.AbstractPresentationFactory;
import org.eclipse.ui.presentations.IStackPresentationSite;
import org.eclipse.ui.presentations.StackPresentation;

import java.util.Map;

/**
 * 
 *
 * @author chris.gross@us.ibm.com
 * @since 2.0.0
 */
public class PresentationFactory extends AbstractPresentationFactory
{

    /** 
     * {@inheritDoc}
     */
    @Override
    public StackPresentation createEditorPresentation(Composite parent, IStackPresentationSite site)
    {
        return new CTabFolderStackPresentation(site,parent);
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public StackPresentation createStandaloneViewPresentation(Composite parent,
                                                              IStackPresentationSite site,
                                                              boolean showTitle)
    {
        if (showTitle)
        {
            if ("group".equals(site.getProperty("type")))
            {
                return new PGroupStackPresentation(site,parent);
            }            
            else
            {
                return new ExpandBarStandaloneStackPresentation(site,parent);
            }            
        }
        else
        {
            return new EmptyStandaloneStackPresentation(site,parent);
        }
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public StackPresentation createViewPresentation(Composite parent, IStackPresentationSite site)
    {
        if ("group".equals(site.getProperty("type")))
        {
            return new PGroupStackPresentation(site,parent);
        }     
        return new PShelfStackPresentation(site,parent);
    }

}
