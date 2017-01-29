package info.benallen.jeeves.jeevescontroller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
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
import org.altbeacon.beacon.service.ArmaRssiFilter;
import org.altbeacon.beacon.service.RssiFilter;
import org.altbeacon.beacon.service.RunningAverageRssiFilter;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class MainActivity extends AppCompatActivity implements BeaconConsumer {
    private final String TAG = "Main";
    private final int interval = 1000;
    private BluetoothHandler mBluetoothHandler;
    private Map<Integer, Float> activeBeacons = new ConcurrentHashMap<>();
    private Compass compass;


    private Handler handler = new Handler();
    private Gson mGson = new Gson();
    private SocketHandler mSocketHandler = new SocketHandler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // Start socket setup
        mSocketHandler.connectToSocket(new SocketInterface() {
            @Override
            public void onComplete(WebSocket webSocket) {
                startLocalizationUpdates();
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
        Button faceBtn = (Button) findViewById(R.id.faceBtn);
        faceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(MainActivity.this, FaceActivity.class);
                startActivity(newIntent);
            }
        });

        Button requestBtn = (Button) findViewById(R.id.ardBtn);
        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.destinationText);
                final String content = editText.getText().toString();

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
                Arduino myLittleArduino = new Arduino();

                myLittleArduino.findBT();
                try {
                    myLittleArduino.openBT();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    myLittleArduino.sendData("testing");
                } catch (IOException e) {
                    e.printStackTrace();
                }

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
            case "instructions": {
                Log.d(TAG, "Will do some position stuff: " + eventData.getPayload());
            }
            case "message": {
                LinkedTreeMap genericMessage = (LinkedTreeMap) eventData.getPayload();
                Log.d(TAG, "Got a message: " + genericMessage.get("msg"));
            }
            default: {
                // Do nothing, unknown event
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
    }


}

