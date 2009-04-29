/*******************************************************************************
 * Copyright (c) 2008 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 248956)
 *******************************************************************************/

package org.eclipse.swt.nebula.snippets.radiogroup;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.databinding.viewers.ViewerSupport;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.jface.viewer.radiogroup.RadioGroupViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class RadioGroupViewerSnippet01 {
	public static void main(String[] arrrrgs) {
		final Display display = new Display();

		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
			public void run() {
				Shell shell = createShell();

				shell.open();
				while (!shell.isDisposed())
					if (!display.readAndDispatch())
						display.sleep();

				display.dispose();
			}
		});
	}

	private static Shell createShell() {
		final IObservableList people = createModel();

		final Shell shell = new Shell();
		shell.setLayout(new GridLayout(2, false));

		Group actionGroup = new Group(shell, SWT.NONE);
		actionGroup
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		actionGroup.setText(" Actions ");
		RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
		rowLayout.fill = true;
		actionGroup.setLayout(rowLayout);

		Button addPerson = new Button(actionGroup, SWT.PUSH);
		addPerson.setText("Add person");

		Button renamePerson = new Button(actionGroup, SWT.PUSH);
		renamePerson.setText("Rename person");

		Button removePerson = new Button(actionGroup, SWT.PUSH);
		removePerson.setText("Remove person");

		Button refresh = new Button(actionGroup, SWT.PUSH);
		refresh.setText("Refresh");

		Group group = new Group(shell, SWT.NONE);
		group.setText(" RadioGroupViewer ");
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		group.setLayout(new FillLayout());

		final RadioGroupViewer radioGroupViewer = new RadioGroupViewer(group,
				SWT.VERTICAL);

		final Label statusLabel = new Label(shell, SWT.NONE);
		GridData statusLabelData = new GridData(SWT.FILL, SWT.FILL, true, false);
		statusLabelData.horizontalSpan = 2;
		statusLabel.setLayoutData(statusLabelData);

		// Bind UI

		addPerson.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				InputDialog dialog = new InputDialog(shell, "Add person",
						"Enter name", "<name>", null);
				if (dialog.open() == Window.OK) {
					Person person = new Person(dialog.getValue());
					people.add(person);
				}
			}
		});

		renamePerson.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				IStructuredSelection selection = (IStructuredSelection) radioGroupViewer
						.getSelection();
				if (!selection.isEmpty()) {
					Person person = (Person) selection.getFirstElement();
					InputDialog dialog = new InputDialog(shell,
							"Rename person", "Enter new name",
							person.getName(), null);
					if (dialog.open() == Window.OK) {
						person.setName(dialog.getValue());
					}
				}
			}
		});

		removePerson.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				IStructuredSelection selection = (IStructuredSelection) radioGroupViewer
						.getSelection();
				if (!selection.isEmpty()) {
					Person person = (Person) selection.getFirstElement();
					if (MessageDialog.openConfirm(shell, "Remove person",
							"Remove person " + person.getName() + "?")) {
						people.remove(person);
					}
				}
			}
		});

		refresh.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				radioGroupViewer.refresh();
			}
		});

		IValueProperty nameProp = BeanProperties.value(Person.class, "name");

		ViewerSupport.bind(radioGroupViewer, people, nameProp);

		ISWTObservableValue statusLabelText = WidgetProperties.text().observe(
				statusLabel);
		IObservableValue selectionName = ViewerProperties.singleSelection()
				.value(nameProp).observe(radioGroupViewer);

		DataBindingContext dbc = new DataBindingContext();
		dbc.bindValue(statusLabelText, selectionName);

		return shell;
	}

	private static IObservableList createModel() {
		final IObservableList radioItems = new WritableList();
		radioItems.add(new Person("Tom"));
		radioItems.add(new Person("Dick"));
		radioItems.add(new Person("Harry"));
		return radioItems;
	}

	static class Person {
		private String name;

		private PropertyChangeSupport changeSupport = new PropertyChangeSupport(
				this);

		Person(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			changeSupport.firePropertyChange("name", this.name,
					this.name = name);
		}

		public void addPropertyChangeListener(String propertyName,
				PropertyChangeListener listener) {
			changeSupport.addPropertyChangeListener(propertyName, listener);
		}

		public void removePropertyChangeListener(String propertyName,
				PropertyChangeListener listener) {
			changeSupport.removePropertyChangeListener(propertyName, listener);
		}

		public void addPropertyChangeListener(PropertyChangeListener listener) {
			changeSupport.addPropertyChangeListener(listener);
		}

		public void removePropertyChangeListener(PropertyChangeListener listener) {
			changeSupport.removePropertyChangeListener(listener);
		}
	}
}
