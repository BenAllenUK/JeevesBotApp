package info.benallen.jeeves.jeevescontroller.face;

import com.crowdrobo.robohead.PixelInterface;

/**
 * Created by rory on 30/01/16.
 */
public class NoConnectionScreen implements PixelInterface {

    public static final int[][] NO_CONNECTION_BITMAP = new int[][]{
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0},
            {0,0,0,1,1,1,0,1,0,0,1,1,1,0,0,0},
            {0,0,1,0,0,0,0,1,0,0,0,0,0,1,0,0},
            {0,1,0,0,0,0,0,0,1,0,0,0,0,0,1,0},
            {0,1,0,0,0,0,0,0,0,1,0,0,0,0,1,0},
            {0,1,0,0,0,0,0,0,0,1,0,0,0,0,1,0},
            {0,0,1,0,0,0,0,0,1,0,0,0,0,1,0,0},
            {0,0,0,1,1,1,0,1,0,0,1,1,1,0,0,0},
            {0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
    };

    @Override
    public boolean shouldDrawPixel(int x, int y) {
        return NO_CONNECTION_BITMAP[y][x] == 1;
    }
}
