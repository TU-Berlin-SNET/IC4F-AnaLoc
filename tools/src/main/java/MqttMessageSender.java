import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/*
 *
 *   ClassName: MqttMessageSender
 *   Description: publish MQTT message "1234" to RabbitMQ MQTT broker
 *
 */
public class MqttMessageSender {
    public static void main(String[] args){
        String topic = "/arena2036/activeshuttle/position";             // message topic
        String content      = "1234";                   // message content
        int qos             = 2;                        // QoS-level: exactly one
        String broker       = "tcp://127.0.0.1:1883";   // localhost:1883 is the default port of RabbitMQ
        String clientId     = "JavaSample";
        String password     = "guest";                  // default user of RabbitMQ
        String userName     = "guest";                  // default password of RabbitMQ
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName(userName);
            connOpts.setPassword(password.toCharArray());
            System.out.println("Connecting to broker: "+broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
            System.out.println("Publishing message: "+content);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            sampleClient.publish(topic, message);       // publish message
            System.out.println("Message published");
            sampleClient.disconnect();
            System.out.println("Disconnected");
            System.exit(0);
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }
}
