package org.eclipse.nebula.snippets.datechooser;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.nebula.widgets.datechooser.DateChooser;
import org.eclipse.nebula.widgets.datechooser.DateChooserObservableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Snippet for DateChooser : DataBinding
 */
public class DateChooserDataBindingSnippet {
	static class Person {
		String name;
		Date birthday;
		private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

		public String getName() {
			return name;
		}
		public void setName(String name) {
			propertyChangeSupport.firePropertyChange("name", this.name, this.name = name);
		}
		public Date getBirthday() {
			return birthday;
		}
		public void setBirthday(Date birthday) {
			propertyChangeSupport.firePropertyChange("birthday", this.birthday, this.birthday = birthday);
		}

		public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
			propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
		}

		public void removePropertyChangeListener(PropertyChangeListener listener) {
			propertyChangeSupport.removePropertyChangeListener(listener);
		}
	}

	public static void main(String[] args) {
		final Display display = Display.getDefault();
		Realm.runWithDefault(SWTObservables.getRealm(display), 
			new Runnable() {
				public void run() {
					DateChooserDataBindingSnippet snippet = new DateChooserDataBindingSnippet();
					Shell shell = snippet.createShell();
			    shell.open();
			    while ( ! shell.isDisposed() ) {
			    	if (!display.readAndDispatch()) display.sleep();
			    }
				}
		});
	}

	private Shell createShell() {
		Display display = Display.getDefault();
    Shell shell = new Shell(display);
    shell.setSize(300, 300);
    GridLayoutFactory.swtDefaults().numColumns(2).applyTo(shell);

    Calendar cal = Calendar.getInstance();
    cal.set(1950, 3, 20, 0, 0, 0);
    final Person person = new Person();
    person.setName("Bugs Bunny");
    person.setBirthday(cal.getTime());

    new Label(shell, SWT.NONE).setText("Name:");
    final Text text = new Text(shell, SWT.BORDER);
    GridDataFactory.swtDefaults().hint(200, SWT.DEFAULT).applyTo(text);

    new Label(shell, SWT.NONE).setText("Birthday:");
    final DateChooser dateChooser = new DateChooser(shell, SWT.BORDER);
    GridDataFactory.swtDefaults().applyTo(dateChooser);

    Button bunnyButton = new Button(shell, SWT.NONE);
    bunnyButton.setText("Reset model");
    GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).span(2, 1)
    		.hint(100, SWT.DEFAULT).applyTo(bunnyButton);
    bunnyButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
		    Calendar cal = Calendar.getInstance();
		    cal.set(1950, 3, 20, 0, 0, 0);
		    person.setName("Bugs Bunny");
		    person.setBirthday(cal.getTime());
			}
    });

    Button modelButton = new Button(shell, SWT.NONE);
    modelButton.setText("Display model");
    GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).span(2, 1)
    		.hint(100, SWT.DEFAULT).applyTo(modelButton);

    final Label model = new Label(shell, SWT.NONE);
    GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).span(2, 1).applyTo(model);

    modelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				model.setText(person.getName() + " - " + person.getBirthday());
			}
    });

    DataBindingContext context = new DataBindingContext();
    context.bindValue(SWTObservables.observeText(text, SWT.Modify),
    		BeansObservables.observeValue(person, "name"), null, null);
    context.bindValue(new DateChooserObservableValue(dateChooser, SWT.Selection),
    		BeansObservables.observeValue(person, "birthday"), null, null);

    return shell;
	}
}
