package org.alp.models.OpenCitationApi;

import com.google.gson.annotations.SerializedName;

public class GetMetadataResponseOpenCitation {

	@SerializedName("year")
	private String year;

	@SerializedName("reference")
	private String reference;

	@SerializedName("citation_count")
	private String citationCount;

	@SerializedName("source_id")
	private String sourceId;

	@SerializedName("author")
	private String author;

	@SerializedName("oa_link")
	private String oaLink;

	@SerializedName("volume")
	private String volume;

	@SerializedName("issue")
	private String issue;

	@SerializedName("citation")
	private String citation;

	@SerializedName("doi")
	private String doi;

	@SerializedName("title")
	private String title;

	@SerializedName("page")
	private String page;

	@SerializedName("source_title")
	private String sourceTitle;

	public GetMetadataResponseOpenCitation() { }

	public GetMetadataResponseOpenCitation(String year, String reference, String citationCount, String sourceId, String author, String oaLink, String volume, String issue, String citation, String doi, String title, String page, String sourceTitle) {
		this.year = year;
		this.reference = reference;
		this.citationCount = citationCount;
		this.sourceId = sourceId;
		this.author = author;
		this.oaLink = oaLink;
		this.volume = volume;
		this.issue = issue;
		this.citation = citation;
		this.doi = doi;
		this.title = title;
		this.page = page;
		this.sourceTitle = sourceTitle;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getCitationCount() {
		return citationCount;
	}

	public void setCitationCount(String citationCount) {
		this.citationCount = citationCount;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getOaLink() {
		return oaLink;
	}

	public void setOaLink(String oaLink) {
		this.oaLink = oaLink;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public String getIssue() {
		return issue;
	}

	public void setIssue(String issue) {
		this.issue = issue;
	}

	public String getCitation() {
		return citation;
	}

	public void setCitation(String citation) {
		this.citation = citation;
	}

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

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getSourceTitle() {
		return sourceTitle;
	}

	public void setSourceTitle(String sourceTitle) {
		this.sourceTitle = sourceTitle;
	}
}
