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
		var items = jsonMapperToBody(response, GetWorksResponse.class)
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

	public static Paper getFullReferences(Paper root, Paper oldRoot) {
		if(foundFullPapers.get(root.getDoi()) != null) return foundFullPapers.get(root.getDoi());
		ArrayList<CompletableFuture<HttpResponse<String>>> requests = new ArrayList<>();
		ArrayList<Paper> references = new ArrayList<>();
		ArrayList<Paper> alreadyExistingPapers = new ArrayList<>();

		var newRootResponse = getMetadataAsync(root.getDoi());

		Paper newRoot = null;
		try {
			assert newRootResponse != null;
			newRoot = mapItemToPaper(jsonMapperToBody(newRootResponse.get().body(), GetMetadataResponse.class).getMessage());
		} catch(InterruptedException | ExecutionException | NullPointerException e) { e.printStackTrace(); }

		if(newRoot == null) {
			System.out.println("wtf man");
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
			try { references.add(mapItemToPaper(jsonMapperToBody(response.get().body(), GetMetadataResponse.class).getMessage())); }
			catch(InterruptedException | ExecutionException e) { e.printStackTrace(); }
		});

		addFullReferences(newRoot, references, alreadyExistingPapers);
		addFullPapersToFoundList(newRoot);

		newRoot.fixReferences(true);
		if(oldRoot != null && !newRoot.getReferences().contains(oldRoot)) {
			newRoot.getReferences().add(oldRoot);
		}

		return newRoot;
	}

	private static void addFullPapersToFoundList(Paper root) {
		foundFullPapers.putIfAbsent(root.getDoi(), root);

//		root.getReferences().forEach(paper -> foundFullPapers.put(paper.getDoi(), paper));
	}

	private static void addFullReferences(Paper root, List<Paper> references, List<Paper> alreadyFoundPapers) {
		references.forEach(paper -> {
			if(paper.getDoi() == null || paper.getTitle() == null) return;
			boolean isFullPaper = false;
			if(root.getReferences().contains(paper)) {
				int index = root.getReferences().indexOf(paper);
				Paper reference = root.getReferences().get(index);
				if(reference.getDoi() != null && reference.getTitle() != null) isFullPaper = true;

				if(!isFullPaper) {
					root.getReferences().set(index, paper);
				}
			} else {
				root.getReferences().add(paper);
			}
		});

		root.getReferences().addAll(alreadyFoundPapers);
	}

	public static Paper findPaper(Paper rootPaper, String doiToFind) {
		if(rootPaper.getDoi().equals(doiToFind)) return rootPaper;
		else return rootPaper.getReferences().get(rootPaper.getReferences().indexOf(new Paper(doiToFind)));
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
				mapTitle(item),
				item.getAuthors(),
				references == null ? null : (ArrayList<Paper>) references, item.getPaperAbstract(),
				item.getPublishedPrint(), item.getPublishedOnline());
	}

	private static String mapTitle(Item item) {
		if(item.getTitle() != null && item.getTitle().length > 0) return item.getTitle()[0];

		return null;
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
}


