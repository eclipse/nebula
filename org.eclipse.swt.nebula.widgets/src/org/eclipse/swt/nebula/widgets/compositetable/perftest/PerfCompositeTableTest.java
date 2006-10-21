package org.eclipse.swt.nebula.widgets.compositetable.perftest;

/*
 * Copyright (C) 2005 David Orme <djo@coconut-palm-software.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Orme     - Initial API and implementation
 */

import java.util.LinkedList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.nebula.widgets.compositetable.CompositeTable;
import org.eclipse.swt.nebula.widgets.compositetable.IDeleteHandler;
import org.eclipse.swt.nebula.widgets.compositetable.IInsertHandler;
import org.eclipse.swt.nebula.widgets.compositetable.IRowContentProvider;
import org.eclipse.swt.nebula.widgets.compositetable.IRowFocusListener;
import org.eclipse.swt.nebula.widgets.compositetable.ScrollEvent;
import org.eclipse.swt.nebula.widgets.compositetable.ScrollListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Comment :-
 * Can the row height follow my row's composite object ?
 * Is there's anyway that I can insert row ?
 * 
 */


/**
 * @since 3.2
 *
 */
public class PerfCompositeTableTest {

	private Shell sShell = null;  //  @jve:decl-index=0:visual-constraint="10,10"
	private CompositeTable table = null;
	
	private LinkedList personList = new LinkedList();
	
	/**
	 * 
	 */
	public PerfCompositeTableTest() {
		
		for( int li_count = 0 ; li_count < 100 ; li_count ++ ){
			
			personList.add(new Person("John" + String.valueOf( li_count ), "1234", "Wheaton" + String.valueOf( li_count ), "IL" + String.valueOf( li_count )));
		}
	}

	/**
	 * This method initializes multiRowViewer	
	 *
	 */
	private void createCompositeTable() {
		table = new CompositeTable(sShell, SWT.NONE);
		table.setRunTime(true);
		table.setWeights(new int[] {100, 100, 80, 21});
      
//      table.setFittingVertically(false);
//		table.setFittingHorizontally(false);
//		table.setLinesVisible(true);
//      table.setWeights(new int[] {30, 30, 30, 10});
//		table.setFittingHorizontally(true);
		table.addRowContentProvider(rowContentProvider);
		table.addDeleteHandler(deleteHandler);
		table.addInsertHandler(insertHandler);
		table.addRowFocusListener(rowListener);
		table.addScrollListener(scrollListener);
		table.setNumRowsInCollection(personList.size());
		createHeader();
		createRow();
	}
	
	private ScrollListener scrollListener = new ScrollListener() {
		public void tableScrolled(ScrollEvent scrollEvent) {
//			System.out.println(scrollEvent);
		}
	};
	
	private IRowContentProvider rowContentProvider = new IRowContentProvider() {
		public void refresh(CompositeTable table, int currentObjectOffset, Control row) {
			Row rowObj = (Row) row;
			Person person = (Person)personList.get(currentObjectOffset);
			rowObj.name.setText(person.name);
			rowObj.address.setText(person.address);
			rowObj.city.setText(person.city);
			rowObj.state.setText(person.state);
		}
	};

	private IDeleteHandler deleteHandler = new IDeleteHandler() {
		public boolean canDelete(int rowInCollection) {
			return true;
		}
		public void deleteRow(int rowInCollection) {
			personList.remove(rowInCollection);
		}
	};
	
	private IInsertHandler insertHandler = new IInsertHandler() {
		public int insert(int positionHint) {
			Person newPerson = new Person();
			personList.add(positionHint, newPerson);
			return positionHint;
//			int newPosition = (int)(Math.random() * (personList.size()+1));
//			personList.add(newPosition, newPerson);
//			return newPosition;
		}
	};
	
	private IRowFocusListener rowListener = new IRowFocusListener() {
		public boolean requestRowChange(CompositeTable sender, int currentObjectOffset, Control row) {
//			System.out.println("requestRC");
			return true;
		}
		public void depart(CompositeTable sender, int currentObjectOffset, Control row) {
//			System.out.println("depart");
			Person person = (Person)personList.get(currentObjectOffset);
			Row rowObj = (Row) row;
			person.name = rowObj.name.getText();
			person.address = rowObj.address.getText();
			person.city = rowObj.city.getText();
			person.state = rowObj.state.getText();
		}
		public void arrive(CompositeTable sender, int currentObjectOffset, Control row) {
//			System.out.println("arrive");
		}
	};
	
	/**
	 * This method initializes header	
	 *
	 */
	private void createHeader() {
		new Header(table, SWT.NONE);
	}

	/**
	 * This method initializes row	
	 *
	 */
	private void createRow() {
		new Row(table, SWT.NONE);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = Display.getDefault();
		PerfCompositeTableTest thisClass = new PerfCompositeTableTest();
		thisClass.createSShell();
		thisClass.sShell.open();

		while (!thisClass.sShell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	/**
	 * This method initializes sShell
	 */
	private void createSShell() {
		sShell = new Shell();
		sShell.setText("Shell");
		sShell.setLayout(new FillLayout());
		createCompositeTable();
		sShell.setSize(new org.eclipse.swt.graphics.Point(445,243));
	}

}