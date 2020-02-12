package model;

import org.json.simple.JSONObject;
import util.DateUtil;

import java.util.HashMap;

public class Location {
    private int x;
    private int y;
    private long timeStamp;

    public Location() {
    }

    public Location(int x, int y, long timeStamp) {
        this.x = x;
        this.y = y;
        this.timeStamp = timeStamp;
    }

    public int getX(){
        return x;
    }

    public void setX(int x){
        this.x = x;
    }

    public int getY(){
        return y;
    }

    public void setY(int y){
        this.y = y;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getX()).append(",");
        sb.append(this.getY()).append(",");
        sb.append(this.getTimeStamp());
        return sb.toString();
    }

    public static Location fromString(String message) {
        String[] tokens = message.split(",");

        if (tokens.length != 3) {
            throw new RuntimeException("Invalid location object");
        }

        Location location = new Location();

        try {
            location.setX(Integer.parseInt(tokens[0]));
            location.setY(Integer.parseInt(tokens[1]));
            location.setTimeStamp(Long.parseLong(tokens[2]));
        } catch (NumberFormatException nfe) {
            throw new RuntimeException("Invalid distance event record " + message, nfe);
        }

        return location;
    }

    public static Location fromCSVString(String line, String dataPath) {
        String[] tokens = line.split(",");
        String formatDate = tokens[2] + ":" + tokens[3];
        if (tokens.length != 4) {
            System.out.println(tokens);
            throw new RuntimeException("Invalid CSV record");
        }
        Location location = new Location();
        try {
            location.setX(Integer.parseInt(tokens[0]));
            location.setY(Integer.parseInt(tokens[1]));
            location.setTimeStamp(DateUtil.convertDateStringToTimeStamp(formatDate));
        } catch (NumberFormatException nfe) {
            throw new RuntimeException("Invalid distance event record " + line, nfe);
        }
        return location;
    }

    public String toJSONString(boolean useCurrentTimeStamp){
        /*
        JSONObject obj = new JSONObject();
        obj.put("x",this.getX());
        obj.put("y",this.getY());
        if (useCurrentTimeStamp)
            obj.put("timeStamp",DateUtil.getCurrentUnixTimeStamp());
        else
            obj.put("timeStamp",this.getTimeStamp());
        return obj.toJSONString();
        */
        HashMap<String,String> map = new HashMap<String, String>();
        try {
            map.put("x",Integer.toString(this.getX()));
            map.put("y",Integer.toString(this.getY()));
            if (useCurrentTimeStamp)
                map.put("timeStamp",Long.toString(DateUtil.getCurrentUnixTimeStamp()));
            else
                map.put("timeStamp",Long.toString(this.getTimeStamp()));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        JSONObject obj = new JSONObject(map);
        return obj.toJSONString();
    }
}
