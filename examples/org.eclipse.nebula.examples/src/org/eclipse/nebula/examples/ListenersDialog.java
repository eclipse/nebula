/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    cgross - initial API and implementation
 *******************************************************************************/ 

package org.eclipse.nebula.examples;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * This dialog shows the list of listeners that may be selected.
 *
 * @author cgross
 */
public class ListenersDialog extends Dialog
{
    private ArrayList selectedEvents;
    private Table table;
    
    protected ListenersDialog(Shell parentShell)
    {
        super(parentShell);
    }

    public int open(ArrayList selectedEvents)
    {
        this.selectedEvents = selectedEvents;
        return open();
    }
    
    /** 
     * {@inheritDoc}
     */
    protected Control createDialogArea(Composite parent)
    {
        this.getShell().setText("Select Listeners");
        
        Composite composite = (Composite) super.createDialogArea(parent);
        Composite comp2 = new Composite(composite,SWT.NONE);
        comp2.setLayout(new GridLayout(2,false));
        
        table = new Table(comp2,SWT.CHECK | SWT.BORDER);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.verticalSpan = 2;
        table.setLayoutData(gd);
        
        for (int i = 0; i < AbstractExampleTab.eventIds.length; i++)
        {
            TableItem item = new TableItem(table,SWT.NONE);
            item.setText(AbstractExampleTab.eventNames[i]);
            item.setData(new Integer(AbstractExampleTab.eventIds[i]));
            
            for (Iterator iter = selectedEvents.iterator(); iter.hasNext();)
            {
                Integer eventId = (Integer)iter.next();
                if (eventId.equals(new Integer(AbstractExampleTab.eventIds[i])))
                {
                    item.setChecked(true);
                    break;
                }
                
            }
        }
        
        Button selAll = ButtonFactory.create(comp2,SWT.PUSH,"Select All",new Listener()
        {
            public void handleEvent(Event event)
            {
                for (int i = 0; i < table.getItemCount(); i++)
                {
                    table.getItem(i).setChecked(true);
                }
            }        
        });
        gd = new GridData();
        gd.grabExcessVerticalSpace = false;
        gd.verticalAlignment = SWT.BEGINNING;
        selAll.setLayoutData(gd);
        
        ButtonFactory.create(comp2, SWT.PUSH, "Deselect All", new Listener()
        {
            public void handleEvent(Event event)
            {
                for (int i = 0; i < table.getItemCount(); i++)
                {
                    table.getItem(i).setChecked(false);
                }
            }
        }).setLayoutData(gd);
        
        return composite;
        
    }

    /** 
     * {@inheritDoc}
     */
    protected void okPressed()
    {
        selectedEvents.clear();
        
        for (int i = 0; i < table.getItemCount(); i++)
        {
            TableItem item = table.getItem(i);
            if (item.getChecked())
                selectedEvents.add(item.getData());
        }       
        
        super.okPressed();
    }

}
