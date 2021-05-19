package org.alp.models.opencitationsApi;

public class CociDoiToReferenceResponse {

	private CociCitation[] references;

	public CociDoiToReferenceResponse(CociCitation[] references) {
		this.references = references;
	}

	public CociCitation[] getReferences() {
		return references;
	}

	public void setReferences(CociCitation[] references) {
		this.references = references;
	}
}
