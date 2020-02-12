import com.fasterxml.jackson.databind.ObjectMapper;
import model.MqttMessageModel;

import java.awt.event.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import javax.swing.*;

public class PathVisualizer {
    private static String filename;


    private static ObjectMapper mapper = new ObjectMapper();
    private static Line2D.Double path;
    private static Timer time;
    private static int tDiff = 0;
    private static int scale = 30;
    private static Point2D.Double offset_p = new Point2D.Double(50, 50);

    public static void main(String[] args) throws Exception {
        System.out.print("Please enter filename:  ");
        Scanner in = new Scanner(System.in);
        filename = in.next();
        filename = "./" + filename + ".txt";
        InputStream positionStream = new FileInputStream(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(positionStream, StandardCharsets.UTF_8));
        String line;

        try {
            line = reader.readLine();
            if (line == null) { // done
                System.exit(0);
            }
            if (reader.ready()) {
                MqttMessageModel msgModel = mapper.readValue(line, MqttMessageModel.class);
                line = reader.readLine();
                if (line == null) { // done
                    System.exit(0);
                }
                MqttMessageModel msgModelNext = mapper.readValue(line, MqttMessageModel.class);
                Point2D.Double p1 = new Point2D.Double(msgModel.position.x * scale + offset_p.x, msgModel.position.y * scale + offset_p.y);
                Point2D.Double p2 = new Point2D.Double(msgModelNext.position.x * scale + offset_p.x, msgModelNext.position.y * scale + offset_p.y);
                path = new Line2D.Double(p1, p2);
                long timeDiff = MqttMessageModel.timeDiff(msgModelNext, msgModel);
                tDiff = (int) timeDiff / 1000000;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        reader.close();
        reader = null;
        positionStream.close();
        positionStream = null;
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(new CustomPanel(filename, path, tDiff, scale, offset_p));
                frame.setSize(500, 500);
                frame.setVisible(true);
            }
        });

    }
}

class CustomPanel extends JPanel implements ActionListener {

    private BufferedReader reader;

    private String line;
    private MqttMessageModel msgModel;
    private ObjectMapper mapper = new ObjectMapper();
    private Path2D.Double routine;

    private int scale;
    private Point2D.Double offset_p;

    private Timer time;

    CustomPanel(String filename, Line2D.Double path, int tDiff, int scale, Point2D.Double offset_p) {
        try {
            this.scale = scale;
            this.offset_p = offset_p;
            InputStream positionStream = new FileInputStream(filename);
            reader = new BufferedReader(new InputStreamReader(positionStream, StandardCharsets.UTF_8));
            this.routine = new Path2D.Double(path);
            line = reader.readLine();
            if (line == null) {
                System.exit(0);
            }
            line = reader.readLine();
            if (line == null) {
                System.exit(0);
            }
            msgModel = mapper.readValue(line, MqttMessageModel.class);
            time = new Timer(tDiff, this);
            time.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void animateRoutine(Graphics2D g) {
        g.draw(routine);
    }

    public void actionPerformed(ActionEvent arg0) {
        time.stop();
        try {

            if (reader.ready()) {

                line = reader.readLine();
                if (line == null) {
                    System.exit(0);
                }
                MqttMessageModel msgModelNext = mapper.readValue(line, MqttMessageModel.class);
                Point2D.Double p = new Point2D.Double(msgModel.position.x * scale + offset_p.x, msgModel.position.y * scale + offset_p.x);
                routine.lineTo(p.x, p.y);
                long timeDiff = MqttMessageModel.timeDiff(msgModelNext, msgModel);
                int tDiff = (int) timeDiff / 1000000;
                time = new Timer(tDiff, this);
                msgModel = msgModelNext;
                repaint();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        time.start();
    }

    @Override
    public void paintComponent(Graphics newG) {
        super.paintComponent(newG);
        setBackground(Color.DARK_GRAY);
        setForeground(Color.WHITE);
        Graphics2D g2d = (Graphics2D) newG;
        animateRoutine(g2d);
    }
}