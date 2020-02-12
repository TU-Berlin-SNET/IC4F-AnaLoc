package de.tuberlin.snet.AnaLoc.CoreServices.BatchService.model.web;

import java.util.List;

/**
 * Description:	The type Heatmap request POJO
 */
public class HeatmapRequest {
	public List<String> vehicleIds;
	public String startTime; // rfc3339_date_time_string or rfc3339_like_date_time_string
	public String endTime;		// rfc3339_date_time_string or rfc3339_like_date_time_string
	public int rowCounts;
	public int colCounts;
}
