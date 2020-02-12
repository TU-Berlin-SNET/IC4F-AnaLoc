package de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.model;

import java.math.BigInteger;

/**
 * Description:  MqttMessageModel, a POJO for the input data stream
 * 		!important: if the input format is changed, you need to modify
 * 		this class and MqttWarning Model
 * @see MqttWarningModel contains reference of this class
 * @author Yong Wu (y.wu.1@campus.tu-berlin.de)
 */
public class MqttMessageModel {
	public String timestamp;
	public double confidence;
	public Orientation orientation;
	public String vehicleId;
	public Position position;
	public double velocity;
	public String forbiddenAreaName;

	public MqttMessageModel() {
		this.timestamp = "";
		this.confidence = 0;
		this.orientation = new Orientation();
		this.vehicleId = "";
		this.position = new Position();
		//this.loc = new Location(position,timestamp);
		this.velocity = 0.0;
		this.forbiddenAreaName = "";
	}

	public MqttMessageModel(String timestamp, double confidence, Orientation orientation, String vehicleId, Position position) {
		this.timestamp = timestamp;
		this.confidence = confidence;
		this.orientation = new Orientation(orientation);
		this.vehicleId = vehicleId;
		this.position = new Position(position);
		//this.loc = new Location(position,timestamp);
		this.velocity = 0.0;
	}

	public MqttMessageModel(MqttMessageModel model) {
		this.timestamp = model.timestamp;
		this.confidence = model.confidence;
		this.orientation = new Orientation(model.orientation);
		this.vehicleId = model.vehicleId;
		this.position = new Position(model.position);
		//this.loc = new Location(model.position,model.timestamp);
		this.velocity = 0.0;
	}

	private static long timeDiff(MqttMessageModel m1, MqttMessageModel m2) {
		return Math.abs(new BigInteger(m1.timestamp).subtract(new BigInteger(m2.timestamp)).longValue());
	}

	public static MqttMessageModel calculateVelocity(MqttMessageModel m1, MqttMessageModel m2) {
		long time = timeDiff(m1, m2);
		double velocity;
		if (time != 0) {
			velocity = Position.distance(m1.position, m2.position) / (time * 10e-6);
		} else {
			velocity = 0.0;
		}

		MqttMessageModel model = new MqttMessageModel(m1);
		model.velocity = velocity;
		return model;
	}
}
