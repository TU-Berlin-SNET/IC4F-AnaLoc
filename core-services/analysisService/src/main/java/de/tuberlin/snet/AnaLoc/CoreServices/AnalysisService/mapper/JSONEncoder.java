package de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.mapper;


import de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.model.MqttWarningModel;
import de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.model.WarningSerializer;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.Version;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Description: The Json encoder mapper.
 * 		Used for mapping MqttWarningModel object to json string
 * @see WarningSerializer is used for serialization
 * @author Yong Wu (y.wu.1@campus.tu-berlin.de)
 */
public class JSONEncoder implements MapFunction<MqttWarningModel,String> {
	private static final long serialVersionUID = -8356609215803763439L;
	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public String map(MqttWarningModel value) throws Exception {
		SimpleModule module =
				new SimpleModule("WarningSerializer", new Version(1, 0, 0, null, null, null));
		module.addSerializer(MqttWarningModel.class, new WarningSerializer());
		mapper.registerModule(module);
		return mapper.writeValueAsString(value);
	}
}
