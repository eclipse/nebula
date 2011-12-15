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
import java.util.concurrent.atomic.AtomicBoolean;

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
 * Transition manager applies the required transition on a {@link Transitionable} object. 
 * 
 * @author Ahmed Mahran (ahmahran@gmail.com)
 */
public class TransitionManager {
    
    //private int _finalFrom  = -1;
    //private int _finalTo  = -1;
    //private double _finalDirection = 0;
    //private boolean _isProcessFinalDest = false;
    
	private int _lastItem   = -1;
    Color backgroundColor;
    Image backgroundImage;
    
    //AtomicBoolean isAnyTransitionInProgress   = new AtomicBoolean(false);
    //AtomicBoolean isCurrentTransitionCanceled = new AtomicBoolean(false);
    
    private Transitionable  _transitionable;
    private Transition      _transition;
    
    private List<TransitionListener> _listeners;
    
    /**
     * Constructs a transition manager to handle transitions on the provided
     * transitionable object.
     * @param transitionable the transitionable object to perform transitions on
     */
    public TransitionManager(final Transitionable transitionable) {
        
        _transitionable = transitionable;
        _listeners      = new ArrayList<TransitionListener>();
        backgroundColor = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
        
        //the selected item before the one to be transitioned to
        _lastItem       = transitionable.getSelection();
        
        transitionable.addSelectionListener(new SelectionAdapter() {
        public void widgetSelected(SelectionEvent event) {
        try {
            //the item to be transitioned to
            int currentItem = _transitionable.getSelection();
            
            startTransition(_lastItem, currentItem, transitionable.getDirection(currentItem, _lastItem));
            
            //now the item transition ends on will be used
            //to start transition from next time
            _lastItem = currentItem;
        } catch(Exception e) { e.printStackTrace(); }
        }});
    }
    
    /**
     * Constructs a transition manager to handle transitions on the provided
     * {@link CTabFolder} as the transitionable object.
     * @param tabFolder the {@link CTabFolder} as the transitionable object to perform transitions on
     */
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

    /**
     * Constructs a transition manager to handle transitions on the provided
     * {@link TabFolder} as the transitionable object.
     * @param tabFolder the {@link TabFolder} as the transitionable object to perform transitions on
     */
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
    
