package org.alp.services;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.alp.models.Paper;
import org.alp.models.crossrefApi.getWorksResponse.GetWorksResponse;
import org.alp.models.crossrefApi.Item;
import org.alp.models.crossrefApi.Reference;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
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
		urlString = ParamBuilder.addParam(urlString, "query", URLEncoder.encode(keyword, StandardCharsets.UTF_8));
		urlString = ParamBuilder.addParam(urlString, "mailto", "e160503134@stud.tau.edu.tr");
		var uri = new URI(urlString);

		var httpRequest = HttpRequest.newBuilder().GET().uri(uri).build();

		String response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
		var items = jsonMapperToBody(response, GetWorksResponse.class)
				.getMessage()
				.getItems();

		var ret = Arrays.stream(items).map(CrossRefService::mapItemToPaper).collect(Collectors.toCollection(ArrayList::new));
		ret.forEach(CrossRefService::fixPaperReferences);
		return ret;
	}

	private static CompletableFuture<HttpResponse<String>> getMetadataAsync(String doi) throws URISyntaxException {
		var httpClient = HttpClient.newHttpClient();
		String urlString = crossRefUrl + "works/" + URLEncoder.encode(doi, StandardCharsets.UTF_8);
		urlString = ParamBuilder.addParam(urlString, "mailto", "e160503134@stud.tau.edu.tr");


		var uri = new URI(urlString);
		var httpRequest = HttpRequest.newBuilder().GET().uri(uri).build();

		return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
	}

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

		return new Paper(item.getDoi(),
				item.getTitle() == null || item.getTitle().length == 0 ? null : item.getTitle()[0],
				item.getAuthors(),
				references == null ? null : (ArrayList<Paper>) references, item.getPaperAbstract(),
				item.getPublishedPrint(), item.getPublishedOnline());
	}


	/**
	 *
	 * @param paper - References of "paper" will be gotten
	 * @param depth - How many levels deep the references array should be
	 * @param regetReferences - Should the initial references array be regotten
	 * @return - Only used when regetReferences = true, need to manually set the papers references
	 * @throws URISyntaxException - An error with the url, my fault
	 * @throws InterruptedException - An error with the server, my fault
	 *
	 */
	public static ArrayList<Paper> getRelatedPapers(Paper paper, int depth, boolean regetReferences) throws URISyntaxException, InterruptedException {

	}

	private static ArrayList<Paper> getConnections(Paper paper, int depth) throws URISyntaxException, InterruptedException {

	}

	private static ArrayList<Paper> mapMetadataResponsesToObjects(ArrayList<CompletableFuture<HttpResponse<String>>> responses) {

	}

	private static <T> T jsonMapperToBody(String str, Class<T> classOfT) {
		T returnValue = null;
		try {
			returnValue = new Gson().fromJson(str, classOfT);
		} catch(JsonSyntaxException exception) {
			System.out.println("Exception while parsing json");
			System.out.println("Json in question");
			System.out.println(str);
			System.out.println(exception.toString());
		}
		return returnValue;
	}

	private static void fixPaperReferences(Paper root) {
	}

	private static void fixPaperReferencesAlgo(Paper root, ArrayList<Paper> visited) {

	}

	public static Paper findPaper(Paper rootPaper, String doiToFind) {

	}
}


