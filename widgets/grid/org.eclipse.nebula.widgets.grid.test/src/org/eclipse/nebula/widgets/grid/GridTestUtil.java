/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.grid;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;


public class GridTestUtil {

  private GridTestUtil() {
    // prevent instantiation
  }

  public static GridItem[] createGridItems( Grid grid, int rootItems, int childItems ) {
    GridItem[] result = new GridItem[ rootItems * ( childItems + 1 ) ];
    GridColumn column = new GridColumn( grid, SWT.NONE );
    column.setText( "Column 1" );
    column.setWidth( 100 );
    int counter = 0;
    for( int i = 0; i < rootItems; i++ ) {
      GridItem rootItem = new GridItem( grid, SWT.NONE );
      rootItem.setText( "root_" + i );
      result[ counter ] = rootItem;
      counter++ ;
      for( int j = 0; j < childItems; j++ ) {
        GridItem childItem = new GridItem( rootItem, SWT.NONE );
        childItem.setText( "root_" + i + "_child_" + j );
        result[ counter ] = childItem;
        counter++ ;
      }
    }
    return result;
  }

  public static GridColumn[] createGridColumns( Grid grid, int columns, int style ) {
    GridColumn[] result = new GridColumn[ columns ];
    for( int i = 0; i < columns; i++ ) {
      GridColumn column = new GridColumn( grid, style );
      column.setText( "col_" + i );
      column.setWidth( 20 * ( i + 1 ) );
      result[ i ] = column;
    }
    return result;
  }

  public static GridColumn[] createGridColumns( GridColumnGroup group, int columns, int style ) {
    GridColumn[] result = new GridColumn[ columns ];
    for( int i = 0; i < columns; i++ ) {
      GridColumn column = new GridColumn( group, style );
      column.setText( "col_" + i );
      column.setWidth( 20 * ( i + 1 ) );
      result[ i ] = column;
    }
    return result;
  }

  public static Image loadImage( Display display, String name ) {
    Image result = null;
    InputStream stream = Fixture.class.getClassLoader().getResourceAsStream( name );
    if( stream != null ) {
      try {
        result = new Image( display, stream );
      } finally {
        try {
          stream.close();
        } catch( IOException unexpected ) {
          throw new RuntimeException( "Failed to close image input stream", unexpected );
        }
      }
    }
    return result;
  }
}
