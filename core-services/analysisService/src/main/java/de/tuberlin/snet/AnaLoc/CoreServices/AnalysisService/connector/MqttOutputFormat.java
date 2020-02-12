package de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.connector;

import java.io.IOException;

import org.apache.flink.api.common.io.RichOutputFormat;
import org.apache.flink.configuration.Configuration;
import org.fusesource.mqtt.client.BlockingConnection;

import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;

// Forked from https://github.com/ajiniesta/flink-connector-mqtt
// License Apache 2.0

//RichOutputFormat vs RichSinkFunction: http://mail-archives.apache.org/mod_mbox/flink-user/201607.mbox/%3CCAN0XJzNVEzzMNbi59jp32xTwHFOqdCfz+32rWSoN9vHpaYSitA@mail.gmail.com%3E
//Used in Flink batch program, output DataSet, not DataStream
public class MqttOutputFormat<IT> extends RichOutputFormat<IT> {

    private static final long serialVersionUID = -260008582961487797L;

    private transient BlockingConnection blockingConnection;
    private transient MQTT mqtt;
    private String topic;
    private QoS qos;
    private boolean retain;
    private String host;
    private int port;

    public MqttOutputFormat(String host, String topic) {
        this(host, 1883, topic, QoS.AT_LEAST_ONCE, false);
    }

    public MqttOutputFormat(String host, int port, String topic) {
        this(host, port, topic, QoS.AT_LEAST_ONCE, false);
    }

    public MqttOutputFormat(String host, int port, String topic, QoS qos, boolean retain) {
        this.host = host;
        this.port = port;
        this.topic = topic;
        this.qos = qos;
        this.retain = retain;
    }

    @Override
    public void close() throws IOException {
        System.out.println("closing....");
        try {
            if (blockingConnection != null) {
                blockingConnection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void configure(Configuration arg0) {
        System.out.println("configuring output format....");
    }

    @Override
    public void open(int arg0, int arg1) throws IOException {
        System.out.println("opening...." + arg0 + " " + arg1);
        mqtt = new MQTT();
        try {
            mqtt.setHost(host, port);
            blockingConnection = mqtt.blockingConnection();
            blockingConnection.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void writeRecord(IT record) throws IOException {
        System.out.println("writing record...." + record);
        byte[] payload = record.toString().getBytes();
        try {
            blockingConnection.publish(topic, payload, qos, retain);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
