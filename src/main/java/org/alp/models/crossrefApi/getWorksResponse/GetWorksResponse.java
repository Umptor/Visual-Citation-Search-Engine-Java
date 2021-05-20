package org.alp.models.crossrefApi.getWorksResponse;

import org.alp.models.crossrefApi.DefaultCrossRefResponse;

public class GetWorksResponse extends DefaultCrossRefResponse<GetWorksMessage> {

	public GetWorksResponse(String status, GetWorksMessage getWorksMessage) {
		super(status, getWorksMessage);
	}
}
