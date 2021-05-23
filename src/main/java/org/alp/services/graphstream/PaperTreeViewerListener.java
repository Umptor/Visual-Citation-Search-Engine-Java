package org.alp.services.graphstream;

import org.graphstream.graph.Graph;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;


public class PaperTreeViewerListener implements ViewerListener {
	private final GraphStreamService graphStreamService;
	private final ViewerPipe viewerPipe;
	private boolean loop = true;

	public PaperTreeViewerListener(GraphStreamService graphStreamService, Graph graph, Viewer viewer) {
		this.graphStreamService = graphStreamService;

		this.viewerPipe = viewer.newViewerPipe();
		this.viewerPipe.addViewerListener(this);
		this.viewerPipe.addSink(graph);

		new Thread(() -> {
			while(loop) {
				try {
					this.viewerPipe.blockingPump();
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	String pushedId = "";

	@Override
	public void viewClosed(String s) {
		loop = false;
		System.out.println("view closed");
	}

	@Override
	public void buttonPushed(String id) {
		pushedId = id;
	}

	@Override
	public void buttonReleased(String id) {
		if(pushedId.equals(id)) {
			graphStreamService.selectNode(id);
		} else {
			System.out.println("Make up your mind and select something");
		}
	}

	@Override
	public void mouseOver(String s) {
	}

	@Override
	public void mouseLeft(String s) {
	}
}