    /**
     * Carries out the transition effect on the transitionable object by transitioning from
     *  <code>fromIndex</code> to <code>toIndex</code> in the direction <code>direction</code>
     * @param fromIndex the index of the {@link Control} to start transition from
     * @param toIndex the index of the {@link Control} to make transition to
     * @param direction the direction of the transition
     */
    public void startTransition(int fromIndex, int toIndex, double direction) {
        try {
            
            ////if any transition is in progress, cancel it and
            ////start the most recent one to catch up with the
            ////user's selections
            //if(isAnyTransitionInProgress.get()) {
            //    isCurrentTransitionCanceled.set(true);
            //}
            //
            //isAnyTransitionInProgress.set(true);
            
            //when this event is fired, the current selected item
            //is the item to be transitioned to and the previously
            //selected one is the item to start the transition from
            
        
            //reselect the older item to start transition from
            _transitionable.setSelection(fromIndex);
            
            //capture an image of the "from" view
            Control from    = _transitionable.getControl(fromIndex);
            Rectangle size  = from.getBounds();
            Image imgFrom   = new Image(from.getDisplay(), size.width, size.height);
            GC gcfrom       = new GC(from);
            from.update();
            gcfrom.copyArea(imgFrom, 0, 0);
            gcfrom.dispose();
            
            //capture an image of the "to" view
            Control to  = _transitionable.getControl(toIndex);
            _transitionable.setSelection(toIndex);
            Image imgTo = ImageCapture.getImage(to, size.width, size.height, true);
            _transitionable.setSelection(fromIndex);
            
            
            //create and show the canvas that the transition will be showed on
            Canvas canvas = new Canvas(_transitionable.getComposite(), SWT.DOUBLE_BUFFERED);
            canvas.moveAbove(null);
            canvas.setBounds(to.getBounds());
            
            //make the transition
            GC gcOn = new GC(canvas);
            _transition.start(imgFrom, imgTo, gcOn, direction);
            _transitionable.setSelection(toIndex);
            gcOn.dispose();
            
            //dispose the transition canvas
            canvas.dispose();
            
            //if the current transition was canceled to process
            //a new recent one, show the new selection and make
            //a new transition to it
            //if(isCurrentTransitionCanceled.get()) {
            //    
            //    isCurrentTransitionCanceled.set(false);
            //    isAnyTransitionInProgress.set(false);
            //    //unlock();
            //    
            //} else {
            //    
            //    isAnyTransitionInProgress.set(false);
                for(TransitionListener tl: _listeners)
                    tl.transitionFinished(TransitionManager.this);
                
            //}
            
            
            
        } catch(Exception e) { e.printStackTrace(); }
        
        
//        try {
//            
//            //if any transition is in progress, cancel it and
//            //start the most recent one to catch up with the
//            //user's selections
//            if(isAnyTransitionInProgress.get()) {
//                
//                isCurrentTransitionCanceled.set(true);
//                _finalTo = _transitionable.getSelection();
//                _isProcessFinalDest = true;
//                return;
//                
//            }
//            
//            isCurrentTransitionCanceled.set(false);
//            
//            //when this event is fired, the current selected item
//            //is the item to be transitioned to and the previously
//            //selected one is the item to start the transition from
//            
//            //the item to be transitioned to
//            int currentItem = _transitionable.getSelection();
//        
//            //reselect the older item to start transition from
//            _transitionable.setSelection(_lastItem);
//            
//            //capture an image of the "from" view
//            Control from    = _transitionable.getControl(_lastItem);
//            Rectangle size  = from.getBounds();
//            Image imgFrom   = new Image(from.getDisplay(), size.width, size.height);
//            GC gcfrom       = new GC(from);
//            from.update();
//            gcfrom.copyArea(imgFrom, 0, 0);
//            gcfrom.dispose();
//            
//            //capture an image of the "to" view
//            Control to  = _transitionable.getControl(currentItem);
//            _transitionable.setSelection(currentItem);
//            Image imgTo = ImageCapture.getImage(to, size.width, size.height, true);
//            _transitionable.setSelection(_lastItem);
//            
//            
//            //create and show the canvas that the transition will be showed on
//            Canvas canvas = new Canvas(_transitionable.getComposite(), SWT.DOUBLE_BUFFERED);
//            canvas.moveAbove(null);
//            canvas.setBounds(to.getBounds());
//            
//            //make the transition
//            GC gcOn = new GC(canvas);
//            _transition.start(imgFrom, imgTo, gcOn, direction);
//            _transitionable.setSelection(currentItem);
//            gcOn.dispose();
//            
//            //dispose the transition canvas
//            canvas.dispose();
//            
//            //now the item transition ends on will be used
//            //to start transition from next time
//            _lastItem = currentItem;
//            
//            //if the current transition was canceled to process
//            //a new recent one, show the new selection and make
//            //a new transition to it
//            if(_isProcessFinalDest) {
//                
//                _isProcessFinalDest = false;
//                _transitionable.setSelection(_finalTo);
//                startTransition(direction);
//                
//            } else {
//                
//                for(TransitionListener tl: _listeners)
//                    tl.transitionFinished(TransitionManager.this);
//                
//            }
//            
//        } catch(Exception e) { e.printStackTrace(); }
        
        
    }
    
    /**
     * Sets and changes the transition effect
     * @param transition the transition effect to be applied on the transitionable object
     */
    public void setTransition(Transition transition) {
        _transition = transition;
    }
    
    /**
     * Returns the current transition effect  
     * @return the current transition effect
     */
    public Transition getTransition() {
        return _transition;
    }
    
    /**
     * Sets the background color of the transition frame
     * @param color the background color of the transition frame
     */
    public void setBackground(Color color) {
        backgroundColor = color;
    }
    
    /**
     * Returns the background color of the transition frame
     * @return the background color of the transition frame
     */
    public Color getBackground() {
        return backgroundColor;
    }
    
    /**
     * Sets the background image of the transition frame
     * @param image the background image of the transition frame
     */
    public void setBackgroundImage(Image image) {
        backgroundImage = image;
    }
    
    /**
     * Returns the background image of the transition frame
     * @return the background image of the transition frame
     */
    public Image getBackgroundImage() {
        return backgroundImage;
    }
    
    /**
     * Returns the transitionable object
     * @return the transitionable object
     */
    public Transitionable getTransitionable() {
        return _transitionable;
    }
    
    /**
     * Adds a new transition listener to be invoked at the end of each transition
     * @param transitionListener the new transition listener to be invoked at the end of each transition
     */
    public void addTransitionListener(TransitionListener transitionListener) {
        _listeners.add(transitionListener);
    }
    
    /**
     * Removes a transition listener from the list of transition listeners
     * @param transitionListener the transition listener to be removed
     */
    public void removeTransitionListener(TransitionListener transitionListener) {
        _listeners.remove(transitionListener);
    }

}
