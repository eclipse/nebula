/*******************************************************************************
 * Copyright (c) 2008, 2012 Stepan Rutz.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stepan Rutz - initial implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.geomap;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URL;
import java.net.URLConnection;
import java.util.EventListener;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.nebula.widgets.geomap.internal.DefaultMouseHandler;
import org.eclipse.nebula.widgets.geomap.internal.DefaultMouseHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * GeoMap display tiles from openstreetmap as is. This simple minimal viewer supports zoom around mouse-click center and has a simple api.
 * A number of tiles are cached. See {@link #CACHE_SIZE} constant. If you use this it will create traffic on the tileserver you are
 * using. Please be conscious about this.
 *
 * This class is a JPanel which can be integrated into any swing app just by creating an instance and adding like a JLabel.
 *
 * The map has the size <code>256*1<<zoomlevel</code>. This measure is referred to as map-coordinates. Geometric locations
 * like longitude and latitude can be obtained by helper methods. Note that a point in map-coordinates corresponds to a given
 * geometric position but also depending on the current zoom level.
 *
 * You can zoomIn around current mouse position by left double click. Left right click zooms out.
 *
 * <p>
 * Methods of interest are
 * <ul>
 * <li>{@link #setZoom(int)} which sets the map's zoom level. Values between 1 and 18 are allowed.</li>
 * <li>{@link #setMapPosition(Point)} which sets the map's top left corner. (In map coordinates)</li>
 * <li>{@link #setCenterPosition(Point)} which sets the map's center position. (In map coordinates)</li>
 * <li>{@link #computePosition(java.awt.geom.Point2D.Double)} returns the position in the map panels coordinate system
 * for the given longitude and latitude. If you want to center the map around this geometric location you need
 * to pass the result to the method</li>
 * </ul>
 * </p>
 *
 * <p>For performance tuning the two crucial parameters are the size of the {@link TileCache} and the
 * number of image-loader threads.
 * </p>
 *
 * <p>As mentioned above Longitude/Latitude functionality is available via the method {@link #computePosition(java.awt.geom.Point2D.Double)}.
 * If you have a GIS database you can get this info out of it for a given town/location, invoke {@link #computePosition(java.awt.geom.Point2D.Double)} to
 * translate to a position for the given zoom level and center the view around this position using {@link #setCenterPosition(Point)}.
 * </p>
 *
 * <p>The properties <code>zoom</code> and <code>mapPosition</code> are bound and can be tracked via
 * regular {@link PropertyChangeListener}s.</p>
 *
 * <p>License is EPL (Eclipse Public License) http://www.eclipse.org/legal/epl-v10.html.  Contact at stepan.rutz@gmx.de</p>
 *
 * @author stepan.rutz
 * @version $Revision$
 */
public class GeoMap extends Canvas {
    
    private static final Logger log = Logger.getLogger(GeoMap.class.getName());
    
    /**
     * Stats class, usefull for debugging caching of tiles,
     * also makes sure nothing is drawn (and loaded) that is
     * not displayed.
     * @since 3.3
     *
     */
    public static class Stats {
        public int tileCount;
        public long dt;

        private Stats() {
            reset();
        }
        private void reset() {
            tileCount = 0;
            dt = 0;
        }
    }
    
    /**
     * A tile-cache holds tiles. Instances of {@link TileRef} are used as keys for
     * the cache. The actual data is held in instances of {@link AsyncImage} which
     * is an image that automatically loads itself with one of the supplied image
     * loader threads.
     * @since 3.3
     *
     */
    
    @SuppressWarnings("serial")
	public class TileCache extends LinkedHashMap<TileRef, AsyncImage> {

		private TileCache() {
			 super(cacheSize, 0.75f, true);
		}

		protected boolean removeEldestEntry(Map.Entry<TileRef, AsyncImage> eldest) {
            boolean remove = size() > cacheSize;
            if (remove) {
            	eldest.getValue().dispose(getDisplay());
            }
            return remove;
        }
    }
    
    /**
     * An sync image that loads itself in the background on an image-fetcher thread.
     * Once its loaded it will trigger a redraw. Sometimes redraws that 
     * are not really necessary can be triggered, but that is not relevant in terms of
     * performance for this swt-component.
     *
     */
    public final class AsyncImage implements Runnable {

