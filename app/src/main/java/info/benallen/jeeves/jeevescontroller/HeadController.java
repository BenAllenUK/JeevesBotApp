package info.benallen.jeeves.jeevescontroller;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;

import info.benallen.jeeves.jeevescontroller.face.Eye;
import info.benallen.jeeves.jeevescontroller.face.FaceScreen;
import info.benallen.jeeves.jeevescontroller.face.MiddleFingerScreen;
import info.benallen.jeeves.jeevescontroller.face.emotion.AngryEmotion;
import info.benallen.jeeves.jeevescontroller.face.emotion.HappyEmotion;
import info.benallen.jeeves.jeevescontroller.face.emotion.NeutralEmotion;
import info.benallen.jeeves.jeevescontroller.face.emotion.SurprisedEmotion;
import info.benallen.jeeves.jeevescontroller.face.emotion.UnhappyEmotion;

/**
 * Created by benallen on 29/01/2017.
 */

public class HeadController {
    private static final String TAG = "HeadController";
    private static final long BLINK_RUNNABLE_DELAY = 3000;

    private static final String HAPPY = "emotion:happy";
    private static final String NEUTRAL = "emotion:neutral";
    private static final String UNHAPPY = "emotion:unhappy";
    private static final String ANGRY = "emotion:angry";
    private static final String SURPRISED = "emotion:surprised";
    private static final String MIDDLE_FINGER = "emotion:finger";

    private final FaceScreen mFaceScreen;
    private final PixelMatrix mPixelMatrix;
    private final MediaPlayer mMediaPlayer;
    private final Context mContext;


    private boolean mConnected;
    Runnable blinkRunnable = new Runnable() {
        public boolean blinking;
        int frameNumber = 0;
        @Override
        public void run() {

            if(mConnected) {
                if(blinking) {
                    blinking = false;
                    mFaceScreen.getLeftEye().setPixelInfo(Eye.LEFT_EYE_OPEN);
                    mFaceScreen.getRightEye().setPixelInfo(Eye.RIGHT_EYE_OPEN);
                } else {
                    blinking = true;
                    mFaceScreen.getLeftEye().setPixelInfo(Eye.LEFT_EYE_CLOSED);
                    mFaceScreen.getRightEye().setPixelInfo(Eye.RIGHT_EYE_CLOSED);
                }


                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mPixelMatrix.invalidate();
                    }
                });

                handler.postDelayed(blinkRunnable, (long) (50 + Math.random()*700 + (blinking ? 0 : (BLINK_RUNNABLE_DELAY + Math.random() * BLINK_RUNNABLE_DELAY))));
            }
        }
    };

    private Handler handler;

    public HeadController(final Context context, final PixelMatrix pixelMatrix) {

        mContext = context;

        mPixelMatrix = pixelMatrix;

        handler = new Handler();

        mFaceScreen = new FaceScreen();

        mMediaPlayer = new MediaPlayer();

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });

        handler.postDelayed(blinkRunnable, BLINK_RUNNABLE_DELAY);
        mPixelMatrix.setFace(mFaceScreen);
    }

    @Override
    public void onConnect() {
        mConnected = true;
        Log.d(TAG, "Connected!");
        handler.postDelayed(blinkRunnable, BLINK_RUNNABLE_DELAY);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mPixelMatrix.setFace(mFaceScreen);
            }
        });
    }

    @Override
    public void onMessage(String message) {
        Log.d(TAG, String.format("Got string message! %s", message));

        if (message.startsWith("emotion")) {
            final PixelInterface pixelInterface;
            switch (message) {
                case UNHAPPY:
                    pixelInterface = mFaceScreen;
                    new UnhappyEmotion().applyEmotion(mFaceScreen);
                    break;
                case ANGRY:
                    pixelInterface = mFaceScreen;
                    new AngryEmotion().applyEmotion(mFaceScreen);
                    break;
                case SURPRISED:
                    pixelInterface = mFaceScreen;
                    new SurprisedEmotion().applyEmotion(mFaceScreen);
                    break;
                case MIDDLE_FINGER:
                    pixelInterface = new MiddleFingerScreen();
                    break;
                case HAPPY:
                    pixelInterface = mFaceScreen;
                    new HappyEmotion().applyEmotion(mFaceScreen);
                    break;
                case NEUTRAL:
                default:
                    pixelInterface = mFaceScreen;
                    new NeutralEmotion().applyEmotion(mFaceScreen);
                    break;
            }

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mPixelMatrix.setFace(pixelInterface);
                }
            });
        } else if (message.startsWith("audio")) {
            final String url = message.substring(6,message.length());
            if(mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.reset();
            try {
                mMediaPlayer.setDataSource(url);
                mMediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public void onMessage(byte[] data) {

    }

    @Override
    public void onDisconnect(int code, String reason) {
        mConnected = false;
        Log.d(TAG, String.format("Disconnected! Code: %d Reason: %s", code, reason));
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new NeutralEmotion().applyEmotion(mFaceScreen);
                mPixelMatrix.setFace(new NoConnectionScreen());
            }
        });
    }

    @Override
    public void onError(Exception error) {
        Log.e(TAG, "Error!", error);
    }
}
