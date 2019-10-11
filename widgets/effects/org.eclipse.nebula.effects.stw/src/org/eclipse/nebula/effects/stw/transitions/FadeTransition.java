/*******************************************************************************
 * Copyright (c) 2010 Ahmed Mahran and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0 
 *
 * Contributors:
 *     Ahmed Mahran - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.effects.stw.transitions;

import org.eclipse.nebula.effects.stw.Transition;
import org.eclipse.nebula.effects.stw.TransitionManager;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;

/**
 * Applies a fade effect. The <i>from</i> control fades out and the the <i>to</i>
 * control fades in smoothly.
 * 
 * @author Ahmed Mahran (ahmahran@gmail.com)
 */
public class FadeTransition extends Transition {

    private int _aw = 255, _halfAW = 127;//alpha width = 256
    private double _aFrom, _aTo, _alphaFrom, _alphaTo
            , _alphaFrom0, _alphaTo0, _vFrom0, _vTo0;
    private boolean _flag1, _flag2;
    private long _t1, _TFrom, _TTo, _halfTFrom, _halfTTo
        , _fadeOutStartT, _fadeOutMidT, _fadeOutStopT
        , _fadeInStartT, _fadeInMidT, _fadeInStopT;
    private double _fadeOutStart = 0, _fadeOutStop = 100
            , _fadeInStart = 0, _fadeInStop = 100;
    
    /**
     * This constructor creates a FadeTransition with number of frames per second of {@link Transition#DEFAULT_FPS}
     * and total transition time of {@link Transition#DEFAULT_T} milliseconds. It is similar to 
     * new FadeTransition(transitionManager, {@link Transition#DEFAULT_FPS}, {@link Transition#DEFAULT_T})
     * 
     * @param transitionManager the transition manager to be used to manage transitions
     */
    public FadeTransition(TransitionManager transitionManager) {
        this(transitionManager, DEFAULT_FPS, DEFAULT_T);
    }
    
    /**
     * This constructor creates a FadeTransition with <i>fps</i> number of frames per
     * second and <i>T</i> total transition time in milliseconds.
     * 
     * @param transitionManager the transition manager to be used to manage transitions 
     * @param fps number of frames per second
     * @param T the total time the transition effect will take in milliseconds
     */
    public FadeTransition(TransitionManager transitionManager, long fps, long T) {
        super(transitionManager, fps, T);
    }
    
    @Override
    protected void initTransition(Image from, Image to, GC gc, double direction) {
        
        _alphaFrom  = _aw;
        _alphaTo    = 0;
        
        _fadeOutStartT  = (long) ((_fadeOutStart    * _T) / 100.0);
        _fadeOutStopT   = (long) ((_fadeOutStop     * _T) / 100.0);
        _fadeOutMidT    = (long) ((_fadeOutStartT + _fadeOutStopT) / 2.0);
        _fadeInStartT   = (long) ((_fadeInStart     * _T) / 100.0);
        _fadeInStopT    = (long) ((_fadeInStop      * _T) / 100.0);
        _fadeInMidT     = (long) ((_fadeInStartT + _fadeInStopT) / 2.0);
        
        _TFrom  = _fadeOutStopT - _fadeOutStartT;
        _TTo    = _fadeInStopT  - _fadeInStartT;
        
        _halfTFrom  = (long) (_TFrom    / 2.0);
        _halfTTo    = (long) (_TTo      / 2.0);
        
        _aFrom  = (_aw + 1) / (double)(_halfTFrom   * _halfTFrom);
        _aTo    = (_aw + 1) / (double)(_halfTTo     * _halfTTo);
        
        _flag1 = false;
        _flag2 = false;
        
    }

