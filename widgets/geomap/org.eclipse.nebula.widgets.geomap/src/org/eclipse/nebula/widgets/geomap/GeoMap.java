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
import java.io.InputStream;
import java.net.URL;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.SashForm;
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
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;



/**
 * MapPanel display tiles from openstreetmap as is. This simple minimal viewer supports zoom around mouse-click center and has a simple api.
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
    
    /*
    wait icon 
    credits to: RaminusFalcon
    obtained from: http://commons.wikimedia.org/wiki/File:GreenHourglass_up.svg
    
    world icon
    credits to: Zeus
    http://commons.wikimedia.org/wiki/File:Gartoon-fs-ftp.png
     */
    

    private static final Logger log = Logger.getLogger(GeoMap.class.getName());
    
    public static final class TileServer {
        private final String url;
        private final int maxZoom;
        private boolean broken;

        private TileServer(String url, int maxZoom) {
            this.url = url;
            this.maxZoom = maxZoom;
        }

        public String toString() {
            return url;
        }

        public int getMaxZoom() {
            return maxZoom;
        }
        public String getURL() {
            return url;
        }

        public boolean isBroken() {
            return broken;
        }

        public void setBroken(boolean broken) {
            this.broken = broken;
        }
    }
    
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
    
    private static class Tile {
        private final String key;
        public final int x, y, z;
        public Tile(String tileServer, int x, int y, int z) {
            this.key = tileServer;
            this.x = x;
            this.y = y;
            this.z = z;
        }
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((key == null) ? 0 : key.hashCode());
            result = prime * result + x;
            result = prime * result + y;
            result = prime * result + z;
            return result;
        }
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Tile other = (Tile) obj;
            if (key == null) {
                if (other.key != null)
                    return false;
            } else if (!key.equals(other.key))
                return false;
            if (x != other.x)
                return false;
            if (y != other.y)
                return false;
            if (z != other.z)
                return false;
            return true;
        }
    }
    
    public class TileCache {
        private LinkedHashMap<Tile,AsyncImage> map = new LinkedHashMap<Tile,AsyncImage>(CACHE_SIZE, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<Tile,AsyncImage> eldest) {
                boolean remove = size() > CACHE_SIZE;
                if (remove)
                    eldest.getValue().dispose(getDisplay());
                return remove;
            }
        };
        public void put(TileServer tileServer, int x, int y, int z, AsyncImage image) {
            map.put(new Tile(tileServer.getURL(), x, y, z), image);
        }
        public AsyncImage get(TileServer tileServer, int x, int y, int z) {
            return map.get(new Tile(tileServer.getURL(), x, y, z));
        }
        public void remove(TileServer tileServer, int x, int y, int z) {
            map.remove(new Tile(tileServer.getURL(), x, y, z));
        }
        public int getSize() {
            return map.size();
        }
    }
    
    public final class AsyncImage implements Runnable {
        private final AtomicReference<ImageData> imageData = new AtomicReference<ImageData>();
        private Image image; // might as well be thread-local
        private FutureTask<Boolean> task;
        private volatile long stamp = zoomStamp.longValue();
        private final TileServer tileServer;
        private final int x, y, z;

        public AsyncImage(TileServer tileServer, int x, int y, int z) {
            this.tileServer = tileServer;
            this.x = x;
            this.y = y;
            this.z = z;
            task = new FutureTask<Boolean>(this, Boolean.TRUE);
            executor.execute(task);
        }
        
        public void run() {
            String url = getTileString(tileServer, x, y, z);
            if (stamp != zoomStamp.longValue()) {
                //System.err.println("pending load killed: " + url);
                try {
                    // here is a race, we just live with.
                    if (!getDisplay().isDisposed()) {
                        getDisplay().asyncExec(new Runnable() {
                            public void run() {
                                getCache().remove(tileServer, x, y, z);
                            }
                        });
                    }
                } catch (SWTException e) {
                    log.log(Level.INFO, "swt exception during redraw display-race, ignoring");
                }
                
                return;
            }
            try {
                //System.err.println("fetch " + url);
                //Thread.sleep(2000);
                InputStream in = new URL(url).openConnection().getInputStream();
                imageData.set(new ImageData(in));
                try {
                    // here is a race, we just live with.
                    if (!getDisplay().isDisposed()) {
                        getDisplay().asyncExec(new Runnable() {
                            public void run() {
                                redraw();
                            }
                        });
                    }
                } catch (SWTException e) {
                    log.log(Level.INFO, "swt exception during redraw display-race, ignoring");
                }
            } catch (Exception e) {
                log.log(Level.SEVERE, "failed to load imagedata from url: " + url, e);
            }
        }
        
        public ImageData getImageData(Device device) {
            return imageData.get();
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
                //System.err.println("disposing: " + getTileString(tileServer, x, y, z));
                image.dispose();
            }
        }
        
        private void checkThread(Display display) {
            // jdk 1.6 bug from checkWidget still fails here
            if (display.getThread() != Thread.currentThread()) {
                throw new IllegalStateException("wrong thread to pick up the image");
            }
        }
    }
   
    private class MapMouseListener implements MouseListener, MouseWheelListener, MouseMoveListener, MouseTrackListener {
        private Point mouseCoords = new Point(0, 0);
        private Point downCoords;
        private Point downPosition;
        
        public void mouseEnter(MouseEvent e) {
            GeoMap.this.forceFocus();
        }
        
        public void mouseExit(MouseEvent e) {
        }

        public void mouseHover(MouseEvent e) {
        }
        
        public void mouseDoubleClick(MouseEvent e) {
            if (e.button == 1) 
                zoomIn(new Point(mouseCoords.x, mouseCoords.y));
            else if (e.button == 3)
                zoomOut(new Point(mouseCoords.x, mouseCoords.y));
        }
        public void mouseDown(MouseEvent e) {
            if (e.button == 1 && (e.stateMask & SWT.CTRL) != 0) {
                setCenterPosition(getCursorPosition());
                redraw();
            }
            if (e.button == 1) {
                downCoords = new Point(e.x, e.y);
                downPosition = getMapPosition();
            }
        }
        public void mouseUp(MouseEvent e) {
            if (e.count == 1) {
                handleDrag(e);
            }
            downCoords = null;
            downPosition = null;
        }
        
        public void mouseMove(MouseEvent e) {
            handlePosition(e);
            handleDrag(e);
        }
        public void mouseScrolled(MouseEvent e) {
            if (e.count == 1)
                zoomIn(new Point(mouseCoords.x, mouseCoords.y));
            else if (e.count == -1)
                zoomOut(new Point(mouseCoords.x, mouseCoords.y));
        }
        
        private void handlePosition(MouseEvent e) {
            mouseCoords = new Point(e.x, e.y);
        }

        private void handleDrag(MouseEvent e) {
            if (downCoords != null) {
                int tx = downCoords.x - e.x;
                int ty = downCoords.y - e.y;
                setMapPosition(downPosition.x + tx, downPosition.y + ty);
                GeoMap.this.redraw();
            }
        }    
    }
    
    //-------------------------------------------------------------------------
    // tile url construction.
    // change here to support some other tile

    public static String getTileString(TileServer tileServer, int xtile, int ytile, int zoom) {
        String number = ("" + zoom + "/" + xtile + "/" + ytile);
        String url = tileServer.getURL() + number + ".png";
        return url;
    }

    /* constants ... */
    public static final TileServer[] TILESERVERS = {
        new TileServer("http://tile.openstreetmap.org/", 18),
        new TileServer("http://tah.openstreetmap.org/Tiles/tile/", 17),
    };
 
    public static final String NAMEFINDER_URL = "http://nominatim.openstreetmap.org/search";
    
    public static final String ABOUT_MSG =
        "MapWidget - Minimal Openstreetmap/Maptile Viewer\r\n" +
        "Requirements: Java + SWT. Opensource and licensed under EPL.\r\n" +
        "\r\n" +
        "Web/Source: <a href=\"http://mappanel.sourceforge.net\">http://mappanel.sourceforge.net</a>\r\n" +
        "Written by stepan.rutz. Contact <a href=\"mailto:stepan.rutz@gmx.de?subject=SWT%20MapWidget\">stepan.rutz@gmx.de</a>\r\n\r\n" +
        "Tileserver and Namefinder are part of Openstreetmap.org or associated projects.\r\n";
    	//"MapPanel gets all its data the openstreetmap servers.\r\n\r\n" +
        //"Please support the effort at <a href=\"http://www.openstreetmap.org\">http://www.openstreetmap.org/</a>.\r\n";
        //"Please keep in mind this application is just a alternative renderer for swt.\r\n";
    
    
    /* basically not be changed */
    private static final int TILE_SIZE = 256;
    public static final int CACHE_SIZE = 256;
    public static final int IMAGEFETCHER_THREADS = 4;
    
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private Point mapSize = new Point(0, 0);
    private Point mapPosition = new Point(0, 0);
    private int zoom;
    private AtomicLong zoomStamp = new AtomicLong();

    private TileServer tileServer = TILESERVERS[0];
    private TileCache cache = new TileCache();
    private Stats stats = new Stats();
    private MapMouseListener mouseListener = new MapMouseListener();
    
    private BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
    private ThreadFactory threadFactory = new ThreadFactory( ) {
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName("Async Image Loader " + t.getId() + " " + System.identityHashCode(t));
            t.setDaemon(true);
            return t;
        }
    };
    private ThreadPoolExecutor executor = new ThreadPoolExecutor(IMAGEFETCHER_THREADS, 16, 2, TimeUnit.SECONDS, workQueue, threadFactory);
    
    private Color waitBackground, waitForeground;
    
    public GeoMap(Composite parent, int style) {
        this(parent, style, new Point(275091, 180145), 11);
    }
    
    public GeoMap(Composite parent, int style, Point mapPosition, int zoom) {
        super(parent, SWT.DOUBLE_BUFFERED | style);
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
        setMapPosition(mapPosition);
        addMouseListener(mouseListener);
        addMouseMoveListener(mouseListener);
        addMouseWheelListener(mouseListener);
        addMouseTrackListener(mouseListener);
        /// TODO: check tileservers
    }
    
    protected void paintControl(PaintEvent e) {
        GC gc = e.gc;
        
        getStats().reset();
        long t0 = System.currentTimeMillis();
        Point size = getSize();
        int width = size.x, height = size.y;
        int x0 = (int) Math.floor(((double) mapPosition.x) / TILE_SIZE);
        int y0 = (int) Math.floor(((double) mapPosition.y) / TILE_SIZE);
        int x1 = (int) Math.ceil(((double) mapPosition.x + width) / TILE_SIZE);
        int y1 = (int) Math.ceil(((double) mapPosition.y + height) / TILE_SIZE);

        int dy = y0 * TILE_SIZE - mapPosition.y;
        for (int y = y0; y < y1; ++y) {
            int dx = x0 * TILE_SIZE - mapPosition.x;
            for (int x = x0; x < x1; ++x) {
                paintTile(gc, dx, dy, x, y);
                dx += TILE_SIZE;
                ++getStats().tileCount;
            }
            dy += TILE_SIZE;
        }
        
        long t1 = System.currentTimeMillis();
        stats.dt = t1 - t0;
        //gc.drawString("dis ya draw", 20, 50);
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
            TileCache cache = getCache();
            TileServer tileServer = getTileServer();
            AsyncImage image = cache.get(tileServer, x, y, zoom);
            if (image == null) {
                image = new AsyncImage(tileServer, x, y, zoom);
                cache.put(tileServer, x, y, zoom, image);
            }
            if (image.getImage(getDisplay()) != null) {
                gc.drawImage(image.getImage(getDisplay()), dx, dy);
                imageDrawn = true;
            }
        }
        if (DEBUG && (!imageDrawn && (tileInBounds || DRAW_OUT_OF_BOUNDS))) {
            gc.setBackground(display.getSystemColor(tileInBounds ? SWT.COLOR_GREEN : SWT.COLOR_RED));
            gc.fillRectangle(dx + 4, dy + 4, TILE_SIZE - 8, TILE_SIZE - 8);
            gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
            String s = "T " + x + ", " + y + (!tileInBounds ? " #" : "");
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
    
    public TileCache getCache() {
        return cache;
    }
    
    public TileServer getTileServer() {
        return tileServer;
    }
    
    public void setTileServer(TileServer tileServer) {
        this.tileServer = tileServer;
        redraw();
    }
    
    public Stats getStats() {
        return stats;
    }
    
    public Point getMapPosition() {
        return new Point(mapPosition.x, mapPosition.y);
    }

    public void setMapPosition(Point mapPosition) {
        setMapPosition(mapPosition.x, mapPosition.y);
    }

    public void setMapPosition(int x, int y) {
        if (mapPosition.x == x && mapPosition.y == y)
            return;
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
        if (zoom == this.zoom)
            return;
        zoomStamp.incrementAndGet();
        int oldZoom = this.zoom;
        this.zoom = Math.min(getTileServer().getMaxZoom(), zoom);
        mapSize.x = getXMax();
        mapSize.y = getYMax();
        pcs.firePropertyChange("zoom", oldZoom, zoom);
    }

    public void zoomIn(Point pivot) {
        if (getZoom() >= getTileServer().getMaxZoom())
            return;
        Point mapPosition = getMapPosition();
        int dx = pivot.x;
        int dy = pivot.y;
        setZoom(getZoom() + 1);
        setMapPosition(mapPosition.x * 2 + dx, mapPosition.y * 2 + dy);
        redraw();
    }

    public void zoomOut(Point pivot) {
        if (getZoom() <= 1)
            return;
        Point mapPosition = getMapPosition();
        int dx = pivot.x;
        int dy = pivot.y;
        setZoom(getZoom() - 1);
        setMapPosition((mapPosition.x - dx) / 2, (mapPosition.y - dy) / 2);
        redraw();
    }

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
        return new Point(mapPosition.x + mouseListener.mouseCoords.x, mapPosition.y + mouseListener.mouseCoords.y);
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
    // utils
    public static String format(double d) {
        return String.format("%.5f", d);
    }

    public static double getN(int y, int z) {
        double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z);
        return n;
    }

    public static double position2lon(int x, int z) {
        double xmax = TILE_SIZE * (1 << z);
        return x / xmax * 360.0 - 180;
    }

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
        int xtile = (int) Math.floor((lon + 180) / 360 * (1 << zoom));
        int ytile = (int) Math.floor((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1 << zoom));
        return getTileString(tileServer, xtile, ytile, zoom);
    }
}
