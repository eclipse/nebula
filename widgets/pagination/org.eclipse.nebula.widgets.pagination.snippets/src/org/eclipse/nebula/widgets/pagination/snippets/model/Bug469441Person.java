package org.eclipse.nebula.widgets.pagination.snippets.model;

public class Bug469441Person {
	private final String firstName;
	private final String lastName;

	public Bug469441Person(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getFirstName() {
		return firstName;
	}
}
