package Problem_3;

public class ImagePosition {

    private double angle;
    private double deltaX;
    private double deltaY;

    public ImagePosition(double angle, double deltaX, double deltaY) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.angle = angle;
    }

    public double getAngle() {
        return angle;
    }

    public double getDeltaX() {
        return deltaX;
    }

    public double getDeltaY() {
        return deltaY;
    }
}
