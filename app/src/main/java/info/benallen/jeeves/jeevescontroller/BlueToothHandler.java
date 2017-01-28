package info.benallen.jeeves.jeevescontroller;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.GeomagneticField;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;

import com.google.gson.Gson;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.service.RunningAverageRssiFilter;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by maartie0 on 28/01/2017.
 */

class BluetoothHandler implements RangeNotifier, MonitorNotifier {
    private final String TAG = "BluetoothHandler";
    private BeaconManager mBeaconManager;
    private BluetoothCallback mBluetoothHandler;
    private Context context;

    BluetoothHandler(MainActivity mainActivity, BluetoothCallback bluetoothHandler){

        mBeaconManager = BeaconManager.getInstanceForApplication(mainActivity);
        // Detect the URL frame:
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));

        mBeaconManager.bind(mainActivity);

        this.mBluetoothHandler = bluetoothHandler;
        this.context = mainActivity;
    }

    void destroy(MainActivity mainActivity) {
        mBeaconManager.unbind(mainActivity);
        this.mBluetoothHandler = null;
    }

    BeaconManager getBeaconManager() {
        return mBeaconManager;
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        for (Beacon beacon: beacons) {
            if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x10) {
                // This is a Eddystone-URL frame
                String url = UrlBeaconUrlCompressor.uncompress(beacon.getId1().toByteArray()).replace("http://", "");
                int id = Integer.parseInt(url);
                Log.d(TAG, "I see a beacon transmitting a url: " + url + " approximately " + beacon.getDistance() + " meters away.");
                mBluetoothHandler.onBeaconFound(id, (float)beacon.getDistance());
            }
        }
    }


    @Override
    public void didEnterRegion(Region region) {

    }

    @Override
    public void didExitRegion(Region region) {

    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }

    List<BeaconData> getListFromBeacons(Map<Integer, Float> activeBeacons){
        List<BeaconData> beaconDatas = new ArrayList<>();
        for (int key : activeBeacons.keySet()){
            BeaconData beaconData = new BeaconData(key, activeBeacons.get(key));
            beaconDatas.add(beaconData);
        }
        return beaconDatas;
    }
}
