package org.alp.components.controllers;

public class PaperTableElement {
	private String title;
	private String author;
	private String doi;

	public PaperTableElement(String title, String author, String doi) {
		this.title = title;
		this.author = author;
		this.doi = doi;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDoi() {
		return doi;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}
}
