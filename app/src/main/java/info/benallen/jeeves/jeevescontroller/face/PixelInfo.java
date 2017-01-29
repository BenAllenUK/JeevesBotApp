package info.benallen.jeeves.jeevescontroller.face;

/**
 * Created by rory on 30/01/16.
 */
public class PixelInfo {
    public final int x;
    public final int y;
    public final int[][] map;

    public PixelInfo (int x, int y, int[][] map) {
        this.x = x;
        this.y = y;
        this.map = map;
    }
}
