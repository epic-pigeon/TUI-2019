package Problem_5;

public class ObjectForDetection {
    private String name;
    int x,y;
    double height, wight;

    public ObjectForDetection(String name, int x, int y, double height, double wight) {
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

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
