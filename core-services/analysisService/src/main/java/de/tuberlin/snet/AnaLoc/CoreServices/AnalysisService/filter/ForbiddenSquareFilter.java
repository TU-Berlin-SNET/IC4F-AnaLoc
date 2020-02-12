package de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.filter;

//import de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.model.Location;
import de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.model.Position;
import org.apache.flink.api.common.functions.FilterFunction;

/**
 * Description: ForbiddenSquareFilter is a very simple filter
 * @deprecated
 */
public class ForbiddenSquareFilter implements FilterFunction<Position> {
	int minX;
	int maxX;
	int minY;
	int maxY;

	public ForbiddenSquareFilter(int minX, int maxX, int minY, int maxY){
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
	}
	@Override
	public boolean filter(Position position) {
		return (position.x>=this.minX && position.x<=this.maxX && position.y>=this.minY && position.y<=this.maxY);
	}
}