    @Override
    protected void stepTransition(long t, Image from, Image to, GC gc,
            double direction) {
        
        if( t >= 0 && t < _fadeOutStartT ) {
            
            gc.drawImage(from, 0, 0);
            
        } else if( t >= _fadeOutStartT && t < _fadeOutMidT ) {
            
            gc.setAlpha((int) _alphaFrom);
            gc.drawImage(from, 0, 0);
            
            _t1 = t - _fadeOutStartT;
            _alphaFrom = _aw - Math.min(0.5 * _aFrom * _t1 * _t1, _halfAW);
            
        } else if( t >= _fadeOutMidT && t <= _fadeOutStopT ) {
            
            gc.setAlpha((int) _alphaFrom);
            gc.drawImage(from, 0, 0);
            
            if(!_flag1) {
                
                _alphaFrom0 = _aw - _alphaFrom;
                _vFrom0 = _aFrom * (t - _fadeOutStartT);
                _aFrom *= -1.0;
                _flag1 = true;
                
            }
            
            _t1 = t - _fadeOutMidT;
            _alphaFrom = _aw - Math.min(_alphaFrom0 + _vFrom0 * _t1 + 0.5 * _aFrom * _t1 * _t1, _aw);
            
        }
        
        /////////////////////////////////////////////////////////////////////
        
        if( t >= _fadeInStartT && t < _fadeInMidT ) {
            
            gc.setAlpha((int) _alphaTo);
            gc.drawImage(to, 0, 0);
            
            _t1 = t - _fadeInStartT;
            _alphaTo = Math.min(0.5 * _aTo * _t1 * _t1, _halfAW);
            
        } else if( t >= _fadeInMidT && t <= _fadeInStopT) {
            
            gc.setAlpha((int) _alphaTo);
            gc.drawImage(to, 0, 0);
            
            if(!_flag2) {
                
                _alphaTo0 = _alphaTo;
                _vTo0 = _aTo * (t - _fadeInStartT);
                _aTo *= -1.0;
                _flag2 = true;
                
            }
            
            _t1 = t - _fadeInMidT;
            _alphaTo = Math.min(_alphaTo0 + _vTo0 * _t1 + 0.5 * _aTo * _t1 * _t1, _aw);
            
        } else if( t > _fadeInStopT) {
            
            gc.drawImage(to, 0, 0);
            
        }
        
    }

    @Override
    protected void endTransition(Image from, Image to, GC gc, double direction) {
    }
    
    /**
     * Specifies when the fade-out effect is started as a percentage
     * of the transition total time. For example, 0% means that the 
     * fade-out effect will start at the beginning of the transition.
     * <br/>
     * <i>percentage</i> should have a value in the interval from
     * 0 to 100 inclusive otherwise the passed value would be ignored
     * leaving the previous value unchanged.
     * 
     * @param percentage is a percentage of the transition total time.
     */
    public void setFadeOutStart(double percentage) {
        if( percentage >= 0 && percentage <= 100 )
            _fadeOutStart = percentage;
    }
    
    /**
     * Specifies when the fade-out effect is ended as a percentage
     * of the transition total time. For example, 50% means that the 
     * fade-out effect will stop at the mid of the transition.
     * <br/>
     * <i>percentage</i> should have a value in the interval from
     * 0 to 100 inclusive otherwise the passed value would be ignored
     * leaving the previous value unchanged.
     * 
     * @param percentage is a percentage of the transition total time.
     */
    public void setFadeOutStop(double percentage) {
        if( percentage >= 0 && percentage <= 100 )
            _fadeOutStop = percentage;
    }
    
    /**
     * Specifies when the fade-in effect is started as a percentage
     * of the transition total time. For example, 50% means that the 
     * fade-in effect will start at the mid of the transition.
     * <br/>
     * <i>percentage</i> should have a value in the interval from
     * 0 to 100 inclusive otherwise the passed value would be ignored
     * leaving the previous value unchanged.
     * 
     * @param percentage is a percentage of the transition total time.
     */
    public void setFadeInStart(double percentage) {
        if( percentage >= 0 && percentage <= 100 )
            _fadeInStart = percentage;
    }
    
    /**
     * Specifies when the fade-in effect is ended as a percentage
     * of the transition total time. For example, 100% means that the 
     * fade-in effect will stop at the end of the transition.
     * <br/>
     * <i>percentage</i> should have a value in the interval from
     * 0 to 100 inclusive otherwise the passed value would be ignored
     * leaving the previous value unchanged.
     * 
     * @param percentage is a percentage of the transition total time.
     */
    public void setFadeInStop(double percentage) {
        if( percentage >= 0 && percentage <= 100 )
            _fadeInStop = percentage;
    }

}
