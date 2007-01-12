package org.eclipse.swt.nebula.widgets.cdatetime;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

abstract class AbstractPicker extends Composite {

	static final int NOTIFY_NONE 	= 0;
	static final int NOTIFY_REGULAR = 1;
	static final int NOTIFY_DEFAULT = 2;

	/**
	 * The constructor is passed constants from the ACW class which must be converted
	 * to their corresponding SWT class constants for use in the super's constructor.
	 * No, this is not ideal, but does eliminate collisions during development.  Expect
	 * changes to this in the future.
	 * @param style constants from ACW.java
	 * @return constants from SWT.java
	 */
	static int swtStyle(CDateTime combo) {
		if(combo.isDropDown() || (CDT.BORDER & combo.style) != 0) return SWT.BORDER;
		return SWT.NONE;
	}

	CDateTime combo;
	Date selection;
	int fields = 0;


	AbstractPicker(Composite parent, CDateTime combo, Date selection) {
		super(parent, swtStyle(combo));
		this.combo = combo;
		this.selection = selection;
	}

	protected abstract void clearContents();
	
	protected abstract void createContents();
	
	protected abstract int[] getFields();
	
	int[] getFieldValues() {
		Calendar cal = Calendar.getInstance(combo.locale);
		cal.setTime(selection);
		int[] fa = getFields();
		int[] va = new int[fa.length];
		for(int i = 0; i < fa.length; i++) {
			va[i] = cal.get(fa[i]);
		}
		return va;
	}
	
	boolean isSet(int calendarField) {
		int[] fa = getFields();
		for(int i = 0; i < fa.length; i++) {
			if(fa[i] == calendarField) {
				return (fields & (1 << i)) != 0;
			}
		}
		return false;
	}
	
	void  setFields(int[] calendarFields) {
		fields = 0;
		int[] fa = getFields();
		for(int i = 0; i < calendarFields.length; i++) {
			for(int j = 0; j < fa.length; j++) {
				if(calendarFields[i] == fa[j]) 
					fields |= (1 << j);
			}
		}
	}

	public abstract boolean setFocus();

	public void  setLayoutData(Object layoutData) {
		if(layoutData instanceof GridData) super.setLayoutData(layoutData);
	}

	void setSelection(Date date, int field, int notification) {
		selection = date;
		if(notification > 0) {
			combo.setSelectionFromPicker(field, (notification == NOTIFY_DEFAULT));
		}
	}

	void updateContents() {
		clearContents();
		createContents();
		layout();
		updateLabels();
		updateSelection();
	}

	protected abstract void updateLabels();

	protected abstract void updateNullSelection();

	protected abstract void updateSelection();

	void updateSelection(Date date) {
		if(date == null) {
			updateNullSelection();
		} else {
			selection = date;
			updateSelection();
		}
	}
}
