package de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.connector;

import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

// Forked from https://github.com/ajiniesta/flink-connector-mqtt
// License Apache 2.0

public class MqttSource extends RichSourceFunction<MqttMessage> {

    private static final long serialVersionUID = 6688710373425613856L;
    private String host;
    private int port;
    private String topic;
    private QoS qos;
    private boolean retain;
    private volatile boolean isRunning = true; // add this for cancelling.

    public MqttSource(String host, String topic) {
        this(host, 1883, topic, QoS.AT_LEAST_ONCE, false);
    }

    public MqttSource(String host, int port, String topic) {
        this(host, port, topic, QoS.AT_LEAST_ONCE, false);
    }

    public MqttSource(String host, int port, String topic, QoS qos, boolean retain) {
        this.host = host;
        this.port = port;
        this.topic = topic;
        this.qos = qos;
        this.retain = retain;
    }


    @Override
    public void cancel() {
        isRunning = false;
        //  This is implement by Yong Wu according to the java doc here:
        //  https://ci.apache.org/projects/flink/flink-docs-master/api/java/org/apache/flink/streaming/api/functions/source/SourceFunction.html#cancel--
    }

    @Override
    public void run(SourceContext<MqttMessage> sourceContext) throws Exception {
        MQTT mqtt = new MQTT();
        mqtt.setHost(host, port);
        BlockingConnection blockingConnection = mqtt.blockingConnection();
        blockingConnection.connect();

        byte[] qoses = blockingConnection.subscribe(new Topic[]{new Topic(topic, qos)});

        while (isRunning && blockingConnection.isConnected()) {
            Message message = blockingConnection.receive();
            MqttMessage mmsg = new MqttMessage(message.getTopic(), new String(message.getPayload()));
            message.ack();
            sourceContext.collect(mmsg);
        }
        blockingConnection.disconnect();
    }

}
