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
    private ImageData _toData;
    private ImageData _imgDataBuffer;
    private ImageData _imgDataToDraw;
    private Image _bgImage;
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
        _toData = to.getImageData();
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
        
        if (useDataBuffersToDraw()) {
            initImgDataBuffers(gc, direction);
        }
    }
    
    /**
     * Initialize the image data buffers, used to draw the transition
     * instead of drawing into the graphics context object directly.
     * @param gc Graphics context where to initialize the background image object.
     * @param direction Direction to be used as a reference to
     * initialize the image data buffers.
     */
    private void initImgDataBuffers(GC gc, double direction) {
        switch((int)direction) {
        case (int)DIR_RIGHT:
        case (int)DIR_LEFT:
            _imgDataBuffer = new ImageData((int) _dx1, _h, 
                    _fromData.depth, _fromData.palette);
            break;
        case (int)DIR_UP:
        case (int)DIR_DOWN:
            _imgDataBuffer = new ImageData(_w, (int) _dy1, 
                    _fromData.depth, _fromData.palette);
            break;
        }
        _bgImage = new Image(gc.getDevice(), _w, _h);
    }
    
    /**
     * @return true if the application will use the data buffers
     * to draw the image, false if the buffers won't be used and the
     * image will be drawn directly to the graphics context
     * object instead.
     */
    private boolean useDataBuffersToDraw() {
        return IS_MAC_OS;
    }
    
    /**
     * Draw an image to a graphics context object. The image
     * can be drawn using image data buffers before drawing to the
     * graphics context object, or drawn directly to the
     * graphics context object. 
     * @param gc Graphics context object to draw the image to.
     * @param src the source image.
     * @param srcData image data of the source image.
     * @param srcX the x coordinate in the source image to copy from
     * @param srcY the y coordinate in the source image to copy from
     * @param srcWidth the width in pixels to copy from the source
     * @param srcHeight the height in pixels to copy from the source
     * @param destX the x coordinate in the destination to copy to
     * @param destY the y coordinate in the destination to copy to
     * @param destWidth the width in pixels of the destination rectangle
     * @param destHeight the height in pixels of the destination rectangle
     */
    private void drawImage(GC gc, Image src, ImageData srcData, 
            int srcX, int srcY, int srcWidth, int srcHeight,
            int destX, int destY, int destWidth, int destHeight) {
        
        if (useDataBuffersToDraw()) {
            drawImageData(srcData, 
                    srcX, srcY, srcWidth, srcHeight,
                    destX, destY, destWidth, destHeight);
        } else {
            gc.drawImage(src, srcX, srcY, srcWidth, srcHeight,
                    destX, destY, destWidth, destHeight);
        }
    }
    
    /**
     * Draw an image to the image data buffer object that contains all
     * the image data to be drawn to the graphics context object.
     * @param srcData image data of the source image.
     * @param srcX the x coordinate in the source image to copy from
     * @param srcY the y coordinate in the source image to copy from
     * @param srcWidth the width in pixels to copy from the source
     * @param srcHeight the height in pixels to copy from the source
     * @param destX the x coordinate in the destination to copy to
     * @param destY the y coordinate in the destination to copy to
     * @param destWidth the width in pixels of the destination rectangle
     * @param destHeight the height in pixels of the destination rectangle
     */
    private void drawImageData(ImageData srcData, 
            int srcX, int srcY, int srcWidth, int srcHeight,
            int destX, int destY, int destWidth, int destHeight) {

        if (srcWidth == 0 || srcHeight == 0 || destWidth == 0 || destHeight == 0) {
            return;
        }
        if (srcX == srcData.width) {
            srcX--;
        }
        if (srcY == srcData.height) {
            srcY--;
        }
        if (destX == _imgDataToDraw.width) {
            destX--;
        }
        if (destY == _imgDataToDraw.height) {
            destY--;
        }
        if ((srcX + srcWidth) > srcData.width || (srcY + srcHeight) > srcData.height
                || (destX + destWidth) > _imgDataToDraw.width || (destY + destHeight) > _imgDataToDraw.height) {
            return;
        }
        
        for (int y = 0; y < srcHeight; y++) {
            for (int x = 0; x < srcWidth; x++) {
                _imgDataBuffer.setPixel(x, y, srcData.getPixel(srcX + x, srcY + y));
            }
        }
        
        ImageData scaledDataBuffer = _imgDataBuffer.scaledTo(destWidth, destHeight);
        
        for (int y = 0; y < destHeight; y++) {
            for (int x = 0; x < destWidth; x++) {
                _imgDataToDraw.setPixel(destX + x, destY + y, scaledDataBuffer.getPixel(x, y));
            }
        }
    }
    
    @Override
    protected void stepTransition(long t, Image from, Image to, GC gc,
            double direction) {

        if (useDataBuffersToDraw()) {
            gc.copyArea(_bgImage, 0, 0);
            _imgDataToDraw = _bgImage.getImageData();
        }
        
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
                    
                    drawImage(gc, from, _fromData, (int) _x1, 0, (int) _dx1, _h,
                            (int) (_x + _x1 * _ratio1), (int) _y1,
                            (int) _dx1, (int) (_h - _y1 - _y1));
                    drawImage(gc, to, _toData, (int) _x2, 0, (int) _dx2, _h,
                            (int) (_x2 * _ratio2), (int) _y2 ,
                            (int) _dx2, (int) (_h - _y2 - _y2));
                    
                    _y1 += _dy1;
                    _y2 -= _dy2;
                    
                } catch (Exception e) {
                    drawImage(gc, from, _fromData, (int) _x1, 0, (int) _remainedSize, _h,
                          (int) (_x + _x1 * _ratio1), (int) _y1
                          , (int) _remainedSize, (int) (_h - _y1 - _y1));
                    drawImage(gc, to, _toData, (int) _x2, 0, (int) _remainedSize, _h,
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
                    drawImage(gc, from, _fromData, (int) _x1, 0, (int) _dx1, _h,
                            (int) (_x1 * _ratio1), (int) _y1
                            , (int) _dx1, (int) (_h - _y1 - _y1));
                    _y1 -= _dy1;
                } catch (Exception e) {
                    drawImage(gc, from, _fromData, (int) _x1, 0, (int) _remainedSize, _h,
                            (int) (_x1 * _ratio1), (int) _y1
                            , (int) _remainedSize, (int) (_h - _y1 - _y1));
                }
            }
            for (; _x2 < _w; _x2 += _dx2) {
                try {
                    drawImage(gc, to, _toData, (int) _x2, 0, (int) _dx2, _h,
                            (int) (_x + _x2 * _ratio2), (int) _y2
                            , (int) _dx2, (int) (_h - _y2 - _y2));
                    _y2 += _dy2;
                } catch (Exception e) {
                    drawImage(gc, to, _toData, (int) _x2, 0, (int) _remainedSize, _h,
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
                    drawImage(gc, from, _fromData, 0, (int) _y1, _w, (int) _dy1
                            , (int) _x1, (int) (_y1 * _ratio1) 
                            , (int) (_w - _x1 - _x1), (int) _dy1);
                    _x1 -= _dx1;
                } catch (Exception e) {
                    drawImage(gc, from, _fromData, 0, (int) _y1, _w, (int) _remainedSize
                            , (int) _x1, (int) (_y1 * _ratio1) 
                            , (int) (_w - _x1 - _x1), (int) _remainedSize);
                }
            }
            for (; _y2 < _h; _y2 += _dy2) {
                try {
                    drawImage(gc, to, _toData, 0, (int) _y2, _w, (int) _dy2
                            , (int) _x2, (int) (_y + _y2 * _ratio2)
                            , (int) (_w - _x2 - _x2), (int) _dy2);
                    _x2 += _dx2;
                } catch (Exception e) {
                    drawImage(gc, to, _toData, 0, (int) _y2, _w, (int) _remainedSize
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
                    drawImage(gc, from, _fromData, 0, (int) _y1, _w, (int) _dy1
                            , (int) _x1, (int) (_y + _y1 * _ratio1)
                            , (int) (_w - _x1 - _x1), (int) _dy1);
                    drawImage(gc, to, _toData, 0, (int) _y2, _w, (int) _dy2
                            , (int) _x2, (int) (_y2 * _ratio2)
                            , (int) (_w - _x2 - _x2), (int) _dy2);
                    _x1 += _dx1;
                    _x2 -= _dx2;
                } catch (Exception e) {
                    drawImage(gc, from, _fromData, 0, (int) _y1, _w, (int) _remainedSize
                            , (int) _x1, (int) (_y + _y1 * _ratio1)
                            , (int) (_w - _x1 - _x1), (int) _remainedSize);
                    drawImage(gc, to, _toData, 0, (int) _y2, _w, (int) _remainedSize
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
        if (useDataBuffersToDraw()) {
            Image buffer = new Image(gc.getDevice(), _imgDataToDraw);
            gc.drawImage(buffer, 0, 0);
            buffer.dispose();
        }
    }
    
    @Override
    protected void endTransition(Image from, Image to, GC gc, double direction) {
        if (_bgImage != null && !_bgImage.isDisposed()) {
            _bgImage.dispose();
        }
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
