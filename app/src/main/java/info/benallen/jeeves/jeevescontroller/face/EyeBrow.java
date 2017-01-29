package info.benallen.jeeves.jeevescontroller.face;

/**
 * Created by rory on 30/01/16.
 */
public class EyeBrow extends FacialFeature {

    public static final PixelInfo LEFT_ANGRY = new PixelInfo(4, 1, new int[][]{{1, 0, 0}, {0, 1, 0}, {0, 0, 1}});
    public static final PixelInfo RIGHT_ANGRY = new PixelInfo(9, 1, new int[][]{{0, 0, 1}, {0, 1, 0}, {1, 0, 0}});

    public static final PixelInfo LEFT_SURPRISED = new PixelInfo(0, 0, new int[][]{{0, 0, 1}, {0, 1, 0}, {1, 0, 0}});
    public static final PixelInfo RIGHT_SURPRISED = new PixelInfo(13, 0, new int[][]{{1, 0, 0}, {0, 1, 0}, {0, 0, 1}});

    public static final PixelInfo NONE = new PixelInfo(0, 0, new int[0][0]);

    public enum EYE_TYPE {LEFT, RIGHT;};

    public EyeBrow (EyeBrow.EYE_TYPE eyeType) {
        switch (eyeType) {
            case LEFT:
                setPixelInfo(NONE);
                break;
            case RIGHT:
                setPixelInfo(NONE);
                break;
        }
    }
}
