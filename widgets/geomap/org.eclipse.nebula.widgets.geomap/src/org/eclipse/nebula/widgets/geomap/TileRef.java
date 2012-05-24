/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http\://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors\:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.geomap;

/**
 * A single tile in the map. A tile has an x and y coordinate, but
 * both only make sense in the context of a given zoom level. The
 * tile's zoom level is given by <code>z</code>.
 * 
 * <p>For caching the tiles support some equals and hashCode behavior
 * that makes them suitable as key-objects in java-util collections.</p>
 */
public final class TileRef {

    public final int x, y, z;

    public TileRef(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
	@Override
	@SuppressWarnings("nls")
    public String toString() {
    	return "Tile {" + x + ", " + y + " @ " + z + "}";
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        result = prime * result + z;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
        	return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
        	return false;
        }
        TileRef other = (TileRef) obj;
        return (x == other.x && y == other.y && z == other.z);
    }
}