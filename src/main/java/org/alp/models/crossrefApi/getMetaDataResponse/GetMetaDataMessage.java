package org.alp.models.crossrefApi.getMetaDataResponse;

import org.alp.models.crossrefApi.Author;
import org.alp.models.crossrefApi.Item;
import org.alp.models.crossrefApi.PublishTime;
import org.alp.models.crossrefApi.Reference;

public class GetMetaDataMessage extends Item {

	public GetMetaDataMessage(int referenceCount, String doi, String isReferencedByCount, String[] title,
	                          String[] containerTitle, Author[] authors, float score, Reference[] references,
	                          String paperAbstract, PublishTime publishPrint, PublishTime publishedOnline) {

		super(referenceCount, doi, isReferencedByCount, title, containerTitle, authors, score, references,
				paperAbstract, publishPrint, publishedOnline);
	}
}
