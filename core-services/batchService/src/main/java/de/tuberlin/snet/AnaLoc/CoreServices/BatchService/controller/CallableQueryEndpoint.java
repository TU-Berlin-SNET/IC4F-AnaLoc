package de.tuberlin.snet.AnaLoc.CoreServices.BatchService.controller;

import de.tuberlin.snet.AnaLoc.CoreServices.BatchService.db.InfluxDBUtils;
import de.tuberlin.snet.AnaLoc.CoreServices.BatchService.model.AVGSpeedPOJO;
import de.tuberlin.snet.AnaLoc.CoreServices.BatchService.model.web.HeatmapRequest;
import org.influxdb.annotation.Measurement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.influxdb.querybuilder.BuiltQuery.QueryBuilder.*;
import static org.influxdb.querybuilder.FunctionFactory.sum;

/**
 * Description: CallableQueryEndpoint is a REST Controller used for handling heatmap request
 * 		it will query the InfluxDB and return result asynchronously
 * @author Yong Wu (y.wu.1@campus.tu-berlin.de)
 */
@RestController
public class CallableQueryEndpoint {

	@Value(value = "${map.xMin}")
	private Double xMin;

	@Value(value = "${map.xMax}")
	private Double xMax;

	@Value(value = "${map.yMin}")
	private Double yMin;

	@Value(value = "${map.yMax}")
	private Double yMax;

	@Value("${influxDBconf.dbName}")
	private String dbName;

	@Value(value = "${influxDBconf.measurement}")
	private String dataFrame;

	@Value(value = "${influxDBconf.positionX}")
	private String positionX;

	@Value(value = "${influxDBconf.positionY}")
	private String positionY;

	@Value(value = "${influxDBconf.vehicleId}")
	private String vehicleId;

	@Value(value = "${influxDBconf.deltaDistance}")
	private String deltaDistance;

	@Value(value = "${influxDBconf.deltaTime}")
	private String deltaTime;

	@Value(value = "${influxDBconf.speedFactor}")
	private long speedFactor;


	/**
	 * The Influx db utils from package db.
	 */
	final private InfluxDBUtils influxDBUtils;

	final private HttpServletRequest request;

	private static final Logger LOG = LoggerFactory.getLogger(CallableQueryEndpoint.class);

	@Autowired
	public CallableQueryEndpoint(InfluxDBUtils influxDBUtils, HttpServletRequest request) {
		this.influxDBUtils = influxDBUtils;
		this.request = request;
	}

