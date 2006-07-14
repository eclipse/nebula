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
import org.eclipse.swt.layout.GridData;
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
import org.eclipse.swt.widgets.Label;
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
    private Grid grid;
    private Button showLines;
    private Button showHeader;
    private Button showRowHeader;
    private Button columnScrolling;
    private Button moveableColumns;
    private Button resizeableColumns;
    private Button left;
    private Button center;
    private Button right;
    private Button columnCheck;
    private Button check;
    private Button toggle;
    private Button cellSelection;
    private Button selectionEnabled;
    private Button columnCellSelection;

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
        styles.setLayoutData(new GridData(GridData.FILL_VERTICAL));
        
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

        check = ButtonFactory.create(styles, SWT.CHECK, "SWT.CHECK", listenerRecreates,false);
        Label l = new Label(styles,SWT.NONE);
        l.setText("Styles for Second Column:");
        
        Composite alignmentComp = new Composite(styles,SWT.NONE);
        GridLayoutFactory.swtDefaults().margins(0,0).applyTo(alignmentComp);
        
        left = ButtonFactory.create(alignmentComp,SWT.RADIO, "SWT.LEFT",listenerRecreates,true);
        center = ButtonFactory.create(alignmentComp,SWT.RADIO, "SWT.CENTER",listenerRecreates,false);
        right = ButtonFactory.create(alignmentComp,SWT.RADIO, "SWT.RIGHT",listenerRecreates,false);
        
        columnCheck = ButtonFactory.create(styles, SWT.CHECK, "SWT.CHECK", listenerRecreates, false);
        
        l = new Label(styles,SWT.NONE);
        l.setText("Styles for Column Group:");
        
        toggle = ButtonFactory.create(styles,SWT.CHECK,"SWT.TOGGLE",listenerRecreates,true);
        
        Group other = new Group(parent,SWT.SHADOW_ETCHED_IN);
        other.setText("Other");
        other.setLayout(new GridLayout());
        other.setLayoutData(new GridData(GridData.FILL_VERTICAL));
        
        showLines = ButtonFactory.create(other, SWT.CHECK, "Show Lines", new Listener()
        {
            public void handleEvent(Event event)
            {
                grid.setLinesVisible(showLines.getSelection());
            }
        }, true);
        
        showHeader = ButtonFactory.create(other, SWT.CHECK, "Show Column Headers", new Listener()
        {
            public void handleEvent(Event event)
            {
                grid.setHeaderVisible(showHeader.getSelection());
            }
        }, true);
        
        showRowHeader = ButtonFactory.create(other, SWT.CHECK, "Show Row Headers", new Listener()
        {
            public void handleEvent(Event event)
            {
                grid.setRowHeaderVisible(showRowHeader.getSelection());
                columnScrolling.setEnabled(!showRowHeader.getSelection());
                if (showRowHeader.getSelection())
                    columnScrolling.setSelection(true);
            }
        });
        
        columnScrolling = ButtonFactory.create(other, SWT.CHECK, "Scroll by Column", new Listener()
        {
            public void handleEvent(Event event)
            {
                grid.setColumnScrolling(columnScrolling.getSelection());
            }        
        });
        
        moveableColumns = ButtonFactory.create(other, SWT.CHECK, "Moveable Columns", new Listener()
        {
            public void handleEvent(Event event)
            {
                for (int i = 0; i < grid.getColumns().length; i++)
                {
                    grid.getColumn(i).setMoveable(moveableColumns.getSelection());
                }
            }
        }, true);

        resizeableColumns = ButtonFactory.create(other, SWT.CHECK, "Resizeable Columns",new Listener()
         {
             public void handleEvent(Event event)
             {
                 for (int i = 0; i < grid.getColumns().length; i++)
                 {
                     grid.getColumn(i).setResizeable(resizeableColumns.getSelection());
                 }
             }
         }, true);
        
        selectionEnabled = ButtonFactory.create(other, SWT.CHECK, "Selection Enabled",new Listener()
                                             {
            public void handleEvent(Event event)
            {
                for (int i = 0; i < grid.getColumns().length; i++)
                {
                    grid.setSelectionEnabled(selectionEnabled.getSelection());
                }
            }
        },true);
        
        cellSelection = ButtonFactory.create(other, SWT.CHECK, "Cell Selection",new Listener()
                                             {
            public void handleEvent(Event event)
            {
                for (int i = 0; i < grid.getColumns().length; i++)
                {
                    grid.setCellSelectionEnabled(cellSelection.getSelection());
                }
            }
        });
        
        
        l = new Label(other,SWT.NONE);
        l.setText("Properties for Second Column:");
        
        columnCellSelection = ButtonFactory.create(other, SWT.CHECK, "Cell Selection Enabled",new Listener()
                                             {
            public void handleEvent(Event event)
            {
                for (int i = 0; i < grid.getColumns().length; i++)
                {
                    grid.getColumn(1).setCellSelectionEnabled(columnCellSelection.getSelection());
                }
            }
        },true);
        
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
        
        if (check.getSelection())
            style |= SWT.CHECK;
        
        grid = new Grid(parent, style);
        grid.setHeaderVisible(true);
        
        GridColumn col = new GridColumn(grid, SWT.NONE);
        col.setTree(true);
        col.setText("First Column");
        col.setWidth(140);
        
