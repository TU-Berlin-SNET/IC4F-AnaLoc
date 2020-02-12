package de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.function;

import de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.model.MqttMessageModel;
import org.apache.flink.api.common.functions.AggregateFunction;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Description:	VelocityCalculation is an AggregateFunction
 * 		which takes 2 data points from the stream and calculate the velocity accordingly.
 * @author Yong Wu (y.wu.1@campus.tu-berlin.de)
 */
public class VelocityCalculation implements AggregateFunction<MqttMessageModel, Queue<MqttMessageModel>, MqttMessageModel> {
	private static final long serialVersionUID = -6629952017716942010L;

	@Override
	public Queue<MqttMessageModel> createAccumulator() {
		return new LinkedList<>();
	}

	@Override
	public Queue<MqttMessageModel> add(MqttMessageModel value, Queue<MqttMessageModel> accumulator) {
		accumulator.offer(value);
		return accumulator;
	}

	@Override
	public MqttMessageModel getResult(Queue<MqttMessageModel> accumulator) {
		MqttMessageModel m1 = accumulator.poll();
		MqttMessageModel m2 = accumulator.poll();
		return MqttMessageModel.calculateVelocity(m1, m2);
	}

	@Override
	public Queue<MqttMessageModel> merge(Queue<MqttMessageModel> acc1, Queue<MqttMessageModel> acc2) {
		acc1.addAll(acc2);
		return acc1;
	}
}
