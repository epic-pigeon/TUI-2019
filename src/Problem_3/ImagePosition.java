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

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public double getDeltaX() {
        return deltaX;
    }

    public void setDeltaX(double deltaX) {
        this.deltaX = deltaX;
    }

    public double getDeltaY() {
        return deltaY;
    }

    public void setDeltaY(double deltaY) {
        this.deltaY = deltaY;
    }
}
