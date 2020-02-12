package de.tuberlin.snet.AnaLoc.CoreServices.BatchService.db;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Description:	The type Influx db utils.
 * Forked from https://gitee.com/itxsl/example/blob/master/01-example-influxdb/src/main/java/cn/itxsl/repository/InfluxDBRepo.java
 * Modified by Yong Wu
 *
 * @param <T> the type parameter
 */
@Component
public class InfluxDBUtils<T> {
	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * The Influx db.
	 */
	@Autowired
	InfluxDB influxDB;	//This will be configured automatically by Spring Boot


	@Value("${influxDBconf.dbName}")
	private String dbName;

	/**
	 * Insert
	 *
	 * @param obj       the obj
	 * @param tableName the table name
	 */
	public void post(Object obj, String tableName) {

		if(!isDbExist()) {
			QueryResult result = influxDB.query(new Query(String.format("CREATE DATABASE %s",dbName)));
			logger.info(String.format("Database %s does not exist, Creating a new one.",dbName));
		}

		//Get Columns and Names
		Field[] fields = obj.getClass().getDeclaredFields();

		//Create Object to insert
		Point.Builder point = Point.measurement(tableName);
		point = addKeyValue(obj, fields, point);

		//set the insert time
		point.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);

		//Set the Database for the operation
		influxDB.setDatabase(dbName);

		//write
		influxDB.write(point.build());

		//close
		influxDB.close();
	}

	/**
	 * Query
	 *
	 * @param sql    InfluxQL
	 * @param aClass POJO Class for mapping
	 * @return List of POJO
	 */
	public List<T> gets(String sql, Class<T> aClass) {
//    logger.info(sql);	//See the magic sql
//		if(!isDbExist()) {
//			throw new RuntimeException(String.format("Database %s does not exist!",dbName));
//		}

		QueryResult queryResult = influxDB.query(new Query(sql, dbName));
		influxDB.close();
		InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
		return resultMapper.toPOJO(queryResult, aClass); // this is a list
	}

	/**
	 * Check vehicle exist boolean.
	 *
	 * @param sql the sql
	 * @return true when vehicle exists
	 */
	public boolean checkVehicleExist(String sql){
		QueryResult queryResult = influxDB.query(new Query(sql, dbName));
		influxDB.close();
		//System.out.println(queryResult.getResults());
		return queryResult.getResults().get(0).getSeries()!=null;
	}

	/**
	 * Gets db name.
	 *
	 * @return the db name
	 */
	public String getDbName() {
		return dbName;
	}

	/**
	 * Is db exist boolean.
	 *
	 * @return true when db exists.
	 */
	public Boolean isDbExist() {
		QueryResult result = influxDB.query(new Query("SHOW DATABASES"));
		List<List<Object>> databaseNames = result.getResults().get(0).getSeries().get(0).getValues();
		List<String> databases = new ArrayList<>();
		if (databaseNames != null) {
			for (List<Object> database : databaseNames) {
				databases.add(database.get(0).toString());
			}
		}

		for (String databaseName : databases) {
			if (databaseName.trim().equals(dbName)) {
				return true;
			}
		}
		return false;
	}

	private Point.Builder addKeyValue(Object obj, Field[] fields, Point.Builder point) {
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				String fieldName = field.getName();
				String typeName = field.getGenericType().getTypeName();
				int start = typeName.lastIndexOf(".") + 1;
				//Data type
				String type = typeName.substring(start, typeName.length());
				//field value
				Object o = field.get(obj);
				switch (type) {
					case "Instant":
						break;
					case "String":
						point.addField(fieldName, (String) o);
						break;
					case "Integer":
						point.addField(fieldName, (Integer) o);
						break;
					default:
						throw new IllegalArgumentException("invalid parameter");
				}
			} catch (Exception e) {
				logger.error("invalid parameter:{}", e.getMessage());
			}
		}
		return point;
	}
}
