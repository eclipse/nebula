package org.eclipse.nebula.widgets.cdatetime.snippets;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.jface.cdatetime.CDateTimeCellEditor;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

/**
 * Edit cell values in a table
 *
 * @author Tom Schindl <tom.schindl@bestsolution.at>
 *
 */
public class CDTEditorSample {
	private class MyContentProvider implements IStructuredContentProvider {

		/**
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement) {
			return (MyModel[]) inputElement;
		}

		/**
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose() {

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.
		 * viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		}

	}

	public class MyModel {
		public String firstName, lastName;
		public Date birthDate;

		public MyModel(String firstName, String lastName, Date birthDate) {
			super();
			this.firstName = firstName;
			this.lastName = lastName;
			this.birthDate = birthDate;
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public Date getBirthDate() {
			return birthDate;
		}

		public void setBirthDate(Date birthDate) {
			this.birthDate = birthDate;
		}

	}

	public CDTEditorSample(Shell shell) {
		final TableViewer v = new TableViewer(shell, SWT.BORDER | SWT.FULL_SELECTION);
		TableViewerColumn tc = new TableViewerColumn(v, SWT.NONE);
		tc.getColumn().setWidth(100);
		tc.getColumn().setText("First Name");
		tc.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				MyModel e = (MyModel) element;
				return e.getFirstName();
			}
		});

		tc = new TableViewerColumn(v, SWT.NONE);
		tc.getColumn().setWidth(200);
		tc.getColumn().setText("Last Name");
		tc.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				MyModel e = (MyModel) element;
				return e.getLastName();
			}
		});

		tc = new TableViewerColumn(v, SWT.NONE);
		tc.getColumn().setWidth(200);
		tc.getColumn().setText("Date of birth");
		tc.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				MyModel e = (MyModel) element;
				return new SimpleDateFormat("MM/dd/yyyy").format(e.getBirthDate());
			}
		});

		v.setContentProvider(new MyContentProvider());
		v.setCellModifier(new ICellModifier() {

			/**
			 * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object,
			 *      java.lang.String)
			 */
			public boolean canModify(Object element, String property) {
				return true;
			}

			/**
			 * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object,
			 *      java.lang.String)
			 */
			public Object getValue(Object element, String property) {
				MyModel e = (MyModel) element;
				if (property.equals("firstName")) {
					return e.getFirstName();
				} else if (property.equals("lastName")) {
					return e.getLastName();
				}
				return e.getBirthDate();
			}

			/**
			 * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object,
			 *      java.lang.String, java.lang.Object)
			 */
			public void modify(Object element, String property, Object value) {
				TableItem item = (TableItem) element;
				MyModel e = (MyModel) item.getData();
				if (property.equals("firstName")) {
					e.firstName = (String) value;
				} else if (property.equals("lastName")) {
					e.lastName = (String) value;
				} else {
					e.birthDate = (Date) value;
				}
				v.update(item.getData(), null);
			}

		});

		v.setColumnProperties(new String[] { "firstName", "lastName", "birthDate" });
		v.setCellEditors(new CellEditor[] { new TextCellEditor(v.getTable()), new TextCellEditor(v.getTable()),
				new CDateTimeCellEditor(v.getTable(), CDT.BORDER | CDT.SPINNER) });
		TableViewerEditor.create(v, new ColumnViewerEditorActivationStrategy(v), ColumnViewerEditor.TABBING_HORIZONTAL
				| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | ColumnViewerEditor.TABBING_VERTICAL);

		MyModel[] model = createModel();
		v.setInput(model);
		v.getTable().setLinesVisible(true);
	}

	private MyModel[] createModel() {
		MyModel[] elements = new MyModel[10];

		for (int i = 0; i < 10; i++) {
			elements[i] = new MyModel("First name #" + i, "Last Name #" + i, new Date());
		}

		return elements;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		new CDTEditorSample(shell);
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		display.dispose();

	}

}
