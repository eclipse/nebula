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

package org.eclipse.nebula.effects.stw.transitions;

import org.eclipse.nebula.effects.stw.Transition;
import org.eclipse.nebula.effects.stw.TransitionManager;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

/**
 * A cubic rotation effect. Showing two sides of a cube, the cube rotates from one
 * side to the other side.
 * 
 * @author Ahmed Mahran (ahmahran@gmail.com)
 */
public class CubicRotationTransition extends Transition {

    private int _w, _halfW, _h, _halfH;
    private double _a1, _a2, _x, _y, _x0, _y0, _v0;
    private boolean _flag1;
    private ImageData _fromData;
    private long _halfT, _t1, _tSqrd;
    private double _dy1, _dx1, _dx2, _dy2
                , _x1, _y1, _x2, _y2
                , _destHeight, _destWidth
                , _destHeight0, _destWidth0
                , _destHeightV0, _destWidthV0
                , _ratio1, _ratio2
                , _remainedSize;
    
    private double _quality = 100;//%
    
    /**
     * This constructor creates a CubicRotationTransition with number of frames per second of {@link Transition#DEFAULT_FPS}
     * and total transition time of {@link Transition#DEFAULT_T} milliseconds. It is similar to 
     * new CubicRotationTransition(transitionManager, {@link Transition#DEFAULT_FPS}, {@link Transition#DEFAULT_T})
     * 
     * @param transitionManager the transition manager to be used to manage transitions
     */
    public CubicRotationTransition(TransitionManager transitionManager) {
        this(transitionManager, DEFAULT_FPS, DEFAULT_T);
    }
    
    /**
     * This constructor creates a CubicRotationTransition with number of frames per second of <code>fps</code>
     * and total transition time of <code>T</code> milliseconds.
     * 
     * @param transitionManager the transition manager to be used to manage transitions 
     * @param fps number of frames per second
     * @param T the total time the transition effect will take in milliseconds
     */
    public CubicRotationTransition(TransitionManager transitionManager, long fps, long T) {
        super(transitionManager, fps, T);
    }

    @Override
    protected void initTransition(Image from, Image to, GC gc, double direction) {

        _halfT = (long) (_T / 2.0);
        _fromData = from.getImageData();
        _w = _fromData.width;
        _h = _fromData.height;
        _halfW = (int) (_w / 2.0);
        _halfH = (int) (_h / 2.0);
        
        switch((int)direction) {
        
        case (int)DIR_RIGHT:
            _a1 = _w / (double)(_halfT * _halfT);
            _a2 = _h / (double)(_halfT * _halfT);
            _x = 0;
            _destHeight = 0;
            _dx1 = _dx2 = _w - _quality * (_w - 1) / 100.0;
            _remainedSize = _w - ((int)(_w / _dx1) * _dx1);
            break;
        
        case (int)DIR_LEFT:
            _a1 = _w / (double)(_halfT * _halfT);
            _a2 = _h / (double)(_halfT * _halfT);
            _x = _w;
            _destHeight = _h;
            _dx1 = _dx2 = _w - _quality * (_w - 1) / 100.0;
            _remainedSize = _w - ((int)(_w / _dx1) * _dx1);
            break;
        
        case (int)DIR_UP:
            _a1 = _h / (double)(_halfT * _halfT);
            _a2 = _w / (double)(_halfT * _halfT);
            _y = _h;
            _destWidth = _w;
            _dy1 = _dy2 = _h - _quality * (_h - 1) / 100.0;
            _remainedSize = _h - ((int)(_h / _dy1) * _dy1);
            break;
            
        case (int)DIR_DOWN:
            _a1 = _h / (double)(_halfT * _halfT);
            _a2 = _w / (double)(_halfT * _halfT);
            _y = 0;
            _destWidth = 0;
            _dy1 = _dy2 = _h - _quality * (_h - 1) / 100.0;
            _remainedSize = _h - ((int)(_h / _dy1) * _dy1);
            break;
        
        }
        
        _flag1 = false;
        
    }

