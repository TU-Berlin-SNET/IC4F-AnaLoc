package de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.processor;

import de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.connector.MqttMessage;
import de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.connector.MqttSink;
import de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.connector.MqttSource;
import de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.filter.ForbiddenAreasFilter;
import de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.filter.LowSpeedFilter;
import de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.function.VelocityCalculation;
import de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.mapper.JSONEncoder;
import de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.mapper.JSONParser;
import de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.model.MqttMessageModel;
import de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.model.MqttWarningModel;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.runtime.executiongraph.restart.RestartStrategy;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


/**
 * Description: This class use MQTT connector to connect Flink to MQTT Broker
 * 		use Flink to filter message and then write the non negative
 *    value from the message to text file
 * @author Yong Wu (y.wu.1@campus.tu-berlin.de)
 * @version 0.0.2
 * @since 0.0.1
 *
 */
@Component
public class DataPointProcessor {

	@Autowired
	private ApplicationContext appContext;

	private static final Logger LOG = LoggerFactory.getLogger(DataPointProcessor.class);


	@Value(value = "${mqtt.hostname}")
	private String host;

	@Value(value = "${mqtt.input.topic}")
	private String inputTopic;

	@Value(value = "${mqtt.output.topic.forbiddenArea}")
	private String areaWarning;

	@Value(value = "${mqtt.output.topic.lowSpeed}")
	private String velocityWarning;

	@Value(value = "${filter.configuration.speed.threshold}")
	private String lowSpeedThreshold;

	@Value(value = "${filter.configuration.location}")
	private String mapFileName;

	/**
	 * Description: In this function, the processing steps of stream processing
	 * is defined. And the pipeline is setup.
	 *
	 * @param args
	 * @throws Exception
	 */
	public void run(String[] args) throws Exception {
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setRestartStrategy(RestartStrategies.failureRateRestart(
				3, // max failures per interval
				Time.of(5, TimeUnit.MINUTES), // time interval for measuring failure rate
				Time.of(10,TimeUnit.SECONDS)  // delay
		));

		LOG.info("##### InputTopic: {}", inputTopic);
		LOG.info("##### areaWarning: {}", areaWarning);
		LOG.info("##### velocityWarning: {}", velocityWarning);
		LOG.info("##### Low Speed Threshold: {}", Double.parseDouble(lowSpeedThreshold));
		LOG.info("##### Determined location of JobManager log file: {}", "./job-logger");
		LOG.info("##### Location of map file: {}", mapFileName);

		try {

			DataStream<MqttMessage> testStream = env
					.addSource(new MqttSource(host, inputTopic)).name("MQTT Source");

			DataStream<MqttMessageModel> vehicleDataStream;
				vehicleDataStream = testStream
						.map(MqttMessage::getPayload)
						.flatMap(new JSONParser()).name("JSONParser");	// String -> MqttMessageModel;

			/*
			 *  Post velocity back to MQTT Broker with topic "velocity"
			 */
			double threshold=Double.parseDouble(lowSpeedThreshold);
			vehicleDataStream
					.keyBy("vehicleId")
					.countWindow(2)
					.aggregate(new VelocityCalculation())
					.filter(new LowSpeedFilter(threshold))
					.map(MqttWarningModel::new)
					.map(new JSONEncoder())
					.addSink(new MqttSink<>(host, velocityWarning))
					.name("Velocity Sink")
					.setParallelism(1);

			// get filter bean from spring
			ForbiddenAreasFilter forbiddenAreasFilter = appContext.getBean(ForbiddenAreasFilter.class);
			vehicleDataStream
					.filter(forbiddenAreasFilter)
					.map(x -> new MqttWarningModel(x,forbiddenAreasFilter))
					.map(new JSONEncoder())
					.addSink(new MqttSink<>(host, areaWarning))
					.name("Area Sink")
					.setParallelism(1);
			/*
			 * To save the execution plan to a textfile named "ExecutionPlan.txt" in folder "job-logger"
			 * The path of "/logger" in container is mounted to "./job-logger" of localhost in "docker-compose.yml"
			 */
			try {
				File logFile = new File("/logger/ExecutionPlan.txt");
				if (!logFile.getParentFile().exists()) {
					boolean success = logFile.getParentFile().mkdirs();
					if (!success) {
						throw new IOException("Can not mkdir of /logger.");
					}
				}
				if (!logFile.exists()) {
					boolean success = logFile.createNewFile();
					if (!success) {
						throw new IOException("Can not create file ExecutionPlan.txt");
					}
				}

				BufferedWriter writer = new BufferedWriter(new FileWriter(logFile));
				/*
				 * You can paste the content of file to this website:
				 *       https://flink.apache.org/visualizer/
				 * to see the graphical execution plan of current task.
				 */
				writer.write(env.getExecutionPlan());
				writer.close();
			} catch (IOException err) {
				LOG.error(err.getMessage());
			}

		} catch (Exception err) {
			LOG.error(err.getMessage());//err.printStackTrace();
		}

		try {
			env.execute("DataPointProcessor");
		} catch (Exception e) {
			LOG.error(e.getMessage());//e.printStackTrace();
		}
	}

}
