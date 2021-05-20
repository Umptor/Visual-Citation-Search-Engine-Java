package org.alp.services;

import com.google.gson.Gson;
import org.alp.models.Paper;
import org.alp.models.crossrefApi.getMetaDataResponse.GetMetadataResponse;
import org.alp.models.crossrefApi.getWorksResponse.GetWorksResponse;
import org.alp.models.crossrefApi.Item;
import org.alp.models.crossrefApi.Reference;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CrossRefService {
	private static final String crossRefUrl = "https://api.crossref.org/";

	// Api

	public static ArrayList<Paper> getPaperByKeyWord(String keyword) throws URISyntaxException, IOException, InterruptedException {
		var httpClient = HttpClient.newHttpClient();
		String urlString = crossRefUrl + "works";
		urlString = ParamBuilder.addParam(urlString, "query", keyword);
		urlString = ParamBuilder.addParam(urlString, "mailto", "bongutcha@gmail.com");
		var uri = new URI(urlString);

		var httpRequest = HttpRequest.newBuilder().GET().uri(uri).build();

		String response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
		var items = new Gson().fromJson(response, GetWorksResponse.class).getMessage().getItems();

		return Arrays.stream(items).map(CrossRefService::mapItemToPaper).collect(Collectors.toCollection(ArrayList::new));
	}

	public static CompletableFuture<HttpResponse<String>> getMetadataFromDoi(String doi) throws URISyntaxException {
		var httpClient = HttpClient.newHttpClient();
		String urlString = crossRefUrl + "works/" + doi;
		urlString = ParamBuilder.addParam(urlString, "mailto", "bongutcha@gmail.com");


		var uri = new URI(urlString);
		var httpRequest = HttpRequest.newBuilder().GET().uri(uri).build();

		return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());

	}

	// Serialized parallel API calls
//	private static <T> T serializeParallelCalls(HttpRequest request) {
//		HttpClient httpClient = HttpClient.newHttpClient();
//
////		var response = httpClient.
//	}

	// Mappers

	private static Paper mapItemToPaper(Item item) {
		List<Paper> references;
		if(item.getReferences() == null) {
			references = null;
		} else {
			references = Arrays.stream(item.getReferences())
					.map((Reference reference) -> {
						Paper paper = new Paper();
						paper.setDoi(reference.getDoi());
						return (paper.getDoi() == null) ? null : paper;
					})
					.filter(Objects::nonNull)
					.collect(Collectors.toList());
		}

		return new Paper(item.getDoi(), item.getTitle()[0], item.getAuthors(), references == null ? null : (ArrayList<Paper>) references);
	}



	public static ArrayList<Paper> getRelatedPapers(Paper paper, int depth) throws URISyntaxException {

		return getConnections(paper, depth);
	}

	private static ArrayList<Paper> getConnections(Paper paper, int depth) throws URISyntaxException {
		// TODO: Only get depth amount of references
		ArrayList<Paper> references = new ArrayList<>();
		ArrayList<CompletableFuture<HttpResponse<String>>> responses = new ArrayList<>();

		if(paper.getReferences() != null) {
			for(int i = 0; i < paper.getReferences().size(); i++) {
				responses.add(getMetadataFromDoi(paper.getReferences().get(i).getDoi()));
			}

			// Join response, then map item to paper and add into references
			references = responses.stream()
					.map(response -> mapItemToPaper(
							new Gson().fromJson(response.join().body(), GetMetadataResponse.class).getMessage()))
					.collect(Collectors.toCollection(ArrayList::new));
		}

		return references;
	}
}
