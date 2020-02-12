package de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.connector;

import java.io.Serializable;

// Forked from https://github.com/ajiniesta/flink-connector-mqtt
// License Apache 2.0

public class MqttMessage implements Serializable {

    private static final long serialVersionUID = -4673414704450588069L;

    private String topic;
    private String payload;

    public MqttMessage() {
    }

    public MqttMessage(String topic, String payload) {
        super();
        this.topic = topic;
        this.payload = payload;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((payload == null) ? 0 : payload.hashCode());
        result = prime * result + ((topic == null) ? 0 : topic.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MqttMessage other = (MqttMessage) obj;
        if (payload == null) {
            if (other.payload != null)
                return false;
        } else if (!payload.equals(other.payload))
            return false;
        if (topic == null) {
            return other.topic == null;
        } else return topic.equals(other.topic);
    }

    @Override
    public String toString() {
        return "MqttMessage [topic=" + topic + ", payload=" + payload + "]";
    }

}
