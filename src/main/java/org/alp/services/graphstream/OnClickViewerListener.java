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

	@Override
	public void viewClosed(String s) {
		loop = false;
		System.out.println("view closed");
	}

	@Override
	public void buttonPushed(String s) {
		System.out.println("button Pushed" + s);
	}

	@Override
	public void buttonReleased(String s) {
		System.out.println("button released");
	}

	@Override
	public void mouseOver(String s) {
		System.out.println("mouse over");
	}

	@Override
	public void mouseLeft(String s) {
		System.out.println("mouse left");
	}
}
