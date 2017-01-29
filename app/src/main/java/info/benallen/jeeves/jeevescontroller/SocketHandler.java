package info.benallen.jeeves.jeevescontroller;

import android.util.Log;

import com.google.gson.Gson;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

/**
 * Created by benallen on 28/01/2017.
 */

public class SocketHandler {
    private final String URL = "ws://ff743017.ngrok.io:80";
    private final String TAG = "SocketHandler";

    private final String kIdentityField = "identity";
    private final String kRequestField = "request";

    private WebSocket mWebSocket;
    private Gson mGson = new Gson();

    // Connect to server
    void connectToSocket(final SocketInterface socketInterface) {
        AsyncHttpClient.getDefaultInstance().websocket(URL, "jeeves", new AsyncHttpClient.WebSocketConnectCallback() {
            @Override
            public void onCompleted(Exception ex, WebSocket webSocket) {
                if (ex != null) {
                    ex.printStackTrace();
                    socketInterface.onError();
                    return;
                }

                // Setup callback
                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    @Override
                    public void onStringAvailable(String s) {
                        EventData eventData = mGson.fromJson(s, EventData.class);
                        socketInterface.onDataResponse(eventData);
                    }
                });

                mWebSocket = webSocket;

                // Setup identity
                setupIdentity(webSocket, "robot");

                // Call callback to start refresh timer
                socketInterface.onComplete(webSocket);
            }
        });
    }

    // Update position information
    void updatePositionData(PositionData posData, RequestCallback requestCallback) {
        if (mWebSocket == null) {
            requestCallback.onError();
            return;
        }

        EventData eventData = new EventData("location", posData);
        String json = mGson.toJson(eventData);
        Log.d(TAG, "Updating position with: " + json);
        mWebSocket.send(json);
    }

    // Send a request
    void sendMoveRequest(String destination, RequestCallback requestCallback) {
        if (mWebSocket == null) {
            requestCallback.onError();
            return;
        }
        EventData eventData = new EventData(kRequestField, new RequestObject(destination));
        String json = mGson.toJson(eventData);
        mWebSocket.send(json);
    }

    // Setup identity
    private void setupIdentity(WebSocket socket, String name) {
        EventData eventData = new EventData(kIdentityField, new IdentityObject(name));
        String json = mGson.toJson(eventData);
        socket.send(json);

        Log.d(TAG, "Setup up with identity: " + name);
    }
}