    	private final TileRef tile;
    	private final String tileUrl;
    	
    	private volatile long stamp = zoomStamp.longValue();
        private final AtomicReference<ImageData> imageData = new AtomicReference<ImageData>();
        private Image image = null; // might as well be thread-local

        public AsyncImage(TileRef tile, String tileUrl) {
        	this.tile = tile;
        	this.tileUrl = tileUrl;
            executor.execute(new FutureTask<Boolean>(this, Boolean.TRUE));
        }
        
        public void run() {
            if (stamp != zoomStamp.longValue()) {
                try {
                    // here is a race, we just live with.
                    if (! getDisplay().isDisposed()) {
                        getDisplay().asyncExec(new Runnable() {
                            public void run() {
                                cache.remove(tile);
                            }
                        });
                    }
                } catch (SWTException e) {
                    log.log(Level.INFO, "swt exception during redraw display-race, ignoring");
                }
                return;
            }
            try {
                URLConnection con = new URL(tileUrl).openConnection();
                con.setRequestProperty("User-Agent", "org.eclipse.nebula.widgets.geomap.GeoMap"); 
				imageData.set(new ImageData(con.getInputStream()));
                try {
                    // here is a race, we just live with.
                    if (! getDisplay().isDisposed()) {
                        getDisplay().asyncExec(new Runnable() {
                            public void run() {
                                redraw(tile);
                            }
                        });
                    }
                } catch (SWTException e) {
                    log.log(Level.INFO, "swt exception during redraw display-race, ignoring");
                }
            } catch (Exception e) {
                log.log(Level.SEVERE, "failed to load imagedata from url: " + tileUrl, e);
            }
        }
        
        public Image getImage(Display display) {
            checkThread(display);
            if (image == null && imageData.get() != null) {
                image = new Image(display, imageData.get());
            }
            return image;
        }
        
        public void dispose(Display display) {
            checkThread(display);
            if (image != null) {
                image.dispose();
                image = null;
            }
        }
        
        private void checkThread(Display display) {
            // jdk 1.6 bug from checkWidget still fails here
            if (display.getThread() != Thread.currentThread()) {
                throw new IllegalStateException("wrong thread to pick up the image");
            }
        }
    }

    private void redraw(TileRef tile) {
    	redraw();
    }

    public static final String NAMEFINDER_URL = "http://nominatim.openstreetmap.org/search";
    
    public static final String ABOUT_MSG =
        "MapWidget - Minimal Openstreetmap/Maptile Viewer\r\n" +
        "Requirements: Java + SWT. Opensource and licensed under EPL.\r\n" +
        "\r\n" +
        "Web/Source: <a href=\"http://mappanel.sourceforge.net\">http://mappanel.sourceforge.net</a>\r\n" +
        "Written by stepan.rutz. Contact <a href=\"mailto:stepan.rutz@gmx.de?subject=SWT%20MapWidget\">stepan.rutz@gmx.de</a>\r\n\r\n" +
        "Tileserver and Nominationserver are accessed online and are part of Openstreetmap.org and not of this software.\r\n";
    	//"MapPanel gets all its data the openstreetmap servers.\r\n\r\n" +
        //"Please support the effort at <a href=\"http://www.openstreetmap.org\">http://www.openstreetmap.org/</a>.\r\n";
        //"Please keep in mind this application is just a alternative renderer for swt.\r\n";
    
    
    /* basically not be changed */
    private static final int TILE_SIZE = 256;
    
    public static final int DEFAULT_CACHE_SIZE = 256;
    public static final int DEFAULT_NUMBER_OF_IMAGEFETCHER_THREADS = 4;
    
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    private Point mapSize = new Point(0, 0);
    private Point mapPosition = new Point(0, 0);
    private int zoom;
    
    private AtomicLong zoomStamp = new AtomicLong();

    private TileServer tileServer = OsmTileServer.TILESERVERS[0];
    private TileCache cache = new TileCache();
    private Stats stats = new Stats();
    
