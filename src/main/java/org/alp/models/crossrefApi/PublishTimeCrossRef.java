package org.alp.models.crossrefApi;

import com.google.gson.annotations.SerializedName;

public class PublishTimeCrossRef {

	public static Integer DEFAULT_MONTH = 6;
	public static Integer DEFAULT_DAY = 15;

	@SerializedName("date-parts")
	Integer[][] dateParts;

	public PublishTimeCrossRef(Integer[][] dateParts) {
		this.dateParts = dateParts;
		formatDate(this);
	}

	public Integer[][] getDateParts() {
		return dateParts;
	}

	public void setDateParts(Integer[][] dateParts) {
		this.dateParts = dateParts;
	}

	public void setYear(Integer year) {
		if(dateParts != null && dateParts.length > 0)
			dateParts[0][0] = year;
	}

	public void setMonth(Integer month) {
		if(dateParts != null && dateParts.length > 1)
			dateParts[0][1] = month;
	}
	public void setDay(Integer day) {
		if(dateParts != null && dateParts.length > 2)
			dateParts[0][1] = day;
	}

	/**
	 * Do Not Use, Only for Paper Class
	 * */
	public Integer getYear() {
		if(dateParts != null && dateParts.length > 0)
			return dateParts[0][0];
		return null;
	}
	/**
	 * Do Not Use, Only for Paper Class
	 * */
	public Integer getMonth() {
		if(dateParts != null && dateParts.length > 1)
			return dateParts[0][1];
		return null;
	}

	/**
	 * Do Not Use, Only for Paper Class
	 * */
	public Integer getDay() {
		if(dateParts != null && dateParts.length > 2)
			return dateParts[0][1];
		return null;
	}

	private static void formatDate(PublishTimeCrossRef publishTime) {
		Integer year = publishTime.getYear();
		Integer month = publishTime.getMonth();
		Integer day = publishTime.getDay();

		if(month == null) month = DEFAULT_MONTH;
		if(day == null) day = DEFAULT_DAY;

		publishTime.setDateParts(new Integer[][]{{year, month, day}});
	}
}
