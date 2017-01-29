package info.benallen.jeeves.jeevescontroller.face.emotion;

import com.crowdrobo.robohead.face.EyeBrow;
import com.crowdrobo.robohead.face.FaceScreen;
import com.crowdrobo.robohead.face.Mouth;

/**
 * Created by rory on 30/01/16.
 */
public class SurprisedEmotion implements Emotion {
    @Override
    public void applyEmotion(FaceScreen faceScreen) {
        faceScreen.getLeftEyeBrow().setPixelInfo(EyeBrow.LEFT_SURPRISED);
        faceScreen.getRightEyeBrow().setPixelInfo(EyeBrow.RIGHT_SURPRISED);
        faceScreen.getMouth().setPixelInfo(Mouth.ROUND);
    }
}
