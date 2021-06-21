package org.alp.models;

import org.alp.models.crossrefApi.AuthorCrossRef;
import org.alp.models.crossrefApi.PublishTimeCrossRef;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Paper implements Comparable<Paper> {

	private String doi;

	private String title;

	private AuthorCrossRef[] authors;

	private ArrayList<Paper> references;

	private String paperAbstract;

	private PublishTimeCrossRef publishedPrint;
	private PublishTimeCrossRef publishedOnline;

	private Integer year;
	private Integer month;
	private Integer day;

	private Float x;
	private Float y;
	private Float z;

	public Paper(String doi, String title, AuthorCrossRef[] authors, ArrayList<Paper> references, String paperAbstract,
	             PublishTimeCrossRef publishedPrint, PublishTimeCrossRef publishedOnline) {
		this.doi = doi;
		this.title = this.formatTitle(title);
		this.authors = authors;
		this.references = references;
		this.paperAbstract = paperAbstract;
		this.publishedOnline = publishedOnline;
		this.formatAbstract();
		this.publishedPrint = publishedPrint;
	}

	public Paper(String doi) {
		this.doi = doi;
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

	public AuthorCrossRef[] getAuthors() {
		return authors;
	}

	public void setAuthors(AuthorCrossRef[] authors) {
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
		this.formatAbstract();
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

	public PublishTimeCrossRef getPublishedPrint() {
		return publishedPrint;
	}

	public void setPublishedPrint(PublishTimeCrossRef publishTime) {
		this.publishedPrint = publishTime;
	}

	public PublishTimeCrossRef getPublishedOnline() {
		return publishedOnline;
	}

	public void setPublishedOnline(PublishTimeCrossRef publishedOnline) {
		this.publishedOnline = publishedOnline;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	/* Fix references methods */
	public void fixReferences(boolean removeReferencesWithoutDates) {
		if(this.references == null) return;

		this.doBackwardsReferences();
		this.removeEmpties(removeReferencesWithoutDates);
	}

	private void doBackwardsReferences() {
		this.references.forEach(reference -> {
			if(reference.getReferences() == null)
				reference.setReferences(new ArrayList<>());
			reference.getReferences().add(this);
		});
	}

	public void removeEmpties(boolean removeReferencesWithoutDates) {
		this.references = this.references.stream()
				.filter(reference -> reference.getDoi() != null && !reference.getDoi().equals("") &&
						(reference.getYear() != null || !removeReferencesWithoutDates))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	/* Logic Starts Here */
	public Integer getYear() {
		if(this.year != null) return this.year;
		Integer printYear = publishedPrint  == null ? null : publishedPrint.getYear();
		Integer onlineYear = publishedOnline == null ? null : publishedOnline.getYear();
		this.year = returnMinDate(printYear, onlineYear);
		return this.year;
	}

	public Integer getMonth() {
		if(this.month != null) return this.month;
		Integer printMonth = publishedPrint  == null ? null : publishedPrint.getMonth();
		Integer onlineMonth = publishedOnline == null ? null : publishedOnline.getMonth();
		Integer month = returnMinDate(printMonth, onlineMonth);
		this.month = month == null ? PublishTimeCrossRef.DEFAULT_MONTH : null;
		return this.month;
	}

	public Integer getDay() {
		if(this.day != null) return this.day;
		Integer printDay = publishedPrint  == null ? null : publishedPrint.getDay();
		Integer onlineDay = publishedOnline == null ? null : publishedOnline.getDay();
		Integer day = returnMinDate(printDay, onlineDay);
		this.day = day == null ? PublishTimeCrossRef.DEFAULT_DAY : null;
		return this.day;
	}

	public LocalDate getDate() {
		return LocalDate.of(
				this.getYear(),
				this.getMonth(),
				this.getDay()
		);
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
//		if(paper == null) return -1;
		if(!paper.getYear().equals(this.getYear())) return Integer.compare(this.getYear(), paper.getYear());
		if(!this.getMonth().equals(paper.getMonth())) return Integer.compare(this.getMonth(), paper.getMonth());
		return Integer.compare(this.getDay(), paper.getDay());
	}

	@Override
	public boolean equals(Object obj) {
		if((obj instanceof Paper)) {
			Paper another = (Paper) obj;
			return this.doi.equals(another.getDoi());
		}

		if(obj instanceof String) {
			String doi = (String) obj;
			return this.doi.equals(doi);
		}

		return false;
	}

	private String formatTitle(String title) {
		title = title.toLowerCase();
		String[] parts = title.split(" ");
		StringBuilder pascalCaseString = new StringBuilder();

		for(String part : parts) {
			for(int i = 0; i < part.length(); i++) {
				if(i == 0) pascalCaseString.append(toUpperCase("" + part.charAt(i)));
				else pascalCaseString.append(part.charAt(i));
			}
			pascalCaseString.append(" ");
		}


		return pascalCaseString.toString();
	}

	private String toUpperCase(String s) {
		return s.toUpperCase();
	}
}