//        if (showHeaderImage.getSelection())
//            col.setImage(ExamplesPlugin.getImage("icons/eclipse.png"));
        
        int groupStyle = SWT.NONE;
        if (toggle.getSelection())
            groupStyle |= SWT.TOGGLE;
        
        GridColumnGroup group = new GridColumnGroup(grid,groupStyle);
        group.setText("Column Grouping");
        
        
        int colStyle = SWT.NONE;
        
        if (left.getSelection())
            colStyle |= SWT.LEFT;
        if (center.getSelection())
            colStyle |= SWT.CENTER;
        if (right.getSelection())
            colStyle |= SWT.RIGHT;
        if (columnCheck.getSelection())
            colStyle |= SWT.CHECK;
        
        GridColumn col2 = new GridColumn(group,colStyle);
        col2.setText("The Column #2");
        col2.setWidth(230);
        col2.setResizeable(false);
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
        
        final GridItem item = new GridItem(grid,SWT.NONE);
        item.setText("Item #000000000000000000000");
        item.setText(1,"Test data ");       
        item.setText(2,"asdfjas;dlfjk");
        item.setColumnSpan(2,1);
        
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
     
        item55.setText(1,"This cell spans over many columns, use setColumnSpan method to achieve this");
        item55.setChecked(1,true);
       
        
//        item55.setForeground(1,Display.getCurrent().getSystemColor(SWT.COLOR_RED));
//        item55.setBackground(1,Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN));
        
        item55.setColumnSpan(1,2);
        
        
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
        
        GridColumn col4 = new GridColumn(grid,SWT.NONE);
        col4.setText("4th col");
        col4.setWidth(50);

        
        grid.setRowHeaderVisible(showRowHeader.getSelection());
        grid.setHeaderVisible(showHeader.getSelection());
        grid.setLinesVisible(showLines.getSelection());
        grid.setColumnScrolling(columnScrolling.getSelection());
        for (int i = 0; i < grid.getColumns().length; i++)
        {
            grid.getColumn(i).setMoveable(moveableColumns.getSelection());
        }
        for (int i = 0; i < grid.getColumns().length; i++)
        {
            grid.getColumn(i).setResizeable(resizeableColumns.getSelection());
        }  
        grid.setSelectionEnabled(selectionEnabled.getSelection());
        grid.setCellSelectionEnabled(cellSelection.getSelection());
        grid.getColumn(1).setCellSelectionEnabled(columnCellSelection.getSelection());
        
        for (int i = 0; i < grid.getColumns().length; i++)
        {
            this.addEventParticipant(grid.getColumn(i));            
        }
        for (int i = 0; i < grid.getColumnGroups().length; i++)
        {
            this.addEventParticipant(grid.getColumnGroup(i));            
        }
        
        return grid;
    }

    public String[] createLinks()
    {
        String[] links = new String[3];
        
        links[0] = "<a href=\"http://www.eclipse.org/nebula/widgets/grid/grid.php\">Grid Home Page</a>";
        
        links[1] = "<a href=\"http://www.eclipse.org/nebula/widgets/grid/snippets.php\">Snippets</a>";
        
        links[2] = "<a href=\"https://bugs.eclipse.org/bugs/buglist.cgi?query_format=advanced&short_desc_type=allwordssubstr&short_desc=&classification=Technology&product=Nebula&component=Grid&long_desc_type=allwordssubstr&long_desc=&bug_file_loc_type=allwordssubstr&bug_file_loc=&status_whiteboard_type=allwordssubstr&status_whiteboard=&keywords_type=allwords&keywords=&emailtype1=substring&email1=&emailtype2=substring&email2=&bugidtype=include&bug_id=&votes=&chfieldfrom=&chfieldto=Now&chfieldvalue=&cmdtype=doit&order=Reuse+same+sort+as+last+time&field0-0-0=noop&type0-0-0=noop&value0-0-0=\">Bugs</a>";
        
        return links;
    }

}
