package model;

public class Orientation {
    public double x;
    public double y;
    public double z;
    public double w;

    public Orientation() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.w = 0;
    }

    public Orientation(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Orientation(Orientation o) {
        this.x = o.x;
        this.y = o.y;
        this.z = o.z;
        this.w = o.w;
    }

    @Override
    public String toString() {
        return "orientation.x=" + this.x + ",orientation.y=" + this.y + ",orientation.z=" + this.z + ",orientation.w=" + this.w;
    }
}
