package de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.mapper;

import de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.model.MqttMessageModel;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: The Json parser mapper.
 * 		which ignores the error mapping results
 * @author Yong Wu (y.wu.1@campus.tu-berlin.de)
 */
public class JSONParser implements FlatMapFunction<String, MqttMessageModel> {
	private static final long serialVersionUID = -8356609215803763439L;
	private static final Logger LOG = LoggerFactory.getLogger(JSONParser.class);
	private ObjectMapper mapper = new ObjectMapper();

	/**
	 * Use flatmap for error handling
	 * @param value json string from the queue
	 * @param collector	collector for MqttMessageModel object when successfully mapped
	 */
	@Override
	public void flatMap(String value, Collector<MqttMessageModel> collector) {
		//LOG.info(value);
		try{
			MqttMessageModel mdl = mapper.readValue(value, MqttMessageModel.class);
			collector.collect(mdl);
		}catch(Exception e){
			LOG.error("JSON Parse Failure:"+e.getMessage());
		}
	}
}
