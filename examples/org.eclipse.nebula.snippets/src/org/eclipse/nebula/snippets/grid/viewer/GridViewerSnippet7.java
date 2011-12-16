/*******************************************************************************
 * Copyright (c) 2009 Tom Schindl and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tom Schindl - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.snippets.grid.viewer;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Creating a viewer where columns are dynamic
 * 
 * @author Tom Schindl <tom.schindl@bestsolution.at>
 */
public class GridViewerSnippet7 {
	private static class Committer {
		private String name;
		private Date birthday;
		private int commits;
		private int bugs;

		public Committer(String name, Date birthday, int commits, int bugs) {
			this.name = name;
			this.birthday = birthday;
			this.commits = commits;
			this.bugs = bugs;
		}

		public String getName() {
			return name;
		}

		@SuppressWarnings("unused")
		public void setName(String name) {
			this.name = name;
		}

		@SuppressWarnings("unused")
		public int getCommits() {
			return commits;
		}

		@SuppressWarnings("unused")
		public void setCommits(int commits) {
			this.commits = commits;
		}

		@SuppressWarnings("unused")
		public int getBugs() {
			return bugs;
		}

		@SuppressWarnings("unused")
		public void setBugs(int bugs) {
			this.bugs = bugs;
		}

		@SuppressWarnings("unused")
		public Date getBirthday() {
			return birthday;
		}

		@SuppressWarnings("unused")
		public void setBirthday(Date birthday) {
			this.birthday = birthday;
		}
	}

	private static interface IMediator {
		public Object getValue(int index);

		public void setValue(int index, Object value);

		public Class<?> getType(int index);

		public String getPropertyName();
	}

	private static class Mediator implements IMediator {
		private List<Object> domainObjects;
		private String propertyName;
		private PropertyDescriptor descriptor;

		public Mediator(List<?> domainObjects, String propertyName) {
			this.domainObjects = new ArrayList<Object>(domainObjects);
			this.propertyName = propertyName;

			BeanInfo beanInfo;
			try {
				beanInfo = Introspector.getBeanInfo(domainObjects.get(0)
						.getClass());
				PropertyDescriptor[] propertyDescriptors = beanInfo
						.getPropertyDescriptors();

				for (int i = 0; i < propertyDescriptors.length; i++) {
					PropertyDescriptor descriptor = propertyDescriptors[i];
					if (descriptor.getName().equals(propertyName)) {
						this.descriptor = descriptor;
						break;
					}
				}
			} catch (IntrospectionException e) {
				e.printStackTrace();
			}
		}

