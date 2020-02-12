package de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.model;

import de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.filter.ForbiddenAreasFilter;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Description: MqttWarningModel
 *		Used for storing the calculation information and preparing for the result
 * @see WarningSerializer for serialization process
 * @author Yong Wu (y.wu.1@campus.tu-berlin.de)
 */
@JsonSerialize(using = WarningSerializer.class)
public class MqttWarningModel {
	public String timestamp;
	public Orientation orientation;
	public String vehicleId;
	public Position position;
	public WarningType warningType;
	public double velocity;
	public String areaName;

	public MqttWarningModel(){

	}

	public MqttWarningModel(MqttWarningModel mdl) {
		this.timestamp = "";
		this.orientation = new Orientation(mdl.orientation);
		this.vehicleId = "";
		this.position = new Position(mdl.position);
		this.warningType = mdl.warningType;
		this.velocity = mdl.velocity;
		this.areaName= mdl.areaName;
	}

	public MqttWarningModel(String timestamp, double confidence, Orientation orientation, String vehicleId, Position position, int warningType) {
		this.timestamp = timestamp;
		this.orientation = new Orientation(orientation);
		this.vehicleId = vehicleId;
		this.position = new Position(position);
		this.warningType = new WarningType(warningType);
	}

	public MqttWarningModel(MqttMessageModel mdl, final ForbiddenAreasFilter filter) { // Forbidden Area Warning
		this.timestamp = mdl.timestamp;
		this.orientation = new Orientation(mdl.orientation);
		this.position = new Position(mdl.position);
		this.vehicleId = mdl.vehicleId;
		this.warningType = new WarningType(1);
		this.velocity = mdl.velocity;
		this.areaName = filter.getAreaName(mdl);
	}

	public MqttWarningModel(MqttMessageModel mdl) { // Low speed Warning
		this.timestamp = mdl.timestamp;
		this.orientation = new Orientation(mdl.orientation);
		this.position = new Position(mdl.position);
		this.vehicleId = mdl.vehicleId;
		this.warningType = new WarningType(2);
		this.velocity = mdl.velocity;
		this.areaName = "";
	}

	public MqttWarningModel(MqttMessageModel mdl, WarningType warningType) { // Reserved for Other Warning
		this.timestamp = mdl.timestamp;
		this.orientation = new Orientation(mdl.orientation);
		this.position = new Position(mdl.position);
		this.vehicleId = mdl.vehicleId;
		this.warningType = new WarningType(warningType.typeNo);
		this.velocity = mdl.velocity;
		this.areaName = "";
	}

}
