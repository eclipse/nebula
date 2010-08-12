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

import org.eclipse.nebula.effects.stw.Transition;
import org.eclipse.nebula.effects.stw.TransitionManager;
import org.eclipse.nebula.effects.stw.TransitionListener;
import org.eclipse.nebula.effects.stw.Transitionable;
import org.eclipse.nebula.effects.stw.transitions.CubicRotationTransition;
import org.eclipse.nebula.effects.stw.transitions.FadeTransition;
import org.eclipse.nebula.effects.stw.transitions.SlideTransition;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


/**
 * @author Ahmed Mahran (ahmahran@gmail.com)
 */
public class TransitionTest2 {

    private Shell sShell = null;
    private int curImg = 0;
    private Image imgs[];

    public static void main(String[] args) {
        /* Before this is run, be sure to set up the launch configuration (Arguments->VM Arguments)
         * for the correct SWT library path in order to run with the SWT dlls. 
         * The dlls are located in the SWT plugin jar.  
         * For example, on Windows the Eclipse SWT 3.1 plugin jar is:
         *       installation_directory\plugins\org.eclipse.swt.win32_3.1.0.jar
         */
        Display display = Display.getDefault();
        TransitionTest2 thisClass = new TransitionTest2();
        thisClass.createSShell();
        thisClass.sShell.open();
        while (!thisClass.sShell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }

    /**
     * This method initializes sShell
     */
    private void createSShell() {
        final TransitionTest2 me = this;
        
        sShell = new Shell();
        sShell.setText("Transition Test 2");
        sShell.setSize(new Point(800, 600));
        sShell.setLocation(0, 0);
        sShell.setLayout(new FormLayout());
        //sShell.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        
        imgs = new Image[6];
        for(int i = 0; i < imgs.length; i++)
            imgs[i] = new Image(sShell.getDisplay(), getClass().getResourceAsStream(new Formatter().format("%02d.jpg", i+1).toString()));
        
        final Canvas cnvs = new Canvas(sShell, SWT.DOUBLE_BUFFERED);
        cnvs.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        FormData fd = new FormData();
        fd.left = new FormAttachment(0, 5);
        fd.right = new FormAttachment(100, -5);
        fd.top = new FormAttachment(0, 5);
        fd.bottom = new FormAttachment(100, -35);
        cnvs.setLayoutData(fd);
        
        final Button btn = new Button(sShell, SWT.PUSH);
        btn.setText("Hit me!");
        fd = new FormData();
        fd.top = new FormAttachment(cnvs, 5);
        fd.bottom = new FormAttachment(100, -5);
        fd.left = new FormAttachment(0, 5);
        fd.right = new FormAttachment(100, -5);
        btn.setLayoutData(fd);
        
        TransitionManager t = new TransitionManager(new Transitionable() {
            public void setSelection(int index) {
                me.curImg = (index + 1) % me.imgs.length;
            }
        
            public int getSelection() {
                return me.curImg;
            }
        
            public Control getControl(int index) {
                return cnvs;
            }
        
            public Composite getComposite() {
                return sShell;
            }
        
            public double getDirection(int toIndex, int fromIndex) {
                if(Math.random() > 0.5)
                    return Math.random() > 0.5 ? Transition.DIR_RIGHT : Transition.DIR_LEFT;
                else
                    return Math.random() > 0.5 ? Transition.DIR_DOWN : Transition.DIR_UP;
            }
        
            public void addSelectionListener(SelectionListener listener) {
                btn.addSelectionListener(listener);
            }
        });
        
        //Slide Transition
        t.setTransition(new SlideTransition(t));
        
        //TODO Uncomment this to see Cubic Rotation Transition
        //t.setTransition(new CubicRotationTransition(t));
        
        //TODO Uncomment this line to see Fade Transition
        //t.setTransition(new FadeTransition(t));
        
        t.addTransitionListener(new TransitionListener() {
            public void transitionFinished(TransitionManager transition) {
                System.out.println("End Of Transition! current item: " 
                        + transition.getTransitionable().getSelection());
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
