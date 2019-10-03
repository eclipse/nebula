package org.eclipse.nebula.snippets.compositetable;

/*
 * Text example snippet: verify input (only allow digits)
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 */
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.compositetable.CompositeTable;
import org.eclipse.nebula.widgets.compositetable.IRowContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

@SuppressWarnings("deprecation")
public class SnippetCompositeTableDataBinding {

	/**
	 * A Model class representing a hero
	 * 
	 * @author sylvere.richard@sogeti.lu
	 * 
	 */
	private static class Hero {
		private String name;

		private String firstname;

		public Hero() {
		}

		/**
		 * copy constructor
		 * 
		 * @param hero
		 */
		public Hero(Hero hero) {
			this.firstname = hero.firstname;
			this.name = hero.name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setFirstname(String firstname) {
			this.firstname = firstname;
		}

	}

	/**
	 * A model class containing a list of persons.
	 * 
	 * @author sylvere.richard@sogeti.lu
	 * 
	 */
	private static class MyModel {
		private final PropertyChangeSupport support = new PropertyChangeSupport(this);

		/**
		 * @param listener
		 */
		public void addPropertyChangeListener(PropertyChangeListener listener) {
			support.addPropertyChangeListener(listener);
		}

		/**
		 * @param propertyName
		 * @param listener
		 */
		public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
			support.addPropertyChangeListener(propertyName, listener);
		}

		/**
		 * @param listener
		 */
		public void removePropertyChangeListener(PropertyChangeListener listener) {
			support.removePropertyChangeListener(listener);
		}

		/**
		 * @param propertyName
		 * @param listener
		 */
		public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
			support.removePropertyChangeListener(propertyName, listener);
		}

