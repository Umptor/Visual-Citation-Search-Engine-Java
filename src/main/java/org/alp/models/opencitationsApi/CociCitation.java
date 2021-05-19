package org.alp.models.opencitationsApi;

public class CociCitation {
	private String oci;
	private String citing;
	private String cited;

	public CociCitation(String oci, String citing, String cited) {
		this.oci = oci;
		this.citing = citing;
		this.cited = cited;
	}

	public String getOci() {
		return oci;
	}

	public void setOci(String oci) {
		this.oci = oci;
	}

	public String getCiting() {
		return citing;
	}

	public void setCiting(String citing) {
		this.citing = citing;
	}

	public String getCited() {
		return cited;
	}

	public void setCited(String cited) {
		this.cited = cited;
	}
}