    private Point mouseCoords = new Point(0, 0);
    private DefaultMouseHandler defaultMouseHandler = new DefaultMouseHandler(this);
    
    private BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();

    private ThreadFactory threadFactory = new ThreadFactory( ) {
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName("Async Image Loader " + t.getId() + " " + System.identityHashCode(t));
            t.setDaemon(true);
            return t;
        }
    };
    private ThreadPoolExecutor executor = new ThreadPoolExecutor(DEFAULT_NUMBER_OF_IMAGEFETCHER_THREADS, 16, 2, TimeUnit.SECONDS, workQueue, threadFactory);
    private Color waitBackground, waitForeground;
	private final int cacheSize;
    
    /**
     * Creates a new <code>GeoMap</code> using the {@link GeoMap#DEFAULT_CACHE_SIZE} size
     * for its internal cache of tiles. The map is showing the position <code>(275091, 180145</code>
     * at zoom level <code>11</code>. In other words this constructor is best used only in debugging
     * scenarios.
     * 
     * @param parent SWT parent <code>Composite</code>
     * @param style SWT style as in <code>Canvas</code>, since this class inherits from it. Double buffering is always enabed.
     */   
    public GeoMap(Composite parent, int style) {
        this(parent, style, new Point(275091, 180145), 11);
    }
    
    /**
     * Creates a new <code>GeoMap</code> using the {@link GeoMap#DEFAULT_CACHE_SIZE} size
     * for its internal cache of tiles
     * @param parent SWT parent <code>Composite</code>
     * @param style SWT style as in <code>Canvas</code>, since this class inherits from it. Double buffering is always enabed.
     * @param mapPosition initial mapPosition.
     * @param zoom initial map zoom
     */
    public GeoMap(Composite parent, int style, Point mapPosition, int zoom) {
    	this(parent, style, mapPosition, zoom, DEFAULT_CACHE_SIZE);
    }
    
    private class MouseCoordsHandler implements MouseListener, MouseMoveListener {

    	private void setMouseCoords(MouseEvent e) {
    		mouseCoords = new Point(e.x, e.y);
    	}

    	public void mouseDown(MouseEvent e) {
    		setMouseCoords(e);
    	}
    	public void mouseMove(MouseEvent e) {
    		setMouseCoords(e);
		}
		public void mouseUp(MouseEvent e) {
			setMouseCoords(e);
		}
		public void mouseDoubleClick(MouseEvent e) {
			setMouseCoords(e);
		}
    }
    
    /**
     * Creates a new <code>GeoMap</code> using the {@link GeoMap#DEFAULT_CACHE_SIZE} size
     * for its internal cache of tiles
     * @param parent SWT parent <code>Composite</code>
     * @param style SWT style as in <code>Canvas</code>, since this class inherits from it. Double buffering is always enabed.
     * @param mapPosition initial mapPosition.
     * @param zoom initial map zoom
     * @param cacheSize initial cache size, eg number of tile-images that are kept in cache
     * to prevent reloading from the network.
     */
    public GeoMap(Composite parent, int style, Point mapPosition, int zoom, int cacheSize) {
    
        super(parent, SWT.DOUBLE_BUFFERED | style);
        this.cacheSize = cacheSize;
        waitBackground = new Color(getDisplay(), 0x88, 0x88, 0x88);
        waitForeground = new Color(getDisplay(), 0x77, 0x77, 0x77);
        
        setZoom(zoom);
        addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
               GeoMap.this.widgetDisposed(e);
            }
        });
        addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent e) {
                GeoMap.this.paintControl(e);
            }
        });
        MouseCoordsHandler mouseCoordsHandler = new MouseCoordsHandler();
        addMouseListener(mouseCoordsHandler);
        addMouseMoveListener(mouseCoordsHandler);
        setMapPosition(mapPosition);
        addMouseHandler(defaultMouseHandler);
    }
    
    /**
     * Returns the default mouse handler, so it may be configured or removed.
     * @return the default mouse handler
     */
    public DefaultMouseHandler getDefaultMouseHandler() {
    	return defaultMouseHandler;
    }
    
    /**
     * Adds listener to appropriate listener lists depending on the listener interfaces that are implemented.
     * @param listener the listener
     */
    public void addMouseHandler(EventListener listener) {
		if (listener instanceof MouseListener) {
			addMouseListener((MouseListener) listener);
		}
		if (listener instanceof MouseMoveListener) {
			addMouseMoveListener((MouseMoveListener) listener);
		}
		if (listener instanceof MouseTrackListener) {
			addMouseTrackListener((MouseTrackListener) listener);
		}
		if (listener instanceof MouseWheelListener) {
			addMouseWheelListener((MouseWheelListener) listener);
		}
		if (listener instanceof PaintListener) {
			addPaintListener((PaintListener) listener);
		}
    }

    /**
     * Removes listener from appropriate listener lists depending on the listener interfaces that are implemented.
     * @param listener the listener
     */
    public void removeMouseHandler(EventListener listener) {
    	if (listener instanceof MouseListener) {
    		removeMouseListener((MouseListener) listener);
    	}
    	if (listener instanceof MouseMoveListener) {
    		removeMouseMoveListener((MouseMoveListener) listener);
    	}
    	if (listener instanceof MouseTrackListener) {
    		removeMouseTrackListener((MouseTrackListener) listener);
    	}
    	if (listener instanceof MouseWheelListener) {
    		removeMouseWheelListener((MouseWheelListener) listener);
    	}
		if (listener instanceof PaintListener) {
			removePaintListener((PaintListener) listener);
		}
    }

    public TileServer getTileServer() {
		return tileServer;
	}

	public Stats getStats() {
		return stats;
	}

	public TileCache getCache() {
		return cache;
	}
	
    /**
	 * @return Returns the cacheSize.
	 */
	public int getCacheSize() {
		return cacheSize;
	}
    
    protected void paintControl(PaintEvent e) {

        stats.reset();
        long t0 = System.currentTimeMillis();
        int x0 = (int) Math.floor(((double) mapPosition.x) / TILE_SIZE);
        int y0 = (int) Math.floor(((double) mapPosition.y) / TILE_SIZE);
        Point size = getSize();
        int x1 = (int) Math.ceil(((double) mapPosition.x + size.x) / TILE_SIZE);
        int y1 = (int) Math.ceil(((double) mapPosition.y + size.y) / TILE_SIZE);

        int dy = y0 * TILE_SIZE - mapPosition.y;
        for (int y = y0; y < y1; y++) {
        	int dx = x0 * TILE_SIZE - mapPosition.x;
            for (int x = x0; x < x1; x++) {
            	if (dx + TILE_SIZE >= e.x && dy + TILE_SIZE >= e.y && dx <= e.x + e.width && dy <= e.y + e.height) {
            		paintTile(e.gc, dx, dy, x, y);
            	} else {
            		System.out.println(dx + "," + dy + " -> " + e.x + "," + e.y + "-" + e.width + "x" + e.height);
            	}
                dx += TILE_SIZE;
                stats.tileCount++;
            }
            dy += TILE_SIZE;
        }
        
        long t1 = System.currentTimeMillis();
        stats.dt = t1 - t0;
    }
    
    private void paintTile(GC gc, int dx, int dy, int x, int y) {
        Display display = getDisplay();
        boolean DRAW_IMAGES = true;
        boolean DEBUG = false;
        boolean DRAW_OUT_OF_BOUNDS = !false;

        boolean imageDrawn = false;
        int xTileCount = 1 << zoom;
        int yTileCount = 1 << zoom;
        boolean tileInBounds = x >= 0 && x < xTileCount && y >= 0 && y < yTileCount;
        boolean drawImage = DRAW_IMAGES && tileInBounds;
        if (drawImage) {
            TileRef tileRef = new TileRef(x, y, zoom);
            AsyncImage image = cache.get(tileRef);
            if (image == null) {
                image = new AsyncImage(tileRef, tileServer.getTileURL(tileRef));
                cache.put(tileRef, image);
            }
            if (image.getImage(getDisplay()) != null) {
                gc.drawImage(image.getImage(getDisplay()), dx, dy);
                imageDrawn = true;
            } else {
                tileRef = new TileRef(x / 2, y / 2, zoom - 1);
				image = cache.get(tileRef);
                if (image != null && image.getImage(getDisplay()) != null) {
                    gc.drawImage(image.getImage(getDisplay()), (x % 2 == 0 ? 0 : TILE_SIZE / 2), (y % 2 == 0 ? 0 : TILE_SIZE / 2), TILE_SIZE / 2, TILE_SIZE / 2, dx, dy, TILE_SIZE, TILE_SIZE);
                    imageDrawn = true;
                }
            }
        }
        if (DEBUG && (!imageDrawn && (tileInBounds || DRAW_OUT_OF_BOUNDS))) {
            gc.setBackground(display.getSystemColor(tileInBounds ? SWT.COLOR_GREEN : SWT.COLOR_RED));
            gc.fillRectangle(dx + 4, dy + 4, TILE_SIZE - 8, TILE_SIZE - 8);
            gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
            String s = "T " + x + ", " + y + (! tileInBounds ? " #" : "");
            gc.drawString(s, dx + 4+ 8, dy + 4 + 12);
        }  else if (!DEBUG && !imageDrawn && tileInBounds) {
            gc.setBackground(waitBackground);
            gc.fillRectangle(dx, dy, TILE_SIZE, TILE_SIZE);
            gc.setForeground(waitForeground);
            for (int yl = 0; yl < TILE_SIZE; yl += 32) {
            	gc.drawLine(dx, dy + yl, dx + TILE_SIZE, dy + yl);
            }
            for (int xl = 0; xl < TILE_SIZE; xl += 32) {
            	gc.drawLine(dx + xl, dy, dx + xl, dy + TILE_SIZE);
            }
        }
    }

    protected void widgetDisposed(DisposeEvent e) {
        waitBackground.dispose();
        waitForeground.dispose();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
    
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }
    
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }
    
    public void setTileServer(TileServer tileServer) {
        this.tileServer = tileServer;
        cache.clear();
        redraw();
    }
    
    public Point getMapPosition() {
        return new Point(mapPosition.x, mapPosition.y);
    }

    public void setMapPosition(Point mapPosition) {
        setMapPosition(mapPosition.x, mapPosition.y);
    }

    public void setMapPosition(int x, int y) {
        if (mapPosition.x == x && mapPosition.y == y) {
        	return;
        }
        Point oldMapPosition = getMapPosition();
        mapPosition.x = x;
        mapPosition.y = y;
        pcs.firePropertyChange("mapPosition", oldMapPosition, getMapPosition());
    }

    public void translateMapPosition(int tx, int ty) {
        setMapPosition(mapPosition.x + tx, mapPosition.y + ty);
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        if (zoom == this.zoom) {
        	return;
        }
        zoomStamp.incrementAndGet();
        int oldZoom = this.zoom;
        this.zoom = Math.min(tileServer.getMaxZoom(), zoom);
        mapSize.x = getXMax();
        mapSize.y = getYMax();
        pcs.firePropertyChange("zoom", oldZoom, zoom);
    }

    public void zoomIn(Point pivot) {
        if (getZoom() >= tileServer.getMaxZoom()) {
        	return;
        }
        Point mapPosition = getMapPosition();
        int dx = pivot.x;
        int dy = pivot.y;
        setZoom(getZoom() + 1);
        setMapPosition(mapPosition.x * 2 + dx, mapPosition.y * 2 + dy);
        redraw();
    }

    public void zoomOut(Point pivot) {
        if (getZoom() <= 1) {
        	return;
        }
        Point mapPosition = getMapPosition();
        int dx = pivot.x;
        int dy = pivot.y;
        setZoom(getZoom() - 1);
        setMapPosition((mapPosition.x - dx) / 2, (mapPosition.y - dy) / 2);
        redraw();
    }

    private int zoomMargin = 10;
    
    public void zoomTo(Rectangle rect) {
    	Rectangle zoomRectangle = new Rectangle(rect.x, rect.y, rect.width, rect.height);
    	Point mapSize = getSize();
    	Point pivot = new Point(0, 0);
		do {
			// pivot on center of zoom rectangle
			zoomOut(pivot);
			// scale zoom rectangle down, to match zoom level
			zoomRectangle.x /= 2;
			zoomRectangle.y /= 2;
			zoomRectangle.width /= 2;
			zoomRectangle.height /= 2;
		} while (Math.min(mapSize.x / (zoomRectangle.width + zoomMargin), mapSize.y / (zoomRectangle.height + zoomMargin)) < 1);

		while (Math.min(mapSize.x / (zoomRectangle.width + zoomMargin), mapSize.y / (zoomRectangle.height + zoomMargin)) > 1) {
			// pivot on center of zoom rectangle
			zoomIn(pivot);
			// scale zoom rectangle up, to match zoom level
			zoomRectangle.x *= 2;
			zoomRectangle.y *= 2;
			zoomRectangle.width *= 2;
			zoomRectangle.height *= 2;
		}
		setMapPosition(zoomRectangle.x + (zoomRectangle.width - mapSize.x) / 2, zoomRectangle.y + (zoomRectangle.height - mapSize.y) / 2);
    }
    
    //
    
    public int getXTileCount() {
        return (1 << zoom);
    }

    public int getYTileCount() {
        return (1 << zoom);
    }

    public int getXMax() {
        return TILE_SIZE * getXTileCount();
    }

    public int getYMax() {
        return TILE_SIZE * getYTileCount();
    }

    public Point getCursorPosition() {
        return new Point(mapPosition.x + mouseCoords.x, mapPosition.y + mouseCoords.y);
    }

    public Point getTile(Point position) {
        return new Point((int) Math.floor(((double) position.x) / TILE_SIZE),(int) Math.floor(((double) position.y) / TILE_SIZE));
    }

    public Point getCenterPosition() {
        org.eclipse.swt.graphics.Point size = getSize();
        return new Point(mapPosition.x + size.x / 2, mapPosition.y + size.y / 2);
    }

    public void setCenterPosition(Point p) {
        org.eclipse.swt.graphics.Point size = getSize();
        setMapPosition(p.x - size.x / 2, p.y - size.y / 2);
    }

    public PointD getLongitudeLatitude(Point position) {
        return new PointD(
                position2lon(position.x, getZoom()),
                position2lat(position.y, getZoom()));
    }

    public Point computePosition(PointD coords) {
        int x = lon2position(coords.x, getZoom());
        int y = lat2position(coords.y, getZoom());
        return new Point(x, y);
    }

    //-------------------------------------------------------------------------
    // utils, there are important when using the GeoMap

    public static String format(double d) {
        return String.format("%.5f", d);
    }

    /**
     * Converts position to longitude.
     * @param x position x coord (pixels in this swt control)
     * @param z the current zoom level.
     * @return the longitude
     */
    public static double position2lon(int x, int z) {
        double xmax = TILE_SIZE * (1 << z);
        return x / xmax * 360.0 - 180;
    }

    /**
     * Converts position to latitude.
     * @param y position y coord (pixels in this swt control)
     * @param z the current zoom level.
     * @return the latitude
     */
    public static double position2lat(int y, int z) {
        double ymax = TILE_SIZE * (1 << z);
        return Math.toDegrees(Math.atan(Math.sinh(Math.PI - (2.0 * Math.PI * y) / ymax)));
    }

    public static double tile2lon(int x, int z) {
        return x / Math.pow(2.0, z) * 360.0 - 180;
    }

    public static double tile2lat(int y, int z) {
        return Math.toDegrees(Math.atan(Math.sinh(Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z))));
    }

    public static int lon2position(double lon, int z) {
        double xmax = TILE_SIZE * (1 << z);
        return (int) Math.floor((lon + 180) / 360 * xmax);
    }

    public static int lat2position(double lat, int z) {
        double ymax = TILE_SIZE * (1 << z);
        return (int) Math.floor((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * ymax);
    }

    public static String getTileNumber(TileServer tileServer, double lat, double lon, int zoom) {
        int x = (int) Math.floor((lon + 180) / 360 * (1 << zoom));
        int y = (int) Math.floor((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1 << zoom));
        return tileServer.getTileURL(new TileRef(x, y, zoom));
    }
}
