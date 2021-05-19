package org.alp.models;

public class Paper {
	private String DOI;
	private String[] ISSN;
	private String URL;
	private String title;
	private String[] containerTitle;
	private Author author;
	private String[] subject;
	private Reference reference;

	public Paper(String DOI, String[] ISSN, String URL, String title, String[] containerTitle, Author author, String[] subject, Reference reference) {
		this.DOI = DOI;
		this.ISSN = ISSN;
		this.URL = URL;
		this.title = title;
		this.containerTitle = containerTitle;
		this.author = author;
		this.subject = subject;
		this.reference = reference;
	}

	public String getDOI() {
		return DOI;
	}

	public void setDOI(String DOI) {
		this.DOI = DOI;
	}

	public String[] getISSN() {
		return ISSN;
	}

	public void setISSN(String[] ISSN) {
		this.ISSN = ISSN;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String URL) {
		this.URL = URL;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String[] getContainerTitle() {
		return containerTitle;
	}

	public void setContainerTitle(String[] containerTitle) {
		this.containerTitle = containerTitle;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public String[] getSubject() {
		return subject;
	}

	public void setSubject(String[] subject) {
		this.subject = subject;
	}

	public Reference getReference() {
		return reference;
	}

	public void setReference(Reference reference) {
		this.reference = reference;
	}
}
