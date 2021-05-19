package org.alp.services;

public class ParamBuilder {

	public static String addParam(String url, String key, String value) {
		String separator = url.contains("?") ? "&" : "?";
		return url + separator + key + "=" + value;
	}
}
