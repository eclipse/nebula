/*******************************************************************************
 * Copyright (c) 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 293508)
 *******************************************************************************/
package org.eclipse.nebula.widgets.radiogroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import junit.framework.TestCase;

public class RadioGroupTest extends TestCase {
	static class MyListener implements Listener {
		private List events = new ArrayList();
		Event lastEvent;

		public void handleEvent(Event e) {
			events.add(e);
			lastEvent = e;
		}

		int eventCount() {
			return events.size();
		}

		Event event(int i) {
			return (Event) events.get(i);
		}
	}

	private Display display;
	private boolean createdDisplay = false;
	private Composite composite;
	private RadioGroup group;
	private RadioItem item;
	private MyListener listener;

	protected void setUp() throws Exception {
		super.setUp();

		display = Display.getCurrent();
		if( display == null ){
			display = new Display();
			createdDisplay = true;
		}
		
		composite = new Shell(display);
		composite.setLayout(new FillLayout());

		listener = new MyListener();
	}

	protected void tearDown() throws Exception {
		composite.dispose();
	
		if( createdDisplay){
			display.dispose();
		}
		
		super.tearDown();
	}

	public void testConstructor_NullParent() {
		try {
			new RadioGroup(null, 0);
			fail("Excepted exception");
		} catch (IllegalArgumentException expected) {
		}
	}

	public void testDispose_DisposesItems() {
		createGroup();
		createItem();
		item.addListener(SWT.Dispose, listener);

		group.dispose();

		assertEventType(SWT.Dispose, listener.lastEvent);
		assertTrue(item.isDisposed());
	}

	private static void assertEventType(int eventType, Event event) {
		assertNotNull(event);
		assertEquals(event.type, eventType);
	}

	private void createGroup() {
		group = new RadioGroup(composite, SWT.NONE);
	}

	private void createItem() {
		item = new RadioItem(group, SWT.NONE);
	}

	public void testDispose_DisposesItemButtons() {
		createGroup();
		createItem();
		Button button = item.getButton();
		button.addListener(SWT.Dispose, listener);

		group.dispose();

		assertEventType(SWT.Dispose, listener.lastEvent);
		assertTrue(button.isDisposed());
	}

	public void testDisposeSelectedItem_FiresDeselectEvent() {
		createGroup();
		createItem();
		group.setSelection(item);
		group.addListener(SWT.Selection, listener);

		item.dispose();

		assertEventType(SWT.Selection, listener.lastEvent);
		assertNull(group.getSelection());
	}

	public void testGetItems_DefensiveCopy() {
		createGroup();
		createItem();

		RadioItem[] items = group.getItems();
		items[0] = null;

		items = group.getItems();
		assertEquals(1, items.length);
		assertSame(item, items[0]);
	}

	public void testSelect_FiresSelectionEvent() {
		createGroup();
		createItem();
		group.addListener(SWT.Selection, listener);

		group.select(0);

		assertEventType(SWT.Selection, listener.lastEvent);
		assertSame(item, group.getSelection());
	}

	public void testDeselectAll_FiresSelectionEvent() {
		createGroup();
		createItem();
		group.select(0);
		group.addListener(SWT.Selection, listener);

		group.deselectAll();

		assertEventType(SWT.Selection, listener.lastEvent);
		assertNull(group.getSelection());
	}

	public void testSetSelection_NullToItem() {
		createGroup();
		createItem();
		group.addListener(SWT.Selection, listener);

		group.setSelection(item);

		assertEventType(SWT.Selection, listener.lastEvent);
		assertSame(item, group.getSelection());
	}

	public void testSetSelection_ItemToNull() {
		createGroup();
		createItem();
		group.setSelection(item);
		group.addListener(SWT.Selection, listener);

		group.setSelection(null);

		assertEventType(SWT.Selection, listener.lastEvent);
		assertNull(group.getSelection());
	}

	public void testSetSelection_NullToNull() {
		createGroup();
		createItem();
		group.addListener(SWT.Selection, listener);

		group.setSelection(null);

		assertEquals(0, listener.events.size());
	}

	public void testSetSelection_ItemToSameItem() {
		createGroup();
		createItem();
		group.setSelection(item);
		group.addListener(SWT.Selection, listener);

		group.setSelection(item);

		assertEquals(0, listener.events.size());
	}

	public void testSetSelection_ItemToDifferentItem() {
		createGroup();
		createItem();
		group.setSelection(item);
		RadioItem item2 = new RadioItem(group, SWT.NONE);
		group.addListener(SWT.Selection, listener);

		group.setSelection(item2);

		assertEquals(2, listener.eventCount());
		assertEvent(SWT.Selection, null, -1, listener.event(0));
		assertEvent(SWT.Selection, item2, 1, listener.event(1));
	}

	private void assertEvent(int eventType, RadioItem item, int index,
			Event event) {
		assertNotNull(event);
		assertEquals(eventType, event.type);
		assertSame(item, event.item);
		assertEquals(index, event.index);
	}

	public void testClickSelection_NullToItem() {
		createGroup();
		createItem();
		group.addListener(SWT.Selection, listener);

		simulateClick(item);

		assertEquals(1, listener.eventCount());
		assertEvent(SWT.Selection, item, 0, listener.lastEvent);
	}

	private void simulateClick(RadioItem item) {
		simulateDeselect(group.getSelection());

		Button button = item.getButton();
		button.setSelection(true);
		button.notifyListeners(SWT.Selection, null);
	}

	private void simulateDeselect(RadioItem item) {
		if (item == null)
			return;
		if (!item.isSelected())
			return;

		Button button = item.getButton();
		button.setSelection(false);
		button.notifyListeners(SWT.Selection, null);
	}

	public void testClickSelection_ItemToItem() {
		createGroup();
		createItem();
		group.setSelection(item);
		RadioItem item2 = new RadioItem(group, SWT.NONE);
		group.addListener(SWT.Selection, listener);

		simulateClick(item2);

		assertFalse(item.isSelected());
		assertTrue(item2.isSelected());
		assertEquals(2, listener.eventCount());
		assertEvent(SWT.Selection, null, -1, listener.event(0));
		assertEvent(SWT.Selection, item2, 1, listener.event(1));
	}

	public void testNewItemWithIndex() {
		createGroup();
		RadioItem item1 = new RadioItem(group, SWT.NONE);
		RadioItem item2 = new RadioItem(group, SWT.NONE, 0);
		RadioItem item3 = new RadioItem(group, SWT.NONE, -1);

		RadioItem[] items = group.getItems();
		RadioItem[] expectedItems = { item2, item1, item3 };
		assertEquals(Arrays.asList(expectedItems), Arrays.asList(items));

		// TODO getChildren will be changed to return empty array at some point
		Control[] buttons = group.getChildren();
		Control[] expectedButtons = { item2.getButton(), item1.getButton(),
				item3.getButton() };
		assertEquals(Arrays.asList(expectedButtons), Arrays.asList(buttons));
	}

	public void testConstructor_FlatStyle() {
		group = new RadioGroup(composite, SWT.FLAT);
		assertFlatStyle(group);

		createItem();
		assertFlatStyle(item.getButton());
	}

	private void assertFlatStyle(Widget widget) {
		assertTrue((widget.getStyle() & SWT.FLAT) != 0);
	}
}
