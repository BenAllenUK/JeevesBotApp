package info.benallen.jeeves.jeevescontroller.face;

/**
 * Created by rory on 30/01/16.
 */
public class Mouth extends FacialFeature {

    public static final PixelInfo FLAT = new PixelInfo(2, 10, new int[][]{{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}});
    public static final PixelInfo SAG = new PixelInfo(2, 9, new int[][]{{0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},{0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0} , {1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1}, {1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1},{1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1}});
    public static final PixelInfo SMILE = new PixelInfo(2, 9, new int[][]{{1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1}, {1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1}, {1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1}, {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0}, {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0}});
    public static final PixelInfo ROUND = new PixelInfo(6, 8, new int[][]{{1, 1, 1, 1},{1, 0, 0, 1},{1, 0, 0, 1},{1, 1, 1, 1}});
    public static final PixelInfo GRIN = new PixelInfo(4, 8, new int[][]{{1, 1, 1, 1, 1, 1, 1, 1}, {1, 0, 0, 0, 0, 0, 0, 1}, {0, 1, 0, 0, 0, 0, 1, 0}, {0, 0, 1, 1, 1, 1, 0, 0}});

    public Mouth () {
        setPixelInfo(GRIN);
    }
}
