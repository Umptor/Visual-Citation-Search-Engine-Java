package org.alp.services;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.alp.models.Paper;
import org.alp.models.crossrefApi.getMetaDataResponse.GetMetadataResponse;
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
import java.util.concurrent.TimeUnit;
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

	public static Paper getMetadata(Paper paper) throws URISyntaxException {
		var response = getMetadata(paper.getDoi());

		var responseArr = new ArrayList<CompletableFuture<HttpResponse<String>>>();
		responseArr.add(response);

		var ret = mapMetadataResponsesToObjects(responseArr).get(0);
		fixPaperReferences(ret);

		return ret;
	}
	private static CompletableFuture<HttpResponse<String>> getMetadata(String doi) throws URISyntaxException {
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
		ArrayList<Paper> ret;
		if(regetReferences) {
			Paper newPaper = getMetadata(paper);
			ret = getConnections(newPaper, depth);
			paper.setReferences(ret);
		} else {
			ret = getConnections(paper, depth);
		}
		fixPaperReferences(paper);
		return ret;
	}

	private static ArrayList<Paper> getConnections(Paper paper, int depth) throws URISyntaxException, InterruptedException {
		// TODO: Only get depth amount of references
		ArrayList<Paper> references = new ArrayList<>();
		ArrayList<Paper> existingReferences = new ArrayList<>();
		ArrayList<CompletableFuture<HttpResponse<String>>> responses = new ArrayList<>();

		if(paper.getReferences() != null && depth > 0) {
			for(int i = 0; i < paper.getReferences().size(); i++) {
				String title = paper.getReferences().get(i).getTitle();
				if(title != null && !title.equals("")) {
					existingReferences.add(paper.getReferences().get(i));
				}

				// Wait 1 second between every 20 requests because of the limit on CrossRef's Api. Even though the limit
				// is 50 requests per second, when I set this to 20+ I get errors
				if(responses.size() % 20 == 0 && responses.size() != 0) {
					TimeUnit.SECONDS.sleep(1);
				}
				responses.add(getMetadata(paper.getReferences().get(i).getDoi()));
			}

			// Join response, then map item to paper and add into references
			references = mapMetadataResponsesToObjects(responses);
			references.addAll(existingReferences);

			for(Paper reference : references) {
				paper.setReferences(references);
				getConnections(reference, depth - 1);
			}
		}

		return references;
	}

	private static ArrayList<Paper> mapMetadataResponsesToObjects(ArrayList<CompletableFuture<HttpResponse<String>>> responses) {
		return responses.stream()
				.map(response -> mapItemToPaper(
						jsonMapperToBody(response.join().body(), GetMetadataResponse.class).getMessage()))
				.filter((Paper reference) -> reference.getTitle() != null || !reference.getTitle().equals(""))
				.collect(Collectors.toCollection(ArrayList::new));
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
		fixPaperReferencesAlgo(root, new ArrayList<>());
	}

	private static void fixPaperReferencesAlgo(Paper root, ArrayList<Paper> visited) {
		if(visited.contains(root) || root.getReferences() == null) {
			return;
		}

		ArrayList<Paper> references = root.getReferences();

		references.forEach(reference -> {
			visited.add(reference);

			if(reference.getReferences() == null) {
				reference.setReferences(new ArrayList<>());
			}

			if(reference.getReferences().contains(root)) return;

			reference.getReferences().add(root);

			fixPaperReferencesAlgo(reference, visited);
		});
	}

	public static Paper findPaper(Paper rootPaper, String doiToFind) {

		// BFS Search for paper
		ArrayList<Paper> currentLevel = new ArrayList<>();
		ArrayList<Paper> nextLevel = new ArrayList<>();
		currentLevel.add(rootPaper);
		if(rootPaper.getDoi().equals(doiToFind)) {
			return rootPaper;
		}
		while(currentLevel.size() > 0) {
			for(Paper paperInLevel: currentLevel) {
				if(paperInLevel == null || paperInLevel.getReferences() == null) {
					continue;
				}
				for(Paper neighbour: paperInLevel.getReferences()) {
					if(neighbour == null) {
						continue;
					}
					if(neighbour.getDoi().equals(doiToFind)) {
						return neighbour;
					}
					nextLevel.add(neighbour);
				}
			}
			currentLevel = new ArrayList<>(nextLevel);
			nextLevel.clear();
		}
		System.out.println("Paper not found, huh?");
		return new Paper();
	}
}


