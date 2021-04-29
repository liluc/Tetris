
/**
 * @author Lucas Li
 * A bigger unit to make the tetris.
 * Some movement just related to the single block
 */

public class blocks {
    private cells[] location;
    private String type;
    int count;

    //The constructor takes two int as parameter. The ints are the starting point of the block object
    //Create different blocks based on the starting point.
    public blocks(String type, int x, int y){
        location = new cells[4];
        this.type = type;
        switch (type){
            case "I":
                location[0] = new cells(x,y);
                location[1] = new cells(x + 1, y);
                location[2] = new cells(x + 2, y);
                location[3] = new cells(x + 3, y);
                break;

            case "L":
                location[0] = new cells(x,y);
                location[1] = new cells(x, y - 1);
                location[2] = new cells(x + 1, y - 1);
                location[3] = new cells(x + 2, y - 1);
                break;

            case "iL":
                location[0] = new cells(x,y);
                location[1] = new cells(x + 1, y);
                location[2] = new cells(x + 2, y);
                location[3] = new cells(x + 2, y + 1);
                break;

            case "Z":
                location[0] = new cells(x,y);
                location[1] = new cells(x + 1, y);
                location[2] = new cells(x + 1, y + 1);
                location[3] = new cells(x + 2, y + 1);
                break;

            case "iZ":
                location[0] = new cells(x,y);
                location[1] = new cells(x + 1, y);
                location[2] = new cells(x + 1, y - 1);
                location[3] = new cells(x + 2, y - 1);
                break;

            case "T":
                location[0] = new cells(x,y);
                location[1] = new cells(x + 1, y);
                location[2] = new cells(x + 1, y - 1);
                location[3] = new cells(x + 2, y);
                break;

            case "O":
                location[0] = new cells(x,y);
                location[1] = new cells(x, y - 1);
                location[2] = new cells(x + 1, y);
                location[3] = new cells(x + 1, y - 1);
                break;
        }
    }

    //set the new location for the block
    public blocks(cells[] location){
        this.location = location;
    }

    //rotate the block
    public void rotate(){
        cells[] position = this.location;
        //differences between the starting location of a block and (0,0) in x-y coordinate.
        int xDiff = position[1].getX();
        int yDiff = position[1].getY();


        //Spin the block counterclockwise for 90 degrees.
        for (int i = 0; i < position.length; i++){
            //get each cell
            cells thisOne = position[i];
            //fold every cell in the x-y coordinate with line y = -x as the axis
            cells needToBeRotate = new cells(thisOne.getX() - xDiff, thisOne.getY() - yDiff);
            //then fold ever cell with x axis as the folding axis
            position[i].move(needToBeRotate.getY() + xDiff,-1 * needToBeRotate.getX() + yDiff);
        }
    }

    //move right
    public void moveRight(){
        for (cells x : location){
            x.move(x.getX() + 1, x.getY());
        }
    }

    //move left
    public void moveLeft(){
        for (cells x : location){
            x.move(x.getX() - 1, x.getY());
        }
    }

    //move down by 1
    public void drop(){
        for (int i = 0; i < location.length; i++){
            location[i].move(location[i].getX(), location[i].getY() + 1);
        }
    }

    //print out the block to make it easier for debugging
    public String toString(){
        int[][] result= new int[4][2];
        for (int i = 0; i < 4; i++){
            for (int m = 0; m < 2; m++){
                if (m == 0){
                    result[i][m] = location[i].getX();
                }
                else{
                    result[i][m] = location[i].getY();
                }
            }
        }

        String str = "{";
        for (int[] x : result){
            str += "(";
            for (int c : x){
                str += c + ", ";
            }
            str = str.substring(0, str.length() - 2) + ")";
        }
        str += "}";

        return str;
    }

    //just some get method, return the coordinate fields.
    cells[] getLocation(){
        return location;
    }

    String getType(){
        return type;
    }

    //relocate the block without changing the type of the block.
    void setLocation(cells[] cells){
        location = cells;
    }
}
