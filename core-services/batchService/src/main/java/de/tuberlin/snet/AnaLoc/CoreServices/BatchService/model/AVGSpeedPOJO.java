package de.tuberlin.snet.AnaLoc.CoreServices.BatchService.model;


import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

/**
 * Description:	 Average speed POJO.
 * Using mapping, the class must be annotated with "@Measurement"
 */
@Measurement(name = "aaa")  // here can be arbitrary value because we read from config file in class CallableQueryEndpoint
public class AVGSpeedPOJO {
  /**
   * Ignored Column "time", because it only represents the query time
   */
  //@Column(name = "time")
  //public Instant time;

  /**
   * The Average speed.
   * Using mapping, the property must be annotated with "@Column"
   */
  @Column(name = "averageSpeed")
  public double averageSpeed;

}
