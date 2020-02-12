import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Scanner;
import model.Location;
import util.DateUtil;


/*
 *
 *   ClassName: MqttMessageSender
 *   Description: publish MQTT message "1234" to RabbitMQ MQTT broker
 *
 */
public class MqttDataPointSender implements Runnable {
    private Thread t;
    private String threadName;

    private String filename;

    private int speedFactor = 1000;

    private transient BufferedReader reader;
    private transient InputStream positionStream;

    private final int HEADER_OFFSET = 1;


    public MqttDataPointSender(String name) {
        threadName = name;
        System.out.println("Creating" + threadName);
    }

    public void generateLocationStream (MqttClient sampleClient, int qos, String topic) throws Exception{
        String line;
        Location location;
        int waitingTime = 0;
        long servingTime = DateUtil.getCurrentUnixTimeStamp();
        //skip first line for header
        for (int i = 0; i < HEADER_OFFSET; i++) {
            try {
                if (reader.ready()) {
                    reader.readLine();
                }
            } catch (IOException io) {
                io.printStackTrace();
            }
        }

        try {

            while (reader.ready() && (line = reader.readLine()) != null) {

                location = Location.fromCSVString(line, filename);

                /* Here mark and reset to keep this line still in the reader */
                reader.mark(0);
                line = reader.readLine();


                sendMqttMessage (location, sampleClient, qos, topic);

                if (line == null) { // done
                    System.exit(0);
                }

                reader.reset();

                Location nextLocation = Location.fromCSVString(line, filename);
                long timeDiff = nextLocation.getTimeStamp() - location.getTimeStamp();
                try {
                    Thread.sleep(timeDiff * 1000 / speedFactor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private void sendMqttMessage (Location location, MqttClient sampleClient, int qos, String topic) throws Exception {
        MqttMessage message = new MqttMessage(location.toJSONString(true).getBytes());
        message.setQos(qos);
        sampleClient.publish(topic, message);       // publish message
    }

    @Override
    public void run() {
        System.out.println("Running " +  threadName );
        String topic = "/arena2036/activeshuttle/position";             // message topic
        String content      = "1234";                   // message content
        int qos             = 1;                        // QoS-level: exactly one
        String broker       = "tcp://localhost:1883";   // localhost:1883 is the default port of RabbitMQ
        String clientId     = "JavaSample";
        String password     = "guest";                  // default user of RabbitMQ
        String userName     = "guest";                  // default password of RabbitMQ
        MemoryPersistence persistence = new MemoryPersistence();

        System.out.print("Please enter filename:  ");
        Scanner in = new Scanner(System.in);
        filename = in.next();
        filename = "./"+filename+".csv";

        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName(userName);
            connOpts.setPassword(password.toCharArray());
            System.out.println("Connecting to broker: "+broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");

            positionStream = new FileInputStream(filename);
            reader = new BufferedReader(new InputStreamReader(positionStream, StandardCharsets.UTF_8));
            generateLocationStream(sampleClient, qos, topic);

            this.reader.close();
            this.reader = null;
            this.positionStream.close();
            this.positionStream = null;

            sampleClient.disconnect();
            System.out.println("Disconnected");
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        System.out.println("Starting " +  threadName );
        if (t == null) {
            t = new Thread (this, threadName);
            t.start ();
        }
    }

    public static void main(String[] args) {
        MqttDataPointSender sender = new MqttDataPointSender("Message-sender-1");
        sender.start();
    }
}
