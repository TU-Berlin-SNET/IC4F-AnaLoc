package de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.filter;

import de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.model.Position;
import org.apache.flink.api.common.functions.FilterFunction;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 * Description:	ForbiddenPolygonFilter is designed for creating forbidden polygon zones
 * @author Yong Wu (y.wu.1@campus.tu-berlin.de)
 */
public class ForbiddenPolygonFilter implements FilterFunction<Position>, Serializable {
	Path2D polygonPath;

	/**
	 * Constructor which use the received points to create polygon
	 * @param point2DS several points
	 */
	public ForbiddenPolygonFilter(Point2D... point2DS) {
		polygonPath = new Path2D.Double();
		boolean isFirst = true;
		for (int i = 0; i < point2DS.length; i++) {
			if (isFirst) {
				polygonPath.moveTo(point2DS[i].getX(), point2DS[i].getY());
				isFirst = false;
			} else {
				polygonPath.lineTo(point2DS[i].getX(), point2DS[i].getY());
			}
		}
		polygonPath.closePath();
	}

	/**
	 * Constructor which use the received lines to create polygon
	 * NOTICE: the lines should be in the 'end-to-end' order.
	 * @param line2DS several lines
	 */
	public ForbiddenPolygonFilter(Line2D... line2DS) {
		polygonPath = new Path2D.Double();
		for (int i = 0; i < line2DS.length; i++) {
			polygonPath.append(line2DS[i], true);
		}
		polygonPath.closePath();
	}

	public ForbiddenPolygonFilter(Path2D path){
		this.polygonPath = (Path2D) path.clone();
	}

	public Path2D getPolygonPath() {
		return polygonPath;
	}

	/**
	 * This is the filter function, which receives location and judge the relationship
	 * between location and the polygon
	 * @param pos Position Object
	 * @return true   when the location is inside the polygon zone or on the border
	 * 				 false  when the location is outside the polygon zone
	 */
	@Override
	public boolean filter(Position pos) {
		return this.polygonPath.contains(pos.x,pos.y);
	}
}



