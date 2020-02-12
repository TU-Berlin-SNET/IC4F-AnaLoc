package de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.model;

import de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.util.DateUtil;
import org.springframework.boot.configurationprocessor.json.JSONObject;


import java.awt.geom.Point2D;
import java.util.HashMap;


/**
 * Class Location is deprecated and will not be used.
 * @deprecated
 */
@Deprecated
public class Location {
	public Point2D.Double point;
	public String timeStamp;

	public Location() {
	}

	public Location(double x, double y, String timeStamp) {
		this.point = new Point2D.Double();
		this.point.setLocation(x,y);
		this.setTimeStamp(timeStamp);
	}

	public Location(Position position, String timeStamp) {
		this.point = new Point2D.Double(position.x, position.y);
		this.setTimeStamp(timeStamp);
	}

	public Location(MqttMessageModel msgModel) {
		this.point = new Point2D.Double(msgModel.position.x, msgModel.position.y);
		this.timeStamp = msgModel.timestamp;
	}

	public double getX(){
		return this.point.getX();
	}

	public double getY(){
		return this.point.getY();
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getX()).append(",");
		sb.append(this.getY()).append(",");
		sb.append(this.getTimeStamp());
		return sb.toString();
	}

	public static Location fromString(String message) {
		String[] tokens = message.split(",");

		if (tokens.length != 3) {
			throw new RuntimeException("Invalid location object");
		}

		Location location = new Location();

		try {
			location.point.setLocation(Double.parseDouble(tokens[0]),Double.parseDouble(tokens[1]));
			location.setTimeStamp(tokens[2]);
		} catch (NumberFormatException nfe) {
			throw new RuntimeException("Invalid distance event record " + message, nfe);
		}

		return location;
	}

	@Deprecated
	public static Location fromCSVString(String line, String dataPath) {
		String[] tokens = line.split(",");
		String formatDate = tokens[2] + ":" + tokens[3];
		if (tokens.length != 4) {
			System.out.println(line);
			throw new RuntimeException("Invalid CSV record");
		}
		Location location = new Location();
		try {
			location.point.setLocation(Double.parseDouble(tokens[0]),Double.parseDouble(tokens[1]));
			location.setTimeStamp(formatDate);
		} catch (NumberFormatException nfe) {
			throw new RuntimeException("Invalid distance event record " + line, nfe);
		}
		return location;
	}

	public String toJSONString(boolean useCurrentTimeStamp) {
		HashMap<String,String> map = new HashMap<String, String>();
		try {
			map.put("x",Double.toString(this.getX()));
			map.put("y",Double.toString(this.getY()));
			if (useCurrentTimeStamp)
				map.put("timeStamp",Long.toString(DateUtil.getCurrentUnixTimeStamp()));
			else
				map.put("timeStamp", this.getTimeStamp());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		JSONObject obj = new JSONObject(map);
		return obj.toString();
	}
}
