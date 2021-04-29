
/**
 * @author Lucas Li
 * Smallest unit to make a tetris.
 */

public class cells {
    private int x;
    private int y;

    public cells(int x, int y){
        this.x = x;
        this.y = y;

    }

    //move the cell to the given location(parameter)
    public void move(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void setX(int x){
        this.x = x;
    }

    public void setY(int y){
        this.y = y;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public String toString(){
        return "(" + x + ", " + y + ")";
    }

    //equals method to compare locations of two cells.
    public boolean equals(cells other){
        return toString().equals(other.toString());
    }

}
