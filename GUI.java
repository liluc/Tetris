/**
 * @author Lucas Li
 * Main class for the whole program.
 */
public class GUI {
    public static void main(String[] args) {
        tetris tet = new tetris(20,30);
        //using Tread class to make sure that when the thread sleeps, there will be less errors.
        Thread t = new Thread(tet);
        t.run();
    }

}
