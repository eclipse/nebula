package org.eclipse.nebula.widgets.ctreecombo.snippets;

import java.util.Arrays;
import java.util.List;

public class Country {
	private String name;
	private List<String> commiters;

	public Country(String name, String...commiters) {
		this.name = name;
		this.commiters=Arrays.asList(commiters);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getCommiters() {
		return commiters;
	}

	public void setCommiters(List<String> commiters) {
		this.commiters = commiters;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((commiters == null) ? 0 : commiters.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Country other = (Country) obj;
		if (commiters == null) {
			if (other.commiters != null)
				return false;
		} else if (!commiters.equals(other.commiters))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Country [name=" + name + ", commiters=" + commiters + "]";
	}

}
