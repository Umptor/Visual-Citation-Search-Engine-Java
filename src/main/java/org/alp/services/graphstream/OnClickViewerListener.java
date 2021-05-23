package org.alp.services.graphstream;

import org.graphstream.graph.Graph;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;


public class OnClickViewerListener implements ViewerListener {
	private final Graph graph;
	private final Viewer viewer;
	private final GraphStreamService graphStreamService;
	private boolean loop = true;

	public OnClickViewerListener(Graph graph, GraphStreamService graphStreamService) {
		this.graph = graph;
		this.graphStreamService = graphStreamService;

		viewer = graph.display();

		viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);

		ViewerPipe fromViewer = viewer.newViewerPipe();
		fromViewer.addViewerListener(this);
		fromViewer.addSink(graph);

		new Thread(() -> {
			while(loop) {
				try {
					fromViewer.blockingPump();
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
