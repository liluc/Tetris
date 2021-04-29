import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/**
 * @author Lucas Li
 * Put the blocks together in a JFrame
 */
public class tetris implements Runnable {
    private static int[] eachline;
    private static int score;
    //height and length of the tetris table
    private static int height, length;
    //boolean values check whether the program needs to stop or refresh
    private boolean gameOver, touchBottom;
    //static boolean[][] to store all the blocks
    private static boolean[][] table;
    //another one just for store the next one to make it easier to print out
    private static boolean[][] next;
    //just two blocks
    private blocks nextOne, currentOne;
    //Class canvas extends JPanel. It's a special JPanel which can be paint on. Used for paint the main game layout.
    Canvas can;
    //The main JFrame contain all the parts
    JFrame jf;
    //Different labels to show messages
    JLabel scoreTi, scoreNum, highTi, highNum, gameOverNoti;
    //Class printNext extends JPanel. Used for paint the next part as the hint.
    printNext printnext;
    //The file store the highest score.
    private static File highest;
    //The int to store the highest score in the program.
    private int highestScore;
    //x rows, y cols
    //x = 20, y = 30, in testing program.
    public tetris(int x, int y) {
        next = new boolean[4][4];
        highest = new File("highest.txt");
        getHighest();
        height = y ;
        length = x ;
        eachline = new int[y];
        table = new boolean[x][y];
        score = 0;
        can = new Canvas();
        can.setLayout(null);
        jf = new JFrame("Tetris");

        scoreTi = new JLabel("Current score");
        scoreTi.setBounds(length * 20 + 10, height * 10 + 10, 100, 20);

        scoreNum = new JLabel(String.valueOf(score));
        scoreNum.setBounds(length * 20 + 10, height * 10 + 35, 100, 20);

        printnext = new printNext();
        printnext.setBounds(length * 20 + 60, 60, 280, 180);

        highTi = new JLabel("Highest score");
        highTi.setBounds(length * 20 + 10, height * 10 + 60, 100, 20);

        highNum = new JLabel(String.valueOf(highestScore));
        highNum.setBounds(length * 20 + 10, height * 10 + 85, 100, 20);

        gameOverNoti = new JLabel();
        gameOverNoti.setBounds(length * 20 + 10, height * 10 + 115, 100, 20);

        can.add(scoreTi);
        can.add(scoreNum);
        can.add(printnext);
        can.add(highTi);
        can.add(highNum);
        can.add(gameOverNoti);

        jf.add(can);
        jf.addKeyListener(new keyhandler());
        //"+200" is for the message part, "+20" is for showing the bottom line.
        jf.setSize(length * 20 + 200, height * 20 + 20);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setResizable(false);
        jf.setVisible(true);
        //Just in case the highest score haven't be stored. But because of the "JFrame.EXIT_ON_CLOSE", this part may do nothing.
        jf.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                storeHighest();
            }
        });
    }

    public static int getLength(){
        return length * 20 + 200;
    }

    public static int getHeight(){
        return height * 20 + 20;
    }

    //get the highest score in the file
    private void getHighest(){
        try{
            Scanner scan = new Scanner(highest);
            if (scan.hasNextInt()) {
                highestScore = scan.nextInt();
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }

    //write the highest score in the file
    private void storeHighest(){
        try{
            PrintWriter output = new PrintWriter(highest);
            output.print(highestScore);
            output.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }

    //update the highest score in the program.
    private void updateHighest(){
        if(score > highestScore){
            highestScore = score;
        }
        highNum.setText(String.valueOf(highestScore));
    }

    //return the static variable table, so that it could be used in Canvas or printNext class.
    static boolean[][] getTable() {
        return table;
    }

    //check if the block touches the right side or other blocks at right side.
    private static boolean isTouchRightSides(blocks block){
        boolean touchsides = false;
        cells[] cellLocation = block.getLocation();
        boolean[] allSides = new boolean[4];
        //go through all the cells and get each one each time.
        for (int i = 0; i < cellLocation.length; i++) {
            cells c = cellLocation[i];
            //if the cell touches right side of the Canvas "table" or it touches other cells on the right,
            //set the boolean variable true;
            if (c.getX() + 1 >= length || table[c.getX() + 1][c.getY()]) {
                allSides[i] = true;
                //pre-move the cell and check if it equals other cells in "this" block
                //if there are some cells in the same block equals to this one, then set the coordinate boolean value to false;
                cells newC = new cells(c.getX() + 1, c.getY());
                for (int m = 0; m < cellLocation.length; m++) {
                    if (newC.equals(cellLocation[m])) {
                        allSides[i] = false;
                    }
                }
            }
        }
        //go through the whole boolean value. If there is one value equals to true, then "touchsides", which need to be returned, is true;
        for (boolean b : allSides){
            touchsides = touchsides || b;
        }
        return touchsides;
    }

    //check if the block touches the left side or other blocks at left side.
    //all the thinking and statements are almost the same as "isTouchRrightSide", just change some actual values or variable names.
    private static boolean isTouchLeftSides(blocks block){
        boolean touchsides = false;
        cells[] cellLocation = block.getLocation();
        boolean[] allSides = new boolean[4];
        for (int i = 0; i < cellLocation.length; i++) {
            cells c = cellLocation[i];
                if (c.getX() - 1 <= -1 || table[c.getX() - 1][c.getY()]) {
                    allSides[i] = true;
                    cells newC = new cells(c.getX() - 1, c.getY());
                    for (int m = 0; m < cellLocation.length; m++) {
                        if (newC.equals(cellLocation[m])) {
                            allSides[i] = false;
                        }
                    }
                }

        }
        for (boolean b : allSides){
            touchsides = touchsides || b;
        }
        return touchsides;
    }

    //update the int[] eachLine to check for canceling lines.
    private void updateLine(){
        for (int i = 0; i < table[0].length; i++){
            int count = 0;
            for (int m = 0; m < table.length; m++){
                if(table[m][i]){
                    count ++;
                }
            }
            eachline[i] = count;
        }
    }

    //check if the block touches bottom or other blocks.
    //all the thinking and statements are almost the same as "isTouchRrightSide", just change some actual values or variable names.
    private static boolean isTouchBottom(blocks block) {
        boolean touchBottom = false;
        cells[] cellLocation = block.getLocation();
        boolean[] allBottoms = new boolean[4];
        for (int i = 0; i < cellLocation.length; i++) {
            cells c = cellLocation[i];
            if (c.getY() + 1 >= height || table[c.getX()][c.getY() + 1]) {
                allBottoms[i] = true;
                cells newC = new cells(c.getX(), c.getY() + 1);
                for (int m = 0; m < cellLocation.length; m++) {
                    if (newC.equals(cellLocation[m])) {
                        allBottoms[i] = false;
                    }
                }
            }
        }
        for (boolean b : allBottoms){
            touchBottom = touchBottom || b;
        }
        return touchBottom;
    }

    //rotate the block counterclockwise for 90 degrees.
    private void rotate(){
        cells[] position = new cells[4];
        cells[] copyLocation = currentOne.getLocation();
        //just get the values of location of currentOne.
        //otherwise rotate newB will also rotate currentOne.
        for(int i = 0; i < 4; i++){
            position[i] = new cells(copyLocation[i].getX(),copyLocation[i].getY());
        }
        //a block which has the same location as currentOne.
        //pre-rotate this to make sure that the rotated one won't go out of bound.
        blocks newB = new blocks(position);

        newB.rotate();
        //indicate whether currentOne can rotate
        boolean canRotate = true;
        for (int i = 0; i < 4; i++){
            cells newC = newB.getLocation()[i];
            if(newC.getY() <= 0){
                canRotate = false;
            }
        }
        if (canRotate) {
            if (isTouchBottom(currentOne) || isTouchRightSides(newB) || isTouchLeftSides(newB)) {
                canRotate = false;
            }
        }

        if (canRotate) {
            currentOne.rotate();
        }
    }

    //what the program is going to do if one line of brick is full.
    //can be simplified by delete the method of updateLine and put this part in this method.
    //But I think use another updateLine method is easier for checking errors and understanding.
    private void cancelLine() {
        for (int i = 0; i < eachline.length; i++) {
            if (eachline[i] == length){
                score++;
                for (int m = i; m > 0; m--) {
                    eachline[m] = eachline[m - 1];
                    for (int y = 0; y < table.length; y++){
                        table[y][m] = table[y][m - 1];
                    }
                }
            }
        }
        can.repaint();
    }

    //check if the game is over.
    private boolean isGameOver() {
        cells[] position = currentOne.getLocation();
        for (cells x : position){
            if (x.getY() < 1){
                gameOver = true;
            }
        }
        return gameOver;
    }

    //generate random blocks.
    private blocks randomBlock() {
        String[] types = {"I", "iL", "L", "Z", "iZ", "T", "O"};
        String type = types[(int) (Math.random() * 7)];
        return new blocks(type, length / 2 - 1, 1);
    }

    //generate next block
    private void next() {
        nextOne = randomBlock();
    }

    //drop the block.
    private void drop() {
        ereaseLocation();
        currentOne.drop();
        printLocation();
        can.repaint();
    }

    //set ture in table to show the location of cells
    private void printLocation() {
        cells[] cellLocation = currentOne.getLocation();
        for (cells c : cellLocation) {
            table[c.getX()][c.getY()] = true;
        }
    }

    //remove true when the cells leave the location
    private void ereaseLocation() {
        cells[] cellLocation = currentOne.getLocation();
        for (cells c : cellLocation) {
            table[c.getX()][c.getY()] = false;
        }
    }

    //handler any key actions and combine them with the code.
    private class keyhandler extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e) {
            super.keyPressed(e);
            switch(e.getKeyCode()){
                case KeyEvent.VK_UP:
                    ereaseLocation();
                    rotate();
                    printLocation();
                    can.repaint();
                    break;

                case KeyEvent.VK_DOWN:
                    ereaseLocation();
                    if(!isTouchBottom(currentOne)) {
                        currentOne.drop();
                    }
                    printLocation();
                    can.repaint();
                    break;

                case KeyEvent.VK_LEFT:
                    ereaseLocation();
                    if (!isTouchLeftSides(currentOne)) {
                        currentOne.moveLeft();
                    }
                    printLocation();
                    can.repaint();
                    break;

                case KeyEvent.VK_RIGHT:
                    ereaseLocation();
                    if (!isTouchRightSides(currentOne)) {
                        currentOne.moveRight();
                    }
                    printLocation();
                    can.repaint();
                    break;

                case KeyEvent.VK_SPACE:
                    ereaseLocation();
                    while(!isTouchBottom(currentOne)){
                        drop();
                    }
                    printLocation();
                    can.repaint();
                    break;
            }
        }
    }

    //put the block "nextOne" in the array "next" for paint later.
    private void updateNext(){
        //just a "re-new" loop, make sure that the boolean[][] refreshes every time.
        for (int i = 0; i < 4; i++){
            for (int m = 0; m < 4; m++){
                next[i][m] = false;
            }
        }
        cells[] location = nextOne.getLocation();
        //because the randomBlock method generates the blocks at the middle of boolean[][] table
        //and we have to put nextOne into boolean[][] next, there will be an index error if I do not change it.
        //next two loops are for move the block to the left top corner without carrying out any error.
        int xDiff = location[0].getX();
        int yDiff = location[0].getY();
        for (cells c : location){
            if (c.getX() < xDiff){
                xDiff = c.getX();
            }
        }

        for (cells c : location){
            if (c.getY() < yDiff){
                yDiff = c.getY();
            }
        }

        //put the location changed block in the boolean[][] next for print.
        for (int i = 0; i < 4; i++){
            cells thisOne = location[i];
            for (int m = 0; m < 4; m++){
                next[thisOne.getX() - xDiff][thisOne.getY() - yDiff] = true;
            }
        }
    }

    //return the boolean[][] for printNext class to use to paint out the next block at the top right coner of the frame.
    static boolean[][] getNext(){
        return next;
    }

    //Combine every method together and run the game.
    public void run() {
        int count = 0;
        int changeSpeed = 0;
        //do - while loop to make sure that it runs at least once
        //stop if game over.
        do{
            if(count == 0) {
                currentOne = randomBlock();
            }else{
                currentOne = nextOne;
            }
            next();
            updateNext();
            //drop and sleep the thread.
            //because it is difficult to control 2 or more thread in the program, I just use one.
            while(!isTouchBottom(currentOne)){
                drop();
                if (score / 5 < 4) {
                    changeSpeed = score / 5;
                }
                try {
                    Thread.sleep(300 - changeSpeed * 100);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

            //check and renew some of the variable after every "currentOne" block touches bottom.
            updateLine();
            cancelLine();
            updateHighest();
            storeHighest();
            scoreNum.setText(String.valueOf(score));
            touchBottom = false;
            count ++;
        }while(!isGameOver());
        gameOverNoti.setText("Game Over");
        storeHighest();
    }
}

//special JPanel to draw things out.
//This one is for drawing the main panel of the tetris
class Canvas extends JPanel {
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;

        for (int i = 0; i < tetris.getTable().length; i++) {
            for (int j = 0; j < tetris.getTable()[0].length; j++) {
                g2.setColor(Color.LIGHT_GRAY);
                g2.fillRect(i * 20, 0, 1, tetris.getHeight());
                g2.fillRect(0, j * 20, tetris.getLength() - 200, 1);
            }
        }

        g2.setColor(Color.black);
        for (int i = 0; i < tetris.getTable().length; i++) {
            for (int j = 0; j < tetris.getTable()[0].length; j++) {
                if (tetris.getTable()[i][j]) {
                    g2.fillRect(i * 20, j * 20, 20, 20);
                }
            }
        }

        g2.fillRect(tetris.getLength() - 200,0,5,tetris.getHeight() - 20);
        g2.fillRect(tetris.getLength() - 200 + 5,(tetris.getHeight() - 20) / 2,200,5);
    }
}

//This one is for drawing the "nextOne" at the top right corner of the previous one,
class printNext extends JPanel{
    public void paint(Graphics g){
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        boolean[][] next = tetris.getNext();
        for (int i = 0; i < 4; i++){
            for (int m = 0; m < 4; m++){
                if (next[i][m]){
                    g2.fillRect(i * 20, m * 20,20,20);
                }
            }
        }
    }
}
