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
import java.io.InputStream;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.jface.viewer.radiogroup.RadioGroupViewer;
import org.eclipse.nebula.jface.viewer.radiogroup.RadioGroupViewerUpdater;
import org.eclipse.nebula.widgets.radiogroup.RadioGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
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
				Shell shell = createShell(display);

				shell.open();
				while (!shell.isDisposed())
					if (!display.readAndDispatch())
						display.sleep();

				display.dispose();
			}
		});
	}

	private static Shell createShell(Display display) {
		final Shell shell = new Shell();
		shell.setLayout(new GridLayout(2, false));

		Group actionGroup = new Group(shell, SWT.NONE);
		actionGroup
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		actionGroup.setText(" Actions ");
		RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
		rowLayout.fill = true;
		actionGroup.setLayout(rowLayout);

		final IObservableList people = createModel(display);

		final RadioGroupViewer radioGroupViewer = createRadioGroupViewer(shell,
				people);

		createPushButton(actionGroup, "Add person", new Listener() {
			public void handleEvent(Event event) {
				InputDialog dialog = new InputDialog(shell, "Add person",
						"Enter name", "<name>", null);
				if (dialog.open() == Window.OK) {
					Person person = new Person(dialog.getValue(), null);
					people.add(person);
				}
			}
		});

		createPushButton(actionGroup, "Rename person", new Listener() {
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

		createPushButton(actionGroup, "Remove person", new Listener() {
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

		createPushButton(actionGroup, "Refresh", new Listener() {
			public void handleEvent(Event event) {
				radioGroupViewer.refresh();
			}
		});

		final Label statusLabel = new Label(shell, SWT.NONE);
		GridData statusLabelData = new GridData(SWT.FILL, SWT.FILL, true, false);
		statusLabelData.horizontalSpan = 2;
		statusLabel.setLayoutData(statusLabelData);

		radioGroupViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						statusLabel.setText(event.getSelection().toString());
					}
				});

		return shell;
	}

	private static RadioGroupViewer createRadioGroupViewer(Composite parent,
			IObservableList input) {
		Group group = new Group(parent, SWT.NONE);
		group.setText(" RadioGroupViewer ");
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		group.setLayout(new FillLayout());

		RadioGroupViewer radioGroupViewer = new RadioGroupViewer(
				new RadioGroup(group, SWT.VERTICAL));

		ObservableListContentProvider cp = new ObservableListContentProvider(
				new RadioGroupViewerUpdater(radioGroupViewer));

		radioGroupViewer.setContentProvider(cp);
		IObservableMap labelMap = BeansObservables.observeMap(cp
				.getKnownElements(), Person.class, "name");
		radioGroupViewer.setLabelProvider(new ObservableMapLabelProvider(
				labelMap) {
			public Image getImage(Object element) {
				return ((Person) element).getImage();
			}
		});

		radioGroupViewer.setInput(input);

		return radioGroupViewer;
	}

	private static IObservableList createModel(Display display) {
		final IObservableList radioItems = new WritableList();
		radioItems.add(new Person("Tom", loadImage(display, "tom.png")));
		radioItems.add(new Person("Dick", loadImage(display, "dick.png")));
		radioItems.add(new Person("Harry", loadImage(display, "harry.png")));
		return radioItems;
	}

	private static Button createPushButton(Composite parent, String text,
			Listener listener) {
		Button button = new Button(parent, SWT.PUSH);
		button.setText(text);
		button.addListener(SWT.Selection, listener);
		return button;
	}

	private static Image loadImage(Display display, String filename) {
		InputStream is = RadioGroupViewerSnippet01.class
				.getResourceAsStream(filename);
		return (is == null) ? null : new Image(display, is);
	}

	static class Person {
		private String name;
		private Image image;

		private PropertyChangeSupport changeSupport = new PropertyChangeSupport(
				this);

		Person(String name, Image image) {
			this.name = name;
			this.image = image;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			changeSupport.firePropertyChange("name", this.name,
					this.name = name);
		}

		public Image getImage() {
			return image;
		}

		public void setImage(Image image) {
			changeSupport.firePropertyChange("image", this.image,
					this.image = image);
		}

		public String toString() {
			return "Person { name: " + name + ", image: " + image + " }";
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
