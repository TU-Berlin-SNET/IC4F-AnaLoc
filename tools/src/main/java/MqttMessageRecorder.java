/**
 * This Code is copied from https://gist.github.com/benedekh/697b3507e0b3f890f105
 * <p>
 * <p>
 * Modified by Yong Wu to Record the MQTT Messages into a text file
 */

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Scanner;

public class MqttMessageRecorder implements MqttCallback {
    static Writer output;
    static String my_file_name = "";

    public static void main(String[] args) {
        String topic = "/arena2036/#";
        int qos = 2;
        String broker = "tcp://localhost:1883";
        String clientId = "JavaMQTTRecorder";
        MemoryPersistence persistence = new MemoryPersistence();


        try {
            System.out.print("Please enter filename:  ");
            Scanner in = new Scanner(System.in);
            my_file_name = in.next();
            my_file_name = "./" + my_file_name + ".txt";
            MqttAsyncClient sampleClient = new MqttAsyncClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            sampleClient.setCallback(new MqttMessageRecorder());
            System.out.println("Connecting to broker: " + broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
            Thread.sleep(1000);
            sampleClient.subscribe(topic, qos);
            System.out.println("Subscribed");
        } catch (Exception me) {
            if (me instanceof MqttException) {
                System.out.println("reason " + ((MqttException) me).getReasonCode());
            }
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }

    public void connectionLost(Throwable arg0) {
        System.err.println("connection lost");

    }

    public void deliveryComplete(IMqttDeliveryToken arg0) {
        System.err.println("delivery complete");
    }

    public void messageArrived(String topic, MqttMessage message) throws Exception {
        output = new BufferedWriter(new FileWriter(my_file_name, true));
        System.out.println(topic + " > " + new String(message.getPayload()) + "\n");
        output.append(topic).append(" > ").append(new String(message.getPayload())).append("\n");
        output.close();
    }

}