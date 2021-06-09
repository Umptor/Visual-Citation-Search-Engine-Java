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

	@SerializedName("abstract")
	private String paperAbstract;

	@SerializedName("published-print")
	private PublishTime publishedPrint;

	@SerializedName("published-online")
	private PublishTime publishedOnline;

	@SerializedName("journal-title")
	private String journalTitle;

	@SerializedName("unstructured")
	private String unstructured;

	public Item(int referenceCount, String doi, String isReferencedByCount, String[] title, String[] containerTitle,
	            Author[] authors, float score, Reference[] references, String paperAbstract, PublishTime publishedPrint, PublishTime publishedOnline, String journalTitle, String unstructured) {
		this.referenceCount = referenceCount;
		this.doi = doi;
		this.isReferencedByCount = isReferencedByCount;
		this.title = title;
		this.containerTitle = containerTitle;
		this.authors = authors;
		this.score = score;
		this.references = references;
		this.paperAbstract = paperAbstract;
		this.publishedPrint = publishedPrint;
		this.publishedOnline = publishedOnline;
		this.journalTitle = journalTitle;
		this.unstructured = unstructured;
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

	public String getPaperAbstract() {
		return paperAbstract;
	}

	public void setPaperAbstract(String paperAbstract) {
		this.paperAbstract = paperAbstract;
	}

	public PublishTime getPublishedPrint() {
		return publishedPrint;
	}

	public void setPublishedPrint(PublishTime publishTime) {
		this.publishedPrint = publishTime;
	}

	public PublishTime getPublishedOnline() {
		return publishedOnline;
	}

	public void setPublishedOnline(PublishTime publishedOnline) {
		this.publishedOnline = publishedOnline;
	}

	public String getJournalTitle() {
		return journalTitle;
	}

	public void setJournalTitle(String journalTitle) {
		this.journalTitle = journalTitle;
	}

	public String getUnstructured() {
		return unstructured;
	}

	public void setUnstructured(String unstructured) {
		this.unstructured = unstructured;
	}

	/* Logic Starts Here */
	public Integer getYear() {
		Integer printYear = this.getPublishedPrint().getYear();
		Integer onlineYear = this.getPublishedOnline().getYear();
		return returnMinDate(printYear, onlineYear);
	}

	public Integer getMonth() {
		Integer printMonth = this.getPublishedOnline().getYear();
		Integer onlineMonth = this.getPublishedPrint().getYear();
		return returnMinDate(printMonth, onlineMonth);
	}

	public Integer getDay() {
		Integer printDay = this.getPublishedPrint().getYear();
		Integer onlineDay = this.getPublishedOnline().getYear();
		return returnMinDate(printDay, onlineDay);
	}

	private Integer returnMinDate(Integer printDate, Integer onlineDate) {

		if(printDate != null) {
			if(onlineDate != null) {
				return Math.min(printDate, onlineDate);
			}
			return printDate;
		}
		return onlineDate;
	}
}
