package org.alp.models.crossrefApi.getWorksResponse;

import org.alp.models.crossrefApi.DefaultCrossRefResponseCrossRef;

public class GetWorksResponseCrossRef extends DefaultCrossRefResponseCrossRef<GetWorksMessageCrossRef> {

	public GetWorksResponseCrossRef(String status, GetWorksMessageCrossRef getWorksMessageCrossRef) {
		super(status, getWorksMessageCrossRef);
	}
}
