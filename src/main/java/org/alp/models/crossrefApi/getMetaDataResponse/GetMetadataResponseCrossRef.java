package org.alp.models.crossrefApi.getMetaDataResponse;

import org.alp.models.crossrefApi.DefaultCrossRefResponseCrossRef;

public class GetMetadataResponseCrossRef extends DefaultCrossRefResponseCrossRef<GetMetaDataMessageCrossRef> {

	public GetMetadataResponseCrossRef(String status, GetMetaDataMessageCrossRef message) {
		super(status, message);
	}
}
