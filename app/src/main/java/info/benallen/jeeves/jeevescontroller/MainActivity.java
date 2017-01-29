package info.benallen.jeeves.jeevescontroller;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.koushikdutta.async.http.WebSocket;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.service.RunningAverageRssiFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class MainActivity extends AppCompatActivity implements BeaconConsumer {
    private final String TAG = "Main";
    private final int interval = 1000;
    private BluetoothHandler mBluetoothHandler;
    private Arduino mArduino;
    private Map<Integer, Float> activeBeacons = new ConcurrentHashMap<>();
    private Compass compass;


    private Handler handler = new Handler();
    private Gson mGson = new Gson();
    private SocketHandler mSocketHandler = new SocketHandler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        voiceRecognitionTest = new VoiceRecognition(this);

        // Start bluetooth listings
        mBluetoothHandler = new BluetoothHandler(this, new BluetoothCallback() {
            @Override
            public void onBeaconFound(int id, float distance) {
                if(activeBeacons.containsKey(id)){
                    activeBeacons.remove(id);
                }
                activeBeacons.put(id, distance);

                updateDebugText();
            }
        });

        compass = new Compass(this);



        setEventListeners();
    }

    private void updateDebugText() {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView logText = (TextView) findViewById(R.id.debugText);
                String content = Helper.debugInfo(activeBeacons);
                logText.setText(content);
            }
        });
    }

    private void setEventListeners() {
        Button leftBtn = (Button) findViewById(R.id.leftBtn);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSocketHandler != null) {
                    mSocketHandler.sendDebugMove("debug-left");
                }

                if (mArduino != null) {
                    try {
                        mArduino.sendData("turn 270");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Button rightBtn = (Button) findViewById(R.id.rightBtn);
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSocketHandler != null) {
                    mSocketHandler.sendDebugMove("debug-right");
                }

                if (mArduino != null) {
                    try {
                        mArduino.sendData("turn 090");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Button forwardBtn = (Button) findViewById(R.id.forwardBtn);
        forwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSocketHandler != null) {
                    mSocketHandler.sendDebugMove("debug-forward");
                }

                if (mArduino != null) {
                    try {
                        mArduino.sendData("travel 100");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        Button requestBtn = (Button) findViewById(R.id.requestBtn);
        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.destinationText);
                final String content = editText.getText().toString();

                MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.start);
                mp.start();

                mSocketHandler.sendMoveRequest(content, new RequestCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this, "Sent to " + content, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(MainActivity.this, "Failed to send", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Button ardButton = (Button) findViewById(R.id.ardBtn);
        ardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mArduino != null) {
                    try {
                        mArduino.closeBT();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Arduino myLittleArduino = new Arduino();

                myLittleArduino.findBT();
                try {
                    myLittleArduino.openBT();
                    mArduino = myLittleArduino;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    myLittleArduino.sendData("5");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        Button speakButton = (Button) findViewById(R.id.speechButton);
        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voiceRecognitionTest.startRecording();
            }
        });

        final Button danceBtn = (Button) findViewById(R.id.danceBtn);
        danceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mArduino == null) {
                    Toast.makeText(MainActivity.this, "No Connection", Toast.LENGTH_SHORT).show();
                    return;
                }

                MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.start);
                mp.start();

                danceBtn.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int delay = 3000;

                        try {
//                            mArduino.sendData("turn 100");
//                            SystemClock.sleep(delay);
                            mArduino.sendData("travel 800");
                            SystemClock.sleep(delay);
                            mArduino.sendData("turn 100");
                            SystemClock.sleep(delay);
//                            for (int i = 0; i < 3; i++) {
//                                mArduino.sendData("turn 045");
//                                SystemClock.sleep(delay);
//                                mArduino.sendData("travel 050");
//                                SystemClock.sleep(delay);
//                                mArduino.sendData("turn 315");
//                                SystemClock.sleep(delay);
//                                mArduino.sendData("travel 050");
//                                SystemClock.sleep(delay);
//                                mArduino.sendData("turn 045");
//                                SystemClock.sleep(delay);
//                                mArduino.sendData("travel 050");
//                                SystemClock.sleep(delay);
//                                mArduino.sendData("turn 315");
//                                SystemClock.sleep(delay);
//                                mArduino.sendData("travel 050");
//                                SystemClock.sleep(delay);
//                            }
                            MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.finished);
                            mp.start();

                        } catch (IOException e) {
                            Log.d(TAG, "Error");
                            e.printStackTrace();
                        }
                    }
                }, 1000);

            }
        });

        Button connectBtn = (Button) findViewById(R.id.setupBtn);
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameField = (EditText) findViewById(R.id.identityTxt);

                String identity = nameField.getText().toString();

                // Start socket setup
                mSocketHandler.connectToSocket(identity, new SocketInterface() {
                    @Override
                    public void onComplete(WebSocket webSocket) {
                        startLocalizationUpdates();
                        Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError() {
                        Log.d(TAG, "Fuck, it fucked up");
                        Toast.makeText(MainActivity.this, "Cant connect", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDataResponse(EventData data) {
                        onServerResponse(data);
                    }

                });
            }
        });
    }

    private void startLocalizationUpdates(){
        handler.postAtTime(runnable, System.currentTimeMillis()+interval);
        handler.postDelayed(runnable, interval);
    }

    // Active working trigger: -
    private Runnable runnable = new Runnable(){
        public void run() {

            Log.d(TAG, "Timer triggered");
            Log.d(TAG, "Orientation" + String.valueOf(compass.getDirection()));

            List<BeaconData> beacons = new LinkedList<>();
            beacons.addAll(mBluetoothHandler.getListFromBeacons(activeBeacons));
            Log.d(TAG, String.valueOf(activeBeacons.size()));

            PositionData myPositionInfo = new PositionData(beacons);

            // Send this information to the server
            mSocketHandler.updatePositionData(myPositionInfo, new RequestCallback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "Update success");
                }

                @Override
                public void onError() {
                    Toast.makeText(MainActivity.this, "Web socket is nil", Toast.LENGTH_SHORT).show();
                }
            });

            activeBeacons.clear();

            // And repeat...
            handler.postAtTime(runnable, System.currentTimeMillis()+interval);
            handler.postDelayed(runnable, interval);
        }
    };

    // When a response is received do this:
    private void onServerResponse(EventData eventData) {
        if (eventData.getEvent() == null) {
            return;
        }

        Log.d(TAG, "Got some response: " + eventData.toString());

        switch (eventData.getEvent()) {
            case "instruction": {
                MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.confirm);
                mp.start();
                LinkedTreeMap genericMessage = (LinkedTreeMap) eventData.getPayload();
                String turn = (String) genericMessage.get("turn");
                String travel = (String) genericMessage.get("travel");
                Log.d("BOT", "Got cmd: " + genericMessage.toString());
                int turnVal = Integer.parseInt(turn);
                turnVal = turnVal % 360;
                String turnStr = String.format("%03d", turnVal);
                Log.d("BOT", "Turn string: " + turnStr);

                if (mArduino == null) {
                    Toast.makeText(MainActivity.this, "No Connection", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {

                    Log.d("BOT", "Turn = " + turnStr);
                    mArduino.sendData("turn " + turnStr);
                    SystemClock.sleep(3000);
                    Log.d("BOT", "Travel = " + travel);
                    mArduino.sendData("travel " + travel);
                    SystemClock.sleep(3000);

                } catch (IOException e) {
                    Log.d(TAG, "Could not send: " + eventData.getPayload());
                    e.printStackTrace();
                }



                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(5000);
                        MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.finished);
                        mp.start();
                    }
                });

                break;

            }
            case "message": {
                LinkedTreeMap genericMessage = (LinkedTreeMap) eventData.getPayload();
                Log.d(TAG, "Got a message: " + genericMessage.get("msg"));
                break;

            }
            case "finished": {
                LinkedTreeMap genericMessage = (LinkedTreeMap) eventData.getPayload();
                Log.d(TAG, "Got a message: " + genericMessage.get("msg"));
                MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.finished);
                mp.start();
                break;

            }
            default: {
                // Do nothing, unknown event
                break;
            }
        }
    }

    @Override
    public void onBeaconServiceConnect() {
        Region region = new Region("all-beacons-region", null, null, null);
        try {
            mBluetoothHandler.getBeaconManager().startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        BeaconManager.setRssiFilterImplClass(RunningAverageRssiFilter.class);
        mBluetoothHandler.getBeaconManager().setForegroundScanPeriod(50);
        mBluetoothHandler.getBeaconManager().setForegroundBetweenScanPeriod(10);
        RunningAverageRssiFilter.setSampleExpirationMilliseconds(50);
        mBluetoothHandler.getBeaconManager().setMonitorNotifier(mBluetoothHandler);
        mBluetoothHandler.getBeaconManager().setRangeNotifier(mBluetoothHandler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mBluetoothHandler.destroy(this);

        // Destory if i can
        try {
            if (mArduino == null || mArduino.mmDevice == null) {
                return;
            }
            mArduino.closeBT();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private VoiceRecognition voiceRecognitionTest;

    public void speechEndedSuccesfullEvent(ArrayList<Enum> commands){
        Button requestBtn = (Button) findViewById(R.id.ardBtn);
        EditText editText = (EditText) findViewById(R.id.destinationText);
        for (Enum receiver:commands){
            if(receiver instanceof SpeechAnalyser.Subject){
                editText.setText(String.valueOf(receiver.ordinal()));
            }
        }

//        requestBtn.performClick();

        Log.d("Speech",commands.toString());
    }

    public void speechEndedUnsuccessfullEvent(){
        Log.d("Speech","No Success");
    }

    public void speechEndedWithDance(){
        Log.d("Speech","Dance");
    }


}