    @Override
    protected void stepTransition(long t, Image from, Image to, GC gc,
            double direction) {
        
        switch((int)direction) {
        
        case (int)DIR_RIGHT:
            
            _ratio1 = (_w - _x) / _w;
            _ratio2 = (_x) / _w;
            
            _dy1 = _dx1 * (_destHeight) / (2.0 * _w);
            _dy2 = _dx2 * (_h - _destHeight) / (2.0 * _w);
            _x1 = 0; _y1 = 0; _x2 = 0; _y2 = (_h - _destHeight) / 2.0;
            
            for (; _x1 < _w; _x1 += _dx1) {
                try {
                    _x2 = _x1;
                    gc.drawImage(from, (int) _x1, 0, (int) _dx1, _h,
                            (int) (_x + _x1 * _ratio1), (int) _y1
                            , (int) _dx1, (int) (_h - _y1 - _y1));
                    gc.drawImage(to, (int) _x2, 0, (int) _dx2, _h,
                            (int) (_x2 * _ratio2), (int) _y2
                            , (int) _dx2, (int) (_h - _y2 - _y2));
                    _y1 += _dy1;
                    _y2 -= _dy2;
                } catch (Exception e) {
                    gc.drawImage(from, (int) _x1, 0, (int) _remainedSize, _h,
                            (int) (_x + _x1 * _ratio1), (int) _y1
                            , (int) _remainedSize, (int) (_h - _y1 - _y1));
                    gc.drawImage(to, (int) _x2, 0, (int) _remainedSize, _h,
                            (int) (_x2 * _ratio2), (int) _y2
                            , (int) _remainedSize, (int) (_h - _y2 - _y2));
                }
            }
            
            if( t <= _halfT ) {
                
                _tSqrd = t * t;
                _x = Math.min(0.5 * _a1 * _tSqrd, _halfW);
                _destHeight = Math.min(0.5 * _a2 * _tSqrd, _halfH);
                
            } else {
                
                if(!_flag1) {
                    
                    _x0 = _x; _destHeight0 = _destHeight;
                    _v0 = _a1 * t; _destHeightV0 = _a2 * t;
                    _a1 *= -1.0; _a2 *= -1.0;
                    _flag1 = true;
                    
                }
                
                _t1 = t - _halfT;
                _tSqrd = _t1 * _t1;
                _x = Math.min(_x0 + _v0 * _t1 + 0.5 * _a1 * _tSqrd, _w);
                _destHeight = Math.min(_destHeight0 + _destHeightV0 * _t1 + 0.5 * _a2 * _tSqrd, _h);
                
            }
            break;
            
        case (int)DIR_LEFT:
            
            _ratio1 = (_x) / _w;
            _ratio2 = (_w - _x) / _w;
            
            _dy1 = _dx1 * (_h - _destHeight) / (2.0 * _w);
            _dy2 = _dx2 * (_destHeight) / (2.0 * _w);
            _x1 = 0; _y1 = (_h - _destHeight) / 2.0; _x2 = 0; _y2 = 0;
            
            for (; _x1 < _w; _x1 += _dx1) {
                try {
                    gc.drawImage(from, (int) _x1, 0, (int) _dx1, _h,
                            (int) (_x1 * _ratio1), (int) _y1
                            , (int) _dx1, (int) (_h - _y1 - _y1));
                    _y1 -= _dy1;
                } catch (Exception e) {
                    gc.drawImage(from, (int) _x1, 0, (int) _remainedSize, _h,
                            (int) (_x1 * _ratio1), (int) _y1
                            , (int) _remainedSize, (int) (_h - _y1 - _y1));
                }
            }
            for (; _x2 < _w; _x2 += _dx2) {
                try {
                    gc.drawImage(to, (int) _x2, 0, (int) _dx2, _h,
                            (int) (_x + _x2 * _ratio2), (int) _y2
                            , (int) _dx2, (int) (_h - _y2 - _y2));
                    _y2 += _dy2;
                } catch (Exception e) {
                    gc.drawImage(to, (int) _x2, 0, (int) _remainedSize, _h,
                            (int) (_x + _x2 * _ratio2), (int) _y2
                            , (int) _remainedSize, (int) (_h - _y2 - _y2));
                }
            }
        
            if( t <= _halfT ) {
                
                _tSqrd = t * t;
                _x = _w - Math.min(0.5 * _a1 * _tSqrd, _halfW);
                _destHeight = _h - Math.min(0.5 * _a2 * _tSqrd, _halfH);
                
            } else {
                
                if(!_flag1) {
                    
                    _x0 = _w - _x; _destHeight0 = _h - _destHeight;
                    _v0 = _a1 * t; _destHeightV0 = _a2 * t;
                    _a1 *= -1.0; _a2 *= -1.0;
                    _flag1 = true;
                    
                }
                
                _t1 = t - _halfT;
                _tSqrd = _t1 * _t1;
                _x = _w - Math.min(_x0 + _v0 * _t1 + 0.5 * _a1 * _tSqrd, _w);
                _destHeight = _h - Math.min(_destHeight0 + _destHeightV0 * _t1 + 0.5 * _a2 * _tSqrd, _h);
                
            }
            break;
        
        case (int)DIR_UP:
            
            _ratio1 = (_y) / _h;
            _ratio2 = (_h - _y) / _h;
            
            _dx1 = _dy1 * (_w - _destWidth) / (2.0 * _h);
            _dx2 = _dy2 * (_destWidth) / (2.0 * _h);
            _y1 = 0; _x1 = (_w - _destWidth) / 2.0; _y2 = 0; _x2 = 0;
            
            for (; _y1 < _h; _y1 += _dy1) {
                try {
                    gc.drawImage(from, 0, (int) _y1, _w, (int) _dy1
                            , (int) _x1, (int) (_y1 * _ratio1) 
                            , (int) (_w - _x1 - _x1), (int) _dy1);
                    _x1 -= _dx1;
                } catch (Exception e) {
                    gc.drawImage(from, 0, (int) _y1, _w, (int) _remainedSize
                            , (int) _x1, (int) (_y1 * _ratio1) 
                            , (int) (_w - _x1 - _x1), (int) _remainedSize);
                }
            }
            for (; _y2 < _h; _y2 += _dy2) {
                try {
                    gc.drawImage(to, 0, (int) _y2, _w, (int) _dy2
                            , (int) _x2, (int) (_y + _y2 * _ratio2)
                            , (int) (_w - _x2 - _x2), (int) _dy2);
                    _x2 += _dx2;
                } catch (Exception e) {
                    gc.drawImage(to, 0, (int) _y2, _w, (int) _remainedSize
                            , (int) _x2, (int) (_y + _y2 * _ratio2)
                            , (int) (_w - _x2 - _x2), (int) _remainedSize);
                }
            }
        
            if( t <= _halfT ) {
                
                _tSqrd = t * t;
                _y = _h - Math.min(0.5 * _a1 * _tSqrd, _halfH);
                _destWidth = _w - Math.min(0.5 * _a2 * _tSqrd, _halfW);
                
            } else {
                
                if(!_flag1) {
                    
                    _y0 = _h - _y; _destWidth0 = _w - _destWidth;
                    _v0 = _a1 * t; _destWidthV0 = _a2 * t;
                    _a1 *= -1.0; _a2 *= -1.0;
                    _flag1 = true;
                    
                }
                
                _t1 = t - _halfT;
                _tSqrd = _t1 * _t1;
                _y = _h - Math.min(_y0 + _v0 * _t1 + 0.5 * _a1 * _tSqrd, _h);
                _destWidth = _w - Math.min(_destWidth0 + _destWidthV0 * _t1 + 0.5 * _a2 * _tSqrd, _w);
                
            }
            break;
        
        case (int)DIR_DOWN:
            
            _ratio1 = (_h - _y) / _h;
            _ratio2 = (_y) / _h;
            
            _dx1 = _dy1 * (_destWidth) / (2.0 * _h);
            _dx2 = _dy2 * (_w - _destWidth) / (2.0 * _h);
            _y1 = 0; _x1 = 0; _y2 = 0; _x2 = (_w - _destWidth) / 2.0;
            
            for (; _y1 < _h; _y1 += _dy1) {
                try {
                    _y2 = _y1;
                    gc.drawImage(from, 0, (int) _y1, _w, (int) _dy1
                            , (int) _x1, (int) (_y + _y1 * _ratio1)
                            , (int) (_w - _x1 - _x1), (int) _dy1);
                    gc.drawImage(to, 0, (int) _y2, _w, (int) _dy2
                            , (int) _x2, (int) (_y2 * _ratio2)
                            , (int) (_w - _x2 - _x2), (int) _dy2);
                    _x1 += _dx1;
                    _x2 -= _dx2;
                } catch (Exception e) {
                    gc.drawImage(from, 0, (int) _y1, _w, (int) _remainedSize
                            , (int) _x1, (int) (_y + _y1 * _ratio1)
                            , (int) (_w - _x1 - _x1), (int) _remainedSize);
                    gc.drawImage(to, 0, (int) _y2, _w, (int) _remainedSize
                            , (int) _x2, (int) (_y2 * _ratio2)
                            , (int) (_w - _x2 - _x2), (int) _remainedSize);
                }
            }
            
            if( t <= _halfT ) {
                
                _tSqrd = t * t;
                _y = Math.min(0.5 * _a1 * _tSqrd, _halfH);
                _destWidth = Math.min(0.5 * _a2 * _tSqrd, _halfW);
                
            } else {
                
                if(!_flag1) {
                    
                    _y0 = _y; _destWidth0 = _destWidth;
                    _v0 = _a1 * t; _destWidthV0 = _a2 * t;
                    _a1 *= -1.0; _a2 *= -1.0;
                    _flag1 = true;
                    
                }
                
                _t1 = t - _halfT;
                _tSqrd = _t1 * _t1;
                _y = Math.min(_y0 + _v0 * _t1 + 0.5 * _a1 * _tSqrd, _h);
                _destWidth = Math.min(_destWidth0 + _destWidthV0 * _t1 + 0.5 * _a2 * _tSqrd, _w);
                
            }
            break;
        
        }
        
    }
    
    @Override
    protected void endTransition(Image from, Image to, GC gc, double direction) {

    }
    
    /**
     * Sets the quality of image slicing as a percentage in
     * the interval from 0 to 100 inclusive
     * 
     * @param quality is a percentage from 0 to 100 inclusive
     */
    public void setQuality(double quality) {
        if(quality >= 0.0 && quality <= 100.0)
            _quality = quality;
    }
    
    
    /**
     * Returns a percentage representing the quality of image slicing
     * @return a percentage representing the quality of image slicing
     */
    public double getQuality() {
        return _quality;
    }

}
