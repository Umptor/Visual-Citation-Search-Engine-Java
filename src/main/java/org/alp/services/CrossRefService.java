package org.alp.services;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CrossRefService {
	private static String crossRefUrl = "https://api.crossref.org/";

	public static String getPaperByKeyWord(String keyword) throws URISyntaxException, IOException, InterruptedException {
		var httpClient = HttpClient.newHttpClient();
		String urlString = crossRefUrl + "works?query=" + keyword;
		urlString = ParamBuilder.addParam(urlString, "mailto", "bongutcha@gmail.com");
		var uri = new URI(urlString);

		var httpRequest = HttpRequest.newBuilder().GET().uri(uri).build();

		return httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
	}

}
