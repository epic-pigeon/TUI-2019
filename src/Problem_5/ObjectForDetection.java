package Problem_5;

public class ObjectForDetection {
    private String name;
    double x,y;
    double height, wight;

    public ObjectForDetection(String name, double x, double y, double height, double wight) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.height = height;
        this.wight = wight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWight() {
        return wight;
    }

    public void setWight(double wight) {
        this.wight = wight;
    }
}
