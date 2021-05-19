package org.alp.models.crossrefApi;

public class DefaultCrossRefResponse<T> {
	private String status;
	private T message;

	public DefaultCrossRefResponse(String status, T message) {
		this.status = status;
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public T getMessage() {
		return message;
	}

	public void setMessage(T message) {
		this.message = message;
	}
}
