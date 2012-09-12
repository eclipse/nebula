package org.eclipse.nebula.widgets.pagination.snippets.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Team model.
 * 
 */
public class Team {

	private final String name;
	private final List<Person> persons;

	public Team(String name) {
		this.name = name;
		this.persons = new ArrayList<Person>();
	}

	public String getName() {
		return name;
	}

	public List<Person> getPersons() {
		return persons;
	}

	public void addPerson(Person person) {
		this.persons.add(person);
	}
}
