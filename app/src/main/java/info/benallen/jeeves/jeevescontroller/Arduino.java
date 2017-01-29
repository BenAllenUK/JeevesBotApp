package info.benallen.jeeves.jeevescontroller;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.UUID;

public class Arduino extends Activity {
    public static final String TAG = "ARD";
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;

    void findBT() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0) {
            for(BluetoothDevice device : pairedDevices) {
                Log.d("ARD", device.getName());
                if(device.getName().equals("HC-06")) {
                    mmDevice = device;
                    break;
                }
            }
        }
    }

    void openBT() throws IOException {
        if (mmDevice == null) {
            return;
        }

        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard //SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();
    }

    /*
        request format:
        "Turn X" - Orientation of Jeeves: where x is the number of degrees from 0 to 360.
        "Travel X" - Distance for Jeeves to travel: where X is a distance in meters.
     */
    void sendData(String request) throws IOException {
        if (mmDevice == null) {
            Log.d(TAG, "No device set");
        }

        Log.d(TAG, "Request is " + request);
        if (mmOutputStream != null){
            mmOutputStream.write(request.getBytes(Charset.forName("UTF-8")));
        }

        if (false && request.contains("travel")) {
            closeBT();
            openBT();
        }
    }

    void closeBT() throws IOException {
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
    }
}