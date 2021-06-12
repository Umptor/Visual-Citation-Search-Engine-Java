package org.alp.services;

import javafx.event.Event;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import org.alp.models.Paper;
import org.alp.services.graphstream.GraphStreamService;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ContextMenuService {
	private ArrayList<MenuItem> menuItems = new ArrayList<>();
	private final ContextMenu contextMenu;
	private final Paper paper;
	private final GraphStreamService graphStreamService;

	public ContextMenuService(Paper paper, GraphStreamService graphStreamService) {
		this.contextMenu = new ContextMenu();
		this.paper = paper;
		this.graphStreamService = graphStreamService;
		this.generateMenuItems(paper);

		this.contextMenu.getItems().addAll(this.menuItems);
	}

	public ContextMenu getContextMenu() {
		return contextMenu;
	}

	public void setMenuItems(ArrayList<MenuItem> menuItems) {
		this.menuItems = menuItems;
	}

	private void generateMenuItems(Paper paper) {
		MenuItem focus = new MenuItem("Focus");
		focus.setOnAction(this::onFocusClick);

		MenuItem save = new MenuItem("Save");
		save.setOnAction(this::onSaveClick);

		this.menuItems.add(focus);
		this.menuItems.add(save);
	}

	private void onFocusClick(Event event) {
		System.out.println("Focus on this! " + paper.getTitle());
	}
	private void onSaveClick(Event event) {
		System.out.println("Save This! " + paper.getTitle());
	}
}