		public Object getValue(int index) {
			try {
				if (domainObjects.size() > index) {
					return descriptor.getReadMethod().invoke(
							domainObjects.get(index), new Object[0]);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		public void setValue(int index, Object value) {
			try {
				if (domainObjects.size() > index) {
					descriptor.getWriteMethod().invoke(
							domainObjects.get(index), value);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public Class<?> getType(int index) {
			return descriptor.getPropertyType();
		}

		public String getPropertyName() {
			return propertyName;
		}
	}

	private static class LabelProviderImpl extends ColumnLabelProvider {
		private int colIndex;

		public LabelProviderImpl(int colIndex) {
			this.colIndex = colIndex;
		}

		@Override
		public String getText(Object element) {
			IMediator m = (IMediator) element;
			if (m.getType(colIndex) == int.class) {
				return NumberFormat.getIntegerInstance().format(
						m.getValue(colIndex));
			} else if (m.getType(colIndex) == Date.class) {
				return new SimpleDateFormat("yyyy-MM-dd").format(
						m.getValue(colIndex));
			}
			return super.getText(element);
		}

	}

	private static class ContentProvider implements IStructuredContentProvider {
		private String[] attributes;
		private List<IMediator> mediators = new ArrayList<IMediator>();

		public ContentProvider(String... attributes) {
			this.attributes = attributes;
		}

		public Object[] getElements(Object inputElement) {
			return mediators.toArray();
		}

		public void dispose() {

		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			mediators = new ArrayList<IMediator>();

			if (newInput != null && newInput instanceof List<?>) {
				for (String attribute : attributes) {
					mediators.add(new Mediator((List<?>) newInput, attribute));
				}
			}
		}
	}

	private static class EditingSupportImpl extends EditingSupport {

		private int colIndex;

		private TextCellEditor integerEditor;
		private TextCellEditor dateEditor;

		public EditingSupportImpl(ColumnViewer viewer, int colIndex) {
			super(viewer);
			this.colIndex = colIndex;
			this.integerEditor = new TextCellEditor((Composite) viewer
					.getControl(), SWT.NONE);
			((Text) this.integerEditor.getControl())
					.addVerifyListener(new VerifyListener() {

						public void verifyText(VerifyEvent e) {
							String string = e.text;
							char[] chars = new char[string.length()];
							string.getChars(0, chars.length, chars, 0);
							for (int i = 0; i < chars.length; i++) {
								if (!('0' <= chars[i] && chars[i] <= '9')) {
									e.doit = false;
									return;
								}
							}
						}
					});
			this.dateEditor = new TextCellEditor((Composite) viewer
					.getControl(), SWT.NONE);
			((Text) this.dateEditor.getControl())
					.addVerifyListener(new VerifyListener() {

						public void verifyText(VerifyEvent e) {
							String string = e.text;
							char[] chars = new char[string.length()];
							string.getChars(0, chars.length, chars, 0);
							for (int i = 0; i < chars.length; i++) {
								if (!( ('0' <= chars[i] && chars[i] <= '9') || chars[i] == '-' ) ) {
									e.doit = false;
									return;
								}
							}
						}
					});
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			IMediator m = (IMediator) element;

			if (m.getType(colIndex) == int.class) {
				return integerEditor;
			} else if (m.getType(colIndex) == Date.class) {
				return dateEditor;
			}

			return null;
		}

		@Override
		protected Object getValue(Object element) {
			IMediator m = (IMediator) element;

			if (m.getType(colIndex) == int.class) {
				return m.getValue(colIndex) + "";
			} else if (m.getType(colIndex) == Date.class) {
				return new SimpleDateFormat("yyyy-MM-dd").format(m
						.getValue(colIndex));
			}

			return null;
		}

		@Override
		protected void setValue(Object element, Object value) {
			IMediator m = (IMediator) element;

			if (m.getType(colIndex) == int.class) {
				try {
					m.setValue(colIndex, Integer.parseInt(value.toString()));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}

			} else if (m.getType(colIndex) == Date.class) {
				try {
					m.setValue(colIndex, new SimpleDateFormat("yyyy-MM-dd")
							.parse(value.toString()));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			
			getViewer().update(element, null);
		}
	}

	public static void main(String[] args) {
		try {
			final Display display = new Display();
			Shell shell = new Shell(display);
			shell.setLayout(new FillLayout());

			final ImageRegistry reg = new ImageRegistry(display);
			reg.put("ICON", ImageDescriptor.createFromFile(
					GridViewerSnippet6.class, "th_vertical.gif"));

			GridTableViewer v = new GridTableViewer(shell, SWT.FULL_SELECTION
					| SWT.H_SCROLL | SWT.V_SCROLL);
			v.getGrid().setLinesVisible(true);
			v.getGrid().setHeaderVisible(true);
			v.setContentProvider(new ContentProvider("birthday", "commits",
					"bugs"));
			v.getGrid().setRowHeaderVisible(true);
			v.setRowHeaderLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					String propertyName = ((Mediator) element)
							.getPropertyName();
					return propertyName;
				}

			});

			List<Committer> committers = new ArrayList<Committer>();
			committers.add(new Committer("Tom Schindl", new Date(), 10, 5));
			committers
					.add(new Committer("Boris Bokowski", new Date(), 1000, 35));

			int i = 0;
			for (Committer committer : committers) {
				GridViewerColumn column = new GridViewerColumn(v, SWT.NONE);
				column.setEditingSupport(new EditingSupportImpl(v, i));
				column.setLabelProvider(new LabelProviderImpl(i));
				column.getColumn().setText(committer.getName());
				column.getColumn().setWidth(200);
				i++;
			}

			v.setInput(committers);

			shell.setSize(500, 200);
			shell.open();

			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}

			display.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}