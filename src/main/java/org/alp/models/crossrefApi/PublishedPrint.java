package org.alp.models.crossrefApi;

import com.google.gson.annotations.SerializedName;

public class PublishedPrint {

	@SerializedName("date-parts")
	Integer[] dateParts;

	public PublishedPrint(Integer[] dateParts) {
		this.dateParts = dateParts;
	}

	public Integer[] getDateParts() {
		return dateParts;
	}

	public void setDateParts(Integer[] dateParts) {
		this.dateParts = dateParts;
	}

	public Integer getYear() {
		if(dateParts != null && dateParts.length > 0)
			return dateParts[0];
		return null;
	}

	public Integer getMonth() {
		if(dateParts != null && dateParts.length > 1)
			return dateParts[1];
		return null;
	}
	public Integer getDay() {
		if(dateParts != null && dateParts.length > 2)
			return dateParts[1];
		return null;
	}
}
