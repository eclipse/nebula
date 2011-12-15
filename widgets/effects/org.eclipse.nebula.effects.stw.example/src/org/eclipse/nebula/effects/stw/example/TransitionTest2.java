/*******************************************************************************
 * Copyright (c) 2010 Ahmed Mahran and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html 
 *
 * Contributors:
 *     Ahmed Mahran - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.effects.stw.example;

import java.util.Formatter;

import org.eclipse.nebula.effects.stw.TransitionManager;
import org.eclipse.nebula.effects.stw.Transitionable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;


/**
 * @author Ahmed Mahran (ahmahran@gmail.com)
 */
public class TransitionTest2 extends AbstractSTWDemoFrame {

    private int curImg = 0;
    private Image imgs[];

    @Override
    public void init() {
        final TransitionTest2 me = this;
        
        _containerComposite.setLayout(new FormLayout());
        //_containerComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        
        imgs = new Image[6];
        for(int i = 0; i < imgs.length; i++)
            imgs[i] = new Image(_containerComposite.getDisplay(), getClass().getResourceAsStream(new Formatter().format("%02d.jpg", i+1).toString()));
        
        final Canvas cnvs = new Canvas(_containerComposite, SWT.DOUBLE_BUFFERED);
        cnvs.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        FormData fd = new FormData();
        fd.left = new FormAttachment(0, 5);
        fd.right = new FormAttachment(100, -5);
        fd.top = new FormAttachment(0, 5);
        fd.bottom = new FormAttachment(100, -35);
        cnvs.setLayoutData(fd);
        
        final Button btn = new Button(_containerComposite, SWT.PUSH);
        btn.setText("Hit me!");
        fd = new FormData();
        fd.top = new FormAttachment(cnvs, 5);
        fd.bottom = new FormAttachment(100, -5);
        fd.left = new FormAttachment(0, 5);
        fd.right = new FormAttachment(100, -5);
        btn.setLayoutData(fd);
        
        _tm = new TransitionManager(new Transitionable() {
            public void setSelection(int index) {
                me.curImg = index;
            }
        
            public int getSelection() {
                return me.curImg;
            }
        
            public Control getControl(int index) {
                return cnvs;
            }
        
            public Composite getComposite() {
                return _containerComposite;
            }
        
            public double getDirection(int toIndex, int fromIndex) {
                return getSelectedDirection(toIndex, fromIndex);
            }
        
            public void addSelectionListener(SelectionListener listener) {
                final SelectionListener transitionableListener = listener;
                btn.addSelectionListener(new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent e) {
                        me.curImg = (me.curImg + 1) % me.imgs.length;
                        transitionableListener.widgetSelected(e);
                    }
                });
            }
        });
        
        cnvs.addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent e) {
                Image img = me.imgs[me.curImg];
                e.gc.drawImage(
                        img
                        , 0
                        , 0
                        , img.getImageData().width
                        , img.getImageData().height
                        , 0
                        , 0
                        , cnvs.getSize().x
                        , cnvs.getSize().y
                        );
            }
        });
        
    }

}
