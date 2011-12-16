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
package org.eclipse.nebula.widgets.pgroup.example;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.nebula.widgets.pgroup.AbstractGroupStrategy;
import org.eclipse.nebula.widgets.pgroup.AbstractRenderer;
import org.eclipse.nebula.widgets.pgroup.ChevronsToggleRenderer;
import org.eclipse.nebula.widgets.pgroup.FormGroupStrategy;
import org.eclipse.nebula.widgets.pgroup.MinMaxToggleRenderer;
import org.eclipse.nebula.widgets.pgroup.PGroup;
import org.eclipse.nebula.widgets.pgroup.RectangleGroupStrategy;
import org.eclipse.nebula.widgets.pgroup.SimpleGroupStrategy;
import org.eclipse.nebula.widgets.pgroup.TreeNodeToggleRenderer;
import org.eclipse.nebula.widgets.pgroup.TwisteToggleRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
import org.eclipse.swt.widgets.Text;

public class PGroupExampleTab extends AbstractExampleTab
{

    private Combo strategy;
    private Button imageLeft;
    private Button toggleLeft;
    private Button lineCenter;
    private Combo toggle;
    private Button smooth;
    private Button imageOnTop;
    
    private PGroup group;
    private Text text;

    public Control createControl(Composite parent)
    {
        
        AbstractRenderer toggleRenderer = null;
        
        if (toggle.getText().indexOf("Chevron") != -1)
        {
            toggleRenderer = new ChevronsToggleRenderer();
        }
        else if (toggle.getText().indexOf("MinMax") != -1)
        {
            toggleRenderer = new MinMaxToggleRenderer();
        }
        else if (toggle.getText().indexOf("Twiste") != -1)
        {
            toggleRenderer = new TwisteToggleRenderer();
        }
        else if (toggle.getText().indexOf("Tree") != -1)
        {
            toggleRenderer = new TreeNodeToggleRenderer();
        }
        
        AbstractGroupStrategy strat;
        if (strategy.getText().indexOf("Form") != -1)
        {
            strat = new FormGroupStrategy();
        } else if (strategy.getText().indexOf("Rectangle") != -1)
        {
            strat = new RectangleGroupStrategy();
        } else
        {
            strat = new SimpleGroupStrategy();
        }
        
        int style = SWT.NONE;
        
        if (smooth.getSelection())
            style |= SWT.SMOOTH;
        
        group = new PGroup(parent, style);
        group.setStrategy(strat);
        group.setToggleRenderer(toggleRenderer);
        group.setText(text.getText());
        group.setImage(ExamplesView.getImage("icons/woman3.png"));
        
        int imagePos = SWT.NONE;
        if (imageLeft.getSelection())
        {
            imagePos = SWT.LEAD;
        }
        else
        {
            imagePos = SWT.TRAIL;
        }
        
        if (imageOnTop.getSelection())
            imagePos |= SWT.TOP;
        
        group.setImagePosition(imagePos);
        
        if (toggleLeft.getSelection())
        {
            group.setTogglePosition(SWT.LEAD);
        }
        else
        {
            group.setTogglePosition(SWT.TRAIL);
        }
        
        if (lineCenter.getSelection())
        {
            group.setLinePosition(SWT.CENTER);
        }
        else
        {
            group.setLinePosition(SWT.BOTTOM);
        }
        
        group.setLayout(new GridLayout(3,false));
        Button b = new Button(group, SWT.PUSH);
        b.setText("Button 1");
        b = new Button(group, SWT.PUSH);
        b.setText("Button 22222");
        b = new Button(group, SWT.PUSH);
        b.setText("Button 3");
        return group;
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
        
        smooth = ButtonFactory.create(styles, SWT.CHECK, "SWT.SMOOTH",listenerRecreates, false);

        Group parms = new Group(parent,SWT.NONE);
        parms.setText("Other");
        GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).applyTo(parms);
        
        new Label(parms,SWT.NONE).setText("Strategy:");
        
        strategy = new Combo(parms,SWT.READ_ONLY);
        strategy.setItems(new String[]{"FormGroupStrategy","RectangleGroupStrategy","SimpleGroupStrategy"});
        strategy.select(0);
        strategy.addListener(SWT.Selection, listenerRecreates);

        new Label(parms,SWT.NONE).setText("Toggle:");
        
        toggle = new Combo(parms,SWT.READ_ONLY);
        toggle.setItems(new String[]{"null","ChevronToggleRenderer","TwisteToggleRenderer","TreeNodeToggleRenderer","MinMaxToggleRenderer"});
        toggle.select(1);
        toggle.addListener(SWT.Selection, listenerRecreates);

        
        
        Composite imagePos = new Composite(parms,SWT.NONE);
        GridDataFactory.swtDefaults().span(2,1).applyTo(imagePos);
        GridLayoutFactory.fillDefaults().applyTo(imagePos);
        
        imageLeft = ButtonFactory.create(imagePos, SWT.RADIO, "Image on Left",listenerRecreates, true);
        ButtonFactory.create(imagePos, SWT.RADIO, "Image on Right",listenerRecreates, false);
        imageOnTop = ButtonFactory.create(imagePos, SWT.CHECK, "Image outside Rectangle (SWT.TOP)",listenerRecreates, false);

        Composite togglePos = new Composite(parms,SWT.NONE);
        GridDataFactory.swtDefaults().span(2,1).applyTo(togglePos);
        GridLayoutFactory.fillDefaults().applyTo(togglePos);
        
        toggleLeft = ButtonFactory.create(togglePos, SWT.RADIO, "Toggle on Left",listenerRecreates, false);
        ButtonFactory.create(togglePos, SWT.RADIO, "Toggle on Right",listenerRecreates, true);

        
        Composite linePos = new Composite(parms,SWT.NONE);
        GridDataFactory.swtDefaults().span(2,1).applyTo(linePos);
        GridLayoutFactory.fillDefaults().applyTo(linePos);
        
        ButtonFactory.create(linePos, SWT.RADIO, "Line on Bottom",listenerRecreates, true);
        lineCenter = ButtonFactory.create(linePos, SWT.RADIO, "Line in Center",listenerRecreates, false);

        new Label(parms,SWT.NONE).setText("Text:");
        text = new Text(parms,SWT.BORDER);
        text.setText("PGroup Example");
        text.addListener(SWT.Modify, new Listener()
        {
            public void handleEvent(Event event)
            {
              group.setText(text.getText());
            }        
        });
        
        text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

}
