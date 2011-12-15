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

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

/**
 * An abstract class handling the basic actions required for whatever transition effect.
 * These actions are like the transition loop.<br/><br/>
 * 
 * To implement a new transition effect, this class should be extended by the new transition
 * class and only the three methods {@link Transition#initTransition(Image, Image, GC, double)}
 * , {@link Transition#stepTransition(long, Image, Image, GC, double)} and 
 * {@link Transition#endTransition(Image, Image, GC, double)} must be implemented.<br/><br/>
 * 
 * The transition loop:
 * <code><pre>
 * xitionImgGC.drawImage(from, 0, 0);
 * initTransition(from, to, xitionImgGC, direction);
 * render(xitionImgGC);
 * while(t <= T) {
 *   if(t <= T) {
 *     stepTransition(t, from, to, xitionImgGC, direction);
 *   } else {
 *     xitionImgGC.drawImage(to, 0, 0);
 *     endTransition(from, to, xitionImgGC, direction);
 *   }
 *   render(xitionImgGC);
 *   t += dt;
 * }
 * </code></pre>
 * 
 * The <code>initTransition</code> method initializes the transition variables and draws the initial/first 
 * frame of the transition effect at time 0. The <code>stepTransition</code>
 *  method calculates the new transition variables values based on the time parameter <code>t</code>
 *  and draws the transition effect at time instance t. Finally, the <code>endTransition</code> method
 *  finalizes the transition and draws the last frame at instance T. 
 * 
 * @author Ahmed Mahran (ahmahran@gmail.com)
 */
public abstract class Transition {

    /**
     * The default fps (frames per second) is 60
     */
    public static final long DEFAULT_FPS    = 60;
    /**
     * The default transition time is 1000 ms
     */
    public static final long DEFAULT_T      = 1000;
    
    /**
     * The Right direction, 0 degrees
     */
    public static final double DIR_RIGHT    = 0;
    /**
     * The Up direction, 90 degrees
     */
    public static final double DIR_UP       = 90;
    /**
     * The Left direction, 180 degrees
     */
    public static final double DIR_LEFT     = 180;
    /**
     * The Down direction, 270 degrees
     */
    public static final double DIR_DOWN     = 270;
    
    protected TransitionManager _transitionManager;
    
    protected long      _fps;   //frames per second
    protected long      _T;     //total transition time in milliseconds
    
    private long        _dt;    //time step
    private long        _t;     //time counter
    
    /**
     * Constructs a new transition object
     * @param transitionManager the transition manager to be used to manage transitions 
     * @param fps number of frames per second
     * @param T the total time the transition effect will take
     */
    public Transition(TransitionManager transitionManager, long fps, long T) {
        _transitionManager = transitionManager;
        _fps    = fps;
        _T      = T;
        _t      = 0;
        _dt     = (long) (1000.0 / _fps);
    }
    
    /**
     * This constructor is similar to new Transition(transitionManager, {@link Transition#DEFAULT_FPS}, {@link Transition#DEFAULT_T})
     * @param transitionManager the transition manager to be used to manage transitions
     */
    public Transition(TransitionManager transitionManager) {
        this(transitionManager, DEFAULT_FPS, DEFAULT_T);
    }
    
    /**
     * Sets the maximum fps (number of frames per second) for the transition.
     * The actual number of frames displayed will vary depending on the current
     * workload on the machine. 
     * 
     * @param fps maximum number of frames per second
     */
    public final void setFPS(long fps) {
        _fps    = fps;
        _dt     = (long) (1000.0 / fps);
    }
    
    /**
     * Returns the maximum number of frames per second
     * @return the maximum number of frames per second
     */
    public final long getFPS() {
        return _fps;
    }
    
    /**
     * Sets the total time of the transition effect in milliseconds.
     * 
     * @param T total time of the transition effect in milliseconds
     */
    public final void setTotalTransitionTime(long T) {
        _T = T;
    }
    
    
    /**
     * Returns the total time of the transition effect in millisecond
     * @return the total time of the transition effect in millisecond
     */
    public final double getTotalTransitionTime() {
        return _T;
    }
    
    /**
     * Starts the transition from the <i>from</i> image to the <i>to</i> image
     * drawing the effect on the graphics context object <i>gc</i>. The <i>direction</i>
     * parameter determines the direction of the transition in degrees starting from 0
     * as the right direction and increasing in counter clock wise direction.
     *  
     * @param from is the image to start the transition from
     * @param to is the image to end the transition to
     * @param gc is the GC object to draw the transition on
     * @param direction determines the direction of the transition in degrees
     */
    public final void start(final Image from, final Image to, final GC gc, final double direction) {
        
        //_transitionManager.isAnyTransitionInProgress.setValue(true);
        
        boolean flag = true;
        long t0 = System.currentTimeMillis();
        long dt = 0;
        long ttemp = 0;
        _t = 0;
        
        //prepare transition background
        ImageData   fromData    = from.getImageData();
        Image       xitionBg    = new Image(Display.getCurrent(), fromData.width, fromData.height);
        GC          xitionBgGC  = new GC(xitionBg);
        
        xitionBgGC.setBackground(_transitionManager.backgroundColor);
        xitionBgGC.fillRectangle(0, 0, fromData.width, fromData.height);
        
        if( null != _transitionManager.backgroundImage ) {
            
            ImageData imgData = _transitionManager.backgroundImage.getImageData();
            xitionBgGC.drawImage(_transitionManager.backgroundImage
                    , 0, 0, imgData.width, imgData.height
                    , 0, 0, fromData.width, fromData.height);
            
        }
        
        xitionBgGC.dispose();
        
        Image xitionImg     = new Image(Display.getCurrent(), fromData.width, fromData.height);
        GC    xitionImgGC   = new GC(xitionImg);
        
        
        xitionImgGC.drawImage(xitionBg, 0, 0);
        xitionImgGC.drawImage(from, 0, 0);
        initTransition(from, to, xitionImgGC, direction);
        gc.drawImage(xitionImg, 0, 0);
        
        //while(!_transitionManager.isCurrentTransitionCanceled.get()
        //        && _t <= _T) {
        while(_t <= _T) {
            
            ttemp = System.currentTimeMillis() - t0;
            dt = ttemp - _t;
            if(flag) _t = ttemp;
            
            //this condition is to make sure that the
            //required fps (or less) is satisfied and
            //not more
            if(dt >= _dt) {
                
                if(_t <= _T) {
                    
                    xitionImgGC.drawImage(xitionBg, 0, 0);
                    stepTransition(_t, from, to, xitionImgGC, direction);
                    gc.drawImage(xitionImg, 0, 0);
                    
                } else {
                    
                    xitionImgGC.drawImage(xitionBg, 0, 0);
                    xitionImgGC.drawImage(to, 0, 0);
                    endTransition(from, to, xitionImgGC, direction);
                    gc.drawImage(xitionImg, 0, 0);
                    
                }
                
                flag = true;
                doEvents();
                
            } else {
                try {
                    flag = false;
                    Thread.sleep(_dt - dt);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
        }
        
        xitionBg.dispose();
        xitionImg.dispose();
        xitionImgGC.dispose();
        
        //_transitionManager.isAnyTransitionInProgress.setValue(false);
        
    }
    
    protected void doEvents() {
        Display.getCurrent().readAndDispatch();
    }
    
    protected abstract void initTransition(final Image from, final Image to, final GC gc, final double direction);
    protected abstract void stepTransition(long t, final Image from, final Image to, final GC gc, final double direction);
    protected abstract void endTransition(final Image from, final Image to, final GC gc, final double direction);
    
}
