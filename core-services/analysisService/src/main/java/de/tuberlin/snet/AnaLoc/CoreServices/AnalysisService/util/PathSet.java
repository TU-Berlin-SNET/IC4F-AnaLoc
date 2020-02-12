package de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.util;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.LinkedList;


/**
 * This Class is created to sort the lines in to a end-to-end path, which is useful
 * for creating polygon from lines.
 *
 * @author Yong Wu
 */
public class PathSet {
	private LinkedList<LinkedList<Line2D>> path;

	/**
	 * This is the constructor, which initializes an empty linked list for linked list of the lines.
	 */
	public PathSet() {
		path = new LinkedList<LinkedList<Line2D>>(); // Hard to imagine, but useful for sorting.
	}


	/**
	 * Find the line which has the same start point or end point in the existing path and
	 * insert the line to proper position. If there are no line next to the line to be insert,
	 * create a new sub-path.
	 * After insertion, the path list may contains several sub paths.
	 * @param line
	 */
	public void add(Line2D line) {
		LinkedList<Line2D> list = new LinkedList<Line2D>();
		if (path.size() == 0) {
			list.add(line);
		} else {
			for (LinkedList<Line2D> tempList : path) {
				Point2D firstPoint = tempList.getFirst().getP1();
				Point2D lastPoint = tempList.getLast().getP2();
				if (line.getP2().equals(firstPoint)) {
					tempList.addFirst(line);
					return;
				} else if (line.getP1().equals(lastPoint)) {
					tempList.addLast(line);
					return;
				}
			}
			list.add(line);
		}
		path.add(list);
	}

	/**
	 * This function rearranges sub paths into the correct path.
	 * Sometimes, sub path may need to be reverse because of the constructor function.
	 * @return The path in correct order.
	 * @throws Exception
	 */
	public LinkedList<Line2D> getCorrectPath() throws Exception {
		if (path.size() == 0) throw new Exception("Path is empty!");
		else {
			LinkedList<Line2D> correctPath = new LinkedList<Line2D>(path.pop());
			while (path.size() != 0) {
				Point2D correctP1 = correctPath.getFirst().getP1();
				Point2D correctP2 = correctPath.getLast().getP2();
				boolean found = false;
				for (LinkedList<Line2D> list : path) {
					Point2D currentP1 = list.getFirst().getP1();
					Point2D currentP2 = list.getLast().getP2();
					if (currentP1.equals(correctP2)) {
						found = true;
						correctPath.addAll(list);
						path.remove(list);
						break;
					} else if (currentP2.equals(correctP1)) {
						found = true;
						correctPath.addAll(0, list);
						path.remove(list);
						break;
					} else if (currentP1.equals(correctP1)) {
						found = true;
						LinkedList<Line2D> tempList = list;
						Collections.reverse(tempList);
						correctPath.addAll(0, tempList);
						path.remove(list);
						break;
					} else if (currentP2.equals(correctP2)) {
						found = true;
						LinkedList<Line2D> tempList = list;
						Collections.reverse(tempList);
						correctPath.addAll(tempList);
						path.remove(list);
						break;
					}
				}
				if (!found) throw new Exception("Path is not connected!");
			}
			return correctPath;
		}
	}
}
