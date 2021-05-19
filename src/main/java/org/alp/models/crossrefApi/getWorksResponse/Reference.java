package org.alp.models.crossrefApi.getWorksResponse;

import com.google.gson.annotations.SerializedName;

public class Reference {
	private String key;

	@SerializedName("DOI")
	private String doi;

	public Reference(String key, String doi) {
		this.key = key;
		this.doi = doi;
	}

	public Reference(String doi) {
		this.doi = doi;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getDoi() {
		return doi;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}
}
