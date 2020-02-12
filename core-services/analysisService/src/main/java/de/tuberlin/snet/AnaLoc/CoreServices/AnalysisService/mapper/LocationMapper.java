package de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.mapper;

import de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.model.Location;
import de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.model.MqttMessageModel;
import org.apache.flink.api.common.functions.MapFunction;

/**
 * Class LocationMapper is deprecated and will not be used.
 * @deprecated
 */
@Deprecated
public class LocationMapper implements MapFunction<MqttMessageModel, Location> {
	private static final long serialVersionUID = 7297678522476602222L;

	@Override
	public Location map(MqttMessageModel value) {
		return new Location(value);
	}
}
