/*******************************************************************************
 * Copyright (c) 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.grid;

import static org.eclipse.nebula.widgets.grid.GridTestUtil.createGridColumns;
import static org.junit.Assert.assertTrue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class TestWithMain {

  public static void main( String[] args ) {
    
    test1();
  }

  private static void test2() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Grid grid = new Grid( shell, SWT.CHECK );
    grid.setSize( 200, 200 );
    grid.setLinesVisible( true );
    grid.setHeaderVisible( true );
    GridColumn[] columns = createGridColumns( grid, 3, SWT.NONE );
    createGridItems( grid, 10, 0, false );
    shell.pack();
    shell.open();
    grid.setColumnOrder( new int[] {
      2,
      0,
      1
    } );
    assertTrue( columns[ 2 ].isCheck() );
    while( !shell.isDisposed() ) {
      if( !display.readAndDispatch() )
        display.sleep();
    }
    display.dispose();
  }

  private static void test1() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Grid grid = new Grid( shell, SWT.V_SCROLL | SWT.H_SCROLL );
    grid.setSize( 200, 200 );
    grid.setLinesVisible( true );
    grid.setHeaderVisible( true );
    GridItem[] items = createGridItems( grid, 20, 0, true );
    shell.pack();
    shell.open();
    grid.setTopIndex( 12 );
    grid.showItem( items[ 4 ] );
    while( !shell.isDisposed() ) {
      if( !display.readAndDispatch() )
        display.sleep();
    }
    display.dispose();
  }

  public static GridItem[] createGridItems( Grid grid,
                                            int rootItems,
                                            int childItems,
                                            boolean pColumns )
  {
    GridItem[] result = new GridItem[ rootItems * ( childItems + 1 ) ];
    int counter = 0;
    if( pColumns ) {
      GridColumn column = new GridColumn( grid, SWT.NONE );
      column.setText( "Column 1" );
      column.setWidth( 100 );
    }
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
}
