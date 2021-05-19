package org.alp.models.crossrefApi.getWorksResponse;

import com.google.gson.annotations.SerializedName;
import org.alp.models.crossrefApi.DefaultCrossRefResponse;

public class GetWorksResponse extends DefaultCrossRefResponse<Message> {

	public GetWorksResponse(String status, Message message) {
		super(status, message);
	}
}
