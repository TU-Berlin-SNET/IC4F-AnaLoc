import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *      Class:  DataFileGenerator
 *      Description:    This class is intended to generate stimulated data
 *                      of the robot movement inside a rectangle shaped room.
 *
 */
public class DataFileGenerator {
    public int maxX, maxY;              // The max indexes of the rectangle map
    int[][] map;                        // The map
    final int[] weight = {1, 2, 3, 4};     // The weight simulates the cost of time before moving to another point

    /* Constructor */
    public DataFileGenerator(int maxX, int maxY){
        Date baseDate = new Date();
        this.maxX = maxX;
        this.maxY = maxY;
        initMap(maxX, maxY);
    }

    /* Initialize the map with random weights */
    private void initMap(int maxX, int maxY){
        Random ran = new Random();
        map = new int[maxY+1][maxX+1];
        for (int y=0; y<maxY+1; y++) {
            for (int x=0; x<maxX+1; x++){
                map[y][x] = weight[ran.nextInt(weight.length) ];
            }
        }
    }

    /* Inner class Point which describes the Point used for calculation*/
    public static class Point{

        int x;
        public int getX() {
            return x;
        }

        int y;
        public int getY() {
            return y;
        }

        /* (directionX , directionY) unions together for the increment of data point */
        final int[] directionX = {-1, -1, -1, 0, 1, 1, 1, 0};
        final int[] directionY = {-1, 0, 1, 1, 1, 0, -1, -1};

        /* Default constructor which initialize the point with (0,0) */
        public Point(){
            this.x = 0;
            this.y = 0;
        }

        /* Constructor of (x,y) */
        public Point(int x,int y){
            this.x=x;
            this.y=y;
        }

        /* Constructor of (point) */
        public Point(Point p){
            this.x=p.getX();
            this.y=p.getY();
        }

        /* Transformation to the new position by the given direction */
        public Point walk(int direction){
            return new Point(this.getX()+directionX[direction],this.getY()+directionY[direction]);
        }

        /* Perform a random step to another position inside the given range */
        public Point randomWalk(int maxX, int maxY){
            int direction;
            int x;
            int y;
            Random ran = new Random();
            direction = ran.nextInt(directionX.length);
            Point np = this.walk(direction);
            while (isValid(np,maxX,maxY)==false){
                direction = ran.nextInt(directionX.length);
                np = this.walk(direction);
            }
            return np;
        }

        /* To judge whether a point is inside the given range */
        public static boolean isValid(Point p, int maxX, int maxY){
            int x = p.getX();
            int y = p.getY();
            return x >= 0 && y >= 0 && x <= maxX && y <= maxY;
        }

        /* String generator for debugging */
        public String debugToString(){
            return "x:"+this.x+"\ty:"+this.y;
        }

        @Override
        public String toString(){
            return this.x+","+this.y;
        }
    }

    /* Draw the map with weight for debugging */
    private void debugPrintMap(){
        for (int[] row : map) {
            for (int elem : row){
                System.out.printf("%2d,\t",elem);
            }
            System.out.printf("\n");
        }
    }

    /* Print one step for debugging */
    private void debugPrintStep(Point p){
        System.out.println(p.debugToString());
        System.out.println("delay "+this.map[p.getX()][p.getY()]+"");
        System.out.println("---------");
    }

    /* A simple test for debugging */
    private static void debugTest(int maxX, int maxY){
        DataFileGenerator g = new DataFileGenerator(maxX,maxY);
        g.debugPrintMap();
        Point p = new Point();
        System.out.println("Start simulated streaming:");
        g.debugPrintStep(p);
        for(int i=0; i<(g.maxX*g.maxY); i++){
            p=p.randomWalk(g.maxX,g.maxY);
            g.debugPrintStep(p);
        }
    }

    /**
     *     main method, follow the instruction shown in the console to generate data file
     *      Example of usage:
     *                      Please enter maxX of the map:  9
     *                      Please enter maxY of the map:  9
     *                      Please enter (iteration times-1):  100
     *                      Please enter x of the start point:  1
     *                      Please enter y of the start point:  2
     *                      Please enter filename:  test
     *      Attention: The generated file is "test.csv"  according to the Example of usage
     *                 which contains 1 row of header at beginning
     */
    public static void main(String[] args) throws IOException{
        //debugTest(9,9);
        Scanner in = new Scanner(System.in);
        System.out.print("Please enter maxX of the map:  ");
        int maxX = in.nextInt();
        System.out.print("Please enter maxY of the map:  ");
        int maxY = in.nextInt();
        DataFileGenerator g = new DataFileGenerator(maxX,maxY);
        System.out.print("Please enter (iteration times-1):  ");
        int iter = in.nextInt();
        System.out.print("Please enter x of the start point:  ");
        int x = in.nextInt();
        System.out.print("Please enter y of the start point:  ");
        int y = in.nextInt();
        System.out.print("Please enter filename:  ");
        String filename = in.next();
        filename = "./"+filename+".csv";
        Point p = new Point(x,y);
        Calendar timeNow = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        try (
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename));

                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                        .withHeader("X", "Y", "Date", "Time"))
        ) {
            csvPrinter.printRecord(p.getX(), p.getY(), dateFormat.format(timeNow.getTime()), timeFormat.format(timeNow.getTime()));
            for(int i=0; i<iter-1; i++){
                p=p.randomWalk(g.maxX,g.maxY);
                int delay = g.map[p.getX()][p.getY()];
                timeNow.add(Calendar.SECOND, delay);
                csvPrinter.printRecord(p.getX(), p.getY(), dateFormat.format(timeNow.getTime()), timeFormat.format(timeNow.getTime()));
                // write to file
            }
            csvPrinter.flush();
        }

    }

}
