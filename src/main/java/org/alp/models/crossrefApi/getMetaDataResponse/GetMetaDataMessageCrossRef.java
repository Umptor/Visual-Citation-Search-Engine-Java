package org.alp.models.crossrefApi.getMetaDataResponse;

import org.alp.models.crossrefApi.AuthorCrossRef;
import org.alp.models.crossrefApi.ItemCrossRef;
import org.alp.models.crossrefApi.PublishTimeCrossRef;
import org.alp.models.crossrefApi.ReferenceCrossRef;

public class GetMetaDataMessageCrossRef extends ItemCrossRef {

	public GetMetaDataMessageCrossRef(int referenceCount, String doi, String isReferencedByCount, String[] title,
	                                  String[] containerTitle, AuthorCrossRef[] authors, float score, ReferenceCrossRef[] references,
	                                  String paperAbstract, PublishTimeCrossRef publishPrint, PublishTimeCrossRef publishedOnline,
	                                  String journalTitle, String unstructured) {

		super(referenceCount, doi, isReferencedByCount, title, containerTitle, authors, score, references,
				paperAbstract, publishPrint, publishedOnline, journalTitle, unstructured);
	}
}
