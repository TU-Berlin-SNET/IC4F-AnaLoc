package de.tuberlin.snet.AnaLoc.CoreServices.AnalysisService.model;

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

	public static double distance(Position p1, Position p2) {
		return Math.sqrt(
				(p1.x - p2.x) * (p1.x - p2.x)
						+ (p1.y - p2.y) * (p1.y - p2.y)
						+ (p1.z - p2.z) * (p1.z - p2.z)
		);
	}
}
