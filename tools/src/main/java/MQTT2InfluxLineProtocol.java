import com.fasterxml.jackson.databind.ObjectMapper;
import model.MqttMessageModel;

import util.DateUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class MQTT2InfluxLineProtocol implements Runnable {
    private Thread t;
    private String threadName;

    private String input_filename;
    private String output_filename;
    private String database_name = "ArenaEmulator";


    private int speedFactor = 1;

    private transient BufferedReader reader;
    private transient BufferedWriter writer;
    private transient InputStream positionStream;

    private final int HEADER_OFFSET = 0;
    private ObjectMapper mapper = new ObjectMapper();


    public MQTT2InfluxLineProtocol(String name) {
        threadName = name;
        System.out.println("Creating" + threadName);
    }

    public void writeFileHead() throws Exception {
        writer.write("# DDL");
        writer.newLine();
        writer.newLine();
        writer.write("CREATE DATABASE " + database_name);
        writer.newLine();
        writer.newLine();
        writer.write("# DML");
        writer.newLine();
        writer.newLine();
        writer.write("# CONTEXT-DATABASE: " + database_name);
        writer.newLine();
        writer.newLine();
    }
    public void translate() throws Exception {
        String line,line_old;
        //Location location;
        MqttMessageModel msgModel,msgModel2;

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
            /*line = reader.readLine();
            if (line == null) { // done
                System.exit(0);
            }

            while (reader.ready() && (line = reader.readLine()) != null) {
                msgModel = mapper.readValue(line, MqttMessageModel.class);
                writer.write(msgModel.toString()); //write line
                writer.newLine();
            }*/
            line_old = reader.readLine();
            if (line_old == null) { // done
                System.exit(0);
            }

            while (reader.ready() && (line = reader.readLine()) != null) {
                msgModel = mapper.readValue(line_old, MqttMessageModel.class);
                msgModel2 = mapper.readValue(line, MqttMessageModel.class);
                writer.write(msgModel.toBatchString(msgModel2)); //write line
                writer.newLine();
                line_old = line;
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }


    @Override
    public void run() {
        System.out.println("Running " + threadName);

        System.out.print("Please enter input filename:  ");
        Scanner in = new Scanner(System.in);
        input_filename = in.next();
        input_filename = "./" + input_filename + ".txt";

        System.out.print("Please enter output filename:  ");
        output_filename = in.next();
        output_filename = "./" + output_filename + ".txt";

        try {
            positionStream = new FileInputStream(input_filename);
            reader = new BufferedReader(new InputStreamReader(positionStream, StandardCharsets.UTF_8));
            writer = new BufferedWriter(new FileWriter(output_filename, true));

            writeFileHead();

            translate();

            this.reader.close();
            this.reader = null;
            this.positionStream.close();
            this.positionStream = null;
            this.writer.close();

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
        MQTT2InfluxLineProtocol translator = new MQTT2InfluxLineProtocol("Translator");
        translator.start();
    }
}
