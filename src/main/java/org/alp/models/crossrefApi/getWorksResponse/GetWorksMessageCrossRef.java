package org.alp.models.crossrefApi.getWorksResponse;

import com.google.gson.annotations.SerializedName;
import org.alp.models.crossrefApi.ItemCrossRef;

public class GetWorksMessageCrossRef {

	@SerializedName("total-results")
	private int totalResults;

	private ItemCrossRef[] items;

	@SerializedName("items-per-page")
	private int itemsPerPage;

	public GetWorksMessageCrossRef(int totalResults, ItemCrossRef[] items, int itemsPerPage) {
		this.totalResults = totalResults;
		this.items = items;
		this.itemsPerPage = itemsPerPage;
	}

	public int getTotalResults() {
		return totalResults;
	}

	public void setTotalResults(int totalResults) {
		this.totalResults = totalResults;
	}

	public ItemCrossRef[] getItems() {
		return items;
	}

	public void setItems(ItemCrossRef[] itemCrossRefs) {
		this.items = itemCrossRefs;
	}

	public int getItemsPerPage() {
		return itemsPerPage;
	}

	public void setItemsPerPage(int itemsPerPage) {
		this.itemsPerPage = itemsPerPage;
	}
}
