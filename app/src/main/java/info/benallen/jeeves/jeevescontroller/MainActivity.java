package info.benallen.jeeves.jeevescontroller;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.google.gson.Gson;
import com.koushikdutta.async.http.WebSocket;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private final String TAG = "Main";
    private final int interval = 1000;


    private Handler handler = new Handler();
    private Gson mGson = new Gson();
    private SocketHandler mSocketHandler = new SocketHandler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }

    private void startLocalizationUpdates(){
        handler.postAtTime(runnable, System.currentTimeMillis()+interval);
        handler.postDelayed(runnable, interval);
    }

    // Active working trigger: -
    private Runnable runnable = new Runnable(){
        public void run() {

            Log.d(TAG, "Timer triggered");

            // TODO: Get some information from bluetooth
            BeaconData beaconData = new BeaconData(1073, (float) 0.50);
            List<BeaconData> beacons = new ArrayList<>(1);

            // Add lots of data to the list
            beacons.add(beaconData);


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

                GenericMessage genericMessage = mGson.fromJson((String) eventData.getPayload(), GenericMessage.class);

                Log.d(TAG, "Got a message: " + genericMessage.getMsg());
            }
            default: {
                // Do nothing, unknown event
            }
        }
    }
}

