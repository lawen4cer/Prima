package Core;

/**
 * Created by Administrator on 12/10/2017.
 */
public class int2
{
    private int x, y;

    //Constructors//
    public int2(int x, int y){
        this.x = x;
        this.y = y;
    }
    public int2(){
        this(0, 0);
    }

    //Getters
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    //Setters
    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public void set(int x, int y){
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString()
    {
        return String.format("X: %f, Y: %f", x, y);
    }
}
