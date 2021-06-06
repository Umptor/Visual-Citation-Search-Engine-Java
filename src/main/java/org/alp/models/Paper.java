package org.alp.models;

import org.alp.models.crossrefApi.Author;
import org.alp.models.crossrefApi.PublishTime;

import java.util.ArrayList;

public class Paper implements Comparable<Paper> {

	private String doi;

	private String title;

	private Author[] authors;

	private ArrayList<Paper> references;

	private String paperAbstract;

	private PublishTime publishedPrint;
	private PublishTime publishedOnline;

	private Float x;
	private Float y;
	private Float z;

	public Paper(String doi, String title, Author[] authors, ArrayList<Paper> references, String paperAbstract,
	             PublishTime publishedPrint, PublishTime publishedOnline) {
		this.doi = doi;
		this.title = title;
		this.authors = authors;
		this.references = references;
		this.paperAbstract = paperAbstract;
		this.publishedOnline = publishedOnline;
		this.formatAbstract();
		this.publishedPrint = publishedPrint;
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

	public Float getX() {
		return x;
	}

	public void setX(Float x) {
		this.x = x;
	}

	public Float getY() {
		return y;
	}

	public void setY(Float y) {
		this.y = y;
	}

	public Float getZ() {
		return z;
	}

	public void setZ(Float z) {
		this.z = z;
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

	/* Logic Starts Here */
	public Integer getYear() {
		Integer printYear = publishedPrint  == null ? null : publishedPrint.getYear();
		Integer onlineYear = publishedOnline == null ? null : publishedOnline.getYear();
		return returnMinDate(printYear, onlineYear);
	}

	public Integer getMonth() {
		Integer printMonth = publishedPrint  == null ? null : publishedPrint.getMonth();
		Integer onlineMonth = publishedOnline == null ? null : publishedOnline.getMonth();
		Integer month = returnMinDate(printMonth, onlineMonth);
		return month == null ? PublishTime.DEFAULT_MONTH : null;
	}

	public Integer getDay() {
		Integer printDay = publishedPrint  == null ? null : publishedPrint.getDay();
		Integer onlineDay = publishedOnline == null ? null : publishedOnline.getDay();
		Integer day = returnMinDate(printDay, onlineDay);
		return day == null ? PublishTime.DEFAULT_DAY : null;
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

	@Override
	public int compareTo(Paper paper) {
		if(paper == null) return -1;
		if(!paper.getYear().equals(this.getYear())) return Integer.compare(this.getYear(), paper.getYear());
		if(!this.getMonth().equals(paper.getMonth())) return Integer.compare(this.getMonth(), paper.getMonth());
		return Integer.compare(this.getDay(), paper.getDay());
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Paper)) return false;

		Paper another = (Paper) obj;
		return this.doi.equals(another.getDoi());
	}
}