	private static String getIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip.contains(",")) {
			return ip.split(",")[0];
		} else {
			return ip;
		}
	}

	/**
	 * Description: Heatmap callable endpoint.
	 *
	 * @param reqObj the req obj
	 * @return the callable. Return Callable in order to react asynchronously.
	 */
	@RequestMapping(value="/heatmap") //,method= RequestMethod.GET
	@ResponseBody
	public Callable<Object> heatmap(@RequestBody final HeatmapRequest reqObj) {
		Callable<Object> result = (() -> {
			try {
				if (reqObj.rowCounts < 1 || reqObj.colCounts < 1 || reqObj.rowCounts > 15 || reqObj.colCounts>15) {
					throw new ArithmeticException("Please check the request: row count or column count is not in the range [1,15].");
				}
				//LOG.info("xMax:{}",xMax);
				//LOG.info("yMax:{}",yMax);
				Double xTick = (xMax - xMin) / reqObj.rowCounts;
				Double yTick = (yMax - yMin) / reqObj.colCounts;
				String sql;
				HashMap<String, List<List<Double>>> map = new HashMap<>();
				if (!influxDBUtils.isDbExist()) {
					throw new RuntimeException(String.format("Database %s does not exist!", influxDBUtils.getDbName()));
				}
				List<List<Double>> matrix = new LinkedList<>();
				if (reqObj.vehicleIds.isEmpty()) {
					for (Double x = xMin; x <= (xMax - xTick); x += xTick) {
						List<Double> row = new LinkedList<>();
						for (Double y = yMin; y <= (yMax - yTick); y += yTick) {
							sql = select(op(op(sum(deltaDistance), "/", sum(deltaTime)), "*", speedFactor)).as("averageSpeed").from(dbName, dataFrame)
									.where(gte(positionX, x))
									.and(lte(positionX, x + xTick))
									.and(gte(positionY, y))
									.and(lte(positionY, y + yTick))
									.and(gte("time", reqObj.startTime))
									.and(lte("time", reqObj.endTime))
									.getCommand();
							LOG.info(sql);
							List<AVGSpeedPOJO> cell = influxDBUtils.gets(sql, AVGSpeedPOJO.class);
							//LOG.info(cell.toString());
							if (!cell.isEmpty()) {
								row.add(cell.get(0).averageSpeed);
							} else {
								row.add(0.0);
							}
						}
						matrix.add(row);
					}
					map.put("all", matrix);
				} else {
					for (String vehicle : reqObj.vehicleIds) {
						matrix = new LinkedList<>();
						sql = select().count(positionX).from(dbName, dataFrame)
								.where(eq(vehicleId, vehicle)).getCommand();
						LOG.info(sql);
						if (!influxDBUtils.checkVehicleExist(sql)) {
							map.put(vehicle, matrix);
							continue;
						}
						for (Double x = xMin; x <= (xMax - xTick); x += xTick) {
							List<Double> row = new LinkedList<>();
							for (Double y = yMin; y <= (yMax - yTick); y += yTick) {
								sql = select(op(op(sum(deltaDistance), "/", sum(deltaTime)), "*", speedFactor)).as("averageSpeed").from(dbName, dataFrame)
										.where(eq(vehicleId, vehicle))
										.and(gte(positionX, x))
										.and(lte(positionX, x + xTick))
										.and(gte(positionY, y))
										.and(lte(positionY, y + yTick))
										.and(gte("time", reqObj.startTime))
										.and(lte("time", reqObj.endTime))
										.getCommand();
								LOG.info(sql);
								List<AVGSpeedPOJO> cell = influxDBUtils.gets(sql, AVGSpeedPOJO.class);
								//LOG.info(cell.toString());
								if (!cell.isEmpty()) {
									row.add(cell.get(0).averageSpeed);
								} else {
									row.add(0.0);
								}
							}
							matrix.add(row);
						}
						map.put(vehicle, matrix);
					}
				}
				return map;
			} catch(Exception err) {
				return err.getMessage();
			}
		});
		LOG.info(
			"New request from IP: " + getIpAddress(request)
			+ " for matrix [" + reqObj.rowCounts + "," + reqObj.colCounts + "]"
			+ " of vehicleIds " + reqObj.vehicleIds.toString()
			+ " from " + reqObj.startTime + " to " + reqObj.endTime
		);
		//System.out.println("ready for another request");
		return result;
	}

	/**
	 * Description: This function is used to replace the Measurement name of class AVGSpeedPOJO
	 * @throws Exception NullPointerException
	 */
	@PostConstruct
	void changeAnnotation() throws Exception{
		final Measurement oldMeasurement = (Measurement) AVGSpeedPOJO.class.getAnnotations()[0];
		Measurement newMeasurement = new Measurement(){

			@Override
			public Class<? extends Annotation> annotationType() {
				return oldMeasurement.annotationType();
			}

			@Override
			public String name() {
				return dataFrame;
			}

			@Override
			public String database() {
				return oldMeasurement.database();
			}

			@Override
			public String retentionPolicy() {
				return oldMeasurement.retentionPolicy();
			}

			@Override
			public TimeUnit timeUnit() {
				return oldMeasurement.timeUnit();
			}

		};
		Method method = Class.class.getDeclaredMethod("annotationData",null);
		method.setAccessible(true);
		Object annotationData = method.invoke(AVGSpeedPOJO.class);
		Field field = annotationData.getClass().getDeclaredField("annotations");
		field.setAccessible(true);
		Map<Class<? extends Annotation>,Annotation> annotations = (Map<Class<? extends Annotation>, Annotation>) field.get(annotationData);
		annotations.put(Measurement.class,newMeasurement);
	}
}
