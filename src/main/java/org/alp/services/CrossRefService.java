package org.alp.services;

import com.google.gson.Gson;
import org.alp.models.Paper;
import org.alp.models.crossrefApi.getWorksResponse.GetWorksResponse;
import org.alp.models.crossrefApi.getWorksResponse.Item;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CrossRefService {
	private static String crossRefUrl = "https://api.crossref.org/";



	public static void getPaperByKeyWord(String keyword) throws URISyntaxException, IOException, InterruptedException {
		var httpClient = HttpClient.newHttpClient();
		String urlString = crossRefUrl + "works?query=" + keyword;
		urlString = ParamBuilder.addParam(urlString, "mailto", "bongutcha@gmail.com");
		var uri = new URI(urlString);

		var httpRequest = HttpRequest.newBuilder().GET().uri(uri).build();

		String response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
		var items = new Gson().fromJson(response, GetWorksResponse.class).getMessage().getItems();
		ArrayList<Paper> papers = Arrays.stream(items).map(CrossRefService::mapItemToPaper).collect(Collectors.toCollection(ArrayList::new));

		PaperService.setPapers(papers);
	}

	private static Paper mapItemToPaper(Item item) {
		return new Paper(item.getDoi(), item.getTitle()[0], item.getAuthors(), getRelatedPapers(item));
	}

	private static ArrayList<Paper> getRelatedPapers(Item item) {
		return new ArrayList<>();
	}

}
