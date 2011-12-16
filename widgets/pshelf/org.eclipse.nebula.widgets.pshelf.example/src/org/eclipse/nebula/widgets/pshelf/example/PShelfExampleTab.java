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
package org.eclipse.nebula.widgets.pshelf.example;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.nebula.widgets.pshelf.AbstractRenderer;
import org.eclipse.nebula.widgets.pshelf.PShelf;
import org.eclipse.nebula.widgets.pshelf.PShelfItem;
import org.eclipse.nebula.widgets.pshelf.PaletteShelfRenderer;
import org.eclipse.nebula.widgets.pshelf.RedmondShelfRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.nebula.examples.AbstractExampleTab;
import org.eclipse.nebula.examples.ButtonFactory;
import org.eclipse.nebula.examples.ExamplesView;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class PShelfExampleTab extends AbstractExampleTab
{

    private Button simple;
    private Combo rendererCombo;
    private Button border;

    public Control createControl(Composite parent)
    {
        int style = SWT.NONE;
        
        if (border.getSelection())
            style |= SWT.BORDER;
        
        if (simple.getSelection())
            style |= SWT.SIMPLE;
        
        AbstractRenderer renderer = null;
        Image i = null;
        
        if (rendererCombo.getText().indexOf("Redmond") != -1)
        {
            renderer = new RedmondShelfRenderer();
            i = ExamplesView.getImage("icons/woman3.png");
        }
        else
        {
            renderer = new PaletteShelfRenderer();
            i = ExamplesView.getImage("icons/eclipse.png");
        }
        
        PShelf shelf = new PShelf(parent,style);
        
        shelf.setRenderer(renderer);
        
        PShelfItem item = new PShelfItem(shelf,SWT.NONE);
        item.setText("First Item");
        item.setImage(i);
        
        item.getBody().setLayout(new FillLayout());
        Tree tree = new Tree(item.getBody(),SWT.NONE);
        TreeItem tItem = new TreeItem(tree,SWT.NONE);
        tItem.setText("tree item");
        tItem = new TreeItem(tItem,SWT.NONE);
        tItem.setText("child tree item");
        tItem = new TreeItem(tItem,SWT.NONE);
        tItem.setText("child two");
        
        PShelfItem item2 = new PShelfItem(shelf,SWT.NONE);
        item2.setText("2nd Item");
        item2.setImage(i);
        
        item2.getBody().setLayout(new FillLayout());
        
        Text t = new Text(item2.getBody(),SWT.WRAP);
        t.setText("The England defender and United captain infamously ran the length of the pitch at Old Trafford to celebrate Rio Ferdinand's late winner in front of the Liverpool fans last season by screaming, clutching his shirt and pointing to the United badge. ");
        
        
        PShelfItem item3 = new PShelfItem(shelf,SWT.NONE);
        item3.setText("Item Number 3");
        item3.setImage(i);
        
        item3.getBody().setLayout(new FillLayout());
        
        Table table = new Table(item3.getBody(),SWT.NONE);
        table.setHeaderVisible(true);
        TableColumn col = new TableColumn(table,SWT.NONE);
        col.setText("Column1");
        col.setWidth(100);
        col = new TableColumn(table,SWT.NONE);
        col.setText("Column2");
        col.setWidth(100);
        
        TableItem tableItem = new TableItem(table,SWT.NONE);
        tableItem.setText("afsffsd");
        tableItem.setText(1,"asfdsdf");
        
        return shelf;
    }

    public String[] createLinks()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void createParameters(Composite parent)
    {
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(parent);
        
        Listener listenerRecreates = new Listener()
        {
            public void handleEvent(Event event)
            {
                if (event.widget instanceof Button)
                {
                    Button b = (Button)event.widget;
                    if ((b.getStyle() & SWT.RADIO) != 0)
                    {
                        if (!b.getSelection()) return;
                    }
                }
                recreateExample();
            }        
        };
        
        Group styles = new Group(parent,SWT.NONE);
        styles.setText("Styles");
        GridLayoutFactory.swtDefaults().applyTo(styles);
        GridDataFactory.fillDefaults().applyTo(styles);
        
        border = ButtonFactory.create(styles, SWT.CHECK, "SWT.BORDER",listenerRecreates, false);
        simple = ButtonFactory.create(styles, SWT.CHECK, "SWT.SIMPLE",listenerRecreates, false);

        Group parms = new Group(parent,SWT.NONE);
        parms.setText("Other");
        GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).applyTo(parms);
        
        new Label(parms,SWT.NONE).setText("Renderer:");
        
        rendererCombo = new Combo(parms,SWT.READ_ONLY);
        rendererCombo.setItems(new String[]{"PaletteShelfRenderer","RedmondShelfRenderer"});
        rendererCombo.select(0);
        rendererCombo.addListener(SWT.Selection, listenerRecreates);

        
    }

}
