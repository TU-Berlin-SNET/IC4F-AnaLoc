package model;

import java.math.BigInteger;

public class MqttMessageModel {
    public String timestamp;
    public double confidence;
    public Orientation orientation;
    public String vehicleId;
    public Position position;

    public MqttMessageModel() {
        this.timestamp = "";
        this.confidence = 0;
        this.orientation = new Orientation();
        this.vehicleId = "";
        this.position = new Position();

    }

    public MqttMessageModel(String timestamp, double confidence, Orientation orientation, String vehicleId, Position position) {
        this.timestamp = timestamp;
        this.confidence = confidence;
        this.orientation = new Orientation(orientation);
        this.vehicleId = vehicleId;
        this.position = new Position(position);
    }

    public static long timeDiff(MqttMessageModel m1, MqttMessageModel m2) {
        return Math.abs(new BigInteger(m1.timestamp).subtract(new BigInteger(m2.timestamp)).longValue());
    }

    public static double distanceDiff(MqttMessageModel m1, MqttMessageModel m2) {
        return Math.sqrt((m2.position.x-m1.position.x)*(m2.position.x-m1.position.x)+(m2.position.y-m1.position.y)*(m2.position.y-m1.position.y));
    }
    @Override
    public String toString() {
        return "dataFrame,vehicleId=" + this.vehicleId + " confidence=" + this.confidence + "," + this.orientation.toString() + "," + this.position.toString() + " " + this.timestamp;
    }

    public String toBatchString(MqttMessageModel m2){
        return "dataFrame,vehicleId=" + this.vehicleId + " confidence=" + this.confidence + "," + this.orientation.toString() + "," + this.position.toString() + ",delta_t="+timeDiff(this, m2) +",delta_distance="+ distanceDiff(this,m2) +" " + this.timestamp;
    }
}
