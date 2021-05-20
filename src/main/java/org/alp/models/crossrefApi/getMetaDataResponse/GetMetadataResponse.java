package org.alp.models.crossrefApi.getMetaDataResponse;

import org.alp.models.crossrefApi.DefaultCrossRefResponse;

public class GetMetadataResponse extends DefaultCrossRefResponse<GetMetaDataMessage> {

	public GetMetadataResponse(String status, GetMetaDataMessage message) {
		super(status, message);
	}
}
