package de.tuberlin.snet.AnaLoc.SupportServices.ScaleService.scheduledTask;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.rabbitmq.client.AMQP;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Component
public class QueueChecker {

    @Autowired
    private RabbitAdmin admin;

    @Autowired
    @Qualifier("eurekaClient")
    private EurekaClient discoveryClient;

    //@Autowired
    //private List<Queue> rabbitQueues;

    private int countServiceInstances() {
        //Application app = discoveryClient.getApplication("RECOMMENDATION-SERVICE");
        Application app = discoveryClient.getApplication("FLINK-JOB");
        return app.getInstances().size();
    }

    private int count60s = 0;
    private int count5s = 0;

    private void addInstance() {
        int instanceCount = countServiceInstances();
        int runningInstanceCount = instanceCount * 2;
        System.out.println("Adding one instance, to " + runningInstanceCount + " instance(s)");
        String command = "cd ~/microservices-backend/ && docker-compose -p microservices scale flink-job=" + runningInstanceCount;
        try {
            System.out.println("Executing " + command);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();

            Session session = jsch.getSession("microservices", "dockerhost", 22);
            session.setPassword("microservices");


            session.setConfig(config);
            session.connect();
            System.out.println("Connected");

            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
            InputStream in = channelExec.getInputStream();
            channelExec.setCommand(command);
            channelExec.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            int index = 0;

            while ((line = reader.readLine()) != null) {
                System.out.println(++index + " : " + line);
            }
            channelExec.disconnect();
            session.disconnect();

            System.out.println("Done!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeInstance() {
        int instanceCount = countServiceInstances();
        int runningInstanceCount = instanceCount - 1;
        System.out.println("Removing one instance, to " + runningInstanceCount + " instance(s)");
        String command = "cd ~/RESTService_Microservices/ && docker-compose -p microservices scale flink-job=" + runningInstanceCount;
        try {
            System.out.println("Executing " + command);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();

            Session session = jsch.getSession("microservices", "dockerhost", 22);
            session.setPassword("microservices");


            session.setConfig(config);
            session.connect();
            System.out.println("Connected");

            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
            InputStream in = channelExec.getInputStream();
            channelExec.setCommand(command);
            channelExec.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            int index = 0;

            while ((line = reader.readLine()) != null) {
                System.out.println(++index + " : " + line);
            }

            channelExec.disconnect();
            session.disconnect();

            System.out.println("Done!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Scheduled(fixedRate = 1000)
    public void getCounts() {
        if (count60s == 0) {
            try {

                Properties properties = admin.getQueueProperties("aggQueue");
                Integer messageCount;

                messageCount = Integer.parseInt(properties.get("QUEUE_MESSAGE_COUNT").toString());
                System.out.println("aggque" + " has " + messageCount + " messages");
                System.out.println("Recommendation Service has " + countServiceInstances() + " instance");

                if (messageCount > 5) {
                    count5s = count5s + 1;
                } else {
                    if (countServiceInstances()>1) {
                        removeInstance();
                        count60s = 60;
                    }
                    count5s = 0;
                }

                if (count5s == 5) {
                    addInstance();
                    count5s = 0;
                    count60s = 60;
                }
            } catch (NullPointerException e) {
                System.out.println("addQueue doesnt exist!");
            }
        } else {
            System.out.println("Sleeping... come back after " + count60s + "seconds");
            try {

                Properties properties = admin.getQueueProperties("aggQueue");
                Integer messageCount;
                messageCount = Integer.parseInt(properties.get("QUEUE_MESSAGE_COUNT").toString());
                System.out.println("aggque" + " has " + messageCount + " messages");
            } catch (NullPointerException e) {
                System.out.println("addQueue doesnt exist!");
            }
            count60s = count60s - 1;
        }
    }
}
