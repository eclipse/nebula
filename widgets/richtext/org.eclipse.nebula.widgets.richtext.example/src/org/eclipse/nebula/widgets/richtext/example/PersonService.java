package org.eclipse.nebula.widgets.richtext.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.eclipse.nebula.widgets.richtext.example.Person.Gender;

public class PersonService {

	private PersonService() {
		// empty default constructor
	}

	public static List<Person> getPersons(int numberOfPersons) {
		List<Person> result = new ArrayList<Person>();

		for (int i = 0; i < numberOfPersons; i++) {
			result.add(createPerson());
		}

		return result;
	}

	public static Person createPerson() {
		String[] maleNames = { "Bart", "Homer", "Lenny", "Carl", "Waylon", "Ned", "Timothy" };
		String[] femaleNames = { "Marge", "Lisa", "Maggie", "Edna", "Helen", "Jessica" };
		String[] lastNames = { "Simpson", "Leonard", "Carlson", "Smithers", "Flanders", "Krabappel", "Lovejoy" };

		Random randomGenerator = new Random();

		Person result = new Person();
		result.setGender(Gender.values()[randomGenerator.nextInt(2)]);

		if (result.getGender().equals(Gender.MALE)) {
			result.setFirstName(maleNames[randomGenerator.nextInt(maleNames.length)]);
		} else {
			result.setFirstName(femaleNames[randomGenerator.nextInt(femaleNames.length)]);
		}

		result.setLastName(lastNames[randomGenerator.nextInt(lastNames.length)]);
		result.setMarried(randomGenerator.nextBoolean());

		return result;
	}

}