		/**
		 * 
		 * @param propertyName
		 * @param oldValue
		 * @param newValue
		 */
		protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
			support.firePropertyChange(propertyName, oldValue, newValue);
		}

		private final List<Hero> heroes = new LinkedList<Hero>();

		public List<Hero> getHeroes() {
			return heroes;
		}

		/**
		 * Returns how many heroes the model contains.
		 * 
		 * @return
		 */
		public int getHeroesListSize() {
			return getHeroes().size();
		}

		/**
		 * Adds a new hero to the list of people
		 * 
		 * @param p
		 */
		public void addHero(Hero p) {
			getHeroes().add(p);
			// notify whenever the hero list size changes
			support.firePropertyChange("heroesListSize", getHeroesListSize() - 1, getHeroesListSize());
		}

		public void removePerson(Hero person) {
			getHeroes().remove(person);
			// notify whenever the hero list size changes
			support.firePropertyChange("heroesListSize", getHeroesListSize() + 1, getHeroesListSize());
		}
	}

	/**
	 * contains size of table columns
	 */
	private static int[] cols = new int[] { 0, 30, 60, 75 };

	/**
	 * Header of my table
	 * 
	 * @author sylvere.richard@sogeti.lu
	 * 
	 */
	private static class Header extends Composite {
		public Header(Composite parent, int style) {
			super(parent, style);

			doLayout();
		}

		private void doLayout() {
			setLayout(new FormLayout());
			Label lblName = new Label(this, SWT.CENTER);
			lblName.setText("Name");

			int index = 0;
			FormData data = new FormData();
			data.left = new FormAttachment(cols[index], 100, 5);
			data.right = new FormAttachment(cols[index + 1], 100, 0);
			data.top = new FormAttachment(0, 100, 5);

			lblName.setLayoutData(data);
			index++;

			Label lblFirstname = new Label(this, SWT.CENTER);
			lblFirstname.setText("Firstname");

			data = new FormData();
			data.left = new FormAttachment(cols[index], 100, 5);
			data.right = new FormAttachment(cols[index + 1], 100, 0);
			data.top = new FormAttachment(0, 100, 5);

			lblFirstname.setLayoutData(data);
			index++;

			Label lblAction = new Label(this, SWT.CENTER);
			lblAction.setText("Action");

			data = new FormData();
			data.left = new FormAttachment(cols[index], 100, 5);
			data.right = new FormAttachment(cols[index + 1], 100, 0);
			data.top = new FormAttachment(0, 100, 5);

			lblAction.setLayoutData(data);

		}
	}

	/**
	 * A row of my table
	 * 
	 * @author sylvere.richard@sogeti.lu
	 * 
	 */
	private static class Row extends Composite {

		private DataBindingContext dbcRow;

		private Text lastname;

		private Text firstname;

		private Button action;

		public Row(Composite parent, int style) {
			super(parent, style);
			doLayout();

			addListener(SWT.Dispose, e -> {
				// in case the row is bound, we unbind it
				unbind();
			});
		}

		private void doLayout() {
			lastname = new Text(this, SWT.BORDER);
			firstname = new Text(this, SWT.BORDER);
			action = new Button(this, SWT.NONE);
			action.setText("Delete");

			setLayout(new FormLayout());

			// column index
			int index = 0;
			FormData data = new FormData();
			data.top = new FormAttachment(0, 100, 5);
			data.left = new FormAttachment(cols[index], 100, 5);
			data.right = new FormAttachment(cols[index + 1], 100, 0);
			lastname.setLayoutData(data);
			index++;

			data = new FormData();
			data.top = new FormAttachment(0, 100, 5);
			data.left = new FormAttachment(cols[index], 100, 5);
			data.right = new FormAttachment(cols[index + 1], 100, 0);
			firstname.setLayoutData(data);
			index++;

			data = new FormData();
			data.top = new FormAttachment(0, 100, 5);
			data.left = new FormAttachment(cols[index], 100, 5);
			data.right = new FormAttachment(cols[index + 1], 100, 0);
			action.setLayoutData(data);
			index++;
		}

		/**
		 * Unbind the current row. Be sure to call this method before any call
		 * to bind.
		 */
		public void unbind() {
			if (dbcRow != null) {
				dbcRow.dispose();
				dbcRow = null;
			}
		}

		/**
		 * Binds the current ro with the given Person
		 * 
		 * @param p
		 *            The Person bean to bind.
		 */
		public void bind(Hero p) {
			dbcRow = new DataBindingContext();
			Binding b = dbcRow.bindValue(SWTObservables.observeText(lastname, SWT.Modify), PojoObservables.observeValue(p, "name"), null, null);
			b = dbcRow.bindValue(SWTObservables.observeText(firstname, SWT.Modify), PojoObservables.observeValue(p, "firstname"), null, null);
		}

	}

	/**
	 * A simple dialog to create a new person.
	 * 
	 * @author sylvere.richard@sogeti.lu
	 * 
	 */
	private static final class PersonTitleAreaDialog extends TitleAreaDialog {
		private Text txName;
		private Text txFirstname;
		private final Hero hero = new Hero();

		private DataBindingContext dbc = new DataBindingContext();

		private PersonTitleAreaDialog(Shell parentShell) {
			super(parentShell);

		}

		@Override
		protected Control createContents(Composite parent) {
			Control ret = super.createContents(parent);
			setTitle("Add new Hero");
			setMessage("Enter here your favorite hero :");
			return ret;
		}

		protected Control createDialogArea(Composite parent) {
			Composite composite = (Composite) super.createDialogArea(parent);

			Label lbName = new Label(composite, SWT.NONE);
			lbName.setText("Name :");

			txName = new Text(composite, SWT.BORDER);

			Label lbFirstname = new Label(composite, SWT.NONE);
			lbFirstname.setText("Firstname :");

			txFirstname = new Text(composite, SWT.BORDER);

			GridLayoutFactory.fillDefaults().generateLayout(composite);

			// binding
			Binding b = dbc.bindValue(SWTObservables.observeText(txFirstname, SWT.FocusOut), PojoObservables.observeValue(hero, "firstname"), null, null);
			b = dbc.bindValue(SWTObservables.observeText(txName, SWT.FocusOut), PojoObservables.observeValue(hero, "name"), null, null);
			return composite;
		}

		@Override
		public int open() {
			return super.open();
		}

		@Override
		public boolean close() {
			dbc.dispose();
			return super.close();
		}

		public Hero getHero() {
			// returns a copy
			return new Hero(hero);
		}
	}

	/**
	 * This composite contains the CompositeTable and a button to add new rows
	 * 
	 * @author sylvere.richard@sogeti.lu
	 * 
	 */
	private static class MainCompo extends Composite {

		private CompositeTable table;
		private Button addNew;
		private final MyModel model = new MyModel();

		private final DataBindingContext dbc = new DataBindingContext();

		public MainCompo(Composite parent, int style) {
			super(parent, style);

			doLayout();
		}

		private void doLayout() {
			setLayout(new FormLayout());

			table = new CompositeTable(this, SWT.NULL);
			table.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
			new Header(table, SWT.NULL);
			new Row(table, SWT.NULL);

			addNew = new Button(this, SWT.NONE);
			addNew.setText("Add new Hero");
			addNew.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					PersonTitleAreaDialog d = new PersonTitleAreaDialog(getShell());
					d.open();
					final int result = d.getReturnCode();
					if (result == Window.OK) {
						Hero p = d.getHero();
						getModel().addHero(p);
					}
				}
			});

			FormData data = new FormData();
			data.left = new FormAttachment(0, 100, 5);
			data.right = new FormAttachment(100, 100, -5);
			data.top = new FormAttachment(0, 100, 5);
			data.bottom = new FormAttachment(addNew, -5, SWT.TOP);
			table.setLayoutData(data);
			table.setRunTime(true);

			data = new FormData();
			data.right = new FormAttachment(100, 100, -5);
			data.bottom = new FormAttachment(100, 100, -5);
			addNew.setLayoutData(data);
		}

		/**
		 * In this method, we bind the heroes list size with the
		 * compositetable's property 'numRowsInCollection'.
		 * 
		 */
		public void binding() {

			// table.setNumRowsInCollection(model.getPeople().size());
			Binding b = dbc.bindValue(PojoObservables.observeValue(table, "numRowsInCollection"), BeansObservables.observeValue(model, "heroesListSize"), new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER), new UpdateValueStrategy());

			table.addRowContentProvider(new IRowContentProvider() {

				/**
				 * Keeps a reference of each delete listener for each row, so we can
				 * remove a listener when compositetable associate the row to another hero.
				 */
				private Map<Row, SelectionListener> map = new HashMap<Row, SelectionListener>();

				/**
				 * * Since the compositetable has its own pool of Row elements,
				 * we must first ensure to unbind if necessary the row before
				 * rebinding it with the Hero given as parameter.
				 * 
				 * In the same way, we remove and add selectionlistener.
				 */
				public void refresh(CompositeTable sender, int currentObjectOffset, Control rowControl) {
					Row row = (Row) rowControl;
					final Hero p = model.getHeroes().get(currentObjectOffset);

					// unbind previous hero
					row.unbind();

					// remove previous listener if nay
					if (map.get(row) != null) {
						row.action.removeSelectionListener(map.get(row));
					}

					// bind the new hero
					row.bind(p);

					// add selectionlistener
					SelectionListener listen = new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							getModel().removePerson(p);
						}
					};
					row.action.addSelectionListener(listen);
					// keep a reference to this listener to be able to remove it
					// during the next refresh
					map.put(row, listen);
				}
			});
		}

		public MyModel getModel() {
			return model;
		}
	}

	/**
	 * Main entry point.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		final Display display = new Display();

		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
			public void run() {
				Shell shell = new Shell(display);
				shell.setText("Jules Verne's Heroes");
				shell.setBounds(0, 0, 400, 200);
				shell.setLayout(new FormLayout());

				// creation of the composite containing the table
				MainCompo compo = new MainCompo(shell, SWT.NONE);

				// position it on the shell
				FormData data = new FormData();
				data.left = new FormAttachment(0, 100, 5);
				data.right = new FormAttachment(100, 100, -5);
				data.top = new FormAttachment(0, 100, 5);
				data.bottom = new FormAttachment(100, 100, -5);
				compo.setLayoutData(data);

				// add some data to the model
				populateModel(compo.getModel());

				// binding the UI with the model
				compo.binding();

				// start the UI
				shell.open();

				while (!shell.isDisposed()) {
					if (!display.readAndDispatch())
						display.sleep();
				}
			}
		});
		display.dispose();
	}

	/**
	 * populates the given model with some data
	 * 
	 * @param md
	 */
	private static void populateModel(MyModel md) {
		Hero p = new Hero();
		p.setName("Fogg");
		p.setFirstname("Phileas");
		md.getHeroes().add(p);

		p = new Hero();
		p.setName("Aronnax");
		p.setFirstname("Pierre");
		md.getHeroes().add(p);

		p = new Hero();
		p.setName("Strogoff");
		p.setFirstname("Michel");
		md.getHeroes().add(p);

		p = new Hero();
		p.setName("Nemo");
		p.setFirstname("Captain");
		md.getHeroes().add(p);

		p = new Hero();
		p.setName("Barbicane");
		p.setFirstname("Impey");
		md.getHeroes().add(p);
	}
}
