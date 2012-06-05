package org.eclipse.nebula.widgets.pagination.example.services;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.pagination.IPageLoader;
import org.eclipse.nebula.widgets.pagination.PageableController;
import org.eclipse.nebula.widgets.pagination.collections.PageListHelper;
import org.eclipse.nebula.widgets.pagination.collections.PageResult;
import org.eclipse.nebula.widgets.pagination.example.model.NebulaWidget;
import org.eclipse.nebula.widgets.pagination.example.model.Person;

public class NebulaWidgetServices implements
		IPageLoader<PageResult<NebulaWidget>> {

	private static final NebulaWidgetServices INSTANCE = new NebulaWidgetServices();

	private final List<NebulaWidget> widgets;

	public static NebulaWidgetServices getInstance() {
		return INSTANCE;
	}

	public NebulaWidgetServices() {
		this.widgets = new ArrayList<NebulaWidget>();
		load();
	}

	//@Override
	public PageResult<NebulaWidget> loadPage(PageableController controller) {
		return PageListHelper.createPage(widgets, controller);
	}

	private void load() {
		Person tomSchindl = new Person("Tom", "Schindl");
		addWidget(
				"Grid",
				"An custom table/tree widget which provides Excel like features and a JFaceViewer integration ",
				tomSchindl);
		addWidget(
				"PShelf",
				"The PShelf widget is a composite widget that is similar to a tab folder. It contains items which can be selected to show their client areas.",
				tomSchindl);
		addWidget(
				"PGroup",
				"The PGroup widget is a expandable/collapsible composite widget with attractive styling and an extensible design.",
				tomSchindl);

		Person nicolasRicheton = new Person("Nicolas", "Richeton");
		addWidget(
				"Gallery",
				"This SWT widget displays images or items in a grid-like presentation. Gallery is very useful to display images in a photo viewer or files in an explorer-like view.",
				nicolasRicheton);

		Person eliasVolanakis = new Person("Elias", "Volanakis");
		addWidget(
				"CompositeTable",
				"An custom table/tree widget which provides Excel like features and a JFaceViewer integration ",
				eliasVolanakis);

		Person donaldDunne = new Person("Donald", "Dunne");
		addWidget(
				"XViewer",
				"The purpose of the XViewer is to give the application developer a more advanced and dynamic TreeViewer that has the filtering and sorting ",
				donaldDunne);
		addWidget(
				"CalendarCombo",
				"The Calendar Combo Widget is a combo box widget that opens a calendar when dropped down. ",
				donaldDunne);

		Person wimJongman = new Person("Wim", "Jongman");
		addWidget("Oscilloscope", "A widget showing an Oscilloscope",
				wimJongman);

		Person martyJones = new Person("Marty", "Jones");
		addWidget("TableCombo",
				"A DropDown-Widget which uses a Table to display the items ",
				martyJones);

		Person ahmedMahran = new Person("Ahmed", "Mahran ");
		addWidget("STW", "A component which allows to implement transitions ",
				ahmedMahran);

		Person emilCrumhorn = new Person("Emil", "Crumhorn");
		addWidget("Gantt", "A comprehensive implementation of a Gantt chart.",
				emilCrumhorn);

		Person jeremyDowdall = new Person("Jeremy", "Dowdall");
		addWidget("CDateTime", "A Date and Time selector widget.",
				jeremyDowdall);

		addWidget("CWT", "Nebula animation ", nicolasRicheton);

		addWidget(
				"BidiLayout",
				"The PGroup widget is a expandable/collapsible composite widget with attractive styling and an extensible design.",
				tomSchindl);

		Person mattHall = new Person("Matt", "Hall");
		addWidget("PaperClips", "Printing Library", mattHall);
		addWidget("Radio Group", "Group of Radio Buttons", emilCrumhorn);

		addWidget(
				"Collapsible Buttons",
				"Widget modeled after the bottom left buttons widget in Microsoft Outlook ",
				emilCrumhorn);

		Person ericWuillai = new Person("Eric", "Wuillai");
		addWidget("Date Chooser",
				"Presents the monthly view of a calendar for date picking.",
				ericWuillai);
		addWidget(
				"Formatted Text",
				"A decorator component adding input and display mask capabilities on a Text widget.",
				ericWuillai);

		Person lukaszMilewski = new Person("Lukasz", "Milewski");
		addWidget("Nebula Toolbar",
				"A comprehensive implementation of a Gantt chart. ",
				lukaszMilewski);

		Person mickaelIstria = new Person("Mickael", "Istria");
		addWidget("TreeMapper", "Mapping between Tree's", mickaelIstria);

		Person angeloZerr = new Person("Angelo", "Zerr");
		addWidget("Picture Control",
				"A control managing (download and display) a picture.",
				angeloZerr);
		addWidget(
				"Pagination Control",
				"A control managing paginated list and display pagination navigation bar.",
				angeloZerr);

		addWidget("CTree", "", jeremyDowdall);

		Person edwinPark = new Person("Edwin", "Park");
		addWidget(
				"NatTable",
				"NatTable is a powerful and flexible SWT table/grid widget that is built to handle very large data sets, real-time updates, dynamic styling, and more.",
				edwinPark);

	}

	private void addWidget(String name, String description, Person committer) {
		this.widgets.add(new NebulaWidget(name, description, committer));

	}

}
