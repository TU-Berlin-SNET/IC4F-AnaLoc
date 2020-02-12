package de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.mapper;

import de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.model.Location;
import de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.model.MqttWarningModel;
import org.apache.flink.api.common.functions.MapFunction;
/**
 * Class ForbiddenSquareWarnMapper is deprecated and will not be used.
 * @deprecated
 */
@Deprecated
public class ForbiddenSquareWarnMapper implements MapFunction<Location, String> {
	private static final long serialVersionUID = -6453061588035401319L;

	@Override
	public String map(Location value) {
		// not good for generating warning model...
		//MqttWarningModel mdl = new MqttWarningModel()
		return value.toString();
	}
}
