package org.alp.services;

import org.alp.models.OpenCitationApi.GetMetadataResponseOpenCitation;
import org.alp.models.Paper;
import org.alp.models.crossrefApi.AuthorCrossRef;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class OpenCitationService {


	private static final String opencitationUrl = "https://opencitations.net/index/api/v1/";
	private static final HashMap<String, Paper> foundFullPapers = new HashMap<>();


	public static Paper getFullReferences(Paper root, Paper oldRoot) {
		if(foundFullPapers.containsKey(root.getDoi())) return foundFullPapers.get(root.getDoi());
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
			newRoot = mapItemToPaper(JsonMapperService.mapJson(removeSurroundingCurlyBraces(newRootResponse.get().body()), GetMetadataResponseOpenCitation.class));
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

			requests.add(getMetadataAsync(reference.getDoi()));
		});

		requests.forEach(response -> {
			try {
				String notFoundString = "[]";
				String responseString = response.get().body();
				if(responseString.equals(notFoundString)) {
					return;
				}
				references.add(mapItemToPaper(JsonMapperService.mapJson(removeSurroundingCurlyBraces(responseString), GetMetadataResponseOpenCitation.class)));
			}
			catch(Exception e) {
				System.out.println("Couldn't get paper from OC");
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

	private static String removeSurroundingCurlyBraces(String json) {
		return json.substring(1, json.length() - 2);
	}


	private static CompletableFuture<HttpResponse<String>> getMetadataAsync(String doi) {
		var httpClient = HttpClient.newHttpClient();
		String urlString = opencitationUrl + "metadata/" + doi;

		URI uri;
		try {
			uri = new URI(urlString);
		} catch(URISyntaxException uriSyntaxException) {
			System.out.println(uriSyntaxException.getMessage());
			return null;
		}

		var httpRequest = HttpRequest.newBuilder().GET().uri(uri).build();
		return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
	}

	private static Paper mapItemToPaper(GetMetadataResponseOpenCitation response) {
		String paperSeparator = "; ";

		Paper paper = new Paper();

		paper.setYear(Integer.parseInt(response.getYear()));

		response.setReference(response.getReference() + paperSeparator +  response.getCitation());
		paper.setReferences(Arrays.stream(response.getReference().split("; "))
				.map(doi -> {
					Paper reference = new Paper();
					reference.setDoi(doi);
					return reference;
				})
				.collect(Collectors.toCollection(ArrayList::new)));

		paper.setAuthors(Arrays.stream(response.getAuthor().split(paperSeparator))
				.map(author -> {
					String[] authorArr = author.split(", ");
					String firstName = authorArr.length > 1 ? authorArr[1] : "No first name found";
					String lastName = authorArr.length > 0 ? authorArr[0] : "No last name found";

					return new AuthorCrossRef(firstName, lastName, "");
				}).collect(Collectors.toList()).toArray(new AuthorCrossRef[]{}));


		paper.setDoi(response.getDoi());
		paper.setTitle(response.getTitle());

		return paper;
	}
}
