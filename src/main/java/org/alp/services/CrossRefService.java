package org.alp.services;

import org.alp.models.Paper;
import org.alp.models.crossrefApi.getMetaDataResponse.GetMetadataResponseCrossRef;
import org.alp.models.crossrefApi.getWorksResponse.GetWorksResponseCrossRef;
import org.alp.models.crossrefApi.ItemCrossRef;
import org.alp.models.crossrefApi.ReferenceCrossRef;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class CrossRefService {
	private static final String crossRefUrl = "https://api.crossref.org/";

	private static final HashMap<String, Paper> foundFullPapers = new HashMap<>();

	// Api
	public static ArrayList<Paper> getPaperByKeyWord(String keyword) throws URISyntaxException, IOException, InterruptedException {
		var httpClient = HttpClient.newHttpClient();
		String urlString = crossRefUrl + "works";
		urlString = ParamBuilder.addParam(urlString, "query", URLEncoder.encode(keyword, StandardCharsets.UTF_8));
		urlString = ParamBuilder.addParam(urlString, "mailto", "e160503134@stud.tau.edu.tr");
		var uri = new URI(urlString);

		var httpRequest = HttpRequest.newBuilder().GET().uri(uri).build();

		String response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
		var items = JsonMapperService.mapJson(response, GetWorksResponseCrossRef.class)
				.getMessage()
				.getItems();

		var ret = Arrays.stream(items).map(CrossRefService::mapItemToPaper)
				.filter(paper -> paper.getDoi() != null && !paper.getDoi().equals(""))
				.collect(Collectors.toCollection(ArrayList::new));

		ret.forEach(paper -> paper.fixReferences(false));

		return ret;
	}

	private static CompletableFuture<HttpResponse<String>> getMetadataAsync(String doi) {
		var httpClient = HttpClient.newHttpClient();
		String urlString = crossRefUrl + "works/" + URLEncoder.encode(doi, StandardCharsets.UTF_8);
		urlString = ParamBuilder.addParam(urlString, "mailto", "e160503134@stud.tau.edu.tr");


		URI uri;
		try {
			uri = new URI(urlString);
		} catch(URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
		var httpRequest = HttpRequest.newBuilder().GET().uri(uri).build();

		return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());

	}
	// end Api

	static Paper getFullReferences(Paper root, Paper oldRoot) {
		if(foundFullPapers.get(root.getDoi()) != null) return foundFullPapers.get(root.getDoi());
		ArrayList<CompletableFuture<HttpResponse<String>>> requests = new ArrayList<>();
		ArrayList<Paper> references = new ArrayList<>();
		ArrayList<Paper> alreadyExistingPapers = new ArrayList<>();

		var newRootResponse = getMetadataAsync(root.getDoi());

		Paper newRoot = null;
		try {
			assert newRootResponse != null;
			String notFoundString = "[]";
			String responseString = newRootResponse.get().body();
			if(responseString.equals(notFoundString)) {
				return null;
			}
			newRoot = mapItemToPaper(JsonMapperService.mapJson(newRootResponse.get().body(), GetMetadataResponseCrossRef.class).getMessage());
		} catch(Exception e) {
			System.out.println("Couldn't find root Node in OC");
			if(!(e instanceof NullPointerException)) {
				e.printStackTrace();
			}
			return null;
		}

		if(newRoot.getReferences() == null) newRoot.setReferences(new ArrayList<>());

		newRoot.getReferences().forEach(reference -> {
			Paper alreadyRetrievedPaper = foundFullPapers.get(reference.getDoi());
			if(alreadyRetrievedPaper != null) {
				alreadyExistingPapers.add(alreadyRetrievedPaper);
				return;
			}

			if(requests.size() % 20 == 0 && requests.size() != 0) {
				try { Thread.sleep(1000); } catch(InterruptedException e) { e.printStackTrace(); }
			}

			requests.add(getMetadataAsync(reference.getDoi()));
		});

		requests.forEach(response -> {
			try {
				String notFoundString = "Resource not found.";
				String responseString = response.get().body();
				if(responseString.equals(notFoundString)) {
					return;
				}
				references.add(mapItemToPaper(JsonMapperService.mapJson(response.get().body(), GetMetadataResponseCrossRef.class).getMessage()));
			}
			catch(Exception e) {
				System.out.println("Couldn't get paper from OC");
				e.printStackTrace();
			}
		});

		PaperService.addFullReferences(newRoot, references, alreadyExistingPapers);
		PaperService.addFullPapersToFoundList(newRoot);

		newRoot.fixReferences(true);
		if(oldRoot != null && !newRoot.getReferences().contains(oldRoot)) {
			newRoot.getReferences().add(oldRoot);
		}

		return newRoot;
	}

	private static Paper mapItemToPaper(ItemCrossRef item) {
		List<Paper> references;
		if(item.getReferences() == null) {
			references = null;
		} else {
			references = Arrays.stream(item.getReferences())
					.map((ReferenceCrossRef reference) -> {
						Paper paper = new Paper();
						paper.setDoi(reference.getDoi());
						return (paper.getDoi() == null) ? null : paper;
					})
					.filter(Objects::nonNull)
					.collect(Collectors.toList());
		}

		return new Paper(item.getDoi(),
				mapTitle(item),
				item.getAuthors(),
				references == null ? null : (ArrayList<Paper>) references, item.getPaperAbstract(),
				item.getPublishedPrint(), item.getPublishedOnline());
	}

	private static String mapTitle(ItemCrossRef item) {
		if(item.getTitle() != null && item.getTitle().length > 0) return item.getTitle()[0];

		return null;
	}
}


