package de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.filter;

import de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.model.MqttMessageModel;
import org.apache.flink.api.common.functions.FilterFunction;

/**
 * Description:	LowSpeedFilter
 * 		is used to filter data points whose velocity is less than the threshold
 * @author Yong Wu (y.wu.1@campus.tu-berlin.de)
 */
public class LowSpeedFilter implements FilterFunction<MqttMessageModel> {
	private double lowSpeedThreshold;

	public LowSpeedFilter(Double lowSpeedThreshold){
		this.lowSpeedThreshold = lowSpeedThreshold;
	}
	@Override
	public boolean filter(MqttMessageModel model){
		return model.velocity<this.lowSpeedThreshold;
	}
}
