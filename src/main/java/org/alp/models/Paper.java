package org.alp.models;

import org.alp.models.crossrefApi.Author;

import java.util.ArrayList;

public class Paper {

	private String doi;

	private String title;

	private Author[] authors;

	private ArrayList<Paper> references;

	private String paperAbstract;

	public Paper(String doi, String title, Author[] authors, ArrayList<Paper> references, String paperAbstract) {
		this.doi = doi;
		this.title = title;
		this.authors = authors;
		this.references = references;
		this.paperAbstract = paperAbstract;
		this.formatAbstract();
	}

	public Paper() {}

	public String getDoi() {
		return doi;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Author[] getAuthors() {
		return authors;
	}

	public void setAuthors(Author[] authors) {
		this.authors = authors;
	}

	public ArrayList<Paper> getReferences() {
		return references;
	}

	public void setReferences(ArrayList<Paper> references) {
		this.references = references;
	}

	public String getPaperAbstract() {
		return paperAbstract;
	}

	public void setPaperAbstract(String paperAbstract) {
		this.paperAbstract = paperAbstract;
	}

	private void formatAbstract() {
		if(this.paperAbstract != null) {
			this.paperAbstract = this.paperAbstract.replace("<jats:p>", "");
			this.paperAbstract = this.paperAbstract.replace("</jats:p>", "");
		}
	}
}
