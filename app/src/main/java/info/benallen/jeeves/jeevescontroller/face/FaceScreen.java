package info.benallen.jeeves.jeevescontroller.face;

import com.crowdrobo.robohead.PixelInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rory on 30/01/16.
 */
public class FaceScreen implements PixelInterface {
    private final Eye mLeftEye;
    private final Eye mRightEye;
    private final EyeBrow mLeftEyeBrow;
    private final EyeBrow mRightEyeBrow;
    private final Mouth mMouth;
    protected List<FacialFeature> features;

    public FaceScreen() {
        this.features = new ArrayList<>();
        mLeftEye = new Eye(Eye.EYE_TYPE.LEFT);
        mRightEye = new Eye(Eye.EYE_TYPE.RIGHT);

        mLeftEyeBrow = new EyeBrow(EyeBrow.EYE_TYPE.LEFT);
        mRightEyeBrow = new EyeBrow(EyeBrow.EYE_TYPE.RIGHT);

        mMouth = new Mouth();

        features.add(mLeftEye);
        features.add(mRightEye);
        features.add(mLeftEyeBrow);
        features.add(mRightEyeBrow);
        features.add(mMouth);
    }

    public Eye getLeftEye() {
        return mLeftEye;
    }

    public Eye getRightEye() {
        return mRightEye;
    }

    public EyeBrow getLeftEyeBrow() {
        return mLeftEyeBrow;
    }

    public EyeBrow getRightEyeBrow() {
        return mRightEyeBrow;
    }

    public Mouth getMouth() {
        return mMouth;
    }

    @Override
    public boolean shouldDrawPixel(int x, int y) {
        for (FacialFeature facialFeature : features) {
            if (facialFeature.shouldDrawPixel(x, y)) {
                return true;
            }
        }
        return false;
    }

}
