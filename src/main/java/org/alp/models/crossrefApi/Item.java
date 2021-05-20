package org.alp.models.crossrefApi;

import com.google.gson.annotations.SerializedName;

public class Item {

	@SerializedName("reference-count")
	private int referenceCount;

	@SerializedName("DOI")
	private String doi;

	@SerializedName("is-referenced-by-count")
	private String isReferencedByCount;

	private String[] title;

	@SerializedName("container-title")
	private String[] containerTitle;

	@SerializedName("author")
	private Author[] authors;

	private float score;

	@SerializedName("reference")
	private Reference[] references;

	public Item(int referenceCount, String doi, String isReferencedByCount, String[] title, String[] containerTitle, Author[] authors, float score, Reference[] references) {
		this.referenceCount = referenceCount;
		this.doi = doi;
		this.isReferencedByCount = isReferencedByCount;
		this.title = title;
		this.containerTitle = containerTitle;
		this.authors = authors;
		this.score = score;
		this.references = references;
	}

	public int getReferenceCount() {
		return referenceCount;
	}

	public void setReferenceCount(int referenceCount) {
		this.referenceCount = referenceCount;
	}

	public String getDoi() {
		return doi;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}

	public String getIsReferencedByCount() {
		return isReferencedByCount;
	}

	public void setIsReferencedByCount(String isReferencedByCount) {
		this.isReferencedByCount = isReferencedByCount;
	}

	public String[] getTitle() {
		return title;
	}

	public void setTitle(String[] title) {
		this.title = title;
	}

	public String[] getContainerTitle() {
		return containerTitle;
	}

	public void setContainerTitle(String[] containerTitle) {
		this.containerTitle = containerTitle;
	}

	public Author[] getAuthors() {
		return authors;
	}

	public void setAuthors(Author[] authors) {
		this.authors = authors;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public Reference[] getReferences() {
		return references;
	}

	public void setReferences(Reference[] references) {
		this.references = references;
	}
}
