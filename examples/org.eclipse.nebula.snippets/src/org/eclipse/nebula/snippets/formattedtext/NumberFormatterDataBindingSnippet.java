package org.eclipse.nebula.snippets.formattedtext;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.nebula.widgets.formattedtext.FormattedTextObservableValue;
import org.eclipse.nebula.widgets.formattedtext.IntegerFormatter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Snippet for NumberFormatter : DataBinding
 */
public class NumberFormatterDataBindingSnippet {
	static class Person {
		String name;
		int age;
		private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

		public String getName() {
			return name;
		}
		public void setName(String name) {
			propertyChangeSupport.firePropertyChange("name", this.name, this.name = name);
		}
		public int getAge() {
			return age;
		}
		public void setAge(int age) {
			propertyChangeSupport.firePropertyChange("age", this.age, this.age = age);
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
					NumberFormatterDataBindingSnippet snippet = new NumberFormatterDataBindingSnippet();
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
    shell.setSize(300, 200);
    GridLayoutFactory.swtDefaults().numColumns(2).applyTo(shell);

    final Person person = new Person();
    person.setName("Bugs Bunny");
    person.setAge(50);

    new Label(shell, SWT.NONE).setText("Name:");
    final Text nameField = new Text(shell, SWT.BORDER);
    GridDataFactory.swtDefaults().hint(200, SWT.DEFAULT).applyTo(nameField);

    new Label(shell, SWT.NONE).setText("Age:");
    final FormattedText ageField = new FormattedText(shell, SWT.BORDER | SWT.RIGHT);
    ageField.setFormatter(new IntegerFormatter("##0"));
    GridDataFactory.swtDefaults().hint(100, SWT.DEFAULT).applyTo(ageField.getControl());

    Button bunnyButton = new Button(shell, SWT.NONE);
    bunnyButton.setText("Reset model");
    GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).span(2, 1)
    		.hint(100, SWT.DEFAULT).applyTo(bunnyButton);
    bunnyButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
		    person.setName("Bugs Bunny");
		    person.setAge(50);
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
				model.setText(person.getName() + " - " + person.getAge());
			}
    });

    DataBindingContext context = new DataBindingContext();
    context.bindValue(SWTObservables.observeText(nameField, SWT.Modify),
    		BeansObservables.observeValue(person, "name"), null, null);
    context.bindValue(new FormattedTextObservableValue(ageField, SWT.Modify),
    		BeansObservables.observeValue(person, "age"), null, null);

    return shell;
	}
}
