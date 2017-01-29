package info.benallen.jeeves.jeevescontroller.face;

/**
 * Created by rory on 30/01/16.
 */
public class Eye extends FacialFeature {
    public static final PixelInfo LEFT_EYE_OPEN = new PixelInfo(2, 3, new int[][]{{1, 1, 1}, {1, 1, 1}, {1, 1, 1}});
    public static final PixelInfo RIGHT_EYE_OPEN = new PixelInfo(11, 3, new int[][]{{1, 1, 1}, {1, 1, 1}, {1, 1, 1}});

    public static final PixelInfo LEFT_EYE_CLOSED = new PixelInfo(2, 4, new int[][]{{1, 1, 1}});
    public static final PixelInfo RIGHT_EYE_CLOSED = new PixelInfo(11, 4, new int[][]{{1, 1, 1}});
    private final EYE_TYPE mEyeType;

    public enum EYE_TYPE {LEFT, RIGHT;}

    public Eye(EYE_TYPE eyeType) {
        mEyeType = eyeType;
        switch (eyeType) {
            case LEFT:
                setPixelInfo(LEFT_EYE_OPEN);
                break;
            case RIGHT:
                setPixelInfo(RIGHT_EYE_OPEN);
                break;
        }
    }
}
