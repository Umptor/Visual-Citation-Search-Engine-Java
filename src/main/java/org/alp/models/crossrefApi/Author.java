package org.alp.models.crossrefApi;

public class Author {
	private String given;
	private String family;
	private String sequence;

	public Author(String given, String family, String sequence) {
		this.given = given;
		this.family = family;
		this.sequence = sequence;
	}

	public String getGiven() {
		return given;
	}

	public void setGiven(String given) {
		this.given = given;
	}

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public String getFullname() {
		String givenReturn = this.given;
		String givenFamily = this.family;
		return this.family + ", " + this.given;
	}
}
