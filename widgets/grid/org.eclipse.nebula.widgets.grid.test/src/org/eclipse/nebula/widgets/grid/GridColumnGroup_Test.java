/*******************************************************************************
 * Copyright (c) 2012, 2014 EclipseSource and others.
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
import static org.eclipse.nebula.widgets.grid.GridTestUtil.loadImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TreeAdapter;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings( {
  "deprecation", "restriction"
} )
public class GridColumnGroup_Test {

  private Display display;
  private Shell shell;
  private Grid grid;
  private GridColumnGroup group;

  @Before
  public void setUp() {
    display = Display.getDefault();
    shell = new Shell( display );
    grid = new Grid( shell, SWT.H_SCROLL | SWT.V_SCROLL );
    group = new GridColumnGroup( grid, SWT.NONE );
  }

  @After
  public void tearDown() {
  }

  @Test
  public void testGridColumnGroupCreation() {
    assertSame( grid, group.getParent() );
    assertSame( group, grid.getColumnGroup( 0 ) );
    assertEquals( 0, group.getColumns().length );
  }

  @Test
  public void testDispose() {
    group.dispose();

    assertTrue( group.isDisposed() );
    assertEquals( 0, grid.getColumnGroupCount() );
  }

  @Test
  public void testDispose_DisposeColumns() {
    GridColumn[] columns = createGridColumns( group, 2, SWT.NONE );

    group.dispose();

    assertTrue( columns[ 0 ].isDisposed() );
    assertTrue( columns[ 1 ].isDisposed() );
  }

  @Test
  public void testGetExpanded_Initial() {
    assertTrue( group.getExpanded() );
  }

  @Test
  public void testSetExpanded() {
    group.setExpanded( false );

    assertFalse( group.getExpanded() );
  }

  @Test
  public void testAddRemoveTreeListener() {
    TreeListener listener = new TreeAdapter() {};
    group.addTreeListener( listener );

    assertTrue( group.isListening( SWT.Expand ) );
    assertTrue( group.isListening( SWT.Collapse ) );

    group.removeTreeListener( listener );
    assertFalse( group.isListening( SWT.Expand ) );
    assertFalse( group.isListening( SWT.Collapse ) );
  }

  @Test
  public void testGetHeaderText_Initial() {
    assertEquals( "", group.getText() );
  }

  @Test
  public void testGetHeaderText() {
    group.setText( "foo" );

    assertEquals( "foo", group.getText() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetHeaderText_NullArgument() {
    group.setText( null );
  }

  @Test
  public void testGetHeaderImage_Initial() {
    assertNull( group.getImage() );
  }

  @Test
  public void testGetHeaderImage() {
    Image image = loadImage( display, Fixture.IMAGE1 );

    group.setImage( image );

    assertSame( image, group.getImage() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetHeaderImage_DisposedImage() {
    Image image = loadImage( display, Fixture.IMAGE1 );
    image.dispose();

    group.setImage( image );
  }

  @Test
  public void testGetHeaderFont_Initial() {
    assertSame( grid.getFont(), group.getHeaderFont() );
  }

  @Test
  public void testGetHeaderFont() {
    Font font = new Font( display, "Arial", 20, SWT.BOLD );

    group.setHeaderFont( font );

    assertSame( font, group.getHeaderFont() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetHeaderFont_DisposedFont() {
    Font font = new Font( display, "Arial", 20, SWT.BOLD );
    font.dispose();

    group.setHeaderFont( font );
  }

  @Test
  public void testGetHeaderWordWrap_Initial() {
    assertFalse( group.getHeaderWordWrap() );
  }

  @Test
  public void testGetHeaderWordWrap() {
    group.setHeaderWordWrap( true );

    assertTrue( group.getHeaderWordWrap() );
  }

  @Test
  public void testGetHeaderWordWrap_invalidatesCachedHeaderHeight() {
    grid.getHeaderHeight();

    group.setHeaderWordWrap( true );

    assertFalse( /*grid.layoutCache.hasHeaderHeight()*/ true );
  }

}
