package model;

public class Position {
    public double x;
    public double y;
    public double z;

    public Position() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Position(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Position(Position p) {
        this.x = p.x;
        this.y = p.y;
        this.z = p.z;
    }

    @Override
    public String toString() {
        return "position.x=" + this.x + ",position.y=" + this.y + ",position.z=" + this.z;
    }
}
