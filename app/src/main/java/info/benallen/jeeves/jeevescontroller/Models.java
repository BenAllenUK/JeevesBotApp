package info.benallen.jeeves.jeevescontroller;

import com.koushikdutta.async.http.WebSocket;

import java.util.List;

/**
 * Created by benallen on 28/01/2017.
 */

public class Models {
}

interface SocketInterface {
    void onComplete(WebSocket socket);
    void onError();
    void onDataResponse(EventData eventdata);
}

interface RequestCallback {
    void onSuccess();
    void onError();
}

interface BluetoothCallback {
    void onBeaconFound(int i, float distance);
}

class EventData {
    private String event;
    private Object payload;

    EventData(String event, PositionData payload) {
        this.event = event;
        this.payload = payload;
    }

    EventData(String event, IdentityObject identityObject) {
        this.event = event;
        this.payload = identityObject;
    }

    EventData(String event, RequestObject requestObject) {
        this.event = event;
        this.payload = requestObject;
    }

    EventData(String event, CommandObject requestObject) {
        this.event = event;
        this.payload = requestObject;
    }


    EventData(String event) {
        this.event = event;
        this.payload = "Hi";
    }

    EventData(String event, String payload) {
        this.event = event;
        this.payload = payload;
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
    private List<BeaconData> beacons;

    public PositionData(List<BeaconData> beacons) {
        this.beacons = beacons;
    }

    @Override
    public String toString() {
        return "PositionData{" +
                "beacons=" + beacons +
                '}';
    }
}

class BeaconData {
    private int id;
    private float dist;

    public BeaconData(int id, float dist) {
        this.id = id;
        this.dist = dist;
    }

    @Override
    public String toString() {
        return "BeaconData{" +
                "id='" + id + '\'' +
                ", dist='" + dist + '\'' +
                '}';
    }
}

class IdentityObject {
    private String identity;

    IdentityObject(String identity) {
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

class RequestObject {
    private String destination;

    public RequestObject(String destination) {
        this.destination = destination;
    }

    public String getDestination() {
        return destination;
    }

    @Override
    public String toString() {
        return "RequestObject{" +
                "destination='" + destination + '\'' +
                '}';
    }
}

class GenericMessage {
    private String msg;

    public GenericMessage(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "GenericMessage{" +
                "msg='" + msg + '\'' +
                '}';
    }
}

class CommandObject {
    private String command;

    public CommandObject(String command) {
        this.command = command;
    }
}
