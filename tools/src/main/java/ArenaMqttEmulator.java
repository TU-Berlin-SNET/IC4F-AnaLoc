import model.MqttMessageModel;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import util.DateUtil;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/*
 *
 *   ClassName: ArenaMqttEmulator
 *   Description: Emulate the MQTT Message for shuttle in Arena2036
 *
 *   @author Yong Wu
 *
 */
public class ArenaMqttEmulator implements Runnable {
    private Thread t;
    private String threadName;

    private String filename;

    private int speedFactor = 1;

    private transient BufferedReader reader;
    private transient InputStream positionStream;

    private final int HEADER_OFFSET = 0;
    private ObjectMapper mapper = new ObjectMapper();


    public ArenaMqttEmulator(String name) {
        threadName = name;
        System.out.println("Creating" + threadName);
    }

    public void generateLocationStream(MqttClient sampleClient, int qos, String topic) throws Exception {
        String line;
        //Location location;
        MqttMessageModel msgModel;

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
            line = reader.readLine();
            if (line == null) { // done
                System.exit(0);
            }
            if (reader.ready()) {
                msgModel = mapper.readValue(line, MqttMessageModel.class);
                sendMqttMessage(msgModel, sampleClient, qos, topic);
                MqttMessageModel nextMsgModel;
                while (reader.ready() && (line = reader.readLine()) != null) {
                    nextMsgModel = mapper.readValue(line, MqttMessageModel.class);
                    long timeDiff = MqttMessageModel.timeDiff(nextMsgModel, msgModel);
                    try {
                        Thread.sleep(timeDiff / 1000000, (int) timeDiff % 1000000);
                        sendMqttMessage(msgModel, sampleClient, qos, topic);
                        msgModel = nextMsgModel;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private void sendMqttMessage(MqttMessageModel msgModel, MqttClient sampleClient, int qos, String topic) throws Exception {
        MqttMessage message = new MqttMessage(mapper.writeValueAsString(msgModel).getBytes());
        message.setQos(qos);
        sampleClient.publish(topic, message);       // publish message
        System.out.println(message);
    }

    @Override
    public void run() {
        System.out.println("Running " + threadName);
        String topic = "/arena2036/activeshuttle/position";             // message topic
        String content = "1234";                   // message content
        int qos = 1;                        // QoS-level: exactly one
        String broker = "tcp://localhost:1883";   // localhost:1883 is the default port of RabbitMQ
        String clientId = "JavaSample";
        String password = "guest";                  // default user of RabbitMQ
        String userName = "guest";                  // default password of RabbitMQ
        MemoryPersistence persistence = new MemoryPersistence();

        System.out.print("Please enter filename:  ");
        Scanner in = new Scanner(System.in);
        filename = in.next();
        filename = "./" + filename + ".txt";

        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName(userName);
            connOpts.setPassword(password.toCharArray());
            System.out.println("Connecting to broker: " + broker);
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
        System.out.println("Starting " + threadName);
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }

    public static void main(String[] args) {
        ArenaMqttEmulator sender = new ArenaMqttEmulator("Message-sender-1");
        sender.start();
    }
}
