package org.alp.services;

import org.alp.models.crossrefApi.getWorksResponse.GetWorksResponse;

public class SearchResultsService {
	private static GetWorksResponse getWorksResponse = null;

	public static GetWorksResponse getGetWorksResponse() {
		return getWorksResponse;
	}

	public static void setGetWorksResponse(GetWorksResponse getWorksResponse) {
		SearchResultsService.getWorksResponse = getWorksResponse;
	}

	public static void clear() {
		getWorksResponse = null;
	}


}
