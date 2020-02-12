package de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.model;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonGenerator;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * Description:  WarningSerializer
 * 		Used for generating the correct warning format of json object
 * 		Use jackson for functionality.
 * @author Yong Wu (y.wu.1@campus.tu-berlin.de)
 */
public class WarningSerializer extends StdSerializer<MqttWarningModel> {

	public WarningSerializer(){
		this(null);
	}
	public WarningSerializer(Class<MqttWarningModel> mdl){
		super(mdl);
	}

	@Override
	public void serialize(MqttWarningModel model, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
		jsonGenerator.writeStartObject();
			jsonGenerator.writeStringField("timeStamp",model.timestamp);
			jsonGenerator.writeStringField("vehicleId",model.vehicleId);
			jsonGenerator.writeNumberField("warningType",model.warningType.typeNo);
			jsonGenerator.writeObjectFieldStart("warningInfo");
				jsonGenerator.writeStringField("message",model.warningType.message());
				switch (model.warningType.typeNo){
					case 1: jsonGenerator.writeStringField("areaNo", model.areaName);break;
					case 2: jsonGenerator.writeNumberField("velocity",model.velocity);break;
				}
			jsonGenerator.writeEndObject();
			jsonGenerator.writeObjectFieldStart("position");
				jsonGenerator.writeNumberField("x",model.position.x);
				jsonGenerator.writeNumberField("y",model.position.y);
			jsonGenerator.writeEndObject();
		jsonGenerator.writeEndObject();
	}
}
