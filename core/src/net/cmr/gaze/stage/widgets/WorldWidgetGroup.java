package net.cmr.gaze.stage.widgets;

import java.util.ArrayList;

public class WorldWidgetGroup {
	ArrayList<WorldWidget> group;
	WorldWidget currentlySelected;
	public WorldWidgetGroup() {
		this.group = new ArrayList<>();
		this.currentlySelected = null;
	}
	public void click(WorldWidget widget) {
		if(currentlySelected!=null) {
			currentlySelected.setSelected(false);
		}
		if(widget.equals(currentlySelected)) {
			currentlySelected = null;
			return;
		} else {
			currentlySelected = widget;
			currentlySelected.setSelected(true);
		}
	}
	public void deselectAll() {
		if(currentlySelected!=null) {
			currentlySelected.setSelected(false);
		}
		currentlySelected = null;
	}
	public void addWidget(WorldWidget widget) {
		group.add(widget);
	}
	public ArrayList<WorldWidget> getWidgets() {
		return group;
	}
	public WorldWidget getSelectedWidget() {
		return currentlySelected;
	}
}
