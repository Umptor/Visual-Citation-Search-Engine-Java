package org.alp.services.graphstream;

import javafx.scene.Cursor;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import org.alp.App;
import org.graphstream.graph.Graph;
import org.graphstream.ui.geom.Point2;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;
import org.graphstream.ui.view.camera.Camera;


public class PaperTreeViewerListener implements ViewerListener {
	private final GraphStreamService graphStreamService;
	private final ViewerPipe viewerPipe;
	private final View view;
	private boolean loop = true;

	private double startX = 0.0;
	private double startY = 0.0;
	private final double minDistanceForDrag = 2.0;
	private boolean cursorNormal = true;

	public PaperTreeViewerListener(GraphStreamService graphStreamService, Graph graph, Viewer viewer) {
		this.graphStreamService = graphStreamService;

		this.viewerPipe = viewer.newViewerPipe();
		this.viewerPipe.addViewerListener(this);
		this.viewerPipe.addSink(graph);
		this.view = viewer.getDefaultView();

		((Pane) this.view).addEventFilter(ScrollEvent.ANY, this::onScroll);

		((Pane) this.view).addEventHandler(MouseDragEvent.MOUSE_PRESSED, this::onMouseDown);
		((Pane) this.view).addEventHandler(MouseEvent.MOUSE_DRAGGED, this::onMouseDrag);
		((Pane) this.view).addEventHandler(MouseEvent.MOUSE_RELEASED, this::onMouseUp);

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
		Point3 newCenter;
		if(pushedId.equals(id)) {
			newCenter = graphStreamService.selectNode(id);
		} else {
			System.out.println("Make up your mind and select something");
			return;
		}

		Camera cam = view.getCamera();
		cam.setViewCenter(newCenter.x, newCenter.y, newCenter.z);
	}

	@Override
	public void mouseOver(String s) {
	}

	@Override
	public void mouseLeft(String s) {
	}

	public void onScroll(ScrollEvent scrollEvent) {
		double i = scrollEvent.getDeltaY();
		double factor = Math.pow(.90, i / 40);

		Camera cam = view.getCamera();
		double zoom = cam.getViewPercent() * factor;
		Point2 pxCenter  = cam.transformGuToPx(cam.getViewCenter().x, cam.getViewCenter().y, 0);
		Point3 guClicked = cam.transformPxToGu(scrollEvent.getX(), scrollEvent.getY());
		double newRatioPx2Gu = cam.getMetrics().ratioPx2Gu/factor;
		double x = guClicked.x + (pxCenter.x - scrollEvent.getX())/newRatioPx2Gu;
		double y = guClicked.y - (pxCenter.y - scrollEvent.getY())/newRatioPx2Gu;
		cam.setViewCenter(x, y, 0);
		cam.setViewPercent(zoom);
		scrollEvent.consume();
	}

	public void onMouseDown(MouseEvent mouseEvent) {
		startX = mouseEvent.getX();
		startY = mouseEvent.getY();
	}

	private void onMouseUp(MouseEvent mouseEvent) {
		App.getScene().setCursor(Cursor.DEFAULT);
		cursorNormal = true;
	}

	public void onMouseDrag(MouseEvent mouseEvent) {
		if(!mouseEvent.isPrimaryButtonDown()) return;
		if(cursorNormal) App.getScene().setCursor(Cursor.CLOSED_HAND);

		doDrag(mouseEvent);
	}

	private synchronized void doDrag(MouseEvent mouseEvent) {
		double endX = mouseEvent.getX();
		double endY = mouseEvent.getY();
		double distanceX = -(endX - startX);
		double distanceY = +(endY - startY);

		double normalizationFactor = 20.0;
		double distanceXNormalized = distanceX / normalizationFactor;
		double distanceYNormalized = distanceY / normalizationFactor;

		if(Math.abs(distanceX) > minDistanceForDrag ||
				Math.abs(distanceY) > minDistanceForDrag ||
				Math.abs(distanceX + distanceY) > minDistanceForDrag) {
			// Do drag
			Point3 viewCenter = view.getCamera().getViewCenter();
			startX = endX;
			startY = endY;

			view.getCamera().setViewCenter(
					viewCenter.x + distanceXNormalized,
					viewCenter.y + distanceYNormalized,
					viewCenter.z);
		}
	}
}
