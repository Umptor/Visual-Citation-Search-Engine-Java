package org.alp.components.controllers;

public class PaperTableElement {
	private String title;
	private String author;
	private String doi;
	private Integer referenceCount;


	public PaperTableElement(String title, String author, String doi, int referenceCount) {
		this.title = title;
		this.author = author;
		this.doi = doi;
		this.referenceCount = referenceCount;
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

	public Integer getReferenceCount() {
		return referenceCount;
	}

	public void setReferenceCount(Integer referenceCount) {
		this.referenceCount = referenceCount;
	}
}
