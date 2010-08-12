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

package org.eclipse.nebula.effects.stw;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabFolder;

/**
 * @author Ahmed Mahran (ahmahran@gmail.com)
 */
public class TransitionManager {
    
    private int _finalDest  = -1;
    private boolean _isProcessFinalDest = false;
    
	private int _lastItem   = -1;
    Color backgroundColor;
    Image backgroundImage;
    
    boolean isAnyTransitionInProgress   = false;
    boolean isCurrentTransitionCanceled = false;
    
    private Transitionable  _transitionable;
    private Transition      _transition;
    
    private List<TransitionListener> _listeners;
    
    public TransitionManager(final Transitionable transitionable) {
        
        _transitionable = transitionable;
        _listeners      = new ArrayList<TransitionListener>();
        backgroundColor = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
        
        //the selected item before the one to be transitioned to
        _lastItem       = transitionable.getSelection();
        
        transitionable.addSelectionListener(new SelectionAdapter() {
        public void widgetSelected(SelectionEvent event) {
        try {
            
            //if any transition is in progress, cancel it and
            //start the most recent one to catch up with the
            //user's selections
            if(isAnyTransitionInProgress) {
                
                isCurrentTransitionCanceled = true;
                _finalDest = transitionable.getSelection();
                _isProcessFinalDest = true;
                return;
                
            }
            
            isCurrentTransitionCanceled = false;
            
            //when this event is fired, the current selected item
            //is the item to be transitioned to and the previously
            //selected one is the item to start the transition from
            
            //the item to be transitioned to
            int currentItem = transitionable.getSelection();
        
            //reselect the older item to start transition from
            transitionable.setSelection(_lastItem);
            
            //capture the an image of the "from" view
            Control from    = transitionable.getControl(_lastItem);
            Rectangle size  = from.getBounds();
            Image imgFrom   = new Image(from.getDisplay(), size.width, size.height);
            GC gcfrom       = new GC(from);
            from.update();
            gcfrom.copyArea(imgFrom, 0, 0);
            gcfrom.dispose();
            
            //capture an image of the "to" view
            Control to  = transitionable.getControl(currentItem);
            transitionable.setSelection(currentItem);
            Image imgTo = ImageCapture.getImage(to, size.width, size.height, true);
            transitionable.setSelection(_lastItem);
            
            
            //create and show the canvas that the transition will be showed on
            Canvas canvas = new Canvas(transitionable.getComposite(), SWT.DOUBLE_BUFFERED);
            canvas.moveAbove(null);
            canvas.setBounds(to.getBounds());
            
            //make the transition
            GC gcOn = new GC(canvas);
            _transition.start(imgFrom, imgTo, gcOn
                        , transitionable.getDirection(currentItem, _lastItem));
            transitionable.setSelection(currentItem);
            gcOn.dispose();
            
            //dispose the transition canvas
            canvas.dispose();
            
            //now the item transition ends on will be used
            //to start transition from next time
            _lastItem = currentItem;
            
            //if the current transition was canceled to process
            //a new recent one, show the new selection and make
            //a new transition to it
            if(_isProcessFinalDest) {
                
                _isProcessFinalDest = false;
                transitionable.setSelection(_finalDest);
                widgetSelected(event);
                
            } else {
                
                for(TransitionListener tl: _listeners)
                    tl.transitionFinished(TransitionManager.this);
                
            }
            
        } catch(Exception e) { e.printStackTrace(); }
        }});
    }
    
    public TransitionManager(final CTabFolder tabFolder) {
        this(new Transitionable(){
            public void addSelectionListener(SelectionListener listener) {
                tabFolder.addSelectionListener(listener);
            }
            public Control getControl(int index) {
                return tabFolder.getItem(index).getControl();
            }
            public Composite getComposite() {
                return tabFolder;
            }
            public int getSelection() {
                return tabFolder.getSelectionIndex();
            }
            public void setSelection(int index) {
                tabFolder.setSelection(index);
            }
            public double getDirection(int toIndex, int fromIndex) {
                return toIndex > fromIndex ? Transition.DIR_RIGHT : Transition.DIR_LEFT;
            }
        });
    }

    public TransitionManager(final TabFolder tabFolder) {
        this(new Transitionable(){
            public void addSelectionListener(SelectionListener listener) {
                tabFolder.addSelectionListener(listener);
            }
            public Control getControl(int index) {
                return tabFolder.getItem(index).getControl();
            }
            public Composite getComposite() {
                return tabFolder;
            }

            public int getSelection() {
                return tabFolder.getSelectionIndex();
            }
            public void setSelection(int index) {
                tabFolder.setSelection(index);
            }
            public double getDirection(int toIndex, int fromIndex) {
                return toIndex > fromIndex ? Transition.DIR_RIGHT : Transition.DIR_LEFT;
            }
        });
    }
    
    public void setTransition(Transition transition) {
        _transition = transition;
    }
    
    public Transition getTransition() {
        return _transition;
    }
    
    public void setBackground(Color color) {
        backgroundColor = color;
    }
    
    public Color getBackground() {
        return backgroundColor;
    }
    
    public void setBackgroundImage(Image image) {
        backgroundImage = image;
    }
    
    public Image getBackgroundImage() {
        return backgroundImage;
    }
    
    public Transitionable getTransitionable() {
        return _transitionable;
    }
    
    public void addTransitionListener(TransitionListener transitionListener) {
        _listeners.add(transitionListener);
    }
    
    public void removeTransitionListener(TransitionListener transitionListener) {
        _listeners.remove(transitionListener);
    }

}
