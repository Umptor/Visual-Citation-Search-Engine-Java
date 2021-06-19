package org.alp.models.crossrefApi;

import com.google.gson.annotations.SerializedName;

public class ReferenceCrossRef {
	private String key;

	@SerializedName("DOI")
	private String doi;

	public ReferenceCrossRef(String key, String doi) {
		this.key = key;
		this.doi = doi;
	}

	public ReferenceCrossRef(String doi) {
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
