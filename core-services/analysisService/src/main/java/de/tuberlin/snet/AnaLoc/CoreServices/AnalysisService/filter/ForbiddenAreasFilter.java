package de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.model.MqttMessageModel;
import de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.model.Position;
import de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.util.PathSet;
import org.apache.flink.api.common.functions.FilterFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;


/**
 * Description:	ForbiddenAreasFilter is designed to read the converted json map and transform
 * 		it to ploygon forbidden zones
 * @author Yong Wu (y.wu.1@campus.tu-berlin.de)
 * @see ForbiddenPolygonFilter
 * @see PathSet
 */

// test:
// {"timestamp": "1540824931761162996",
//  "confidence": -1,
//  "orientation": {
//     "y": 0.0 ,
//     "x": 0.0,
//     "z": -0.031759755379824214,
//     "w": 0.9994955317249866
//  },
//  "vehicleId": "ActiveShuttle",
//  "position": {"y": -5.0, "x": 110.0, "z": 0.0}}
// expected result:
// Forbidden Area 7

@Component
@Scope("prototype") // use this for multiple instances
public class ForbiddenAreasFilter implements FilterFunction<MqttMessageModel>, Serializable {
	private static final long serialVersionUID = 1L;

	@Value("${filter.configuration.location}")
	String fileName;// = "/json/arena2036.json";

	@Autowired
	transient ResourceLoader resourceLoader; // because it is unserializable, use transient to prevent from serialization
	// See https://github.com/square/moshi/issues/94

	ArrayList<ForbiddenPolygonFilter> filters;
	ForbiddenPolygonFilter border;
	ArrayList<String> labels;

	/**
	 * Description: init is the most important function of this class.
	 * 		"@PostConstruct" is used to make sure that fileName not null.
	 * 		Here reads the json map file and try to reconstruct the polygons
	 * 		It firstly finds the Border layer and any layers whose name contains "ForbiddenZone"
	 * 		in the json File. And then save them into an ArrayList of the PathSet, and construct
	 * 	 	ForbiddenPolygonFilter accodingly.
	 */
	@PostConstruct
	private void init(){
		JsonNode entities;
		JsonNode layers;
		ArrayList<PathSet> pathsForPolygon;
		filters = new ArrayList<ForbiddenPolygonFilter>();
		try {
			pathsForPolygon = new ArrayList<PathSet>();
			labels = new ArrayList<String>();
			//InputStream is = TypeReference.class.getResourceAsStream(fileName);

			// use this to make it possible to read configuration from both class path and file system
			Resource resource = resourceLoader.getResource(fileName);
			InputStream is = resource.getInputStream();
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode tree = objectMapper.readTree(is);
			layers = tree.path("tables").path("layer").path("layers");
			System.out.println("Layers:");
			for (final JsonNode layer : layers) {
				String layerName = layer.get("name").asText();
				if (layerName.compareTo("Border") == 0 || layerName.contains("ForbiddenZone")) {
					labels.add(layerName);
					System.out.println(layerName);
					pathsForPolygon.add(new PathSet());
				}
			}
			if (layers.isMissingNode()) throw new IOException("JSON data file is not correct! layers are missing");

			entities = tree.path("entities");

			if (entities.isArray()) {
				for (final JsonNode line : entities) {
					PathSet pathSet = pathsForPolygon.get(labels.indexOf(line.get("layer").asText()));
					final JsonNode points = line.get("vertices");
					Point2D[] p = new Point2D.Double[2];
					for (int i = 0; i < 2; i++) {
						final JsonNode point = points.get(i);
						p[i] = new Point2D.Double(point.get("x").asDouble(), point.get("y").asDouble());
					}
					Line2D line2D = new Line2D.Double(p[0], p[1]);
					pathSet.add(line2D);
				}
				try {
					for (int i = 0; i < pathsForPolygon.size(); i++) {
						PathSet path = pathsForPolygon.get(i);
						LinkedList<Line2D> list = path.getCorrectPath();
						Line2D[] array = list.toArray(new Line2D[list.size()]);
						if (i == 0)
							border = new ForbiddenPolygonFilter(array);
						else {
							filters.add(new ForbiddenPolygonFilter(array));
							ForbiddenPolygonFilter polygonFilter = new ForbiddenPolygonFilter(array);
						}
					}
					System.out.println("########ForbiddenPolygonFilter.size:" + filters.size());
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			} else {
				throw new IOException("JSON data file is not correct!");
			}

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}


	/**
	 * Description:  This is the default constructor.
	 */
	public ForbiddenAreasFilter() {

	}

	/**
	 * Description:  This is the filter function, which receives location and judge the relationship
	 * between location and border polygon and forbiddenZones
	 * @param model Data point to check
	 * @return true   when the location is outside the border zone, or inside the forbidden zones
	 *         false  when the location is inside the border zone and outside the forbidden zones
	 */
	@Override
	public boolean filter(MqttMessageModel model) {
		Position position = model.position;
		if (!border.filter(position)) return true;

		for (ForbiddenPolygonFilter filter : filters) {
			if (filter.filter(position)) return true;
		}
		return false;
	}

	/**
	 * Description: function getAreaName is used to get the ForbiddenAreaName of the given point
	 * @param model the givem data point
	 * @return
	 * 		areaName when found
	 * 		"Out of border." when out side the border
	 * 		otherwise "areaName not found"
	 */
	public String getAreaName(MqttMessageModel model){
		Position position = model.position;
		if (!border.filter(position)) return "Out of border.";
		for (ForbiddenPolygonFilter filter : filters) {
			if (filter.filter(position)) {
				return labels.get(filters.indexOf(filter));
			}
		}
		return "areaName not found";
	}
}
