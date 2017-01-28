package info.benallen.jeeves.jeevescontroller;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.EventLog;
import android.util.Log;
import android.widget.Toast;


import com.google.gson.Gson;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;



public class MainActivity extends AppCompatActivity {
    private final int interval = 1000;
    private Handler handler = new Handler();
    private WebSocket mWebSocket;
    private final String TAG = "JE";
    private final String URL = "ws://3a99a531.ngrok.io:80";
    Gson mGson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectToSocket(new SocketInterface() {
            @Override
            public void onComplete(WebSocket webSocket) {
                mWebSocket = webSocket;
                setupIdentity("robot");
                startLocalizationUpdates();
            }

            @Override
            public void onError() {
                Log.d(TAG, "Fuck, it fucked up");
            }

            @Override
            public void onDataResponse(String s) {
                onServerResponse(s);
            }

        });
    }

    private void setupIdentity(String id) {
        EventData eventData = new EventData("identity", new IdentityObject(id));
        String json = mGson.toJson(eventData);
        mWebSocket.send(json);
        Log.d(TAG, "Setup up with identity: " + id);
    }

    private void startLocalizationUpdates(){
        handler.postAtTime(runnable, System.currentTimeMillis()+interval);
        handler.postDelayed(runnable, interval);
    }

    private Runnable runnable = new Runnable(){
        public void run() {

            Log.d(TAG, "Timer triggered");

            // TODO: Get some information from bluetooth

            PositionData myPositionInfo = new PositionData(0, 1, 2, 3);

            // Send this information to the server
            updatePositionData(myPositionInfo);

            // And repeat...
            handler.postAtTime(runnable, System.currentTimeMillis()+interval);
            handler.postDelayed(runnable, interval);
        }
    };

    private void updatePositionData(PositionData posData) {
        if (mWebSocket == null) {
            Toast.makeText(MainActivity.this, "Web socket is nil", Toast.LENGTH_SHORT).show();
            return;
        }

        EventData eventData = new EventData("location", posData);

        String json = mGson.toJson(eventData);

//        Log.d(TAG, "Sending location: ")
        mWebSocket.send(json);
    }

    private void connectToSocket(final SocketInterface socketInterface) {
        AsyncHttpClient.getDefaultInstance().websocket(URL, "jeeves", new AsyncHttpClient.WebSocketConnectCallback() {
            @Override
            public void onCompleted(Exception ex, WebSocket webSocket) {
                if (ex != null) {
                    ex.printStackTrace();
                    socketInterface.onError();
                    return;
                }


                socketInterface.onComplete(webSocket);
                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    @Override
                    public void onStringAvailable(String s) {
                        onServerResponse(s);
                        socketInterface.onDataResponse(s);
                    }
                });
            }
        });
    }

    private void onServerResponse(String response) {
        EventData eventData = mGson.fromJson(response, EventData.class);
        Log.d(TAG, "Got some response: " + eventData.toString());

        if (eventData.getEvent().equals("instruction") && eventData.getEvent() != null) {
            // TODO: something with X
            Log.d(TAG, "Will do some position stuff: " + eventData.getPayload());
        }
    }
}

class EventData {
    private String event;
    private Object payload;

    EventData(String event, PositionData payload) {
        this.event = event;
        this.payload = payload;
    }

    EventData(String event) {
        this.event = event;
        this.payload = "Hi";
    }

    EventData(String event, String payload) {
        this.event = event;
        this.payload = payload;
    }

    EventData(String event, IdentityObject identityObject) {
        this.event = event;
        this.payload = identityObject;
    }

    @Override
    public String toString() {
        return "EventData{" +
                "event='" + event + '\'' +
                ", payload=" + payload +
                '}';
    }

    public String getEvent() {
        return event;
    }

    public Object getPayload() {
        return payload;
    }


}

class PositionData {
    private int x;
    private int y;
    private int z;
    private int w;

    PositionData(int x, int y, int z, int w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    @Override
    public String toString() {
        return "PositionData{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", w=" + w +
                '}';
    }
}

class IdentityObject {
    private String identity;

    public IdentityObject(String identity) {
        this.identity = identity;
    }

    public String getIdentity() {
        return identity;
    }

    @Override
    public String toString() {
        return "IdentityObject{" +
                "identity='" + identity + '\'' +
                '}';
    }
}

interface SocketInterface {
    void onComplete(WebSocket socket);
    void onError();
    void onDataResponse(String s);
}