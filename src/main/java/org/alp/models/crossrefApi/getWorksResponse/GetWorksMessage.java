package org.alp.models.crossrefApi.getWorksResponse;

import com.google.gson.annotations.SerializedName;
import org.alp.models.crossrefApi.Item;

public class GetWorksMessage {

	@SerializedName("total-results")
	private int totalResults;

	private Item[] items;

	@SerializedName("items-per-page")
	private int itemsPerPage;

	public GetWorksMessage(int totalResults, Item[] items, int itemsPerPage) {
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

	public Item[] getItems() {
		return items;
	}

	public void setItems(Item[] items) {
		this.items = items;
	}

	public int getItemsPerPage() {
		return itemsPerPage;
	}

	public void setItemsPerPage(int itemsPerPage) {
		this.itemsPerPage = itemsPerPage;
	}
}
