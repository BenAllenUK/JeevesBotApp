package info.benallen.jeeves.jeevescontroller.face.emotion;

import com.crowdrobo.robohead.face.EyeBrow;
import com.crowdrobo.robohead.face.FaceScreen;
import com.crowdrobo.robohead.face.Mouth;

/**
 * Created by rory on 30/01/16.
 */
public class NeutralEmotion implements Emotion {
    @Override
    public void applyEmotion(FaceScreen faceScreen) {
        faceScreen.getMouth().setPixelInfo(Mouth.FLAT);
        faceScreen.getLeftEyeBrow().setPixelInfo(EyeBrow.NONE);
        faceScreen.getRightEyeBrow().setPixelInfo(EyeBrow.NONE);
    }
}
