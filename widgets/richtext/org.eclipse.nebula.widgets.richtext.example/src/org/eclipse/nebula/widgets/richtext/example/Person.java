package org.eclipse.nebula.widgets.richtext.example;


public class Person {
	public enum Gender {
		MALE, FEMALE
	}

	private String firstName;
	private String lastName;
	private Person.Gender gender;
	private boolean married;
	private String description;

	public Person() {
	}

	public Person(String firstName, String lastName, Person.Gender gender, boolean married) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.gender = gender;
		this.married = married;
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

	public Person.Gender getGender() {
		return gender;
	}

	public void setGender(Person.Gender gender) {
		this.gender = gender;
	}

	public boolean isMarried() {
		return married;
	}

	public void setMarried(boolean married) {
		this.married = married;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}