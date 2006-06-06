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
package org.eclipse.swt.nebula.examples.parts;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.nebula.examples.AbstractExampleTab;
import org.eclipse.swt.nebula.examples.ButtonFactory;
import org.eclipse.swt.nebula.widgets.grid.Grid;
import org.eclipse.swt.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.nebula.widgets.grid.GridColumnGroup;
import org.eclipse.swt.nebula.widgets.grid.GridItem;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;

/**
 * Demonstrates the Grid widget.
 *
 * @author cgross
 */
public class GridExampleTab extends AbstractExampleTab
{

    private Button vScroll;
    private Button hScroll;
    private Button border;
    private Button single;
    private Button multi;

    public GridExampleTab()
    {
        super();
    }

    public void createParameters(Composite parent)
    {
        GridLayoutFactory.swtDefaults().margins(0,0).numColumns(2).applyTo(parent);
        
        Group styles = new Group(parent,SWT.SHADOW_ETCHED_IN);
        styles.setText("Styles");
        styles.setLayout(new GridLayout());
        
        Listener listenerRecreates = new Listener()
        {
            public void handleEvent(Event event)
            {
                recreateExample();
            }        
        };
        
        vScroll = ButtonFactory.create(styles,SWT.CHECK,"SWT.V_SCROLL",listenerRecreates,true);
        hScroll = ButtonFactory.create(styles,SWT.CHECK,"SWT.H_SCROLL",listenerRecreates,true);
        border = ButtonFactory.create(styles,SWT.CHECK,"SWT.BORDER",listenerRecreates,true);
        
        Composite selectionComp = new Composite(styles,SWT.NONE);
        GridLayoutFactory.swtDefaults().margins(0,0).applyTo(selectionComp);
        
        single = ButtonFactory.create(selectionComp,SWT.RADIO,"SWT.SINGLE",listenerRecreates,true);
        multi = ButtonFactory.create(selectionComp,SWT.RADIO,"SWT.MULTI",listenerRecreates);

    }

    public Control createControl(Composite parent)
    {
        int style = SWT.NONE;
        
        if (vScroll.getSelection())
            style |= SWT.V_SCROLL;
        
        if (hScroll.getSelection())
            style |= SWT.H_SCROLL;
        
        if (border.getSelection())
            style |= SWT.BORDER;
            
        if (single.getSelection())
            style |= SWT.SINGLE;
        
        if (multi.getSelection())
            style |= SWT.MULTI;
        
        Grid grid = new Grid(parent, style);
        
        grid.setHeaderVisible(true);
        
        GridColumn col = new GridColumn(grid, SWT.CHECK);
        col.setTree(true);
        col.setText("First Column");
        col.setWidth(140);
//        col.setMoveable(setMoveableColumns.getSelection());
//        col.setResizeable(setResizeableColumns.getSelection());
        
//        if (showHeaderImage.getSelection())
//            col.setImage(ExamplesPlugin.getImage("icons/eclipse.png"));
        
        GridColumnGroup group = new GridColumnGroup(grid,SWT.TOGGLE);
        group.setText("Column Grouping");
        
        GridColumn col2 = new GridColumn(group,SWT.NONE);
        col2.setText("The Column #2");
        col2.setWidth(230);
        col2.setResizeable(false);
//        col2.setMoveable(setMoveableColumns.getSelection());
//        col2.setResizeable(setResizeableColumns.getSelection());
        //col2.setSummary(false);
        
        final GridColumn col3 = new GridColumn(group, SWT.NONE);
        col3.setSummary(false);
        
        col3.setText("Click me");
        col3.setWidth(100);
        col3.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected (SelectionEvent e) {
            }       
            public void widgetSelected(SelectionEvent e) {
                if (col3.getSort() == SWT.UP){
                    col3.setSort(SWT.DOWN);
                } else {
                    col3.setSort(SWT.UP);
                }               
            }       
        });
//        col3.setMoveable(setMoveableColumns.getSelection());
//        col3.setResizeable(setResizeableColumns.getSelection());
        
        final GridItem item = new GridItem(grid,SWT.NONE);
        item.setText("Item #000000000000000000000");
        item.setText(1,"Test data ");       
        item.setText(2,"asdfjas;dlfjk");
        
        GridItem item2 = new GridItem(item,SWT.NONE);
        item2.setText("first tree");
        item2.setText(1,"first tree");
        
        item2 = new GridItem(item,SWT.NONE);
        item2.setText("first tree");
        item2.setText(1,"first tree");
        
        
        item2 = new GridItem(item,SWT.NONE);
        item2.setText("first tree");
        item2.setText(1,"first tree");
        
        GridItem item22 = new GridItem(item2,SWT.NONE);
        item22.setText("second tree");
        item22.setText(1,"second tree");
        item22.setChecked(1,true);
        item22.setGrayed(1,true);
        item22.setColumnSpan(0,2);

        final GridItem item55 = new GridItem(grid,SWT.NONE);
        item55.setText("Item #55");
        item55.setText(1,"55");     
        item55.setText(2,"55");
        item55.setChecked(1,true);
       
        
        item55.setForeground(1,Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        item55.setBackground(1,Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN));
        
        item55.setColumnSpan(1,5);
        
        
        GridItem item52 = new GridItem(item55,SWT.NONE);
        item52.setText("second tree");
        item52.setText(1,"second tree");
        
        for (int i = 3;i < 13;i++){
            GridItem item3 = new GridItem(grid,SWT.NONE);
            item3.setText("Item #" + i);
            item3.setText(1,"Test data ");      
            item3.setText(2,"asdfjas;dlfjk");
            
            if (i == 5){
                Font f = new Font(Display.getCurrent(),"Tahoma",8,SWT.ITALIC | SWT.BOLD);
                item3.setFont(2,f);
                
                item3.setForeground(2,Display.getCurrent().getSystemColor(SWT.COLOR_DARK_MAGENTA));
            }
        }
        
//        grid.setRowHeaderVisible(showRowHeader.getSelection());
//        grid.setHeaderVisible(showHeader.getSelection());
//        grid.setLinesVisible(setLinesVisible.getSelection());
//        grid.setColumnScrolling(setColumnScrolling.getSelection());

        GridColumn col4 = new GridColumn(grid,SWT.NONE);
        col4.setText("4th col");
        col4.setWidth(50);
//        col4.setMoveable(setMoveableColumns.getSelection());
//        col4.setResizeable(setResizeableColumns.getSelection());
        
        return grid;
    }

}
