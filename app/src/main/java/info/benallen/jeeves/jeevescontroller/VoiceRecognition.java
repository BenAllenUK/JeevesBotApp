package info.benallen.jeeves.jeevescontroller;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by maartie0 on 29/01/2017.
 */

public class VoiceRecognition {
    private TextView mText;
    private SpeechRecognizer sr;
    private static final String TAG = "MyStt3Activity";
    private ArrayList<String> mVoiceData = new ArrayList();
    private SpeechAnalyser speechAnalyser;
    private MainActivity mainActivity;

    public VoiceRecognition(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
        mainActivity.setContentView(R.layout.activity_main);
        mText = (TextView) mainActivity.findViewById(R.id.speechResult);
        sr = SpeechRecognizer.createSpeechRecognizer(mainActivity);
        sr.setRecognitionListener(new listener());
        speechAnalyser = new SpeechAnalyser();
    }

    class listener implements RecognitionListener
    {
        public void onReadyForSpeech(Bundle params)
        {
            Log.d(TAG, "onReadyForSpeech");
        }
        public void onBeginningOfSpeech()
        {
            Log.d(TAG, "onBeginningOfSpeech");
        }
        public void onRmsChanged(float rmsdB)
        {
            Log.d(TAG, "onRmsChanged");
        }
        public void onBufferReceived(byte[] buffer)
        {
            Log.d(TAG, "onBufferReceived");
        }
        public void onEndOfSpeech()
        {
            Log.d(TAG, "onEndofSpeech");
        }
        public void onError(int error)
        {
            Log.d(TAG,  "error " +  error);
            mText.setText("error " + error);
        }
        public void onResults(Bundle results)
        {
            String str = new String();
            Log.d(TAG, "onResults " + results);
            ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            mVoiceData = data;
            for (int i = 0; i < data.size(); i++)
            {
                Log.d(TAG, "result " + data.get(i));
                str += data.get(i);
            }
            mText.setText("results: "+String.valueOf(data.size()));

            int status = speechAnalyser.setCommand(mVoiceData);
            switch (status){
                case 0:
                    mainActivity.speechEndedSuccesfullEvent(speechAnalyser.getmCommands());
                    break;
                case 1:
                    mainActivity.speechEndedWithDance();
                    break;
                case 2:
                    mainActivity.speechEndedUnsuccessfullEvent();
                    break;
            }
        }
        public void onPartialResults(Bundle partialResults)
        {
            Log.d(TAG, "onPartialResults");
        }
        public void onEvent(int eventType, Bundle params)
        {
            Log.d(TAG, "onEvent " + eventType);
        }
    }
    public void startRecording() {
        sr.stopListening();
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
        mText.setText("listening");
        sr.startListening(intent);
        Log.d("111111","11111111");
    }
}
